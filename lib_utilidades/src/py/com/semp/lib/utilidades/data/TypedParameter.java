package py.com.semp.lib.utilidades.data;

import java.util.Objects;

import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;

/**
 * Class to define a parameter. Includes the type and name of the parameter.
 * 
 * @author Sergio Morel
 */
public class TypedParameter
{
	private final Class<?> type;
	private final String name;
	
	/**
     * Constructs a {@link TypedParameter} with the specified name and type.
     * This constructor does not accept null types or names.
     * 
     * @param type type of the parameter.
	 * @param name name of the parameter.
     * @throws NullPointerException the type is null.
     * @throws IllegalArgumentException if the name is invalid.
     */
	public TypedParameter(Class<?> type, String name)
	{
		if(type == null)
		{
			String errorMessage = MessageUtil.getMessage(Messages.TYPE_NOT_DEFINED_ERROR);
			
			throw new NullPointerException(errorMessage);
		}
		
		if(name == null || name.trim().isEmpty())
		{
			String errorMessage = MessageUtil.getMessage(Messages.INVALID_NAME_ERROR, name);
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.type = type;
		this.name = name;
	}
	
	public Class<?> getType()
	{
		return this.type;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
		{
			return true;
		}
		if(obj == null || !obj.getClass().equals(this.getClass()))
		{
			return false;
		}
		
		TypedParameter typedParameter = (TypedParameter)obj;
		
		return Objects.equals(this.type, typedParameter.type) && Objects.equals(this.name, typedParameter.name);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.type, this.name);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.type.getSimpleName()).append(" ");
		sb.append(this.name);
		
		return sb.toString();
	}
}
