package py.com.semp.lib.utilidades.messages;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * MessageManager provides functionality to retrieve localized messages 
 * based on the specified locale.
 * 
 * @author Sergio Morel
 */
public record MessageManager(String path, String resource, Locale locale)
{
	/**
     * Constructor that initializes the MessageManager with the given path and 
     * resource and uses the default locale.
     *
     * @param path
     * - The base path for the resource bundles.
     * @param resource
     * - The base name of the resource bundle.
     * @author Sergio Morel
     */
	public MessageManager(String path, String resource)
	{
		this(path, resource, Locale.getDefault());
	}
	
	/**
     * Canonical constructor that ensures the path ends with a trailing slash.
     * 
     * @author Sergio Morel
     */
	public MessageManager
	{
		path = this.ensureTrailingSlash(path);
    }
	
	/**
     * Ensures that the provided path ends with a trailing slash ('/').
     *
     * @param path
     * - The path to be checked and possibly modified.
     * @return
     * - The path ending with a trailing slash, or null if the input path was null.
     * @author Sergio Morel
     */
	private String ensureTrailingSlash(String path)
	{
		if(path == null)
		{
			return null;
		}
		
		return path.endsWith("/") ? path : path + "/";
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
		ResourceBundle bundle = ResourceBundle.getBundle
		(
			this.path() + this.resource(),
			this.locale()
		);
		
		String message = bundle.getString(messageKey);
		
		if(arguments == null || arguments.length < 1)
		{
			return message;
		}
		
		Object[] notNullArguments = new Object[arguments.length];
		
		for(int i = 0; i < notNullArguments.length; i++)
		{
			if(arguments[i] == null)
			{
				notNullArguments[i] = "null";
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