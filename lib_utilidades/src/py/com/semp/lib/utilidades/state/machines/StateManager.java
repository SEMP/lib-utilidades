package py.com.semp.lib.utilidades.state.machines;

import java.util.Iterator;
import java.util.LinkedHashMap;

public interface StateManager extends Runnable
{
	@Override
	default void run()
	{
		this.executeStates();
	}
	
	public void loadStates();
	
	default public void executeStates()
	{
		LinkedHashMap<String,State> states = this.getStates();
		
		if(states.isEmpty())
		{
			return;
		}
		
		Iterator<String> iterator = states.keySet().iterator();
		
		String nextState = iterator.next();
		
		while(nextState != null)
		{
			State meterState = states.get(nextState);
			
			nextState = meterState.executeState();
		}
	}
	
	public LinkedHashMap<String, State> getStates();
}