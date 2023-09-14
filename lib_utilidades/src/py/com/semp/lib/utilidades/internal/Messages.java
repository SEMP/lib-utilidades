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
	CIRCULAR_BUFFER_OUT_OF_BOUNDS("CIRCULAR_BUFFER_OUT_OF_BOUNDS"),
	BUFFER_EMPTY_ERROR("BUFFER_EMPTY_ERROR"),
	CALL_NEXT_BEFORE_REMOVE_ERROR("CALL_NEXT_BEFORE_REMOVE_ERROR"),
	ELEMENT_ARRAY_TYPE_ERROR("ELEMENT_ARRAY_TYPE_ERROR");
	
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