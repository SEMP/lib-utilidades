/**
 * @author Sergio Morel
 */
module lib_utilidades
{
	exports py.com.semp.lib.utilidades.utilities;
	exports py.com.semp.lib.utilidades.messages;
	exports py.com.semp.lib.utilidades.data;
	
	requires org.junit.jupiter.api;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires org.junit.jupiter.params;
	
	opens py.com.semp.lib.utilidades.data to com.fasterxml.jackson.databind;
}