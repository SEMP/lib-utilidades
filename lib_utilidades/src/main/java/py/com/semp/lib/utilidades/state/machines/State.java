package py.com.semp.lib.utilidades.state.machines;

/**
 * Represents a single state in a state machine. Implementations of this interface
 * should define the behavior of the state and the logic to determine the next state.
 * 
 * @author Sergio Morel
 */
public interface State
{
	/**
     * Executes the behavior associated with this state and determines the next state.
     *
     * @return The key of the next state to transition to. Returning null or a non-existent key
     *         will cause the state machine to halt or throw an exception, respectively.
     *         
     * @author Sergio Morel
     */
	public String executeState();
}