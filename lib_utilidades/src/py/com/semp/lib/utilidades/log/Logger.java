package py.com.semp.lib.utilidades.log;

import java.util.Objects;

/**
 * Provides logging capabilities with different levels and input types.
 * 
 * @author Sergio Morel
 */
public interface Logger
{
	/**
	 * Logs a message with the given level.
	 * 
	 * @param level The severity level of the log.
	 * @param message The message to log.
	 */
	void log(LogLevel level, String message);
	
	/**
	 * Logs a message and an associated Throwable (exception or error).
	 * 
	 * @param level The severity level of the log.
	 * @param message The message to log.
	 * @param throwable The throwable to log.
	 */
	void log(LogLevel level, String message, Throwable throwable);
	
	/**
	 * Logs an object's string representation with the given level.
	 * 
	 * @param level The severity level of the log.
	 * @param object The object to log.
	 */
	default void log(LogLevel level, Object object)
	{
		String message = Objects.toString(object);
		
		this.log(level, message);
	}
	
	/**
	 * Logs an object's string representation and an associated Throwable.
	 * 
	 * @param level The severity level of the log.
	 * @param object The object to log.
	 * @param throwable The throwable to log.
	 */
	default void log(LogLevel level, Object object, Throwable throwable)
	{
		String message = Objects.toString(object);
		
		this.log(level, message, throwable);
	}
	
	/**
	 * Logs a Throwable with the INFO level.
	 *
	 * @param throwable The throwable to log with INFO level.
	 */
	default void info(Throwable throwable)
	{
		String message = throwable.getLocalizedMessage();
		
		this.log(LogLevel.INFO, message != null ? message : throwable.toString(), throwable);
	}
	
	/**
	 * Logs an informational message.
	 * 
	 * @param message The message to log with INFO level.
	 */
	default void info(String message)
	{
		this.log(LogLevel.INFO, message);
	}
	
	/**
	 * Logs an object's string representation as an informational message.
	 * 
	 * @param object The object whose string representation will be logged with INFO level.
	 */
	default void info(Object object)
	{
		String message = Objects.toString(object);
		
		this.log(LogLevel.INFO, message);
	}
	
	/**
	 * Logs an informational message along with a Throwable.
	 * 
	 * @param message The message to log with INFO level.
	 * @param throwable The throwable to log.
	 */
	default void info(String message, Throwable throwable)
	{
		this.log(LogLevel.INFO, message, throwable);
	}
	
	/**
	 * Logs an object's string representation as an informational message along with a Throwable.
	 * 
	 * @param object The object whose string representation will be logged with INFO level.
	 * @param throwable The throwable to log.
	 */
	default void info(Object object, Throwable throwable)
	{
		String message = Objects.toString(object);
		
		this.log(LogLevel.INFO, message, throwable);
	}
	
	/**
	 * Logs a Throwable with the ERROR level.
	 *
	 * @param throwable The throwable to log with ERROR level.
	 */
	default void error(Throwable throwable)
	{
		String message = throwable.getLocalizedMessage();
		
		this.log(LogLevel.ERROR, message != null ? message : throwable.toString(), throwable);
	}
	
	/**
	 * Logs an error message.
	 * 
	 * @param message The message to log with ERROR level.
	 */
	default void error(String message)
	{
		this.log(LogLevel.ERROR, message);
	}
	
	/**
	 * Logs an object's string representation as an error message.
	 * 
	 * @param object The object whose string representation will be logged with ERROR level.
	 */
	default void error(Object object)
	{
		String message = Objects.toString(object);
		
		this.log(LogLevel.ERROR, message);
	}
	
	/**
	 * Logs an error message along with a Throwable.
	 * 
	 * @param message The message to log with ERROR level.
	 * @param throwable The throwable to log.
	 */
	default void error(String message, Throwable throwable)
	{
		this.log(LogLevel.ERROR, message, throwable);
	}
	
	/**
	 * Logs an object's string representation as an error message along with a Throwable.
	 * 
	 * @param object The object whose string representation will be logged with ERROR level.
	 * @param throwable The throwable to log.
	 */
	default void error(Object object, Throwable throwable)
	{
		String message = Objects.toString(object);
		
		this.log(LogLevel.ERROR, message, throwable);
	}
	
	/**
	 * Logs a Throwable with the WARNING level.
	 *
	 * @param throwable The throwable to log with WARNING level.
	 */
	default void warning(Throwable throwable)
	{
		String message = throwable.getLocalizedMessage();
		
		this.log(LogLevel.WARNING, message != null ? message : throwable.toString(), throwable);
	}
	
