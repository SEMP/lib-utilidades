package py.com.semp.lib.utilidades.data;

import java.util.Iterator;

public class CircularByteBufferIterator implements Iterator<Byte>
{
	private static final int EMPTY_INDEX = CircularByteBuffer.EMPTY_INDEX;
	
	private CircularByteBuffer buffer;
	private int index;
	
	
	public CircularByteBufferIterator(CircularByteBuffer buffer)
	{
		this.buffer = buffer;
		this.index = buffer.start;
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
		int start = this.buffer.start;
		
		return this.index == start;
	}
	
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
		
		byte[] byteArray = this.buffer.byteArray;
		
		byte data = byteArray[this.index];
		
		this.goNext();
		
		return data;
	}
	
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
		
		this.index++;
		
		if(this.index == this.buffer.getBufferSize())
		{
			this.index = 0;
		}
		
		return this.index;
	}
	
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
		
		index++;
		
		if(index == this.buffer.getBufferSize())
		{
			index = 0;
		}
		
		return index;
	}
	
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