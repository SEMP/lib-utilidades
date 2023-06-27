package py.com.semp.lib.utilidades.utilities;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class ArrayUtils
{
	private ArrayUtils()
	{
		super();
	}
	
	/**
	 * Crea un arreglo nuevo con el contenido de los arreglos pasados por parametro.
	 * Los valores son insertados en el nuevo arreglo union de acuerdo al orden de los
	 * arreglos del parametro <b>arrays</b>.
	 * @param arrays - conjunto de arrays
	 * @return
	 * - nuevo arreglo correspondiente a la union de los arreglos de <b>arrays</b>.
	 */
	public static byte[] join(byte[]... arrays)
	{
		int unionLength = 0;
		
		for(byte[] array : arrays)
		{
			unionLength += array.length;
		}
		
		byte[] union = new byte[unionLength];
		
		int unionIndex = 0;
		
		for(byte[] array : arrays)
		{
			for(int i = 0; i < array.length; i++)
			{
				union[unionIndex++] = array[i];
			}
		}
		
		return union;
	}
	
	/**
	 * Crea un arreglo nuevo con el contenido de los arreglos pasados por parametro.
	 * Los valores son insertados en el nuevo arreglo union de acuerdo al orden de los
	 * arreglos del parametro <b>arrays</b>.
	 * @param arrays - conjunto de arrays
	 * @return
	 * - nuevo arreglo correspondiente a la union de los arreglos de <b>arrays</b>.
	 */
	@SafeVarargs
	public static <E> E[] join(E[]... arrays)
	{
		int unionLength = 0;
		
		for(E[] array : arrays)
		{
			unionLength += array.length;
		}
		
		@SuppressWarnings("unchecked")
		E[] union = (E[])Array.newInstance(arrays.getClass().getComponentType().getComponentType(), unionLength);
		
		int unionIndex = 0;
		
		for(E[] array : arrays)
		{
			for(int i = 0; i < array.length; i++)
			{
				union[unionIndex++] = array[i];
			}
		}
		
		return union;
	}
	
	/**
	 * Obtiene un array con los elementos en comun de los 2 arrays
	 * pasados como par&aacute;metro.
	 * @param array1
	 * primer array.
	 * @param array2
	 * segundo array.
	 * @return
	 * array con elementos en comun de los arrays pasados como par&aacute;metro.
	 */
	public static <E> E[] intersect(E[] array1, E[] array2)
	{
		List<E> result = new LinkedList<>();
		Set<E> hashSet = new HashSet<>();
		
		hashSet.addAll(Arrays.asList(array2));
		
		for(E element : array1)
		{
			if(hashSet.isEmpty())
			{
				break;
			}
			
			if(hashSet.contains(element))
			{
				result.add(element);
				hashSet.remove(element);
			}
		}
		
		@SuppressWarnings("unchecked")
		E[] resultArray = (E[])Array.newInstance(array1.getClass().getComponentType(), result.size());
		
		return result.toArray(resultArray);
	}
}
