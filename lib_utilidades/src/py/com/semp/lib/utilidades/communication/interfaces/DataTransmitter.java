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
	 * - Data Listeners to add.
	 * 
	 * @return
	 * - A reference to this {@link DataTransmitter} instance.
	 */
	public DataTransmitter addDataListeners(DataListener... listeners);
	
	/**
	 * Removes data listeners.
	 * 
	 * @param listeners
	 * - Data listeners to remove.
	 * 
	 * @return
	 * - A reference to this {@link DataTransmitter} instance.
	 */
	public DataTransmitter removeDataListeners(DataListener... listeners);
	
	/**
	 * Removes all data listeners.
	 * 
	 * @return
	 * - A reference to this {@link DataTransmitter} instance.
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
	 * 
	 * @return
	 * - A reference to this {@link DataTransmitter} instance.
	 */
	public DataTransmitter informOnSendingError(byte[] data, Throwable exception);
	
	/**
	 * Sends data.
	 * 
	 * @return
	 * - A reference to this {@link DataTransmitter} instance.
	 * 
	 * @throws CommunicationException
	 * if there was a communication exception while sending data.
	 */
	public DataTransmitter sendData(byte[] data) throws CommunicationException;
	
	/**
	 * Sends data.
	 * 
	 * @return
	 * - A reference to this {@link DataTransmitter} instance.
	 * 
	 * @throws CommunicationException
	 * if there was a communication exception while sending data.
	 */
	default public DataTransmitter sendData(String data) throws CommunicationException
	{
		this.sendData(data.getBytes());
		
		return this;
	}
}