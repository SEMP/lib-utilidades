package py.com.semp.lib.utilidades.log;

/**
 * Enum to represent different logging levels for the {@link py.com.semp.lib.utilidades.log.Logger} interface.
 * The levels are modeled to be general enough to adapt to different logging libraries like Log4j, SLF4J, etc.
 *
 * <ul>
 *     <li>FATAL: Critical errors causing premature termination.</li>
 *     <li>ERROR: Errors that allow the application to continue running, but need to be addressed.</li>
 *     <li>WARN: Potentially harmful situations that deserve attention.</li>
 *     <li>INFO: Informational messages highlighting application progress.</li>
 *     <li>DEBUG: Fine-grained messages useful for debugging.</li>
 * </ul>
 */
public enum LogLevel
{
    /**
     * Critical conditions causing the application to terminate.
     */
    FATAL,
    
    /**
     * Error conditions that may allow the application to continue running.
     */
    ERROR,
    
    /**
     * Warning conditions that require attention but are not critical.
     */
    WARN,
    
    /**
     * Informational messages that provide insights into the application's behavior.
     */
    INFO,
    
    /**
     * Debug-level messages, usually useful only during debugging.
     */
    DEBUG
}