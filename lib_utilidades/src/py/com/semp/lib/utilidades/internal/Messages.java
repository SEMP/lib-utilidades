package py.com.semp.lib.utilidades.internal;

import py.com.semp.lib.utilidades.messages.MessageKey;

/**
 * Messages for the utilities library.
 * 
 * @author Sergio Morel
 */
public enum Messages implements MessageKey
{
	INVALID_HEX_STRING("INVALID_HEX_STRING"),
	DONT_INSTANTIATE("DONT_INSTANTIATE"),
	BUFFER_NOT_NULL_ERROR("BUFFER_NOT_NULL_ERROR"),
	BUFFER_ARRAY_MINIMUM_SIZE("BUFFER_ARRAY_MINIMUM_SIZE"),
	INVALID_INDEX_RANGE_ERROR("INVALID_INDEX_RANGE_ERROR"),
	NO_DATA_AVAILABLE_ERROR("NO_DATA_AVAILABLE_ERROR"),
	CALL_NEXT_OR_PREVIOUS_BEFORE_ERROR("CALL_NEXT_OR_PREVIOUS_BEFORE_ERROR"),
	ELEMENT_ARRAY_TYPE_ERROR("ELEMENT_ARRAY_TYPE_ERROR"),
	INDEX_OUT_OF_BOUNDS("INDEX_OUT_OF_BOUNDS"),
	NULL_VALUES_NOT_ALLOWED_ERROR("NULL_VALUES_NOT_ALLOWED_ERROR"),
	TYPE_OF_NULL_VALUE_ERROR("TYPE_OF_NULL_VALUE_ERROR"),
	TYPE_NOT_DEFINED_ERROR("TYPE_NOT_DEFINED_ERROR"),
	INVALID_NAME_ERROR("INVALID_NAME_ERROR"),
	SHUTDOWN_ERROR("SHUTDOWN_ERROR");
	
	private final String messageKey;
	
	private Messages(String messageKey)
	{
		this.messageKey = messageKey;
	}
	
	@Override
	public String getMessageKey()
	{
		return messageKey;
	}
}