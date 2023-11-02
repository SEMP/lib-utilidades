package py.com.semp.lib.utilidades.communication.interfaces;

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