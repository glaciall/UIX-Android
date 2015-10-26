package com.youaix.framework.ui;

import android.view.View;

public class Option extends Element
{
	private String text;
	private String value;

	@Override
	public String toString()
	{
		return text;
	}

	public Option()
	{
		super();
	}

	@Override
	public View getContentView()
	{
		return null;
	}

	public String getText()
	{
		return text;
	}

	public Option setText(String text)
	{
		this.text = text;
		return this;
	}

	public String getValue()
	{
		return value;
	}

	public Option setValue(String value)
	{
		this.value = value;
		return this;
	}

}
