package py.com.semp.lib.utilidades.communication.interfaces;

import py.com.semp.lib.utilidades.communication.listeners.DataListener;

public interface DataCommunicator extends DataInterface, DataReceiver, DataTransmitter
{
	@Override
	public DataCommunicator addDataListeners(DataListener... listeners);
	
	@Override
	public DataCommunicator removeDataListeners(DataListener... listeners);
	
	@Override
	public DataCommunicator removeAllDataListeners();
}