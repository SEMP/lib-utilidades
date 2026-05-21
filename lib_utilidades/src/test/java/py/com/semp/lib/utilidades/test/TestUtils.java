package py.com.semp.lib.utilidades.test;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import py.com.semp.lib.utilidades.data.Pair;

/**
 * Class with utilities for testing.
 * 
 * @author Sergio Morel
 */
public class TestUtils
{
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	public static <T extends TestDTO> Stream<Arguments> streamArgumentsFromFiles(TypeReference<List<T>> typeReference, String directory, String[] files) throws Exception
	{
		List<Arguments> testData = new LinkedList<>();
		
		for(String fileName : files)
		{
			InputStream inputStream = TestUtils.class.getResourceAsStream(directory + fileName);
			
			List<T> testDTOs = OBJECT_MAPPER.readValue(inputStream, typeReference);
			
			testDTOs.forEach(testDTO -> testDTO.setFileName(fileName));
			
			testData.addAll(testDTOs.stream().map(Arguments::of).collect(Collectors.toList()));
		}
		
		return testData.stream();
	}
	
	@SafeVarargs
	public static Object invokePrivateMethod(Object obj, String methodName, Pair<Class<?>, Object>... args)
	{
		try
		{
			Class<?>[] classes = new Class<?>[args.length];
			Object[] values = new Object[args.length];
			
			for(int i = 0; i < args.length; i++)
			{
				Pair<Class<?>, Object> arg = args[i];
				
				classes[i] = arg.getFirst();
				values[i] = arg.getSecond();
			}
			
			Method method = obj.getClass().getDeclaredMethod(methodName, classes);
			method.setAccessible(true);
			return method.invoke(obj, values);
		}
		catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
		{
			throw new RuntimeException("Failed to invoke method: " + methodName + " on object: " + obj, e);
		}
	}
	
	public static Object invokePrivateMethod(Object obj, String methodName, Object... args)
	{
		try
		{
			Method method = obj.getClass().getDeclaredMethod(methodName, getParameterClasses(args));
			method.setAccessible(true);
			return method.invoke(obj, args);
		}
		catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private static Class<?>[] getParameterClasses(Object... args)
	{
		Class<?>[] classes = new Class[args.length];
		for(int i = 0; i < args.length; i++)
		{
			if(args[i] == null)
			{
				classes[i] = Object.class;
			}
			else
			{
				classes[i] = args[i].getClass();
			}
		}
		
		return classes;
	}
}