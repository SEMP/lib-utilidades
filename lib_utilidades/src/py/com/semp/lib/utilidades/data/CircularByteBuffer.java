package py.com.semp.lib.utilidades.data;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;

/**
 * Circular buffer, when the buffer is full, the oldest data is
 * overwritten.
 * 
 * <p>
 * Note: This implementation is not thread-safe by design to favor performance.
 * If used in a multithreaded environment, users are responsible for handling 
 * synchronization externally.
 * </p>
 * 
 * @author Sergio Morel
 */
public class CircularByteBuffer implements Collection<Byte>
{
	/**
	 * Value of index when not referring to a position in the buffer.
	 */
	static final int EMPTY_INDEX = -1;
	
	/**
	 * Index for the first element of the buffer.
	 */
	int start;
	
	/**
	 * Index for the last element of the buffer.
	 */
	int end;
	
	/**
	 * Underlying byte array for the circular buffer.
	 */
	byte[] byteArray;
	
	/**
	 * Constructor that initializes the buffer with a fixed size.
	 * 
	 * @param size
	 * - buffer's size.
	 * @author Sergio Morel
	 */
	public CircularByteBuffer(int size)
	{
		this.setByteArray(new byte[size]);
		
		this.clear();
	}
	
	/**
	 * Constructor that initializes the buffer with initial values.
	 * 
	 * @param byteArray
	 * - array with the initial values.
	 * @author Sergio Morel
	 */
	public CircularByteBuffer(byte... byteArray)
	{
		this.setByteArray(byteArray);
	}
	
	/**
	 * Gets the underlying byte array for the circular byte buffer
	 * @return
	 * - The underlying array
	 * @author Sergio Morel
	 */
	public byte[] getByteArray()
	{
		return byteArray;
	}
	
	/**
	 * Sets the byte array as the underlying storage for the circular buffer.
	 * 
	 * @param byteArray
	 * - The array to use as the data structure for the circular buffer.
	 * @throws NullPointerException
	 * if the provided byteArray is null.
	 * @throws IllegalArgumentException
	 * if the provided byteArray is empty.
	 * @author Sergio Morel
	 */
	public void setByteArray(byte[] byteArray)
	{
		if(byteArray == null)
		{
			String errorMessage = MessageUtil.getMessage(Messages.BUFFER_NOT_NULL_ERROR);
			
			throw new NullPointerException(errorMessage);
		}
		
		if(byteArray.length < 1)
		{
			String errorMessage = MessageUtil.getMessage(Messages.BUFFER_ARRAY_MINIMUM_SIZE);
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.byteArray = byteArray;
		
		this.start = 0;
		this.end = byteArray.length - 1;
	}
	
	@Override
	public Object[] toArray()
	{
		return this.getDataInObjectArray();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] array)
	{
		int dataSize = this.getDataSize();
		
		T[] usedArray = array;
		
		if(array == null)
		{
			Class<T> type = (Class<T>)this.getClass().getComponentType();
			
			usedArray = (T[])Array.newInstance(type, dataSize);
		}
		
		if(array.length < dataSize)
		{
			Class<T> type = (Class<T>)array.getClass().getComponentType();
			
			usedArray = (T[])Array.newInstance(type, dataSize);
		}
		
		int index = 0;
		
		for(Byte data : this)
		{
			usedArray[index++] = (T)data;
		}
		
		return usedArray;
	}
	
	@Override
	public boolean addAll(Collection<? extends Byte> collection)
	{
		if(collection == null || collection.isEmpty())
		{
			return false;
		}
		
		boolean dataAdded = false;
		
		for(Byte data : collection)
		{
			dataAdded |= this.add(data);
		}
		
		return dataAdded;
	}
	
