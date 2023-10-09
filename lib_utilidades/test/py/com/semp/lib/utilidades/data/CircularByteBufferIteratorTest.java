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
		this.initializeBuffer();
	}

	private void initializeBuffer()
	{
		this.list = new CircularByteBuffer(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8});
		
		list.start = 6;
		list.end = 3;
		
		this.iterator = this.list.iterator();
	}
	
	@Test
	public void testForward()
	{
		int index = iterator.getInternalIndex();
		
		assertEquals(3, this.iterator.forward(index, -8));
		assertEquals(6, this.iterator.forward(index, -7));
		assertEquals(7, this.iterator.forward(index, -6));
		assertEquals(8, this.iterator.forward(index, -5));
		assertEquals(0, this.iterator.forward(index, -4));
		assertEquals(1, this.iterator.forward(index, -3));
		assertEquals(2, this.iterator.forward(index, -2));
		assertEquals(3, this.iterator.forward(index, -1));
		assertEquals(-1, this.iterator.forward(index, 0));
		assertEquals(6, this.iterator.forward(index, 1));
		assertEquals(7, this.iterator.forward(index, 2));
		assertEquals(8, this.iterator.forward(index, 3));
		assertEquals(0, this.iterator.forward(index, 4));
		assertEquals(1, this.iterator.forward(index, 5));
		assertEquals(2, this.iterator.forward(index, 6));
		assertEquals(3, this.iterator.forward(index, 7));
		assertEquals(6, this.iterator.forward(index, 8));
		
		this.list.clear();
		
		assertEquals(-1, this.iterator.forward(index, 6));
	}
	
	@Test
	public void testRewind()
	{
		int index = iterator.getInternalIndex();
		
		assertEquals(6, this.iterator.rewind(index, -8));
		assertEquals(3, this.iterator.rewind(index, -7));
		assertEquals(2, this.iterator.rewind(index, -6));
		assertEquals(1, this.iterator.rewind(index, -5));
		assertEquals(0, this.iterator.rewind(index, -4));
		assertEquals(8, this.iterator.rewind(index, -3));
		assertEquals(7, this.iterator.rewind(index, -2));
		assertEquals(6, this.iterator.rewind(index, -1));
		assertEquals(-1, this.iterator.rewind(index, 0));
		assertEquals(3, this.iterator.rewind(index, 1));
		assertEquals(2, this.iterator.rewind(index, 2));
		assertEquals(1, this.iterator.rewind(index, 3));
		assertEquals(0, this.iterator.rewind(index, 4));
		assertEquals(8, this.iterator.rewind(index, 5));
		assertEquals(7, this.iterator.rewind(index, 6));
		assertEquals(6, this.iterator.rewind(index, 7));
		assertEquals(3, this.iterator.rewind(index, 8));
		
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
		iterator.setInternalIndex(1);
		int index = iterator.getInternalIndex();
		
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
			{1, 14}
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
			{1, -14}
		};
		
		for(Object[] data : positiveCases)
		{
			assertEquals(data[0], iterator.forward(index, (Integer)data[1]));
			assertEquals(data[0], iterator.rewind(index, -(Integer)data[1]));
		}
		
		for(Object[] data : negativeCases)
		{
			assertEquals(data[0], iterator.forward(index, (Integer)data[1]));
			assertEquals(data[0], iterator.rewind(index, -(Integer)data[1]));
		}
	}
	
	@Test
	public void testForwardRewind2()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8});
		
		buffer.start = 6;
		buffer.end = 3;
		
		CircularByteBufferIterator iterator = buffer.iterator();
		iterator.setInternalIndex(6);
		int index = iterator.getInternalIndex();
		
		Object[][] positiveCases = new Object[][]
		{
			{6, 0},
			{7, 1},
			{8, 2},
			{0, 3},
			{1, 4},
			{2, 5},
			{3, 6},
			{6, 7},
			{7, 8},
			{8, 9},
			{0, 10},
			{1, 11},
			{2, 12},
			{3, 13},
			{6, 14}
		};
		
		Object[][] negativeCases = new Object[][]
		{
			{6, 0},
			{3, -1},
			{2, -2},
			{1, -3},
			{0, -4},
			{8, -5},
			{7, -6},
			{6, -7},
			{3, -8},
			{2, -9},
			{1, -10},
			{0, -11},
			{8, -12},
			{7, -13},
			{6, -14}
		};
		
		for(Object[] data : positiveCases)
		{
			assertEquals(data[0], iterator.forward(index, (Integer)data[1]));
			assertEquals(data[0], iterator.rewind(index, -(Integer)data[1]));
		}
		
		for(Object[] data : negativeCases)
		{
			assertEquals(data[0], iterator.forward(index, (Integer)data[1]));
			assertEquals(data[0], iterator.rewind(index, -(Integer)data[1]));
		}
	}
	
	@Test
	public void testHasPrevious()
	{
		assertTrue(this.iterator.hasPrevious());
		Byte value = iterator.next();
		assertTrue(this.iterator.hasPrevious());
		assertEquals(value, this.iterator.previous());
		assertFalse(this.iterator.hasPrevious());
		assertThrows(NoSuchElementException.class, () -> this.iterator.previous());
	}
	
	@Test
	public void testHasNext()
	{
		assertTrue(this.iterator.hasNext());
		Byte value = iterator.previous();
		assertTrue(this.iterator.hasNext());
		assertEquals(value, this.iterator.next());
		assertFalse(this.iterator.hasNext());
		assertThrows(NoSuchElementException.class, () -> this.iterator.next());
	}
	
	@Test
	public void testIterateFullBufferForwards()
	{
		while(this.iterator.hasNext())
		{
			this.iterator.next();
		}
		// Ensure that after iterating through, hasNext() returns false.
		assertFalse(this.iterator.hasNext());
		assertThrows(NoSuchElementException.class, () -> this.iterator.next());
		
		this.iterator.reset();

		while(this.iterator.hasNext())
		{
			this.iterator.nextByte();
		}
		// Ensure that after iterating through, hasNext() returns false.
		assertFalse(this.iterator.hasNext());
		assertThrows(NoSuchElementException.class, () -> this.iterator.nextByte());
	}
	
	@Test
	public void testIterateFullBufferBackwards()
	{
		while(this.iterator.hasPrevious())
		{
			this.iterator.previous();
		}
		
		// Ensure that after iterating backwards through, hasPrevious() returns false.
		assertFalse(this.iterator.hasPrevious());
		assertThrows(NoSuchElementException.class, () -> this.iterator.previous());
		
		this.iterator.reset();
		
		while(this.iterator.hasPrevious())
		{
			this.iterator.previousByte();
		}
		
		// Ensure that after iterating backwards through, hasPrevious() returns false.
		assertFalse(this.iterator.hasPrevious());
		assertThrows(NoSuchElementException.class, () -> this.iterator.previousByte());
	}
	
	@Test
	public void testNextIndex()
	{
		assertEquals(CircularByteBuffer.BUFFER_BOUNDARY, iterator.getIndex());
		assertEquals(0, iterator.nextIndex());
		iterator.next();
		assertEquals(0, iterator.getIndex());
		assertEquals("0", iterator.toString());
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
	public void testAddFirstLast()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(10);
		
		CircularByteBufferIterator iterator = buffer.iterator();
		
		iterator.addFirst((byte)0);
		assertEquals("[00]", buffer.toString());
		assertEquals("[{(00)}, 00, 00, 00, 00, 00, 00, 00, 00, 00]", buffer.stateToString());
		
		buffer.clear();
		iterator.reset();
		
		iterator.addLast((byte)0);
		assertEquals("[00]", buffer.toString());
		assertEquals("[{(00)}, 00, 00, 00, 00, 00, 00, 00, 00, 00]", buffer.stateToString());
		
		buffer.clear();
		iterator.reset();
		
		iterator.addFirst((byte)4);
		assertEquals("[04]", buffer.toString());
		assertEquals("[{(04)}, 00, 00, 00, 00, 00, 00, 00, 00, 00]", buffer.stateToString());
		
		iterator.addFirst((byte)3);
		assertEquals("[03, 04]", buffer.toString());
		assertEquals("[{04}, 00, 00, 00, 00, 00, 00, 00, 00, (03)]", buffer.stateToString());
		
		iterator.addLast((byte)5);
		assertEquals("[03, 04, 05]", buffer.toString());
		assertEquals("[04, {05}, 00, 00, 00, 00, 00, 00, 00, (03)]", buffer.stateToString());
		
		iterator.addLast((byte)6);
		assertEquals("[03, 04, 05, 06]", buffer.toString());
		assertEquals("[04, 05, {06}, 00, 00, 00, 00, 00, 00, (03)]", buffer.stateToString());
		
		iterator.addFirst((byte)2);
		assertEquals("[02, 03, 04, 05, 06]", buffer.toString());
		assertEquals("[04, 05, {06}, 00, 00, 00, 00, 00, (02), 03]", buffer.stateToString());
		
		iterator.addLast((byte)7);
		assertEquals("[02, 03, 04, 05, 06, 07]", buffer.toString());
		assertEquals("[04, 05, 06, {07}, 00, 00, 00, 00, (02), 03]", buffer.stateToString());
		
		iterator.addFirst((byte)1);
		assertEquals("[01, 02, 03, 04, 05, 06, 07]", buffer.toString());
		assertEquals("[04, 05, 06, {07}, 00, 00, 00, (01), 02, 03]", buffer.stateToString());
		
		iterator.addLast((byte)8);
		assertEquals("[01, 02, 03, 04, 05, 06, 07, 08]", buffer.toString());
		assertEquals("[04, 05, 06, 07, {08}, 00, 00, (01), 02, 03]", buffer.stateToString());
		
		iterator.addFirst((byte)0);
		assertEquals("[00, 01, 02, 03, 04, 05, 06, 07, 08]", buffer.toString());
		assertEquals("[04, 05, 06, 07, {08}, 00, (00), 01, 02, 03]", buffer.stateToString());
		
		iterator.addLast((byte)9);
		assertEquals("[00, 01, 02, 03, 04, 05, 06, 07, 08, 09]", buffer.toString());
		assertEquals("[04, 05, 06, 07, 08, {09}, (00), 01, 02, 03]", buffer.stateToString());
		
		iterator.addFirst((byte)10);
		assertEquals("[0A, 01, 02, 03, 04, 05, 06, 07, 08, 09]", buffer.toString());
		assertEquals("[04, 05, 06, 07, 08, {09}, (0A), 01, 02, 03]", buffer.stateToString());
		
		iterator.addLast((byte)11);
		assertEquals("[01, 02, 03, 04, 05, 06, 07, 08, 09, 0B]", buffer.toString());
		assertEquals("[04, 05, 06, 07, 08, 09, {0B}, (01), 02, 03]", buffer.stateToString());
	}
	
	@Test
	public void testInRange()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
		buffer.start = 2;
		buffer.end = 7;
		
		CircularByteBufferIterator iterator = buffer.iterator();
		
		// 1. Buffer and range are both linear.
		assertTrue(iterator.inRange(2, 7));
		assertFalse(iterator.inRange(7, 2));
		assertTrue(iterator.inRange(3, 7));
		assertFalse(iterator.inRange(7, 3));
		assertTrue(iterator.inRange(2, 6));
		assertFalse(iterator.inRange(6, 2));
		assertTrue(iterator.inRange(3, 6));
		assertFalse(iterator.inRange(6, 3));
		assertFalse(iterator.inRange(8, 9));
		assertFalse(iterator.inRange(9, 8));
		assertFalse(iterator.inRange(0, 1));
		assertFalse(iterator.inRange(1, 0));
		assertFalse(iterator.inRange(1, 7));
		assertFalse(iterator.inRange(7, 1));
		assertFalse(iterator.inRange(2, 8));
		assertFalse(iterator.inRange(8, 2));
		assertFalse(iterator.inRange(1, 8));
		assertFalse(iterator.inRange(8, 1));
		assertFalse(iterator.inRange(1, 6));
		assertFalse(iterator.inRange(6, 1));
		assertFalse(iterator.inRange(3, 8));
		assertFalse(iterator.inRange(8, 3));
		
		// Check for out-of-bounds
		assertFalse(iterator.inRange(-1, 7));
		assertFalse(iterator.inRange(7, -1));
	    assertFalse(iterator.inRange(2, 10));
	    assertFalse(iterator.inRange(10, 2));
	    assertFalse(iterator.inRange(-1, 10));
	    assertFalse(iterator.inRange(10, -1));
	    assertFalse(iterator.inRange(-1, 2));
	    assertFalse(iterator.inRange(2, -1));
	    assertFalse(iterator.inRange(7, 10));
	    assertFalse(iterator.inRange(10, 7));
		
		// Simulating a buffer that wraps around.
		// After this, the buffer will have data wrapped around like [7, 8, 9, 0, 1, 2]
		buffer.start = 7;
		buffer.end = 2;
		
		assertFalse(iterator.inRange(2, 7));
		assertTrue(iterator.inRange(7, 2));
		assertFalse(iterator.inRange(3, 7));
		assertFalse(iterator.inRange(7, 3));
		assertFalse(iterator.inRange(2, 6));
		assertFalse(iterator.inRange(6, 2));
		assertFalse(iterator.inRange(3, 6));
		assertFalse(iterator.inRange(6, 3));
		assertTrue(iterator.inRange(8, 9));
		assertFalse(iterator.inRange(9, 8));
		assertTrue(iterator.inRange(0, 1));
		assertFalse(iterator.inRange(1, 0));
		assertFalse(iterator.inRange(1, 7));
		assertTrue(iterator.inRange(7, 1));
		assertFalse(iterator.inRange(2, 8));
		assertTrue(iterator.inRange(8, 2));
		assertFalse(iterator.inRange(1, 8));
		assertTrue(iterator.inRange(8, 1));
		assertFalse(iterator.inRange(1, 6));
		assertFalse(iterator.inRange(6, 1));
		assertFalse(iterator.inRange(3, 8));
		assertFalse(iterator.inRange(8, 3));
		
		// Check for out-of-bounds
		assertFalse(iterator.inRange(-1, 7));
		assertFalse(iterator.inRange(7, -1));
	    assertFalse(iterator.inRange(2, 10));
	    assertFalse(iterator.inRange(10, 2));
	    assertFalse(iterator.inRange(-1, 10));
	    assertFalse(iterator.inRange(10, -1));
	    assertFalse(iterator.inRange(-1, 2));
	    assertFalse(iterator.inRange(2, -1));
	    assertFalse(iterator.inRange(7, 10));
	    assertFalse(iterator.inRange(10, 7));
	}
	
	@Test
	public void testSet()
	{
		assertThrows(IllegalStateException.class, () -> iterator.set((byte)9));
		iterator.next();
		iterator.set((byte)99);
		assertEquals(Byte.valueOf((byte)99), list.get(0));
		assertThrows(NullPointerException.class, () -> iterator.set(null));
	}
	
	@Test
	public void testAdd()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(10);
		
		CircularByteBufferIterator iterator = buffer.iterator();
		
		assertEquals("[]", buffer.toString());
		assertEquals("[00, 00, 00, 00, 00, 00, 00, 00, 00, 00]", buffer.stateToString());
		
		iterator.add((byte)0);
		iterator.add((byte)1);
		iterator.add((byte)2);
		iterator.add((byte)3);
		iterator.add((byte)4);
		
		assertEquals("[00, 01, 02, 03, 04]", buffer.toString());
		assertEquals("[(00), 01, 02, 03, {04}, 00, 00, 00, 00, 00]", buffer.stateToString());
		
		while(iterator.hasNext())
		{
			byte next = iterator.nextByte();
			
			if(next == 1)
			{
				break;
			}
		}
		
		iterator.add((byte)0x0A);
		assertEquals("[00, 01, 0A, 02, 03, 04]", buffer.toString());
		assertEquals("[01, 0A, 02, 03, {04}, 00, 00, 00, 00, (00)]", buffer.stateToString());
		
		iterator.add((byte)0x0B);
		assertEquals("[00, 01, 0A, 0B, 02, 03, 04]", buffer.toString());
		assertEquals("[0A, 0B, 02, 03, {04}, 00, 00, 00, (00), 01]", buffer.stateToString());
		
		iterator.add((byte)0x0C);
		assertEquals("[00, 01, 0A, 0B, 0C, 02, 03, 04]", buffer.toString());
		assertEquals("[0A, 0B, 0C, 02, 03, {04}, 00, 00, (00), 01]", buffer.stateToString());
		
		iterator.add((byte)0x0D);
		assertEquals("[00, 01, 0A, 0B, 0C, 0D, 02, 03, 04]", buffer.toString());
		assertEquals("[0A, 0B, 0C, 0D, 02, 03, {04}, 00, (00), 01]", buffer.stateToString());
		
		iterator.add((byte)0x0E);
		assertEquals("[00, 01, 0A, 0B, 0C, 0D, 0E, 02, 03, 04]", buffer.toString());
		assertEquals("[0A, 0B, 0C, 0D, 0E, 02, 03, {04}, (00), 01]", buffer.stateToString());
		
		iterator.add((byte)0x0F);
		assertEquals("[01, 0A, 0B, 0C, 0D, 0E, 0F, 02, 03, 04]", buffer.toString());
		assertEquals("[0B, 0C, 0D, 0E, 0F, 02, 03, {04}, (01), 0A]", buffer.stateToString());
		
		iterator.add((byte)0x10);
		assertEquals("[0A, 0B, 0C, 0D, 0E, 0F, 10, 02, 03, 04]", buffer.toString());
		assertEquals("[0C, 0D, 0E, 0F, 10, 02, 03, {04}, (0A), 0B]", buffer.stateToString());
		
		iterator.add((byte)0x11);
		assertEquals("[0A, 0B, 0C, 0D, 0E, 0F, 10, 11, 03, 04]", buffer.toString());
		assertEquals("[0C, 0D, 0E, 0F, 10, 11, 03, {04}, (0A), 0B]", buffer.stateToString());
		
		iterator.add((byte)0x12);
		assertEquals("[0A, 0B, 0C, 0D, 0E, 0F, 10, 11, 12, 04]", buffer.toString());
		assertEquals("[0C, 0D, 0E, 0F, 10, 11, 12, {04}, (0A), 0B]", buffer.stateToString());
		
		iterator.add((byte)0x13);
		assertEquals("[0A, 0B, 0C, 0D, 0E, 0F, 10, 11, 12, 13]", buffer.toString());
		assertEquals("[0C, 0D, 0E, 0F, 10, 11, 12, {13}, (0A), 0B]", buffer.stateToString());
		
		iterator.add((byte)0x14);
		assertEquals("[0B, 0C, 0D, 0E, 0F, 10, 11, 12, 13, 14]", buffer.toString());
		assertEquals("[0C, 0D, 0E, 0F, 10, 11, 12, 13, {14}, (0B)]", buffer.stateToString());
		
		iterator.add((byte)0x15);
		assertEquals("[0C, 0D, 0E, 0F, 10, 11, 12, 13, 14, 15]", buffer.toString());
		assertEquals("[(0C), 0D, 0E, 0F, 10, 11, 12, 13, 14, {15}]", buffer.stateToString());
		
		iterator.add((byte)0x16);
		assertEquals("[0D, 0E, 0F, 10, 11, 12, 13, 14, 15, 16]", buffer.toString());
		assertEquals("[{16}, (0D), 0E, 0F, 10, 11, 12, 13, 14, 15]", buffer.stateToString());
		
		iterator.add((byte)0x17);
		assertEquals("[0E, 0F, 10, 11, 12, 13, 14, 15, 16, 17]", buffer.toString());
		assertEquals("[16, {17}, (0E), 0F, 10, 11, 12, 13, 14, 15]", buffer.stateToString());
	}
	
	@Test
	public void testAddInversedIndex()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(new byte[]{02, 03, 04, 00, 00, 00, 00, 00, 00, 01});
		
		buffer.start = 8;
		buffer.end = 2;
		
		assertEquals("[00, 01, 02, 03, 04]", buffer.toString());
		assertEquals("[02, 03, {04}, 00, 00, 00, 00, 00, (00), 01]", buffer.stateToString());
		
		CircularByteBufferIterator iterator = buffer.iterator();
		
		while(iterator.hasNext())
		{
			byte next = iterator.nextByte();
			
			if(next == 1)
			{
				break;
			}
		}
		
		iterator.add((byte)0x0A);
		assertEquals("[00, 01, 0A, 02, 03, 04]", buffer.toString());
		assertEquals("[02, 03, {04}, 00, 00, 00, 00, (00), 01, 0A]", buffer.stateToString());
		
		iterator.add((byte)0x0B);
		assertEquals("[00, 01, 0A, 0B, 02, 03, 04]", buffer.toString());
		assertEquals("[02, 03, {04}, 00, 00, 00, (00), 01, 0A, 0B]", buffer.stateToString());
		
		iterator.add((byte)0x0C);
		assertEquals("[00, 01, 0A, 0B, 0C, 02, 03, 04]", buffer.toString());
		assertEquals("[0C, 02, 03, {04}, 00, 00, (00), 01, 0A, 0B]", buffer.stateToString());
		
		iterator.add((byte)0x0D);
		assertEquals("[00, 01, 0A, 0B, 0C, 0D, 02, 03, 04]", buffer.toString());
		assertEquals("[0C, 0D, 02, 03, {04}, 00, (00), 01, 0A, 0B]", buffer.stateToString());
		
		iterator.add((byte)0x0E);
		assertEquals("[00, 01, 0A, 0B, 0C, 0D, 0E, 02, 03, 04]", buffer.toString());
		assertEquals("[0C, 0D, 0E, 02, 03, {04}, (00), 01, 0A, 0B]", buffer.stateToString());
		
		iterator.add((byte)0x0F);
		assertEquals("[01, 0A, 0B, 0C, 0D, 0E, 0F, 02, 03, 04]", buffer.toString());
		assertEquals("[0D, 0E, 0F, 02, 03, {04}, (01), 0A, 0B, 0C]", buffer.stateToString());
		
		iterator.add((byte)0x10);
		assertEquals("[0A, 0B, 0C, 0D, 0E, 0F, 10, 02, 03, 04]", buffer.toString());
		assertEquals("[0E, 0F, 10, 02, 03, {04}, (0A), 0B, 0C, 0D]", buffer.stateToString());
		
		iterator.add((byte)0x11);
		assertEquals("[0A, 0B, 0C, 0D, 0E, 0F, 10, 11, 03, 04]", buffer.toString());
		assertEquals("[0E, 0F, 10, 11, 03, {04}, (0A), 0B, 0C, 0D]", buffer.stateToString());
		
		iterator.add((byte)0x12);
		assertEquals("[0A, 0B, 0C, 0D, 0E, 0F, 10, 11, 12, 04]", buffer.toString());
		assertEquals("[0E, 0F, 10, 11, 12, {04}, (0A), 0B, 0C, 0D]", buffer.stateToString());
		
		iterator.add((byte)0x13);
		assertEquals("[0A, 0B, 0C, 0D, 0E, 0F, 10, 11, 12, 13]", buffer.toString());
		assertEquals("[0E, 0F, 10, 11, 12, {13}, (0A), 0B, 0C, 0D]", buffer.stateToString());
		
		iterator.add((byte)0x14);
		assertEquals("[0B, 0C, 0D, 0E, 0F, 10, 11, 12, 13, 14]", buffer.toString());
		assertEquals("[0E, 0F, 10, 11, 12, 13, {14}, (0B), 0C, 0D]", buffer.stateToString());
		
		iterator.add((byte)0x15);
		assertEquals("[0C, 0D, 0E, 0F, 10, 11, 12, 13, 14, 15]", buffer.toString());
		assertEquals("[0E, 0F, 10, 11, 12, 13, 14, {15}, (0C), 0D]", buffer.stateToString());
		
		iterator.add((byte)0x16);
		assertEquals("[0D, 0E, 0F, 10, 11, 12, 13, 14, 15, 16]", buffer.toString());
		assertEquals("[0E, 0F, 10, 11, 12, 13, 14, 15, {16}, (0D)]", buffer.stateToString());
		
		iterator.add((byte)0x17);
		assertEquals("[0E, 0F, 10, 11, 12, 13, 14, 15, 16, 17]", buffer.toString());
		assertEquals("[(0E), 0F, 10, 11, 12, 13, 14, 15, 16, {17}]", buffer.stateToString());
	}
	
	@Test
	public void testInsertionBehavior()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(10);
		CircularByteBufferIterator iterator = buffer.iterator();
		
		// If the list contains no elements, the new element becomes the sole element on the list.
		iterator.add((byte)0x01);
		assertEquals("[01]", buffer.toString());
		
		// Add more elements to the buffer
		buffer.add(new byte[]{0x02, 0x03, 0x04, 0x05});
		
		// Set iterator to point just after 0x03
		while(iterator.hasNext())
		{
			if(iterator.nextByte() == 0x03)
			{
				break;
			}
		}
		
		// The new element 0x0A is inserted before 0x04 and after 0x03
		iterator.add((byte)0x0A);
		assertEquals("[01, 02, 03, 0A, 04, 05]", buffer.toString());
		
		// A subsequent call to next would be unaffected (should return 0x04)
		assertEquals(0x04, iterator.nextByte());
		
		iterator.add((byte)0x0B);
		// A subsequent call to previous would return the new element (0x0B)
		assertEquals(0x0B, iterator.previousByte());
		
		//Checking the increment of nextIndex and previousIndex by one (if buffer is not full)
		int currentIndex = iterator.nextIndex();
		iterator.add((byte)0x0B);
		assertEquals(currentIndex + 1, iterator.nextIndex());
		
		currentIndex = iterator.previousIndex();
		iterator.add((byte)0x0C);
		assertEquals(currentIndex + 1, iterator.previousIndex());
	}// assume this method gives the index of the previous element the iterator would return
	
	@Test
	public void testIteratorAddBehavior()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(10);
		CircularByteBufferIterator iterator = buffer.iterator();
		
		// Test 1: If the list contains no elements, the new element becomes the sole element.
		iterator.add((byte)0x01);
		assertEquals("[01]", buffer.toString());
		
		// Add more elements to the buffer
		buffer.add(new byte[]{0x02, 0x03, 0x04, 0x05});
		
		// Move iterator to just after 0x03
		while(iterator.hasNext())
		{
			if(iterator.nextByte() == 0x03)
			{
				break;
			}
		}
		
		// Test 2: Insert new byte and verify it's placed between 0x03 and 0x04
		iterator.add((byte)0x0A);
		assertEquals("[01, 02, 03, 0A, 04, 05]", buffer.toString());
		
		// Test 3: Cursor behavior
		// After insertion, the next element should still be 0x04
		assertEquals(0x04, iterator.nextByte());
		// After moving next, previous should be 0x04 and not the newly added element
		assertEquals(0x04, iterator.previousByte());
		
		// Check behavior when adding at the end of buffer
		while(iterator.hasNext())
		{
			iterator.nextByte();
		}
		iterator.add((byte)0x0B);
		assertEquals("[01, 02, 03, 0A, 04, 05, 0B]", buffer.toString());
		
		// Add more elements to buffer
		buffer.add(new byte[]
		{
				0x06, 0x07, 0x08
		});
		
		// Move the iterator to the last position again to insert using the iterator
		while(iterator.hasNext())
		{
			iterator.nextByte();
		}
		
		iterator.add((byte)0x0C);
		assertEquals("[02, 03, 0A, 04, 05, 0B, 06, 07, 08, 0C]", buffer.toString());
	}
	
	@Test
	public void testRemove()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(10);
		buffer.add(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
		
		CircularByteBufferIterator iterator = buffer.iterator();
		
		// Move to a position in the middle of the buffer
		for(int i = 0; i < 5; i++)
		{
			iterator.nextByte();
		}
		
		// 1. Test removing an element after calling next()
		assertEquals(5, iterator.nextByte());
		iterator.remove();
		assertEquals("[00, 01, 02, 03, 04, 06, 07, 08, 09]", buffer.toString());
		
		// 2. Test removing an element after calling previous()
		assertEquals(4, iterator.previousByte());
		iterator.remove();
		assertEquals("[00, 01, 02, 03, 06, 07, 08, 09]", buffer.toString());
		
		// 3. Ensure calling remove() multiple times throws an exception
		assertThrows(IllegalStateException.class, () -> iterator.remove());
		
		// Move to another position and check for correct positioning
		assertEquals(3, iterator.previousByte());
		
		// 4. Remove the first element in the buffer
		iterator.reset();
		iterator.nextByte();
		iterator.remove();
		assertEquals("[01, 02, 03, 06, 07, 08, 09]", buffer.toString());
		
		// 5. Remove the last element in the buffer
		while(iterator.hasNext())
		{
			iterator.nextByte();
		}
		iterator.remove();
		assertEquals("[01, 02, 03, 06, 07, 08]", buffer.toString());
		
		// 6. Test that calling remove() after add() throws an exception
		iterator.add((byte)0x0A);
		assertThrows(IllegalStateException.class, () -> iterator.remove());
		
		// 7. Test removing from illegal index.
		iterator.reset();
		iterator.next();
		iterator.setInternalIndex(CircularByteBuffer.BUFFER_BOUNDARY);
		assertThrows(NoSuchElementException.class, () -> iterator.remove());
	}
	
	@Test
	public void testGoTo()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(10);
		buffer.add(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
		
		CircularByteBufferIterator iterator = buffer.iterator();
		
		// 1. Test positioning at the start
		iterator.goTo(0);
		assertEquals(0, iterator.nextByte());
		
		// 2. Test positioning in the middle
		iterator.goTo(5);
		assertEquals(5, iterator.nextByte());
		
		// 3. Test positioning at the end
		iterator.goTo(9);
		assertEquals(9, iterator.nextByte());
		
		// 4. Ensure you can't position out of bounds - this assumes you throw an IndexOutOfBoundsException in such cases
		assertThrows(IndexOutOfBoundsException.class, () -> iterator.goTo(10));
		
		// 5. Test going backwards
		iterator.goTo(3);
		assertEquals(3, iterator.nextByte());
		
		// 6. Test positioning at an index after some iterations
		iterator.nextByte(); // Moving the iterator forward to make sure goTo resets its position properly
		iterator.goTo(7);
		assertEquals(7, iterator.nextByte());
	}
	
	@Test
	public void testGoToWithOtherOperations()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(10);
		buffer.add(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
		
		CircularByteBufferIterator iterator = buffer.iterator();
		
		// 1. Test positioning at the start and use previous()
		iterator.goTo(1);
		assertEquals(0, iterator.previousByte());
		
		// 2. Test positioning in the middle, and add 
		iterator.goTo(4);
		assertEquals(4, iterator.nextByte());
		iterator.add((byte)0xA);
		assertEquals("[01, 02, 03, 04, 0A, 05, 06, 07, 08, 09]", buffer.toString());
		
		// 3. Test positioning at the end, add and use previous()
		iterator.goTo(9);
		iterator.add((byte)0x0B);
		assertEquals(0x0B, iterator.previousByte());
		
		// 4. Test positioning in the middle after removing elements
		iterator.goTo(5);
		iterator.remove();
		assertEquals("[02, 03, 04, 0A, 06, 07, 08, 0B, 09]", buffer.toString());
		iterator.goTo(5);
		assertEquals(7, iterator.nextByte());
		
		// 5. Ensure you can't position out of bounds
		assertThrows(IndexOutOfBoundsException.class, () -> iterator.goTo(11));
		
		// 6. Test positioning at an index after some iterations and operations
		iterator.nextByte(); // Moving the iterator forward
		iterator.goTo(3);
		assertEquals(0x0A, iterator.nextByte());
	}
	
	@Test
	public void testIteratorRemoveRange()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(10);
		buffer.add(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
		CircularByteBufferIterator iterator = buffer.iterator();
		
		// Scenario 1: Removing from an empty buffer
		buffer.clear();
		assertThrows(NoSuchElementException.class, () -> iterator.remove(0, 2));
		buffer.add(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
		
		// Scenario 2: Remove a range starting from the beginning of the buffer
		iterator.remove(0, 3);
		assertEquals("[03, 04, 05, 06, 07, 08, 09]", buffer.toString());
		
		// Scenario 3: Remove a range from the middle of the buffer
		iterator.remove(2, 5);
		assertEquals("[03, 04, 08, 09]", buffer.toString());
		
		// Scenario 4: Remove a range from the end of the buffer
		iterator.remove(2, 4);
		assertEquals("[03, 04]", buffer.toString());
		
		// Scenario 5: Remove a range out of bounds
		assertThrows(IndexOutOfBoundsException.class, () -> iterator.remove(-1, 3));
		assertThrows(IndexOutOfBoundsException.class, () -> iterator.remove(1, 5));
		
		// Scenario 6: Removing with "from" greater than "to"
		assertThrows(IndexOutOfBoundsException.class, () -> iterator.remove(3, 1));
		
		// Scenario 7: Removing from and to the same index (effectively, no removal)
		iterator.remove(1, 1);
		assertEquals("[03, 04]", buffer.toString());
		
		// Scenario 8: Removing all the remaining data.
		iterator.remove(0, 2);
		assertEquals("[]", buffer.toString());
	}
	
	@Test
	public void testShiftFromStartScenarios()
	{
		// Test for shifting from the very start
		iterator.shiftFromStart(6);
		assertEquals("[07, 08, 00, 01, 02, 03]", list.toString());
		assertEquals("[00, 01, 02, {03}, 04, 05, 06, (07), 08]", list.stateToString());
		
		// Test for shifting near the end
		this.initializeBuffer();
		iterator.shiftFromStart(2);
		assertEquals("[06, 07, 08, 00, 01, 03]", list.toString());
		assertEquals("[08, 00, 01, {03}, 04, 05, 06, (06), 07]", list.stateToString());
		
		// Test for shifting from the end
		this.initializeBuffer();
		iterator.shiftFromStart(3);
		assertEquals("[06, 07, 08, 00, 01, 02]", list.toString());
		assertEquals("[08, 00, 01, {02}, 04, 05, 06, (06), 07]", list.stateToString());
		
		// Test for shifting to the immediate next position from the start
		this.initializeBuffer();
		iterator.shiftFromStart(7);
		assertEquals("[06, 08, 00, 01, 02, 03]", list.toString());
		assertEquals("[00, 01, 02, {03}, 04, 05, 06, (06), 08]", list.stateToString());

		// Test for shifting to a position right before the end
		this.initializeBuffer();
		iterator.shiftFromStart(1);
		assertEquals("[06, 07, 08, 00, 02, 03]", list.toString());
		assertEquals("[08, 00, 02, {03}, 04, 05, 06, (06), 07]", list.stateToString());

		this.initializeBuffer();
		iterator.shiftFromStart(8);
		iterator.shiftFromStart(0);
		assertEquals("[06, 07, 01, 02, 03]", list.toString());
		assertEquals("[07, 01, 02, {03}, 04, 05, 06, 06, (06)]", list.stateToString());

		this.initializeBuffer();
		iterator.shiftFromStart(2);
		iterator.shiftFromStart(3);
		assertEquals("[06, 07, 08, 00, 01]", list.toString());
		assertEquals("[07, 08, 00, {01}, 04, 05, 06, 06, (06)]", list.stateToString());
		
		// Test for multiple consecutive shifts
		this.initializeBuffer();
		iterator.shiftFromStart(3);
		iterator.shiftFromStart(2);
		assertEquals("[06, 07, 08, 00, 02]", list.toString());
		assertEquals("[07, 08, 00, {02}, 04, 05, 06, 06, (06)]", list.stateToString());
		
		this.initializeBuffer();
		iterator.shiftFromStart(7, 3);
		assertEquals("[06]", list.toString());
		assertEquals("[00, 01, 02, {(06)}, 04, 05, 06, 07, 08]", list.stateToString());
		
		iterator.shiftFromStart(3);
		assertEquals("[]", list.toString());
		assertEquals("[00, 01, 02, 06, 04, 05, 06, 07, 08]", list.stateToString());
	}
	
	@Test
	public void testShiftFromStartSegment()
	{
		iterator.shiftFromStart(1, 3);
		assertEquals("[06, 07, 08, 00]", list.toString());
		assertEquals("[(06), 07, 08, {00}, 04, 05, 06, 07, 08]", list.stateToString());
		
		// Test for shifting a segment that starts at the very beginning.
		this.initializeBuffer();
		iterator.shiftFromStart(0, 2);
		assertEquals("[06, 07, 08, 03]", list.toString());
		assertEquals("[(06), 07, 08, {03}, 04, 05, 06, 07, 08]", list.stateToString());
		
		// Test for shifting a segment that ends at the very end.
		this.initializeBuffer();
		iterator.shiftFromStart(7, 8);
		assertEquals("[06, 00, 01, 02, 03]", list.toString());
		assertEquals("[00, 01, 02, {03}, 04, 05, 06, 07, (06)]", list.stateToString());
		
		// Test for shifting a single element.
		this.initializeBuffer();
		iterator.shiftFromStart(2, 2);
		assertEquals("[06, 07, 08, 00, 01, 03]", list.toString());
		assertEquals("[08, 00, 01, {03}, 04, 05, 06, (06), 07]", list.stateToString());
		
		// Test for shifting the whole range.
		this.initializeBuffer();
		iterator.shiftFromStart(6, 3);
		assertEquals("[]", list.toString());
		assertEquals("[00, 01, 02, 03, 04, 05, 06, 07, 08]", list.stateToString());
		
		// Test for overlapping multiple consecutive shifts.
		this.initializeBuffer();
		iterator.shiftFromStart(0, 1);
		iterator.shiftFromStart(2, 3);
		assertEquals("[06, 07, 08]", list.toString());
		assertEquals("[07, (06), 07, {08}, 04, 05, 06, 07, 06]", list.stateToString());
		
		// Test for shifting a segment starting from the end and wrapping to the start.
		this.initializeBuffer();
		iterator.shiftFromStart(7, 1);
		assertEquals("[06, 02, 03]", list.toString());
		assertEquals("[00, (06), 02, {03}, 04, 05, 06, 07, 08]", list.stateToString());
	}
	
	@Test
	public void testShiftFromEndScenarios()
	{
		// Test for shifting from the very end
		iterator.shiftFromEnd(3);
		assertEquals("[06, 07, 08, 00, 01, 02]", list.toString());
		assertEquals("[00, 01, {02}, 03, 04, 05, (06), 07, 08]", list.stateToString());
		
		this.initializeBuffer();
		iterator.shiftFromEnd(7);
		assertEquals("[06, 08, 00, 01, 02, 03]", list.toString());
		assertEquals("[01, 02, {03}, 03, 04, 05, (06), 08, 00]", list.stateToString());
		
		// Test for shifting from the very start (should be a no-op)
		this.initializeBuffer();
		iterator.shiftFromEnd(6);
		assertEquals("[07, 08, 00, 01, 02, 03]", list.toString());
		assertEquals("[01, 02, {03}, 03, 04, 05, (07), 08, 00]", list.stateToString());
		
		// Test for shifting the mid of buffer
		this.initializeBuffer();
		iterator.shiftFromEnd(8);
		assertEquals("[06, 07, 00, 01, 02, 03]", list.toString());
		assertEquals("[01, 02, {03}, 03, 04, 05, (06), 07, 00]", list.stateToString());
		
		// Test for two consecutive shifts.
		this.initializeBuffer();
		iterator.shiftFromEnd(7);
		iterator.shiftFromEnd(7);
		assertEquals("[06, 00, 01, 02, 03]", list.toString());
		assertEquals("[02, {03}, 03, 03, 04, 05, (06), 00, 01]", list.stateToString());
		
		this.initializeBuffer();
		iterator.shiftFromEnd(7, 3);
		assertEquals("[06]", list.toString());
		assertEquals("[00, 01, 02, 03, 04, 05, {(06)}, 07, 08]", list.stateToString());
		
		iterator.shiftFromEnd(6);
		assertEquals("[]", list.toString());
		assertEquals("[00, 01, 02, 03, 04, 05, 06, 07, 08]", list.stateToString());
	}
	
	@Test
	public void testShiftFromEndSegment()
	{
		iterator.shiftFromEnd(1, 3);
		assertEquals("[06, 07, 08, 00]", list.toString());
		assertEquals("[{00}, 01, 02, 03, 04, 05, (06), 07, 08]", list.stateToString());
		
		// Test for shifting a segment that starts at the very beginning.
		this.initializeBuffer();
		iterator.shiftFromEnd(0, 2);
		assertEquals("[06, 07, 08, 03]", list.toString());
		assertEquals("[{03}, 01, 02, 03, 04, 05, (06), 07, 08]", list.stateToString());
		
		// Test for shifting a segment that ends at the very end.
		this.initializeBuffer();
		iterator.shiftFromEnd(7, 8);
		assertEquals("[06, 00, 01, 02, 03]", list.toString());
		assertEquals("[02, {03}, 02, 03, 04, 05, (06), 00, 01]", list.stateToString());
		
		// Test for shifting a single element.
		this.initializeBuffer();
		iterator.shiftFromEnd(2, 2);
		assertEquals("[06, 07, 08, 00, 01, 03]", list.toString());
		assertEquals("[00, 01, {03}, 03, 04, 05, (06), 07, 08]", list.stateToString());
		
		// Test for shifting the whole range.
		this.initializeBuffer();
		iterator.shiftFromEnd(6, 3);
		assertEquals("[]", list.toString());
		assertEquals("[00, 01, 02, 03, 04, 05, 06, 07, 08]", list.stateToString());
		
		// Test for overlapping multiple consecutive shifts.
		this.initializeBuffer();
		iterator.shiftFromEnd(0, 1);
		iterator.shiftFromEnd(6, 7); //TODO queda fuera de rango
		assertEquals("[08, 02, 03]", list.toString());
		assertEquals("[02, 03, 02, 03, 04, 05, (08), 02, {03}]", list.stateToString());
		
		// Test for shifting a segment starting from the end and wrapping to the start.
		this.initializeBuffer();
		iterator.shiftFromEnd(7, 1);
		assertEquals("[06, 02, 03]", list.toString());
		assertEquals("[00, 01, 02, 03, 04, 05, (06), 02, {03}]", list.stateToString());
	}
	
//	@Test
//	public void testShiftToStart() {
//	    iterator.shiftToStart(7);
//	    assertEquals("[06, 07, 07, 08, 00, 01, 02, 03]", list.toString());
//	}
//	
//	@Test
//	public void testShiftToEnd() {
//	    iterator.shiftToEnd(4);
//	    assertEquals("[07, 08, 06, 07, 08, 00, 01, 02, 03]", list.toString());
//	}
}