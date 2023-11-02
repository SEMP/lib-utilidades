package py.com.semp.lib.utilidades.communication.interfaces;

import java.util.Set;

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
	public DataReceiver addDataListeners(DataListener... listeners);
	
	/**
	 * Removes data listeners.
	 * 
	 * @param listeners
	 * Data listeners to remove.
	 */
	public DataReceiver removeDataListeners(DataListener... listeners);
	
	/**
	 * Removes all data listeners.
	 */
	public DataReceiver removeAllDataListeners();
	
	/**
	 * Obtains the data listeners.
	 * 
	 * @return
	 * - The data listeners.
	 */
	public Set<DataListener> getDataListeners();
	
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