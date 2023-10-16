package py.com.semp.lib.utilidades.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PairTest
{
	
	private Pair<Integer, String> pair1;
	private Pair<Integer, String> pair2;
	private Pair<Integer, String> pair3;
	
	@BeforeEach
	public void setup()
	{
		pair1 = new Pair<>(1, "one");
		pair2 = new Pair<>(1, "one");
		pair3 = new Pair<>(2, "two");
	}
	
	@Test
	public void testGetFirst()
	{
		assertEquals(Integer.valueOf(1), pair1.getFirst());
		assertEquals(Integer.valueOf(2), pair3.getFirst());
	}
	
	@Test
	public void testGetSecond()
	{
		assertEquals("one", pair1.getSecond());
		assertEquals("two", pair3.getSecond());
	}
	
	@Test
	public void testSetFirst()
	{
		pair1.setFirst(3);
		assertEquals(Integer.valueOf(3), pair1.getFirst());
	}
	
	@Test
	public void testSetSecond()
	{
		pair1.setSecond("three");
		assertEquals("three", pair1.getSecond());
	}
	
	@Test
	public void testEqualsAndHashCode()
	{
		assertEquals(pair1, pair1);
		assertEquals(pair1, pair2);
		assertEquals(pair1.hashCode(), pair2.hashCode());
		
		assertNotEquals(pair1, pair3);
		assertNotEquals(pair1, null);
		assertNotEquals(pair1, "one");
		assertNotEquals(pair1.hashCode(), pair3.hashCode());
		assertNotEquals(pair1, new Pair<>(1, "two"));
		assertNotEquals(pair1, new Pair<>());
		assertNotEquals(pair1.hashCode(), new Pair<>().hashCode());
	}
	
	@Test
	public void testToString()
	{
		assertEquals("(1, one)", pair1.toString());
		assertEquals("(2, two)", pair3.toString());
	}
}
