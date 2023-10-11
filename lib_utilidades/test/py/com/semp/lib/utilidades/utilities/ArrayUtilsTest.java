package py.com.semp.lib.utilidades.utilities;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

class ArrayUtilsTest
{
	@Test
	void joinByte()
	{
		byte[] array1 = {1, 2, 3};
		byte[] array2 = {4, 5, 6};
		byte[] expected = {1, 2, 3, 4, 5, 6};
		
		assertArrayEquals(expected, ArrayUtils.join(array1, array2));
	}
	
	@Test
	void joinByteArrays()
	{
		byte[] array1 = {1, 2, 3};
		byte[] array2 = {4, 5, 6};
		byte[] array3 = {7, 8, 9};
		byte[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};
		byte[] joined = ArrayUtils.join(array1, array2, array3);
		
		assertArrayEquals(expected, joined);
	}
	
	@Test
	void toStringTest()
	{
		byte[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
		
		assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16]", ArrayUtils.toString(array));
		assertEquals("[00, 01, 02, 03, 04, 05, 06, 07, 08, 09, 0A, 0B, 0C, 0D, 0E, 0F, 10]", ArrayUtils.toHexaArrayString(array));
		assertEquals("0x000102030405060708090A0B0C0D0E0F10", ArrayUtils.toHexaString(array));
	}
	
	@Test
	void indexOfTest()
	{
		byte[] array = {10, 9, 2, 3, 4, 10, 6, 7, 8, 9, 10};
		
		assertEquals(0, ArrayUtils.findFirst(array, (byte)10));
		assertEquals(1, ArrayUtils.findFirst(array, (byte)9));
		assertEquals(2, ArrayUtils.findFirst(array, (byte)2));
		assertEquals(6, ArrayUtils.findLast(array, (byte)6));
		assertEquals(9, ArrayUtils.findLast(array, (byte)9));
		assertEquals(10, ArrayUtils.findLast(array, (byte)10));
		assertEquals(-1, ArrayUtils.findFirst(array, (byte)11));
		assertEquals(-1, ArrayUtils.findLast(array, (byte)11));
		assertEquals(-1, ArrayUtils.findFirst((byte[])null, (byte)10));
		assertEquals(-1, ArrayUtils.findLast((byte[])null, (byte)10));
	}
	
	@Test
	void join_byteArrays_emptyArrays()
	{
		byte[] array1 = {};
		byte[] array2 = {};
		byte[] expected = {};
		byte[] joined = ArrayUtils.join(array1, array2);
		
		assertArrayEquals(expected, joined);
	}

	@Test
	void join_byteArrays_nullArrays()
	{
		byte[] array1 = null;
		byte[] array2 = null;
		byte[] expected = {};
		byte[] joined = ArrayUtils.join(array1, array2);
		
		assertArrayEquals(expected, joined);
	}

	@Test
	void join_generic()
	{
		Integer[] array1 = {1, 2, 3};
		Integer[] array2 = {4, 5, 6};
		Integer[] expected = {1, 2, 3, 4, 5, 6};
		
		assertArrayEquals(expected, ArrayUtils.join(array1, array2));
	}
	
	@Test
	void intersect_byte()
	{
		byte[] array1 = {1, 2, 3};
		byte[] array2 = {2, 3, 4};
		byte[] expected = {2, 3};
		
		assertArrayEquals(expected, ArrayUtils.intersect(array1, array2));
	}
	
	@Test
	void intersect_generic()
	{
		Integer[] array1 = {1, 2, 3};
		Integer[] array2 = {2, 3, 4};
		Integer[] expected = {2, 3};
		
		assertArrayEquals(expected, ArrayUtils.intersect(array1, array2));
	}
	
	@Test
	void subArray_byte()
	{
		byte[] array = {1, 2, 3, 4, 5};
		byte[] expected = {2, 3};
		
		assertArrayEquals(expected, ArrayUtils.subArray(array, 1, 2));
	}
	
	@Test
	void subArray_byteArray_negativeIndex()
	{
		byte[] array = {1, 2, 3, 4, 5};
		byte[] subArray = ArrayUtils.subArray(array, -1, 3);
		byte[] expected = {};
		
		assertArrayEquals(expected, subArray);
	}

	@Test
	void subArray_byteArray_exceedingIndex()
	{
		byte[] array = {1, 2, 3, 4, 5};
		byte[] subArray = ArrayUtils.subArray(array, 6, 3);
		byte[] expected = {};
		
		assertArrayEquals(expected, subArray);
	}
	
	@Test
	void subArray_generic()
	{
		Integer[] array = {1, 2, 3, 4, 5};
		Integer[] expected = {2, 3};
		
		assertArrayEquals(expected, ArrayUtils.subArray(array, 1, 2));
	}
	
	@Test
	void contains_byte()
	{
		byte[] array = {1, 2, 3};
		
		assertTrue(ArrayUtils.contains(array, (byte)2));
		assertFalse(ArrayUtils.contains(array, (byte)4));
	}
	
	@Test
	void contains_byteArray_nullArray()
	{
		byte[] array = null;
		
		assertFalse(ArrayUtils.contains(array, (byte)1));
	}
	
