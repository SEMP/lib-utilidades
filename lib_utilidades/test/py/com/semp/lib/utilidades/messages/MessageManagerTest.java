package py.com.semp.lib.utilidades.messages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ResourceBundle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import py.com.semp.lib.utilidades.data.Pair;
import py.com.semp.lib.utilidades.test.TestUtils;

public class MessageManagerTest
{
	
	private MessageManager messageManager;
	
	@BeforeEach
	public void setup()
	{
		messageManager = new MessageManager(ResourceBundle.getBundle("py/com/semp/lib/utilidades/" + "messages"));
	}
	
	@Test
	public void testEnsureTrailingSlashForNullPath()
	{
		String path = null;
		
		Pair<Class<?>, Object> argument = new Pair<>(String.class, path);
		
		// Using reflection to test the private method.
		String result = (String)TestUtils.invokePrivateMethod(messageManager, "ensureTrailingSlash", argument);
		
		assertEquals(null, result);
	}
	
	@Test
	public void testEnsureTrailingSlashWithPathEndingWithSlash()
	{
		String path = "example/path/";
		String result = (String)TestUtils.invokePrivateMethod(messageManager, "ensureTrailingSlash", path);
		assertEquals("example/path/", result);
	}
	
	@Test
	public void testEnsureTrailingSlashWithPathNotEndingWithSlash()
	{
		String path = "example/path";
		String result = (String)TestUtils.invokePrivateMethod(messageManager, "ensureTrailingSlash", path);
		assertEquals("example/path/", result);
	}
}
