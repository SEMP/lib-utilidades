package py.com.semp.lib.utilidades.messages;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import py.com.semp.lib.utilidades.configuration.Values;
import py.com.semp.lib.utilidades.log.Logger;
import py.com.semp.lib.utilidades.log.LoggerManager;

/**
 * An abstract class for retrieval of messages.
 * This class is should be used as base for classes that would manage a {@link MessageManager} instance.
 * 
 * @author Sergio Morel
 */
public abstract class MessageRetriever
{
	private MessageManager messageManager;
	
	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
	
	/**
	 * Constructor that initializes the class with the default {@link Locale}
	 */
	public MessageRetriever()
	{
		super();
		
		this.messageManager = this.getMessageManager();
	}
	
	/**
	 * Constructor that receives a {@link Locale} instance.
	 * 
	 * @param locale
	 * - {@link Locale} for this instance..
	 */
	public MessageRetriever(Locale locale)
	{
		super();
		
		this.messageManager = this.getNewMessageManager(locale);
	}
	
	/**
	 * Retrieves a message corresponding to the provided key from the shared resource bundle.
	 * 
	 * @param messageKey
	 * - The key of the desired message.
	 * @param arguments
	 * - Arguments to build the string.
	 * @return
	 * - The message string associated with the key.
	 */
	public String getMessage(String messageKey, Object... arguments)
	{
		MessageManager messageManager = this.getMessageManager();
		
		try
		{
			return messageManager.getMessage(messageKey, arguments);
		}
		catch(Exception e)
		{
			StringBuilder errorMessage = new StringBuilder();
			
			errorMessage.append("Failed to load message: ");
			errorMessage.append(messageKey).append("\n");
			
			for(Object object : arguments)
			{
				errorMessage.append(" - ");
				errorMessage.append(object);
				errorMessage.append("\n");
			}
			
			return errorMessage.toString();
		}
	}
	
	/**
	 * Retrieves a message corresponding to the provided {@link MessageKey} from the shared resource bundle.
	 * 
	 * @param messageKey
	 * - The MessageKey enum representation of the desired message key.
	 * @param arguments
	 * - Arguments to build the string.
	 * @return
	 * - The message string associated with the key.
	 */
	public String getMessage(MessageKey messageKey, Object... arguments)
	{
		return this.getMessage(messageKey.getMessageKey(), arguments);
	}
	
	/**
	 * Obtains the {@link MessageManager} instance. If the value is null, it gets
	 * initialized with the default locale.
	 * 
	 * @return
	 * - The {@link MessageManager} instance of this class. 
	 */
	protected MessageManager getMessageManager()
	{
		this.rwLock.readLock().lock();
		
		try
		{
			if(this.messageManager == null)
			{
				this.rwLock.readLock().unlock();
				this.rwLock.writeLock().lock();
				
				try
				{
					if(this.messageManager == null)
					{
						this.messageManager = this.getNewMessageManager(Locale.getDefault());
					}
				}
				catch(Exception e)
				{
					Logger logger = LoggerManager.getLogger(Values.Constants.UTILITIES_CONTEXT);
					
					logger.error(e);
				}
				finally
				{
					this.rwLock.readLock().lock();
					this.rwLock.writeLock().unlock();
				}
			}
			
			return this.messageManager;
		}
		finally
		{
			this.rwLock.readLock().unlock();
		}
	}
	
	/**
	 * Creates the {@link ResourceBundle} using the {@link Locale} to instantiate a new {@link MessageManager}.
	 * 
	 * @param locale
	 * - {@link Locale} used to create the new {@link MessageManager}.
	 * 
	 * @return
	 * - The new {@link MessageManager}.
	 */
	protected abstract MessageManager getNewMessageManager(Locale locale);
	
	/**
	 * Sets a new {@link MessageManager} with the locale configured.
	 * 
	 * @param locale
	 * - The locale to configure.
	 */
	public void setLocale(Locale locale)
	{
		this.rwLock.writeLock().lock();
		
		try
		{
			this.messageManager = this.getNewMessageManager(locale);
		}
		finally
		{
			this.rwLock.writeLock().unlock();
		}
	}
}