	@Test
	void contains_generic()
	{
		Integer[] array = {1, 2, 3};
		
		assertTrue(ArrayUtils.contains(array, 2));
		assertFalse(ArrayUtils.contains(array, 4));
	}
	
	@Test
	void findFirst_byte()
	{
		byte[] array = {1, 2, 3, 4, 5};
		
		assertEquals(2, ArrayUtils.findFirst(array, b -> b > 2));
		assertEquals(-1, ArrayUtils.findFirst(array, b -> b > 5));
	}
	
	@Test
	void findFirst_generic()
	{
		Integer[] array = {1, 2, 3, 4, 5};
		
		assertEquals(2, ArrayUtils.findFirst(array, b -> b > 2));
		assertEquals(-1, ArrayUtils.findFirst(array, b -> b > 5));
	}
	
	@Test
	void findFirst_byteArray_nullArray()
	{
		byte[] array = null;
		Predicate<Byte> predicate = element -> element == 1;
		
		assertEquals(-1, ArrayUtils.findFirst(array, predicate));
	}
	
	@Test
	void findFirst_byteArray_nullPredicate()
	{
		byte[] array = {1, 2, 3, 4, 5};
		Predicate<Byte> predicate = null;
		
		assertEquals(-1, ArrayUtils.findFirst(array, predicate));
	}
	
	@Test
	void testHexaStringToBytesValid()
	{
		byte[] result = ArrayUtils.hexaStringToBytes("0xAB0234");
		byte[] expected = {(byte)0xAB, 0x02, 0x34};
		
		assertArrayEquals(expected, result);
		
		result = ArrayUtils.hexaStringToBytes("F");
		expected = new byte[]{0x0F};
		
		assertArrayEquals(expected, result);
	}
	
	@Test
	public void testHexaStringToBytesWithWhiteSpace()
	{
		byte[] result = ArrayUtils.hexaStringToBytes(" 0xAB0234 ");
		byte[] expected = {(byte)0xAB, 0x02, 0x34};
		
		assertArrayEquals(expected, result);
	}
	
	@Test
	public void testHexaStringToBytesNullInput()
	{
		byte[] result = ArrayUtils.hexaStringToBytes(null);
		
		assertNull(result);
	}
	
	@Test
	public void testHexaStringToBytesInvalidHex()
	{
		assertThrows(IllegalArgumentException.class, () -> ArrayUtils.hexaStringToBytes("ZXY"));
	}
	
	@Test
	public void testHexaStringToBytesEmptyString()
	{
		assertThrows(IllegalArgumentException.class, () -> ArrayUtils.hexaStringToBytes(""));
	}
	
	@Test
	void testToHexaStringValid()
	{
	    byte[] input = {(byte)0xAB, 0x02, 0x34};
	    String result = ArrayUtils.toHexaString(input);
	    String expected = "0xAB0234";
	    
	    assertEquals(expected, result);
	    
	    input = new byte[]{0x0F};
	    result = ArrayUtils.toHexaString(input);
	    expected = "0x0F";
	    
	    assertEquals(expected, result);
	}

	@Test
	void testToHexaStringNullInput()
	{
	    byte[] input = null;
	    String result = ArrayUtils.toHexaString(input);
	    
	    assertNull(result);
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
		
		assertEquals(-1, ArrayUtils.findLast(array, new Byte[]{1, 3}));
		assertEquals(-1, ArrayUtils.findLast(array, new Byte[]{4, 5, 6}));
		assertEquals(-1, ArrayUtils.findLast(array, new Byte[]{0, 1, 2}));
		assertEquals(-1, ArrayUtils.findFirst(array, new Byte[]{1, 2, 3, 5}));
		assertEquals(-1, ArrayUtils.findLast(array, (Byte[])null));
		assertEquals(-1, ArrayUtils.findLast((Byte[])null, new Byte[]{1, 2}));
		assertEquals(-1, ArrayUtils.findLast(array, new Byte[]{0, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5}));
		assertEquals(-1, ArrayUtils.findLast(array, new Byte[]{1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6}));
		assertEquals(-1, ArrayUtils.findFirst(array, new Byte[]{1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5}));
	}
	
	@Test
	void testFindFirst_WithNullArray()
	{
		byte[] array = null;
		byte[] subArray = {1, 2};
		
		int result = ArrayUtils.findFirst(array, subArray);
		
		assertEquals(-1, result);
	}
	
	@Test
	void testFindFirst_WithNullSubArray()
	{
		byte[] array = {1, 2, 3, 4, 5};
		byte[] subArray = null;
		
		int result = ArrayUtils.findFirst(array, subArray);
		
		assertEquals(-1, result);
	}
	
	@Test
	void testFindFirst_WithEmptyArray()
	{
		byte[] array = {};
		byte[] subArray = {1, 2};
		
		int result = ArrayUtils.findFirst(array, subArray);
		
		assertEquals(-1, result);
	}
	
	@Test
	void testFindFirst_WithEmptySubArray()
	{
		byte[] array = {1, 2, 3, 4, 5};
		byte[] subArray = {};
		
		int result = ArrayUtils.findFirst(array, subArray);
		
		assertEquals(-1, result);
	}
}