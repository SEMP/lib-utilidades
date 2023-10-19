package py.com.semp.lib.utilidades.data;

import java.io.Serializable;
import java.util.Objects;

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
		
		return Objects.equals(this.first, pair.first) && Objects.equals(this.second, pair.second);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.first, this.second);
	}
	
	@Override
	public String toString()
	{
		return "(" + this.first + ", " + this.second + ")";
	}
}