package py.com.semp.lib.utilidades.data;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import py.com.semp.lib.utilidades.configuration.Values;
import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;

/**
 * Circular buffer, when the buffer is full, the oldest data is
 * overwritten. This implementation does not allow null values.
 * 
 * <p>
 * Note: This implementation is not thread-safe by design to favor performance.
 * If used in a multithreaded environment, users are responsible for handling 
 * synchronization externally.
 * </p>
 * 
 * @author Sergio Morel
 */
public class CircularByteBuffer implements List<Byte>
{
	/**
	 * Value of index when not referring to a position in the buffer.
	 */
	private static final int BUFFER_BOUNDARY = Values.Constants.BUFFER_BOUNDARY;
	
	/**
	 * Index for the first element of the buffer.
	 */
	protected int start;
	
	/**
	 * Index for the last element of the buffer.
	 */
	protected int end;
	
	/**
	 * Underlying byte array for the circular buffer.
	 */
	protected byte[] byteArray;
	
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
		return this.byteArray;
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
	public <T> T[] toArray(T[] array)
	{
		if(array == null)
		{
			String errorMessage = MessageUtil.getMessage(Messages.BUFFER_NOT_NULL_ERROR);
			throw new NullPointerException(errorMessage);
		}
		
		int dataSize = this.getDataSize();
		
		if(array.length < dataSize)
		{
			Class<?> type = array.getClass().getComponentType();
			
			@SuppressWarnings("unchecked")
			T[] newArray = (T[])java.lang.reflect.Array.newInstance(type, dataSize);
			
			array = newArray;
		}
		
		int index = 0;
		
		for(Byte data : this)
		{
			try
			{
				@SuppressWarnings("unchecked")
				T element = (T)data;
				
				array[index++] = element;
			}
			catch(ClassCastException e)
			{
				String errorMessage = MessageUtil.getMessage
				(
					Messages.ELEMENT_ARRAY_TYPE_ERROR,
					array.getClass().getName(),
					data.getClass().getName()
				);
				
				throw new ArrayStoreException(errorMessage);
			}
		}
		
		if(array.length > dataSize)
		{
			array[dataSize] = null;
		}
		
		return array;
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
	public boolean add(byte[] bytes)
	{
		return this.addBytes(bytes, 0,  bytes.length);
	}
	
	/**
	 * Adds a specified portion of the byte array to the buffer.
	 * 
	 * <p>This method attempts to add bytes from the provided array up to the specified
	 * dataSize. If dataSize is larger than the array's length, the method throws
	 * an IndexOutOfBoundsException with a descriptive error message. It starts adding bytes
	 * from the beginning of the array and continues until either dataSize bytes have
	 * been added, or the buffer is full.</p>
	 *
	 * <p>If the buffer does not have enough space to accommodate dataSize bytes,
	 * this method calculates the starting index in the array such that only the last
	 * portion of the array that fits in the buffer is added.</p>
	 *
	 * @param bytes The byte array from which bytes are to be added.
	 * @param dataSize The number of bytes to add from the beginning of the array. 
	 * This value should not exceed the array's length.
	 * @return <b>true</b> if the bytes are added successfully.
	 *         <b>false</b> if any byte cannot be added to the buffer.
	 * @throws IndexOutOfBoundsException
	 * If dataSize is greater than the array's length. The exception message includes
	 * the invalid index and the array's length for reference.
	 * @see #add(byte[])
	 */
	public boolean add(byte[] bytes, int dataSize)
	{
		if(dataSize > bytes.length)
		{
			String errorMessage = MessageUtil.getMessage(Messages.INDEX_OUT_OF_BOUNDS, dataSize, bytes.length);
			
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		return this.addBytes(bytes, 0, dataSize);
	}
	
	/**
	 * Adds a portion of the byte array to the buffer, starting from a specified index up to a specified data size.
	 *
	 * <p>This method adds bytes to the buffer starting at the 'from' index of the byte array, and continues adding
	 * until it reaches the end of the data that has been read, specified by 'dataSize'. This is useful for adding only
	 * a part of a buffer that has been filled up to a certain point.</p>
	 *
	 * <p>If 'from' is beyond the end of the data that has been read into the buffer, or if 'dataSize' exceeds the
	 * actual data length, an IndexOutOfBoundsException is thrown.</p>
	 *
	 * @param bytes The byte array from which bytes are to be added.
	 * @param from The starting index in the array from which to begin adding bytes.
	 * @param dataSize The total number of bytes that have been read into the array.
	 * @return <b>true</b> if the bytes are added successfully, <b>false</b> otherwise.
	 * @throws IndexOutOfBoundsException If 'from' is beyond the data read, or if 'dataSize' exceeds the buffer length.
	 */
	public boolean add(byte[] bytes, int from, int dataSize)
	{
		if(from > dataSize)
		{
			String errorMessage = MessageUtil.getMessage(Messages.INDEX_OUT_OF_BOUNDS, from, dataSize);
			
			throw new IndexOutOfBoundsException(errorMessage);
		}
		else if(dataSize > bytes.length)
		{
			String errorMessage = MessageUtil.getMessage(Messages.INDEX_OUT_OF_BOUNDS, dataSize, bytes.length);
			
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		if(from == dataSize)
		{
			return false;
		}
		
		return this.addBytes(bytes, from, dataSize);
	}

	private boolean addBytes(byte[] bytes, int from, int dataSize)
	{
		int copyIndex = from;
		
		int bufferCapacity = this.getBufferCapacity();
		
		if((dataSize - from) > bufferCapacity)
		{
			copyIndex = dataSize - bufferCapacity;
		}
		
		while(copyIndex < dataSize)
		{
			//TODO ver si se puede optimizar usando una version mas simplificada del add.
			if(!this.add(bytes[copyIndex++]))
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
		int bufferCapacity = this.getBufferCapacity();
		
		if(this.start == BUFFER_BOUNDARY)
		{
			this.start = 0;
			this.end = 0;
		}
		else
		{
			this.end = (this.end + 1) % bufferCapacity;
			
			if(this.start == this.end)
			{
				this.start = (this.start + 1) % bufferCapacity;
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
	public Byte[] getDataInByteArray()
	{
		return extractInByteArray(this.start, this.end);
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
	 * - the first index of the range (inclusive).
	 * @param end
	 * - the last index of the range (inclusive).
	 * @return
	 * - new array with the data from the range in the circular buffer.
	 * @author Sergio Morel
	 */
	public byte[] getData(int start, int end)
	{
		int dataSize = this.getDataSize();
		
		if(start < 0 || start >= dataSize || end < 0 || end >= dataSize || start > end)
		{
			String errorMessage = MessageUtil.getMessage
			(
				Messages.INVALID_INDEX_RANGE_ERROR,
				start,
				end,
				this.getDataSize()
			);
			
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		int dataStart = this.start;
		
		CircularByteBufferIterator iterator = this.iterator();
		
		int startIndex = iterator.forward(start, dataStart);
		int endIndex = iterator.forward(end, dataStart);
		
		if(!this.inRange(startIndex, endIndex))
		{
			String errorMessage = MessageUtil.getMessage
			(
				Messages.INVALID_INDEX_RANGE_ERROR,
				start,
				end,
				this.getDataSize()
			);
			
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
	protected boolean inRange(int start, int end)
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
	protected int getDataSize(int start, int end)
	{
		if(this.isEmpty())
		{
			return 0;
		}
		
		if(end >= start)
		{
			return (end - start) + 1;
		}
		else
		{
			return ((this.getBufferCapacity() - start) + end) + 1;
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
	public int getBufferCapacity()
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
		this.start = BUFFER_BOUNDARY;
		this.end = BUFFER_BOUNDARY;
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
		return this.start == BUFFER_BOUNDARY;
	}
	
	@Override
	public boolean contains(Object compareObject)
	{
		if(!(compareObject instanceof Byte))
		{
			return false;
		}
		
		CircularByteBufferIterator iterator = this.iterator();
		
		byte compareByte = (Byte)compareObject;
		
		while(iterator.hasNext())
		{
			byte data = iterator.nextByte();
			
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
		
		byte removeByte = (Byte)removeObject;
		
		while(iterator.hasNext())
		{
			byte data = iterator.nextByte();
			
			if(data == removeByte)
			{
				iterator.remove();
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes the first element from the buffer.
	 * 
	 * @return
	 * - The first byte data from the buffer.
	 * 
	 * @throws NoSuchElementException
	 * If the buffer is empty.
	 * @author Sergio Morel
	 */
	public Byte removeFirst()
	{
		CircularByteBufferIterator iterator = this.iterator();
		
		return iterator.removeFirst();
	}
	
	/**
	 * Removes the last element from the buffer.
	 * 
	 * @return
	 * - The last byte data from the buffer.
	 * 
	 * @throws NoSuchElementException
	 * If the buffer is empty.
	 * @author Sergio Morel
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
			StringBuilder methodName = new StringBuilder();
			
			methodName.append("boolean ");
			methodName.append(this.getClass().getSimpleName());
			methodName.append("::");
			methodName.append("retainAll(Collection<?>)");
			
			String errorMessage = MessageUtil.getMessage(Messages.NULL_VALUES_NOT_ALLOWED_ERROR, methodName.toString());
			
			throw new NullPointerException(errorMessage);
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
	 * segment includes the content of both indexes. This does not modify the buffer.
	 * 
	 * @param start
	 * - start internal index (inclusive).
	 * @param end
	 * - end internal index (inclusive).
	 * @return
	 * - the extracted segment.
	 * @author Sergio Morel.
	 */
	protected byte[] extract(int start, int end)
	{
		int dataSize = this.getDataSize(start, end);
		int bufferCapacity = this.getBufferCapacity();
		
		byte[] segment = new byte[dataSize];
		
		int j = 0;
		
		for(int i = start; j < dataSize; i++)
		{
			if(i == bufferCapacity)
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
	 * Extracts from the buffer the segment contained between the indexes. The
	 * segment includes the content of both indexes. This does not modify the buffer.
	 * 
	 * @param start
	 * - start internal index (inclusive).
	 * @param end
	 * - end internal index (inclusive).
	 * @return
	 * - the extracted segment.
	 * @author Sergio Morel.
	 */
	protected Byte[] extractInByteArray(int start, int end)
	{
		int dataSize = this.getDataSize(start, end);
		int bufferCapacity = this.getBufferCapacity();
		
		Byte[] segment = new Byte[dataSize];
		
		int j = 0;
		
		for(int i = start; j < dataSize; i++)
		{
			if(i == bufferCapacity)
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
	protected Object[] extractInObjectArray(int start, int end)
	{
		int dataSize = this.getDataSize(start, end);
		int bufferCapacity = this.getBufferCapacity();
		
		Object[] segment = new Object[dataSize];
		
		int j = 0;
		
		for(int i = start; j < dataSize; i++)
		{
			if(i == bufferCapacity)
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
		return this.extractOne(endHeader.getBytes(StandardCharsets.UTF_8));
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
			iterator.goNext();
			
			// End header found
			if(iterator.patternFound(endHeader))
			{
				int index = iterator.getInternalIndex();
				
				byte[] message = this.extract(this.start, index);
				
				extraction = message;
				
				this.start = iterator.goNext(index);
				
				break;
			}
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
		return this.extractAll(endHeader.getBytes(StandardCharsets.UTF_8));
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
			iterator.goNext();
			
			// End header found
			if(iterator.patternFound(endHeader))
			{
				int index = iterator.getInternalIndex();
				
				byte[] segment = this.extract(this.start, index);
				
				extraction.add(segment);
				
				this.start = iterator.goNext(index);
				
				iterator.reset();
			}
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
		return this.extractOne(startHeader.getBytes(StandardCharsets.UTF_8), endHeader.getBytes(StandardCharsets.UTF_8));
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
			iterator.goNext();
			
			// Start header found
			if(iterator.patternFound(startHeader))
			{
				int index = iterator.getInternalIndex();
				
				this.start = iterator.rewind(index, startHeader.length - 1);
				
				betweenHeaders = true;
			}
			
			// End header found
			if(iterator.patternFound(endHeader))
			{
				int index = iterator.getInternalIndex();
				
				if(betweenHeaders)
				{
					byte[] segment = this.extract(this.start, index);
					
					this.start = iterator.goNext(index);
					
					return segment;
				}
				
				this.start = iterator.goNext(index);
				
				betweenHeaders = false;
			}
		}
		
		return new byte[]{};
	}
	
	public byte[] extractOne(String startHeader, String endHeader, int extraBytesAfter)
	{
		return this.extractOne(startHeader.getBytes(StandardCharsets.UTF_8), endHeader.getBytes(StandardCharsets.UTF_8), extraBytesAfter);
	}
	
	/**
	 * Extracts from the buffer the first data segment found between occurrences of the start header and end header plus some extra bytes.
	 * The segment of data extracted includes both the start and the extra bytes after the end header.
	 * 
	 * @param startHeader
	 * - The starting header.
	 * @param endHeader
	 * - The ending header.
	 * @param
	 * - Extra bytes to be included after the end header.
	 * @return
	 * - The first segment of data found between the headers, including the headers and extra bytes.
	 * @author Sergio Morel
	 */
	public byte[] extractOne(byte[] startHeader, byte[] endHeader, int extraBytesAfter)
	{
		boolean betweenHeaders = false;
		
		CircularByteBufferIterator iterator = this.iterator();
		
		while(iterator.hasNext())
		{
			iterator.goNext();
			
			// Start header found
			if(iterator.patternFound(startHeader))
			{
				int index = iterator.getInternalIndex();
				
				this.start = iterator.rewind(index, startHeader.length - 1);
				
				betweenHeaders = true;
			}
			
			// End header found
			if(iterator.patternFound(endHeader) && iterator.hasNext(extraBytesAfter))
			{
				int index = iterator.getInternalIndex();
				
				if(betweenHeaders)
				{
					iterator.forward(extraBytesAfter);
					
					index = iterator.getInternalIndex();
					
					byte[] segment = this.extract(this.start, index);
					
					this.start = iterator.goNext(index);
					
					return segment;
				}
				
				this.start = iterator.goNext(index);
				
				betweenHeaders = false;
			}
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
		return this.extractAll
		(
			startHeader.getBytes(StandardCharsets.UTF_8),
			endHeader.getBytes(StandardCharsets.UTF_8)
		);
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
			iterator.goNext();
			
			// Start header found
			if(iterator.patternFound(startHeader))
			{
				int internalIndex = iterator.getInternalIndex();
				
				this.start = iterator.rewind(internalIndex, startHeader.length - 1);
				
				betweenHeaders = true;
			}
			
			// End header found
			if(iterator.patternFound(endHeader))
			{
				int internalIndex = iterator.getInternalIndex();
				
				if(betweenHeaders)
				{
					byte[] segment = this.extract(this.start, internalIndex);
					
					extraction.add(segment);
				}
				
				this.start = iterator.goNext(internalIndex);
				
				iterator.reset();
				
				betweenHeaders = false;
			}
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
			
			sb.append(this.formatValue(iterator.nextByte()));
		}
		
		sb.append("]");
		
		return sb.toString();
	}
	
	/**
	 * Returns a string representing the circular buffer in its internal state.<br>
	 * - the start index is marked by ()<br>
	 * - the end index is marked by {}
	 * 
	 * @return
	 * - the String corresponding to the state of the buffer.
	 */
	public String stateToString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		
		for(int i = 0; i < this.byteArray.length; i++)
		{
			if(i != 0)
			{
				sb.append(", ");
			}
			
			if(i == this.end)
			{
				sb.append("{");
			}
			
			if(i == this.start)
			{
				sb.append("(");
			}
			
			sb.append(this.formatValue(this.byteArray[i]));
			
			if(i == this.start)
			{
				sb.append(")");
			}
			
			if(i == this.end)
			{
				sb.append("}");
			}
		}
		
		sb.append("]");
		
		return sb.toString();
	}
	
	/**
	 * Returns a string representing the circular buffer in its internal state. This
	 * includes the data size, and the buffer capacity.<br>
	 * - the start index is marked by ()<br>
	 * - the end index is marked by {}
	 * 
	 * @return
	 * - the String corresponding to the state of the buffer.
	 */
	public String stateToStringDetails()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.stateToString());
		
		sb.append(" Size: (").append(this.getDataSize()).append(" / ");
	    sb.append(this.byteArray.length).append(")");
		
		return sb.toString();
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends Byte> collection)
	{
		int bufferCapacity = this.getBufferCapacity();
		
		if(index >= bufferCapacity)
		{
			String errorMessage = MessageUtil.getMessage(Messages.INDEX_OUT_OF_BOUNDS, index, bufferCapacity);
			
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		boolean changed = false;
		
		int size = this.size();
		
		if(collection.size() > 0)
		{
			changed = true;
		}
		
		if(index == 0)
		{
			List<Byte> list = new ArrayList<>(collection);
			
			CircularByteBufferIterator iterator = this.iterator();
			
			for(int i = list.size() - 1; i >= 0; i--)
			{
				Byte value = list.get(i);
				
				iterator.addFirst(value);
			}
			
			return changed;
		}
		
		if(index == size)
		{
			CircularByteBufferIterator iterator = this.iterator();
			
			for(Byte element : collection)
			{
				iterator.addLast(element);
			}
			
			return changed;
		}
		
		CircularByteBufferIterator iterator = this.listIterator(index);
		
		for(Byte element : collection)
		{
			iterator.add(element);
		}
		
		return changed;
	}
	
	@Override
	public Byte get(int index)
	{
		int size = this.size();
		
		this.validateIndex(index, size);
		
		CircularByteBufferIterator iterator = this.iterator();
		
		int internalIndex = iterator.forward(this.start, index);
		
		return this.byteArray[internalIndex];
	}
	
	@Override
	public Byte set(int index, Byte element)
	{
		if(element == null)
		{
			StringBuilder methodName = new StringBuilder();
			
			methodName.append("Byte ");
			methodName.append(this.getClass().getSimpleName());
			methodName.append("::");
			methodName.append("set(int, Byte)");
			
			String errorMessage = MessageUtil.getMessage(Messages.NULL_VALUES_NOT_ALLOWED_ERROR, methodName.toString());
			
			throw new NullPointerException(errorMessage);
		}
		
		int size = this.size();
		
		this.validateIndex(index, size);
		
		CircularByteBufferIterator iterator = this.iterator();
		
		int internalIndex = iterator.forward(index + 1);
		
		byte previousValue = this.byteArray[internalIndex];
		
		this.byteArray[internalIndex] = element;
		
		return previousValue;
	}
	
	@Override
	public void add(int index, Byte element)
	{
		if(element == null)
		{
			StringBuilder methodName = new StringBuilder();
			
			methodName.append("Byte ");
			methodName.append(this.getClass().getSimpleName());
			methodName.append("::");
			methodName.append("set(int, Byte)");
			
			String errorMessage = MessageUtil.getMessage(Messages.NULL_VALUES_NOT_ALLOWED_ERROR, methodName.toString());
			
			throw new NullPointerException(errorMessage);
		}
		
		int size = this.size();
		
		this.validateIndex(index, size + 1);
		
		CircularByteBufferIterator iterator = this.iterator();
		
		if(index == 0)
		{
			iterator.addFirst(element);
			
			return;
		}
		
		if(index == size)
		{
			iterator.addLast(element);
			
			return;
		}
		
		iterator.forward(index);
		
		iterator.add(element);
	}

	@Override
	public Byte remove(int index)
	{
		int size = this.size();
		
		this.validateIndex(index, size);
		
		CircularByteBufferIterator iterator = this.iterator();
		
		if(index == 0)
		{
			Byte removed = this.byteArray[this.start];
			
			iterator.removeFirst();
			
			return removed;
		}
		
		if(index == size - 1)
		{
			Byte removed = this.byteArray[this.end];
			
			iterator.removeLast();
			
			return removed;
		}
		
		iterator.goTo(index + 1);
		
		Byte removed = this.byteArray[iterator.getInternalIndex()];
		
		iterator.remove();
		
		return removed;
	}

	@Override
	public int indexOf(Object o)
	{
		if(!(o instanceof Byte))
		{
			return -1;
		}
		
		CircularByteBufferIterator iterator = this.iterator();
		
		byte compareByte = (Byte)o;
		
		while(iterator.hasNext())
		{
			byte element = iterator.nextByte();
			
			if(element == compareByte)
			{
				return iterator.getInternalIndex();
			}
		}
		
		return -1;
	}

	@Override
	public int lastIndexOf(Object o)
	{
		if(!(o instanceof Byte))
		{
			return -1;
		}
		
		CircularByteBufferIterator iterator = this.iterator();
		
		byte compareByte = (Byte)o;
		
		while(iterator.hasPrevious())
		{
			byte element = iterator.previousByte();
			
			if(element == compareByte)
			{
				return iterator.getInternalIndex();
			}
		}
		
		return -1;
	}
	
	@Override
	public CircularByteBufferIterator listIterator()
	{
		return this.iterator();
	}
	
	@Override
	public CircularByteBufferIterator listIterator(int index)
	{
		int size = this.size();
		
		this.validateIndex(index, size);
		
		CircularByteBufferIterator iterator = this.iterator();
		
		iterator.goTo(index);
		
		return iterator;
	}
	
	/**
	 * Returns a view of the portion of this list between the specified {@code fromIndex}, inclusive,
	 * and {@code toIndex}, exclusive. This method deviates from the typical Java {@code List} sublist
	 * behavior in an important way: it returns a <em>deep copy</em> of the sublist, rather than a view on 
	 * the original list.
	 * 
	 * <p>Therefore, any modifications made to the returned sublist won't affect this original list, 
	 * and vice-versa. This deviation is intentional to avoid complexities associated with maintaining
	 * a sublist view on a circular buffer.</p>
	 * 
	 * <p>Examples:</p>
	 * <ul>
	 *     <li>If you modify the returned sublist using {@code add}, {@code remove}, or any other modification
	 *     operations, the original {@code CircularByteBufferList} remains unchanged.</li>
	 *     <li>Clearing the returned sublist using {@code clear()} will empty the sublist, but the 
	 *     original {@code CircularByteBufferList} remains unaffected.</li>
	 * </ul>
	 *
	 * @param fromIndex
	 * - low endpoint (inclusive) of the subList
	 * @param toIndex
	 * - high endpoint (exclusive) of the subList
	 * @return
	 * - a deep copy of the specified range within this list
	 * @throws IndexOutOfBoundsException
	 * if the {@code fromIndex} or {@code toIndex} are out of range.
	 * @throws IllegalArgumentException
	 * if {@code fromIndex} is greater than {@code toIndex}
	 */
	@Override
	public CircularByteBuffer subList(int fromIndex, int toIndex)
	{
		int size = this.size();
		
		this.validateIndex(fromIndex, size);
		this.validateIndex(toIndex, size + 1);
		
		if(fromIndex > toIndex)
		{
			String errorMessage = MessageUtil.getMessage
			(
				Messages.INVALID_INDEX_RANGE_ERROR,
				fromIndex,
				toIndex,
				size
			);
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		CircularByteBufferIterator iterator = this.iterator();
		
		int start = iterator.forward(this.start, fromIndex);
		int end = iterator.forward(this.start, toIndex);
		
		end = iterator.goPrevious(end);
		
		byte[] extraction = this.extract(start, end);
		
		return new CircularByteBuffer(extraction);
	}
	
	private void validateIndex(int index, int size)
	{
		if(index < 0 || index >= size)
		{
			String errorMessage = MessageUtil.getMessage(Messages.INDEX_OUT_OF_BOUNDS, index, size);
			
			throw new IndexOutOfBoundsException(errorMessage);
		}
	}
}