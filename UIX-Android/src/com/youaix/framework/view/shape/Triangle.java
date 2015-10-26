package com.youaix.framework.view.shape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class Triangle extends ShapeView
{
	public static final int DIR_DOWN = 0x01;
	public static final int DIR_UP = 0x02;
	public static final int DIR_LEFT = 0x03;
	public static final int DIR_RIGHT = 0x04;
	
	private int color = 0xff000000;
	private int direction = 0x01;
	private Path path = null;
	private Paint paint = null;
	
	public Triangle(Context context)
	{
		super(context);
		path = new Path();
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);  
	}
	
	public void setColor(int color)
	{
		this.color = color;
		this.paint.setColor(this.color);
		this.invalidate();
	}
	
	public void setDirection(int dir)
	{
		this.direction = dir;
	}
	
	protected void onDraw(Canvas canvas)
	{
		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();
		if (this.direction == DIR_DOWN)
		{
			this.path.reset();
			this.path.moveTo(0, 0);
			this.path.lineTo(width / 2, height);
			this.path.lineTo(width, 0);
			this.path.close();
		}
		
		canvas.drawPath(this.path, this.paint);
	}
}
