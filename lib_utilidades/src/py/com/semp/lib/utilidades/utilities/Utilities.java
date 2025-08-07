package py.com.semp.lib.utilidades.utilities;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;

/**
 * Class with static methods with utilities.
 * 
 * @author Sergio Morel
 */
public final class Utilities
{
	private Utilities()
	{
		super();
		
		String errorMessage = MessageUtil.getMessage(Messages.DONT_INSTANTIATE, this.getClass().getName());
		
		throw new AssertionError(errorMessage);
	}
	
	/**
	 * Returns the first non-{@code null} value from the given arguments.
	 * <p>
	 * This method mimics the behavior of SQL's {@code COALESCE(...)} function.
	 * It evaluates the provided values in order and returns the first one that is not {@code null}.
	 * If all values are {@code null}, or if the array itself is {@code null}, it returns {@code null}.
	 * </p>
	 *
	 * <pre>{@code
	 * String value = coalesce(null, null, "default", "ignored");
	 * // value == "default"
	 * }</pre>
	 *
	 * @param <T> the type of the values
	 * @param values the values to evaluate
	 * @return the first non-{@code null} value, or {@code null} if none is found
	 */
	@SafeVarargs
	public static <T> T coalesce(T... values)
	{
		if(values == null) return null;
		
		for(T value : values)
		{
			if(value != null) return value;
		}
		
		return null;
	}
	
	/**
	 * Verifies if the object is present in the collection. It uses Utilities::equals to compare
	 * the elements, which does a deeper comparison than than Object::equals. This method also
	 * avoids NullPointerException in case the collection is <b>null</b>.
	 * 
	 * @param collection
	 * - Collection from which to find the object.
	 * @param object
	 * - Object to be found.
	 * @return
	 * - <b>true</b> if the object is found.<br>
	 * - <b>false</b> if the object is not found, or if the collection is null.
	 * @author Sergio Morel
	 */
	public static boolean collectionContains(Collection<?> collection, Object object)
	{
		if(collection == null)
		{
			return false;
		}
		
		for(Object element : collection)
		{
			if(Utilities.equals(element, object))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Compares two objects for equality, performing a deep comparison when necessary.<br>
	 * - For arrays, performs a deep comparison of their contents.<br>
	 * - For Iterables, compares elements in order.<br>
	 * - For other objects, uses their own equals method.<br>
	 * 
	 * @param object1 first object to check.
	 * @param object2 second object to check.
	 * @return
	 * - <b>true</b> if the objects are equal.<br>
	 * - <b>false</b> otherwise.
	 * @author Sergio Morel
	 */
	public static boolean equals(Object object1, Object object2)
	{
		if(object1 == object2)
		{
			return true;
		}
		
		if(object1 == null || object2 == null)
		{
			return false;
		}
		
		Class<?> type1 = object1.getClass();
		Class<?> type2 = object2.getClass();
		
		if(type1.isArray() && type2.isArray())
		{
			if(!type1.equals(type2))
			{
				return false;
			}
			
			if(type1.getComponentType().isPrimitive())
			{
				if(byte[].class.isAssignableFrom(type1))
				{
					return Arrays.equals((byte[]) object1, (byte[]) object2);
				}
				else if(int[].class.isAssignableFrom(type1))
				{
					return Arrays.equals((int[]) object1, (int[]) object2);
				}
				else if(short[].class.isAssignableFrom(type1))
				{
					return Arrays.equals((short[]) object1, (short[]) object2);
				}
				else if(long[].class.isAssignableFrom(type1))
				{
					return Arrays.equals((long[]) object1, (long[]) object2);
				}
				else if(float[].class.isAssignableFrom(type1))
				{
					return Arrays.equals((float[]) object1, (float[]) object2);
				}
				else if(double[].class.isAssignableFrom(type1))
				{
					return Arrays.equals((double[]) object1, (double[]) object2);
				}
				else if(char[].class.isAssignableFrom(type1))
				{
					return Arrays.equals((char[]) object1, (char[]) object2);
				}
				else if(boolean[].class.isAssignableFrom(type1))
				{
					return Arrays.equals((boolean[]) object1, (boolean[]) object2);
				}
			}
			
			return Arrays.deepEquals((Object[])object1, (Object[])object2);
		}
		else if(Iterable.class.isAssignableFrom(type1) && Iterable.class.isAssignableFrom(type2))
		{
			return Utilities.equals((Iterable<?>) object1, (Iterable<?>) object2);
		}
		
		return object1.equals(object2);
	}
	
	/**
	 * Compares if two iterables have equal elements. It uses Utilities::equal to compare
	 * the elements.
	 * @param iterable1
	 * - first iterable
	 * @param iterable2
	 * - second iterable
	 * @return
	 * - <b>true</b> if the iterables are equal.<br>
	 * - <b>false</b> if the iterables are not equal.
	 * @author Sergio Morel
	 */
	public static boolean equals(Iterable<?> iterable1, Iterable<?> iterable2)
	{
		if(iterable1 == iterable2)
		{
			return true;
		}
		
		if(iterable1 == null || iterable2 == null)
		{
			return false;
		}
		
		Iterator<?> iterator1 = iterable1.iterator();
		Iterator<?> iterator2 = iterable2.iterator();
		
		boolean hasNext1 = iterator1.hasNext();
		boolean hasNext2 = iterator2.hasNext();
		
		if(hasNext1 != hasNext2)
		{
			return false;
		}
		
		while(hasNext1)
		{
			Object element1 = iterator1.next();
			Object element2 = iterator2.next();
			
			if(!Utilities.equals(element1, element2))
			{
				return false;
			}
			
			hasNext1 = iterator1.hasNext();
			hasNext2 = iterator2.hasNext();
			
			if(hasNext1 != hasNext2)
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Finds and returns the maximum value among a variable number of {@code Long} values.
	 * This method is null-safe, meaning it can handle {@code null} as an input array or
	 * contain {@code null} elements within the array. If the input array is {@code null} or
	 * contains only {@code null} elements, this method returns {@code null}.
	 * 
	 * @param values A variable number of {@code Long} values or a {@code Long[]} array. 
	 *               It can be {@code null} or contain {@code null} elements.
	 * @return The maximum {@code Long} value among the provided values. If the input is 
	 *         {@code null} or all elements are {@code null}, returns {@code null}.
	 *         If there is at least one non-null element, returns the maximum value found
	 *         ignoring any {@code null} elements.
	 * 
	 * @example Long maxValue = maxValue(1L, 2L, null, 4L); // returns 4L
	 * @example Long maxValue = maxValue(null, null); // returns null
	 * @example Long maxValue = maxValue(); // returns null
	 */
	public static Long maxValue(Long... values)
	{
		if(values == null)
		{
			return null;
		}
		
		Long max = null;
		
		for(Long value : values)
		{
			if(value == null)
			{
				continue;
			}
			
			if(max == null || value > max)
			{
				max = value;
			}
		}
		
		return max;
	}
	
	public static String toString(Instant instant)
	{
		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
		
		return zonedDateTime.toString();
	}
}
