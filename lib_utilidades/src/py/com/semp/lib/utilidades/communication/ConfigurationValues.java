package py.com.semp.lib.utilidades.communication;

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
	private static final String NULL_VALUE = "null";
	
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
		
		synchronized(this.parameters)
		{
			this.parameters.put(name, typedValue);
		}
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
		
		synchronized(this.parameters)
		{
			typedValue = this.parameters.get(name);
		}
		
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
		synchronized(this.requiredParameters)
		{
			this.requiredParameters.add(typedParameter);
		}
		
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
		synchronized(this.optionalParameters)
		{
			this.optionalParameters.add(typedParameter);
		}
		
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
		
		synchronized(parameters)
		{
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
				sb.append(value == null ? NULL_VALUE : value);
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}
}