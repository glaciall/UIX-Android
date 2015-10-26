package com.youaix.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.Spinner;

public class SpinnerView extends Spinner
{
	Paint paint = null;
	public SpinnerView(Context context)
	{
		super(context);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(0xff666666);
	}
	
	protected void onDraw(Canvas canvas)
	{
		/*
		paint.setStrokeWidth(Resolution.pixels(2));
		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();
		canvas.drawLine(0, 0, width, 0, paint);
		canvas.drawLine(0, height, 0, height, paint);
		canvas.drawLine(0, 0, 0, height, paint);
		canvas.drawLine(width, 0, width, height, paint);
		
		Option op = (Option)this.getSelectedItem();
		canvas.drawText(op.getText(), Resolution.pixels(5), height / 2.0f, paint);
		*/
	}
}
