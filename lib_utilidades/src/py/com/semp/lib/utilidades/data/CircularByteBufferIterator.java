package py.com.semp.lib.utilidades.data;

import java.util.Iterator;
import java.util.NoSuchElementException;

import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;

/**
 * Iterator for {@link CircularByteBuffer}
 * 
 * @author Sergio Morel
 */
public class CircularByteBufferIterator implements Iterator<Byte>
{
	/**
	 * Value of index when not referring to a position in the buffer.
	 */
	private static final int EMPTY_INDEX = CircularByteBuffer.EMPTY_INDEX;
	
	/**
	 * Buffer to be iterated.
	 */
	private CircularByteBuffer buffer;
	
	/**
	 * Index for the current position.
	 */
	private int index;
	
	/**
	 * Constructor with argument for the {@link CircularByteBuffer}.
	 * 
	 * @param buffer
	 * - buffer to be iterated.
	 * @author Sergio Morel
	 */
	public CircularByteBufferIterator(CircularByteBuffer buffer)
	{
		super();
		
		this.buffer = buffer;
		this.index = buffer.start;
	}
	
	/**
	 * Returns the index for the current position.
	 * 
	 * @return
	 * - the index for the current position.
	 */
	public int getIndex()
	{
		return index;
	}
	
	/**
	 * Determines if the current iteration is the first by comparing the index with the buffer's start.
	 * 
	 * @return
	 * - <b>true</b> if the current index matches the buffer's start position,
	 * indicating the first iteration.<br>
	 * - <b>false</b> otherwise.
	 * @author Sergio Morel
	 */
	public boolean isFirstIteration()
	{
		return this.index == this.buffer.start;
	}
	
	/**
	 * Checks if the given range, from start to end, falls within the buffer's range.
	 * <p>
	 * This method is designed to handle cases where the buffer is circular, 
	 * meaning that data might wrap around from the end back to the start of the buffer.
	 * </p>
	 * 
	 * @param start
	 * - The starting index of the range to check.
	 * @param end
	 * - The ending index of the range to check.
	 * 
	 * @return
	 * - <b>true</b> if the entire range from start to end is within the buffer's range.<br>
	 * - <b>false</b> otherwise.
	 * @author Sergio Morel
	 */
	public boolean inRange(int start, int end)
	{
		boolean fi = this.buffer.end >= this.buffer.start;
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
	
	/**
	 * Checks if the given pattern exists immediately preceding the current index in the buffer.
	 * <p>
	 * This method looks backward from the current index to determine if the pattern is present.
	 * The search is done in reverse order, starting from the end of the pattern towards its beginning,
	 * and it stops as soon as a mismatch is found or the pattern is fully matched.
	 * </p>
	 *
	 * @param pattern The byte array pattern to search for within the buffer.
	 * 
	 * @return
	 * - <b>true</b> if the entire pattern is found immediately preceding the current index.<br>
	 * - <b>false</b> otherwise or if the pattern is not fully matched.
	 */
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
		
		if(i < 0)
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
			String errorMessage = MessageUtil.getMessage(Messages.BUFFER_EMPTY_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		byte[] byteArray = this.buffer.byteArray;
		
		byte data = byteArray[this.index];
		
		this.goNext();
		
		return data;
	}
	
	/**
	 * Retrieves the next byte from the buffer.
	 * 
	 * @return
	 * - the next byte in the buffer.
	 * @throws NoSuchElementException
	 * if the buffer is empty.
	 * @author Sergio Morel
	 */
	public byte nextByte()
	{
		if(this.index == EMPTY_INDEX)
		{
			String errorMessage = MessageUtil.getMessage(Messages.BUFFER_EMPTY_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		byte[] byteArray = this.buffer.byteArray;
		
		byte data = byteArray[this.index];
		
		this.goNext();
		
		return data;
	}
	
	/**
	 * Updates the current index to point to the next position in the buffer.
	 * 
	 * @return The updated index after the operation.<br>
	 * - If the current index is at the end of the buffer, the index is set to EMPTY_INDEX.<br>
	 * - If the current index is EMPTY_INDEX, the index is reset to the start of the buffer.<br>
	 * - In other cases, the index is incremented and wrapped around if it exceeds the buffer size.<br>
	 * @author Sergio Morel
	 */
	public int goNext()
	{
		if(this.index == this.buffer.end)
		{
			return this.index = EMPTY_INDEX;
		}
		
		if(this.index == EMPTY_INDEX)
		{
			return this.index = this.buffer.start;
		}
		
		this.index = (this.index + 1) % this.buffer.getBufferSize();
		
		return this.index;
	}
	
	/**
	 * Moves to the next position in the buffer from a given index. It doesn't change
	 * the iterator's index value.
	 * 
	 * @param index
	 * - The index from which to move to the next position.
	 * @return The updated index after the operation.<br>
	 * - If the provided index is at the end of the buffer, the returned index is EMPTY_INDEX.<br>
	 * - If the provided index is EMPTY_INDEX, the returned index is set to the start of the buffer.<br>
	 * - For other values of the provided index, it is incremented, and if it exceeds the buffer size, 
	 *   it wraps around to the start.
	 *   @author Sergio Morel
	 */
	public int goNext(int index)
	{
		if(index == this.buffer.end)
		{
			return EMPTY_INDEX;
		}
		
		if(index == EMPTY_INDEX)
		{
			return this.buffer.start;
		}
		
		index = (index + 1) % this.buffer.getBufferSize();
		
		return index;
	}
	
	/**
	 * Moves the iterator to the previous position in the buffer.
	 * 
	 * @return The updated index after the operation:<br>
	 * - If the iterator's index is at the start of the buffer, the returned index is set to EMPTY_INDEX.<br>
	 * - If the iterator's index is EMPTY_INDEX, the returned index is set to the end of the buffer.<br>
	 * - For other positions of the iterator's index, it is decremented, and if it goes negative, it wraps around to the end of the buffer.<br>
	 * @author Sergio Morel
	 */
	public int goPrevious()
	{
		if(this.index == this.buffer.start)
		{
			return this.index = EMPTY_INDEX;
		}
		
		if(this.index == EMPTY_INDEX)
		{
			return this.index = this.buffer.end;
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
		if(index == this.buffer.start)
		{
			return EMPTY_INDEX;
		}
		
		if(index == EMPTY_INDEX)
		{
			return this.buffer.end;
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
	 * Removes the first element.
	 * 
	 * @return - first element.<br>
	 *         - null if the buffer is empty.
	 */
	public Byte removeFirst()
	{
		int dataStart = this.buffer.start;
		int dataEnd = this.buffer.end;
		byte[] byteArray = this.buffer.byteArray;
		
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
	 * Removes the last element.
	 * 
	 * @return - last element.<br>
	 *         - null if the buffer is empty.
	 */
	public Byte removeLast()
	{
		int dataStart = this.buffer.start;
		int dataEnd = this.buffer.end;
		byte[] byteArray = this.buffer.byteArray;
		
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