package py.com.semp.lib.utilidades.communication.listeners;
import java.time.Instant;

import py.com.semp.lib.utilidades.communication.interfaces.DataInterface;

/**
 * Listens to the data communication.
 * 
 * @author Sergio Morel
 */
public interface DataListener
{
	/**
	 * Receives data when data is sent.
	 * 
	 * @param instant        Instant at which the data was sent.
	 * @param dataInterface  Data source from which the data was sent.
	 * @param data           Data that was sent.
	 */
	void onDataSent(Instant instant, DataInterface dataInterface, byte[] data);
	
	/**
	 * Receives data when data is received.
	 * 
	 * @param instant        Instant at which the data was received.
	 * @param dataInterface  Data source from which the data was received.
	 * @param data           Data that was received.
	 */
	void onDataReceived(Instant instant, DataInterface dataInterface, byte[] data);
	
	/**
	 * Receives the Throwable when there is an error while sending data.
	 * 
	 * @param instant        Instant at which the error occurred.
	 * @param dataInterface  Data source where the error occurred.
	 * @param data           The data attempted to be sent when the error occurred.
	 * @param throwable      Throwable thrown during data sending.
	 */
	void onSendingError(Instant instant, DataInterface dataInterface, byte[] data, Throwable throwable);
	
	/**
	 * Receives the Exception when there is an error while receiving data.
	 * 
	 * @param instant        Instant at which the error occurred.
	 * @param dataInterface  Data source where the error occurred.
	 * @param throwable      Throwable thrown when reading data.
	 */
	void onReceivingError(Instant instant, DataInterface dataInterface, Throwable throwable);
}