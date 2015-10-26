package com.youaix.framework.issue;

public abstract class PageException extends Exception
{
	public PageException(String message)
	{
		super(message);
	}
	
	public PageException(Exception ex)
	{
		super(ex);
	}
}
