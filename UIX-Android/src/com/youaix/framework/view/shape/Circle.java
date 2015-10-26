package com.youaix.framework.view.shape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Circle extends ShapeView
{
	private Paint paint = null;
	private int color = 0xffff0000;
	
	public Circle(Context context)
	{
		super(context);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(this.color);
	}
	
	public void setColor(int color)
	{
		this.color = color;
		paint.setColor(color);
		this.invalidate();
	}
	
	protected void onDraw(Canvas canvas)
	{
		float r = this.getMeasuredWidth() / 2;
		canvas.drawCircle(r, r, r, paint);
	}
}
