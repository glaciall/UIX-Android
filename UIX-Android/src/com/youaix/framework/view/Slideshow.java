package com.youaix.framework.view;

import java.util.*;
import android.graphics.*;
import android.content.Context;
import android.view.View;

public class Slideshow extends View implements Runnable
{
    private static final int MAX_COUNT = 5;
    private static final int DIRECTION_LEFT = 0x01;         // 图片向左
    private static final int DIRECTION_RIGHT = 0x02;        // 图片向右

    int count = 0;
    int currentIndex = 0;
    int lastIndex = 0;
    int status = 0x00;
    int direction = 0x00;
    int scrollPercent = 0;          // 滚动百分比

    int dotSize = 5;
    int dotBorderSize = 2;
    int dotMargin = 3;

    int width = 320;
    int height = 80;

    ArrayList<Bitmap> pictures = null;

	public Slideshow(Context context)
    {
		super(context);
        pictures = new ArrayList<Bitmap>(5);
	}

    public void addPicture(Bitmap image) throws Exception
    {
        if (this.pictures.size() > MAX_COUNT) throw new Exception("Exceeds max picture count");
        this.pictures.add(image);
        float ratio = (float)image.getWidth() / (float)this.width;
        this.height = (int)(ratio * image.getHeight());
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public void setDotStyles(int dotSize, int dotBorderSize, int dotMargin)
    {
        this.dotSize = dotSize;
        this.dotBorderSize = dotBorderSize;
        this.dotMargin = dotMargin;
    }

    public void onDraw(Canvas canvas)
    {
        int count = this.pictures.size();
        Paint paint = new Paint();

        // 画图图，要考虑当前位置
        Bitmap currImage = this.pictures.get(this.currentIndex);
        Bitmap lastImage = this.pictures.get(this.lastIndex);

        // 先画底下那张，也就是lastImage
        // canvas.drawBitmap(lastImage, 0.0f, 0.0f, paint);

        // 再画上面这张，也就是currImage
        canvas.drawBitmap(currImage, 0.0f, 0.0f, paint);

        // 画小点点
        int top = this.height - this.dotSize - this.dotBorderSize - this.dotSize;
        int left = this.width - (count * (this.dotSize + this.dotBorderSize + this.dotMargin));

        for (int i = 0; i < count; i++)
        {
            paint.setColor(0xaaffffff);
            canvas.drawCircle(left, top, this.dotBorderSize + this.dotSize, paint);

            paint.setColor(i == this.currentIndex ? 0xffffffff : 0xff000000);
            canvas.drawCircle(left, top, this.dotSize, paint);

            left += this.dotSize + this.dotBorderSize + this.dotMargin;
        }
    }

    public int getCurrentIndex()
    {
        return this.currentIndex;
    }

    private final void moveNextIndex(int direction)
    {
        currentIndex += direction == 1 ? 1 : -1;
        if (currentIndex >= count) currentIndex = 0;
        if (currentIndex < 0) currentIndex = count - 1;
    }

    public void run()
    {
        // ...
        while (true)
        {

            android.util.Log.e("slideshow-thread", "Current: " + System.currentTimeMillis());
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(this.width, this.height);
        android.util.Log.e("slideshow", "onMeasure: " + this.width + "," + this.height);
    }
}
