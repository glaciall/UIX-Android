package com.youaix.framework.page;

public interface ShareListener
{
	public void onSuccess();
	public void onFailure(int errorCode, String errorMessage);
	public void onCancel();
}