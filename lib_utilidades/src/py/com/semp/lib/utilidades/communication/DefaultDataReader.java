package py.com.semp.lib.utilidades.communication;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import py.com.semp.lib.utilidades.communication.interfaces.DataInterface;
import py.com.semp.lib.utilidades.communication.interfaces.DataReader;
import py.com.semp.lib.utilidades.communication.interfaces.DataReceiver;
import py.com.semp.lib.utilidades.communication.listeners.ConnectionEventListener;
import py.com.semp.lib.utilidades.configuration.ConfigurationValues;
import py.com.semp.lib.utilidades.configuration.Values;
import py.com.semp.lib.utilidades.exceptions.CommunicationException;
import py.com.semp.lib.utilidades.exceptions.CommunicationTimeoutException;
import py.com.semp.lib.utilidades.exceptions.ShutdownException;
import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;
import py.com.semp.lib.utilidades.log.Logger;
import py.com.semp.lib.utilidades.log.LoggerManager;
import py.com.semp.lib.utilidades.utilities.Utilities;

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
	private T dataReceiver;
	
	private int pollDelayMS = Values.Defaults.POLL_DELAY_MS;
	private volatile boolean pauseReading = false;
	private volatile boolean reading = false;
	private volatile boolean readingComplete = false;
	private volatile boolean stopping = false;
	private volatile AtomicBoolean threadNameUpdated  = new AtomicBoolean(false);
	
	private static final Logger LOGGER = LoggerManager.getLogger(Values.Constants.UTILITIES_CONTEXT);
	
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
	}
	
	 /**
     * Main reading loop that runs asynchronously to fetch data from the receiver.
     */
	@Override
	public void run()
	{
		Integer readTimeoutMS = this.getConfiguration(Values.VariableNames.READ_TIMEOUT_MS, Values.Defaults.READ_TIMEOUT_MS);
		long readTimeoutNanos = TimeUnit.MILLISECONDS.toNanos(readTimeoutMS);
		
		this.setThreadName();
		
		while(true)
		{
			if(this.dataReceiver.isShuttingdown())
			{
				this.shutdown();
				
				return;
			}
			
			if(Thread.currentThread().isInterrupted())
			{
				this.shutdown();
				
				return;
			}
			
			if(this.stopping || this.dataReceiver.isStopping())
			{
				break;
			}
			
			if(!this.pauseReading && this.dataReceiver.isConnected())
			{
				if(this.threadNameUpdated.compareAndSet(false, true))
				{
					this.setThreadName();
				}
				
				try
				{
					this.reading = true;
					
					this.readWithTimeout(readTimeoutNanos);
				}
				catch(CommunicationException e)
				{
					this.stopReading();
				}
			}
			else
			{
				this.reading = false;
				this.pollDelay();
			}
		}
		
		this.completeReading();
	}
	
	private void setThreadName()
	{
		Thread currentThread = Thread.currentThread();
		
		StringBuilder threadName = new StringBuilder();
		
		threadName.append(this.getClass().getSimpleName());
		threadName.append("_");
		threadName.append(currentThread.getId());
		threadName.append("_");
		threadName.append(this.dataReceiver.getDynamicStringIdentifier());
		
		currentThread.setName(threadName.toString());
	}

	/**
	 * Attempts to read data with a specified timeout. If data is not received within the timeout period, 
	 * a CommunicationTimeoutException is thrown. This method repeatedly tries to read data until it is successful 
	 * or the timeout is exceeded.
	 *
	 * @param readTimeoutNanos the maximum time in nano seconds to wait for data to be read
	 * @return the read data as a byte array
	 * @throws CommunicationException if there is an issue with reading the data or a timeout occurs
	 */
	private byte[] readWithTimeout(long readTimeoutNanos) throws CommunicationException
	{
		byte[] data = new byte[]{};
		
		long start = System.nanoTime();
		
		while(!Thread.currentThread().isInterrupted())
		{
			long current = System.nanoTime();
			
			if(readTimeoutNanos >= 0 && (current - start > readTimeoutNanos))
			{
				String errorMessage = MessageUtil.getMessage(Messages.READING_TIMOUT_ERROR, this.getConfigurationValues().toString());
				
				CommunicationTimeoutException exception = new CommunicationTimeoutException(errorMessage);
				
				this.dataReceiver.informOnReceivingError(exception);
			}
			
			data = this.dataReceiver.readData();
			
			if(data.length != 0)
			{
				break;
			}
			
			this.pollDelay();
		}
		
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
		
		if(configurationValues == null)
		{
			return defaultValue;
		}
		
		return configurationValues.getValue(name, defaultValue);
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
		finally
		{
			this.reading = false;
			this.readingComplete = true;
		}
	}
	
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
	public DefaultDataReader<T> shutdown()
	{
		try
		{
			this.dataReceiver.shutdown();
		}
		catch(ShutdownException e)
		{
			String errorMessage = MessageUtil.getMessage(Messages.SHUTDOWN_ERROR, this.getReceiverString(this.dataReceiver));
			
			LOGGER.error(errorMessage, e);
		}
		finally
		{
			this.reading = false;
			this.readingComplete = true;
		}
		
		return this;
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
		return this.reading;
	}
	
	@Override
	public void onDisconnect(Instant instant, DataInterface dataInterface)
	{
		this.stopReading();
	}
	
	@Override
	public void onConnect(Instant instant, DataInterface dataInterface) {}
	
	@Override
	public void onConnectError(Instant instant, DataInterface dataInterface, Throwable throwable)
	{
		String errorMessage = MessageUtil.getMessage(Messages.CONNECTION_ERROR, this.getConfigurationValues().toString());
		
		LOGGER.debug(errorMessage, throwable);
	}
	
	@Override
	public void onDisconnectError(Instant instant, DataInterface dataInterface, Throwable throwable)
	{
		String errorMessage = MessageUtil.getMessage(Messages.DISCONNECTION_ERROR, this.getReceiverString(dataInterface));
		
		LOGGER.debug(errorMessage, throwable);
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
	
	@Override
	public boolean isShuttingdown()
	{
		if(this.dataReceiver == null)
		{
			return false;
		}
		
		return this.dataReceiver.isShuttingdown();
	}
	
	public void setPollDelayMS(int pollDelayMS)
	{
		this.pollDelayMS = pollDelayMS;
	}
	
	public int getPollDelayMS()
	{
		return this.pollDelayMS;
	}
	
	private void pollDelay()
	{
		try
		{
			Thread.sleep(this.pollDelayMS);
		}
		catch(InterruptedException e)
		{
			Thread.currentThread().interrupt();
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
		
		sb.append(Utilities.toString(Instant.now())).append(": ");
		sb.append(dataInterface.getDynamicStringIdentifier());
		
		return sb.toString();
	}
}