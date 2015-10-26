package com.youaix.framework.ui;

import com.youaix.framework.view.CircleGifView;

import android.graphics.Movie;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class Gif extends Element
{
	private CircleGifView view = null;
	
	public Gif()
	{
		super();
		this.view = new CircleGifView(getContext());
	}
	
	public Gif setSrc(String uri)
	{
		return this;
	}
	
	public Gif setWidth(int width)
	{
        super.setWidth(width);
		return this;
	}
	
	public Gif setWidth(float width)
	{
		super.setWidth(width);
		return this;
	}
	
	public Gif setHeight(int height)
	{
        super.setHeight(height);
		return this;
	}
	
	public Gif setHeight(float height)
	{
		super.setHeight(height);
		return this;
	}
	
	public View getContentView()
	{
		LayoutParams layout = this.getLayout();
        if (layout != null) this.view.setLayoutParams(layout);
        return this.view;
	}
}
