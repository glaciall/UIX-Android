package com.youaix.framework.mission;

import com.youaix.framework.page.Page;
import com.youaix.framework.page.PageManager;
import com.youaix.framework.page.UIXApplication;

public abstract class Mission extends Thread
{
	private String mission = null;
	protected Callbackable receiver = null;
	protected UIXApplication application = null;
	
	public Mission(String mission, Callbackable receiver)
	{
		this.mission = mission;
		this.receiver = receiver;
		this.application = ((UIXApplication)((Page)this.receiver).getApplication());
		this.application.newMission(mission, this);
	}
	
	protected void reportProgress(float percent)
	{
		try
		{
			if (!isCurrent()) return;
			this.receiver.echo("progress:" + this.mission, new Float(percent));
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	public void stopMission()
	{
		try
		{
			super.stop();
			this.application.missionCompleted(this.mission);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	public void pauseMission()
	{
		try
		{
			super.suspend();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public void resumeMission()
	{
		try
		{
			super.resume();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	public Object getData(String field)
	{
		return null;
	}
	
	protected boolean isCurrent()
	{
		return PageManager.getInstance().isCurrent(this.receiver);
	}
	
	public final void run()
	{
		boolean callback = true;
		try
		{
			Object data = this.handle();
			Page caller = (Page)this.receiver;
			callback = (PageManager.getInstance().isCurrent(caller));
			if (callback) this.receiver.echo(this.mission, data);
		}
		catch(Exception ex)
		{
			if (callback) this.receiver.echo(this.mission, ex);
		}
	}
	
	public final void go()
	{
		this.start();
	}
	
	public abstract Object handle() throws Exception;
}
