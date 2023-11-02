package py.com.semp.lib.utilidades.communication.interfaces;

public interface DataReader extends Runnable
{
	public boolean stopReading();
	public boolean isReading();
}