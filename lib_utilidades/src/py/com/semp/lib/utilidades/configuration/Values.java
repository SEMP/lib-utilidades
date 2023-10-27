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
		 * Context name for the utilities library
		 */
		public static final String UTILITIES_CONTEXT = "lib_utilidades";
		
		/**
		 * Context name for Shutdown
		 */
		public static final String SHUTDOWN_CONTEXT = "shutdown";
		
		/**
		 * Path where the messages for localization are found.
		 */
		public static final String MESSAGES_PATH = "py/com/semp/lib/utilidades";
		
		/**
		 * String representation of a null value.
		 */
		public static final String NULL_VALUE_STRING = "null";
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