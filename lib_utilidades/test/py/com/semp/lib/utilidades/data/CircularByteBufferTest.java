package py.com.semp.lib.utilidades.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.type.TypeReference;

import py.com.semp.lib.utilidades.test.TestUtils;

public class CircularByteBufferTest
{
	public static final String DATA_DIRECTORY = "/resources/data/buffer/";
	
	private static final String[] JSON_FILES_EXTRACT_ALL_1H =
	{
		"extract_all_1h.json"
	};
	
	private static final String[] JSON_FILES_EXTRACT_ONE_1H =
	{
		"extract_one_1h.json"
	};
	
	private static final String[] JSON_FILES_EXTRACT_ALL_2H =
	{
		"extract_all_2h.json"
	};
	
	private static final String[] JSON_FILES_EXTRACT_ONE_2H =
	{
		"extract_one_2h.json"
	};
	
	private static final String[] JSON_FILES_CIRCULAR_BUFFER =
	{
		"circular_buffer_byteArray.json",
		"circular_buffer_capacity.json"
	};
	
	@Test
	public void testBytes()
	{
		assertThrows(NullPointerException.class, () -> new CircularByteBuffer(null));
		assertThrows(IllegalArgumentException.class, () -> new CircularByteBuffer(new byte[]{}));
		
		byte[] originalArray1 = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		byte[] originalArray2 = new byte[]{7, 8, 9, 10, 0, 1, 2, 3, 4, 5, 6};
		
		CircularByteBuffer buffer1 = new CircularByteBuffer(originalArray1);
		CircularByteBuffer buffer2 = new CircularByteBuffer(originalArray2);
		
		buffer2.start = 4;
		buffer2.end = 3;
		
		byte[] data1 = buffer1.getData();
		byte[] data2 = buffer2.getData();
		byte[] byteArray1 = buffer1.getByteArray();
		byte[] byteArray2 = buffer2.getByteArray();
		Object[] objectArray1 = buffer1.toArray();
		Object[] objectArray2 = buffer2.toArray();
		
		assertEquals(true, buffer1.equals(buffer2));
		assertArrayEquals(data1, data2);
		assertEquals(false, Arrays.equals(byteArray1, byteArray2));
		
		for(int i = 0; i < buffer1.size(); i++)
		{
			assertEquals(true, objectArray1[i].equals(byteArray1[i]));
		}
		
		assertThrows(NullPointerException.class, () -> buffer1.toArray((Byte[])null));
		assertThrows(ArrayStoreException.class, () -> buffer1.toArray(new String[20]));
		
		Byte[] baseArray = new Byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 2, 3, 4};
		Byte[] expectedArray = new Byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, null, 1, 2, 3, 4};
		
		assertArrayEquals(expectedArray, buffer1.toArray(baseArray));
		assertEquals(false, Arrays.equals(buffer1.toArray(new Byte[]{}), buffer1.toArray(baseArray)));
		assertEquals(true, Arrays.equals(buffer2.toArray(new Object[]{}), objectArray2));
		
		String expectedString = "[00, 01, 02, 03, 04, 05, 06, 07, 08, 09, 0A]";
		
		assertEquals(expectedString, buffer1.toString());
	}
	
	@Test
	public void testAddAll()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(3);
		
		buffer.add((byte)1);
		buffer.add((byte)2);
		
		List<Byte> newData = Arrays.asList((byte)3, (byte)4, (byte)5);
		buffer.addAll(newData);
		
		assertTrue(buffer.contains((byte)3));
		assertTrue(buffer.contains((byte)4));
		assertTrue(buffer.contains((byte)5));
		assertFalse(buffer.contains((byte)1));
		assertFalse(buffer.contains((byte)2));
		assertFalse(buffer.addAll(null));
		assertFalse(buffer.addAll(new ArrayList<>()));
	}
	
	@Test
	public void testContainsAll()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(new byte[]{0, 1, 2, 3, 4});
		assertTrue(buffer.containsAll(Arrays.asList((byte)2, (byte)3)));
		assertFalse(buffer.containsAll(Arrays.asList((byte)2, (byte)6)));
	}
	
	@Test
	public void testRemove()
	{
	    CircularByteBuffer buffer = new CircularByteBuffer(new byte[]{0, 1, 2, 3, 4});
	    
	    assertFalse(buffer.contains(null));
	    assertFalse(buffer.remove(null));
	    assertTrue(buffer.remove((byte)2));
	    assertFalse(buffer.contains((byte)2));
	    assertFalse(buffer.remove((byte)5));
	    
	    assertEquals(Byte.valueOf((byte)0), buffer.removeFirst());
	    assertFalse(buffer.contains((byte)0));
	    
	    assertEquals(Byte.valueOf((byte)4), buffer.removeLast());
	    assertFalse(buffer.contains((byte)4));

	    assertArrayEquals(new byte[]{1, 3}, buffer.getData());
	    
	    buffer.add((byte)5);
	    buffer.add((byte)6);
	    assertArrayEquals(new byte[]{1, 3, 5, 6}, buffer.getData());

	    assertTrue(buffer.remove((byte)3));
	    assertFalse(buffer.contains((byte)3));
	    assertArrayEquals(new byte[]{1, 5, 6}, buffer.getData());

	    CircularByteBufferIterator iterator = buffer.iterator();
	    assertThrows(IllegalStateException.class, iterator::remove);

	    assertFalse(buffer.isEmpty());
	    
	    buffer.clear();
	    
	    assertTrue(buffer.isEmpty());
	    assertThrows(NoSuchElementException.class, buffer::removeFirst);
	    assertThrows(NoSuchElementException.class, buffer::removeLast);

	    assertThrows(NoSuchElementException.class, iterator::remove);
	}
	
