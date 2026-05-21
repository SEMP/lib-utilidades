package py.com.semp.lib.utilidades.exceptions;

/**
 * Exception to be thrown when the {@link Sate} of a state machine is not found.
 * 
 * @author Sergio Morel
 */
public class StateNotFoundException extends Exception
{
	private static final long serialVersionUID = 458025485484740361L;
	
	public StateNotFoundException()
	{
		super();
	}
	
	public StateNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public StateNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public StateNotFoundException(String message)
	{
		super(message);
	}
	
	public StateNotFoundException(Throwable cause)
	{
		super(cause);
	}
}