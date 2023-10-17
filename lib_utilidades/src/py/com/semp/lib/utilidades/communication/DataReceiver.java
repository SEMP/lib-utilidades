package py.com.semp.lib.utilidades.communication;

import py.com.semp.lib.utilidades.communication.listeners.DataListener;
import py.com.semp.lib.utilidades.exceptions.CommunicationException;

/**
 * Interface for data receivers.
 * 
 * @author Sergio Morel
 */
public interface DataReceiver
{
	/**
	 * Adds data listeners.
	 * 
	 * @param listeners
	 * Data Listeners to add.
	 */
	public void addDataListeners(DataListener... listeners);
	
	/**
	 * Removes data listeners.
	 * 
	 * @param listeners
	 * Data listeners to remove.
	 */
	public void removeDataListeners(DataListener... listeners);
	
	/**
	 * Removes all data listeners.
	 */
	public void removeAllDataListeners();
	
	/**
	 * Reads data.
	 * 
	 * @return
	 * - The data read.
	 * @throws CommunicationException
	 * if there was a communication exception while reading data.
	 */
	public byte[] readData() throws CommunicationException;
}