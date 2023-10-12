package py.com.semp.lib.utilidades.utilities;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import py.com.semp.lib.utilidades.data.CircularByteBuffer;

class UtilitiesTest
{
	@Test
	void testUtilitiesConstructor()
	{
		assertThrows(InvocationTargetException.class, () ->
		{
			Constructor<Utilities> constructor = Utilities.class.getDeclaredConstructor();
			constructor.setAccessible(true);
			try
			{
				constructor.newInstance();
			}
			catch(InvocationTargetException ite)
			{
				Throwable cause = ite.getCause();
				assertTrue(cause instanceof AssertionError);
				throw ite;
			}
		});
	}
	
	@Test
	void testCollectionContains()
	{
		// Test with a null collection
		assertFalse(Utilities.collectionContains(null, "a"));
		
		// Test with non-null collections and various types of objects
		List<String> stringList = Arrays.asList("a", "b", "c");
		assertTrue(Utilities.collectionContains(stringList, "a"));
		assertFalse(Utilities.collectionContains(stringList, "d"));
		assertTrue(Utilities.collectionContains(stringList, "c"));
		assertFalse(Utilities.collectionContains(stringList, null));
		
		List<Integer> intList = Arrays.asList(1, 2, 3);
		assertTrue(Utilities.collectionContains(intList, 1));
		assertFalse(Utilities.collectionContains(intList, 4));
		
		// Test with an object that isn't present in the collection but is similar to existing ones
		String a = new String("a");
		assertTrue(Utilities.collectionContains(stringList, a));
		
		// Test with null in the collection
		List<String> stringListWithNull = Arrays.asList("a", null, "c");
		assertTrue(Utilities.collectionContains(stringListWithNull, null));
		
		// Test with custom objects
		List<Object[]> objectArrayList = Arrays.asList(new Object[]{1, "a"}, new Object[]{2, "b"});
		
		assertTrue(Utilities.collectionContains(objectArrayList, new Object[]{1, "a"}));
		assertFalse(Utilities.collectionContains(objectArrayList, new Object[]{1, "c"}));
	}
	
	@Test
	void comparesUsingEquals()
	{
		// Test for null values
		assertTrue(Utilities.equals(null, null));
		assertFalse(Utilities.equals(null, "string"));
		assertFalse(Utilities.equals("string", null));
		
		// Test for primitive arrays
		assertTrue(Utilities.equals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));
		assertFalse(Utilities.equals(new int[]{1, 2, 3}, new int[]{1, 2, 4}));
		assertFalse(Utilities.equals(new long[]{1, 2, 3}, new int[]{1, 2, 3}));
		
