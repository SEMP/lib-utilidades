package py.com.semp.lib.utilidades.internal;

import py.com.semp.lib.utilidades.messages.MessageKey;

/**
 * Messages for the utilities library.
 * 
 * @author Sergio Morel
 */
public enum Messages implements MessageKey
{
	INVALID_HEX_STRING,
	DONT_INSTANTIATE,
	BUFFER_NOT_NULL_ERROR,
	BUFFER_ARRAY_MINIMUM_SIZE,
	INVALID_INDEX_RANGE_ERROR,
	NO_DATA_AVAILABLE_ERROR,
	CALL_NEXT_OR_PREVIOUS_BEFORE_ERROR,
	ELEMENT_ARRAY_TYPE_ERROR,
	INDEX_OUT_OF_BOUNDS,
	NULL_VALUES_NOT_ALLOWED_ERROR,
	TYPE_OF_NULL_VALUE_ERROR,
	TYPE_NOT_DEFINED_ERROR,
	INVALID_NAME_ERROR,
	SHUTDOWN_ERROR,
	LISTENER_THROWN_EXCEPTION_ERROR,
	TASK_SHUTDOWN_ERROR,
	TERMINATION_TIMEOUT_ERROR,
	READING_TIMOUT_ERROR,
	CONNECTION_CLOSED_ERROR,
	READING_ERROR,
	DISCONNECTION_ERROR,
	INTERRUPTED_ERROR,
	CONNECTION_ERROR,
	STATE_NOT_FOUND_ERROR,
	SUPPRESSED_EXCEPTION;
	
	@Override
	public String getMessageKey()
	{
		return this.name();
	}
}