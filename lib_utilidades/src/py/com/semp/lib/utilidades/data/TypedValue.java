package py.com.semp.lib.utilidades.data;

import java.util.Objects;

import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;

/**
 * Class to store a value with its type.
 * 
 * @author Sergio Morel
 *
 * @param <T>
 * Data type of the value.
 */
public class TypedValue<T>
{
	private T value;
	private Class<T> type;
	
	/**
     * Constructs a {@link TypedValue} with the specified value and type.
     * This constructor does not accept null types.
     * 
     * @param type type of the value to be set.
	 * @param value the value to be set.
     * @throws NullPointerException if value is null.
     */
	public TypedValue(Class<T> type, T value)
	{
		super();
		
		if(type == null)
		{
			String errorMessage = MessageUtil.getMessage(Messages.TYPE_NOT_DEFINED_ERROR);
			
			throw new NullPointerException(errorMessage);
		}
		
		this.value = value;
		this.type = type;
	}
	
	/**
     * Constructs a TypedValue with the specified value.
     * This constructor does not accept null values.
     * 
     * @param value the value to be set.
     * @throws NullPointerException if value is null.
     */
	public TypedValue(T value)
	{
		this(getClass(value), value);
	}
	
	public T getValue()
	{
		return this.value;
	}
	
	public Class<T> getType()
	{
		return this.type;
	}
	
	/**
	 * Obtains the value's type.
	 * 
	 * @param <T> Value's type.
	 * @param value the value.
	 * @return the value's type.
	 */
	private static <T> Class<T> getClass(T value)
	{
		if(value == null)
		{
			String errorMessage = MessageUtil.getMessage(Messages.TYPE_OF_NULL_VALUE_ERROR);
			
			throw new NullPointerException(errorMessage);
		}
		
		@SuppressWarnings("unchecked")
		Class<T> type = (Class<T>)value.getClass();
		
		return type;
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
		
		TypedValue<?> typedValue = (TypedValue<?>)obj;
		
		return Objects.equals(this.type, typedValue.type) && Objects.equals(this.value, typedValue.value);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.type, this.value);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.type.getSimpleName()).append(" ");
		sb.append(this.value);
		
		return sb.toString();
	}
}