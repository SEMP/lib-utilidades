package py.com.semp.lib.utilidades.configuration;

/**
 * Contains the value of constants used in the project.
 * 
 * @author Sergio Morel
 */
public interface Values
{
	/**
	 * Contains constants values
	 * 
	 * @author Sergio Morel
	 */
	public interface Constants
	{
		//Integers
		/**
		 * Value of the index for the buffer boundaries. 
		 */
		public static final int BUFFER_BOUNDARY = -1;
		
		//Strings
		/**
		 * Path where the messages for localization are found.
		 */
		public static final String MESSAGES_PATH = "py/com/semp/lib/utilidades";
	}
	
	/**
	 * Contains resources names
	 * 
	 * @author Sergio Morel
	 */
	public interface Resources
	{
		/**
		 * Base name of the boundle of properties files for each language.
		 */
		public static final String MESSAGES_BASE_NAME = "messages";
	}
}