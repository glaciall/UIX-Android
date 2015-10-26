package com.youaix.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;

public class UlView extends ViewGroup
{
	public UlView(Context context)
	{
		super(context);
		this.setWillNotDraw(false);
	}

	protected void onDraw(Canvas canvas)
	{
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(0xffff0000);
		int top = 0;
		for (int i = 0, l = this.getChildCount(); i < l; i++)
		{
			View child = this.getChildAt(i);
			int h = child.getMeasuredHeight();
			canvas.drawCircle(10, top + h / 2, 5, paint);
			top += h;
		}
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		int wMode = MeasureSpec.getMode(widthMeasureSpec);
		int hMode = MeasureSpec.getMode(heightMeasureSpec);

		// width = 100, wMode = AT_MOST

		// EXACTLY, UNSPECIFIED, AT_MOST
		// WRAP_CONTENT, FILL_PARENT

		int totalWidth = 0;
		int totalHeight = 0;
		// 需要给每个子元素确定尺寸
		for (int i = 0, l = this.getChildCount(); i < l; i++)
		{
			View child = this.getChildAt(i);
			
			ViewGroup.LayoutParams layout = child.getLayoutParams();
			
			child.measure(MeasureSpec.makeMeasureSpec(-2, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(-2, MeasureSpec.UNSPECIFIED));

			int w = child.getMeasuredWidth();
			int h = child.getMeasuredHeight();

			totalHeight += h;
			totalWidth = Math.max(totalWidth, w);
		}
		totalWidth += 20;

		// 确定自己的尺寸
		super.onMeasure(MeasureSpec.makeMeasureSpec(totalWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY));
	}

	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		int l = 20;
		int t = 0;
		int r = 0;
		int b = 0;
		for (int i = 0, s = this.getChildCount(); i < s; i++)
		{
			View child = this.getChildAt(i);
			r = child.getMeasuredWidth() + 20;
			b = child.getMeasuredHeight() + t;
			child.layout(l, t, r, b);
			t += child.getMeasuredHeight();
		}
	}

}
