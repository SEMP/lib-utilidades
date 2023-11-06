package py.com.semp.lib.utilidades.communication.interfaces;

/**
 * The {@link DataReader} interface defines the operations for reading data
 * from a data source in a separate thread of execution.
 */
public interface DataReader extends Runnable
{
	 /**
     * Initiates the reading process from a data source.
     */
	public void startReading();
	
	/**
     * Temporarily pauses the reading process without stopping it entirely.
     */
	public void pauseReading();
	
	/**
     * Permanently stops the reading process and releases any associated resources.
     * After invoking this method, restarting the reading process may not be possible.
     */
	public void stopReading();
	
	/**
     * Immediately shuts down the reader, attempting to stop operations as quickly as possible
     * without concern for informing all listeners of the shutdown. This is in contrast to
     * {@link #stopReading()}, which may perform a more graceful shutdown with notifications.
     */
    void shutdown();
    
    /**
     * Checks if the reader is currently in the process of reading.
     *
     * @return {@code true} if the reader is currently reading, {@code false} otherwise.
     */
	public boolean isReading();
	
	/**
     * Checks if the reading process has been completed.
     *
     * @return {@code true} if the reading is complete, {@code false} otherwise.
     */
	public boolean isReadingComplete();
}