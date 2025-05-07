package py.com.semp.lib.utilidades.configuration;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import py.com.semp.lib.utilidades.data.TypedParameter;
import py.com.semp.lib.utilidades.data.TypedValue;
import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;

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
			sb.append(type.getCanonicalName());
			sb.append(" ");
			sb.append(name);
			sb.append(" = ");
			sb.append(value == null ? Values.Constants.NULL_VALUE_STRING : value);
			sb.append("\n");
		}
		
		return sb.toString();
	}
}