	/**
	 * Adds the contents of the byte array to the buffer.
	 * 
	 * @param bytes
	 * - bytes to be added to the buffer.
	 * @return
	 * <b>true</b> if the bytes are added succesfully.<br>
	 * <b>false</b> if the bytes couldn't be added.
	 * @author Sergio Morel
	 */
	public boolean add(byte... array)
	{
		int copyIndex = 0;
		
		int bufferSize = this.getBufferSize();
		
		if(array.length > bufferSize)
		{
			copyIndex = array.length - bufferSize;
		}
		
		while(copyIndex < array.length)
		{
			if(!this.add(array[copyIndex++]))
			{
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public boolean add(Byte data)
	{
		return this.add(data.byteValue());
	}
	
	/**
	 * Adds a byte to the buffer.
	 * If the buffer is full, the oldest byte will be overwritten.
	 * 
	 * @param data
	 * - byte to be added.
	 * @return true
	 * - if the byte was added successfully, false otherwise.
	 * @author Sergio Morel
	 */
	public boolean add(byte data)
	{
		int size = this.getBufferSize();
		
		if(this.start == EMPTY_INDEX)
		{
			this.start = 0;
			this.end = 0;
		}
		else
		{
			this.end = (this.end + 1) % size;
			
			if(this.start == this.end)
			{
				this.start = (this.start + 1) % size;
			}
		}
		
		this.byteArray[this.end] = data;
		
		return true;
	}
	
	/**
	 * Gets a new array with the data from the circular buffer.<br>
	 * 
	 * @return
	 * - new array with the data from the circular buffer.
	 * @author Sergio Morel
	 */
	public byte[] getData()
	{
		return this.extract(this.start, this.end);
	}
	
	/**
	 * Gets a new array with the data from the circular buffer.<br>
	 * 
	 * @return
	 * - new array with the data from the circular buffer.
	 * @author Sergio Morel
	 */
	public Object[] getDataInObjectArray()
	{
		return extractInObjectArray(this.start, this.end);
	}
	
	/**
	 * Gets a new array with the data from the circular buffer. The data is taken
	 * from a range defined by first index up to the last index in the arguments. (the element in the last
	 * index is included)<br>
	 * 
	 * @param start
	 * - the first index of the range.
	 * @param end
	 * - the last index of the range.
	 * @return
	 * - new array with the data from the range in the circular buffer.
	 * @author Sergio Morel
	 */
	public byte[] getData(int start, int end)
	{
		int dataStart = this.start;
		
		CircularByteBufferIterator iterator = this.iterator();
		
		int startIndex = iterator.forward(start, dataStart);
		int endIndex = iterator.forward(end, dataStart);
		
		if(!this.inRange(startIndex, endIndex))
		{
			String errorMessage = MessageUtil.getMessage(Messages.CIRCULAR_BUFFER_OUT_OF_BOUNDS, start, end, this.getDataSize());
			
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		return extract(startIndex, endIndex);
	}
	
	/**
	 * Indicates if the range defined by the start and end indices is included
	 * inside the range of data of the circular buffer.
	 * 
	 * @param start
	 * - start index.
	 * @param end
	 * - end index.
	 * @return
	 * <b>true</b> if the indices are inside the data range.<br>
	 * <b>false</b> if the indices are not in the data range.
	 * @author Sergio Morel
	 */
	private boolean inRange(int start, int end)
	{
		CircularByteBufferIterator iterator = this.iterator();
		
		return iterator.inRange(start, end);
	}
	
	/**
	 * Calculates the size of the data range defined by the start and end indices.
	 * 
	 * @param start
	 * - The starting index of the data range.
	 * @param end
	 * - The ending index of the data range.
	 * @return
	 * - The size of the data range inside the buffer.
	 * @author Sergio Morel
	 */
	private int getDataSize(int start, int end)
	{
		if(start == EMPTY_INDEX || end == EMPTY_INDEX)
		{
			return 0;
		}
		
		if(end >= start)
		{
			return (end - start) + 1;
		}
		else
		{
			return ((this.getBufferSize() - start) + end) + 1;
		}
	}
	
	/**
	 * Gets the size of the data inside the circular buffer.
	 * 
	 * @return
	 * - size of the data in the circular buffer.
	 * @author Sergio Morel
	 */
	public int getDataSize()
	{
		return this.getDataSize(this.start, this.end);
	}
	
	/**
	 * Gets the size of the underlying buffer.
	 * 
	 * @return
	 * - size of the underlying buffer.
	 * @author Sergio Morel
	 */
	public int getBufferSize()
	{
		return this.byteArray.length;
	}
	
	@Override
	public int size()
	{
		return this.getDataSize();
	}
	
	/**
	 * Clears the data of the buffer.
	 * 
	 * @author Sergio Morel
	 */
	public void clear()
	{
		this.start = EMPTY_INDEX;
		this.end = EMPTY_INDEX;
	}
	
	/**
	 * Verifies if the buffer is empty.
	 * 
	 * @return
	 * <b>true</b> if the buffer is empty.<br>
	 * <b>false</b> if the buffer has any data.
	 * 
	 * @author Sergio Morel
	 */
	public boolean isEmpty()
	{
		return start == EMPTY_INDEX;
	}
	
	@Override
	public boolean contains(Object compareObject)
	{
		if(!(compareObject instanceof Byte))
		{
			return false;
		}
		
		byte compareByte = (Byte)compareObject;
		
		for(byte data : this.byteArray)
		{
			if(data == compareByte)
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean containsAll(Collection<?> collection)
	{
		for(Object compareObject : collection)
		{
			if(!this.contains(compareObject))
			{
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public boolean remove(Object removeObject)
	{
		if(!(removeObject instanceof Byte))
		{
			return false;
		}
		
		CircularByteBufferIterator iterator = this.iterator();
		
		while(iterator.hasNext())
		{
			Byte data = iterator.next();
			
			if(data.equals(removeObject))
			{
				iterator.remove();
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes the first element.
	 * @return
	 * - first element.<br>
	 * - null if the buffer is empty.
	 */
	public Byte removeFirst()
	{
		CircularByteBufferIterator iterator = this.iterator();
		
		return iterator.removeFirst();
	}
	
	/**
	 * Removes the last element.
	 * @return
	 * - last element.<br>
	 * - null if the buffer is empty.
	 */
	public Byte removeLast()
	{
		CircularByteBufferIterator iterator = this.iterator();
		
		return iterator.removeLast();
	}
	
	@Override
	public boolean removeAll(Collection<?> collection)
	{
		if(collection == null || collection.isEmpty())
		{
			return false;
		}
		
		Set<?> efficientSet = (collection instanceof Set) ? (Set<?>)collection : new HashSet<>(collection);
		
		CircularByteBufferIterator iterator = this.iterator();
		
		boolean removed = false;
		
		while(iterator.hasNext())
		{
			Byte data = iterator.next();
			
			if(efficientSet.contains(data))
			{
				iterator.remove();
				
				removed = true;
			}
		}
		
		return removed;
	}
	
	@Override
	public boolean retainAll(Collection<?> collection)
	{
		if(collection == null)
		{
			return false;
		}
		
		boolean wasModified = false;
		
		if(collection.isEmpty())
		{
			wasModified = this.size() > 0;
			
			this.clear();
			
			return wasModified;
		}
		
		Set<?> efficientSet = (collection instanceof Set) ? (Set<?>)collection : new HashSet<>(collection);
		
		CircularByteBufferIterator iterator = this.iterator();
		
		while(iterator.hasNext())
		{
			Byte data = iterator.next();
			
			if(!efficientSet.contains(data))
			{
				iterator.remove();
				
				wasModified = true;
			}
		}
		
		return wasModified;
	}
	
	@Override
	public CircularByteBufferIterator iterator()
	{
		return new CircularByteBufferIterator(this);
	}
	
	/**
	 * Extracts from the buffer the segment contained between the indexes. The
	 * segment includes the content of both indexes.
	 * 
	 * @param start
	 * - start index.
	 * @param end
	 * - end index.
	 * @return
	 * - the extracted segment.
	 * @author Sergio Morel.
	 */
	private byte[] extract(int start, int end)
	{
		int dataSize = this.getDataSize(start, end);
		int bufferSize = this.getBufferSize();
		
		byte[] segment = new byte[dataSize];
		
		int j = 0;
		
		for(int i = start; j < dataSize; i++)
		{
			if(i == bufferSize)
			{
				i = 0;
			}
			
			segment[j++] = this.byteArray[i];
			
			if(i == end)
			{
				break;
			}
		}
		
		return segment;
	}
	
	/**
	 * Extracts from the buffer the segment contained between the indexes into an Object array. The
	 * segment includes the content of both indexes.
	 * 
	 * @param start
	 * - start index.
	 * @param end
	 * - end index.
	 * @return
	 * - An Object array with the extracted segment elements.
	 * @author Sergio Morel.
	 */
	private Object[] extractInObjectArray(int start, int end)
	{
		int dataSize = this.getDataSize(start, end);
		int bufferSize = this.getBufferSize();
		
		Object[] segment = new Object[dataSize];
		
		int j = 0;
		
		for(int i = start; j < dataSize; i++)
		{
			if(i == bufferSize)
			{
				i = 0;
			}
			
			segment[j++] = this.byteArray[i];
			
			if(i == end)
			{
				break;
			}
		}
		
		return segment;
	}
	
	/**
	 * Extracts from the buffer the first segment finalized by the end header.
	 * The segment of data extracted includes the end header.
	 * 
	 * @param endHeader
	 * - The ending header in String format. Converted to bytes using the system's default charset.
	 * @return
	 * - The first segment of data found, including the header.
	 * @author Sergio Morel
	 */
	public byte[] extractOne(String endHeader)
	{
		return this.extractOne(endHeader.getBytes());
	}
	
	/**
	 * Extracts from the buffer the first segment finalized by the end header.
	 * The segment of data extracted includes the end header.
	 * 
	 * @param endHeader
	 * - The ending header.
	 * @return
	 * - The first segment of data found, including the header.
	 * @author Sergio Morel
	 */
	public byte[] extractOne(byte[] endHeader)
	{
		byte[] extraction = new byte[]{};
		
		CircularByteBufferIterator iterator = this.iterator();
		
		while(iterator.hasNext())
		{
			// End header found
			if(iterator.patternFound(endHeader))
			{
				int index = iterator.getIndex();
				
				byte[] message = this.extract(this.start, index);
				
				extraction = message;
				
				this.start = iterator.goNext(index);
				
				break;
			}
			
			iterator.goNext();
		}
		
		return extraction;
	}
	
	/**
	 * Extracts from the buffer all the data segments terminated by an ending header.
	 * Each segment of data extracted includes the end header.
	 * 
	 * @param endHeader
	 * - The ending header.
	 * @return
	 * - A list containing segments of data terminated by the end header, including the header.
	 * @author Sergio Morel
	 */
	public List<byte[]> extractAll(String endHeader)
	{
		return this.extractAll(endHeader.getBytes());
	}
	
	/**
	 * Extracts from the buffer all the data segments terminated by an ending header.
	 * Each segment of data extracted includes the end header.
	 * 
	 * @param endHeader
	 * - The ending header.
	 * @return
	 * - A list containing segments of data terminated by the end header, including the header.
	 * @author Sergio Morel
	 */
	public List<byte[]> extractAll(byte[] endHeader)
	{
		List<byte[]> extraction = new LinkedList<>();
		
		CircularByteBufferIterator iterator = this.iterator();
		
		while(iterator.hasNext())
		{
			// End header found
			if(iterator.patternFound(endHeader))
			{
				int index = iterator.getIndex();
				
				byte[] segment = this.extract(this.start, index);
				
				extraction.add(segment);
				
				this.start = iterator.goNext(index);
			}
			
			iterator.goNext();
		}
		
		return extraction;
	}
	
	/**
	 * Extracts from the buffer the first segment found between occurrences of the start header and end header.
	 * The segment of data extracted includes both the start and end headers.
	 * 
	 * @param startHeader
	 * - The starting header in String format. Converted to bytes using the system's default charset.
	 * @param endHeader
	 * - The ending header in String format. Converted to bytes using the system's default charset.
	 * @return
	 * - The first segment of data found between the headers, including the headers.
	 * @author Sergio Morel
	 */
	public byte[] extractOne(String startHeader, String endHeader)
	{
		return this.extractOne(startHeader.getBytes(), endHeader.getBytes());
	}
	
	/**
	 * Extracts from the buffer the first data segment found between occurrences of the start header and end header.
	 * The segment of data extracted includes both the start and end headers.
	 * 
	 * @param startHeader
	 * - The starting header.
	 * @param endHeader
	 * - The ending header.
	 * @return
	 * - The first segment of data found between the headers, including the headers.
	 * @author Sergio Morel
	 */
	public byte[] extractOne(byte[] startHeader, byte[] endHeader)
	{
		boolean betweenHeaders = false;
		
		CircularByteBufferIterator iterator = this.iterator();
		
		while(iterator.hasNext())
		{
			// Start header found
			if(iterator.patternFound(startHeader))
			{
				int index = iterator.getIndex();
				
				this.start = iterator.rewind(index, startHeader.length - 1);
				
				betweenHeaders = true;
			}
			
			// End header found
			if(iterator.patternFound(endHeader))
			{
				int index = iterator.getIndex();
				
				if(betweenHeaders)
				{
					byte[] segment = this.extract(this.start, index);
					
					this.start = iterator.goNext(index);
					
					return segment;
				}
				
				this.start = iterator.goNext(index);
				
				betweenHeaders = false;
			}
			
			iterator.goNext();
		}
		
		return new byte[]{};
	}
	
	/**
	 * Extracts from the buffer all the data segments found between occurrences of the start header and end header.
	 * Each segment of data extracted includes both the start and end headers.
	 * 
	 * @param startHeader
	 * - The starting header in String format. Converted to bytes using the system's default charset.
	 * @param endHeader
	 * - The ending header in String format. Converted to bytes using the system's default charset.
	 * @return
	 * - A list containing segments of data found between the headers, including the headers.
	 * @author Sergio Morel
	 */
	public List<byte[]> extractAll(String startHeader, String endHeader)
	{
		return this.extractAll(startHeader.getBytes(), endHeader.getBytes());
	}
	
	/**
	 * Extracts from the buffer all the data segments found between occurrences of the start header and end header.
	 * Each segment of data extracted includes both the start and end headers.
	 * 
	 * @param startHeader
	 * - The starting header.
	 * @param endHeader
	 * - The ending header.
	 * @return
	 * - A list containing segments of data found between the headers, including the headers.
	 * @author Sergio Morel
	 */
	public List<byte[]> extractAll(byte[] startHeader, byte[] endHeader)
	{
		List<byte[]> extraction = new LinkedList<>();
		
		boolean betweenHeaders = false;
		
		CircularByteBufferIterator iterator = this.iterator();
		
		while(iterator.hasNext())
		{
			// Start header found
			if(iterator.patternFound(startHeader))
			{
				int index = iterator.getIndex();
				
				this.start = iterator.rewind(index, startHeader.length - 1);
				
				betweenHeaders = true;
			}
			
			// End header found
			if(iterator.patternFound(endHeader))
			{
				int index = iterator.getIndex();
				
				if(betweenHeaders)
				{
					byte[] segment = this.extract(this.start, index);
					
					extraction.add(segment);
				}
				
				this.start = iterator.goNext(index);
				
				betweenHeaders = false;
			}
			
			iterator.goNext();
		}
		
		return extraction;
	}
	
	private String formatValue(byte value)
	{
		return String.format("%02x", value).toUpperCase();
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(object == null)
		{
			return false;
		}
		
		if(this == object)
		{
			return true;
		}
		
		if(!this.getClass().equals(object.getClass()))
		{
			return false;
		}
		
		CircularByteBuffer byteBuffer = (CircularByteBuffer)object;
		
		int dataSize1 = this.getDataSize();
		int dataSize2 = byteBuffer.getDataSize();
		
		if(dataSize1 != dataSize2)
		{
			return false;
		}
		
		CircularByteBufferIterator iterator1 = this.iterator();
		CircularByteBufferIterator iterator2 = byteBuffer.iterator();
		
		while(iterator1.hasNext())
		{
			byte data1 = iterator1.nextByte();
			byte data2 = iterator2.nextByte();
			
			if(data1 != data2)
			{
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public int hashCode()
	{
		byte[] data = this.getData();
		
		return Arrays.hashCode(data);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		
		CircularByteBufferIterator iterator = this.iterator();
		
		while(iterator.hasNext())
		{
			if(!iterator.isFirstIteration())
			{
				sb.append(", ");
			}
			
			sb.append(this.formatValue(iterator.next()));
		}
		
		sb.append("]");
		
		return sb.toString();
	}
}