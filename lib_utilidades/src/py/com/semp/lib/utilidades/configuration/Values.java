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
		
		/**
		 * Time delay for polls.
		 */
		public static final int POLL_DELAY_MS = 50;
		
		/**
		 * The size of the thread pool used to inform listeners of events.
		 */
		public static final int LISTENERS_THREAD_POOL_SIZE = 10;
		
		//Longs
		/**
		 * Time to wait for tasks termination.
		 */
		public static final int TERMINATION_TIMOUT_MS = 3000;
		
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
		public static final String MESSAGES_PATH = "/py/com/semp/lib/utilidades/";
		
		/**
		 * String representation of a null value.
		 */
		public static final String NULL_VALUE_STRING = "null";
	}
	
	/**
	 * Contains variable names
	 * 
	 * @author Sergio Morel
	 */
	public interface VariableNames
	{
		/**
		 * The time communication will be blocked during a read operation before throwing an exception.<br>
		 */
		public static final String READ_TIMEOUT_MS = "readTimeoutMS";
	}
	
	/**
	 * Contains constants with default values.
	 * 
	 * @author Sergio Morel
	 */
	public interface Defaults
	{
		//Integer values
		public static final Integer CONNECTION_TIMEOUT_MS = -1;
		public static final Integer READ_TIMEOUT_MS = -1;
		public static final Integer WRITE_TIMEOUT_MS = -1;
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