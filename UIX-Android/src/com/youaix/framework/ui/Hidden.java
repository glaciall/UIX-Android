package com.youaix.framework.ui;

import android.view.View;

public class Hidden extends Element
{
	private String value = null;
	
	public Hidden()
	{
		super();
		// ...
	}
	
	public Hidden setValue(String value)
	{
		this.value = value;
		return this;
	}
	
	public String getValue()
	{
		return this.value;
	}
	
	public boolean isFormElement()
	{
		return true;
	}
	
	public View getContentView()
	{
		return null;
	}

}
