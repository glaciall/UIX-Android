package com.youaix.framework.view;

import com.youaix.framework.page.Resolution;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.RadioButton;

public class Indicator extends RadioButton
{
	Paint paint = null;
	public Indicator(Context context)
	{
		super(context);
		paint = new Paint();
		paint.setColor(0xffff0000);
		paint.setAntiAlias(true);
		this.setSoundEffectsEnabled(false);
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width = Resolution.pixels(8);
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void onDraw(Canvas canvas)
	{
		float r = this.getMeasuredWidth() / 2;
		paint.setColor(0x80333333);
		canvas.drawCircle(r, r, r, paint);
		paint.setColor(this.isChecked() ? 0xffffffff : 0xff333333);
		canvas.drawCircle(r, r, r - 2, paint);
	}
}