//	@Test
//	public void testRemoveAll()
//	{
//		CircularByteBuffer buffer = new CircularByteBuffer(new byte[]{0, 1, 2, 3, 4});
//		
//		assertFalse(buffer.removeAll(null));
//		assertFalse(buffer.removeAll(new ArrayList<>()));
//		assertTrue(buffer.removeAll(Arrays.asList((byte)2, (byte)3)));
//		assertFalse(buffer.contains((byte)2));
//		assertFalse(buffer.contains((byte)3));
//		assertEquals(3, buffer.size());
//	}
	
	@Test
	public void testRemoveAll()
	{
	    CircularByteBuffer buffer = new CircularByteBuffer(new byte[]{0, 1, 2, 3, 4});
	    
	    assertFalse(buffer.removeAll(null));
	    assertFalse(buffer.removeAll(new ArrayList<>()));
	    
	    assertTrue(buffer.removeAll(Arrays.asList((byte)2, (byte)3)));
	    assertFalse(buffer.contains((byte)2));
	    assertFalse(buffer.contains((byte)3));
	    
	    Set<Byte> setToRemove = new HashSet<>();
	    setToRemove.add((byte)0);
	    setToRemove.add((byte)4);
	    assertTrue(buffer.removeAll(setToRemove));
	    assertFalse(buffer.contains((byte)0));
	    assertFalse(buffer.contains((byte)4));
	    assertEquals(1, buffer.size());
	    assertTrue(buffer.contains((byte)1));
	}
	
	@Test
	public void testRetainAll()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(new byte[]{0, 1, 2, 3, 4});
		
		assertFalse(buffer.retainAll(null));
		assertTrue(buffer.retainAll(Arrays.asList((byte)2, (byte)3)));
		assertTrue(buffer.contains((byte)2));
		assertTrue(buffer.contains((byte)3));
		assertEquals(2, buffer.size());
		assertTrue(buffer.retainAll(new ArrayList<>()));
		assertFalse(buffer.retainAll(new ArrayList<>()));
	}
	
	@Test
	public void testGetData()
	{
		byte[] initialData = new byte[]{1, 2, 3, 4, 5};
	    CircularByteBuffer buffer = new CircularByteBuffer(initialData);
	    
	    byte[] result = buffer.getData(1, 3);
	    assertArrayEquals(new byte[]{2, 3, 4}, result);
	    
	    assertThrows(IndexOutOfBoundsException.class, () -> buffer.getData(0, 5));
	    assertThrows(IndexOutOfBoundsException.class, () -> buffer.getData(-1, 3));
	    assertThrows(IndexOutOfBoundsException.class, () -> buffer.getData(2, 0));
	}
	
	@Test
	public void testEquals()
	{
		CircularByteBuffer buffer1 = new CircularByteBuffer(new byte[]{0, 1, 2});
		CircularByteBuffer buffer2 = new CircularByteBuffer(new byte[]{0, 1, 2});
		CircularByteBuffer buffer3 = new CircularByteBuffer(new byte[]{2, 3, 4});
		CircularByteBuffer buffer4 = new CircularByteBuffer(new byte[]{2, 3, 4, 5});
		
		assertFalse(buffer1.equals(null));
		assertFalse(buffer1.equals(5));
		assertTrue(buffer1.equals(buffer1));
		assertTrue(buffer1.equals(buffer2));
		assertFalse(buffer1.equals(buffer3));
		assertFalse(buffer1.equals(buffer4));
	}
	
	@Test
	public void testHashCode()
	{
		CircularByteBuffer buffer1 = new CircularByteBuffer(new byte[]{0, 1, 2});
		CircularByteBuffer buffer2 = new CircularByteBuffer(new byte[]{0, 1, 2});
		
		assertEquals(buffer1.hashCode(), buffer2.hashCode());
	}
	
	@Test
	public void testInRange()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(new byte[]{0, 1, 2, 3, 4});
		
		assertTrue(buffer.inRange(1, 3));
		assertFalse(buffer.inRange(3, 1));
		assertTrue(buffer.inRange(0, 4));
		assertFalse(buffer.inRange(0, 5));
		
		buffer.end = 1;
		buffer.start = 3;
		
		assertTrue(buffer.inRange(3, 1));
		assertTrue(buffer.inRange(4, 0));
		assertFalse(buffer.inRange(2, 1));
		assertFalse(buffer.inRange(3, 2));
	}
	
	@ParameterizedTest
	@MethodSource("readExtractAll1HData")
	public void testExtractAll1H(ExtractDto testDTO)
	{
		String endHeader = testDTO.getEndHeader();
		String input = testDTO.getInput();
		String[] expectedOutput = testDTO.getExpectedOutput();
		String expectedRemainingData = testDTO.getExpectedRemainingData();
		
		byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
		
		CircularByteBuffer buffer;
		
		if(inputBytes.length < 1)
		{
			buffer = new CircularByteBuffer(10);
		}
		else
		{
			buffer = new CircularByteBuffer(inputBytes);
		}
		
		List<byte[]> extracted = buffer.extractAll(endHeader);
		
		assertEquals(expectedOutput.length, extracted.size(), testDTO.toString());
		
		for(int i = 0; i < expectedOutput.length; i++)
		{
			String expected = expectedOutput[i];
			String actual = new String(extracted.get(i));
			
			String message = testDTO + expected + " != " + actual;
			
			assertEquals(expected, actual, message);
		}
		
		byte[] remainingBytes = buffer.getData();
		String remaining = new String(remainingBytes);
		String message = testDTO + expectedRemainingData + " != " + remaining;
		
		assertEquals(expectedRemainingData, remaining, message);
	}
	
	@ParameterizedTest
	@MethodSource("readExtractOne1HData")
	public void testExtractOne1H(ExtractDto testDTO)
	{
		String endHeader = testDTO.getEndHeader();
		String input = testDTO.getInput();
		String[] expectedOutput = testDTO.getExpectedOutput();
		String expectedRemainingData = testDTO.getExpectedRemainingData();
		
		byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
		
		CircularByteBuffer buffer;
		
		if(inputBytes.length < 1)
		{
			buffer = new CircularByteBuffer(10);
		}
		else
		{
			buffer = new CircularByteBuffer(inputBytes);
		}
		
		byte[] extracted = buffer.extractOne(endHeader);
		
		assertEquals(1, expectedOutput.length, testDTO.toString());
		
		String expected = expectedOutput[0];
		String actual = new String(extracted);
		String message = testDTO + expected + " != " + actual;
		
		assertEquals(expected, actual, message);
		
		byte[] remainingBytes = buffer.getData();
		String remaining = new String(remainingBytes);
		message = testDTO + expectedRemainingData + " != " + remaining;
		
		assertEquals(expectedRemainingData, remaining, message);
	}
	
	@ParameterizedTest
	@MethodSource("readExtractAll2HData")
	public void testExtractAll2H(ExtractDto testDTO)
	{
		String startHeader = testDTO.getStartHeader();
		String endHeader = testDTO.getEndHeader();
		String input = testDTO.getInput();
		String[] expectedOutput = testDTO.getExpectedOutput();
		String expectedRemainingData = testDTO.getExpectedRemainingData();
		
		byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
		
		CircularByteBuffer buffer;
		
		if(inputBytes.length < 1)
		{
			buffer = new CircularByteBuffer(10);
		}
		else
		{
			buffer = new CircularByteBuffer(inputBytes);
		}
		
		List<byte[]> extracted = buffer.extractAll(startHeader, endHeader);
		
		assertEquals(expectedOutput.length, extracted.size(), testDTO.toString());
		
		for(int i = 0; i < expectedOutput.length; i++)
		{
			String expected = expectedOutput[i];
			String actual = new String(extracted.get(i));
			
			String message = testDTO + expected + " != " + actual;
			
			assertEquals(expected, actual, message);
		}
		
		byte[] remainingBytes = buffer.getData();
		String remaining = new String(remainingBytes);
		String message = testDTO + expectedRemainingData + " != " + remaining;
		
		assertEquals(expectedRemainingData, remaining, message);
	}
	
	@ParameterizedTest
	@MethodSource("readExtractOne2HData")
	public void testExtractOne2H(ExtractDto testDTO)
	{
		String startHeader = testDTO.getStartHeader();
		String endHeader = testDTO.getEndHeader();
		String input = testDTO.getInput();
		String[] expectedOutput = testDTO.getExpectedOutput();
		String expectedRemainingData = testDTO.getExpectedRemainingData();
		
		byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
		
		CircularByteBuffer buffer;
		
		if(inputBytes.length < 1)
		{
			buffer = new CircularByteBuffer(10);
		}
		else
		{
			buffer = new CircularByteBuffer(inputBytes);
		}
		
		byte[] extracted = buffer.extractOne(startHeader, endHeader);
		
		assertEquals(1, expectedOutput.length, testDTO.toString());
		
		String expected = expectedOutput[0];
		String actual = new String(extracted);
		String message = testDTO + expected + " != " + actual;
		
		assertEquals(expected, actual, message);
		
		byte[] remainingBytes = buffer.getData();
		String remaining = new String(remainingBytes);
		message = testDTO + expectedRemainingData + " != " + remaining;
		
		assertEquals(expectedRemainingData, remaining, message);
	}
	
	@ParameterizedTest
	@MethodSource("readCircularTestData")
	public void testCircular(CircularTestDto testDTO)
	{
		String constructorType = testDTO.getConstructorType();
		byte[] constructorArgument = testDTO.getConstructorArgument();
		byte[] dataToAdd = testDTO.getDataToAdd();
		int expectedSize = testDTO.getExpectedSize();
		byte[] expectedData = testDTO.getExpectedData();
		
		CircularByteBuffer buffer = null;
		
		switch(constructorType)
		{
			case "capacity":
			{
				int capacity = constructorArgument[0];
				
				buffer = new CircularByteBuffer(capacity);
				
				break;
			}
			case "byteArray":
			{
				buffer = new CircularByteBuffer(constructorArgument);
				
				break;
			}
			
			default: break;
		}
		
		buffer.add(dataToAdd);
		
		byte[] obtainedData = buffer.getData();
		
		String message = testDTO.toString() + "Obtained: " + Arrays.toString(obtainedData);
		
		assertEquals(expectedSize, obtainedData.length);
		assertArrayEquals(expectedData, obtainedData, message);
	}
	
	//************************************ Get data from JSON ************************************//
	
	private static Stream<Arguments> readExtractAll1HData() throws Exception
	{
		TypeReference<List<ExtractDto>> typeReference = new TypeReference<List<ExtractDto>>(){};
		
		return TestUtils.streamArgumentsFromFiles(typeReference, DATA_DIRECTORY, JSON_FILES_EXTRACT_ALL_1H);
	}
	
	private static Stream<Arguments> readExtractOne1HData() throws Exception
	{
		TypeReference<List<ExtractDto>> typeReference = new TypeReference<>(){};
		
		return TestUtils.streamArgumentsFromFiles(typeReference, DATA_DIRECTORY, JSON_FILES_EXTRACT_ONE_1H);
	}
	
	private static Stream<Arguments> readExtractAll2HData() throws Exception
	{
		TypeReference<List<ExtractDto>> typeReference = new TypeReference<>(){};
		
		return TestUtils.streamArgumentsFromFiles(typeReference, DATA_DIRECTORY, JSON_FILES_EXTRACT_ALL_2H);
	}
	
	private static Stream<Arguments> readExtractOne2HData() throws Exception
	{
		TypeReference<List<ExtractDto>> typeReference = new TypeReference<>(){};
		
		return TestUtils.streamArgumentsFromFiles(typeReference, DATA_DIRECTORY, JSON_FILES_EXTRACT_ONE_2H);
	}
	
	private static Stream<Arguments> readCircularTestData() throws Exception
	{
		TypeReference<List<CircularTestDto>> typeReference = new TypeReference<>(){};
		
		return TestUtils.streamArgumentsFromFiles(typeReference, DATA_DIRECTORY, JSON_FILES_CIRCULAR_BUFFER);
	}
	
	//********************************************************************************************//
}