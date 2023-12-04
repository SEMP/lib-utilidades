/**
 * @author Sergio Morel
 */
module lib_utilidades
{
	exports py.com.semp.lib.utilidades.configuration;
	exports py.com.semp.lib.utilidades.communication;
	exports py.com.semp.lib.utilidades.communication.interfaces;
	exports py.com.semp.lib.utilidades.communication.listeners;
	exports py.com.semp.lib.utilidades.data;
	exports py.com.semp.lib.utilidades.exceptions;
	exports py.com.semp.lib.utilidades.log;
	exports py.com.semp.lib.utilidades.messages;
	exports py.com.semp.lib.utilidades.utilities;
	exports py.com.semp.lib.utilidades.shutdown;
	exports py.com.semp.lib.utilidades.state.machines;
	
	requires org.junit.jupiter.api;
	requires org.junit.jupiter.params;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	
	opens py.com.semp.lib.utilidades.data to com.fasterxml.jackson.databind;
}