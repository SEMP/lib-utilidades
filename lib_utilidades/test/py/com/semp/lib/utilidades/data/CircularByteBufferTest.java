package py.com.semp.lib.utilidades.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

class CircularByteBufferTest
{
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	private static final String DATA_DIRECTORY = "/resources/data/buffer/";
	
	private static final String[] JSON_FILE_NAME_EXTRACT_TEST =
	{
		"extract_test.json"
	};
	
	private static final String[] JSON_FILE_NAME_EXTRACT_ONE_TEST =
	{
		"extract_one_test.json"
	};
	
	private static final String[] JSON_FILE_NAME_CIRCULAR_BUFFER =
	{
		"circular_buffer_byteArray.json",
		"circular_buffer_capacity.json"
	};
	
	@ParameterizedTest
	@MethodSource("readExtractTestData")
	public void testExtract(ExtractTestDto testDTO)
	{
		String startHeader = testDTO.getStartHeader();
		String endHeader = testDTO.getEndHeader();
		String input = testDTO.getInput();
		String[] expectedOutput = testDTO.getExpectedOutput();
		
		byte[] inputBytes = input.getBytes();
		
		CircularByteBuffer buffer;
		
		if(inputBytes.length < 1)
		{
			buffer = new CircularByteBuffer(10);
		}
		else
		{
			buffer = new CircularByteBuffer(inputBytes);
		}
		
		List<byte[]> extracted = buffer.extract(startHeader, endHeader);
		
		assertEquals(expectedOutput.length, extracted.size(), testDTO.toString());
		
		for(int i = 0; i < expectedOutput.length; i++)
		{
			String expected = expectedOutput[i];
			String actual = new String(extracted.get(i));
			
			String message = testDTO + expected + " != " + actual;
			
			assertEquals(expected, actual, message);
		}
	}
	