	/**
	 * Logs a warning message.
	 * 
	 * @param message The message to log with WARNING level.
	 */
	default void warning(String message)
	{
		this.log(LogLevel.WARNING, message);
	}
	
	/**
	 * Logs an object's string representation as a warning message.
	 * 
	 * @param object The object whose string representation will be logged with WARNING level.
	 */
	default void warning(Object object)
	{
		String message = Objects.toString(object);
		
		this.log(LogLevel.WARNING, message);
	}
	
	/**
	 * Logs a warning message along with a Throwable.
	 * 
	 * @param message The message to log with WARNING level.
	 * @param throwable The throwable to log.
	 */
	default void warning(String message, Throwable throwable)
	{
		this.log(LogLevel.WARNING, message, throwable);
	}
	
	/**
	 * Logs an object's string representation as a warning message along with a Throwable.
	 * 
	 * @param object The object whose string representation will be logged with WARNING level.
	 * @param throwable The throwable to log.
	 */
	default void warning(Object object, Throwable throwable)
	{
		String message = Objects.toString(object);
		
		this.log(LogLevel.WARNING, message, throwable);
	}
	
	/**
	 * Logs a Throwable with the DEBUG level.
	 *
	 * @param throwable The throwable to log with DEBUG level.
	 */
	default void debug(Throwable throwable)
	{
		String message = throwable.getLocalizedMessage();
		
		this.log(LogLevel.DEBUG, message != null ? message : throwable.toString(), throwable);
	}
	
	/**
	 * Logs a debug message.
	 * 
	 * @param message The message to log with DEBUG level.
	 */
	default void debug(String message)
	{
		this.log(LogLevel.DEBUG, message);
	}
	
	/**
	 * Logs an object's string representation as a debug message.
	 * 
	 * @param object The object whose string representation will be logged with DEBUG level.
	 */
	default void debug(Object object)
	{
		String message = Objects.toString(object);
		
		this.log(LogLevel.DEBUG, message);
	}
	
	/**
	 * Logs a debug message along with a Throwable.
	 * 
	 * @param message The message to log with DEBUG level.
	 * @param throwable The throwable to log.
	 */
	default void debug(String message, Throwable throwable)
	{
		this.log(LogLevel.DEBUG, message, throwable);
	}
	
	/**
	 * Logs an object's string representation as a debug message along with a Throwable.
	 * 
	 * @param object The object whose string representation will be logged with DEBUG level.
	 * @param throwable The throwable to log.
	 */
	default void debug(Object object, Throwable throwable)
	{
		String message = Objects.toString(object);
		
		this.log(LogLevel.DEBUG, message, throwable);
	}
	
	/**
	 * Logs a Throwable with the FATAL level.
	 *
	 * @param throwable The throwable to log with FATAL level.
	 */
	default void fatal(Throwable throwable)
	{
		String message = throwable.getLocalizedMessage();
		
		this.log(LogLevel.FATAL, message != null ? message : throwable.toString(), throwable);
	}
	
	/**
	 * Logs a fatal error message.
	 * 
	 * @param message The message to log with FATAL level.
	 */
	default void fatal(String message)
	{
		this.log(LogLevel.FATAL, message);
	}
	
	/**
	 * Logs an object's string representation as a fatal error message.
	 * 
	 * @param object The object whose string representation will be logged with FATAL level.
	 */
	default void fatal(Object object)
	{
		String message = Objects.toString(object);
		
		this.log(LogLevel.FATAL, message);
	}
	
	/**
	 * Logs a fatal error message along with a Throwable.
	 * 
	 * @param message The message to log with FATAL level.
	 * @param throwable The throwable to log.
	 */
	default void fatal(String message, Throwable throwable)
	{
		this.log(LogLevel.FATAL, message, throwable);
	}
	
	/**
	 * Logs an object's string representation as a fatal error message along with a Throwable.
	 * 
	 * @param object The object whose string representation will be logged with FATAL level.
	 * @param throwable The throwable to log.
	 */
	default void fatal(Object object, Throwable throwable)
	{
		String message = Objects.toString(object);
		
		this.log(LogLevel.FATAL, message, throwable);
	}
	
	/**
	 * Indicates if the logger is configured to log the DEBUG level messages.
	 * 
	 * @return
	 * - <b>true</b> if DEBUG level messages are being logged.<br>
	 * - <b>false</b> if DEBUG level messages are not being logged.
	 */
	public boolean isDebugging();
}