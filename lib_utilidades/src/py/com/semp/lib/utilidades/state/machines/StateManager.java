package py.com.semp.lib.utilidades.state.machines;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import py.com.semp.lib.utilidades.configuration.Values;
import py.com.semp.lib.utilidades.exceptions.ShutdownException;
import py.com.semp.lib.utilidades.exceptions.StateNotFoundException;
import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;
import py.com.semp.lib.utilidades.log.Logger;
import py.com.semp.lib.utilidades.log.LoggerManager;
import py.com.semp.lib.utilidades.shutdown.ShutdownCapable;
import py.com.semp.lib.utilidades.utilities.Utilities;

/**
 * This abstract class manages the execution of states in a state machine. It provides a framework
 * for managing state transitions, handling shutdown requests, and ensuring thread-safe operation.
 * Implementing classes should define the specific states and transitions of the state machine.
 * 
 * @author Sergio Morel
 */
public abstract class StateManager implements Runnable, ShutdownCapable
{
	private final AtomicBoolean shutdownRequested = new AtomicBoolean(false);
	private final AtomicBoolean statesFrozen = new AtomicBoolean(false);
	protected final Map<String, State> states = new LinkedHashMap<>();
	private String initialStateKey;
	protected final ReentrantLock lock = new ReentrantLock();
	protected volatile Logger logger;
	
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
			this.statesFrozen.set(true);
			this.executeStates();
		}
		catch(Exception e)
		{
			this.handleException(e);
		}
		finally
		{
			this.finalizeStateMachine();
			this.statesFrozen.set(false);
		}
	}
	
	/**
	 * Handles and logs the exception.
	 * 
	 * @param exception
	 * - Exception to handle.
	 */
	private void handleException(Exception exception)
	{
		Logger logger = this.getLogger();
		
		if(exception instanceof InterruptedException)
		{
			try
			{
				Thread.currentThread().interrupt();
			}
			catch(SecurityException e)
			{
				logger.error(e);
			}
			
			try
			{
				this.shutdown();
			}
			catch(ShutdownException e)
			{
				exception.addSuppressed(e);
			}
			
			logger.debug(exception);
			
			return;
		}
		
		logger.error(exception);
	}
	
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
	 * Retrieves the {@link Logger} instance associated with this state manager. 
	 * If the logger has not been initialized, it initializes a new logger using the 
	 * {@link LoggerManager}.
	 *
	 * @return The {@link Logger} instance used for logging messages and errors.
	 */
	protected Logger getLogger()
	{
		if(this.logger == null)
		{
			this.logger = LoggerManager.getLogger(Values.Constants.UTILITIES_CONTEXT);
		}
		
		return this.logger;
	}
	
	protected void setLogger(Logger logger)
	{
		this.logger = logger;
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
		Objects.requireNonNull(key, "key");
		Objects.requireNonNull(state, "state");
		
		if(this.statesFrozen.get())
		{
			String className = Utilities.coalesce(this.getClass().getCanonicalName(), this.getClass().getName());
			String stateClassName = Utilities.coalesce(state.getClass().getCanonicalName(), state.getClass().getName());
			
			String method = String.format("[%s, %s] void %s::addState(String, State)",key, stateClassName, className);
			
			String errorMessage = MessageUtil.getMessage(Messages.EDIT_STATE_MACHINE_IN_EXECUTION_ERROR, method);
			
			throw new IllegalStateException(errorMessage);
		}
		
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
	
	public void setInitialStateKey(String initialStateKey)throws StateNotFoundException
	{
		Objects.requireNonNull(initialStateKey, "initialStateKey");
		
		if(this.statesFrozen.get())
		{
			String className = Utilities.coalesce(this.getClass().getCanonicalName(), this.getClass().getName());
			
			String method = String.format("void %s::setInitialStateKey(String)", className);
			
			String errorMessage = MessageUtil.getMessage(Messages.EDIT_STATE_MACHINE_IN_EXECUTION_ERROR, method);
			
			throw new IllegalStateException(errorMessage);
		}
		
		if(!this.states.containsKey(initialStateKey))
		{
			String errorMessage = MessageUtil.getMessage(Messages.STATE_NOT_FOUND_ERROR, initialStateKey);
			
			throw new StateNotFoundException(errorMessage);
		}
		
		this.initialStateKey = initialStateKey;
	}
	
	public String getInitialStateKey()
	{
		return this.initialStateKey;
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
		Map<String, State> states = this.getStates();
		
		if(states.isEmpty())
		{
			return;
		}
		
		String nextStateKey = this.initialStateKey;
		
		if(nextStateKey == null)
		{
			Iterator<String> iterator = states.keySet().iterator();
			
			nextStateKey = iterator.next();
		}
		
		Logger logger = this.getLogger();
		
		while(nextStateKey != null)
		{
			if(this.isShuttingDown())
			{
				break;
			}
			
			if(Thread.interrupted())
			{
				String className = Utilities.coalesce(this.getClass().getCanonicalName(), this.getClass().getName());
				String methodName = String.format("void %s::executeStates()", className);
				String errorMessage = MessageUtil.getMessage(Messages.INTERRUPTED_ERROR, methodName);
				
				throw new InterruptedException(errorMessage);
			}
			
			State state = states.get(nextStateKey);
			
			if(state == null)
			{
				String errorMessage = MessageUtil.getMessage(Messages.STATE_NOT_FOUND_ERROR, nextStateKey);
				
				throw new StateNotFoundException(errorMessage);
			}
			
			logger.debug(nextStateKey);
			
			final String current = nextStateKey;
			
			try
			{
				nextStateKey = state.executeState();
			}
			catch(Exception e)
			{
				String errorMessage = MessageUtil.getMessage(Messages.SUPPRESSED_EXCEPTION, String.format("State: %s", current));
				
				Exception wrapped = new Exception(errorMessage, e);
				
				this.handleException(wrapped);
				
				return;
			}
		}
	}
	
	/**
	 * To be executed after the state machine finishes executing.
	 */
	protected abstract void finalizeStateMachine();
	
	/**
	 * To be executed when the {@code shutdown()} method has been called.
	 */
	protected abstract void onShutdown();
	
	@Override
	public ShutdownCapable shutdown() throws ShutdownException
	{
		if(this.shutdownRequested.compareAndSet(false, true))
		{
			this.onShutdown();
		}
		
		return this;
	}
	
	@Override
	public boolean isShuttingDown()
	{
		return this.shutdownRequested.get();
	}
}