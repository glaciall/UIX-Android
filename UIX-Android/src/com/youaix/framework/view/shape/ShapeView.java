package com.youaix.framework.view.shape;

import android.content.Context;
import android.view.View;

public abstract class ShapeView extends View
{
	public static final int TYPE_TRIANGLE = 0x01;
	public static final int TYPE_RECTANGLE = 0x02;
	public static final int TYPE_CIRCLE = 0x03;
	
	public ShapeView(Context context)
	{
		super(context);
	}
	
	public abstract void setColor(int color);
	
	public static ShapeView create(Context context, int type)
	{
		if (type == TYPE_TRIANGLE) return new Triangle(context);
		if (type == TYPE_CIRCLE) return new Circle(context);
		if (type == TYPE_RECTANGLE) return null;
		return null;
	}
}
