package py.com.semp.lib.utilidades.communication;

import py.com.semp.lib.utilidades.configuration.Values;
import py.com.semp.lib.utilidades.exceptions.CommunicationException;
import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;
import py.com.semp.lib.utilidades.log.Logger;
import py.com.semp.lib.utilidades.log.LoggerManager;

/**
 * A {@link ShutdownHookAction} is a utility class responsible for
 * performing a graceful shutdown of a given {@code DataInterface}.
 * It encapsulates the shutdown logic and error handling.
 * <p>
 * This class implements {@link Runnable} and is intended to be
 * used as a JVM shutdown hook.
 * </p>
 *
 *@author Sergio Morel
 *
 * @see DataInterface
 * @see CommunicationException
 */
public class ShutdownHookAction implements Runnable
{
	/**
    * A {@link DataInterface} instance that this shutdown hook will act upon.
    */
	private DataInterface dataInterface;
	
	/**
     * Logger for this shutdown hook.
     */
	private Logger logger;
	
	/**
     * Constructs a new {@link ShutdownHookAction} with the specified {@link DataInterface},
     * using a default logger.
     *
     * @param dataInterface The {@link DataInterface} to be managed.
     */
	public ShutdownHookAction(DataInterface dataInterface)
	{
		super();
		
		this.dataInterface = dataInterface;
		this.logger = LoggerManager.getLogger(Values.Constants.SHUTDOWN_CONTEXT);
	}
	
	/**
     * Constructs a new {@link ShutdownHookAction} with the specified {@code DataInterface}
     * and {@link Logger}.
     *
     * @param dataInterface The {@link DataInterface} to be managed.
     * @param logger        The {@link Logger} to be used for logging.
     */
	public ShutdownHookAction(DataInterface dataInterface, Logger logger)
	{
		super();
		
		this.dataInterface = dataInterface;
		this.logger = logger;
	}
	
	/**
     * Executes the shutdown process.
     * Invokes {@code DataInterface#shutdown()} and logs any exceptions that occur.
     */
	@Override
	public void run()
	{
		try
		{
			this.dataInterface.shutdown();
		}
		catch(CommunicationException e)
		{
			String errorMessage = MessageUtil.getMessage(Messages.SHUTDOWN_ERROR, this.dataInterface.getStringIdentifier());
			
			this.logger.error(errorMessage, e);
		}
	}
}