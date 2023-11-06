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

/**
 * The {@link DefaultDataReader} class is responsible for asynchronously reading data from a data receiver
 * that also acts as a data interface. It handles the life cycle of reading operations including start,
 * stop, pause, and managing data notifications to listeners. This class also implements connection event
 * listeners to react to connect/disconnect events.
 *
 * @param <T> The type of the data receiver that this reader will interact with, which must implement
 *            both {@link DataReceiver} and {@link DataInterface}.
 */
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
	
	 /**
     * Constructor for {@link DefaultDataReader}.
     *
     * @param dataReceiver The data receiver instance that will provide the data and configuration.
     */
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
	
	 /**
     * Main reading loop that runs asynchronously to fetch data from the receiver.
     */
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
	
	/**
	 * Attempts to read data with a specified timeout. If data is not received within the timeout period, 
	 * a CommunicationTimeoutException is thrown. This method repeatedly tries to read data until it is successful 
	 * or the timeout is exceeded.
	 *
	 * @param readTimeoutMS the maximum time in milliseconds to wait for data to be read
	 * @return the read data as a byte array
	 * @throws CommunicationException if there is an issue with reading the data or a timeout occurs
	 */
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
	
	/**
	 * Retrieves a configuration value for a given name. If the value is not set, it returns the provided default value.
	 * This method uses the configuration values from the data receiver to get the setting.
	 *
	 * @param name the name of the configuration to retrieve
	 * @param defaultValue the default value to return if the configuration is not set
	 * @return the value of the configuration, or the default value if not set
	 */
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
	
	/**
	 * Gets the configuration values from the underlying data receiver.
	 * 
	 * @return
	 * - Instance of {@link ConfigurationValues} from the underlying {@link DataReceiver}.
	 */
	private ConfigurationValues getConfigurationValues()
	{
		return this.dataReceiver.getConfigurationValues();
	}
	
	/**
	 * Completes the reading process. This method is called when reading is stopped either due to an interrupt or 
	 * if the reader has finished its task. It closes the data receiver's connection, shuts down the executor service, 
	 * and sets the flags indicating that reading is complete and the reader is no longer active.
	 */
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
	
	/**
     * Informs all data listeners that new data has been received.
     *
     * @param data The data received from the data receiver.
     */
	private void informDataReceived(byte[] data)
	{
		Set<DataListener> dataListeners = this.dataReceiver.getDataListeners();
		
		String methodName = "void DefaultDataReader::informDataReceived(byte[])";
		
		notifyListeners(methodName, dataListeners, (listener) ->
		{
			listener.onDataReceived(Instant.now(), this.dataReceiver, data);
		});
	}
	
	/**
     * Submits a task to notify all listeners about an event.
     *
     * @param methodName The name of the method in which the listeners are notified.
     * @param listeners The set of listeners to notify.
     * @param notificationTask The consumer task that performs the notification.
     * @param <L> The listener type.
     */
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
	
	/**
	 * Generates a string representation of the data receiver for logging purposes. This representation includes 
	 * a timestamp and the identifier of the data receiver.
	 *
	 * @param dataInterface the data interface whose string representation is to be generated
	 * @return a string representation of the data interface
	 */
	private String getReceiverString(DataInterface dataInterface)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(Instant.now()).append(": ");
		sb.append(dataInterface.getStringIdentifier());
		
		return sb.toString();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation immediately disconnects the data receiver and shuts down the executor service
	 * without delay, marking the reading state as false and the reading process as complete, regardless
	 * of the current reading state or any potential listeners.
	 * </p>
	 * @throws CommunicationException if any communication error occurs during the shutdown process.
	 */
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation immediately disconnects the data receiver and shuts down the executor service
	 * swiftly, ensuring an immediate attempt to cease reading operations. It sets the reading state to false
	 * and marks the reading process as complete. This is done irrespective of the current reading state or
	 * ongoing tasks, and without ensuring that all listeners are informed of the shutdown.
	 * </p>
	 */
	@Override
	public void shutdown()
	{
		try
		{
			this.executorService.shutdownNow();
			this.dataReceiver.shutdown();
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
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation sets a flag to indicate that the reading process should be stopped.
	 * The actual stopping of the process and resource cleanup should be handled elsewhere, likely
	 * in the reading loop of the {@code run()} method.
	 * </p>
	 */
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
		
		LOGGER.warning(errorMessage, exception);
	}
	
	@Override
	public void onDisconnectError(Instant instant, DataInterface dataInterface, Exception exception)
	{
		String errorMessage = MessageUtil.getMessage(Messages.DISCONNECTION_ERROR, this.getReceiverString(dataInterface));
		
		LOGGER.warning(errorMessage, exception);
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
	public boolean isReadingComplete()
	{
		return this.readingComplete;
	}
}