package py.com.semp.lib.utilidades.shutdown;

import py.com.semp.lib.utilidades.exceptions.ShutdownException;

public interface ShutdownCapable
{
	public ShutdownCapable shutdown() throws ShutdownException;
}