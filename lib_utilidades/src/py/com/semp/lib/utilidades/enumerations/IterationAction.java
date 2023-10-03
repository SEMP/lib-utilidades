package py.com.semp.lib.utilidades.enumerations;

/**
 * Indicates what was the previous iteration movement.
 * 
 * @author Sergio Morel
 */
public enum IterationAction
{
	/**
	 * Indicates that the previous action was a movement was backward.
	 */
	PREVIOUS,
	
	/**
	 * Indicates that no action has occurred, or the state has been reset post-operation.
	 */
	NONE,
	
	/**
	 * Indicates that the previous action was a movement was forward.
	 */
	NEXT,
	
	/**
	 * Indicates that the previous action was to add an element.
	 */
	ADD,
	
	/**
	 * Indicates that the previous action was to remove an element.
	 */
	REMOVE;
}