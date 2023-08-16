package py.com.semp.lib.messages;

import java.util.Locale;
import java.util.ResourceBundle;

public record MessageManager(String path, String resource, Locale locale)
{
	public MessageManager(String path, String resource)
	{
		this(path, resource, Locale.getDefault());
	}
	
	public MessageManager
	{
		path = ensureTrailingSlash(path);
    }
	
	private String ensureTrailingSlash(String path)
	{
		if(path == null)
		{
			return null;
		}
		
		return path.endsWith("/") ? path : path + "/";
	}
	
	public String getMessage(String message)
	{
		ResourceBundle bundle = ResourceBundle.getBundle
		(
			this.path() + this.resource(),
			this.locale()
		);
		
		return bundle.getString(message);
	}
}