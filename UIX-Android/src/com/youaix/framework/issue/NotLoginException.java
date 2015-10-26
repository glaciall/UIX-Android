package com.youaix.framework.issue;

public class NotLoginException extends PageException
{
	public NotLoginException(String message)
	{
		super(message);
	}
	
	public NotLoginException(Exception ex)
	{
		super(ex);
	}
}
