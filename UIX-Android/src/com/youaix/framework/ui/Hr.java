package com.youaix.framework.ui;

import com.youaix.framework.page.Resolution;
import com.youaix.framework.view.HorizontalLine;

import android.view.View;
import android.view.ViewGroup;

public class Hr extends Element
{
    private HorizontalLine view = null;

    public Hr()
    {
    	super();
        this.view = new HorizontalLine(getContext());
        super.setWidth(1.0f);
        this.setSize(1);
    }

	public Hr setWidth(int width)
    {
        super.setWidth(width);
		return this;
	}

    public Hr setWidth(float width)
    {
        super.setWidth(width);
        return this;
    }

    public Hr setSize(int size)
    {
        super.setHeight(size);
        this.view.setStrokeWidth(Resolution.pixels(size));
        return this;
    }

    public Hr setBackgroundColor(int color)
    {
        this.view.setBackgroundColor(color);
        return this;
    }

    public Hr setColor(int color)
    {
        this.view.setColor(color);
        return this;
    }

	public View getContentView()
    {
        return this.view;
	}
}
