package py.com.semp.lib.utilidades.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import py.com.semp.lib.utilidades.configuration.Values;

class MessageUtilTest
{
	private Locale originalLocale;
	
	@BeforeEach
	void setUp()
	{
		this.originalLocale = Locale.getDefault();
		Locale.setDefault(Locale.ENGLISH);
		MessageUtil.setLocale(Locale.ENGLISH);
	}
	
	@AfterEach
	void tearDown()
	{
		Locale.setDefault(this.originalLocale);
		MessageUtil.setLocale(this.originalLocale);
	}
	
	@Test
	void testResource()
	{
		String name = Values.Constants.MESSAGES_PATH + Values.Resources.MESSAGES_BASE_NAME + ".properties";
		
		URL resource = this.getClass().getResource(name);
		
		assertNotNull(resource, name);
	}
	
	@Test
	void testConstructorInvocationThrowsAssertionError()
	{
		try
		{
			Constructor<MessageUtil> constructor = MessageUtil.class.getDeclaredConstructor();
			constructor.setAccessible(true);
			constructor.newInstance();
		}
		catch(InvocationTargetException ex)
		{
			Throwable cause = ex.getCause();
			assertTrue(cause instanceof AssertionError);
			String expectedMessage = MessageUtil.getMessage("DONT_INSTANTIATE", MessageUtil.class.getName());
			assertEquals(expectedMessage, cause.getMessage());
		}
		catch(Exception e)
		{
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void testInvalidHexStringMessage()
	{
		String hexString = "1Z34";
		String expectedMessage = "Invalid hexadecimal string: " + hexString + ".";
		String actualMessage = MessageUtil.getMessage("INVALID_HEX_STRING", hexString);
		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	void testNullValuesNotAllowedMessage()
	{
		String location = "locationName";
		String expectedMessage = "Null values are not allowed in '" + location + "'.";
		String actualMessage = MessageUtil.getMessage("NULL_VALUES_NOT_ALLOWED_ERROR", location);
		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	void testNullValuesNotAllowedMessageWithNullParameterArray()
	{
		String expectedMessage = "Null values are not allowed in ''{0}''.";
		String actualMessage = MessageUtil.getMessage("NULL_VALUES_NOT_ALLOWED_ERROR", (Object[])null);
		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	void testNullValuesNotAllowedMessageWithANullParameter()
	{
		String expectedMessage = "Null values are not allowed in 'null'.";
		String actualMessage = MessageUtil.getMessage("NULL_VALUES_NOT_ALLOWED_ERROR", (Object)null);
		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	void testNullValuesNotAllowedMessageWithNoParameters()
	{
		String expectedMessage = "Null values are not allowed in ''{0}''.";
		String actualMessage = MessageUtil.getMessage("NULL_VALUES_NOT_ALLOWED_ERROR");
		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	public void testMessageLocalizationChange()
	{
		String message = MessageUtil.getMessage(Messages.TYPE_NOT_DEFINED_ERROR);
		assertEquals("The data type must be defined.", message);
		
		MessageUtil.setLocale(new Locale("es"));
		
		message = MessageUtil.getMessage(Messages.TYPE_NOT_DEFINED_ERROR);
		assertEquals("Se debe definir el tipo de dato.", message);
	}
}