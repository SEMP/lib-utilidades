package py.com.semp.lib.utilidades.data;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;
import py.com.semp.lib.utilidades.utilities.Utilities;

/**
 * Circular buffer, when the buffer is full, the oldest data is
 * overwritten.
 * 
 * @author Sergio Morel
 */
public class CircularByteBuffer implements Collection<Byte>
{
	private static final int EMPTY_INDEX = -1;
	
	private int start;
	private int end;
	private byte[] byteArray;
	
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
	
	private int getStart()
	{
		return start;
	}
	
	private void setStart(int start)
	{
		this.start = start;
	}
	
	private int getEnd()
	{
		return end;
	}
	
	private void setEnd(int end)
	{
		this.end = end;
	}
	
	private byte[] getByteArray()
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
		
		this.setStart(0);
		this.setEnd(byteArray.length - 1);
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
			Class<T> type = (Class<T>) this.getClass().getComponentType();
			
			usedArray = (T[])Array.newInstance(type, dataSize);
		}
		
		if(array.length < dataSize)
		{
			Class<T> type = (Class<T>) array.getClass().getComponentType();
			
			usedArray = (T[])Array.newInstance(type, dataSize);
		}
		
		int index = 0;
		
		for(Byte data: this)
		{
			usedArray[index++] = (T) data;
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
			this.end++;
			
			if(this.end == size)
			{
				this.end = 0;
			}
			
			if(this.start == this.end)
			{
				this.start++;
				
				if(this.start == size)
				{
					this.start = 0;
				}
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
		int dataStart = this.getStart();
		int dataEnd = this.getEnd();
		
		return this.extract(dataStart, dataEnd);
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
		int dataStart = this.getStart();
		int dataEnd = this.getEnd();
		
		return extractInObjectArray(dataStart, dataEnd);
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
		int dataStart = this.getStart();
		
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
		int dataStart = this.getStart();
		int dataEnd = this.getEnd();
		
		return this.getDataSize(dataStart, dataEnd);
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
		for(Byte data : this)
		{
			if(Utilities.equals(data, compareObject))
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
	
	/**
	 * Extrae del buffer los datos que se encuentran entre las cabeceras
	 * iniciales y finales. La extracci&oacute;n incluye las cabeceras.
	 * @param startHeader
	 * cabecera inicial.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * lista de datos que se encuentran entre las cabeceras.
	 */
	public List<byte[]> extract(String startHeader, String endHeader)
	{
		return this.extract(startHeader.getBytes(), endHeader.getBytes());
	}
	
	/**
	 * Extrae del buffer los datos que se encuentran entre las cabeceras
	 * iniciales y finales. La extracci&oacute;n incluye las cabeceras.
	 * @param startHeader
	 * cabecera inicial.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * lista de datos que se encuentran entre las cabeceras.
	 */
	public List<byte[]> extract(byte[] startHeader, byte[] endHeader)
	{
		List<byte[]> extraction = new LinkedList<>();
		
		boolean insideMessage = false;
		
		CircularByteBufferIterator iterator = this.iterator();
		
		while(iterator.hasNext())
		{
			//Cabecera inicial encontrada
			if(iterator.patternFound(startHeader))
			{
				int index = iterator.getIndex();
				
				this.start = iterator.rewind(index, startHeader.length - 1);
				
				insideMessage = true;
			}
			
			//Cabecera final encontrada
			if(iterator.patternFound(endHeader))
			{
				int index = iterator.getIndex();
				
				if(insideMessage)
				{
					byte[] message = this.extract(this.start, index);
					
					extraction.add(message);
				}
				
				this.start = iterator.goNext(index);
				
				insideMessage = false;
			}
			
			iterator.goNext();
		}
		
		return extraction;
	}
	
	/**
	 * Extrae del buffer los primeros datos que se encuentran entre las cabeceras
	 * iniciales y finales. La extracci&oacute;n incluye las cabeceras.
	 * @param startHeader
	 * cabecera inicial.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * lista de datos que se encuentran entre las cabeceras.
	 */
	public byte[] extractOne(byte[] startHeader, byte[] endHeader)
	{
		byte[] extraction = new byte[] {};
		
		boolean insideMessage = false;
		
		CircularByteBufferIterator iterator = this.iterator();
		
		while(iterator.hasNext())
		{
			//Cabecera inicial encontrada
			if(iterator.patternFound(startHeader))
			{
				int index = iterator.getIndex();
				
				this.start = iterator.rewind(index, startHeader.length - 1);
				
				insideMessage = true;
			}
			
			//Cabecera final encontrada
			if(iterator.patternFound(endHeader))
			{
				int index = iterator.getIndex();
				
				if(insideMessage)
				{
					byte[] message = this.extract(this.start, index);
					
					extraction = message;
				}
				
				this.start = iterator.goNext(index);
				
				insideMessage = false;
				
				break;
			}
			
			iterator.goNext();
		}
		
		return extraction;
	}
	
	/**
	 * Extrae del buffer los primeros datos que se encuentran entre las cabeceras
	 * iniciales y finales. La extracci&oacute;n incluye las cabeceras.
	 * @param startHeader
	 * cabecera inicial.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * lista de datos que se encuentran entre las cabeceras.
	 */
	public byte[] extractOne(String startHeader, String endHeader)
	{
		return this.extractOne(startHeader.getBytes(), endHeader.getBytes());
	}
	
	/**
	 * Obtiene un segmento del array de bytes delimitado
	 * por los par&aacute;metros.<br>
	 * 
	 * @param start
	 * posici&oacute;n inicial.
	 * @param end
	 * posici&oacute;n final.
	 * @return
	 * array de bytes con los datos entre los l&iacute;mites.
	 */
	public byte[] extract(int start, int end)
	{
		int dataSize = this.getDataSize(start, end);
		int bufferSize = this.getBufferSize();
		
		byte[] segment = new byte[dataSize];
		
		int j = 0;
		
		for(int i = start; j < dataSize ; i++)
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
	
	private Object[] extractInObjectArray(int start, int end)
	{
		int dataSize = this.getDataSize(start, end);
		int bufferSize = this.getBufferSize();
		
		Object[] segment = new Object[dataSize];
		
		int j = 0;
		
		for(int i = start; j < dataSize ; i++)
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
	 * Extrae del buffer los datos que se encuentran finalizados por
	 * una cabecera final.
	 * La extracci&oacute;n incluye la cabecera final.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * lista de datos extraidos.
	 */
	public List<byte[]> extract(String endHeader)
	{
		return this.extract(endHeader.getBytes());
	}
	
	/**
	 * Extrae del buffer los datos que se encuentran finalizados por
	 * una cabecera final.
	 * La extracci&oacute;n incluye la cabecera final.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * lista de datos extraidos.
	 */
	public List<byte[]> extract(byte[] endHeader)
	{
		List<byte[]> extraction = new LinkedList<>();
		
		CircularByteBufferIterator iterator = this.iterator();
		
		while(iterator.hasNext())
		{
			//Cabecera final encontrada
			if(iterator.patternFound(endHeader))
			{
				int index = iterator.getIndex();
				
				byte[] message = this.extract(this.start, index);
				
				extraction.add(message);
				
				this.start = iterator.goNext(index);
			}
			
			iterator.goNext();
		}
		
		return extraction;
	}
	
	/**
	 * Extrae del buffer los primeros datos que se encuentran finalizados por
	 * una cabecera final.
	 * La extracci&oacute;n incluye la cabecera final.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * datos extraidos.
	 */
	public byte[] extractOne(String endHeader)
	{
		return this.extractOne(endHeader.getBytes());
	}
	
	/**
	 * Extrae del buffer los primeros datos que se encuentran finalizados por
	 * una cabecera final.
	 * La extracci&oacute;n incluye la cabecera final.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * datos extraidos.
	 */
	public byte[] extractOne(byte[] endHeader)
	{
		byte[] extraction = new byte[]{};
		
		CircularByteBufferIterator iterator = this.iterator();
		
		while(iterator.hasNext())
		{
			//Cabecera final encontrada
			if(iterator.patternFound(endHeader))
			{
				int index = iterator.getIndex();
				
				byte[] message = this.extract(this.start, index);
				
				extraction =message;
				
				this.start = iterator.goNext(index);
				
				break;
			}
			
			iterator.goNext();
		}
		
		return extraction;
	}
	
	/**
	 * Removers the first element.
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
	 * Removers the last element.
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
	public boolean remove(Object removeObject)
	{
		CircularByteBufferIterator iterator = this.iterator();
		
		while(iterator.hasNext())
		{
			Byte data = iterator.next();
			
			if(Utilities.equals(data, removeObject))
			{
				iterator.remove();
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean removeAll(Collection<?> collection)
	{
		CircularByteBufferIterator iterator = this.iterator();
		
		boolean removed = false;
		
		while(iterator.hasNext())
		{
			Byte data = iterator.next();
			
			if(collection.contains(data))
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
		CircularByteBufferIterator iterator = this.iterator();
		
		boolean removed = false;
		
		while(iterator.hasNext())
		{
			Byte data = iterator.next();
			
			if(!collection.contains(data))
			{
				iterator.remove();
				
				removed = true;
			}
		}
		
		return removed;
	}
	
	/**
	 * Extrae los datos que se encuentran entre las cabeceras
	 * iniciales y finales. La extracci&oacute;n incluye las cabeceras.
	 * 
	 * @param data
	 * array de bytes de donde se desea extraer los datos.
	 * @param startHeader
	 * cabecera inicial.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * lista de datos que se encuentran entre las cabeceras.
	 */
	public static List<byte[]> extractData(byte[] data, String startHeader, String endHeader)
	{
		return extractData(data, startHeader.getBytes(), endHeader.getBytes());
	}
	
	/**
	 * Extrae los datos que se encuentran entre las cabeceras
	 * iniciales y finales. La extracci&oacute;n incluye las cabeceras.
	 * 
	 * @param data
	 * array de bytes de donde se desea extraer los datos.
	 * @param startHeader
	 * cabecera inicial.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * Datos que se encuentran entre las cabeceras.
	 */
	public static byte[] extractOneData(byte[] data, String startHeader, String endHeader)
	{
		return extractOneData(data, startHeader.getBytes(), endHeader.getBytes());
	}
	
	/**
	 * Extrae los datos que se encuentran entre las cabeceras
	 * iniciales y finales. La extracci&oacute;n incluye las cabeceras.
	 * 
	 * @param data
	 * array de bytes de donde se desea extraer los datos.
	 * @param startHeader
	 * cabecera inicial.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * lista de datos que se encuentran entre las cabeceras.
	 */
	public static List<byte[]> extractData(byte[] data, byte[] startHeader, byte[] endHeader)
	{
		CircularByteBuffer circularByteBuffer = new CircularByteBuffer(data);
		
		return circularByteBuffer.extract(startHeader, endHeader);
	}
	
	/**
	 * Extrae los datos que se encuentran entre las cabeceras
	 * iniciales y finales. La extracci&oacute;n incluye las cabeceras.
	 * 
	 * @param data
	 * array de bytes de donde se desea extraer los datos.
	 * @param startHeader
	 * cabecera inicial.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * Datos que se encuentran entre las cabeceras.
	 */
	public static byte[] extractOneData(byte[] data, byte[] startHeader, byte[] endHeader)
	{
		CircularByteBuffer circularByteBuffer = new CircularByteBuffer(data);
		
		return circularByteBuffer.extractOne(startHeader, endHeader);
	}
	
	/**
	 * Extrae los datos que se encuentran finalizados por
	 * una cabecera final.
	 * 
	 * @param data
	 * array de bytes de donde se desea extraer los datos.
	 * La extracci&oacute;n incluye la cabecera final.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * lista de datos extraidos.
	 */
	public static List<byte[]> extractData(byte[] data, String endHeader)
	{
		return extractData(data, endHeader.getBytes());
	}
	
	/**
	 * Extrae los datos que se encuentran finalizados por
	 * una cabecera final.
	 * 
	 * @param data
	 * array de bytes de donde se desea extraer los datos.
	 * La extracci&oacute;n incluye la cabecera final.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * lista de datos extraidos.
	 */
	public static List<byte[]> extractData(byte[] data, byte[] endHeader)
	{
		CircularByteBuffer circularByteBuffer = new CircularByteBuffer(data);
		
		return circularByteBuffer.extract(endHeader);
	}
	
	/**
	 * Extrae los primeros datos que se encuentran finalizados por
	 * una cabecera final.
	 * 
	 * @param data
	 * array de bytes de donde se desea extraer los datos.
	 * La extracci&oacute;n incluye la cabecera final.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * datos extraidos.
	 */
	public static byte[] extractOneData(byte[] data, String endHeader)
	{
		return extractOneData(data, endHeader.getBytes());
	}
	
	/**
	 * Extrae los primeros datos que se encuentran finalizados por
	 * una cabecera final.
	 * 
	 * @param data
	 * array de bytes de donde se desea extraer los datos.
	 * La extracci&oacute;n incluye la cabecera final.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * datos extraidos.
	 */
	public static byte[] extractOneData(byte[] data, byte[] endHeader)
	{
		CircularByteBuffer circularByteBuffer = new CircularByteBuffer(data);
		
		return circularByteBuffer.extractOne(endHeader);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
		{
			return false;
		}
		
		if(this == obj)
		{
			return true;
		}
		
		if(!this.getClass().equals(obj.getClass()))
		{
			return false;
		}
		
		CircularByteBuffer byteBuffer = (CircularByteBuffer) obj;
		
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
			Byte data1 = iterator1.next();
			Byte data2 = iterator2.next();
			
			if(!data1.equals(data2))
			{
				return false;
			}
		}
		
		return true;
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
	
	private String formatValue(byte value)
	{
		return String.format("%02x", value).toUpperCase();
	}
	
	@Override
	public CircularByteBufferIterator iterator()
	{
		return new CircularByteBufferIterator(this);
	}
	
	public static class CircularByteBufferIterator implements Iterator<Byte>
	{
		private CircularByteBuffer buffer;
		private int index;
		
		public CircularByteBufferIterator(CircularByteBuffer buffer)
		{
			this.buffer = buffer;
			this.index = buffer.getStart();
		}
		
		public int getIndex()
		{
			return index;
		}
		
		public void setIndex(int index)
		{
			this.index = index;
		}
		
		public boolean isFirstIteration()
		{
			int start = this.buffer.getStart();
			
			return this.index == start;
		}
		
		public boolean inRange(int start, int end)
		{
			boolean fi =  this.buffer.end >= this.buffer.start;
			boolean es = end >= start;
			boolean si = start >= this.buffer.start;
			boolean fe = this.buffer.end >= end;
			
			if(fi == es)
			{
				return si && fe;
			}
			
			if(!es)
			{
				return false;
			}
			
			boolean ei = end >= this.buffer.start;
			boolean fs = this.buffer.end >= start;
			
			return (si && ei) || (fs && fe);
		}
		
		public boolean patternFound(byte[] pattern)
		{
			byte[] byteArray = this.buffer.byteArray;
			
			int index = this.index;
			int i = pattern.length - 1;
			
			while(i >= 0 && index != EMPTY_INDEX)
			{
				if(byteArray[index] != pattern[i])
				{
					return false;
				}
				
				index = this.goPrevious(index);
				i--;
			}
			
			if(i == EMPTY_INDEX)
			{
				return true;
			}
			
			return false;
		}
		
		@Override
		public boolean hasNext()
		{
			return index != EMPTY_INDEX;
		}
		
		@Override
		public Byte next()
		{
			if(this.index == EMPTY_INDEX)
			{
				return null;
			}
			
			byte[] byteArray = this.buffer.getByteArray();
			
			byte data = byteArray[this.index];
			
			this.goNext();
			
			return data;
		}
		
		public int goNext()
		{
			if(this.index == this.buffer.getEnd())
			{
				return this.index = EMPTY_INDEX;
			}
			
			if(this.index == EMPTY_INDEX)
			{
				return this.index = this.buffer.getStart();
			}
			
			this.index++;
			
			if(this.index == this.buffer.getBufferSize())
			{
				this.index = 0;
			}
			
			return this.index;
		}
		
		public int goNext(int index)
		{
			if(index == this.buffer.getEnd())
			{
				return EMPTY_INDEX;
			}
			
			if(index == EMPTY_INDEX)
			{
				return this.buffer.getStart();
			}
			
			index++;
			
			if(index == this.buffer.getBufferSize())
			{
				index = 0;
			}
			
			return index;
		}
		
		public int goPrevious()
		{
			if(this.index == this.buffer.getStart())
			{
				return this.index = EMPTY_INDEX;
			}
			
			if(this.index == EMPTY_INDEX)
			{
				return this.index = this.buffer.getEnd();
			}
			
			this.index--;
			
			if(this.index < 0)
			{
				this.index = this.buffer.getBufferSize() - 1;
			}
			
			return this.index;
		}
		
		public int goPrevious(int index)
		{
			if(index == this.buffer.getStart())
			{
				return EMPTY_INDEX;
			}
			
			if(index == EMPTY_INDEX)
			{
				return this.buffer.getEnd();
			}
			
			index--;
			
			if(index < 0)
			{
				index = this.buffer.getBufferSize() - 1;
			}
			
			return index;
		}
		
		public int forward(int steps)
		{
			if(this.index == EMPTY_INDEX)
			{
				this.index = 0;
			}
			
			int bufferSize = this.buffer.getBufferSize();
			
			this.index += steps % bufferSize;
			
			if(this.index >= bufferSize)
			{
				this.index -= bufferSize;
			}
			
			return this.index;
		}
		
		public int forward(int index, int steps)
		{
			if(index == EMPTY_INDEX)
			{
				index = 0;
			}
			
			int bufferSize = this.buffer.getBufferSize();
			
			index += steps % bufferSize;
			
			if(index >= bufferSize)
			{
				index -= bufferSize;
			}
			
			return index;
		}
		
		public int rewind(int steps)
		{
			if(this.index == EMPTY_INDEX)
			{
				this.index = 0;
			}
			
			int bufferSize = this.buffer.getBufferSize();
			
			this.index -= steps % bufferSize;
			
			if(this.index < 0)
			{
				this.index += bufferSize;
			}
			
			return this.index;
		}
		
		public int rewind(int index, int steps)
		{
			if(index == EMPTY_INDEX)
			{
				index = 0;
			}
			
			int bufferSize = this.buffer.getBufferSize();
			
			index -= steps % bufferSize;
			
			if(index < 0)
			{
				index += bufferSize;
			}
			
			return index;
		}
		
		/**
		 * Removers the first element.
		 * @return
		 * - first element.<br>
		 * - null if the buffer is empty.
		 */
		public Byte removeFirst()
		{
			int dataStart = this.buffer.getStart();
			int dataEnd = this.buffer.getEnd();
			byte[] byteArray = this.buffer.getByteArray();
			
			if(dataStart == EMPTY_INDEX)
			{
				return null;
			}
			
			byte data = byteArray[dataStart];
			
			if(dataStart == dataEnd)
			{
				this.buffer.clear();
			}
			else
			{
				this.buffer.start = this.goNext(dataStart);
			}
			
			return data;
		}
		
		/**
		 * Removers the last element.
		 * @return
		 * - last element.<br>
		 * - null if the buffer is empty.
		 */
		public Byte removeLast()
		{
			int dataStart = this.buffer.getStart();
			int dataEnd = this.buffer.getEnd();
			byte[] byteArray = this.buffer.getByteArray();
			
			if(dataStart == EMPTY_INDEX)
			{
				return null;
			}
			
			byte data = byteArray[dataEnd];
			
			if(dataStart == dataEnd)
			{
				this.buffer.clear();
			}
			else
			{
				this.buffer.end = this.goPrevious(dataEnd);
			}
			
			return data;
		}
		
		@Override
		public void remove()
		{
			int dataStart = this.buffer.start;
			int dataEnd = this.buffer.end;
			int removeIndex = this.goPrevious(this.index);
			
			if(dataStart == EMPTY_INDEX)
			{
				return;
			}
			
			if(removeIndex == dataStart)
			{
				this.removeFirst();
				
				return;
			}
			
			if(removeIndex == dataEnd)
			{
				this.removeLast();
				
				return;
			}
			
			int copyFromIndex = this.goNext(removeIndex);
			int copyToIndex = removeIndex;
			
			while(copyFromIndex != EMPTY_INDEX)
			{
				this.buffer.byteArray[copyToIndex] = this.buffer.byteArray[copyFromIndex];
				
				copyToIndex = this.goNext(copyToIndex);
				copyFromIndex = this.goNext(copyFromIndex);
			}
			
			this.buffer.end = this.goPrevious(copyToIndex);
			
			this.goPrevious();
		}
		
		public void remove(int start, int end)
		{
			int dataStart = this.buffer.start;
			int dataEnd = this.buffer.end;
			
			if(!this.inRange(start, end))
			{
				StringBuilder sb = new StringBuilder();
				
				sb.append("Rango fuera de limites:\n");
				sb.append("Rango: (").append(start);
				sb.append(", ").append(end).append(")\n");
				sb.append("Data: (").append(dataStart);
				sb.append(", ").append(dataEnd).append(")");
				
				throw new IndexOutOfBoundsException(sb.toString());
			}
			
			if(dataStart == EMPTY_INDEX)
			{
				return;
			}
			
			if(start == dataStart)
			{
				this.buffer.start = this.goNext(end);
				
				if(this.buffer.start == EMPTY_INDEX)
				{
					this.buffer.end = EMPTY_INDEX;
				}
				
				return;
			}
			
			if(end == dataEnd)
			{
				this.buffer.end = this.goPrevious(start);
				
				if(this.buffer.end == EMPTY_INDEX)
				{
					this.buffer.start = EMPTY_INDEX;
				}
				
				return;
			}
			
			int copyFromIndex = this.goNext(end);
			int copyToIndex = start;
			
			while(copyFromIndex != EMPTY_INDEX)
			{
				this.buffer.byteArray[copyToIndex] = this.buffer.byteArray[copyFromIndex];
				
				copyToIndex = this.goNext(copyToIndex);
				copyFromIndex = this.goNext(copyFromIndex);
			}
			
			this.buffer.end = this.goPrevious(copyToIndex);
		}
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append(this.index);
			
			return sb.toString();
		}
	}
}