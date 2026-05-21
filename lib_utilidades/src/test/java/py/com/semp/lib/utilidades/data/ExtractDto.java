package py.com.semp.lib.utilidades.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import py.com.semp.lib.utilidades.test.TestDTO;

public class ExtractDto implements TestDTO
{
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	public String testName;
	public String input;
	public String startHeader;
	public String endHeader;
	public String[] expectedOutput;
	public String expectedRemainingData;
	public String fileName;
	public Integer extraBytesAfter;
	
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
	
	private String getExpectedString()
	{
		String[] expectedOutput = this.getExpectedOutput();
		
		String jsonString = "[]";
		
		try
		{
			jsonString = OBJECT_MAPPER.writeValueAsString(expectedOutput);
		}
		catch(JsonProcessingException e)
		{
		}
		
		return jsonString;
	}
	
	@Override
	public String getFileName()
	{
		return this.fileName;
	}
	
	@Override
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	public Integer getExtraBytesAfter()
	{
		return this.extraBytesAfter;
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
}