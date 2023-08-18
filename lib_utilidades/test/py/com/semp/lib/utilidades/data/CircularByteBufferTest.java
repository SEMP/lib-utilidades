package py.com.semp.lib.utilidades.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
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
	
	private static class ExtractTestDto
	{
		public String testName;
		public String input;
		public String startHeader;
		public String endHeader;
		public String[] expectedOutput;
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
}