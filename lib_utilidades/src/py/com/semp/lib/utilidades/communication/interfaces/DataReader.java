package py.com.semp.lib.utilidades.communication.interfaces;

public interface DataReader extends Runnable
{
	public void startReading();
	public void pauseReading();
	public void stopReading();
	public boolean isReading();
	public boolean hasFinalized();
}