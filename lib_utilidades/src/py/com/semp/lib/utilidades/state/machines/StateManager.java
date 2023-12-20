package py.com.semp.lib.utilidades.state.machines;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import py.com.semp.lib.utilidades.configuration.Values;
import py.com.semp.lib.utilidades.exceptions.ShutdownException;
import py.com.semp.lib.utilidades.exceptions.StateNotFoundException;
import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;
import py.com.semp.lib.utilidades.log.Logger;
import py.com.semp.lib.utilidades.log.LoggerManager;
import py.com.semp.lib.utilidades.shutdown.ShutdownCapable;

/**
 * This abstract class manages the execution of states in a state machine. It provides a framework
 * for managing state transitions, handling shutdown requests, and ensuring thread-safe operation.
 * Implementing classes should define the specific states and transitions of the state machine.
 */
public abstract class StateManager implements Runnable, ShutdownCapable
{
	private volatile boolean shutdownRequested = false;
	private final Map<String, State> states = new LinkedHashMap<>();
	private ReentrantLock lock = new ReentrantLock();
	
	/**
     * Initiates the execution of the state machine. This method is called when the class is run
     * as a thread. It handles {@link StateNotFoundException} by logging the error and ensures
     * proper shutdown handling.
     */
	@Override
	public void run()
	{
		try
		{
			this.loadStates();
			this.executeStates();
		}
		catch(StateNotFoundException | InterruptedException e)
		{
			Logger logger = LoggerManager.getLogger(Values.Constants.UTILITIES_CONTEXT);
			
			logger.error(e);
		}
		finally
		{
			this.onShutdown();
		}
	}
	
	protected abstract void onShutdown();

	/**
	 * Provides the Map of states managed by this state machine.
	 *
	 * @return A Map where keys are state identifiers and values are State objects.
	 */
	protected Map<String, State> getStates()
	{
		return this.states;
	}

	/**
	 * Adds a new state to the state map.
	 * 
	 * @param key
	 * - String containing the key for the state to be added.
	 * @param state
	 * - The {@link State} to be added.
	 */
	protected void addState(String key, State state)
	{
		this.lock.lock();
		
		try
		{
			this.states.put(key, state);
		}
		finally
		{
			this.lock.unlock();
		}
	}

	/**
     * Implement this method to define the logic for loading states into the state machine.
     */
	public abstract void loadStates();
	
	/**
     * Executes the states in the state machine. This method iterates through the states based on
     * their transition logic and executes each one until there are no more states to execute, a
     * state transition error occurs, or a shutdown is requested. 
     *
     * @throws StateNotFoundException if a specified next state is not found in the state machine.
     * @throws InterruptedException if the thread executing the state machine is interrupted.
     */
	public void executeStates() throws StateNotFoundException, InterruptedException
	{
		Map<String,State> states = this.getStates();
		
		if(states.isEmpty())
		{
			return;
		}
		
		String nextStateKey = null;
		
		this.lock.lock();
		
		try
		{
			Iterator<String> iterator = states.keySet().iterator();
			
			nextStateKey = iterator.next();
		}
		finally
		{
			this.lock.unlock();
		}
		
		while(nextStateKey != null)
		{
			if(this.isShuttingdown())
			{
				break;
			}
			
			if(Thread.interrupted())
			{
				String methodName = "void StateManager::executeStates() ";
				String errorMessage = MessageUtil.getMessage(Messages.INTERRUPTED_ERROR, methodName);
				
				throw new InterruptedException(errorMessage);
			}
			
			State meterState = null;
			
			this.lock.lock();
			
			try
			{
				meterState = states.get(nextStateKey);
			}
			finally
			{
				this.lock.unlock();
			}
			
			if(meterState == null)
			{
				String errorMessage = MessageUtil.getMessage(Messages.STATE_NOT_FOUND_ERROR, nextStateKey);
				
				throw new StateNotFoundException(errorMessage);
			}
			
			nextStateKey = meterState.executeState();
		}
	}
	
	@Override
	public ShutdownCapable shutdown() throws ShutdownException
	{
		this.shutdownRequested = true;
		
		return this;
	}

	@Override
	public boolean isShuttingdown()
	{
		return this.shutdownRequested;
	}
}