package py.com.semp.lib.utilidades.utilities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Predicate;

class ArrayUtilsTest
{
	@Test
	void join_byte()
	{
		byte[] array1 = {1, 2, 3};
		byte[] array2 = {4, 5, 6};
		byte[] expected = {1, 2, 3, 4, 5, 6};
		
		assertArrayEquals(expected, ArrayUtils.join(array1, array2));
	}
	
	@Test
	void join_byteArrays()
	{
		byte[] array1 = {1, 2, 3};
		byte[] array2 = {4, 5, 6};
		byte[] array3 = {7, 8, 9};
		byte[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};
		byte[] joined = ArrayUtils.join(array1, array2, array3);
		
		assertArrayEquals(expected, joined);
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
	void testFindFirst_WithExistingSubArray()
	{
		byte[] array = {1, 2, 3, 4, 5};
		byte[] subArray = {3, 4};
		
		int result = ArrayUtils.findFirst(array, subArray);
		
		assertEquals(2, result);
	}
	
	@Test
	void testFindFirst_WithNonExistingSubArray()
	{
		byte[] array = {1, 2, 3, 4, 5};
		byte[] subArray = {6, 7};
		
		int result = ArrayUtils.findFirst(array, subArray);
		
		assertEquals(-1, result);
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