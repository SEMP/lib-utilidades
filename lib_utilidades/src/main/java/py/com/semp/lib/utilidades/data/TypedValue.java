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
	 * Constructs a {@code TypedValue} with the specified type and value.
	 * <p>
	 * This constructor accepts {@code null} values, but the {@code type} must be non-null.
	 *
	 * @param type the runtime type of the value.
	 * @param value the value to wrap, which may be {@code null}.
	 * @throws NullPointerException if {@code type} is {@code null}.
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
	 * Constructs a {@code TypedValue} by inferring the type from a non-null value.
	 *
	 * @param value the non-null value.
	 * @throws NullPointerException if {@code value} is {@code null}.
	 */
	public TypedValue(T value)
	{
		this(resolveClass(value), value);
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
	 * Resolves the runtime class of a given non-null value.
	 *
	 * @param <T> the type of the value.
	 * @param value the non-null value.
	 * @return the class of the value.
	 * @throws NullPointerException if the value is {@code null}.
	 */
	private static <T> Class<T> resolveClass(T value)
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