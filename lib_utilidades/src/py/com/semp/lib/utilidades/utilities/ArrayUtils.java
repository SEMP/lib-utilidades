package py.com.semp.lib.utilidades.utilities;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class with static method with utilities for arrays.
 * 
 * @author Sergio Morel
 */
public final class ArrayUtils
{
	private ArrayUtils()
	{
		super();
	}
	
	/**
	 * Concatenates the content of all the arrays received as parameter.
	 * 
	 * @param arrays
	 * - arrays to concatenate.
	 * @return
	 * - a new array with elements concatenated from the parameter arrays.
	 */
	public static byte[] join(byte[]... arrays)
	{
		if(arrays == null)
		{
			return null;
		}
		
		int unionLength = 0;
		
		for(byte[] array : arrays)
		{
			if(array == null)
			{
				continue;
			}
			
			unionLength += array.length;
		}
		
		byte[] union = new byte[unionLength];
		
		int unionIndex = 0;
		
		for(byte[] array : arrays)
		{
			if(array == null)
			{
				continue;
			}
			
			for(int i = 0; i < array.length; i++)
			{
				union[unionIndex++] = array[i];
			}
		}
		
		return union;
	}
	
	/**
	 * Concatenates the content of all the arrays received as parameter.
	 * 
	 * @param <T>
	 * - type of the array's element.
	 * @param arrays
	 * - arrays to concatenate.
	 * @return
	 * - a new array with elements concatenated from the parameter arrays.
	 */
	@SafeVarargs
	public static <T> T[] join(T[]... arrays)
	{
		if(arrays == null)
		{
			return null;
		}
		
		int unionLength = 0;
		
		for(T[] array : arrays)
		{
			if(array == null)
			{
				continue;
			}
			
			unionLength += array.length;
		}
		
		Class<?> componentType = arrays.getClass().getComponentType().getComponentType();
		
		@SuppressWarnings("unchecked")
		T[] union = (T[])Array.newInstance(componentType, unionLength);
		
		int unionIndex = 0;
		
		for(T[] array : arrays)
		{
			if(array == null)
			{
				continue;
			}
			
			for(int i = 0; i < array.length; i++)
			{
				union[unionIndex++] = array[i];
			}
		}
		
		return union;
	}
	
	/**
	 * Returns an array with only the elements every array has in common.
	 * 
	 * @param arrays - arrays to check if they have elements in common.
	 * @return
	 * - array with the elements in common.<br>
	 * - an empty array if any of the input arrays is null.<br>
	 * - <b>null</b> if the parameter received is null.
	 * @author Sergio Morel
	 */
	public static byte[] intersect(byte[]... arrays)
	{
		if(arrays == null)
		{
			return null;
		}
		
		if(arrays.length == 0)
		{
			return new byte[]{};
		}
		
		for(byte[] array : arrays)
		{
			if(array == null)
			{
				return new byte[]{};
			}
		}
		
		Set<Byte> commonElements = ArrayUtils.toSet(arrays[0]);
		
		for(int i = 1; i < arrays.length; i++)
		{
			commonElements.retainAll(ArrayUtils.toSet(arrays[i]));
			
			if(commonElements.isEmpty())
			{
				break;
			}
		}
		
		byte[] resultArray = new byte[commonElements.size()];
		
		int i = 0;
		
		for(Byte b : commonElements)
		{
			resultArray[i++] = b;
		}
		
		return resultArray;
	}
	
	/**
	 * Returns an array with only the elements every array has in common.
	 * 
	 * @param <T>
	 * - type of the array's element.
	 * @param arrays - arrays to check if they have elements in common.
	 * @return
	 * - array with the elements in common.<br>
	 * - an empty array if any of the input arrays is null.<br>
	 * - <b>null</b> if the parameter received is null.
	 * @author Sergio Morel
	 */
	@SafeVarargs
	public static <T> T[] intersect(T[]... arrays)
	{
		if(arrays == null)
		{
			return null;
		}
		
		Class<?> componentType = arrays.getClass().getComponentType().getComponentType();
		
		@SuppressWarnings("unchecked")
		T[] emptyArray = (T[])Array.newInstance(componentType, 0);
		
		if(arrays.length == 0)
		{
			return emptyArray;
		}
		
		for(T[] array : arrays)
		{
			if(array == null)
			{
				return emptyArray;
			}
		}
		
		Set<T> commonElements = new HashSet<>(List.of(arrays[0]));
		
		for(int i = 1; i < arrays.length; i++)
		{
			commonElements.retainAll(List.of(arrays[i]));
			
			if(commonElements.isEmpty())
			{
				break;
			}
		}
		
		@SuppressWarnings("unchecked")
		T[] resultArray = (T[])Array.newInstance(componentType, commonElements.size());
		
		return commonElements.toArray(resultArray);
	}
	
