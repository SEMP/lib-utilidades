package py.com.semp.lib.utilidades.utilities;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory
{
	private final String namePrefix;
	
	public NamedThreadFactory(String name)
	{
		this.namePrefix = name + "_";
	}
	
	@Override
	public Thread newThread(Runnable runnable)
	{
		Thread thread = new Thread(runnable);
		
		thread.setName(this.getName(thread.getId()));
		
		if(thread.isDaemon())
		{
			thread.setDaemon(false);
		}
		
		if(thread.getPriority() != Thread.NORM_PRIORITY)
		{
			thread.setPriority(Thread.NORM_PRIORITY);
		}
		
		return thread;
	}
	
	private String getName(long id)
	{
		return this.namePrefix + id;
	}
}