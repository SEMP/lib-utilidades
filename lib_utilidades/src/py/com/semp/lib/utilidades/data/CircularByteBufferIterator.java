package py.com.semp.lib.utilidades.data;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import py.com.semp.lib.utilidades.enumerations.IterationAction;
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
	 * Index to keep track of the new elements added to the buffer.
	 */
	private int newElementsIndex;
	
	/**
	 * Indicates what was the previous iteration movement.
	 */
	private IterationAction lastAction;
	
	/**
	 * Indicates if no iteration has been made yet.
	 */
	private boolean firstIteration;
	
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
		this.reset();
	}
	
	/**
	 * Resets the iterator to the starting position.
	 */
	public void reset()
	{
		this.index = BUFFER_BOUNDARY;
		this.newElementsIndex = BUFFER_BOUNDARY;
		this.lastAction = IterationAction.NONE;
		this.firstIteration = true;
	}
	
	/**
	 * Returns the internal index for the current position. This is the
	 * internal index used for the underlying byte array.
	 * 
	 * @return
	 * - the internal index for the current position.
	 * @author Sergio Morel
	 */
	public int getInternalIndex()
	{
		return index;
	}
	
	/**
	 * Sets the position of the index;
	 * 
	 * @param index
	 * - New position of the index.
	 */
	protected void setInternalIndex(int index)
	{
		this.index = index;
	}
	
	/**
	 * Translates the current internal index into the index in the point of view of the circular buffer.
	 * 
	 * @return
	 * - the translated current index.
	 * @author Sergio Morel
	 */
	public int getIndex()
	{
		return this.getIndex(this.index);
	}
	
	/**
	 * Translates the internal index into the index in the point of view
	 * of the circular buffer.
	 * 
	 * @param internalIndex
	 * - The internal index that would be mapped into the point of view
	 * of the circular buffer.
	 * 
	 * @return
	 * - the translated index.
	 * @author Sergio Morel
	 */
	public int getIndex(int internalIndex)
	{
		if(internalIndex == BUFFER_BOUNDARY)
		{
			return BUFFER_BOUNDARY;
		}
		
		int bufferSize = this.buffer.getBufferSize();
		int dataStart = this.buffer.start;
		
		return (internalIndex - dataStart + bufferSize) % bufferSize;
	}
	
	public void goTo(int index)
	{
		int dataSize = this.buffer.getDataSize();
		
		if(index < 0 || index >= dataSize)
		{
			String errorMessage = MessageUtil.getMessage(Messages.INDEX_OUT_OF_BOUNDS, index, dataSize);
			
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		this.index = this.forward(BUFFER_BOUNDARY, index);
		this.lastAction = IterationAction.NEXT;
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
		if(this.lastAction != IterationAction.PREVIOUS)
		{
			return this.goNext(this.index) != BUFFER_BOUNDARY;
		}
		
		return true;
	}
	
	@Override
	public boolean hasPrevious()
	{
		if(this.lastAction != IterationAction.NEXT)
		{
			return this.goPrevious(this.index) != BUFFER_BOUNDARY;
		}
		
		return true;
	}
	
	@Override
	public Byte next()
	{
		if(this.lastAction != IterationAction.PREVIOUS)
		{
			this.goNext();
		}
		
		if(this.index == BUFFER_BOUNDARY)
		{
			String errorMessage = MessageUtil.getMessage(Messages.NO_DATA_AVAILABLE_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		this.lastAction = IterationAction.NEXT;
		
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
		if(this.lastAction != IterationAction.PREVIOUS)
		{
			this.goNext();
		}
		
		if(this.index == BUFFER_BOUNDARY)
		{
			String errorMessage = MessageUtil.getMessage(Messages.NO_DATA_AVAILABLE_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		this.lastAction = IterationAction.NEXT;
		
		this.firstIteration = false;
		
		return this.buffer.byteArray[this.index];
	}
	
	@Override
	public int nextIndex()
	{
		int internalIndex = this.goNext(this.index);
		int nextIndex = this.getIndex(internalIndex);
		
		return (nextIndex == BUFFER_BOUNDARY) ? this.buffer.size() : nextIndex;
	}
	
	@Override
	public Byte previous()
	{
		if(this.lastAction != IterationAction.NEXT && this.lastAction != IterationAction.ADD && this.lastAction != IterationAction.REMOVE)
		{
			this.goPrevious();
		}
		
		if(this.index == BUFFER_BOUNDARY)
		{
			String errorMessage = MessageUtil.getMessage(Messages.NO_DATA_AVAILABLE_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		this.lastAction = IterationAction.PREVIOUS;
		
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
		if(this.lastAction != IterationAction.NEXT && this.lastAction != IterationAction.ADD && this.lastAction != IterationAction.REMOVE)
		{
			this.goPrevious();
		}
		
		if(this.index == BUFFER_BOUNDARY)
		{
			String errorMessage = MessageUtil.getMessage(Messages.NO_DATA_AVAILABLE_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		this.lastAction = IterationAction.PREVIOUS;
		
		this.firstIteration = false;
		
		return this.buffer.byteArray[this.index];
	}
	
	@Override
	public int previousIndex()
	{
		int internalIndex = this.goPrevious(this.index);
		
		return this.getIndex(internalIndex);
	}
	
	@Override
	public void remove()
	{
		if(this.lastAction != IterationAction.PREVIOUS && this.lastAction != IterationAction.NEXT)
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
		
		this.lastAction = IterationAction.REMOVE;
		
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
		
		int forwardDistance = this.forwardDistance(removeIndex);
		int backwardDistance = this.backwardDistance(removeIndex);
		
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
	
	private int backwardDistance(int index)
	{
		int dataStart = this.buffer.start;
		int distance = index - dataStart;
		
		if(distance < 0)
		{
			distance += this.buffer.getBufferSize();
		}
		
		return distance;
	}
	
	private int forwardDistance(int index)
	{
		int dataEnd = this.buffer.end;
		int distance = dataEnd - index;
		
		if(distance < 0)
		{
			distance += this.buffer.getBufferSize();
		}
		
		return distance;
	}
	
	/**
	 * Removes the elements in the circular buffer within the specified range (inclusive of start and end).
	 * If the buffer is empty or the range is out of bounds, an exception is thrown.
	 * 
	 * @param from
	 * - The starting index of the range to be removed (inclusive).
	 * @param to
	 * - The ending index of the range to be removed (exclusive).
	 * @throws NoSuchElementException
	 * if the buffer is empty.
	 * @throws IndexOutOfBoundsException
	 * if the specified range is out of the buffer bounds.
	 */
	public void remove(int from, int to)
	{
		if(this.buffer.isEmpty())
		{
			String errorMessage = MessageUtil.getMessage(Messages.NO_DATA_AVAILABLE_ERROR);
			
			throw new NoSuchElementException(errorMessage);
		}
		
		if(from == to)
		{
			return;
		}
		
		int dataStart = this.buffer.start;
		int dataEnd = this.buffer.end;
		int dataSize = this.buffer.getDataSize();
		
		if(from < 0 || from >= dataSize || to < 0 || to > dataSize || to < from)
		{
			String errorMessage = MessageUtil.getMessage(Messages.INVALID_INDEX_RANGE_ERROR, from, to, dataSize);
			
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		int internalFrom = this.forward(dataStart, from);
		int internalTo = this.forward(dataStart, to - 1);
		
		if(internalFrom == dataStart)
		{
			this.buffer.start = this.goNext(internalTo);
			
			if(this.buffer.start == BUFFER_BOUNDARY)
			{
				this.buffer.end = BUFFER_BOUNDARY;
			}
			
			return;
		}
		
		if(internalTo == dataEnd)
		{
			this.buffer.end = this.goPrevious(internalFrom);
			
			if(this.buffer.end == BUFFER_BOUNDARY)
			{
				this.buffer.start = BUFFER_BOUNDARY;
			}
			
			return;
		}
		
		int forwardDistance = this.forwardDistance(to - 1);
		int backwardDistance = this.backwardDistance(from);
		
		if(forwardDistance <= backwardDistance)
		{
			this.shiftFromEnd(internalFrom, internalTo);
		}
		else
		{
			this.shiftFromStart(internalFrom, internalTo);
		}
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
			this.buffer.start = this.goNext(dataStart);
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
	public void set(Byte element)
	{
		if(this.lastAction != IterationAction.PREVIOUS && this.lastAction != IterationAction.NEXT)
		{
			String errorMessage = MessageUtil.getMessage(Messages.CALL_NEXT_OR_PREVIOUS_BEFORE_ERROR);
			
			throw new IllegalStateException(errorMessage);
		}
		
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
		
		this.buffer.byteArray[this.index] = element;
	}
	
	@Override
	public void add(Byte element)
	{
		int bufferSize = this.buffer.getBufferSize();
		int dataSize = this.buffer.getDataSize();
		int dataStart = this.buffer.start;
		int dataEnd = this.buffer.end;
		
		if(this.lastAction != IterationAction.ADD)
		{
			this.newElementsIndex = this.goNext(this.index);
		}
		
		this.lastAction = IterationAction.ADD;
		
		if(dataSize < 1)
		{
			this.buffer.start = 0;
			this.buffer.end = 0;
			this.buffer.byteArray[0] = element;
			this.index = BUFFER_BOUNDARY;
			
			return;
		}
		
		if(this.index == BUFFER_BOUNDARY)
		{
			this.addLast(element);
			
			return;
		}
		
		if(this.index == dataEnd)
		{
			this.index = this.addLast(element);
			
			return;
		}
		
		int insertPoint = index;
		
		int forwardDistance = this.forwardDistance(insertPoint);
		int backwardDistance = this.backwardDistance(insertPoint);
		
		if(this.spaceAvailable())
		{
			if(forwardDistance <= backwardDistance)
			{
				this.goNext();
				this.shiftToEnd(this.index);
				this.buffer.byteArray[this.index] = element;
			}
			else
			{
				this.shiftToStart(this.index);
				this.buffer.byteArray[this.index] = element;
				this.newElementsIndex = this.goPrevious(this.newElementsIndex);
			}
			
			return;
		}
		
		//replace older data
		if(this.newElementsIndex != dataStart)
		{
			if(forwardDistance <= backwardDistance)
			{
				this.buffer.end = (dataEnd + 1) % bufferSize;
				this.buffer.start = (dataStart + 1) % bufferSize;
				this.goNext();
				this.shiftToEnd(this.index);
				this.buffer.byteArray[this.index] = element;
				
			}
			else
			{
				this.shiftToStart(insertPoint);
				this.buffer.byteArray[insertPoint] = element;
				this.newElementsIndex = this.goPrevious(this.newElementsIndex);
			}
			
			return;
		}
		
		//Overwrite elements to the end.
		if(this.index != dataEnd)
		{
			this.goNext();
			this.buffer.byteArray[this.index] = element;
			
			return;
		}
	}

	private boolean spaceAvailable()
	{
		int dataStart = this.buffer.start;
		int dataEnd = this.buffer.end;
		int bufferSize = this.buffer.getBufferSize();
		
		return
		(dataStart <= dataEnd && dataEnd - dataStart < bufferSize - 1) ||
		(dataStart > dataEnd && dataStart - 1 > dataEnd);
	}
	
	/**
	 * Adds an element at the start of the circular buffer.
	 * If the buffer is full, the first element will be overwritten.
	 * @param element
	 * - Element to insert into the circular buffer.
	 * @return
	 * - The index where the new element was inserted.
	 * @author Sergio Morel
	 */
	protected int addFirst(byte element)
	{
		int insertIndex = 0;
		
		if(this.buffer.isEmpty())
		{
			this.buffer.start = 0;
			this.buffer.end = 0;
			this.buffer.byteArray[insertIndex] = element;
			
			return insertIndex;
		}
		
		int bufferSize = this.buffer.getBufferSize();
		
		insertIndex = (this.buffer.start - 1) % bufferSize;
		
		if(insertIndex < 0)
		{
			insertIndex += bufferSize;
		}
		
		if(insertIndex == this.buffer.end)
		{
			insertIndex = this.buffer.start;
		}
		
		this.buffer.byteArray[insertIndex] = element;
		
		return this.buffer.start = insertIndex;
	}
	
	/**
	 * Adds an element at the end of the circular buffer.
	 * If the buffer is full, the first element will be overwritten.
	 * @param element
	 * - Element to insert into the circular buffer.
	 * @return
	 * - The index where the new element was inserted.
	 * @author Sergio Morel
	 */
	protected int addLast(byte element)
	{
		int insertIndex = 0;
		
		if(this.buffer.isEmpty())
		{
			this.buffer.start = 0;
			this.buffer.end = 0;
			this.buffer.byteArray[insertIndex] = element;
			
			return insertIndex;
		}
		
		int bufferSize = this.buffer.getBufferSize();
		
		insertIndex = (this.buffer.end + 1) % bufferSize;
		
		if(insertIndex == this.buffer.start)
		{
			this.buffer.start = this.forward(this.buffer.start, 1);
		}
		
		this.buffer.byteArray[insertIndex] = element;
		
		return this.buffer.end = insertIndex;
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
		
		if(index == BUFFER_BOUNDARY)
		{
			index = dataStart;
			
			if(steps > 0)
			{
				steps--;
			}
		}
		
		steps = steps % dataSize;
		
		int relativeIndex = index - dataStart;
		
		if(relativeIndex < 0)
		{
			relativeIndex += bufferSize;
		}
		
		int finalRelativeIndex = (relativeIndex + steps) % dataSize;
		
		if(finalRelativeIndex < 0)
		{
			finalRelativeIndex += dataSize;
		}
		
		index = (dataStart + finalRelativeIndex) % bufferSize;
		
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
		int dataEnd = this.buffer.end;
		int dataSize = this.buffer.getDataSize();
		int bufferSize = this.buffer.getBufferSize();
		
		if(dataSize < 1)
		{
			return BUFFER_BOUNDARY;
		}
		
		if(index == BUFFER_BOUNDARY)
		{
			index = dataEnd;
			
			if(steps > 0)
			{
				steps--;
			}
		}
		
		steps = steps % dataSize;
		
		int relativeIndex = index - dataStart;
		
		if(relativeIndex < 0)
		{
			relativeIndex += bufferSize;
		}
		
		int finalRelativeIndex = (relativeIndex - steps) % dataSize;
		
		if(finalRelativeIndex < 0)
		{
			finalRelativeIndex += dataSize;
		}
		
		index = (dataStart + finalRelativeIndex) % bufferSize;
		
		return index;
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
		int bufferSize = this.buffer.getBufferSize();
		
		if(start < 0 || start >= bufferSize || end < 0 || end >= bufferSize)
		{
			return false;
		}
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
	 * Shifts the elements from the start of the circular buffer towards the removeIndex,
	 * overwriting the element at the removeIndex in the process.
	 * 
	 * @param removeIndex
	 * - The index of the element to be overwritten by the shifting.
	 * @author Sergio Morel
	 */
	protected void shiftFromStart(int removeIndex)
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
		
		if(this.buffer.start == BUFFER_BOUNDARY)
		{
			this.buffer.end = BUFFER_BOUNDARY;
		}
	}
	
	/**
	 * Shifts the elements from the start of the circular buffer to occupy
	 * the space corresponding to the range to be removed.
	 * 
	 * @param removeFrom
	 * - The start of the range that will be removed (inclusive).
	 * @param removeTo
	 * - The end of the range that will be removed (inclusive).
	 * @author Sergio Morel
	 */
	protected void shiftFromStart(int removeFrom, int removeTo)
	{
		int copyFromIndex = this.goPrevious(removeFrom);
		int copyToIndex = removeTo;
		
		while(copyFromIndex != BUFFER_BOUNDARY)
		{
			this.buffer.byteArray[copyToIndex] = this.buffer.byteArray[copyFromIndex];
			
			copyToIndex = this.goPrevious(copyToIndex);
			copyFromIndex = this.goPrevious(copyFromIndex);
		}
		
		this.buffer.start = this.goNext(copyToIndex);
		
		if(this.buffer.start == BUFFER_BOUNDARY)
		{
			this.buffer.end = BUFFER_BOUNDARY;
		}
	}
	
	/**
	 * Shifts the elements from the end of the circular buffer towards the removeIndex,
	 * overwriting the element at the removeIndex in the process.
	 * 
	 * @param removeIndex
	 * - The index of the element to be overwritten by the shifting.
	 * @author Sergio Morel
	 */
	protected void shiftFromEnd(int removeIndex)
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
		
		if(this.buffer.end == BUFFER_BOUNDARY)
		{
			this.buffer.start = BUFFER_BOUNDARY;
		}
	}
	
	/**
	 * Shifts the elements from the end of the circular buffer to occupy
	 * the space corresponding to the range to be removed.
	 * 
	 * @param removeFrom
	 * - The start of the range that will be removed (inclusive).
	 * @param removeTo
	 * - The end of the range that will be removed (inclusive).
	 * @author Sergio Morel
	 */
	protected void shiftFromEnd(int removeFrom, int removeTo)
	{
		int copyFromIndex = this.goNext(removeTo);
		int copyToIndex = removeFrom;
		
		while(copyFromIndex != BUFFER_BOUNDARY)
		{
			this.buffer.byteArray[copyToIndex] = this.buffer.byteArray[copyFromIndex];
			
			copyToIndex = this.goNext(copyToIndex);
			copyFromIndex = this.goNext(copyFromIndex);
		}
		
		this.buffer.end = this.goPrevious(copyToIndex);
		
		if(this.buffer.end == BUFFER_BOUNDARY)
		{
			this.buffer.start = BUFFER_BOUNDARY;
		}
	}
	
	/**
	 * Shifts elements towards the start of the buffer, beginning from the given index.
	 * 
	 * If there's an empty slot before the start index, it will be updated to create space.
	 * In the case where there's no space at the beginning, the first element in the buffer will be overwritten.
	 * 
	 * Note: After this operation, the element at the provided 'index' will be duplicated in its previous slot.
	 * It's expected that the caller replaces the element at 'index' after the shift.
	 * 
	 * @param index
	 * - The starting point for the shift. After the shift, this position will have a duplicate of its value in the previous slot.
	 * @author Sergio Morel
	 */
	protected void shiftToStart(int index)
	{
		if(this.buffer.start <= this.buffer.end)
		{
			int bufferSize = this.buffer.getBufferSize();
			
			if(this.buffer.start > 0)
			{
				this.buffer.start--;
			}
			else if(this.buffer.end < bufferSize - 1)
			{
				this.buffer.start = bufferSize - 1;
			}
		}
		else
		{
			if(this.buffer.start - 1 != this.buffer.end)
			{
				this.buffer.start--;
			}
		}
		
		int copyFromIndex = this.goNext(this.buffer.start);
		int copyToIndex = this.buffer.start;
		
		while(copyToIndex != index)
		{
			this.buffer.byteArray[copyToIndex] = this.buffer.byteArray[copyFromIndex];
			
			copyFromIndex = this.goNext(copyFromIndex);
			copyToIndex = this.goNext(copyToIndex);
		}
	}
	
	/**
	 * Shifts elements towards the end of the buffer, beginning from the given index.
	 * 
	 * If there's an empty slot after the end index, it will be updated to create space.
	 * In the case where there's no space at the end, the last element in the buffer will be overwritten.
	 * 
	 * Note: After this operation, the element at the provided 'index' will be duplicated in its previous slot.
	 * It's expected that the caller replaces the element at 'index' after the shift.
	 * 
	 * @param index
	 * - The starting point for the shift. After the shift, this position will have a duplicate of its value in the previous slot.
	 * @author Sergio Morel
	 */
	protected void shiftToEnd(int index)
	{
		if(this.buffer.start <= this.buffer.end)
		{
			int bufferSize = this.buffer.getBufferSize();
			
			if(this.buffer.end < bufferSize - 1)
			{
				this.buffer.end++;
			}
			else if(this.buffer.start > 0)
			{
				this.buffer.end = 0;
			}
		}
		else
		{
			if(this.buffer.end + 1 != this.buffer.start)
			{
				this.buffer.end++;
			}
		}
		
		int copyFromIndex = this.goPrevious(this.buffer.end);
		int copyToIndex = this.buffer.end;
		
		while(copyToIndex != index)
		{
			this.buffer.byteArray[copyToIndex] = this.buffer.byteArray[copyFromIndex];
			
			copyFromIndex = this.goPrevious(copyFromIndex);
			copyToIndex = this.goPrevious(copyToIndex);
		}
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Data Index: ").append(this.getIndex());
		sb.append("\nInternal Index: ").append(this.getInternalIndex());
		
		return sb.toString();
	}
}