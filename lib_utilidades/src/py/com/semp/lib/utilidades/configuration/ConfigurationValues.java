package py.com.semp.lib.utilidades.configuration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import py.com.semp.lib.utilidades.data.TypedParameter;
import py.com.semp.lib.utilidades.data.TypedValue;
import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;
import py.com.semp.lib.utilidades.log.Logger;
import py.com.semp.lib.utilidades.log.LoggerManager;
import py.com.semp.lib.utilidades.utilities.Converter;
import py.com.semp.lib.utilidades.utilities.Utilities;

/**
 * Class for storing configuration values.
 * 
 * @author Sergio Morel
 */
public abstract class ConfigurationValues
{
	private final ConcurrentHashMap<String, TypedValue<?>> parameters = new ConcurrentHashMap<>();
	private final CopyOnWriteArraySet<TypedParameter> requiredParameters = new CopyOnWriteArraySet<>();
	private final CopyOnWriteArraySet<TypedParameter> optionalParameters = new CopyOnWriteArraySet<>();
	
	/**
	 * Constructor to set required, optional and default parameters.
	 */
	public ConfigurationValues()
	{
		this.setRequiredParameters();
		this.setOptionalParameters();
		this.setDefaultValues();
	}
	
	/**
	 * Stores a parameter. The value can't be null.
	 * 
	 * @param name Parameter name.
	 * @param value Parameter value.
	 * @return ConfigurationValues object used to call this method.
	 */
	public <T> ConfigurationValues setParameter(String name, T value)
	{
		TypedValue<T> typedValue = new TypedValue<T>(value);
		
		this.setParameter(name, typedValue);
		
		return this;
	}
	
	/**
	 * Store a parameter.
	 * 
	 * @param type Parameter data type.
	 * @param name Parameter name.
	 * @param value Parameter value.
	 * @return ConfigurationValues object used to call this method.
	 */
	public <T> ConfigurationValues setParameter(Class<T> type, String name, T value)
	{
		TypedValue<T> typedValue = new TypedValue<T>(type, value);
		
		this.setParameter(name, typedValue);
		
		return this;
	}
	
	public <T> void setParameter(String name, TypedValue<T> typedValue)
	{
		this.checkValidName(name);
		
		this.parameters.put(name, typedValue);
	}
	
	/**
	 * Fetches the parameter type and value that matches the given name.
	 * 
	 * @param name Parameter name to fetch.
	 * @return TypedValue object. Null if no parameter with the given name is found.
	 */
	public TypedValue<?> getParameter(String name)
	{
		TypedValue<?> typedValue;
		
		typedValue = this.parameters.get(name);
		
		return typedValue;
	}
	
	/**
	 * Get the parameter map.
	 * 
	 * @return Map containing the parameters.
	 */
	public Map<String, TypedValue<?>> getParameters()
	{
		return this.parameters;
	}
	
	/**
	 * Retrieves the value for the specified configuration parameter name.
	 * The value is cast to the expected type.
	 *
	 * @param <T>   the expected type of the configuration parameter value
	 * @param name  the name of the configuration parameter to retrieve
	 * @return the value of the configuration parameter cast to the expected type, or {@code null} if not found
	 * @throws ClassCastException if the value cannot be cast to the expected type
	 */
	public <T> T getValue(String name)
	{
		TypedValue<?> typedValue = this.getParameter(name);
		
		if(typedValue == null)
		{
			return null;
		}
		
		@SuppressWarnings("unchecked")
		T value = (T)typedValue.getValue();
		
		return value;
	}
	
	/**
	 * Retrieves the value for the specified configuration parameter name,
	 * returning a default value if the parameter is not found or is set to {@code null}.
	 *
	 * @param <T>          the expected type of the configuration parameter value
	 * @param name         the name of the configuration parameter to retrieve
	 * @param defaultValue the default value to return if the actual value is {@code null} or not found
	 * @return the value of the configuration parameter if it exists, otherwise the default value
	 */
	public <T> T getValue(String name, T defaultValue)
	{
		T value = this.getValue(name);
		
		if(value == null)
		{
			return defaultValue;
		}
		
		return value;
	}
	
