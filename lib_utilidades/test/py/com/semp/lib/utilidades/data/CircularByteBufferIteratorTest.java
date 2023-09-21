package py.com.semp.lib.utilidades.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CircularByteBufferIteratorTest
{
	private CircularByteBuffer list;
	private CircularByteBufferIterator iterator;
	
	@BeforeEach
	public void setUp()
	{
		byte[] originalArray = new byte[]{0, 1, 2, 3, 4};
		
		this.list = new CircularByteBuffer(originalArray);
		this.iterator = this.list.iterator();
	}
	
	@Test
	public void testForward()
	{
		int index = iterator.getIndex();
		
		assertEquals(4, this.iterator.forward(index, -6));
		assertEquals(0, this.iterator.forward(index, -5));
		assertEquals(1, this.iterator.forward(index, -4));
		assertEquals(2, this.iterator.forward(index, -3));
		assertEquals(3, this.iterator.forward(index, -2));
		assertEquals(4, this.iterator.forward(index, -1));
		assertEquals(-1, this.iterator.forward(index, 0));
		assertEquals(0, this.iterator.forward(index, 1));
		assertEquals(1, this.iterator.forward(index, 2));
		assertEquals(2, this.iterator.forward(index, 3));
		assertEquals(3, this.iterator.forward(index, 4));
		assertEquals(4, this.iterator.forward(index, 5));
		assertEquals(0, this.iterator.forward(index, 6));
		
		this.list.clear();
		
		assertEquals(-1, this.iterator.forward(index, 6));
	}
	
	@Test
	public void testRewind()
	{
		int index = iterator.getIndex();
		
		assertEquals(0, this.iterator.rewind(index, -6));
		assertEquals(4, this.iterator.rewind(index, -5));
		assertEquals(3, this.iterator.rewind(index, -4));
		assertEquals(2, this.iterator.rewind(index, -3));
		assertEquals(1, this.iterator.rewind(index, -2));
		assertEquals(0, this.iterator.rewind(index, -1));
		assertEquals(-1, this.iterator.rewind(index, 0));
		assertEquals(4, this.iterator.rewind(index, 1));
		assertEquals(3, this.iterator.rewind(index, 2));
		assertEquals(2, this.iterator.rewind(index, 3));
		assertEquals(1, this.iterator.rewind(index, 4));
		assertEquals(0, this.iterator.rewind(index, 5));
		assertEquals(4, this.iterator.rewind(index, 6));
		
		this.list.clear();
		
		assertEquals(-1, this.iterator.rewind(index, 6));
	}
	
	@Test
	public void testForwardRewind()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8});
		
		buffer.start = 6;
		buffer.end = 3;
		
		CircularByteBufferIterator iterator = buffer.iterator();
		iterator.setIndex(1);
		
		Object[][] positiveCases = new Object[][]
		{
			{1, 0},
			{2, 1},
			{3, 2},
			{6, 3},
			{7, 4},
			{8, 5},
			{0, 6},
			{1, 7},
			{2, 8},
			{3, 9},
			{6, 10},
			{7, 11},
			{8, 12},
			{0, 13},
			{1, 14},
		};
		
		Object[][] negativeCases = new Object[][]
		{
			{1, 0},
			{0, -1},
			{8, -2},
			{7, -3},
			{6, -4},
			{3, -5},
			{2, -6},
			{1, -7},
			{0, -8},
			{8, -9},
			{7, -10},
			{6, -11},
			{3, -12},
			{2, -13},
			{1, -14},
		};
		
		for(Object[] data : positiveCases)
		{
			assertEquals(data[0], iterator.forward(1, (Integer)data[1]));
		}
		
		for(Object[] data : negativeCases)
		{
			assertEquals(data[0], iterator.forward(1, (Integer)data[1]));
			assertEquals(data[0], iterator.rewind(1, -(Integer)data[1]));
		}
	}
	
	@Test
	public void testHasPrevious()
	{
		assertTrue(this.iterator.hasPrevious());
		iterator.next();
		assertFalse(this.iterator.hasPrevious());
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
		assertEquals((byte)4, iterator.next());
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
		assertEquals(list.size() - 1, iterator.previousIndex());
		iterator.next();
		assertEquals(-1, iterator.previousIndex());
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