	@ParameterizedTest
	@MethodSource("readExtractOneTestData")
	public void testExtractOne(ExtractTestDto testDTO)
	{
		String startHeader = testDTO.getStartHeader();
		String endHeader = testDTO.getEndHeader();
		String input = testDTO.getInput();
		String[] expectedOutput = testDTO.getExpectedOutput();
		String expectedRemainingData = testDTO.getExpectedRemainingData();
		
		byte[] inputBytes = input.getBytes();
		
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
	
	private static Stream<Arguments> readExtractTestData() throws Exception
	{
		List<Arguments> testData = new LinkedList<>();
		
		TypeReference<List<ExtractTestDto>> typeReference = new TypeReference<List<ExtractTestDto>>(){};
		
		for(String fileName : JSON_FILE_NAME_EXTRACT_TEST)
		{
			InputStream inputStream = CircularByteBufferTest.class.getResourceAsStream(DATA_DIRECTORY + fileName);
			
			List<ExtractTestDto> testDTOs = OBJECT_MAPPER.readValue(inputStream, typeReference);
			
			testDTOs.forEach(testDTO -> testDTO.setFileName(fileName));
			
			testData.addAll(testDTOs.stream().map(Arguments::of).collect(Collectors.toList()));
		}
		
		return testData.stream();
	}
	
	private static Stream<Arguments> readExtractOneTestData() throws Exception
	{
		List<Arguments> testData = new LinkedList<>();
		
		TypeReference<List<ExtractTestDto>> typeReference = new TypeReference<List<ExtractTestDto>>(){};
		
		for(String fileName : JSON_FILE_NAME_EXTRACT_ONE_TEST)
		{
			InputStream inputStream = CircularByteBufferTest.class.getResourceAsStream(DATA_DIRECTORY + fileName);
			
			List<ExtractTestDto> testDTOs = OBJECT_MAPPER.readValue(inputStream, typeReference);
			
			testDTOs.forEach(testDTO -> testDTO.setFileName(fileName));
			
			testData.addAll(testDTOs.stream().map(Arguments::of).collect(Collectors.toList()));
		}
		
		return testData.stream();
	}
	
	private static Stream<Arguments> readCircularTestData() throws Exception
	{
		List<Arguments> testData = new LinkedList<>();
		
		TypeReference<List<CircularTestDto>> typeReference = new TypeReference<List<CircularTestDto>>(){};
		
		for(String fileName : JSON_FILE_NAME_CIRCULAR_BUFFER)
		{
			InputStream inputStream = CircularByteBufferTest.class.getResourceAsStream(DATA_DIRECTORY + fileName);
			
			List<CircularTestDto> testDTOs = OBJECT_MAPPER.readValue(inputStream, typeReference);
			
			testDTOs.forEach(testDTO -> testDTO.setFileName(fileName));
			
			testData.addAll(testDTOs.stream().map(Arguments::of).collect(Collectors.toList()));
		}
		
		return testData.stream();
	}
	
	private static class ExtractTestDto
	{
		public String testName;
		public String input;
		public String startHeader;
		public String endHeader;
		public String[] expectedOutput;
		public String expectedRemainingData;
		public String fileName;
		
		public String getTestName()
		{
			return testName;
		}
		
		public String getInput()
		{
			return input;
		}
		
		public String getStartHeader()
		{
			return startHeader;
		}
		
		public String getEndHeader()
		{
			return endHeader;
		}
		
		public String[] getExpectedOutput()
		{
			return expectedOutput;
		}
		
		public String getExpectedRemainingData()
		{
			return expectedRemainingData;
		}
		
		public String getFileName()
		{
			return this.fileName;
		}
		
		public void setFileName(String fileName)
		{
			this.fileName = fileName;
		}
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append("File: ").append(this.getFileName()).append(" | ");
			sb.append("Name: ").append(this.getTestName()).append(" | ");
			sb.append("Start header: ").append(this.getStartHeader()).append(" | ");
			sb.append("Ending header: ").append(this.getEndHeader()).append(" | ");
			sb.append("Input: ").append(this.getInput()).append(" | ");
			sb.append("Expected: ").append(this.getExpectedString()).append(" | ");
			
			if(this.getExpectedRemainingData() != null)
			{
				sb.append("Expected Remaining Data: ").append(this.getExpectedRemainingData()).append(" | ");
			}
			
			return sb.toString();
		}
		
		private String getExpectedString()
		{
            String[] expectedOutput = this.getExpectedOutput();
            
            String jsonString = "[]";
            
			try
			{
				jsonString = OBJECT_MAPPER.writeValueAsString(expectedOutput);
			}
			catch(JsonProcessingException e){}
            
			return jsonString;
		}
	}
	
	private static class CircularTestDto
	{
		public String testScenario;
		public String constructorType;
		public byte[] constructorArgument;
		public byte[] dataToAdd;
		public int expectedSize;
		public byte[] expectedData;
		public String fileName;
		
		public String getTestScenario()
		{
			return testScenario;
		}
		
		public String getConstructorType()
		{
			return constructorType;
		}
		
		public byte[] getConstructorArgument()
		{
			return constructorArgument;
		}
		
		public byte[] getDataToAdd()
		{
			return dataToAdd;
		}
		
		public int getExpectedSize()
		{
			return expectedSize;
		}
		
		public byte[] getExpectedData()
		{
			return expectedData;
		}
		
		public String getFileName()
		{
			return this.fileName;
		}
		
		public void setFileName(String fileName)
		{
			this.fileName = fileName;
		}
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append("File: ").append(this.getFileName()).append(" | ");
			sb.append("Name: ").append(this.getTestScenario()).append(" | ");
			sb.append("Constructor Type: ").append(this.getConstructorType()).append(" | ");
			sb.append("Constructor Argument: ").append(this.getString(this.getConstructorArgument())).append(" | ");
			sb.append("Data to add: ").append(this.getString(this.getDataToAdd())).append(" | ");
			sb.append("Expected size: ").append(this.getExpectedSize()).append(" | ");
			sb.append("Expected data: ").append(this.getString(this.getExpectedData())).append(" | ");
			
			return sb.toString();
		}
		
		private String getString(byte[] data)
		{
			return Arrays.toString(data);
		}
	}
}