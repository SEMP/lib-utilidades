package py.com.semp.lib.utilidades.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.type.TypeReference;

import py.com.semp.lib.utilidades.test.TestUtils;

public class CircularByteBufferTest
{
	public static final String DATA_DIRECTORY = "/resources/data/buffer/";
	
	private static final String[] JSON_FILES_EXTRACT_ALL_1H =
	{
		"extract_all_1h.json"
	};
	
	private static final String[] JSON_FILES_EXTRACT_ONE_1H =
	{
		"extract_one_1h.json"
	};
	
	private static final String[] JSON_FILES_EXTRACT_ALL_2H =
	{
		"extract_all_2h.json"
	};
	
	private static final String[] JSON_FILES_EXTRACT_ONE_2H =
	{
		"extract_one_2h.json"
	};
	
	private static final String[] JSON_FILES_CIRCULAR_BUFFER =
	{
		"circular_buffer_byteArray.json",
		"circular_buffer_capacity.json"
	};
	
	@Test
	public void testBytes()
	{
		assertThrows(NullPointerException.class, () -> new CircularByteBuffer(null));
		assertThrows(IllegalArgumentException.class, () -> new CircularByteBuffer(new byte[]{}));
		
		byte[] originalArray1 = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		byte[] originalArray2 = new byte[]{7, 8, 9, 10, 0, 1, 2, 3, 4, 5, 6};
		
		CircularByteBuffer buffer1 = new CircularByteBuffer(originalArray1);
		CircularByteBuffer buffer2 = new CircularByteBuffer(originalArray2);
		
		buffer2.start = 4;
		buffer2.end = 3;
		
		byte[] data1 = buffer1.getData();
		byte[] data2 = buffer2.getData();
		byte[] byteArray1 = buffer1.getByteArray();
		byte[] byteArray2 = buffer2.getByteArray();
		Object[] objectArray1 = buffer1.toArray();
		Object[] objectArray2 = buffer2.toArray();
		
		assertEquals(true, buffer1.equals(buffer2));
		assertArrayEquals(data1, data2);
		assertEquals(false, Arrays.equals(byteArray1, byteArray2));
		
		for(int i = 0; i < buffer1.size(); i++)
		{
			assertEquals(true, objectArray1[i].equals(byteArray1[i]));
		}
		
		assertThrows(NullPointerException.class, () -> buffer1.toArray((Byte[])null));
		assertThrows(ArrayStoreException.class, () -> buffer1.toArray(new String[20]));
		
		Byte[] baseArray = new Byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 2, 3, 4};
		Byte[] expectedArray = new Byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, null, 1, 2, 3, 4};
		
		assertArrayEquals(expectedArray, buffer1.toArray(baseArray));
		assertEquals(false, Arrays.equals(buffer1.toArray(new Byte[]{}), buffer1.toArray(baseArray)));
		assertEquals(true, Arrays.equals(buffer2.toArray(new Object[]{}), objectArray2));
		
		assertEquals("[00, 01, 02, 03, 04, 05, 06, 07, 08, 09, 0A]", buffer1.toString());
		assertEquals("[(00), 01, 02, 03, 04, 05, 06, 07, 08, 09, {0A}]", buffer1.stateToString());
	}
	
	@Test
	public void testAddAll()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(3);
		
		buffer.add((byte)1);
		buffer.add((byte)2);
		
		List<Byte> newData = Arrays.asList((byte)3, (byte)4, (byte)5);
		buffer.addAll(newData);
		
		assertTrue(buffer.contains((byte)3));
		assertTrue(buffer.contains((byte)4));
		assertTrue(buffer.contains((byte)5));
		assertFalse(buffer.contains((byte)1));
		assertFalse(buffer.contains((byte)2));
		assertFalse(buffer.addAll(null));
		assertFalse(buffer.addAll(new ArrayList<>()));
	}
	
	@Test
	public void testContainsAll()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(new byte[]{0, 1, 2, 3, 4});
		assertTrue(buffer.containsAll(Arrays.asList((byte)2, (byte)3)));
		assertFalse(buffer.containsAll(Arrays.asList((byte)2, (byte)6)));
	}
	
	@Test
	public void testRemove()
	{
	    CircularByteBuffer buffer = new CircularByteBuffer(new byte[]{0, 1, 2, 3, 4});
	    
	    assertFalse(buffer.contains(null));
	    assertFalse(buffer.remove(null));
	    assertTrue(buffer.remove((Object)(byte)2));
	    assertFalse(buffer.contains((byte)2));
	    assertFalse(buffer.remove((Object)(byte)5));
	    
	    assertEquals(Byte.valueOf((byte)0), buffer.removeFirst());
	    assertFalse(buffer.contains((byte)0));
	    
	    assertEquals(Byte.valueOf((byte)4), buffer.removeLast());
	    assertFalse(buffer.contains((byte)4));

	    assertArrayEquals(new byte[]{1, 3}, buffer.getData());
	    
	    buffer.add((byte)5);
	    buffer.add((byte)6);
	    assertArrayEquals(new byte[]{1, 3, 5, 6}, buffer.getData());

	    assertTrue(buffer.remove((Object)(byte)3));
	    assertFalse(buffer.contains((byte)3));
	    assertArrayEquals(new byte[]{1, 5, 6}, buffer.getData());

	    CircularByteBufferIterator iterator = buffer.iterator();
	    assertThrows(IllegalStateException.class, iterator::remove);

	    assertFalse(buffer.isEmpty());
	    
	    buffer.clear();
	    
	    assertTrue(buffer.isEmpty());
	    assertThrows(NoSuchElementException.class, buffer::removeFirst);
	    assertThrows(NoSuchElementException.class, buffer::removeLast);

	    assertThrows(IllegalStateException.class, iterator::remove);
	}
	
	@Test
	public void testRemoveAll()
	{
	    CircularByteBuffer buffer = new CircularByteBuffer(new byte[]{0, 1, 2, 3, 4});
	    
	    assertFalse(buffer.removeAll(null));
	    assertFalse(buffer.removeAll(new ArrayList<>()));
	    
	    assertTrue(buffer.removeAll(Arrays.asList((byte)2, (byte)3)));
	    assertFalse(buffer.contains((byte)2));
	    assertFalse(buffer.contains((byte)3));
	    
	    Set<Byte> setToRemove = new HashSet<>();
	    setToRemove.add((byte)0);
	    setToRemove.add((byte)4);
	    assertTrue(buffer.removeAll(setToRemove));
	    assertFalse(buffer.contains((byte)0));
	    assertFalse(buffer.contains((byte)4));
	    assertEquals(1, buffer.size());
	    assertTrue(buffer.contains((byte)1));
	}
	
	@Test
	public void testRetainAll()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(new byte[]{0, 1, 2, 3, 4});
		
		assertThrows(NullPointerException.class, () -> buffer.retainAll(null));
		assertTrue(buffer.retainAll(Arrays.asList((byte)2, (byte)3)));
		assertTrue(buffer.contains((byte)2));
		assertTrue(buffer.contains((byte)3));
		assertEquals(2, buffer.size());
		assertTrue(buffer.retainAll(new ArrayList<>()));
		assertFalse(buffer.retainAll(new ArrayList<>()));
	}
	
	@Test
	public void testGetData()
	{
		byte[] initialData = new byte[]{1, 2, 3, 4, 5};
	    CircularByteBuffer buffer = new CircularByteBuffer(initialData);
	    
	    byte[] result = buffer.getData(1, 3);
	    assertArrayEquals(new byte[]{2, 3, 4}, result);
	    
	    assertThrows(IndexOutOfBoundsException.class, () -> buffer.getData(0, 5));
	    assertThrows(IndexOutOfBoundsException.class, () -> buffer.getData(-1, 3));
	    assertThrows(IndexOutOfBoundsException.class, () -> buffer.getData(2, 0));
	}
	
	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void testEquals()
	{
		CircularByteBuffer buffer1 = new CircularByteBuffer(new byte[]{0, 1, 2});
		CircularByteBuffer buffer2 = new CircularByteBuffer(new byte[]{0, 1, 2});
		CircularByteBuffer buffer3 = new CircularByteBuffer(new byte[]{2, 3, 4});
		CircularByteBuffer buffer4 = new CircularByteBuffer(new byte[]{2, 3, 4, 5});
		
		assertFalse(buffer1.equals(null));
		assertFalse(buffer1.equals(5));
		assertTrue(buffer1.equals(buffer1));
		assertTrue(buffer1.equals(buffer2));
		assertFalse(buffer1.equals(buffer3));
		assertFalse(buffer1.equals(buffer4));
	}
	
	@Test
	public void testHashCode()
	{
		CircularByteBuffer buffer1 = new CircularByteBuffer(new byte[]{0, 1, 2});
		CircularByteBuffer buffer2 = new CircularByteBuffer(new byte[]{0, 1, 2});
		
		assertEquals(buffer1.hashCode(), buffer2.hashCode());
	}
	
	@Test
	public void testInRange()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(new byte[]{0, 1, 2, 3, 4});
		
		assertTrue(buffer.inRange(1, 3));
		assertFalse(buffer.inRange(3, 1));
		assertTrue(buffer.inRange(0, 4));
		assertFalse(buffer.inRange(0, 5));
		
		buffer.end = 1;
		buffer.start = 3;
		
		assertTrue(buffer.inRange(3, 1));
		assertTrue(buffer.inRange(4, 0));
		assertFalse(buffer.inRange(2, 1));
		assertFalse(buffer.inRange(3, 2));
	}
	
	@Test
	public void testIndexOf()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(new byte[]
		{
			10, 9, 2, 3, 4, 10, 6, 7, 8, 9, 10
		});
		
		assertEquals(0, buffer.indexOf((byte)10));
		assertEquals(1, buffer.indexOf((byte)9));
		assertEquals(2, buffer.indexOf((byte)2));
		assertEquals(6, buffer.lastIndexOf((byte)6));
		assertEquals(9, buffer.lastIndexOf((byte)9));
		assertEquals(10, buffer.lastIndexOf((byte)10));
	}
	
	@Test
	public void testSet()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(10);
		
		buffer.add(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
		
		assertEquals("[(00), 01, 02, 03, 04, 05, 06, 07, 08, {09}]", buffer.stateToString());
		
		// 1. Replace the element at the start.
		Byte replacedValue = buffer.set(0, (byte)10);
		assertEquals((byte)0, replacedValue.byteValue());
		assertEquals("[0A, 01, 02, 03, 04, 05, 06, 07, 08, 09]", buffer.toString());
		assertEquals("[(0A), 01, 02, 03, 04, 05, 06, 07, 08, {09}]", buffer.stateToString());
		
		// 2. Replace the element in the middle.
		replacedValue = buffer.set(5, (byte)11);
		assertEquals((byte)5, replacedValue.byteValue());
		assertEquals("[0A, 01, 02, 03, 04, 0B, 06, 07, 08, 09]", buffer.toString());
		assertEquals("[(0A), 01, 02, 03, 04, 0B, 06, 07, 08, {09}]", buffer.stateToString());
		
		// 3. Replace the element at the end.
		replacedValue = buffer.set(9, (byte)12);
		assertEquals((byte)9, replacedValue.byteValue());
		assertEquals("[0A, 01, 02, 03, 04, 0B, 06, 07, 08, 0C]", buffer.toString());
		assertEquals("[(0A), 01, 02, 03, 04, 0B, 06, 07, 08, {0C}]", buffer.stateToString());
		
		// 4. Ensure exceptions are thrown for out-of-bounds indices.
		assertThrows(IndexOutOfBoundsException.class, () -> buffer.set(-1, (byte)13));
		assertThrows(IndexOutOfBoundsException.class, () -> buffer.set(10, (byte)13));
		
		// 5. Test replacing with null.
		assertThrows(NullPointerException.class, () -> buffer.set(3, null));
	}
	
	@Test
	public void testAddAtIndex()
	{
	    CircularByteBuffer buffer = new CircularByteBuffer(10);
	    
	    buffer.add(new byte[]{0, 1, 2, 3, 4});
	    
	    assertEquals("[(00), 01, 02, 03, {04}, 00, 00, 00, 00, 00]", buffer.stateToString());
	    

	    // 1. Add an element at the start.
	    buffer.add(0, (byte)10);
	    assertEquals("[0A, 00, 01, 02, 03, 04]", buffer.toString());
	    assertEquals("[00, 01, 02, 03, {04}, 00, 00, 00, 00, (0A)]", buffer.stateToString());

	    // 2. Add an element in the middle.
	    buffer.add(3, (byte)11);
	    assertEquals("[0A, 00, 01, 0B, 02, 03, 04]", buffer.toString());
	    assertEquals("[01, 0B, 02, 03, {04}, 00, 00, 00, (0A), 00]", buffer.stateToString());

	    // 3. Add an element at the end.
	    buffer.add(7, (byte)12);
	    assertEquals("[0A, 00, 01, 0B, 02, 03, 04, 0C]", buffer.toString());
	    assertEquals("[01, 0B, 02, 03, 04, {0C}, 00, 00, (0A), 00]", buffer.stateToString());

	    // 4. Add elements until the buffer becomes full.
	    buffer.add(new byte[]{5, 6});
	    assertEquals("[0A, 00, 01, 0B, 02, 03, 04, 0C, 05, 06]", buffer.toString());
	    assertEquals("[01, 0B, 02, 03, 04, 0C, 05, {06}, (0A), 00]", buffer.stateToString());

	    // 5. Adding an element when the buffer is full should replace the oldest element.
	    buffer.add(10, (byte)13);
	    assertEquals("[00, 01, 0B, 02, 03, 04, 0C, 05, 06, 0D]", buffer.toString());
	    assertEquals("[01, 0B, 02, 03, 04, 0C, 05, 06, {0D}, (00)]", buffer.stateToString());

	    // 6. Ensure exceptions are thrown for out-of-bounds indices.
	    assertThrows(IndexOutOfBoundsException.class, () -> buffer.add(-1, (byte)14));
	    assertThrows(IndexOutOfBoundsException.class, () -> buffer.add(12, (byte)14));
	}
	
	@Test
	public void testRemoveAtIndex()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(10);
		
		buffer.add(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
		
		assertEquals("[(00), 01, 02, 03, 04, 05, 06, 07, 08, {09}]", buffer.stateToString());
		
		// 1. Remove the element at the start.
		Byte removedValue = buffer.remove(0);
		assertEquals((byte)0, removedValue.byteValue());
		assertEquals("[01, 02, 03, 04, 05, 06, 07, 08, 09]", buffer.toString());
		assertEquals("[00, (01), 02, 03, 04, 05, 06, 07, 08, {09}]", buffer.stateToString());
		
		// 2. Remove an element in the middle.
		removedValue = buffer.remove(4);
		assertEquals((byte)5, removedValue.byteValue());
		assertEquals("[01, 02, 03, 04, 06, 07, 08, 09]", buffer.toString());
		assertEquals("[00, (01), 02, 03, 04, 06, 07, 08, {09}, 09]", buffer.stateToString());
		
		// 3. Remove an element at the end.
		removedValue = buffer.remove(7);
		assertEquals((byte)9, removedValue.byteValue());
		assertEquals("[01, 02, 03, 04, 06, 07, 08]", buffer.toString());
		assertEquals("[00, (01), 02, 03, 04, 06, 07, {08}, 09, 09]", buffer.stateToString());
		
		// 4. Repeatedly remove the last element until the buffer is empty.
		while(buffer.size() > 0)
		{
			int lastIdx = buffer.size() - 1;
			buffer.remove(lastIdx);
		}
		assertTrue(buffer.isEmpty());
		
		assertEquals("[00, 01, 02, 03, 04, 06, 07, 08, 09, 09]", buffer.stateToString());
		
		// 5. Ensure exceptions are thrown for out-of-bounds indices.
		assertThrows(IndexOutOfBoundsException.class, () -> buffer.remove(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> buffer.remove(0));
		
		// 6. Add some elements back and test again.
		buffer.add(new byte[]{10, 11, 12});
		
		assertEquals("[(0A), 0B, {0C}, 03, 04, 06, 07, 08, 09, 09]", buffer.stateToString());
		
		removedValue = buffer.remove(1);
		assertEquals((byte)11, removedValue.byteValue());
		assertEquals("[0A, 0C]", buffer.toString());
		assertEquals("[(0A), {0C}, 0C, 03, 04, 06, 07, 08, 09, 09]", buffer.stateToString());
	}
	
	@Test
	public void testAddAllAtIndex()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(10);
		
		buffer.add(new byte[]{0, 1, 2, 3, 4});
		
		assertEquals("[(00), 01, 02, 03, {04}, 00, 00, 00, 00, 00]", buffer.stateToString());
		
		// 1. Add a collection at the start.
		boolean changed = buffer.addAll(0, Arrays.asList((byte)10, (byte)11));
		assertTrue(changed);
		assertEquals("[0A, 0B, 00, 01, 02, 03, 04]", buffer.toString());
		assertEquals("[00, 01, 02, 03, {04}, 00, 00, 00, (0A), 0B]", buffer.stateToString());
		
		// 2. Add a collection in the middle.
		changed = buffer.addAll(3, Arrays.asList((byte)12, (byte)13));
		assertTrue(changed);
		assertEquals("[0A, 0B, 00, 0C, 0D, 01, 02, 03, 04]", buffer.toString());
		assertEquals("[0D, 01, 02, 03, {04}, 00, (0A), 0B, 00, 0C]", buffer.stateToString());
		
		// 3. Add a collection at the end.
		changed = buffer.addAll(9, Arrays.asList((byte)14, (byte)15));
		assertTrue(changed);
		assertEquals("[0B, 00, 0C, 0D, 01, 02, 03, 04, 0E, 0F]", buffer.toString()); // One element is overwritten due to full capacity.
		assertEquals("[0D, 01, 02, 03, 04, 0E, {0F}, (0B), 00, 0C]", buffer.stateToString());
		
		// 4. Add a collection that causes more elements to be overwritten.
		changed = buffer.addAll(5, Arrays.asList((byte)16, (byte)17, (byte)18, (byte)19));
		assertTrue(changed);
		assertEquals("[01, 10, 11, 12, 13, 02, 03, 04, 0E, 0F]", buffer.toString()); // Some previous elements are overwritten.
		assertEquals("[12, 13, 02, 03, 04, 0E, {0F}, (01), 10, 11]", buffer.stateToString());
		
		// 5. Ensure exceptions are thrown for out-of-bounds indices.
		assertThrows(IndexOutOfBoundsException.class, () -> buffer.addAll(-1, Arrays.asList((byte)20)));
		assertThrows(IndexOutOfBoundsException.class, () -> buffer.addAll(10, Arrays.asList((byte)20)));
		
		// 6. Test adding an empty collection (should return false and not modify the buffer).
		changed = buffer.addAll(3, new ArrayList<Byte>());
		assertFalse(changed);
		assertEquals("[01, 10, 11, 12, 13, 02, 03, 04, 0E, 0F]", buffer.toString());
		assertEquals("[12, 13, 02, 03, 04, 0E, {0F}, (01), 10, 11]", buffer.stateToString());
		
		// 7. Test adding a null collection.
		assertThrows(NullPointerException.class, () -> buffer.addAll(3, null));
	}
	
	@Test
	public void testSubList()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(10);
		
		buffer.add(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
		
		assertEquals("[(00), 01, 02, 03, 04, 05, 06, 07, 08, {09}]", buffer.stateToString());
		
		// 1. Get a sublist from the start.
		CircularByteBuffer sub = buffer.subList(0, 5);
		assertEquals(Arrays.asList((byte)0, (byte)1, (byte)2, (byte)3, (byte)4), sub);
		assertEquals("[(00), 01, 02, 03, {04}]", sub.stateToString());
		
		// 2. Get a sublist from the middle.
		sub = buffer.subList(3, 7);
		assertEquals(Arrays.asList((byte)3, (byte)4, (byte)5, (byte)6), sub);
		assertEquals("[(03), 04, 05, {06}]", sub.stateToString());
		
		// 3. Modify the sublist and ensure the original buffer is unchanged.
		sub.add((byte)10);
		assertEquals("[00, 01, 02, 03, 04, 05, 06, 07, 08, 09]", buffer.toString());
		assertEquals("[(00), 01, 02, 03, 04, 05, 06, 07, 08, {09}]", buffer.stateToString());
		
		// 4. Modify the original buffer and ensure the sublist is unchanged.
		buffer.add(0, (byte)11);
		assertEquals("[(0B), 01, 02, 03, 04, 05, 06, 07, 08, {09}]", buffer.stateToString());
		assertEquals(Arrays.asList((byte)4, (byte)5, (byte)6, (byte)10), sub); // No change.
		
		// 5. Get a sublist from the end.
		sub = buffer.subList(8, 10);
		assertEquals(Arrays.asList((byte)8, (byte)9), sub);
		assertEquals("[(08), {09}]", sub.stateToString());
		
		// 6. Ensure exceptions are thrown for out-of-bounds indices.
		assertThrows(IndexOutOfBoundsException.class, () -> buffer.subList(-1, 5));
		assertThrows(IndexOutOfBoundsException.class, () -> buffer.subList(3, 11));
		
		// 7. Ensure exception is thrown if fromIndex is greater than toIndex.
		assertThrows(IllegalArgumentException.class, () -> buffer.subList(7, 3));
		
		// 8. Clear the sublist and ensure the original buffer is unchanged.
		sub.clear();
		assertEquals("[0B, 01, 02, 03, 04, 05, 06, 07, 08, 09]", buffer.toString());
		assertEquals("[(0B), 01, 02, 03, 04, 05, 06, 07, 08, {09}]", buffer.stateToString());
	}
	
	@Test
	public void testExtractInByteArray()
	{
		CircularByteBuffer buffer = new CircularByteBuffer(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
		
		// 1. Extracting a linear segment from the buffer.
		Byte[] extracted1 = buffer.extractInByteArray(2, 6);
		Byte[] expected1 = new Byte[]{2, 3, 4, 5, 6};
		assertArrayEquals(expected1, extracted1);
		
		// 2. Extracting a segment that wraps around from the end to the start.
		buffer.start = 7;
		buffer.end = 3;
		Byte[] extracted2 = buffer.extractInByteArray(8, 1);
		Byte[] expected2 = new Byte[]{8, 9, 0, 1};
		assertArrayEquals(expected2, extracted2);
		
		// 3. Extracting the entire buffer when it's linear.
		Byte[] extracted3 = buffer.extractInByteArray(0, 9);
		Byte[] expected3 = new Byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
		assertArrayEquals(expected3, extracted3);
		
		// 4. Extracting the entire buffer when it wraps around.
		buffer.start = 8;
		buffer.end = 4;
		Byte[] extracted4 = buffer.extractInByteArray(8, 4);
		Byte[] expected4 = new Byte[]{8, 9, 0, 1, 2, 3, 4};
		assertArrayEquals(expected4, extracted4);
		
		// 5. Extracting a segment with the same start and end index.
		Byte[] extracted5 = buffer.extractInByteArray(5, 5);
		Byte[] expected5 = new Byte[]{5};
		assertArrayEquals(expected5, extracted5);
	}
	
	//************************************ Parameterized Test ************************************//
	
	@ParameterizedTest
	@MethodSource("readExtractAll1HData")
	public void testExtractAll1H(ExtractDto testDTO)
	{
		String endHeader = testDTO.getEndHeader();
		String input = testDTO.getInput();
		String[] expectedOutput = testDTO.getExpectedOutput();
		String expectedRemainingData = testDTO.getExpectedRemainingData();
		
		byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
		
		CircularByteBuffer buffer;
		
		if(inputBytes.length < 1)
		{
			buffer = new CircularByteBuffer(10);
		}
		else
		{
			buffer = new CircularByteBuffer(inputBytes);
		}
		
		List<byte[]> extracted = buffer.extractAll(endHeader);
		
		assertEquals(expectedOutput.length, extracted.size(), testDTO.toString());
		
		for(int i = 0; i < expectedOutput.length; i++)
		{
			String expected = expectedOutput[i];
			String actual = new String(extracted.get(i));
			
			String message = testDTO + expected + " != " + actual;
			
			assertEquals(expected, actual, message);
		}
		
		byte[] remainingBytes = buffer.getData();
		String remaining = new String(remainingBytes);
		String message = testDTO + expectedRemainingData + " != " + remaining;
		
		assertEquals(expectedRemainingData, remaining, message);
	}
	
	@ParameterizedTest
	@MethodSource("readExtractOne1HData")
	public void testExtractOne1H(ExtractDto testDTO)
	{
		String endHeader = testDTO.getEndHeader();
		String input = testDTO.getInput();
		String[] expectedOutput = testDTO.getExpectedOutput();
		String expectedRemainingData = testDTO.getExpectedRemainingData();
		
		byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
		
		CircularByteBuffer buffer;
		
		if(inputBytes.length < 1)
		{
			buffer = new CircularByteBuffer(10);
		}
		else
		{
			buffer = new CircularByteBuffer(inputBytes);
		}
		
		byte[] extracted = buffer.extractOne(endHeader);
		
		assertEquals(1, expectedOutput.length, testDTO.toString());
		
		String expected = expectedOutput[0];
		String actual = new String(extracted);
		String message = testDTO + expected + " != " + actual;
		
		assertEquals(expected, actual, message);
		
		byte[] remainingBytes = buffer.getData();
		String remaining = new String(remainingBytes);
		message = testDTO + expectedRemainingData + " != " + remaining;
		
		assertEquals(expectedRemainingData, remaining, message);
	}
	
	@ParameterizedTest
	@MethodSource("readExtractAll2HData")
	public void testExtractAll2H(ExtractDto testDTO)
	{
		String startHeader = testDTO.getStartHeader();
		String endHeader = testDTO.getEndHeader();
		String input = testDTO.getInput();
		String[] expectedOutput = testDTO.getExpectedOutput();
		String expectedRemainingData = testDTO.getExpectedRemainingData();
		
		byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
		
		CircularByteBuffer buffer;
		
		if(inputBytes.length < 1)
		{
			buffer = new CircularByteBuffer(10);
		}
		else
		{
			buffer = new CircularByteBuffer(inputBytes);
		}
		
		List<byte[]> extracted = buffer.extractAll(startHeader, endHeader);
		
		assertEquals(expectedOutput.length, extracted.size(), testDTO.toString());
		
		for(int i = 0; i < expectedOutput.length; i++)
		{
			String expected = expectedOutput[i];
			String actual = new String(extracted.get(i));
			
			String message = testDTO + expected + " != " + actual;
			
			assertEquals(expected, actual, message);
		}
		
		byte[] remainingBytes = buffer.getData();
		String remaining = new String(remainingBytes);
		String message = testDTO + expectedRemainingData + " != " + remaining;
		
		assertEquals(expectedRemainingData, remaining, message);
	}
	
	@ParameterizedTest
	@MethodSource("readExtractOne2HData")
	public void testExtractOne2H(ExtractDto testDTO)
	{
		String startHeader = testDTO.getStartHeader();
		String endHeader = testDTO.getEndHeader();
		String input = testDTO.getInput();
		String[] expectedOutput = testDTO.getExpectedOutput();
		String expectedRemainingData = testDTO.getExpectedRemainingData();
		
		byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
		
		CircularByteBuffer buffer;
		
		if(inputBytes.length < 1)
		{
			buffer = new CircularByteBuffer(10);
		}
		else
		{
			buffer = new CircularByteBuffer(inputBytes);
		}
		
		byte[] extracted = buffer.extractOne(startHeader, endHeader);
		
		assertEquals(1, expectedOutput.length, testDTO.toString());
		
		String expected = expectedOutput[0];
		String actual = new String(extracted);
		String message = testDTO + expected + " != " + actual;
		
		assertEquals(expected, actual, message);
		
		byte[] remainingBytes = buffer.getData();
		String remaining = new String(remainingBytes);
		message = testDTO + expectedRemainingData + " != " + remaining;
		
		assertEquals(expectedRemainingData, remaining, message);
	}
	
	@ParameterizedTest
	@MethodSource("readCircularTestData")
	public void testCircular(CircularTestDto testDTO)
	{
		String constructorType = testDTO.getConstructorType();
		byte[] constructorArgument = testDTO.getConstructorArgument();
		byte[] dataToAdd = testDTO.getDataToAdd();
		int expectedSize = testDTO.getExpectedSize();
		byte[] expectedData = testDTO.getExpectedData();
		
		CircularByteBuffer buffer = null;
		
		switch(constructorType)
		{
			case "capacity":
			{
				int capacity = constructorArgument[0];
				
				buffer = new CircularByteBuffer(capacity);
				
				break;
			}
			case "byteArray":
			{
				buffer = new CircularByteBuffer(constructorArgument);
				
				break;
			}
			
			default: break;
		}
		
		buffer.add(dataToAdd);
		
		byte[] obtainedData = buffer.getData();
		
		String message = testDTO.toString() + "Obtained: " + Arrays.toString(obtainedData);
		
		assertEquals(expectedSize, obtainedData.length);
		assertArrayEquals(expectedData, obtainedData, message);
	}
	
	//************************************ Get data from JSON ************************************//
	
	private static Stream<Arguments> readExtractAll1HData() throws Exception
	{
		TypeReference<List<ExtractDto>> typeReference = new TypeReference<List<ExtractDto>>(){};
		
		return TestUtils.streamArgumentsFromFiles(typeReference, DATA_DIRECTORY, JSON_FILES_EXTRACT_ALL_1H);
	}
	
	private static Stream<Arguments> readExtractOne1HData() throws Exception
	{
		TypeReference<List<ExtractDto>> typeReference = new TypeReference<>(){};
		
		return TestUtils.streamArgumentsFromFiles(typeReference, DATA_DIRECTORY, JSON_FILES_EXTRACT_ONE_1H);
	}
	
	private static Stream<Arguments> readExtractAll2HData() throws Exception
	{
		TypeReference<List<ExtractDto>> typeReference = new TypeReference<>(){};
		
		return TestUtils.streamArgumentsFromFiles(typeReference, DATA_DIRECTORY, JSON_FILES_EXTRACT_ALL_2H);
	}
	
	private static Stream<Arguments> readExtractOne2HData() throws Exception
	{
		TypeReference<List<ExtractDto>> typeReference = new TypeReference<>(){};
		
		return TestUtils.streamArgumentsFromFiles(typeReference, DATA_DIRECTORY, JSON_FILES_EXTRACT_ONE_2H);
	}
	
	private static Stream<Arguments> readCircularTestData() throws Exception
	{
		TypeReference<List<CircularTestDto>> typeReference = new TypeReference<>(){};
		
		return TestUtils.streamArgumentsFromFiles(typeReference, DATA_DIRECTORY, JSON_FILES_CIRCULAR_BUFFER);
	}
	
	//********************************************************************************************//
}