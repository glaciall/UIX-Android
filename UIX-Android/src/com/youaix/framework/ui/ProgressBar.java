package com.youaix.framework.ui;

import com.youaix.framework.view.ProgressBarView;

import android.view.View;

public class ProgressBar extends Element
{
	private ProgressBarView view = null;
	
	public ProgressBar()
	{
		super();
		this.view = new ProgressBarView(getContext());
	}
	
	public ProgressBar setBorderColor(int color)
	{
		this.view.setBorderColor(color);
		return this;
	}
	
	public ProgressBar setBorderWidth(int borderWidth)
	{
		this.view.setBorderWidth(borderWidth);
		return this;
	}
	
	public ProgressBar setBarColor(int barColor)
	{
		this.view.setBarColor(barColor);
		return this;
	}
	
	public ProgressBar setPercent(float percent)
	{
		this.view.setPercent(percent);
		return this;
	}
	
	public View getContentView()
	{
		return this.view;
	}
}
