package py.com.semp.lib.utilidades.utilities;

import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;
import py.com.semp.lib.utilidades.log.JSONLogger;
import py.com.semp.lib.utilidades.log.UtilLogger;


//TODO incluir mapas para los loggers.
public final class Factory
{
	private Factory()
	{
		super();
		
		String errorMessage = MessageUtil.getMessage(Messages.DONT_INSTANTIATE, this.getClass().getName());
		
		throw new AssertionError(errorMessage);
	}
	
	public static UtilLogger getLogger()
	{
		return new UtilLogger();
	}
	
	public static JSONLogger getJSONLogger()
	{
		return new JSONLogger();
	}
}