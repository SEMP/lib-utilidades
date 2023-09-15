package py.com.semp.lib.utilidades.data;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import py.com.semp.lib.utilidades.enumerations.IterationMovement;
import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;

/**
 * Iterator for {@link CircularByteBuffer}
 * 
 * @author Sergio Morel
 */
public class CircularByteBufferIteratorList implements ListIterator<Byte>
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
	
	private IterationMovement lastMovement;
	
	/**
	 * Constructor with argument for the {@link CircularByteBuffer}.
	 * 
	 * @param buffer
	 * - buffer to be iterated.
	 * @author Sergio Morel
	 */
	public CircularByteBufferIteratorList(CircularByteBuffer buffer)
	{
		super();
		
		this.buffer = buffer;
		this.index = buffer.start;
		this.lastMovement = IterationMovement.NONE;
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
		
		this.lastMovement = IterationMovement.NEXT;
		
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
		
		this.lastMovement = IterationMovement.NEXT;
		
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
	
	/**
	 * Moves to the previous position in the buffer from a given index. It doesn't change
	 * the iterator's index value.
	 * 
	 * @param index
	 * - The index from which to move to the previous position.
	 * @return The updated index after the operation:<br>
	 * - If the provided index is at the start of the buffer, the returned index is set to EMPTY_INDEX.<br>
	 * - If the provided index is EMPTY_INDEX, the returned index is set to the end of the buffer.<br>
	 * - For other positions of the provided index, it is decremented, and if it goes negative, it wraps around to the end of the buffer.
	 * @author Sergio Morel
	 */
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
	
	/**
	 * Advances the iterator's index position by the given number of steps in the buffer. The method
	 * handles cases where the index goes out of bounds by wrapping it around.
	 * 
	 * @param steps
	 * - The number of positions to advance. It can be negative to move backward.
	 * @return The updated index position after advancing.<br> 
	 * - If the current index is EMPTY_INDEX, the method resets it to 0 before advancing.<br>
	 * - If the resultant index goes out of bounds, it wraps around to stay within buffer limits.
	 * @author Sergio Morel
	 */
	public int forward(int steps)
	{
		if(this.index == EMPTY_INDEX)
		{
			this.index = 0;
		}
		
		int bufferSize = this.buffer.getBufferSize();
		
		this.index = (this.index + steps) % bufferSize;
		
		if(this.index < 0)
		{
			this.index += bufferSize;
		}
		
		return this.index;
	}
	
	/**
	 * Advances a given index position by the specified number of steps in the buffer without
	 * affecting the iterator's current index. The method handles cases where the index goes
	 * out of bounds by wrapping it around.
	 * 
	 * @param index
	 * - The current index position to advance from.
	 * @param steps
	 * - The number of positions to advance. It can be negative to move backward.
	 * @return The updated index position after advancing.<br> 
	 * - If the provided index is EMPTY_INDEX, the method resets it to 0 before advancing.<br>
	 * - If the resultant index goes out of bounds, it wraps around to stay within buffer limits.
	 * @author Sergio Morel
	 */
	public int forward(int index, int steps)
	{
		if(index == EMPTY_INDEX)
		{
			index = 0;
		}
		
		int bufferSize = this.buffer.getBufferSize();
		
		index = (index + steps) % bufferSize;
		
		if(index < 0)
		{
			index += bufferSize;
		}
		
		return index;
	}
	
	/**
	 * Rewinds the internal index of the iterator by a specified number of steps.
	 * 
	 * @param steps
	 * - The number of steps to rewind the iterator. If the number of steps is larger
	 * than the buffer size, the rewinding will wrap around the buffer.
	 * @return The updated index after the rewind operation.<br>
	 * - If the internal index is set to EMPTY_INDEX, it gets initialized to 0 before the rewind operation.<br>
	 * - After the rewind operation, if the updated index becomes negative (indicating a move past the start of
	 * the buffer), the index will wrap around to the end of the buffer.
	 * @author Sergio Morel
	 */
	public int rewind(int steps)
	{
		if(this.index == EMPTY_INDEX)
		{
			this.index = 0;
		}
		
		int bufferSize = this.buffer.getBufferSize();
		
		this.index = (this.index - steps) % bufferSize;
		
		if(this.index < 0)
		{
			this.index += bufferSize;
		}
		
		return this.index;
	}
	
	/**
	 * Rewinds a specified index by a specified number of steps.
	 * 
	 * @param index
	 * - The index from which to start the rewind operation.
	 * @param steps
	 * - The number of steps to rewind from the given index.
	 * @return The updated index after the rewind operation.<br>
	 * - If the provided index is set to EMPTY_INDEX, it gets initialized to 0 before the rewind operation.<br>
	 * - After the rewind operation, if the updated index becomes negative (indicating a move past the start
	 * of the buffer), the index will wrap around to the end of the buffer.
	 * @author Sergio Morel
	 */
	public int rewind(int index, int steps)
	{
		if(index == EMPTY_INDEX)
		{
			index = 0;
		}
		
		int bufferSize = this.buffer.getBufferSize();
		
		index = (index - steps) % bufferSize;
		
		if(index < 0)
		{
			index += bufferSize;
		}
		
		return index;
	}
	//TODO hacer el codigo para previous
	@Override
	public void remove()
	{
		int dataStart = this.buffer.start;
		int dataEnd = this.buffer.end;
		int removeIndex = this.index;
		
		if(dataStart == EMPTY_INDEX)
		{
			String errorMessage = MessageUtil.getMessage(Messages.BUFFER_EMPTY_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		switch(this.lastMovement)
		{
			case NEXT:
			{
				removeIndex = this.goPrevious(removeIndex);
				
				break;
			}
			
			case PREVIOUS:
			{
				removeIndex = this.goNext(removeIndex);
				
				break;
			}
			
			case NONE:
			{
				String errorMessage = MessageUtil.getMessage(Messages.CALL_NEXT_BEFORE_REMOVE_ERROR);
				
				throw new IllegalStateException(errorMessage);
			}
		}
		
		this.lastMovement = IterationMovement.NONE;
		
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
		
		int forwardDistance = Math.abs(dataEnd - removeIndex);
		int backwardDistance = Math.abs(dataStart - removeIndex);
		
		if(forwardDistance <= backwardDistance)
		{
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
		else
		{
			int copyFromIndex = this.goPrevious(removeIndex);
			int copyToIndex = removeIndex;
			
			while(copyFromIndex != EMPTY_INDEX)
			{
				this.buffer.byteArray[copyToIndex] = this.buffer.byteArray[copyFromIndex];
				
				copyToIndex = this.goPrevious(copyToIndex);
				copyFromIndex = this.goPrevious(copyFromIndex);
			}
			
			this.buffer.start = this.goNext(copyToIndex);
		}
	}
	
	/**
	 * Removes the elements in the buffer within the specified range (inclusive of start and end).
	 * If the buffer is empty or the range is out of bounds, an exception is thrown.
	 * 
	 * @param start
	 * - The starting index of the range to be removed.
	 * @param end
	 * - The ending index of the range to be removed.
	 * @throws NoSuchElementException
	 * if the buffer is empty.
	 * @throws IndexOutOfBoundsException
	 * if the specified range is out of the buffer bounds.
	 */
	public void remove(int start, int end)
	{
		int dataStart = this.buffer.start;
		int dataEnd = this.buffer.end;
		
		if(dataStart == EMPTY_INDEX)
		{
			String errorMessage = MessageUtil.getMessage(Messages.BUFFER_EMPTY_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		if(!this.inRange(start, end))
		{
			String errorMessage = MessageUtil.getMessage(Messages.CIRCULAR_BUFFER_OUT_OF_BOUNDS, start, end, this.buffer.size());
			
			throw new IndexOutOfBoundsException(errorMessage);
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
	
	/**
	 * Removes the first element from the buffer.
	 * <p>
	 * Note:<br>
	 * If the internal iterator index is at the start of the buffer (the position of the
	 * element being removed), this method will also update the iterator's index to the next
	 * position after the removal.
	 * </p>
	 * @return
	 * - The first byte data from the buffer.
	 * 
	 * @throws NoSuchElementException
	 * If the buffer is empty.
	 * @author Sergio Morel
	 */
	public byte removeFirst()
	{
		int dataStart = this.buffer.start;
		int dataEnd = this.buffer.end;
		byte[] byteArray = this.buffer.byteArray;
		
		if(dataStart == EMPTY_INDEX)
		{
			String errorMessage = MessageUtil.getMessage(Messages.BUFFER_EMPTY_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		byte data = byteArray[dataStart];
		
		if(dataStart == dataEnd)
		{
			this.buffer.clear();
		}
		else
		{
			if(this.index == dataStart)
			{
				this.buffer.start = this.goNext();
			}
			else
			{
				this.buffer.start = this.goNext(dataStart);
			}
		}
		
		return data;
	}
	
	/**
	 * Removes the last element from the buffer.
	 * <p>
	 * Note:<br>
	 * If the internal iterator index is at the end of the buffer (the position of
	 * the element being removed), this method will also update the iterator's index
	 * to the previous position after the removal.
	 * </p>
	 * 
	 * @return
	 * - The last byte data from the buffer.
	 * 
	 * @throws NoSuchElementException
	 * If the buffer is empty.
	 * @author Sergio Morel
	 */
	public byte removeLast()
	{
		int dataStart = this.buffer.start;
		int dataEnd = this.buffer.end;
		byte[] byteArray = this.buffer.byteArray;
		
		if(dataStart == EMPTY_INDEX)
		{
			String errorMessage = MessageUtil.getMessage(Messages.BUFFER_EMPTY_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		byte data = byteArray[dataEnd];
		
		if(dataStart == dataEnd)
		{
			this.buffer.clear();
		}
		else
		{
			if(this.index == dataEnd)
			{
				this.buffer.end = this.goPrevious();
			}
			else
			{
				this.buffer.end = this.goPrevious(dataEnd);
			}
		}
		
		return data;
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
		// True if the buffer doesn't wrap around
		boolean bufferIsLinear = this.buffer.end >= this.buffer.start;
		// True if the provided range doesn't wrap around
		boolean rangeIsLinear = end >= start;
		boolean startIsAfterBufferStart = start >= this.buffer.start;
		boolean endIsBeforeBufferEnd = this.buffer.end >= end;
		
		// If both the buffer's data and the provided range are either linear or both wrap around
		if(bufferIsLinear == rangeIsLinear)
		{
			return startIsAfterBufferStart && endIsBeforeBufferEnd;
		}
		
		// If the provided range wraps around
		if(!rangeIsLinear)
		{
			return false;
		}
		
		// If only the buffer's data wraps around, but the range is linear
		boolean endIsAfterBufferStart = end >= this.buffer.start;
		boolean startIsBeforeBufferEnd = this.buffer.end >= start;
		
		return (startIsAfterBufferStart && endIsAfterBufferStart) || (startIsBeforeBufferEnd && endIsBeforeBufferEnd);
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
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.index);
		
		return sb.toString();
	}
	
	@Override
	public boolean hasPrevious()
	{
		return this.previousIndex() != EMPTY_INDEX;
	}
	
	@Override
	public Byte previous()
	{
		if(this.index == EMPTY_INDEX)
		{
			String errorMessage = MessageUtil.getMessage(Messages.BUFFER_EMPTY_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		byte[] byteArray = this.buffer.byteArray;
		
		byte data = byteArray[this.index];
		
		this.goPrevious();
		
		this.lastMovement = IterationMovement.PREVIOUS;
		
		return data;
	}
	
	public byte previousByte()
	{
		if(this.index == EMPTY_INDEX)
		{
			String errorMessage = MessageUtil.getMessage(Messages.BUFFER_EMPTY_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		byte[] byteArray = this.buffer.byteArray;
		
		byte data = byteArray[this.index];
		
		this.goPrevious();
		
		this.lastMovement = IterationMovement.PREVIOUS;
		
		return data;
	}
	
	@Override
	public int nextIndex()
	{
		int nextIndex = this.goNext(this.index);
		
	    return (nextIndex == EMPTY_INDEX) ? this.buffer.size() : nextIndex;
	}
	
	@Override
	public int previousIndex()
	{
		return this.goPrevious(this.index);
	}
	
	@Override
	public void set(Byte e)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void add(Byte e)
	{
		// TODO Auto-generated method stub
		
	}
}