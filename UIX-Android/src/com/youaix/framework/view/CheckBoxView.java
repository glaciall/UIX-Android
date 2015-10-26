package com.youaix.framework.view;

import com.youaix.framework.page.Resolution;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.CheckBox;

public class CheckBoxView extends CheckBox
{
	Paint paint = null;
	public CheckBoxView(Context context)
	{
		super(context);
		paint = new Paint();
		paint.setAntiAlias(true);
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		int wMode = MeasureSpec.getMode(widthMeasureSpec);
		int hMode = MeasureSpec.getMode(heightMeasureSpec);
		
		if (wMode != MeasureSpec.EXACTLY) width = Resolution.pixels(16);
		if (hMode != MeasureSpec.EXACTLY) height = Resolution.pixels(16);
		if (width != height) height = width;
		super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
	}
	
	protected void onDraw(Canvas canvas)
	{
		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();
		
		paint.setColor(this.isChecked() ? 0xff0066ff : 0xffcccccc);
		paint.setStrokeWidth(Resolution.pixels(2));
		
		canvas.drawLine(0, 0, width, 0, paint);
		canvas.drawLine(0, height, width, height, paint);
		canvas.drawLine(0, 0, 0, height, paint);
		canvas.drawLine(width, 0, width, height, paint);
		
		// canvas.drawRect(0,  0, width, height, paint);
		// 1 - 2
		// 1/6 , 1/2  - 2/5 , 2/3
		// 2/5 , 2/3  - 2/3 , 1/5
		int x1 = (int)(width * (1f / 6f));
		int y1 = (int)(height * (1f / 2f));
		int x2 = (int)(width * (2f / 5f));
		int y2 = (int)(width * (5f / 6f));
		canvas.drawLine(x1, y1, x2, y2, paint);
		
		x1 = x2;
		y1 = y2;
		
		x2 = (int)(width * (5f / 6f));
		y2 = (int)(width * (1f / 5f));
		canvas.drawLine(x1, y1, x2, y2, paint);
	}
}
