package com.youaix.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class CircleGifView extends View
{
	int index = 0;
	int color = 0xff333333;
	long time = 0L;
	int[] colors = new int[]{ 0xFF7F9ECE, 0xFF7F9ECE, 0xFFA0B9DD, 0xFFA0B9DD, 0xFFC8DDF0, 0xFFC8DDF0, 0xFFC8DDF0, 0xFFC8DDF0 };
	Paint paint = new Paint();
	
	public CircleGifView(Context context)
	{
		super(context);
		paint.setAntiAlias(true);
		this.time = System.currentTimeMillis();
	}
	
	private final float toRad(int angle)
	{
		return (float) ((float)angle * Math.PI / 180f);
	}
	
	protected void onDraw(Canvas canvas)
	{
		this.index = (int)((System.currentTimeMillis() - this.time) / 100);
		this.index %= 8;
		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();
		
		int square = Math.min(width, height);
		color += 0xff;
		
		int r = square / 2;
		int angle = 0;
		int cr = r / 4;
		int f = (r - cr);
		for (int i = 0; i < 8; i++)
		{
			float x = (float)(Math.sin(toRad(angle)) * f);
			float y = (float)(Math.cos(toRad(angle)) * f);
			
			paint.setColor(this.colors[(this.index + i) % 8]);
			canvas.drawCircle(x + r, y + r, cr, paint);
			angle += 45;
		}
		// this.index += 1;
		// this.index %= 8;
		
		this.postInvalidateDelayed(100);
	}
}
