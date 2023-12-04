package py.com.semp.lib.utilidades.exceptions;

/**
 * Exception to be thrown when an error occurs during the shutdown process.
 * 
 * @author Sergio Morel
 */
public class ShutdownException extends Exception
{
	private static final long serialVersionUID = -6186944764659819811L;
	
	public ShutdownException()
	{
		super();
	}
	
	public ShutdownException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public ShutdownException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public ShutdownException(String message)
	{
		super(message);
	}
	
	public ShutdownException(Throwable cause)
	{
		super(cause);
	}
}