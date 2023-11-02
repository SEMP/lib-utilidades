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
import py.com.semp.lib.utilidades.configuration.Values;
import py.com.semp.lib.utilidades.exceptions.CommunicationException;
import py.com.semp.lib.utilidades.exceptions.ConnectionClosedException;
import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;
import py.com.semp.lib.utilidades.log.Logger;
import py.com.semp.lib.utilidades.log.LoggerManager;

public class DefaultDataReader<T extends DataReceiver & DataInterface> implements DataReader, ConnectionEventListener
{
	private T dataReceiver;
	
	private volatile boolean pauseReading;
	private volatile boolean isReading;
	private volatile boolean readingComplete;
	private volatile boolean stopping;
	
	private static final Logger LOGGER = LoggerManager.getLogger(Values.Constants.UTILITIES_CONTEXT);
	private final ExecutorService executorService = Executors.newFixedThreadPool(Values.Constants.LISTENERS_THREAD_POOL_SIZE);
	
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
		while(true)
		{
			if(this.stopping)
			{
				break;
			}
			
			if(!this.pauseReading && this.dataReceiver.isConnected())
			{
				try
				{
					this.isReading = true;
					
					byte[] data = this.dataReceiver.readData();
					
					this.informDataReceived(data);
				}
				catch(ConnectionClosedException e)
				{
					LOGGER.error(this.getMessage(Instant.now(), this.dataReceiver), e);
					
					this.stopReading();
				}
				catch(CommunicationException e)
				{
					LOGGER.error(this.getMessage(Instant.now(), this.dataReceiver), e);
				}
			}
			else
			{
				this.isReading = false;
			}
		}
		
		this.completeReading();
	}

	private void completeReading()
	{
		try
		{
			this.dataReceiver.disconnect();
		}
		catch(CommunicationException e)
		{
			LOGGER.error(this.getMessage(Instant.now(), this.dataReceiver), e);
		}
		
		this.executorService.shutdown();
		
		this.isReading = false;
		this.readingComplete = true;
		
		try
		{
			boolean terminated = this.executorService.awaitTermination(Values.Constants.TERMINATION_TIMOUT_MS, TimeUnit.MILLISECONDS);
			
			if(!terminated)
			{
				this.executorService.shutdownNow();
			}
		}
		catch(InterruptedException e)
		{
			Thread.currentThread().interrupt();
			
			LOGGER.error(this.getMessage(Instant.now(), this.dataReceiver));
			
			this.executorService.shutdownNow();
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
				if(this.stopping)
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
	public void onConnect(Instant instant, DataInterface dataInterface){}
	
	@Override
	public void onConnectError(Instant instant, DataInterface dataInterface, Exception exception)
	{
		String message = this.getMessage(instant, dataInterface);
		
		LOGGER.error(message, exception);
	}
	
	private String getMessage(Instant instant, DataInterface dataInterface)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(instant).append(": ");
		sb.append(dataInterface.getStringIdentifier());
		
		return sb.toString();
	}
	
	@Override
	public void onDisconnectError(Instant instant, DataInterface dataInterface, Exception exception)
	{
		String message = this.getMessage(instant, dataInterface);
		
		LOGGER.error(message, exception);
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
	public boolean hasFinalized()
	{
		return this.readingComplete;
	}
}