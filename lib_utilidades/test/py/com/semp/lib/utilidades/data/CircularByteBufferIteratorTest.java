package py.com.semp.lib.utilidades.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CircularByteBufferIteratorTest
{
	private CircularByteBuffer list;
	private CircularByteBufferIterator iterator;
	
	@BeforeEach
	public void setUp()
	{
		this.list = new CircularByteBuffer(new byte[]
		{
				0, 1, 2, 3, 4, 5, 6, 7, 8
		});
		
		list.start = 6;
		list.end = 3;
		
		//		byte[] originalArray = new byte[]{0, 1, 2, 3, 4};
		//		
		//		this.list = new CircularByteBuffer(originalArray);
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
		CircularByteBuffer buffer = new CircularByteBuffer(new byte[]
		{
				0, 1, 2, 3, 4, 5, 6, 7, 8
		});
		
		buffer.start = 6;
		buffer.end = 3;
		
		CircularByteBufferIterator iterator = buffer.iterator();
		iterator.setInternalIndex(1);
		int index = iterator.getInternalIndex();
		
		Object[][] positiveCases = new Object[][]
		{
				{
						1, 0
				},
				{
						2, 1
				},
				{
						3, 2
				},
				{
						6, 3
				},
				{
						7, 4
				},
				{
						8, 5
				},
				{
						0, 6
				},
				{
						1, 7
				},
				{
						2, 8
				},
				{
						3, 9
				},
				{
						6, 10
				},
				{
						7, 11
				},
				{
						8, 12
				},
				{
						0, 13
				},
				{
						1, 14
				},
		};
		
		Object[][] negativeCases = new Object[][]
		{
				{
						1, 0
				},
				{
						0, -1
				},
				{
						8, -2
				},
				{
						7, -3
				},
				{
						6, -4
				},
				{
						3, -5
				},
				{
						2, -6
				},
				{
						1, -7
				},
				{
						0, -8
				},
				{
						8, -9
				},
				{
						7, -10
				},
				{
						6, -11
				},
				{
						3, -12
				},
				{
						2, -13
				},
				{
						1, -14
				},
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
		CircularByteBuffer buffer = new CircularByteBuffer(new byte[]
		{
				0, 1, 2, 3, 4, 5, 6, 7, 8
		});
		
		buffer.start = 6;
		buffer.end = 3;
		
		CircularByteBufferIterator iterator = buffer.iterator();
		iterator.setInternalIndex(6);
		int index = iterator.getInternalIndex();
		
		Object[][] positiveCases = new Object[][]
		{
				{
						6, 0
				},
				{
						7, 1
				},
				{
						8, 2
				},
				{
						0, 3
				},
				{
						1, 4
				},
				{
						2, 5
				},
				{
						3, 6
				},
				{
						6, 7
				},
				{
						7, 8
				},
				{
						8, 9
				},
				{
						0, 10
				},
				{
						1, 11
				},
				{
						2, 12
				},
				{
						3, 13
				},
				{
						6, 14
				},
		};
		
		Object[][] negativeCases = new Object[][]
		{
				{
						6, 0
				},
				{
						3, -1
				},
				{
						2, -2
				},
				{
						1, -3
				},
				{
						0, -4
				},
				{
						8, -5
				},
				{
						7, -6
				},
				{
						6, -7
				},
				{
						3, -8
				},
				{
						2, -9
				},
				{
						1, -10
				},
				{
						0, -11
				},
				{
						8, -12
				},
				{
						7, -13
				},
				{
						6, -14
				},
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
		iterator.next();
		assertFalse(this.iterator.hasPrevious());
	}
	
	@Test
	public void testPrevious()
	{
		this.iterator.next();
		this.iterator.next();
		assertEquals(Byte.valueOf((byte)7), iterator.previous());
		assertEquals(Byte.valueOf((byte)6), iterator.previous());
	}
	
	@Test
	public void testPreviousNext()
	{
		assertEquals((byte)3, iterator.previous());
		assertEquals((byte)3, iterator.next());
	}
	
	@Test
	public void testPreviousByte()
	{
		iterator.next();
		iterator.next();
		assertEquals((byte)7, ((CircularByteBufferIterator)iterator).previousByte());
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
		CircularByteBuffer buffer = new CircularByteBuffer(10);
		
		CircularByteBufferIterator iterator = buffer.iterator();
		
		assertEquals("[]", buffer.toString());
		
		iterator.add((byte)0);
		iterator.add((byte)1);
		iterator.add((byte)2);
		iterator.add((byte)3);
		iterator.add((byte)4);
		
		assertEquals("[00, 01, 02, 03, 04]", buffer.toString());
		
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
		
		iterator.add((byte)0x0B);
		assertEquals("[00, 01, 0A, 0B, 02, 03, 04]", buffer.toString());
		
		iterator.add((byte)0x0C);
		assertEquals("[00, 01, 0A, 0B, 0C, 02, 03, 04]", buffer.toString());
		
		iterator.add((byte)0x0D);
		assertEquals("[00, 01, 0A, 0B, 0C, 0D, 02, 03, 04]", buffer.toString());
		
		iterator.add((byte)0x0E);
		assertEquals("[00, 01, 0A, 0B, 0C, 0D, 0E, 02, 03, 04]", buffer.toString());
		
		iterator.add((byte)0x0F);
		assertEquals("[01, 0A, 0B, 0C, 0D, 0E, 0F, 02, 03, 04]", buffer.toString());
		
		iterator.add((byte)0x10);
		assertEquals("[0A, 0B, 0C, 0D, 0E, 0F, 10, 02, 03, 04]", buffer.toString());
		
		iterator.add((byte)0x11);
		assertEquals("[0A, 0B, 0C, 0D, 0E, 0F, 10, 11, 03, 04]", buffer.toString());
		
		iterator.add((byte)0x12);
		assertEquals("[0A, 0B, 0C, 0D, 0E, 0F, 10, 11, 12, 04]", buffer.toString());
		
		iterator.add((byte)0x13);
		assertEquals("[0A, 0B, 0C, 0D, 0E, 0F, 10, 11, 12, 13]", buffer.toString());
		
		iterator.add((byte)0x14);
		assertEquals("[0B, 0C, 0D, 0E, 0F, 10, 11, 12, 13, 14]", buffer.toString());
		
		iterator.add((byte)0x15);
		assertEquals("[0C, 0D, 0E, 0F, 10, 11, 12, 13, 14, 15]", buffer.toString());
		
		iterator.add((byte)0x16);
		assertEquals("[0D, 0E, 0F, 10, 11, 12, 13, 14, 15, 16]", buffer.toString());
	}
	
	@Test
	public void testAddInversedIndex()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(new byte[]
		{
				02, 03, 04, 00, 00, 00, 00, 00, 00, 01
		});
		buffer.start = 8;
		buffer.end = 2;
		
		assertEquals("[00, 01, 02, 03, 04]", buffer.toString());
		
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
		
		iterator.add((byte)0x0B);
		assertEquals("[00, 01, 0A, 0B, 02, 03, 04]", buffer.toString());
		
		iterator.add((byte)0x0C);
		assertEquals("[00, 01, 0A, 0B, 0C, 02, 03, 04]", buffer.toString());
		
		iterator.add((byte)0x0D);
		assertEquals("[00, 01, 0A, 0B, 0C, 0D, 02, 03, 04]", buffer.toString());
		
		iterator.add((byte)0x0E);
		assertEquals("[00, 01, 0A, 0B, 0C, 0D, 0E, 02, 03, 04]", buffer.toString());
		
		iterator.add((byte)0x0F);
		assertEquals("[01, 0A, 0B, 0C, 0D, 0E, 0F, 02, 03, 04]", buffer.toString());
		
		iterator.add((byte)0x10);
		assertEquals("[0A, 0B, 0C, 0D, 0E, 0F, 10, 02, 03, 04]", buffer.toString());
		
		iterator.add((byte)0x11);
		assertEquals("[0A, 0B, 0C, 0D, 0E, 0F, 10, 11, 03, 04]", buffer.toString());
		
		iterator.add((byte)0x12);
		assertEquals("[0A, 0B, 0C, 0D, 0E, 0F, 10, 11, 12, 04]", buffer.toString());
		
		iterator.add((byte)0x13);
		assertEquals("[0A, 0B, 0C, 0D, 0E, 0F, 10, 11, 12, 13]", buffer.toString());
		
		iterator.add((byte)0x14);
		assertEquals("[0B, 0C, 0D, 0E, 0F, 10, 11, 12, 13, 14]", buffer.toString());
		
		iterator.add((byte)0x15);
		assertEquals("[0C, 0D, 0E, 0F, 10, 11, 12, 13, 14, 15]", buffer.toString());
		
		iterator.add((byte)0x16);
		assertEquals("[0D, 0E, 0F, 10, 11, 12, 13, 14, 15, 16]", buffer.toString());
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
		buffer.add(new byte[]
		{
				0x02, 0x03, 0x04, 0x05
		});
		
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
		
		//		iterator.previous();
		//		iterator.previous();
		iterator.add((byte)0x0B);
		
		//		System.out.println(iterator);
		//		System.out.println(buffer);
		
		// A subsequent call to previous would return the new element (0x0B)
		assertEquals(0x0B, iterator.previousByte());
		
		// Checking the increment of nextIndex and previousIndex by one
		int currentIndex = iterator.nextIndex(); // assume this method gives the index of the next element the iterator would return
		iterator.add((byte)0x0B);
		assertEquals(currentIndex + 1, iterator.nextIndex());
		
		currentIndex = iterator.previousIndex(); // assume this method gives the index of the previous element the iterator would return
		iterator.add((byte)0x0C);
		assertEquals(currentIndex + 1, iterator.previousIndex());
	}
	
	@Test
	public void testIteratorAddBehavior()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(10);
		CircularByteBufferIterator iterator = buffer.iterator();
		
		// Test 1: If the list contains no elements, the new element becomes the sole element.
		iterator.add((byte)0x01);
		assertEquals("[01]", buffer.toString());
		
		// Add more elements to the buffer
		buffer.add(new byte[]
		{
				0x02, 0x03, 0x04, 0x05
		});
		
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
		
		// Test edge cases: When buffer is full, ensure that new elements are added correctly
		buffer.add(new byte[]
		{
				0x06, 0x07, 0x08
		});
		iterator.add((byte)0x0C);
		assertEquals("[02, 03, 0A, 04, 05, 0B, 0C, 06, 07, 08]", buffer.toString());
	}
	
	@Test
	public void testIteratorAddBehavior2()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(10);
		CircularByteBufferIterator iterator = buffer.iterator();
		
		// Test 1: If the list contains no elements, the new element becomes the sole element.
		iterator.add((byte)0x01);
		assertEquals("[01]", buffer.toString());
		
		// Add more elements to the buffer
		buffer.add(new byte[]
		{
				0x02, 0x03, 0x04, 0x05
		});
		
		// Move iterator to just after 0x03
		iterator = buffer.iterator(); // Reinitialize the iterator after direct buffer modification
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
		iterator = buffer.iterator(); // Reinitialize the iterator after direct buffer modification
		while(iterator.hasNext())
		{
			iterator.nextByte();
		}
		
		iterator.add((byte)0x0C);
		assertEquals("[02, 03, 0A, 04, 05, 0B, 06, 07, 08, 0C]", buffer.toString());
	}
}
