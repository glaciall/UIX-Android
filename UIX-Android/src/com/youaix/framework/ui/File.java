package com.youaix.framework.ui;

import com.youaix.framework.common.Schema;

import android.view.View;

public class File extends Element
{
	private String uri = null;
	
	public File()
	{
		super();
	}
	
	public File setSrc(String src)
	{
		return this.setValue(src);
	}
	
	public File setValue(String src)
	{
		this.uri = src;
		return this;
	}
	
	public Schema.Uri getValue()
	{
		return Schema.uri(this.uri);
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