	/**
	 * Validates if all required parameters with correct data types are present.
	 * 
	 * @return true if parameters are correct, false otherwise.
	 */
	public boolean checkRequiredParameters()
	{
		Set<TypedParameter> requiredParameters = this.getRequiredParameters();
		
		for(TypedParameter requiredParameter : requiredParameters)
		{
			if(!this.checkRequiredParameter(requiredParameter))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Verifies if the required parameter is stored. It also checks if the data type
	 * of the parameter is correct.
	 * 
	 * @param requiredParameter
	 * {@link TypedParameter} that contains the data type and name of the required parameter.
	 * @return
	 * - <b>true</b> if the required parameter is stored.<br>
	 * - <b>false</b> if the required parameter isn't stored or if the data type is not compatible.<br>
	 */
	private boolean checkRequiredParameter(TypedParameter requiredParameter)
	{
		String requiredName = requiredParameter.getName();
		Class<?> requiredType = requiredParameter.getType();
		
		TypedValue<?> typedValue = this.getParameter(requiredName);
		
		if(typedValue == null)
		{
			return false;
		}
		
		Class<?> type = typedValue.getType();
		
		if(!requiredType.isAssignableFrom(type))
		{
			return false;
		}
		
		return true;
	}
	
	private void checkValidName(String name)
	{
		if(name == null || name.trim().isEmpty())
		{
			String errorMessage = MessageUtil.getMessage(Messages.INVALID_NAME_ERROR, name);
			
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
	/**
	 * Set required configuration values.
	 */
	protected abstract void setRequiredParameters();
	
	/**
	 * Set optional configuration values.
	 */
	protected abstract void setOptionalParameters();
	
	/**
	 * Set default configuration values.
	 */
	protected abstract void setDefaultValues();
	
	/**
	 * Fetches a list of required parameters for communication.
	 * 
	 * @return Set of TypedParameter.
	 */
	public Set<TypedParameter> getRequiredParameters()
	{
		return this.requiredParameters;
	}
	
	/**
	 * Adds a new required parameter.
	 * 
	 * @param typedParameter Parameter to be added.
	 * @return ConfigurationValues object used to call this method.
	 */
	public ConfigurationValues addRequiredParameter(TypedParameter typedParameter)
	{
		this.requiredParameters.add(typedParameter);
		
		return this;
	}
	
	/**
	 * Adds a new required parameter.
	 * 
	 * @param parameterType Parameter data type.
	 * @param parameterName Parameter name.
	 * @return ConfigurationValues object used to call this method.
	 */
	public ConfigurationValues addRequiredParameter(Class<?> parameterType, String parameterName)
	{
		TypedParameter typedParameter = new TypedParameter(parameterType, parameterName);
		
		return this.addRequiredParameter(typedParameter);
	}
	
	/**
	 * Fetches a list of optional parameters for communication.
	 * 
	 * @return Set of TypedParameter.
	 */
	public Set<TypedParameter> getOptionalParameters()
	{
		return this.optionalParameters;
	}
	
	/**
	 * Adds a new optional parameter.
	 * 
	 * @param typedParameter Parameter to be added.
	 * @return ConfigurationValues object used to call this method.
	 */
	public ConfigurationValues addOptionalParameter(TypedParameter typedParameter)
	{
		this.optionalParameters.add(typedParameter);
		
		return this;
	}
	
	/**
	 * Adds a new optional parameter.
	 * 
	 * @param parameterType Parameter data type.
	 * @param parameterName Parameter name.
	 * @return ConfigurationValues object used to call this method.
	 */
	public ConfigurationValues addOptionalParameter(Class<?> parameterType, String parameterName)
	{
		TypedParameter typedParameter = new TypedParameter(parameterType, parameterName);
		
		return this.addOptionalParameter(typedParameter);
	}
	
	/**
	 * Loads configuration values from environment variables.
	 * <p>
	 * For each required and optional parameter, this method checks if an environment variable
	 * with the same name exists (case-sensitive). If so, it attempts to parse the value
	 * using the expected type and stores it into the configuration.
	 * <p>
	 * This method fails gracefully if access to environment variables is restricted by the security manager.
	 *
	 * @return the current {@code ConfigurationValues} instance (for method chaining)
	 */
	public ConfigurationValues loadFromEnvironment()
	{
		Map<String, String> envMap;
		
		try
		{
			envMap = System.getenv();
		}
		catch(SecurityException e)
		{
			Logger logger = LoggerManager.getLogger(Values.Constants.UTILITIES_CONTEXT);
			
			String errorMessage = MessageUtil.getMessage(Messages.ACCESS_DENIED_ENV_ERROR);
			
			logger.debug(errorMessage, e);
			
			return this;
		}
		
		return this.loadFromMap(envMap);
	}
	
	/**
	 * Loads all environment variables into this configuration instance.
	 * <p>
	 * This method behaves like {@link #loadAllFromMap(Map)} but specifically reads from the
	 * system environment via {@link System#getenv()}. It attempts to load both declared
	 * (required and optional) and undeclared environment variables. Declared parameters
	 * are parsed using their expected types, while undeclared variables are loaded as
	 * {@code String}-typed values.
	 * <p>
	 * If access to the environment is denied due to security restrictions, the method logs
	 * the issue and returns without modifying the current configuration.
	 *
	 * @return the same {@code ConfigurationValues} instance, for method chaining
	 */
	public ConfigurationValues loadAllFromEnvironment()
	{
		Map<String, String> envMap;
		
		try
		{
			envMap = System.getenv();
		}
		catch(SecurityException e)
		{
			Logger logger = LoggerManager.getLogger(Values.Constants.UTILITIES_CONTEXT);
			
			String errorMessage = MessageUtil.getMessage(Messages.ACCESS_DENIED_ENV_ERROR);
			
			logger.debug(errorMessage, e);
			
			return this;
		}
		
		return this.loadAllFromMap(envMap);
	}
	
	/**
	 * Loads configuration parameters from a {@link Properties} object.
	 * <p>
	 * For each required and optional parameter, this method checks if a property with the same name
	 * exists, and if so, loads it using the expected type.
	 * 
	 * @param properties the {@code Properties} object to load from
	 * @return the same {@code ConfigurationValues} instance, for method chaining
	 */
	public ConfigurationValues loadFromProperties(Properties properties)
	{
		Map<String, String> propertyMap = new HashMap<>();
		
		for(String name : properties.stringPropertyNames())
		{
			propertyMap.put(name, properties.getProperty(name));
		}
		
		return loadFromMap(propertyMap);
	}
	
	/**
	 * Loads configuration parameters from a {@link Properties} object, including both declared
	 * (required and optional) and undeclared parameters.
	 * <p>
	 * Declared parameters are parsed using their expected types. Any additional properties found
	 * in the input that are not explicitly declared in the configuration schema are loaded as
	 * {@code String}-typed values.
	 * <p>
	 * This method is functionally equivalent to calling {@link #loadAllFromMap(Map)} with a
	 * {@code Map<String, String>} created from the {@code Properties} object.
	 *
	 * @param properties the {@code Properties} object to load values from
	 * @return the same {@code ConfigurationValues} instance, for method chaining
	 */
	public ConfigurationValues loadAllFromProperties(Properties properties)
	{
		Map<String, String> propertyMap = new HashMap<>();
		
		for(String name : properties.stringPropertyNames())
		{
			propertyMap.put(name, properties.getProperty(name));
		}
		
		return loadAllFromMap(propertyMap);
	}
	
	/**
	 * Loads configuration values from a {@code Map<String, String>}, using the expected types
	 * declared in required and optional parameters.
	 *
	 * @param values the map of configuration key-value pairs (as strings)
	 * @return the same {@code ConfigurationValues} instance, for method chaining
	 */
	public ConfigurationValues loadFromMap(Map<String, String> values)
	{
		for(TypedParameter parameter : this.requiredParameters)
		{
			this.loadParameterFromMap(parameter, values);
		}
		
		for(TypedParameter parameter : this.optionalParameters)
		{
			this.loadParameterFromMap(parameter, values);
		}
		
		return this;
	}
	
	/**
	 * Loads all configuration parameters from the given map, including both declared
	 * (required and optional) and undeclared parameters.
	 * <p>
	 * For each parameter declared via {@link #requiredParameters} or {@link #optionalParameters},
	 * this method attempts to parse and store the value using the expected type.
	 * <p>
	 * Any additional parameters found in the input map but not declared are treated as
	 * {@code String}-typed values and loaded separately via {@link #loadExtraParameters(Set, Map)}.
	 * <p>
	 * This method allows the configuration to be populated with a complete set of values from
	 * a source like {@code System.getenv()} or a {@code Properties} object, even if some keys
	 * are not explicitly defined in the schema.
	 *
	 * @param values the map of configuration values to load
	 * @return the same {@code ConfigurationValues} instance, for method chaining
	 */
	public ConfigurationValues loadAllFromMap(Map<String, String> values)
	{
		Set<String> officialParameters = new HashSet<>();
		
		for(TypedParameter parameter : this.requiredParameters)
		{
			officialParameters.add(parameter.getName());
			this.loadParameterFromMap(parameter, values);
		}
		
		for(TypedParameter parameter : this.optionalParameters)
		{
			officialParameters.add(parameter.getName());
			this.loadParameterFromMap(parameter, values);
		}
		
		this.loadExtraParameters(officialParameters, values);
		
		return this;
	}
	
	/**
	 * Loads configuration parameters from the given map that were not previously declared
	 * as required or optional parameters.
	 * <p>
	 * These additional parameters are treated as {@code String}-typed values and are validated
	 * using {@link #checkValidName(String)} before being stored. This method is typically used
	 * to allow the configuration to include arbitrary or undocumented values beyond the known schema.
	 * <p>
	 * Successfully loaded variables are logged at {@code DEBUG} level. Invalid parameter names
	 * or failed insertions are logged at {@code WARNING} level.
	 *
	 * @param officialParameters the set of parameter names that are already declared as required or optional
	 * @param values the full map of available configuration values (e.g., from environment or property file)
	 */
	private void loadExtraParameters(Set<String> officialParameters, Map<String, String> values)
	{
		Logger logger = LoggerManager.getLogger(Values.Constants.UTILITIES_CONTEXT);
		
		for(Map.Entry<String, String> entry : values.entrySet())
		{
			String name = entry.getKey();
			String value = entry.getValue();
			
			if(officialParameters.contains(name))
			{
				continue;
			}
			
			try
			{
				this.checkValidName(name);
				
				TypedValue<?> typedValue = createTypedValue(String.class, value);
				
				this.parameters.put(name, typedValue);
				
				String debugMessage = MessageUtil.getMessage(Messages.VARIABLE_LOADED, name, value, String.class.getName());
				
				logger.debug(debugMessage);
			}
			catch (IllegalArgumentException e)
			{
				String errorMessage = MessageUtil.getMessage(Messages.VARIABLE_NOT_LOADED_ERROR, name, String.class.getName());
				
				logger.warning(errorMessage, e);
			}
		}
	}
	
	/**
	 * Attempts to load and parse a parameter from a provided map of key-value pairs.
	 * <p>
	 * If the given parameter's name exists as a key in the source map, its value is parsed
	 * into the parameter's expected type using {@link Converter#parseStringValue(Class, String)}.
	 * If successful, the parameter is added to the configuration.
	 * <p>
	 * Any parse errors or invalid values are logged but do not interrupt the process.
	 *
	 * @param parameter the typed parameter definition to load
	 * @param source the map of raw values (typically from environment variables or properties)
	 */
	private void loadParameterFromMap(TypedParameter parameter, Map<String, String> source)
	{
		String name = parameter.getName();
		Class<?> type = parameter.getType();
		
		String rawValue = source.get(name);
		
		if(rawValue != null)
		{
			Logger logger = LoggerManager.getLogger(Values.Constants.UTILITIES_CONTEXT);
			
			try
			{
				Object parsedValue = Converter.parseStringValue(type, rawValue);
				
				TypedValue<?> typedValue = createTypedValue(type, parsedValue);
				
				this.checkValidName(name);
				
				this.parameters.put(name, typedValue);
				
				String debugMessage = MessageUtil.getMessage(Messages.VARIABLE_LOADED, name, rawValue, type.getName());
				
				logger.debug(debugMessage);
			}
			catch(IllegalArgumentException e)
			{
				String errorMessage = MessageUtil.getMessage(Messages.VARIABLE_NOT_LOADED_ERROR, name, type.getName());
				
				logger.warning(errorMessage, e);
				
				return;
			}
		}
	}
	
	/**
	 * Creates a {@link TypedValue} instance from a given type and object.
	 * <p>
	 * The value is cast to the specified type and wrapped inside a {@code TypedValue}.
	 * This method assumes the cast is valid; callers are responsible for ensuring type safety.
	 *
	 * @param <T> the generic type of the parameter
	 * @param type the class representing the expected type
	 * @param value the value to cast and wrap
	 * @return a {@code TypedValue} instance holding the typed value
	 * @throws ClassCastException if the cast is invalid
	 */
	private static <T> TypedValue<T> createTypedValue(Class<T> type, Object value)
	{
		@SuppressWarnings("unchecked")
		T castValue = (T)value;
		
		return new TypedValue<>(type, castValue);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
		{
			return true;
		}
		
		if(obj == null || this.getClass() != obj.getClass())
		{
			return false;
		}
		
		ConfigurationValues other = (ConfigurationValues) obj;
		
		return Objects.equals(this.parameters, other.parameters) &&
		       Objects.equals(this.requiredParameters, other.requiredParameters) &&
		       Objects.equals(this.optionalParameters, other.optionalParameters);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.parameters, this.requiredParameters, this.optionalParameters);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		Map<String, TypedValue<?>> parameters = this.getParameters();
		
		for(Map.Entry<String, TypedValue<?>> entry : parameters.entrySet())
		{
			TypedValue<?> typedValue = entry.getValue();
			
			String name = entry.getKey();
			Class<?> type = typedValue.getType();
			Object value = typedValue.getValue();
			
			sb.append(" - ");
			sb.append(Utilities.coalesce(type.getCanonicalName(), type.getName()));
			sb.append(" ");
			sb.append(name);
			sb.append(" = ");
			sb.append(value == null ? Values.Constants.NULL_VALUE_STRING : value);
			sb.append("\n");
		}
		
		return sb.toString();
	}
}