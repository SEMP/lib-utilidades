package py.com.semp.lib.utilidades.exceptions;

/**
 * Exception to be thrown when an object is not found.
 * 
 * @author Sergio Morel
 */
public class ObjectNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = -6298905681540674883L;
	
	public ObjectNotFoundException()
	{
		super();
	}
	
	public ObjectNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public ObjectNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public ObjectNotFoundException(String message)
	{
		super(message);
	}
	
	public ObjectNotFoundException(Throwable cause)
	{
		super(cause);
	}
}