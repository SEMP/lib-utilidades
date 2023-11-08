package py.com.semp.lib.utilidades.communication.listeners;

import java.time.Instant;

import py.com.semp.lib.utilidades.communication.interfaces.DataInterface;

/**
 * Listen to events corresponding to the connection of the {@link DataInterface}
 * 
 * @author Sergio Morel
 */
public interface ConnectionEventListener
{
	/**
	* Indicates that the data source connection has been closed.
	*
	* @param instant
	* Instant at which the connection was closed.
	* @param dataInterface
	* Data source that was closed.
	*/
	public void onDisconnect(Instant instant, DataInterface dataInterface);
	
	/**
	 * Indicates that the data source connection has been established.
	 *
	 * @param instant
	 * Instant at which the connection was established.
	 * @param dataInterface
	 * Data source that was connected.
	 */
	public void onConnect(Instant instant, DataInterface dataInterface);
	
	/**
	 * Indicates that there was an error while trying to establish a connection.
	 *
	 * @param instant
	 * Instant at which the error occurred.
	 * @param dataInterface
	 * Data source where the error occurred.
	 * @param throwable
	 * Exception encountered during the connection attempt.
	 */
	public void onConnectError(Instant instant, DataInterface dataInterface, Throwable throwable);
	
	/**
	 * Indicates that there was an error while trying to close a connection.
	 *
	 * @param instant
	 * Instant at which the error occurred.
	 * @param dataInterface
	 * Data source where the error occurred.
	 * @param throwable
	 * Exception encountered while closing the connection.
	 */
	public void onDisconnectError(Instant instant, DataInterface dataInterface, Throwable throwable);
}