package py.com.semp.lib.utilidades.log;

import java.util.concurrent.ConcurrentHashMap;

import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;

/**
 * Manages the logger instances for different contexts.
 * Provides a centralized way to access and modify logger instances.
 * 
 * @author Sergio Morel
 */
public final class LoggerManager
{
	/**
     * Stores Logger instances in a thread-safe manner, mapped by their context.
     */
	private static final ConcurrentHashMap<String, Logger> LOGGERS = new ConcurrentHashMap<>();
	
	/**
     * Private constructor to prevent instantiation.
     * 
     * @throws AssertionError always
     */
	private LoggerManager()
	{
		super();
		
		String errorMessage = MessageUtil.getMessage(Messages.DONT_INSTANTIATE, this.getClass().getName());
		
		throw new AssertionError(errorMessage);
	}
	
	/**
     * Provides a default logger instance.
     * 
     * @return a new instance of {@link DefaultLogger}
     */
	private static Logger getDefaultLogger()
	{
		return new DefaultLogger();
	}
	
	/**
     * Retrieves the logger associated with the given context.
     * If no logger is associated, a default logger is created.
     * 
     * @param context The context for which the logger is requested.
     * @return Logger The logger associated with the given context.
     * 
     * @throws IllegalArgumentException if the context is null or empty.
     */
	public static Logger getLogger(String context)
	{
		checkContextName(context);
		
		return LOGGERS.computeIfAbsent(context, (key) -> getDefaultLogger());
	}
	
	/**
     * Sets a Logger instance for a specific context. Replaces any existing Logger for the given context.
     * 
     * @param context The context for which the logger should be set.
     * @param logger  The logger to be set.
     * 
     * @throws IllegalArgumentException if the context is null or empty.
     */
	public static void setLogger(String context, Logger logger)
	{
		checkContextName(context);
		
		LOGGERS.put(context, logger);
	}
	
	/**
     * Validates the context name.
     * 
     * @param context The context name to check.
     * 
     * @throws IllegalArgumentException if the context is null or empty.
     */
	private static void checkContextName(String context)
	{
		if(context == null || context.trim().isEmpty())
		{
			String errorMessage = MessageUtil.getMessage(Messages.INVALID_NAME_ERROR, context);
			
			throw new IllegalArgumentException(errorMessage);
		}
	}
}