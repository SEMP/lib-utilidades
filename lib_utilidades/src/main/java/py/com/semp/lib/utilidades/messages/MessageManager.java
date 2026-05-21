package py.com.semp.lib.utilidades.messages;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import py.com.semp.lib.utilidades.configuration.Values;

/**
 * MessageManager provides functionality to retrieve localized messages 
 * based on the specified locale.
 * 
 * @author Sergio Morel
 */
public class MessageManager
{
	private final ResourceBundle resourceBundle;
	
	public MessageManager(ResourceBundle resourceBundle)
	{
		this.resourceBundle = resourceBundle;
	}
	
	/**
     * Retrieves the message corresponding to the provided key from the resource bundle.
     *
     * @param messageKey
     * - The key for the desired message in the resource bundle.
     * @param arguments
     * - Arguments to build the string.
     * @return
     * - The localized message string.
     * @author Sergio Morel
     */
	public String getMessage(String messageKey, Object... arguments)
	{
		String message = this.resourceBundle.getString(messageKey);
		
		if(arguments == null || arguments.length < 1)
		{
			return message;
		}
		
		Object[] notNullArguments = new Object[arguments.length];
		
		for(int i = 0; i < notNullArguments.length; i++)
		{
			if(arguments[i] == null)
			{
				notNullArguments[i] = Values.Constants.NULL_VALUE_STRING;
			}
			else
			{
				notNullArguments[i] = arguments[i];
			}
		}
		
		return MessageFormat.format(message, notNullArguments);
	}
	
	/**
     * Retrieves the message corresponding to the provided key from the resource bundle.
     *
     * @param messageKey
     * - The key for the desired message in the resource bundle.
     * @param arguments
     * - Arguments to build the string.
     * @return
     * - The localized message string.
     * @author Sergio Morel
     */
	public String getMessage(MessageKey messageKey, Object... arguments)
	{
		return this.getMessage(messageKey.getMessageKey(), arguments);
	}
}