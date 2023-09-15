package py.com.semp.lib.utilidades.enumerations;

/**
 * Indicates what was the previous iteration movement.
 * 
 * @author Sergio Morel
 */
public enum IterationMovement
{
	/**
	 * Indicates that the previous movement in the iterator was backward.
	 */
	PREVIOUS,
	
	/**
	 * Indicates that no movement has occurred, or the state has been reset post-operation.
	 */
	NONE,
	
	/**
	 * Indicates that the previous movement in the iterator was forward.
	 */
	NEXT;
}