package py.com.semp.lib.utilidades.communication;

import py.com.semp.lib.utilidades.communication.listeners.ConnectionEventListener;
import py.com.semp.lib.utilidades.configuration.ConfigurationValues;
import py.com.semp.lib.utilidades.exceptions.CommunicationException;

public interface DataInterface
{
	public DataInterface connect() throws CommunicationException;
	public DataInterface connect(ConfigurationValues configurationValues) throws CommunicationException;
	public DataInterface disconnect() throws CommunicationException;
	public DataInterface setConfigurationValues(ConfigurationValues configurationValues) throws CommunicationException;
	public ConfigurationValues getConfigurationValues();
	public DataInterface addConnectionEventListeners(ConnectionEventListener... connectionEventListeners);
	public DataInterface removeConnectionEventListeners(ConnectionEventListener... connectionEventListeners);
	public DataInterface removeAllConnectionEventListeners();
	public String getStringIdentifier();
	
	/**
	 * Obtencion del estado de la conexi&oacute;n.
	 * 
	 * @return
	 * estado de la conexi&oacute;n.
	 */
	public boolean isConnected();
}