		assertTrue(Utilities.equals(new boolean[]{true, false, false}, new boolean[]{true, false, false}));
		assertTrue(Utilities.equals(new byte[]{1, 2, 3}, new byte[]{1, 2, 3}));
		assertTrue(Utilities.equals(new short[]{1, 2, 3}, new short[]{1, 2, 3}));
		assertTrue(Utilities.equals(new char[]{1, 2, 3}, new char[]{1, 2, 3}));
		assertTrue(Utilities.equals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));
		assertTrue(Utilities.equals(new long[]{1, 2, 3}, new long[]{1, 2, 3}));
		assertTrue(Utilities.equals(new float[]{1, 2, 3}, new float[]{1, 2, 3}));
		assertTrue(Utilities.equals(new double[]{1, 2, 3}, new double[]{1, 2, 3}));
		
		assertTrue(Utilities.equals(new Object[]{1, 2, 3}, new Object[]{1, 2, 3}));
		
		// Test for Object arrays
		String[] arr1 = {"a", null, "c"};
		String[] arr2 = {"a", null, "c"};
		String[] arr3 = {"a", null, "d"};
		
		assertTrue(Utilities.equals(arr1, arr2));
		assertFalse(Utilities.equals(arr1, arr3));
		assertFalse(Utilities.equals(arr1, "abc"));
		
		// Test for Lists (as Iterable)
		List<String> list1 = Arrays.asList("a", null, "c");
		List<String> list2 = Arrays.asList("a", null, "c");
		List<String> list3 = Arrays.asList("a", null, "d");
		assertTrue(Utilities.equals(list1, list2));
		assertFalse(Utilities.equals(list1, list3));
		
		// Distinct arrays with null
		assertFalse(Utilities.equals(new String[]{null, "b", "c"}, new String[]{"a", "b", "c"}));
		assertFalse(Utilities.equals(Arrays.asList(null, "b", "c"), Arrays.asList("a", "b", "c")));

		// Multidimensional arrays
		Integer[][] multiArray1 = {{1, 2}, {3, 4}};
		Integer[][] multiArray2 = {{1, 2}, {3, 4}};
		Integer[][] multiArray3 = {{1, 2}, {3, null}};
		assertTrue(Utilities.equals(multiArray1, multiArray2));
		assertFalse(Utilities.equals(multiArray1, multiArray3));
		
		// Test for mixed types
		assertFalse(Utilities.equals(list1, arr1));
		
		// Test for other objects
		assertTrue(Utilities.equals("string", "string"));
		assertFalse(Utilities.equals("string1", "string2"));
		
		List<Byte> byteList1 = Arrays.asList((byte)0, (byte)1, (byte)2, (byte)3, (byte)4);
		List<Byte> byteList2 = new CircularByteBuffer(new byte[] {0, 1, 2, 3, 4});
		assertTrue(Utilities.equals(byteList1, byteList2));
		
		assertFalse(Utilities.equals(byteList2, new byte[] {0, 1, 2, 3, 4}));
		assertFalse(Utilities.equals(new byte[] {0, 1, 2, 3, 4}, byteList2));
		assertTrue(Utilities.equals(byteList2, byteList2));
		assertTrue(Utilities.equals((Object)byteList2, (Object)new CircularByteBuffer(new byte[] {0, 1, 2, 3, 4})));
	}
	
	@Test
	void testEqualsForIterables()
	{
		// Test with same lists
		List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
		List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5);
		assertTrue(Utilities.equals(list1, list2));
		
		// Test with different lists
		list1 = Arrays.asList(1, 2, 3, 4, 5);
		list2 = Arrays.asList(1, 2, 3, 4, 6);
		assertFalse(Utilities.equals(list1, list2));
		
		// Test with one list being longer than the other
		list1 = Arrays.asList(1, 2, 3, 4, 5);
		list2 = Arrays.asList(1, 2, 3, 4);
		assertFalse(Utilities.equals(list1, list2));
		
		// Test with null iterables
		list1 = null;
		list2 = Arrays.asList(1, 2, 3, 4, 5);
		assertFalse(Utilities.equals(list1, list2));
		
		// Test with both iterables being null
		list1 = null;
		list2 = null;
		assertTrue(Utilities.equals(list1, list2));
		
		// Test with other Iterable types (e.g., Set)
		Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
		Set<Integer> set2 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
		assertTrue(Utilities.equals(set1, set2));
		
		set1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
		set2 = new HashSet<>(Arrays.asList(1, 2, 3, 4));
		assertFalse(Utilities.equals(set1, set2));
		
		List<Byte> byteList1 = Arrays.asList((byte)0, (byte)1, (byte)2, (byte)3, (byte)4);
		List<Byte> byteList2 = new CircularByteBuffer(new byte[] {0, 1, 2, 3, 4});
		assertTrue(Utilities.equals(byteList1, byteList2));
		
		assertFalse(Utilities.equals(null, byteList2));
		assertFalse(Utilities.equals(byteList1, null));
		
		List<Byte> byteList3 = new ArrayList<>();
		
		byteList3.addAll(byteList1);
		
		assertTrue(Utilities.equals(byteList1, byteList3));
		
		byteList3.add((byte)5);
		
		assertFalse(Utilities.equals(byteList1, byteList3));
		
		byteList2.clear();
		
		assertFalse(Utilities.equals(byteList1, byteList2));
	}
}