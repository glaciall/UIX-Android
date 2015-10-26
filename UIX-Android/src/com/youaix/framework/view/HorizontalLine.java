package com.youaix.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class HorizontalLine extends View
{
    public static final int STYLE_SOLID = 0x01;
    public static final int STYLE_DOTTED = 0x02;
    public static final int STYLE_DASHED = 0x03;

    private int paddingTop = 0;
    private int paddingBottom = 0;
    private int strokeWidth = 1;
    private int color = 0xff000000;
    private Paint paint = new Paint();
    
	public HorizontalLine(Context context)
    {
		super(context);
		paint.setAntiAlias(true);
	}

    public void setStrokeWidth(int size)
    {
        this.strokeWidth = size;
        this.invalidate();
    }

    public void setColor(int color)
    {
        this.color = color;
        this.invalidate();
    }

    public void onDraw(Canvas canvas)
    {
        int width = this.getMeasuredWidth();
        int height = this.getMeasuredHeight();
        paint.setColor(this.color);
        paint.setStrokeWidth(height);
        canvas.drawLine(0, ((float)this.strokeWidth) / 2 + height / 2, width, ((float)this.strokeWidth) / 2 + height / 2, paint);
    }
}
