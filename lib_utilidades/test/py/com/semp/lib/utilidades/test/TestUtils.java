package py.com.semp.lib.utilidades.test;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
}