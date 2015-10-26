package com.youaix.framework.mission;

public interface Callbackable
{
    public void echo(String mission, Object data);
	public void complete(String mission, Object data) throws Exception;
	public void error(String mission, Exception ex) throws Exception;
}
