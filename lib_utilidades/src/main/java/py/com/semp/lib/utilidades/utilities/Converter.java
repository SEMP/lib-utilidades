package py.com.semp.lib.utilidades.utilities;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;

/**
 * Utility class for converting {@link String} values to typed objects.
 * <p>
 * This class supports conversion to a wide variety of types including primitive wrappers,
 * characters, date/time types (Java 8+), and enums. If the value cannot be parsed to the 
 * specified type, a detailed {@link IllegalArgumentException} is thrown.
 *
 * <h3>Supported types:</h3>
 * <ul>
 *   <li>{@link String}</li>
 *   <li>{@link Integer}, {@code int}</li>
 *   <li>{@link Boolean}, {@code boolean}</li>
 *   <li>{@link Long}, {@code long}</li>
 *   <li>{@link Double}, {@code double}</li>
 *   <li>{@link Float}, {@code float}</li>
 *   <li>{@link Short}, {@code short}</li>
 *   <li>{@link Byte}, {@code byte}</li>
 *   <li>{@link Character}, {@code char}</li>
 *   <li>{@link java.time.LocalDate}</li>
 *   <li>{@link java.time.LocalDateTime}</li>
 *   <li>{@link java.time.OffsetDateTime}</li>
 *   <li>{@link java.time.Instant}</li>
 *   <li>{@link java.util.Date} (parsed from ISO-8601)</li>
 *   <li>{@link Enum} types</li>
 * </ul>
 *
 * @param <T> the target type
 * @throws IllegalArgumentException if the value cannot be parsed to the specified type
 */
public final class Converter
{
	private Converter()
	{
		super();
		
		String errorMessage = MessageUtil.getMessage(Messages.DONT_INSTANTIATE, this.getClass().getName());
		
		throw new AssertionError(errorMessage);
	}
	
	/**
	 * Parses a {@link String} value into an instance of the specified type.
	 *
	 * @param type  the expected target type
	 * @param value the string value to parse
	 * @return the parsed object
	 * @param <T>   the type to return
	 * @throws IllegalArgumentException if the string cannot be parsed or the type is unsupported
	 */
	public static <T> T parseStringValue(Class<T> type, String value)
	{
		if(type == String.class) return type.cast(value);
		if(type == Integer.class || type == int.class) return type.cast(Integer.valueOf(value));
		if(type == Boolean.class || type == boolean.class) return type.cast(Boolean.valueOf(value));
		if(type == Long.class || type == long.class) return type.cast(Long.valueOf(value));
		if(type == Double.class || type == double.class) return type.cast(Double.valueOf(value));
		if(type == Float.class || type == float.class) return type.cast(Float.valueOf(value));
		if(type == Short.class || type == short.class) return type.cast(Short.valueOf(value));
		if(type == Byte.class || type == byte.class) return type.cast(Byte.valueOf(value));
		if(type == Character.class || type == char.class)
		{
			if(value.length() != 1)
			{
				String errorMessage = MessageUtil.getMessage(Messages.VALUE_PARSE_ERROR, value, type.getName());
				
				throw new IllegalArgumentException(errorMessage);
			}
			
			return type.cast(Character.valueOf(value.charAt(0)));
		}
		if(type == LocalDate.class) return type.cast(LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE));
		if(type == LocalDateTime.class) return type.cast(LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
		if(type == OffsetDateTime.class) return type.cast(OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		if(type == Instant.class) return type.cast(Instant.parse(value));
		if(type == Date.class) return type.cast(Date.from(Instant.parse(value))); // assumes ISO-8601
		if(type.isEnum())
		{
			try
			{
				@SuppressWarnings({"rawtypes", "unchecked"})
				Object enumValue = Enum.valueOf((Class<Enum>)type.asSubclass(Enum.class), value);
				
				return type.cast(enumValue);
			}
			catch(IllegalArgumentException e)
			{
				String errorMessage = MessageUtil.getMessage(Messages.ENUM_DOES_NOT_EXIST_ERROR, value, type.getName());
				
				throw new IllegalArgumentException(errorMessage, e);
			}
		}
		
		String errorMessage = MessageUtil.getMessage(Messages.VALUE_PARSE_ERROR, value, type.getName());
		
		throw new IllegalArgumentException(errorMessage);
	}
}
