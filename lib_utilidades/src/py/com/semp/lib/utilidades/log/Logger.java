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
	 * Logs a warning message.
	 * 
	 * @param message The message to log with WARN level.
	 */
	default void warning(String message)
	{
		this.log(LogLevel.WARN, message);
	}
	
	/**
	 * Logs an object's string representation as a warning message.
	 * 
	 * @param object The object whose string representation will be logged with WARN level.
	 */
	default void warning(Object object)
	{
		String message = Objects.toString(object);
		
		this.log(LogLevel.WARN, message);
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
}