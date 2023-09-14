package py.com.semp.lib.utilidades.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
		Object[] objectArray2 = buffer1.toArray();
		
		assertEquals(true, buffer1.equals(buffer2));
		assertArrayEquals(data1, data2);
		assertEquals(false, Arrays.equals(byteArray1, byteArray2));
		
		for(int i = 0; i < buffer1.size(); i++)
		{
			assertEquals(true, objectArray1[i].equals(byteArray1[i]));
		}
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