package py.com.semp.lib.utilidades.communication.interfaces;

import java.util.Set;

import py.com.semp.lib.utilidades.communication.listeners.ConnectionEventListener;
import py.com.semp.lib.utilidades.configuration.ConfigurationValues;
import py.com.semp.lib.utilidades.exceptions.CommunicationException;

/**
 * Provides an interface for data communication operations, allowing for connection management,
 * configuration setting, and event handling related to the connection.
 *
 * <p>This interface defines methods to establish and terminate connections, set configuration values,
 * manage connection event listeners, and fetch the status or identifier of the connection.</p>
 * 
 * @author Sergio Morel
 */
public interface DataInterface
{
	/**
     * Connects to the data source without any specific configuration.
     *
     * @return A reference to this DataInterface instance.
     * @throws CommunicationException If any error occurs during the connection process.
     */
	public DataInterface connect() throws CommunicationException;
	
	/**
     * Connects to the data source using the provided configuration values.
     *
     * @param configurationValues The configuration values to be used for the connection.
     * @return A reference to this DataInterface instance.
     * @throws CommunicationException If any error occurs during the connection process.
     */
	public DataInterface connect(ConfigurationValues configurationValues) throws CommunicationException;
	
	/**
     * Disconnects from the data source.
     *
     * @return A reference to this DataInterface instance.
     * @throws CommunicationException If any error occurs during the disconnection process.
     */
	public DataInterface disconnect() throws CommunicationException;
	
	/**
     * Sets the configuration values for this data interface.
     *
     * @param configurationValues The configuration values to be set.
     * @return A reference to this DataInterface instance.
     * @throws CommunicationException If any error occurs while setting the configuration.
     */
	public DataInterface setConfigurationValues(ConfigurationValues configurationValues) throws CommunicationException;
	
	/**
     * Retrieves the current configuration values of this data interface.
     *
     * @return The current configuration values.
     */
	public ConfigurationValues getConfigurationValues();
	
	/**
     * Adds one or more connection event listeners to this data interface.
     *
     * @param listeners The event listeners to be added.
     * @return A reference to this DataInterface instance.
     */
	public DataInterface addConnectionEventListeners(ConnectionEventListener... listeners);
	
	/**
     * Removes one or more connection event listeners from this data interface.
     *
     * @param listeners The event listeners to be removed.
     * @return A reference to this DataInterface instance.
     */
	public DataInterface removeConnectionEventListeners(ConnectionEventListener... listeners);
	
	/**
     * Removes all connection event listeners from this data interface.
     *
     * @return A reference to this DataInterface instance.
     */
	public DataInterface removeAllConnectionEventListeners();
	
	/**
	 * Obtains the data listeners.
	 * 
	 * @return
	 * - The data listeners.
	 */
	public Set<ConnectionEventListener> getConnectionEventListeners();
	
	/**
     * Retrieves a string identifier for this data interface. This can be used for logging or 
     * other identification purposes.
     *
     * @return A string identifier for this data interface.
     */
	public String getStringIdentifier();
	
	/**
	 * Emergency shutdown method.
	 * 
	 * @return A reference to this DataInterface instance.
     * @throws CommunicationException If any error occurs during the shutdown process.
	 */
	public DataInterface shutdown() throws CommunicationException;
	
	/**
	 * Indicates if the {@link DataInterface} is shutting down.
	 * 
	 * @return
	 * - <b>true</b> if the {@link DataInterface} is being shutdown.<br>
	 * - <b>false</b> otherwise.
	 */
	public boolean isShuttingdown();
	
	/**
     * Checks if the data interface is currently connected to its data source.
     *
     * @return True if connected, false otherwise.
     */
	public boolean isConnected();
}