package py.com.semp.lib.utilidades.test;

/**
 * Interface for DTOs used for JUnit Test.
 * 
 * @author Sergio Morel
 */
public interface TestDTO
{
	/**
	 * Sets the name of the json file where the test case is stored.
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName);
	
	/**
	 * Gets the file name from which the test case was obtained.
	 * 
	 * @return
	 * - file name from which the test case was obtained.
	 */
	public String getFileName();
}