	/**
	 * Returns a new array containing a sub set of the original array's elements.
	 * If the size exceeds the data from the array, the resulting array might be
	 * smaller that the size received as parameter.
	 * 
	 * @param array
	 * - array from which you want to get a sub set of elements.
	 * @param index
	 * - index from which you want to get the elements.
	 * @param size
	 * - the amount of elements you want to get from the array.
	 * @return
	 * - new array with the sub set of elements.<br>
	 * - <b>null</b> if the array is null.<br>
	 * - empty array if the index is out of bounds.
	 * @author Sergio Morel
	 */
	public static byte[] subArray(byte[] array, int index, int size)
	{
		if(array == null)
		{
			return null;
		}
		
		if(index < 0 || index >= array.length)
		{
			return new byte[]{};
		}
		
		int resultSize = Math.min(size, array.length - index);
		
		byte[] section = new byte[resultSize];
		
		for(int i = index; i < array.length; i++)
		{
			if(i >= index + size)
			{
				break;
			}
			
			section[i - index] = array[i];
		}
		
		return section;
	}
	
	/**
	 * Returns a new array containing a sub set of the original array's elements.
	 * If the size exceeds the data from the array, the resulting array might be
	 * smaller that the size received as parameter.
	 * 
	 * @param <T>
	 * - type of the array's element.
	 * @param array
	 * - array from which you want to get a sub set of elements.
	 * @param index
	 * - index from which you want to get the elements.
	 * @param size
	 * - the amount of elements you want to get from the array.
	 * @return
	 * - new array with the sub set of elements.<br>
	 * - <b>null</b> if the array is null.<br>
	 * - empty array if the index is out of bounds.
	 * @author Sergio Morel
	 */
	public static <T> T[] subArray(T[] array, int index, int size)
	{
		if(array == null)
		{
			return null;
		}
		
		if(index < 0 || index >= array.length)
		{
			@SuppressWarnings("unchecked")
			T[] emptyArray = (T[])Array.newInstance(array.getClass().getComponentType(), 0);
			
			return emptyArray;
		}
		
		int resultSize = Math.min(size, array.length - index);
		
		@SuppressWarnings("unchecked")
		T[] section = (T[])Array.newInstance(array.getClass().getComponentType(), resultSize);
		
		for(int i = index; i < array.length; i++)
		{
			if(i >= index + size)
			{
				break;
			}
			
			section[i - index] = array[i];
		}
		
		return section;
	}
	
	/**
	 * Checks if element is present in the array.
	 * 
	 * @param array
	 * - array to check.
	 * @param element
	 * - element to be found in the array.
	 * @return
	 * - <b>true</b> if the element was found.<br>
	 * - <b>false</b> if the element wasn't found.
	 * @author Sergio Morel
	 */
	public static boolean contains(byte[] array, byte element)
	{
		if(array == null)
		{
			return false;
		}
		
		for(int i = 0; i < array.length; i++)
		{
			if(array[i] == element)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if element is present in the array.
	 * 
	 * @param <T>
	 * - type of the array's element.
	 * @param array
	 * - array to check.
	 * @param element
	 * - element to be found in the array.
	 * @return
	 * - <b>true</b> if the element was found.<br>
	 * - <b>false</b> if the element wasn't found.
	 * @author Sergio Morel
	 */
	public static <T> boolean contains(T[] array, T element)
	{
		if(array == null)
		{
			return false;
		}
		
		for(int i = 0; i < array.length; i++)
		{
			if(Utilities.equals(array[i], element))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Finds the index of the first occurrence of an element in the array.
	 * 
	 * @param array
	 * - array to check.
	 * @param element
	 * - element to be found in the array.
	 * @return
	 * - the index of the found element.<br>
	 * - <b>-1</b> if the element wasn't found.
	 * @author Sergio Morel
	 */
	public static int findFirst(byte[] array, byte element)
	{
		if(array == null)
		{
			return -1;
		}
		
		for(int i = 0; i < array.length; i++)
		{
			if(array[i] == element)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Finds the index of the first occurrence of an element in the array.
	 * 
	 * @param <T>
	 * - type of the array's element.
	 * @param array
	 * - array to check.
	 * @param element
	 * - element to be found in the array.
	 * @return
	 * - the index of the found element.<br>
	 * - <b>-1</b> if the element wasn't found.
	 * @author Sergio Morel
	 */
	public static <T> int findFirst(T[] array, T element)
	{
		if(array == null)
		{
			return -1;
		}
		
		for(int i = 0; i < array.length; i++)
		{
			if(Utilities.equals(array[i], element))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Creates a {@link Set} using the elements of the array.
	 * 
	 * @param array
	 * - array from which you want to build a {@link Set} array.<br>
	 * - <b>null</b> if the array is null.
	 * @author Sergio Morel
	 */
	public static Set<Byte> toSet(byte[] array)
	{
		if(array == null)
		{
			return null;
		}
		
		Set<Byte> set = new HashSet<>();
		
		for(byte b : array)
		{
			set.add(b);
		}
		
		return set;
	}
	
	/**
	 * Creates a {@link String} from the byte array. Each element is represented
	 * as an hexadecimal number.
	 * @param bytes
	 * - bytes to be converted to the corresponding string.
	 * @return
	 * - {@link String} that represents the byte array with each element in hexadecimal.<br>
	 * - <b>null</b> if the parameter is null.
	 * @author Sergio Morel
	 */
	public static String toHexaArrayString(byte... bytes)
	{
		if(bytes == null)
		{
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		
		boolean includeSeparator = false;
		
		String separator = ", ";
		
		for(byte b : bytes)
		{
			if(includeSeparator)
			{
				sb.append(separator);
			}
			
			sb.append(String.format("0x%02X", b));
			
			includeSeparator = true;
		}
		
		sb.append("]");
		
		return sb.toString();
	}
}
