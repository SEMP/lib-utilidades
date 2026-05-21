package py.com.semp.lib.utilidades.shutdown;

import py.com.semp.lib.utilidades.configuration.Values;
import py.com.semp.lib.utilidades.exceptions.ShutdownException;
import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;
import py.com.semp.lib.utilidades.log.Logger;
import py.com.semp.lib.utilidades.log.LoggerManager;

/**
 * A {@link ShutdownHookAction} is a utility class responsible for
 * performing a graceful shutdown of a given {@code ShutdownCapable}.
 * It encapsulates the shutdown logic and error handling.
 * <p>
 * This class implements {@link Runnable} and is intended to be
 * used as a JVM shutdown hook.
 * </p>
 *
 *@author Sergio Morel
 *
 * @see ShutdownCapable
 * @see ShutdownException
 */
public class ShutdownHookAction implements Runnable
{
	/**
    * A {@link ShutdownCapable} instance that this shutdown hook will act upon.
    */
	private ShutdownCapable shutdownCapable;
	
	/**
     * Logger for this shutdown hook.
     */
	private Logger logger;
	
	/**
     * Constructs a new {@link ShutdownHookAction} with the specified {@link ShutdownCapable},
     * using a default logger.
     *
     * @param shutdownCapable The {@link ShutdownCapable} to be managed.
     */
	public ShutdownHookAction(ShutdownCapable shutdownCapable)
	{
		super();
		
		this.shutdownCapable = shutdownCapable;
		this.logger = LoggerManager.getLogger(Values.Constants.SHUTDOWN_CONTEXT);
	}
	
	/**
     * Constructs a new {@link ShutdownHookAction} with the specified {@code ShutdownCapable}
     * and {@link Logger}.
     *
     * @param shutdownCapable The {@link ShutdownCapable} to be managed.
     * @param logger        The {@link Logger} to be used for logging.
     */
	public ShutdownHookAction(ShutdownCapable shutdownCapable, Logger logger)
	{
		super();
		
		this.shutdownCapable = shutdownCapable;
		this.logger = logger;
	}
	
	/**
     * Executes the shutdown process.
     * Invokes {@code ShutdownCapable#shutdown()} and logs any exceptions that occur.
     */
	@Override
	public void run()
	{
		try
		{
			this.shutdownCapable.shutdown();
		}
		catch(ShutdownException e)
		{
			String errorMessage = MessageUtil.getMessage(Messages.SHUTDOWN_ERROR, this.shutdownCapable.toString());
			
			this.logger.error(errorMessage, e);
		}
	}
}