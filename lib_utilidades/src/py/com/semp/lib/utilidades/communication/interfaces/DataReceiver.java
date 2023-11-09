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
	 * 
	 * @return
	 * - A reference to this {@link DataReceiver} instance.
	 */
	public DataReceiver addDataListeners(DataListener... listeners);
	
	/**
	 * Removes data listeners.
	 * 
	 * @param listeners
	 * Data listeners to remove.
	 * 
	 * @return
	 * - A reference to this {@link DataReceiver} instance.
	 */
	public DataReceiver removeDataListeners(DataListener... listeners);
	
	/**
	 * Removes all data listeners.
	 * 
	 * @return
	 * - A reference to this {@link DataReceiver} instance.
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
	 * Informs the {@link DataListener} instances of exceptions occurring when
	 * receiving data.
	 * 
	 * @param exception
	 * - The exception that occurred.
	 * 
	 * @return
	 * - A reference to this {@link DataReceiver} instance.
	 */
	public DataReceiver informOnReceivingError(Throwable exception);
	
	/**
	 * Obtains the data reader for this {@link DataReceiver}.
	 * 
	 * @return
	 * - The data reader for this {@link DataReceiver}.
	 */
	public DataReader getDataReader();
	
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