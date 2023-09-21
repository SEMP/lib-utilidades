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
public class CircularByteBufferIterator implements ListIterator<Byte>
{
	/**
	 * Value of index when not referring to a position in the buffer.
	 */
	private static final int BUFFER_BOUNDARY = CircularByteBuffer.BUFFER_BOUNDARY;
	
	/**
	 * Buffer to be iterated.
	 */
	private CircularByteBuffer buffer;
	
	/**
	 * Index for the current position.
	 */
	private int index;
	
	/**
	 * Indicates what was the previous iteration movement.
	 */
	private IterationMovement lastMovement;
	
	/**
	 * Indicates if no iteration has been made yet.
	 */
	private boolean firstIteration = true;
	
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
		this.index = BUFFER_BOUNDARY;
		this.lastMovement = IterationMovement.NONE;
	}
	
	/**
	 * Returns the index for the current position.
	 * 
	 * @return
	 * - the index for the current position.
	 * @author Sergio Morel
	 */
	public int getIndex()
	{
		return index;
	}
	
	/**
	 * Sets the position of the index;
	 * 
	 * @param index
	 * - New position of the index.
	 */
	protected void setIndex(int index)
	{
		this.index = index;
	}
	
	/**
	 * Determines if the current iteration is the first by comparing the index with the buffer's start.
	 * 
	 * @return
	 * - <b>true</b> next() or previous() haven't been called previously.<br>
	 * - <b>false</b> otherwise.
	 * @author Sergio Morel
	 */
	public boolean isFirstIteration()
	{
		return this.firstIteration;
	}
	
	@Override
	public boolean hasNext()
	{
		return this.goNext(this.index) != BUFFER_BOUNDARY;
	}
	
	@Override
	public boolean hasPrevious()
	{
		return this.goPrevious(this.index) != BUFFER_BOUNDARY;
	}
	
	@Override
	public Byte next()
	{
		if(this.lastMovement != IterationMovement.PREVIOUS)
		{
			this.goNext();
		}
		
		if(this.index == BUFFER_BOUNDARY)
		{
			String errorMessage = MessageUtil.getMessage(Messages.NO_DATA_AVAILABLE_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		this.lastMovement = IterationMovement.NEXT;
		
		this.firstIteration = false;
		
		return this.buffer.byteArray[this.index];
	}
	
	/**
	 * Retrieves the next byte from the buffer.
	 * 
	 * @return
	 * - the next byte in the buffer.
	 * @throws NoSuchElementException
	 * if the buffer is empty or if there is no more data in the buffer.
	 * @author Sergio Morel
	 */
	public byte nextByte()
	{
		if(this.lastMovement != IterationMovement.PREVIOUS)
		{
			this.goNext();
		}
		
		if(this.index == BUFFER_BOUNDARY)
		{
			String errorMessage = MessageUtil.getMessage(Messages.NO_DATA_AVAILABLE_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		this.lastMovement = IterationMovement.NEXT;
		
		this.firstIteration = false;
		
		return this.buffer.byteArray[this.index];
	}
	
	@Override
	public int nextIndex()
	{
		int nextIndex = this.goNext(this.index);
		
	    return (nextIndex == BUFFER_BOUNDARY) ? this.buffer.size() : nextIndex;
	}

	@Override
	public Byte previous()
	{
		if(this.lastMovement != IterationMovement.NEXT)
		{
			this.goPrevious();
		}
		
		if(this.index == BUFFER_BOUNDARY)
		{
			String errorMessage = MessageUtil.getMessage(Messages.NO_DATA_AVAILABLE_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		this.lastMovement = IterationMovement.PREVIOUS;
		
		this.firstIteration = false;
		
		return this.buffer.byteArray[this.index];
	}
	
	/**
	 * Retrieves the next byte from the buffer.
	 * 
	 * @return
	 * - the next byte in the buffer.
	 * @throws NoSuchElementException
	 * if the buffer is empty or if there is no more data in the buffer.
	 * @author Sergio Morel
	 */
	public byte previousByte()
	{
		if(this.lastMovement != IterationMovement.NEXT)
		{
			this.goPrevious();
		}
		
		if(this.index == BUFFER_BOUNDARY)
		{
			String errorMessage = MessageUtil.getMessage(Messages.NO_DATA_AVAILABLE_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		this.lastMovement = IterationMovement.PREVIOUS;
		
		this.firstIteration = false;
		
		return this.buffer.byteArray[this.index];
	}
	
	@Override
	public int previousIndex()
	{
		return this.goPrevious(this.index);
	}

	@Override
	public void remove() //TODO revisar
	{
		if(this.lastMovement == IterationMovement.NONE)
		{
			String errorMessage = MessageUtil.getMessage(Messages.CALL_NEXT_OR_PREVIOUS_BEFORE_ERROR);
			
			throw new IllegalStateException(errorMessage);
		}
		
		int dataStart = this.buffer.start;
		int dataEnd = this.buffer.end;
		int removeIndex = this.index;
		
		if(removeIndex == BUFFER_BOUNDARY)
		{
			String errorMessage = MessageUtil.getMessage(Messages.NO_DATA_AVAILABLE_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		IterationMovement lastMovement = this.lastMovement;
		
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
		
		if(lastMovement == IterationMovement.NEXT)
		{
			if(forwardDistance <= backwardDistance)
			{
				this.shiftFromEnd(removeIndex);
				
				this.goPrevious();
			}
			else
			{
				this.shiftFromStart(removeIndex);
			}
		}
		else if(lastMovement == IterationMovement.PREVIOUS)
		{
			if(forwardDistance <= backwardDistance)
			{
				shiftFromEnd(removeIndex);
			}
			else
			{
				shiftFromStart(removeIndex);
				
				this.goNext();
			}
		}
	}

	/**
	 * Updates the current index to point to the next position in the circular buffer.<br>
	 * - If the end of the circular buffer is reached, the index is set as BUFFER_BOUNDARY.<br>
	 * - If the index is at BUFFER_BOUNDARY, it moves to the circular buffer start.
	 * 
	 * @return
	 * - The updated index after the operation.
	 * @author Sergio Morel
	 */
	protected int goNext()
	{
		return this.index = this.goNext(this.index);
	}
	
	/**
	 * Moves to the next position in the circular buffer from a given index.
	 * It doesn't change the iterator's index value.<br>
	 * - If the end of the circular buffer is reached, the index is set as BUFFER_BOUNDARY.<br>
	 * - If the index is at BUFFER_BOUNDARY, it moves to the circular buffer start.
	 * 
	 * @param index
	 * - The index from which to move to the next position.
	 * @return
	 * - The index after the operation.
	 *   @author Sergio Morel
	 */
	protected int goNext(int index)
	{
		int dataStart = this.buffer.start;
		int dataEnd = this.buffer.end;
		int dataSize = this.buffer.getDataSize();
		int bufferSize = this.buffer.getBufferSize();
		
		if(dataSize < 1 || index == dataEnd)
		{
			return index = BUFFER_BOUNDARY;
		}
		
		if(index == BUFFER_BOUNDARY)
		{
			return index = dataStart;
		}
		
		index = (index + 1) % bufferSize;
		
		if(index < 0)
		{
			index += bufferSize;
		}
		
		return index;
	}
	
	/**
	 * Moves the iterator to the previous position in the circular buffer.<br>
	 * - If the start of the circular buffer is reached, the index is set as BUFFER_BOUNDARY.<br>
	 * - If the index is at BUFFER_BOUNDARY, it moves to the circular buffer end.
	 * 
	 * @return
	 * - The updated index after the operation.
	 * @author Sergio Morel
	 */
	protected int goPrevious()
	{
		return this.index = this.goPrevious(this.index);
	}
	
	/**
	 * Moves the iterator to the previous position in the circular buffer.<br>
	 * It doesn't change the iterator's index value.<br>
	 * - If the start of the circular buffer is reached, the index is set as BUFFER_BOUNDARY.<br>
	 * - If the index is at BUFFER_BOUNDARY, it moves to the circular buffer end.
	 * 
	 * @param index
	 * - The index from which to move to the previous position.
	 * @return
	 * - The index after the operation.
	 *   @author Sergio Morel
	 */
	protected int goPrevious(int index)
	{
		int dataStart = this.buffer.start;
		int dataEnd = this.buffer.end;
		int dataSize = this.buffer.getDataSize();
		int bufferSize = this.buffer.getBufferSize();
		
		if(dataSize < 1 || index == dataStart)
		{
			return index = BUFFER_BOUNDARY;
		}
		
		if(index == BUFFER_BOUNDARY)
		{
			return index = dataEnd;
		}
		
		index = (index - 1) % bufferSize;
		
		if(index < 0)
		{
			index += bufferSize;
		}
		
		return index;
	}
	
	/**
	 * Advances the iterator's index position by the given number of steps in the circular buffer.
	 * The method handles cases where the index goes out of bounds by wrapping it around.
	 * After leaving BUFFER_BOUNDARY, this method won't step into BUFFER_BOUNDARY again,
	 * unless the circular buffer is empty.
	 * 
	 * @param steps
	 * - The number of positions to advance. It can be negative to move backward.
	 * @return
	 * - The updated index position after advancing the steps.
	 * @author Sergio Morel
	 */
	protected int forward(int steps)
	{
		return this.index = this.forward(this.index, steps);
	}
	
	/**
	 * Advances a given index position by the specified number of steps in the circular buffer without
	 * affecting the iterator's current index. The method handles cases where the index goes
	 * out of bounds by wrapping it around.
	 * After leaving BUFFER_BOUNDARY, this method won't step into BUFFER_BOUNDARY again,
	 * unless the circular buffer is empty.
	 * 
	 * @param index
	 * - The current index position to advance from.
	 * @param steps
	 * - The number of positions to advance. It can be negative to move backward.
	 * @return
	 * - The updated index position after advancing.
	 * @author Sergio Morel
	 */
	protected int forward(int index, int steps)
	{
		if(steps == 0)
		{
			return index;
		}
		
		int dataStart = this.buffer.start;
		int dataSize = this.buffer.getDataSize();
		int bufferSize = this.buffer.getBufferSize();
		
		if(dataSize < 1)
		{
			return BUFFER_BOUNDARY;
		}
		
		if(index == BUFFER_BOUNDARY && steps < 0)
		{
			steps++;
		}
		
		steps = steps % dataSize;
		
		int relativeIndex = (index - dataStart + bufferSize + steps) % dataSize;
		
		if(relativeIndex < 0)
		{
			relativeIndex += dataSize;
		}
		
		index = (dataStart + relativeIndex) % bufferSize;
		
		return index;
	}
	
	/**
	 * Rewinds the internal index of the iterator by a specified number of steps.
	 * If the number of steps is larger than the circular buffer size, the rewinding will wrap
	 * around the circular buffer. After leaving BUFFER_BOUNDARY, this method won't step
	 * into BUFFER_BOUNDARY again, unless the circular buffer is empty.
	 * 
	 * @param steps
	 * - The number of steps to rewind the iterator.
	 * The value can be negative to move forward.
	 * @return
	 * - The updated index after the rewind operation.
	 * @author Sergio Morel
	 */
	protected int rewind(int steps)
	{
		return this.index = this.rewind(this.index, steps);
	}
	
	/**
	 * Rewinds a given index position by the specified number of steps in the circular buffer without
	 * affecting the iterator's current index.
	 * If the number of steps is larger than the circular buffer size, the rewinding will wrap
	 * around the circular buffer. After leaving BUFFER_BOUNDARY, this method won't step
	 * into BUFFER_BOUNDARY again, unless the circular buffer is empty.
	 * 
	 * @param index
	 * - The index from which to start the rewind operation.
	 * @param steps
	 * - The number of steps to rewind the iterator.
	 * The value can be negative to move forward.
	 * @return
	 * - The updated index after the rewind operation.
	 * @author Sergio Morel
	 */
	protected int rewind(int index, int steps)
	{
		if(steps == 0)
		{
			return index;
		}
		
		int dataStart = this.buffer.start;
		int dataSize = this.buffer.getDataSize();
		int bufferSize = this.buffer.getBufferSize();
		
		if(dataSize < 1)
		{
			return BUFFER_BOUNDARY;
		}
		
		if(index == BUFFER_BOUNDARY && steps > 0)
		{
			steps--;
		}
		
		steps = steps % dataSize;
		
		int relativeIndex = (index - dataStart +  bufferSize - steps) % dataSize;
		
		if(relativeIndex < 0)
		{
			relativeIndex += dataSize;
		}
		
		index = (dataStart + relativeIndex) % bufferSize;
		
		return index;
	}
	
	/**
	 * Shifts the elements from the start of the buffer towards the removeIndex,
	 * overwriting the element at the removeIndex in the process.
	 * 
	 * @param removeIndex
	 * - The index of the element to be overwritten by the shifting.
	 * @author Sergio Morel
	 */
	private void shiftFromStart(int removeIndex)
	{
		int copyFromIndex = this.goPrevious(removeIndex);
		int copyToIndex = removeIndex;
		
		while(copyFromIndex != BUFFER_BOUNDARY)
		{
			this.buffer.byteArray[copyToIndex] = this.buffer.byteArray[copyFromIndex];
			
			copyToIndex = this.goPrevious(copyToIndex);
			copyFromIndex = this.goPrevious(copyFromIndex);
		}
		
		this.buffer.start = this.goNext(copyToIndex);
	}
	
	/**
	 * Shifts the elements from the end of the buffer towards the removeIndex,
	 * overwriting the element at the removeIndex in the process.
	 * 
	 * @param removeIndex
	 * - The index of the element to be overwritten by the shifting.
	 * @author Sergio Morel
	 */
	private void shiftFromEnd(int removeIndex)
	{
		int copyFromIndex = this.goNext(removeIndex);
		int copyToIndex = removeIndex;
		
		while(copyFromIndex != BUFFER_BOUNDARY)
		{
			this.buffer.byteArray[copyToIndex] = this.buffer.byteArray[copyFromIndex];
			
			copyToIndex = this.goNext(copyToIndex);
			copyFromIndex = this.goNext(copyFromIndex);
		}
		
		this.buffer.end = this.goPrevious(copyToIndex);
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
		
		if(this.buffer.isEmpty())
		{
			String errorMessage = MessageUtil.getMessage(Messages.NO_DATA_AVAILABLE_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		if(!this.inRange(start, end))
		{
			String errorMessage = MessageUtil.getMessage(Messages.INVALID_INDEX_RANGE_ERROR, start, end, this.buffer.size());
			
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		if(start == dataStart)
		{
			this.buffer.start = this.goNext(end);
			
			if(this.buffer.start == BUFFER_BOUNDARY)
			{
				this.buffer.end = BUFFER_BOUNDARY;
			}
			
			return;
		}
		
		if(end == dataEnd)
		{
			this.buffer.end = this.goPrevious(start);
			
			if(this.buffer.end == BUFFER_BOUNDARY)
			{
				this.buffer.start = BUFFER_BOUNDARY;
			}
			
			return;
		}
		
		int copyFromIndex = this.goNext(end);
		int copyToIndex = start;
		
		while(copyFromIndex != BUFFER_BOUNDARY)
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
		
		if(this.buffer.isEmpty())
		{
			String errorMessage = MessageUtil.getMessage(Messages.NO_DATA_AVAILABLE_ERROR);
			
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
		
		if(dataStart == BUFFER_BOUNDARY)
		{
			String errorMessage = MessageUtil.getMessage(Messages.NO_DATA_AVAILABLE_ERROR);
			
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
	 * Checks if the given range, from start to end, falls within the circular buffer's range.
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
	protected boolean inRange(int start, int end)
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
		
		while(i >= 0 && index != BUFFER_BOUNDARY)
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
	public void set(Byte element)
	{
		switch(this.lastMovement)
		{
			case NEXT:
			{
				int setIndex = this.goPrevious(this.index);
				
				this.buffer.byteArray[setIndex] = element;
				
				break;
			}
			
			case PREVIOUS:
			{
				int setIndex = this.goNext(this.index);
				
				this.buffer.byteArray[setIndex] = element;
				
				break;
			}
			
			case NONE:
			{
				String errorMessage = MessageUtil.getMessage(Messages.CALL_NEXT_OR_PREVIOUS_BEFORE_ERROR);
				
				throw new IllegalStateException(errorMessage);
			}
		}
	}
	
	@Override
	public void add(Byte e)
	{
		// TODO Auto-generated method stub
		
//		if(this.lastAddedIndex == EMPTY_INDEX)
//		{
//			//update lastAddedIndex.
//		}
		
		this.lastMovement = IterationMovement.NONE;
	}
}