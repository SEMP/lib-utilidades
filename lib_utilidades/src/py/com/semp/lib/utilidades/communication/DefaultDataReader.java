package py.com.semp.lib.utilidades.communication;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import py.com.semp.lib.utilidades.communication.interfaces.DataInterface;
import py.com.semp.lib.utilidades.communication.interfaces.DataReader;
import py.com.semp.lib.utilidades.communication.interfaces.DataReceiver;
import py.com.semp.lib.utilidades.communication.listeners.ConnectionEventListener;
import py.com.semp.lib.utilidades.communication.listeners.DataListener;
import py.com.semp.lib.utilidades.configuration.ConfigurationValues;
import py.com.semp.lib.utilidades.configuration.Values;
import py.com.semp.lib.utilidades.exceptions.CommunicationException;
import py.com.semp.lib.utilidades.exceptions.CommunicationTimeoutException;
import py.com.semp.lib.utilidades.exceptions.ConnectionClosedException;
import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;
import py.com.semp.lib.utilidades.log.Logger;
import py.com.semp.lib.utilidades.log.LoggerManager;
import py.com.semp.lib.utilidades.utilities.NamedThreadFactory;

public class DefaultDataReader<T extends DataReceiver & DataInterface> implements DataReader, ConnectionEventListener
{
	private static final String LISTENER_THREAD_NAME = "DefaultDataReaderListeners";
	
	private T dataReceiver;
	
	private volatile boolean pauseReading;
	private volatile boolean isReading;
	private volatile boolean readingComplete;
	private volatile boolean stopping;
	
	private static final Logger LOGGER = LoggerManager.getLogger(Values.Constants.UTILITIES_CONTEXT);
	private final ExecutorService executorService = Executors.newFixedThreadPool(Values.Constants.LISTENERS_THREAD_POOL_SIZE, new NamedThreadFactory(LISTENER_THREAD_NAME));
	
	public DefaultDataReader(T dataReceiver)
	{
		super();
		
		this.dataReceiver = dataReceiver;
		this.dataReceiver.addConnectionEventListeners(this);
		this.isReading = false;
		this.pauseReading = false;
		this.readingComplete = false;
		this.stopping = false;
	}
	
	@Override
	public void run()
	{
		Integer readTimeoutMS = this.getConfiguration(Values.VariableNames.READ_TIMEOUT_MS, Values.Defaults.READ_TIMEOUT_MS);
		
		while(true)
		{
			if(this.dataReceiver.isShuttingdown())
			{
				this.shutdown();
				
				return;
			}
			
			if(this.stopping)
			{
				break;
			}
			
			if(!this.pauseReading && this.dataReceiver.isConnected())
			{
				try
				{
					this.isReading = true;
					
					byte[] data = this.readWithTimeout(readTimeoutMS);
					
					if(data.length > 0)
					{
						this.informDataReceived(data);
					}
				}
				catch(ConnectionClosedException e)
				{
					String errorMessage = MessageUtil.getMessage(Messages.CONNECTION_CLOSED_ERROR, this.getReceiverString(this.dataReceiver));
					
					LOGGER.error(errorMessage, e);
					
					this.stopReading();
				}
				catch(CommunicationException e)
				{
					String errorMessage = MessageUtil.getMessage(Messages.READING_ERROR, this.getReceiverString(this.dataReceiver));
					
					LOGGER.error(errorMessage, e);
				}
			}
			else
			{
				this.isReading = false;
				
				try
				{
					Thread.sleep(Values.Constants.POLL_DELAY_MS);
				}
				catch(InterruptedException e)
				{
					Thread.currentThread().interrupt();
					
					String errorMessage = MessageUtil.getMessage(Messages.READING_ERROR, this.getReceiverString(this.dataReceiver));
					
					LOGGER.error(errorMessage, e);
					
					this.shutdown();
					
					return;
				}
			}
		}
		
		this.completeReading();
	}
	
	private void shutdown()
	{
		try
		{
			this.dataReceiver.disconnect();
			this.executorService.shutdownNow();
		}
		catch(CommunicationException e)
		{
			String errorMessage = MessageUtil.getMessage(Messages.SHUTDOWN_ERROR, this.getReceiverString(this.dataReceiver));
			
			LOGGER.error(errorMessage, e);
		}
		finally
		{
			this.isReading = false;
			this.readingComplete = true;
		}
	}
	
	private byte[] readWithTimeout(Integer readTimeoutMS) throws CommunicationException
	{
		byte[] data;
		
		long start = System.currentTimeMillis();
		
		do
		{
			long current = System.currentTimeMillis();
			
			if(readTimeoutMS >= 0 && (current - start > readTimeoutMS))
			{
				String errorMessage = MessageUtil.getMessage(Messages.READING_TIMOUT_ERROR, this.getConfigurationValues().toString());
				
				throw new CommunicationTimeoutException(errorMessage);
			}
			
			data = this.dataReceiver.readData();
			
		}
		while(data.length == 0);
		
		return data;
	}
	
