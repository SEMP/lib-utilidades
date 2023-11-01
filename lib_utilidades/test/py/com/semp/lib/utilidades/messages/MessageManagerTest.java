package py.com.semp.lib.utilidades.messages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import py.com.semp.lib.utilidades.configuration.Values;
import py.com.semp.lib.utilidades.internal.Messages;

public class MessageManagerTest
{
	private MessageManager englishMessage;
	private MessageManager spanishMessage;
	
	@BeforeEach
	public void setup()
	{
		ResourceBundle englishBundle = ResourceBundle.getBundle(Values.Constants.MESSAGES_PATH + Values.Resources.MESSAGES_BASE_NAME, Locale.ENGLISH);
		ResourceBundle spanisheBundle = ResourceBundle.getBundle(Values.Constants.MESSAGES_PATH + Values.Resources.MESSAGES_BASE_NAME, new Locale("es"));
		
		this.englishMessage = new MessageManager(englishBundle);
		this.spanishMessage = new MessageManager(spanisheBundle);
	}
	
	@Test
	public void retrieveEnglishMessage()
	{
		String message = this.englishMessage.getMessage(Messages.TYPE_NOT_DEFINED_ERROR);
		
		assertEquals("The data type must be defined.", message);
	}
	
	@Test
	public void retrieveSpanishMessage()
	{
		String message = this.spanishMessage.getMessage(Messages.TYPE_NOT_DEFINED_ERROR);
		
		assertEquals("Se debe definir el tipo de dato.", message);
	}
}
