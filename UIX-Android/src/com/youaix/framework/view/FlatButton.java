package com.youaix.framework.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.graphics.*;
import android.graphics.Paint.FontMetrics;

public final class FlatButton extends View
{
    int borderWidth = 0;
    int backgroundColor = 0xff000000;
    int borderColor = 0xff000000;
    int textColor = 0xffffffff;
    float textSize = 12.0f;
    int textAlign = Gravity.CENTER;

    String text = null;

	public FlatButton(Context context)
    {
		super(context);
	}

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int width = this.getMeasuredWidth();
        int height = this.getMeasuredHeight();

        Paint paint = new Paint();
        paint.setColor(this.borderColor);
        paint.setAntiAlias(true);
        canvas.drawRect(0, 0, width, height, paint);

        paint.setColor(this.backgroundColor);
        canvas.drawRect(this.borderWidth, this.borderWidth, width - this.borderWidth, height - this.borderWidth, paint);

        paint.setColor(this.textColor);
        paint.setTextSize(this.textSize);

        Paint.Align align = Paint.Align.CENTER;
        if (this.textAlign == Gravity.LEFT) align = Paint.Align.LEFT;
        if (this.textAlign == Gravity.RIGHT) align = Paint.Align.RIGHT;

        paint.setTextAlign(align);
        FontMetrics fm = paint.getFontMetrics();

        int h = (int)Math.ceil(fm.bottom - fm.top);
        int x = this.borderWidth;
        // FontMetrics.top的数值是个负数，其绝对值就是字符绘制边界到baseline的距离。
        int y = (int)(height - fm.bottom - fm.top) / 2;

        if (this.textAlign == Gravity.RIGHT) x = width - this.borderWidth;
        if (this.textAlign == Gravity.CENTER) x = width / 2;

        canvas.drawText(this.text, x, y, paint);

        // android.util.Log.e("flatbutton", "onDraw called");
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // android.util.Log.e("floatbutton", "measured");
        // setMeasuredDimension(this.width, this.height);
    }

    public void setText(String text)
    {
        this.text = text;
        this.invalidate();
    }

    public void setTextAlign(int align)
    {
        this.textAlign = align;
        this.invalidate();
    }

    public void setTextSize(float sp)
    {
        this.textSize = sp;
        this.invalidate();
    }

    public void setTextColor(int color)
    {
        this.textColor = color;
        this.invalidate();
    }

    public void setBorderColor(int color)
    {
        this.borderColor = color;
        this.invalidate();
    }

    public void setBackgroundColor(int color)
    {
        this.backgroundColor = color;
        this.invalidate();
    }

    public void setBorderWidth(int size)
    {
        this.borderWidth = size;
        this.invalidate();
    }
}
