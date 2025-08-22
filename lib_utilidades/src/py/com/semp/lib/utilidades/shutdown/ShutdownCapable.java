package py.com.semp.lib.utilidades.shutdown;

import py.com.semp.lib.utilidades.exceptions.ShutdownException;

/**
 * Interface for clases that are able to respond to a shutdown request.
 * 
 * @author Sergio Morel
 */
public interface ShutdownCapable
{
	/**
	 * Requests a shutdown.
	 * 
	 * @return
	 * - an instance of the object where the shutdown is called.
	 * @throws ShutdownException
	 * If an error happens during the shutdown call.
	 */
	public ShutdownCapable shutdown() throws ShutdownException;
	
	/**
	 * Indicates if a shutdown has been requested.
	 * 
	 * @return
	 * <b>true</b> if a shutdown has been requested.<br>
	 * <b>false</b> otherwise.
	 */
	public boolean isShuttingDown();
}