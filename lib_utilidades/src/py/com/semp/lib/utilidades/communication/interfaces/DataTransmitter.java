package py.com.semp.lib.utilidades.communication.interfaces;

import java.util.Set;

import py.com.semp.lib.utilidades.communication.listeners.DataListener;
import py.com.semp.lib.utilidades.exceptions.CommunicationException;

public interface DataTransmitter
{
	/**
	 * Adds data listeners.
	 * 
	 * @param listeners
	 * Data Listeners to add.
	 */
	public DataTransmitter addDataListeners(DataListener... listeners);
	
	/**
	 * Removes data listeners.
	 * 
	 * @param listeners
	 * Data listeners to remove.
	 */
	public DataTransmitter removeDataListeners(DataListener... listeners);
	
	/**
	 * Removes all data listeners.
	 */
	public DataTransmitter removeAllDataListeners();
	
	/**
	 * Obtains the data listeners.
	 * 
	 * @return
	 * - The data listeners.
	 */
	public Set<DataListener> getDataListeners();
	
	/**
	 * Informs the {@link DataListener} instances of exceptions occurring when
	 * sending data.
	 * 
	 * @param data
	 * - Data tried to be sent when the error occurred.
	 * @param exception
	 * - The exception that occurred.
	 */
	public void informOnSendingError(byte[] data, Throwable exception);
	
	/**
	 * Sends data.
	 * 
	 * @throws CommunicationException
	 * if there was a communication exception while sending data.
	 */
	public void sendData(byte[] data) throws CommunicationException;
	
	/**
	 * Sends data.
	 * 
	 * @throws CommunicationException
	 * if there was a communication exception while sending data.
	 */
	default public void sendData(String data) throws CommunicationException
	{
		this.sendData(data.getBytes());
	}
}