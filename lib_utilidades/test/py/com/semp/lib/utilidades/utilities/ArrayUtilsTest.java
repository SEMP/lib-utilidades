package py.com.semp.lib.utilidades.utilities;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

class ArrayUtilsTest
{
	@Test
	void testArrayUtilsConstructor()
	{
		assertThrows(InvocationTargetException.class, () ->
		{
			Constructor<ArrayUtils> constructor = ArrayUtils.class.getDeclaredConstructor();
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
	void joinByteArrays()
	{
		byte[] array1 = {1, 2, 3};
		byte[] array2 = {4, 5, 6};
		byte[] array3 = {7, 8, 9};
		
		assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6}, ArrayUtils.join(array1, array2));
		assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, ArrayUtils.join(array1, array2, array3));
		assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, ArrayUtils.join(null, array1, null, array2, null, array3, null));
		assertArrayEquals(new byte[]{}, ArrayUtils.join(new byte[]{}, new byte[]{}));
		assertArrayEquals(new byte[]{}, ArrayUtils.join(new byte[]{}, new byte[]{}, null));
		assertArrayEquals(new byte[]{}, ArrayUtils.join((byte[])null, (byte[])null, (byte[])null));
		assertArrayEquals(null, ArrayUtils.join((byte[][])null));
	}
	
	@Test
	void joinArraysGeneric()
	{
		Integer[] array1 = {1, 2, 3};
		Integer[] array2 = {4, 5, 6};
		Integer[] array3 = {7, 8, 9};
		
		assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6}, ArrayUtils.join(array1, array2));
		assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, ArrayUtils.join(array1, array2, array3));
		assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, ArrayUtils.join(null, array1, null, array2, null, array3, null));
		assertArrayEquals(new Integer[]{}, ArrayUtils.join(new Integer[]{}, new Integer[]{}));
		assertArrayEquals(new Integer[]{}, ArrayUtils.join(new Integer[]{}, new Integer[]{}, null));
		assertArrayEquals(new Integer[]{}, ArrayUtils.join((Integer[])null, (Integer[])null, (Integer[])null));
		assertArrayEquals(null, ArrayUtils.join((Integer[][])null));
	}
	
	@Test
	void toStringTest()
	{
		byte[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
		
		assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16]", ArrayUtils.toArrayString(array));
		assertEquals("[00, 01, 02, 03, 04, 05, 06, 07, 08, 09, 0A, 0B, 0C, 0D, 0E, 0F, 10]", ArrayUtils.toHexaArrayString(array));
		assertEquals("0x000102030405060708090A0B0C0D0E0F10", ArrayUtils.toHexaString(array));
		assertEquals(null, ArrayUtils.toHexaArrayString(null));
		assertEquals(null, ArrayUtils.toArrayString(null));
		assertEquals(null, ArrayUtils.toHexaString(null));
	}
	
	@Test
	void findTest()
	{
		byte[] array = {10, 9, 2, 3, 4, 10, 6, 7, 8, 9, 10};
		
		assertEquals(0, ArrayUtils.findFirst(array, (byte)10));
		assertEquals(1, ArrayUtils.findFirst(array, (byte)9));
		assertEquals(8, ArrayUtils.findFirst(array, (byte)8));
		assertEquals(2, ArrayUtils.findFirst(array, (byte)2));
		assertEquals(-1, ArrayUtils.findFirst(array, (byte)11));
		assertEquals(-1, ArrayUtils.findFirst((byte[])null, (byte)10));
		
		assertEquals(10, ArrayUtils.findLast(array, (byte)10));
		assertEquals(9, ArrayUtils.findLast(array, (byte)9));
		assertEquals(8, ArrayUtils.findLast(array, (byte)8));
		assertEquals(2, ArrayUtils.findLast(array, (byte)2));
		assertEquals(-1, ArrayUtils.findLast(array, (byte)11));
		assertEquals(-1, ArrayUtils.findLast((byte[])null, (byte)10));
	}
	
	@Test
	void findGenericTest()
	{
		Integer[] array = {10, 9, 2, 3, 4, 10, 6, 7, 8, 9, 10};
		
		assertEquals(0, ArrayUtils.findFirst(array, 10));
		assertEquals(1, ArrayUtils.findFirst(array, 9));
		assertEquals(8, ArrayUtils.findFirst(array, 8));
		assertEquals(2, ArrayUtils.findFirst(array, 2));
		assertEquals(-1, ArrayUtils.findFirst(array, 11));
		assertEquals(-1, ArrayUtils.findFirst((Integer[])null, 10));
		
		assertEquals(10, ArrayUtils.findLast(array, 10));
		assertEquals(9, ArrayUtils.findLast(array, 9));
		assertEquals(8, ArrayUtils.findLast(array, 8));
		assertEquals(2, ArrayUtils.findLast(array, 2));
		assertEquals(-1, ArrayUtils.findLast(array, 11));
		assertEquals(-1, ArrayUtils.findLast((Integer[])null, 10));
	}
	
	@Test
	void intersectByte()
	{
		byte[] array1 = {1, 2, 3};
		byte[] array2 = {2, 3, 4};
		byte[] array3 = {1, 4};
		byte[] expected = {2, 3};
		
		assertArrayEquals(expected, ArrayUtils.intersect(array1, array2));
		assertArrayEquals(new byte[]{}, ArrayUtils.intersect(array1, array2, array3));
		assertArrayEquals(new byte[]{}, ArrayUtils.intersect(array1, null, array2));
		assertArrayEquals(null, ArrayUtils.intersect((byte[][])null));
		assertArrayEquals(new byte[]{}, ArrayUtils.intersect(new byte[][]{}));
	}
	
	@Test
	void intersectGeneric()
	{
		Integer[] array1 = {1, 2, 3};
		Integer[] array2 = {2, 3, 4};
		Integer[] array3 = {1, 4};
		Integer[] expected = {2, 3};
		
		assertArrayEquals(expected, ArrayUtils.intersect(array1, array2));
		assertArrayEquals(new Integer[]{}, ArrayUtils.intersect(array1, array2, array3));
		assertArrayEquals(new Integer[]{}, ArrayUtils.intersect(array1, null, array2));
		assertArrayEquals(null, ArrayUtils.intersect((Integer[][])null));
		assertArrayEquals(new Integer[]{}, ArrayUtils.intersect(new Integer[][]{}));
	}
	
	@Test
	void subArrayByte()
	{
		byte[] array = {1, 2, 3, 4, 5};
		
		assertArrayEquals(new byte[]{2, 3}, ArrayUtils.subArray(array, 1, 2));
		assertArrayEquals(new byte[]{2, 3, 4, 5}, ArrayUtils.subArray(array, 1, 9));
		assertArrayEquals(null, ArrayUtils.subArray((byte[])null, 1, 2));
		assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.subArray(array, 6, 3));
		assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.subArray(array, -1, 3));
	}
	
	@Test
	void subArrayGeneric()
	{
		Integer[] array = {1, 2, 3, 4, 5};
		
		assertArrayEquals(new Integer[]{2, 3}, ArrayUtils.subArray(array, 1, 2));
		assertArrayEquals(new Integer[]{2, 3, 4, 5}, ArrayUtils.subArray(array, 1, 9));
		assertArrayEquals(null, ArrayUtils.subArray((Integer[])null, 1, 2));
		assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.subArray(array, 6, 3));
		assertThrows(IndexOutOfBoundsException.class, () -> ArrayUtils.subArray(array, -1, 3));
	}
	
	@Test
	void containsByte()
	{
		byte[] array = {1, 2, 3};
		
		assertTrue(ArrayUtils.contains(array, (byte)2));
		assertFalse(ArrayUtils.contains(array, (byte)4));
		assertFalse(ArrayUtils.contains(null, (byte)2));
	}
	
	@Test
	void containsGeneric()
	{
		Integer[] array = {1, 2, 3};
		
		assertTrue(ArrayUtils.contains(array, 2));
		assertFalse(ArrayUtils.contains(array, 4));
		assertFalse(ArrayUtils.contains(null, 2));
	}
	
	@Test
	void findBytePredicate()
	{
		byte[] array = {1, 2, 3, 4, 5};
		
		assertEquals(2, ArrayUtils.findFirst(array, b -> b > 2));
		assertEquals(-1, ArrayUtils.findFirst((byte[])null, b -> b > 2));
		assertEquals(-1, ArrayUtils.findFirst(array, b -> b > 5));
		assertEquals(-1, ArrayUtils.findFirst(array, (Predicate<Byte>)null));
		
		assertEquals(4, ArrayUtils.findLast(array, b -> b > 2));
		assertEquals(-1, ArrayUtils.findLast((byte[])null, b -> b > 2));
		assertEquals(-1, ArrayUtils.findLast(array, b -> b > 5));
		assertEquals(-1, ArrayUtils.findLast(array, (Predicate<Byte>)null));
	}
	
	@Test
	void findGenericPredicate()
	{
		Integer[] array = {1, 2, 3, 4, 5};
		
		assertEquals(2, ArrayUtils.findFirst(array, b -> b > 2));
		assertEquals(-1, ArrayUtils.findFirst((Integer[])null, b -> b > 2));
		assertEquals(-1, ArrayUtils.findFirst(array, b -> b > 5));
		assertEquals(-1, ArrayUtils.findFirst(array, (Predicate<Integer>)null));
		
		assertEquals(4, ArrayUtils.findLast(array, b -> b > 2));
		assertEquals(-1, ArrayUtils.findLast((Integer[])null, b -> b > 2));
		assertEquals(-1, ArrayUtils.findLast(array, b -> b > 5));
		assertEquals(-1, ArrayUtils.findLast(array, (Predicate<Integer>)null));
	}
	
	@Test
	void testHexaStringToBytes()
	{
		assertArrayEquals(new byte[]{(byte)0xAB, 0x02, 0x34}, ArrayUtils.hexaStringToBytes("0xAB0234"));
		assertArrayEquals(new byte[]{0x0F}, ArrayUtils.hexaStringToBytes("F"));
		assertArrayEquals(null, ArrayUtils.hexaStringToBytes(null));
		
		assertThrows(IllegalArgumentException.class, () -> ArrayUtils.hexaStringToBytes("ZXY"));
		assertThrows(IllegalArgumentException.class, () -> ArrayUtils.hexaStringToBytes(""));
	}
	
	@Test
	void testFindWithExistingSubArray()
	{
		byte[] array = {1, 2, 3, 4, 5, 1, 2, 3, 4, 5};
		
		assertEquals(0, ArrayUtils.findFirst(array, new byte[]{1, 2}));
		assertEquals(2, ArrayUtils.findFirst(array, new byte[]{3, 4}));
		assertEquals(3, ArrayUtils.findFirst(array, new byte[]{4, 5}));
		assertEquals(4, ArrayUtils.findFirst(array, new byte[]{5, 1}));
		assertEquals(0, ArrayUtils.findFirst(array, new byte[]{1, 2, 3, 4, 5, 1, 2, 3, 4, 5}));
		
		assertEquals(5, ArrayUtils.findLast(array, new byte[]{1, 2}));
		assertEquals(7, ArrayUtils.findLast(array, new byte[]{3, 4}));
		assertEquals(8, ArrayUtils.findLast(array, new byte[]{4, 5}));
		assertEquals(4, ArrayUtils.findLast(array, new byte[]{5, 1}));
		assertEquals(0, ArrayUtils.findLast(array, new byte[]{1, 2, 3, 4, 5, 1, 2, 3, 4, 5}));
	}
	
	@Test
	void testFindWithNonExistingSubArray()
	{
		byte[] array = {1, 2, 3, 4, 5, 1, 2, 3, 4, 5};
		
		assertEquals(-1, ArrayUtils.findFirst(array, new byte[]{1, 3}));
		assertEquals(-1, ArrayUtils.findFirst(array, new byte[]{4, 5, 6}));
		assertEquals(-1, ArrayUtils.findFirst(array, new byte[]{0, 1, 2}));
		assertEquals(-1, ArrayUtils.findFirst(array, new byte[]{1, 2, 3, 5}));
		assertEquals(-1, ArrayUtils.findFirst(array, (byte[])null));
		assertEquals(-1, ArrayUtils.findFirst((byte[])null, new byte[]{1, 2}));
		assertEquals(-1, ArrayUtils.findFirst(array, new byte[]{0, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5}));
		assertEquals(-1, ArrayUtils.findFirst(array, new byte[]{1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6}));
		
		assertEquals(-1, ArrayUtils.findLast(array, new byte[]{1, 3}));
		assertEquals(-1, ArrayUtils.findLast(array, new byte[]{4, 5, 6}));
		assertEquals(-1, ArrayUtils.findLast(array, new byte[]{0, 1, 2}));
		assertEquals(-1, ArrayUtils.findFirst(array, new byte[]{1, 2, 3, 5}));
		assertEquals(-1, ArrayUtils.findLast(array, (byte[])null));
		assertEquals(-1, ArrayUtils.findLast((byte[])null, new byte[]{1, 2}));
		assertEquals(-1, ArrayUtils.findLast(array, new byte[]{0, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5}));
		assertEquals(-1, ArrayUtils.findLast(array, new byte[]{1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6}));
	}
	
	@Test
	void testFindWithExistingObjectSubArray()
	{
		Byte[] array = {1, 2, 3, 4, 5, null, 1, 2, 3, 4, 5};
		
		assertEquals(0, ArrayUtils.findFirst(array, new Byte[]{1, 2}));
		assertEquals(2, ArrayUtils.findFirst(array, new Byte[]{3, 4}));
		assertEquals(3, ArrayUtils.findFirst(array, new Byte[]{4, 5}));
		assertEquals(4, ArrayUtils.findFirst(array, new Byte[]{5, null, 1}));
		assertEquals(0, ArrayUtils.findFirst(array, new Byte[]{1, 2, 3, 4, 5, null, 1, 2, 3, 4, 5}));
		
		assertEquals(6, ArrayUtils.findLast(array, new Byte[]{1, 2}));
		assertEquals(8, ArrayUtils.findLast(array, new Byte[]{3, 4}));
		assertEquals(9, ArrayUtils.findLast(array, new Byte[]{4, 5}));
		assertEquals(4, ArrayUtils.findLast(array, new Byte[]{5, null, 1}));
		assertEquals(0, ArrayUtils.findLast(array, new Byte[]{1, 2, 3, 4, 5, null, 1, 2, 3, 4, 5}));
	}
	
	@Test
	void testFindWithNonExistingObjectSubArray()
	{
		Byte[] array = {1, 2, 3, 4, 5, null, 1, 2, 3, 4, 5};
		
		assertEquals(-1, ArrayUtils.findFirst(array, new Byte[]{1, 3}));
		assertEquals(-1, ArrayUtils.findFirst(array, new Byte[]{4, 5, 6}));
		assertEquals(-1, ArrayUtils.findFirst(array, new Byte[]{0, 1, 2}));
		assertEquals(-1, ArrayUtils.findFirst(array, new Byte[]{1, 2, 3, 5}));
		assertEquals(-1, ArrayUtils.findFirst(array, (Byte[])null));
		assertEquals(-1, ArrayUtils.findFirst((Byte[])null, new Byte[]{1, 2}));
		assertEquals(-1, ArrayUtils.findFirst(array, new Byte[]{0, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5}));
		assertEquals(-1, ArrayUtils.findFirst(array, new Byte[]{1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6}));
		assertEquals(-1, ArrayUtils.findFirst(array, new Byte[]{1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5}));
		assertEquals(-1, ArrayUtils.findFirst(array, new Byte[]{1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 8, 9}));
		
		assertEquals(-1, ArrayUtils.findLast(array, new Byte[]{1, 3}));
		assertEquals(-1, ArrayUtils.findLast(array, new Byte[]{4, 5, 6}));
		assertEquals(-1, ArrayUtils.findLast(array, new Byte[]{0, 1, 2}));
		assertEquals(-1, ArrayUtils.findLast(array, new Byte[]{1, 2, 3, 5}));
		assertEquals(-1, ArrayUtils.findLast(array, (Byte[])null));
		assertEquals(-1, ArrayUtils.findLast((Byte[])null, new Byte[]{1, 2}));
		assertEquals(-1, ArrayUtils.findLast(array, new Byte[]{0, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5}));
		assertEquals(-1, ArrayUtils.findLast(array, new Byte[]{1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6}));
		assertEquals(-1, ArrayUtils.findLast(array, new Byte[]{1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5}));
		assertEquals(-1, ArrayUtils.findLast(array, new Byte[]{1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 8, 9}));
	}
	
	@Test
	void testToSet()
	{
		byte[] array = {0, 1, 0, 2, 0, 3, 3, 4, 5, 5};
		
		Set<Byte> set = ArrayUtils.toSet(array);
		
		assertTrue(set.containsAll(Arrays.asList((byte)0, (byte)1, (byte)2, (byte)3, (byte)4, (byte)5)));
		assertEquals(6, set.size());
		
		assertEquals(0, ArrayUtils.toSet(new byte[]{}).size());
		assertEquals(null, ArrayUtils.toSet((byte[])null));
	}
	
	@Test
	void testToSetGeneric()
	{
		Integer[] array = {0, 1, 0, 2, 0, 3, 3, 4, 5, 5};
		
		Set<Integer> set = ArrayUtils.toSet(array);
		
		assertTrue(set.containsAll(Arrays.asList(0, 1, 2, 3, 4, 5)));
		assertEquals(6, set.size());
		
		assertEquals(0, ArrayUtils.toSet(new Integer[]{}).size());
		assertEquals(null, ArrayUtils.toSet((Integer[])null));
	}
}