	private <C> C getConfiguration(String name, C defaultValue)
	{
		ConfigurationValues configurationValues = this.dataReceiver.getConfigurationValues();
		
		C value = configurationValues.getValue(name);
		
		if(value == null)
		{
			value = defaultValue;
		}
		return value;
	}
	
	private ConfigurationValues getConfigurationValues()
	{
		return this.dataReceiver.getConfigurationValues();
	}
	
	private void completeReading()
	{
		try
		{
			this.dataReceiver.disconnect();
		}
		catch(CommunicationException e)
		{
			String errorMessage = MessageUtil.getMessage(Messages.DISCONNECTION_ERROR, this.getReceiverString(this.dataReceiver));
			
			LOGGER.error(errorMessage, e);
		}
		
		this.executorService.shutdown();
		
		this.isReading = false;
		this.readingComplete = true;
		
		boolean terminated = false;
		
		try
		{
			terminated = this.executorService.awaitTermination(Values.Constants.TERMINATION_TIMOUT_MS, TimeUnit.MILLISECONDS);
		}
		catch(InterruptedException e)
		{
			Thread.currentThread().interrupt();
			
			String methodName = "boolean" + this.executorService.getClass().getName() + "::awaitTermination(long, TimeUnit) ";
			String errorMessage = MessageUtil.getMessage(Messages.INTERRUPTED_ERROR, methodName);
			
			LOGGER.error(errorMessage, e);
		}
		finally
		{
			if(!terminated)
			{
				this.executorService.shutdownNow();
				
				String methodName = "boolean" + this.executorService.getClass().getName() + "::awaitTermination(long, TimeUnit) ";
				String errorMessage = MessageUtil.getMessage(Messages.TERMINATION_TIMEOUT_ERROR, methodName);
				
				LOGGER.error(errorMessage);
			}
		}
	}
	
	private void informDataReceived(byte[] data)
	{
		Set<DataListener> dataListeners = this.dataReceiver.getDataListeners();
		
		String methodName = "void DefaultDataReader::informDataReceived(byte[])";
		
		notifyListeners(methodName, dataListeners, (listener) ->
		{
			listener.onDataReceived(Instant.now(), this.dataReceiver, data);
		});
	}
	
	private <L> void notifyListeners(String methodName, Set<L> listeners, Consumer<L> notificationTask)
	{
		try
		{
			this.executorService.submit(() ->
			{
				if(this.dataReceiver.isShuttingdown())
				{
					return;
				}
				
				for(L listener : listeners)
				{
					try
					{
						notificationTask.accept(listener);
					}
					catch(RuntimeException e)
					{
						String errorMessage = MessageUtil.getMessage(Messages.LISTENER_THROWN_EXCEPTION_ERROR, listener.getClass().getName());
						
						LOGGER.warning(errorMessage, e);
					}
				}
			});
		}
		catch(RejectedExecutionException e)
		{
			String errorMessage = MessageUtil.getMessage(Messages.TASK_SHUTDOWN_ERROR, methodName);
			
			LOGGER.debug(errorMessage, e);
		}
	}
	
	@Override
	public void stopReading()
	{
		this.stopping = true;
	}
	
	@Override
	public boolean isReading()
	{
		return this.isReading;
	}
	
	@Override
	public void onDisconnect(Instant instant, DataInterface dataInterface)
	{
		this.stopReading();
	}
	
	@Override
	public void onConnect(Instant instant, DataInterface dataInterface)
	{
	}
	
	@Override
	public void onConnectError(Instant instant, DataInterface dataInterface, Exception exception)
	{
		String errorMessage = MessageUtil.getMessage(Messages.CONNECTION_ERROR, this.getConfigurationValues().toString());
		
		LOGGER.error(errorMessage, exception);
	}
	
	private String getReceiverString(DataInterface dataInterface)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(Instant.now()).append(": ");
		sb.append(dataInterface.getStringIdentifier());
		
		return sb.toString();
	}
	
	@Override
	public void onDisconnectError(Instant instant, DataInterface dataInterface, Exception exception)
	{
		String errorMessage = MessageUtil.getMessage(Messages.DISCONNECTION_ERROR, this.getReceiverString(dataInterface));
		
		LOGGER.error(errorMessage, exception);
	}
	
	@Override
	public void startReading()
	{
		this.pauseReading = false;
	}
	
	@Override
	public void pauseReading()
	{
		this.pauseReading = true;
	}
	
	@Override
	public boolean readingComplete()
	{
		return this.readingComplete;
	}
}