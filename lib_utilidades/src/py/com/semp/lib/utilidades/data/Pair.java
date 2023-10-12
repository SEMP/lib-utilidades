package py.com.semp.lib.utilidades.data;

import java.io.Serializable;

import py.com.semp.lib.utilidades.utilities.Utilities;

/**
 * A utility class to hold two related objects or values.
 *
 * @author Sergio Morel
 * 
 * @param <A> The type of the first element.
 * @param <B> The type of the second element.
 */
public class Pair<A, B> implements Serializable
{
	private static final long serialVersionUID = 4254309979262985743L;
	
	private A first;
	private B second;
	
	public Pair()
	{
		super();
	}
	
	public Pair(A first, B second)
	{
		super();
		
		this.first = first;
		this.second = second;
	}
	
	public A getFirst()
	{
		return this.first;
	}
	
	public void setFirst(A first)
	{
		this.first = first;
	}
	
	public B getSecond()
	{
		return this.second;
	}
	
	public void setSecond(B second)
	{
		this.second = second;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
		{
			return true;
		}
		
		if(obj == null || !obj.getClass().equals(this.getClass()))
		{
			return false;
		}
		
		@SuppressWarnings("unchecked")
		Pair<A, B> pair = (Pair<A, B>)obj;
		
		return Utilities.equals(this.first, pair.first) && Utilities.equals(this.second, pair.second);
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		
		result = prime * result + (this.first == null ? 0 : this.first.hashCode());
		result = prime * result + (this.second == null ? 0 : this.second.hashCode());
		
		return result;
	}
	
	@Override
	public String toString()
	{
		return "(" + this.first + ", " + this.second + ")";
	}
}