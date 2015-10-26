package com.youaix.framework.mission;

public class Timeout extends Mission
{
	private int timeout;
	public Timeout(String mission, int timeout)
	{
		super(mission, PageManager.getInstance().getCurrent());
		this.timeout = timeout;
	}
	
	public Object handle() throws Exception
	{
		Thread.sleep(this.timeout);
		return null;
	}
}
