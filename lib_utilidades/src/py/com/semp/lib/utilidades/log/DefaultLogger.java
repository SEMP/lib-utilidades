package py.com.semp.lib.utilidades.log;

import java.time.Instant;

import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;
import py.com.semp.lib.utilidades.utilities.Utilities;

/**
 * The DefaultLogger class provides a simple logging mechanism that writes log messages
 * to standard output streams.
 * 
 * This logger directs logs according to {@link LogLevel}.<br>
 * {@code System.out} for:
 * <ul>
 * 	<li>{@code LogLevel.INFO}</li>
 * 	<li>{@code LogLevel.DEBUG}</li>
 * </ul>
 * {@code System.err} for:
 * <ul>
 * 	<li>{@code LogLevel.FATAL}</li>
 * 	<li>{@code LogLevel.ERROR}</li>
 * 	<li>{@code LogLevel.WARNING}</li>
 * </ul>
 * 
 * @author Sergio Morel
 * @see Logger
 * @see LogLevel
 */
public class DefaultLogger implements Logger
{
	private volatile boolean debug = false;
	@Override
	public void log(LogLevel level, String message)
	{
		if(level == LogLevel.ERROR || level == LogLevel.FATAL || level == LogLevel.WARNING)
		{
			System.err.println(Utilities.toString(Instant.now()));
			System.err.println(message);
		}
		else
		{
			if(level == LogLevel.DEBUG)
			{
				if(this.debug)
				{
					System.out.println(Utilities.toString(Instant.now()));
					System.out.println(message);
				}
			}
			else
			{
				System.out.println(Utilities.toString(Instant.now()));
				System.out.println(message);
			}
		}
	}
	
	@Override
	public void log(LogLevel level, String message, Throwable throwable)
	{
		if(level == LogLevel.ERROR || level == LogLevel.FATAL || level == LogLevel.WARNING)
		{
			System.err.println(Utilities.toString(Instant.now()));
			System.err.println(message);
			
			Throwable[] suppressedExceptions = throwable.getSuppressed();
			
			for(Throwable suppressedException : suppressedExceptions)
			{
				String errorMessage = MessageUtil.getMessage(Messages.SUPPRESSED_EXCEPTION, suppressedException.getMessage());
				
		        System.err.println(errorMessage);
		    }
			
			throwable.printStackTrace(System.err);
		}
		else
		{
			if(level == LogLevel.DEBUG)
			{
				if(this.debug)
				{
					System.out.println(Utilities.toString(Instant.now()));
					System.out.println(message);
					throwable.printStackTrace(System.out);
				}
			}
			else
			{
				System.out.println(Utilities.toString(Instant.now()));
				System.out.println(message);
				throwable.printStackTrace(System.out);
			}
		}
	}
	
	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}
	
	@Override
	public boolean isDebugging()
	{
		return this.debug;
	}
}