package com.youaix.framework.ui;

import android.view.Gravity;

public class Textarea extends Input
{
	public Textarea()
	{
		super();
		this.view.setSingleLine(false);
		this.view.setGravity(Gravity.TOP);
		this.setWidthPercent(1.0f);
		this.setHeight(60);
	}

	public String getValue()
	{
		return super.getValue();
	}
	
	public boolean isFormElement()
	{
		return true;
	}
	
	public Textarea setColor(int color)
	{
		return (Textarea)super.setColor(color);
	}
	
	@Override
	public Textarea setTextSize(int size)
	{
		return (Textarea)super.setTextSize(size);
	}
	
	@Override
	public Textarea setMaxLength(int length)
	{
		return (Textarea)super.setMaxLength(length);
	}
	
	@Override
	public Textarea setText(String text)
	{
		return (Textarea)super.setText(text);
	}
	
	@Override
	public Textarea setValue(String text)
	{
		return (Textarea)super.setValue(text);
	}
	
	@Override
	public Textarea setRichText(String text) throws Exception
	{
		return (Textarea)super.setRichText(text);
	}
	
	public Textarea appendRichText(String text) throws Exception
	{
		return (Textarea)super.appendRichText(text);
	}
	
	public Textarea setType(String type)
	{
		return (Textarea)super.setType(type);
	}
	
	@Override
	public Textarea setDefaultText(String text)
	{
		return (Textarea)super.setDefaultText(text);
	}
}
