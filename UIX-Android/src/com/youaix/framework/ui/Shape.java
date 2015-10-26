package com.youaix.framework.ui;

import com.youaix.framework.view.shape.ShapeView;

import android.view.View;
import android.view.ViewGroup;

public class Shape extends Element
{
	private ShapeView view = null;
	private int color = 0xff000000;
	private int colorHover = 0xff000000;
	private boolean isStyleStatable = false;
	private int type = ShapeView.TYPE_TRIANGLE;
	
	public Shape()
	{
		super();
	}
	
	public Shape setType(String type)
	{
		if ('t' == type.charAt(0)) this.type = ShapeView.TYPE_TRIANGLE;
		if ('c' == type.charAt(0)) this.type = ShapeView.TYPE_CIRCLE;
		if ('r' == type.charAt(0)) this.type = ShapeView.TYPE_RECTANGLE;
		return this;
	}
	
	public Shape setColor(int color)
	{
		this.color = color;
		if (this.view != null) this.view.setColor(color);
		return this;
	}
	
	public Shape setHoverColor(int color)
	{
		this.colorHover = color;
		this.isStyleStatable = true;
		return this;
	}
	
	@Override
	public boolean isStyleStatable()
	{
		return this.isStyleStatable;
	}
	
	@Override
	public StatableStyle generateStyle()
	{
		final int clr = this.color;
		final int clrHover = this.colorHover;
		return new StatableStyle()
		{
			@Override
			public void onHover(Element element)
			{
				Shape self = (Shape)element;
				self.setColor(clrHover);
			}

			@Override
			public void onRelease(Element element)
			{
				Shape self = (Shape)element;
				self.setColor(clr);
			}
		};
	}
	
	public View getContentView()
	{
		if (null == this.view) this.view = ShapeView.create(getContext(), this.type);
		ViewGroup.LayoutParams layout = this.getLayout();
		if (null != layout) this.view.setLayoutParams(layout);
		this.view.setColor(this.color);
		return this.view;
	}
}
