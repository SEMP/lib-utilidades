package py.com.semp.lib.utilidades.data;

import java.util.Arrays;

import py.com.semp.lib.utilidades.test.TestDTO;

public class CircularTestDto implements TestDTO
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