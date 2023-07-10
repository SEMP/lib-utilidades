package py.com.semp.lib.utilidades.utilities;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class ArrayUtilsTest
{
	
	@Test
	void test()
	{
		Integer[] array = {1, 2, 3, 4, 5};
		
		Integer[] subset = ArrayUtils.subArray(array, 3, 3);
		
		System.out.println(Arrays.toString(subset));
		
		
	}
	
}
