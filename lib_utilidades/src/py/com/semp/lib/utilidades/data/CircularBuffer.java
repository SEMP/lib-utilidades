package py.com.semp.lib.utilidades.data;

public class CircularBuffer<T>
{
	private Object[] buffer;
	private int size; // Maximum size of buffer
	private int start; // Index of the oldest data
	private int end; // Index one step past the newest data
	
	public CircularBuffer(int size)
	{
		if(size <= 0)
		{
			throw new IllegalArgumentException("Size should be greater than 0.");
		}
		
		this.size = size;
		this.buffer = new Object[size];
		this.start = 0;
		this.end = 0;
	}
	
	public void add(T item)
	{
		buffer[end] = item;
		end = (end + 1) % size;
		
		// If the buffer is full, increment the start as well to overwrite the
		// oldest data.
		if(end == start)
		{
			start = (start + 1) % size;
		}
	}
	
	@SuppressWarnings("unchecked")
	public T removeFirst()
	{
		if(isEmpty())
		{
			return null; // Buffer is empty
		}
		
		T item = (T)buffer[start];
		start = (start + 1) % size;
		return item;
	}
	
	public boolean isEmpty()
	{
		return start == end;
	}
	
	public boolean isFull()
	{
		return (end + 1) % size == start;
	}
	
	public int capacity()
	{
		return size;
	}
	
	public int size()
	{
		if(end >= start)
		{
			return end - start;
		}
		else
		{
			return size - start + end;
		}
	}
	
	public void clear()
	{
		start = 0;
		end = 0;
	}
}

// Usage example:
