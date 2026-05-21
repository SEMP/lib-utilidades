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
		this.pair1 = new Pair<>(1, "one");
		this.pair2 = new Pair<>(1, "one");
		this.pair3 = new Pair<>(2, "two");
	}
	
	@Test
	public void testGetFirst()
	{
		assertEquals(Integer.valueOf(1), this.pair1.getFirst());
		assertEquals(Integer.valueOf(2), this.pair3.getFirst());
	}
	
	@Test
	public void testGetSecond()
	{
		assertEquals("one", this.pair1.getSecond());
		assertEquals("two", this.pair3.getSecond());
	}
	
	@Test
	public void testSetFirst()
	{
		this.pair1.setFirst(3);
		assertEquals(Integer.valueOf(3), this.pair1.getFirst());
	}
	
	@Test
	public void testSetSecond()
	{
		this.pair1.setSecond("three");
		assertEquals("three", this.pair1.getSecond());
	}
	
	@Test
	public void testEqualsAndHashCode()
	{
		assertEquals(this.pair1, this.pair1);
		assertEquals(this.pair1, this.pair2);
		assertEquals(this.pair1.hashCode(), this.pair2.hashCode());
		
		assertNotEquals(this.pair1, this.pair3);
		assertNotEquals(this.pair1, null);
		assertNotEquals(this.pair1, "one");
		assertNotEquals(this.pair1.hashCode(), this.pair3.hashCode());
		assertNotEquals(this.pair1, new Pair<>(1, "two"));
		assertNotEquals(this.pair1, new Pair<>());
		assertNotEquals(this.pair1.hashCode(), new Pair<>().hashCode());
	}
	
	@Test
	public void testToString()
	{
		assertEquals("(1, one)", this.pair1.toString());
		assertEquals("(2, two)", this.pair3.toString());
	}
}