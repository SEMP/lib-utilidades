package py.com.semp.lib.utilidades.log;

public class UtilLogger implements Logger
{
	@Override
	public void log(LogLevel level, String message)
	{
		// TODO Auto-generated method stub
		System.out.println(message);
	}
	
	@Override
	public void log(LogLevel level, String message, Throwable throwable)
	{
		// TODO Auto-generated method stub
		System.out.println(message);
		System.out.println(throwable.getMessage());
	}
}