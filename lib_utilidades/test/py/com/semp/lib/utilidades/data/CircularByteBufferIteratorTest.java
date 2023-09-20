package py.com.semp.lib.utilidades.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ListIterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CircularByteBufferIteratorTest
{
	private CircularByteBuffer list;
	private ListIterator<Byte> iterator;
	
	@BeforeEach
	public void setUp()
	{
		byte[] originalArray = new byte[]{0, 1, 2, 3, 4};
		
		this.list = new CircularByteBuffer(originalArray);
		this.iterator = this.list.listIterator();
	}
	
	@Test
	public void testHasPrevious()
	{
		assertTrue(this.iterator.hasPrevious());
		iterator.next();
		assertTrue(this.iterator.hasPrevious());
	}
	
	@Test
	public void testPrevious()
	{
		this.iterator.next();
		this.iterator.next();
		assertEquals(Byte.valueOf((byte)1), iterator.previous());
		assertEquals(Byte.valueOf((byte)0), iterator.previous());
	}
	
	@Test
	public void testPreviousNext()
	{
		assertEquals((byte)4, iterator.previous());
		assertEquals((byte)0, iterator.next());
	}
	
	@Test
	public void testPreviousByte()
	{
		iterator.next();
		iterator.next();
		assertEquals((byte)1, ((CircularByteBufferIterator)iterator).previousByte());
	}
	
	@Test
	public void testNextIndex()
	{
		assertEquals(0, iterator.nextIndex());
		iterator.next();
		assertEquals(1, iterator.nextIndex());
	}
	
	@Test
	public void testPreviousIndex()
	{
		assertEquals(-1, iterator.previousIndex());
		iterator.next();
		assertEquals(0, iterator.previousIndex());
	}
	
	@Test
	public void testSet()
	{
		iterator.next();
		iterator.set((byte)99);
		assertEquals(Byte.valueOf((byte)99), list.get(0));
	}
	
	@Test
	public void testAdd()
	{
		iterator.add((byte)88);
		assertEquals(Byte.valueOf((byte)88), list.get(0));
		assertEquals(6, list.size()); // Assuming your CircularByteBufferList provides a size() method
	}
}
