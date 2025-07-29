package py.com.semp.lib.utilidades.exceptions;

/**
 * Exception to be thrown when there is an error during the process of obtaining data
 * data. For example from a database.
 * 
 * @author Sergio Morel
 */
public class DataAccessException extends Exception
{
	private static final long serialVersionUID = 458025485484740361L;
	
	public DataAccessException()
	{
		super();
	}
	
	public DataAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public DataAccessException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public DataAccessException(String message)
	{
		super(message);
	}
	
	public DataAccessException(Throwable cause)
	{
		super(cause);
	}
}