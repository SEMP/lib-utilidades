package py.com.semp.lib.utilidades.exceptions;

/**
 * Exception to be thrown when there is an error during the process of data
 * communication.|
 * 
 * @author Sergio Morel
 */
public class CommunicationException extends Exception
{
	private static final long serialVersionUID = 458025485484740361L;
	
	public CommunicationException()
	{
		super();
	}
	
	public CommunicationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public CommunicationException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public CommunicationException(String message)
	{
		super(message);
	}
	
	public CommunicationException(Throwable cause)
	{
		super(cause);
	}
}