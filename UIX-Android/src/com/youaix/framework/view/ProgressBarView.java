package com.youaix.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class ProgressBarView extends View
{
	float percent = 0.0f;
	int borderWidth = 1;
	int borderColor = 0xff000000;
	int barColor = 0xff006699;
	
	Paint paint = null;
	
	public ProgressBarView(Context context)
	{
		super(context);
		paint = new Paint();
		paint.setAntiAlias(true);
	}
	
	public void setBarColor(int barColor)
	{
		this.barColor = barColor;
		this.invalidate();
	}
	
	public void setBorderColor(int borderColor)
	{
		this.borderColor = borderColor;
		this.invalidate();
	}
	
	public void setBorderWidth(int borderWidth)
	{
		this.borderWidth = borderWidth;
		this.invalidate();
	}
	
	public void setPercent(float percent)
	{
		this.percent = percent;
		this.invalidate();
	}
	
	protected void onDraw(Canvas canvas)
	{
		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();
		
		paint.setColor(this.borderColor);
		canvas.drawRect(0, 0, width, height, paint);
		
		paint.setColor(0xffffffff);
		canvas.drawRect(this.borderWidth, this.borderWidth, width - this.borderWidth, height - this.borderWidth, paint);
		
		paint.setColor(this.barColor);
		int bw = (int)((width - this.borderWidth) * this.percent);
		canvas.drawRect(this.borderWidth, this.borderWidth, bw, height - this.borderWidth, paint);
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
