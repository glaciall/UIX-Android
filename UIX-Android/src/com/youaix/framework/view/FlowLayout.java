package com.youaix.framework.view;

import java.util.*;

import com.youaix.framework.common.ImageCache;
import com.youaix.framework.ui.Attribute;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

public class FlowLayout extends ViewGroup
{
	public static final int BG_LEFT = -10000001;
	public static final int BG_CENTER = -10000002;
	public static final int BG_RIGHT = -10000003;
	public static final int BG_TOP = -10000004;
	public static final int BG_MIDDLE = -10000005;
	public static final int BG_BOTTOM = -10000006;
	
    private int topRadius = 0;
    private int bottomRadius = 0;
    private Bitmap corner = null;

    private int borderWidthTop = 0;
    private int borderWidthRight = 0;
    private int borderWidthBottom = 0;
    private int borderWidthLeft = 0;
    
    private int borderColorTop = 0;
    private int borderColorRight = 0;
    private int borderColorBottom = 0;
    private int borderColorLeft = 0;

    private int backgroundColor = 0x00ffffff;
    private String backgroundImageUri = null;
    
    private int backgroundImageWidth = -2;
    private int backgroundImageHeight = -2;								// 默认WRAP_CONTENT
    
    private int backgroundImageTop = BG_TOP;
    private int backgroundImageLeft = BG_LEFT;								// 默认从左上开始
    
    private boolean backgroundImageRepeatX = true;
    private boolean backgroundImageRepeatY = true;						// 默认全重复
    
    private boolean resized = false;										// 是否己经经过缩放了
    
    int offsetTop = Attribute.POSITION_UNSPECIFIED;
	int offsetLeft = Attribute.POSITION_UNSPECIFIED;
    
    public void debug()
    {
    	super.debug(100);
    }
    
    protected void onDetachedFromWindow()
    {
    	if (this.corner != null && !this.corner.isRecycled())
    	{
    		this.corner.recycle();
    		this.corner = null;
    	}
    	// TODO: 释放背景图
    	super.onDetachedFromWindow();
    }
    
    public static Comparator zIndexSorter = new Comparator()
    {
		public int compare(Object layer1, Object layer2)
		{
			ViewGroup.LayoutParams p1 = ((View)layer1).getLayoutParams();
			ViewGroup.LayoutParams p2 = ((View)layer2).getLayoutParams();
			
			FlowLayout.LayoutParams lp1 = null;
			FlowLayout.LayoutParams lp2 = null;
			
			if (p1 instanceof FlowLayout.LayoutParams) lp1 = (FlowLayout.LayoutParams)p1;
			else
			{
				lp1 = new FlowLayout.LayoutParams(0, 0);
				lp1.setZIndex(1);
			}
			if (p2 instanceof FlowLayout.LayoutParams) lp2 = (FlowLayout.LayoutParams)p2;
			else
			{
				lp2 = new FlowLayout.LayoutParams(0, 0);
				lp2.setZIndex(1);
			}
			
			return lp1.getZIndex() - lp2.getZIndex();
		}
    };

    public FlowLayout(Context context)
    {
        super(context);
    }
    
    public void setBgColor(int color)
    {
    	this.backgroundColor = color;
    	this.setWillNotDraw(false);
    	this.invalidate();
    }
    
    public void setBackgroundColor(int color)
    {
    	if (this.topRadius + this.bottomRadius == 0)
    		super.setBackgroundColor(color);
    	else
    	{
    		this.corner = null;
    		super.setBackgroundColor(0x00ffffff);
    	}
    }
    
    // 设置背景图相关属性
    // url(res://bg.png) top left width height repeat
    /*
    public void setBackgroundImage(Bitmap bgImg)
    {
    	this.backgroundImage = bgImg;
    	this.setWillNotDraw(false);
    	this.invalidate();
    }
    */
    public void setBackgroundImageUri(String uri)
    {
    	this.backgroundImageUri = uri;
    	this.setWillNotDraw(false);
    	this.invalidate();
    }
    
    // 设置背景图位置
    public void setBackgroundPosition(int left, int top)
    {
    	this.backgroundImageTop = top;
    	this.backgroundImageLeft = left;
    	this.invalidate();
    }
    
    // 设置背景图大小
    public void setBackgroundSize(int width, int height)
    {
    	this.backgroundImageWidth = width;
    	this.backgroundImageHeight = height;
    	this.resized = false;
    	this.invalidate();
    }
    
    // 设置背景图的重复
    public void setBackgroundRepeat(boolean repeatX, boolean repeatY)
    {
    	this.backgroundImageRepeatX = repeatX;
    	this.backgroundImageRepeatY = repeatY;
    	this.invalidate();
    }

    // 边框宽度
    public void setBorderWidth(int top, int right, int bottom, int left)
    {
        this.borderWidthTop = top;
        this.borderWidthRight = right;
        this.borderWidthBottom = bottom;
        this.borderWidthLeft = left;
        this.setWillNotDraw(false);
        this.invalidate();
    }

    // 边框颜色
    public void setBorderColor(int top, int right, int bottom, int left)
    {
        this.borderColorTop = top;
        this.borderColorRight = right;
        this.borderColorBottom = bottom;
        this.borderColorLeft = left;
        this.setWillNotDraw(false);
        this.invalidate();
    }
    
    public int getOffsetTop()
    {
    	return this.offsetTop;
    }
    
    public int getOffsetLeft()
    {
    	return this.offsetLeft;
    }

    public int getBorderWidthTop() { return this.borderWidthTop; };
    public int getBorderWidthBottom() { return this.borderWidthBottom; };
    public int getBorderWidthLeft() { return this.borderWidthLeft; };
    public int getBorderWidthRight() { return this.borderWidthRight; };

    public int getBorderColorTop() { return this.borderColorTop; };
    public int getBorderColorBottom() { return this.borderColorBottom; };
    public int getBorderColorLeft() { return this.borderColorLeft; };
    public int getBorderColorRight() { return this.borderColorRight; };

    // 边框弧角
    public void setRadius(int topRadius, int bottomRadius)
    {
    	if (topRadius != bottomRadius && topRadius > 0 && bottomRadius > 0)
    	{
    		bottomRadius = topRadius;
    	}
    	this.topRadius = topRadius;
    	this.bottomRadius = bottomRadius;
    	this.setBackgroundColor(0x00ffffff);
    	this.setWillNotDraw(false);
    	this.invalidate();
    }
    
    protected void onDraw(Canvas canvas)
    {
    	boolean isNeedsInvalidate = false;
    	
        int width = this.getMeasuredWidth();
        int height = this.getMeasuredHeight();

        // draw borders or background
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        
        // 背景图
        if (this.backgroundColor != 0x00ffffff)
        {
        	paint.setColor(this.backgroundColor);
        	// 画radius以内的矩形区域
        	canvas.drawRect(0, this.topRadius, width, height - this.bottomRadius, paint);
        	
        	// 填充满顶部的圆角间隙
        	canvas.drawRect(this.topRadius, 0, width - this.topRadius, this.topRadius, paint);
        	
        	// 填充满底部的圆角间隙
        	canvas.drawRect(this.bottomRadius, height - this.bottomRadius, width - this.bottomRadius, height, paint);
        }
        
        // TOP边框
        int offsetRadius = this.topRadius + (this.topRadius > 0 ? this.borderWidthRight : 0);
        if (this.borderWidthTop > 0)
        {
            paint.setColor(this.borderColorTop);
            paint.setStrokeWidth(this.borderWidthTop);

            // 画线条时，x,y是这条线的中心线坐标
            canvas.drawLine(offsetRadius, this.borderWidthTop / 2, width - offsetRadius, this.borderWidthTop / 2, paint);
        }

        // BOTTOM边框
        offsetRadius = this.bottomRadius + (this.bottomRadius > 0 ? this.borderWidthRight : 0);
        if (this.borderWidthBottom > 0)
        {
            paint.setColor(this.borderColorBottom);
            paint.setStrokeWidth(this.borderWidthBottom);
            canvas.drawLine(offsetRadius, height - this.borderWidthBottom / 2, width - offsetRadius, height - this.borderWidthBottom / 2, paint);
        }

        // LEFT边框
        offsetRadius = this.bottomRadius + ((this.bottomRadius) > 0 ? this.borderWidthRight : 0);
        if (this.borderWidthLeft > 0)
        {
            paint.setColor(this.borderColorLeft);
            paint.setStrokeWidth(this.borderWidthLeft);
            canvas.drawLine(this.borderWidthLeft / 2, this.topRadius + this.borderWidthTop, this.borderWidthLeft / 2, height - offsetRadius, paint);
        }

        // RIGHT边框
        if (this.borderWidthRight > 0)
        {
            paint.setColor(this.borderColorRight);
            paint.setStrokeWidth(this.borderWidthRight);
            canvas.drawLine(width - this.borderWidthRight / 2, this.topRadius + this.borderWidthTop, width - this.borderWidthRight / 2, height - offsetRadius, paint);
        }
        
        if (this.topRadius + this.bottomRadius > 0)
        {
        	if (null == this.corner)
        	{
        		int radius = this.topRadius;
        		if (radius == 0) radius = this.bottomRadius;
        		try
        		{
        			this.corner = Bitmap.createBitmap((radius + this.borderWidthLeft) * 2, (radius + this.borderWidthLeft) * 2, Bitmap.Config.ARGB_8888);
        		}
        		catch(Throwable e)
        		{
        			isNeedsInvalidate = true;
        			e.printStackTrace();
        		}
        		Paint p = new Paint();
        		p.setAntiAlias(true);
        		Canvas cvs = new Canvas(this.corner);
        		p.setStrokeWidth(1);
        		
        		p.setColor(this.borderColorLeft);
        		cvs.drawCircle(radius + this.borderWidthLeft, radius + this.borderWidthLeft, radius + this.borderWidthLeft, p);
        		
        		p.setColor(this.backgroundColor);
        		cvs.drawCircle(radius + this.borderWidthLeft, radius + this.borderWidthLeft, radius, p);
        	}
        	
        	Rect srcCorner = new Rect();
        	Rect dstCanvas = new Rect();
        	
        	if (this.topRadius > 0)
        	{
	        	// 左上角
	        	srcCorner.top = 0;
	        	srcCorner.left = 0;
	        	srcCorner.right = this.topRadius + this.borderWidthLeft;
	        	srcCorner.bottom = this.topRadius + this.borderWidthLeft;
	        	dstCanvas.top = 0;
	        	dstCanvas.left = 0;
	        	dstCanvas.right = srcCorner.right;
	        	dstCanvas.bottom = srcCorner.bottom;
	        	try
	        	{
	        		canvas.drawBitmap(this.corner, srcCorner, dstCanvas, paint);
	        	}
	        	catch(Throwable e)
	        	{
	        		isNeedsInvalidate = true;
	        		e.printStackTrace();
	        	}
	        	
	        	// 右上角
	        	srcCorner.top = 0;
	        	srcCorner.left = this.topRadius + this.borderWidthLeft;
	        	srcCorner.right = srcCorner.left * 2;
	        	srcCorner.bottom = srcCorner.left;
	        	
	        	dstCanvas.top = 0;
	        	dstCanvas.left = width - this.topRadius - this.borderWidthLeft;
	        	dstCanvas.right = width;
	        	dstCanvas.bottom = this.topRadius + this.borderWidthLeft;
	        	try
	        	{
	        		canvas.drawBitmap(this.corner, srcCorner, dstCanvas, paint);
	        	}
	        	catch(Throwable e)
	        	{
	        		isNeedsInvalidate = true;
	        		e.printStackTrace();
	        	}
        	}
        	
        	if (this.bottomRadius > 0)
        	{
	        	// 左下角
	        	srcCorner.top = this.bottomRadius + this.borderWidthLeft;
	        	srcCorner.left = 0;
	        	srcCorner.right = srcCorner.top;
	        	srcCorner.bottom = srcCorner.top * 2;
	        	
	        	dstCanvas.top = height - this.bottomRadius - this.borderWidthLeft;
	        	dstCanvas.left = 0;
	        	dstCanvas.right = this.bottomRadius + this.borderWidthLeft;
	        	dstCanvas.bottom = height;
	        	try
	        	{
	        		canvas.drawBitmap(this.corner, srcCorner, dstCanvas, paint);
	        	}
	        	catch(Throwable e)
	        	{
	        		isNeedsInvalidate = true;
	        		e.printStackTrace();
	        	}
	        	
	        	// 右下角
	        	srcCorner.top = this.bottomRadius + this.borderWidthLeft;
	        	srcCorner.left = srcCorner.top;
	        	srcCorner.right = srcCorner.top * 2;
	        	srcCorner.bottom = srcCorner.right;
	        	
	        	dstCanvas.top = height - this.bottomRadius - this.borderWidthLeft;
	        	dstCanvas.left = width - this.bottomRadius - this.borderWidthLeft;
	        	dstCanvas.right = width;
	        	dstCanvas.bottom = height;
	        	try
	        	{
	        		canvas.drawBitmap(this.corner, srcCorner, dstCanvas, paint);
	        	}
	        	catch(Throwable e)
	        	{
	        		isNeedsInvalidate = true;
	        		e.printStackTrace();
	        	}
        	}
        }
        
        if (isNeedsInvalidate) this.postInvalidateDelayed(100);
        
        // 画背景图
        // 从哪开始，画到哪结束？背景图的大小是多少？
        // 背景图的大小
        if (this.backgroundImageUri == null) return;
        Bitmap backgroundImage = ImageCache.getInstance().get(this.backgroundImageUri);
        if (null == backgroundImage) return;
        int bw = backgroundImage.getWidth();
        int bh = backgroundImage.getHeight();
        
        if (this.backgroundImageWidth == -1) bw = width;
        else if (this.backgroundImageWidth > 0) bw = this.backgroundImageWidth;
        
        if (this.backgroundImageHeight == -1) bh = height;
        else if (this.backgroundImageHeight > 0) bh = this.backgroundImageHeight;
        
        ImageCache.getInstance().reset(this.backgroundImageUri, bw, bh, 0);
        backgroundImage = ImageCache.getInstance().get(this.backgroundImageUri);
        
        int top = this.backgroundImageTop;
        int left = this.backgroundImageLeft;
        
        if (top == BG_TOP) top = 0;
        if (top == BG_MIDDLE) top = (height - bh) / 2;
        if (top == BG_BOTTOM) top = height - bh;
        if (left == BG_LEFT) left = 0;
        if (left == BG_CENTER) left = (width - bw) / 2;
        if (left == BG_RIGHT) left = width - bw;
        
        top += this.borderWidthTop;
        left += this.borderWidthLeft;
        
        width -= this.borderWidthRight;
        height -= this.borderWidthBottom;
        
        try
        {
	        for (int y = 0, t = 0; ; y++)
	        {
	        	t = top + y * bh;
	        	for (int x = 0, l = 0; ; x++)
	        	{
	        		l = (left + x * bw);
	        		canvas.drawBitmap(backgroundImage, l, t, paint);
	        		if (l + bw > width) break;
	        		if (!this.backgroundImageRepeatX) break;
	        	}
	        	
	        	if (t + bh > height) break;
	        	
	        	// 如果Y方向上不重复，就不再继续画了
	        	if (!this.backgroundImageRepeatY) break;
	        }
        }
        catch(Throwable e)
        {
        	this.postInvalidateDelayed(100);
        	e.printStackTrace();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        // 减去内间距、边框这部分不可用的空间部分
        int width = measuredWidth - this.getPaddingLeft() - this.getPaddingRight() - this.borderWidthLeft - this.borderWidthRight;
        int height = measuredHeight - this.getPaddingTop() - this.getPaddingBottom() - this.borderWidthTop - this.borderWidthBottom;

        // android.util.Log.e("mode", String.valueOf(MeasureSpec.getMode(widthMeasureSpec)));

        // 宽度必须指定，高度才能自适应
        int totalHeight = 0;
        int totalWidth = 0;
        int rowWidth = 0;
        int rowHeight = 0;
        int offsetLeft = this.getPaddingLeft();
        for (int i = 0, l = this.getChildCount(); i < l; i++)
        {
            final View child = this.getChildAt(i);
            // TODO: 这里引发了一个问题，暂时不记得了，隐藏的元素，宽高是不测量的
            if (child.getVisibility() == GONE) continue;
            int wSpec = 0, hSpec = 0;

            ViewGroup.LayoutParams param = child.getLayoutParams();
            FlowLayout.LayoutParams layout = param instanceof FlowLayout.LayoutParams ? (FlowLayout.LayoutParams)param : new FlowLayout.LayoutParams(param.width, param.height);

            boolean isRelativePosition = layout.isRelativePosition();
            
            int w = layout.getWidth();// + layout.getMarginLeft() + layout.getMarginRight();
            int h = layout.getHeight();// + layout.getMarginTop() + layout.getMarginBottom();

            int borderWidth = 0;
            int borderHeight = 0;

            // if (child instanceof ImageView) android.util.Log.e("image-width", String.valueOf(w));

            float wPercent = layout.getWidthPercent();
            float hPercent = layout.getHeightPercent();
            if (wPercent > -1.0f) wSpec = MeasureSpec.makeMeasureSpec(((int)(wPercent * width)) - layout.getMarginLeft() - layout.getMarginRight(), MeasureSpec.EXACTLY);
            else
            {
                if (w > 0) wSpec = MeasureSpec.makeMeasureSpec(w + borderWidth, MeasureSpec.EXACTLY);                                               // 预先设定的宽度
                else if (w == FlowLayout.LayoutParams.FILL_REST) wSpec = MeasureSpec.makeMeasureSpec(width - rowWidth, MeasureSpec.EXACTLY);        // 占据剩余宽度
                else wSpec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.UNSPECIFIED);                                                               // 
            }
            if (hPercent > -1.0f) hSpec = MeasureSpec.makeMeasureSpec(((int)(hPercent * height)) - layout.getMarginTop() - layout.getMarginBottom(), MeasureSpec.EXACTLY);
            else
            {
                if (h > 0) hSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
                else if (h == FlowLayout.LayoutParams.FILL_REST) hSpec = MeasureSpec.makeMeasureSpec(height - totalHeight, MeasureSpec.EXACTLY);
                else hSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.UNSPECIFIED);
            }

            child.measure(wSpec, hSpec);
            if (isRelativePosition) continue;

            // 计算一遍，子元素的实际总高度
            w = child.getMeasuredWidth();
            h = child.getMeasuredHeight();

            // 实际占据大小需要加上外边距
            w += layout.getMarginLeft() + layout.getMarginRight();
            h += layout.getMarginTop() + layout.getMarginBottom();
            
            totalWidth += w;

            if (rowWidth + w <= width)
            {
                rowWidth += w;
                rowHeight = Math.max(rowHeight, h);
            }
            else
            {
                totalHeight += rowHeight;

                rowWidth = w;           // 下一行的rowWidth以最后溢出的这个元素的宽为初始值
                rowHeight = h;
                offsetLeft = this.getPaddingLeft();
            }
        }

        // android.util.Log.e("layout-total-height", String.valueOf(totalHeight));

        totalHeight += rowHeight + this.getPaddingTop() + this.getPaddingBottom();
        totalWidth += this.getPaddingLeft() + this.getPaddingRight();

        // 加上边框的宽高，！不再需要了
        // TODO: 边框是可以给出额外的宽度的
        // widthMeasureSpec = MeasureSpec.makeMeasureSpec(measuredWidth + this.borderWidthLeft + this.borderWidthRight, MeasureSpec.EXACTLY);

        // 如果是WRAP_CONTENT值呢？
        // TODO: 如果是WRAP_CONTENT值，则width或height值将会是-2
        // 这里要以子元素所占据的总宽高来设置，只有当设定了具体的宽度或高度时，WRAP_CONTENT才有意义
        // android.util.Log.e("onMeasure", "check is wrap_content" + height);
        // heightMeasureSpec = 0是为了兼容scrollView的问题，洒家瞎写的
        if (heightMeasureSpec == -2 || heightMeasureSpec == 0 || heightMeasureSpec == 1073741822)
        {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(totalHeight + this.borderWidthTop + this.borderWidthBottom + (this.topRadius + this.bottomRadius), MeasureSpec.EXACTLY);
        }
        
        if (widthMeasureSpec == -2 || widthMeasureSpec == 0 || widthMeasureSpec == 1073741822)
        {
        	widthMeasureSpec = MeasureSpec.makeMeasureSpec(totalWidth + this.borderWidthLeft + this.borderWidthRight, MeasureSpec.EXACTLY);
        }
        
        // android.util.Log.e("Div-Height-" + this.getChildCount(), String.valueOf(MeasureSpec.getSize(heightMeasureSpec)));

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
    	this.offsetLeft = left;
    	this.offsetTop = top;
    	
        int childCount = this.getChildCount();
        int[][] positions = new int[childCount][7];
        int[][] margins = new int[childCount][4];

        ViewGroup.LayoutParams param = this.getLayoutParams();
        
        ArrayList<View> layers = new ArrayList<View>();

        int vAlign = FlowLayout.LayoutParams.ALIGN_TOP;
        int hAlign = FlowLayout.LayoutParams.ALIGN_LEFT;
        int rAlign = Attribute.ALIGN_MIDDLE;
        if (param instanceof FlowLayout.LayoutParams)
        {
            vAlign = ((FlowLayout.LayoutParams)param).getVerticalAlign();
            hAlign = ((FlowLayout.LayoutParams)param).getHorizontalAlign();
            rAlign = ((FlowLayout.LayoutParams)param).getRowAlign();
        }

        int width = this.getMeasuredWidth();
        int height = this.getMeasuredHeight() - (this.topRadius + this.bottomRadius);

        // 艹艹艹，原来不用管内间距的，系统自己会处理好
        int offsetTop = this.topRadius;
        int offsetLeft = 0;

        int rowWidth = 0;
        int rowHeight = 0;
        int row = 0;
        int rowElements = 0;
        for (int i = 0; i < childCount; i++)
        {
            View child = this.getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            ViewGroup.LayoutParams lp = child.getLayoutParams();
            FlowLayout.LayoutParams layout = lp instanceof FlowLayout.LayoutParams ? (FlowLayout.LayoutParams)lp : new FlowLayout.LayoutParams(lp.width, lp.height);

            if (layout.isRelativePosition())
            {
            	layers.add(child);
            	continue;
            }
            
            margins[i][0] = layout.getMarginTop();
            margins[i][1] = layout.getMarginRight();
            margins[i][2] = layout.getMarginBottom();
            margins[i][3] = layout.getMarginLeft();

            int w = child.getMeasuredWidth();
            int h = child.getMeasuredHeight();

            // w += layout.getMarginLeft() + layout.getMarginRight();
            // h += layout.getMarginTop() + layout.getMarginBottom();

            // 实际占据的宽高要加上外边距
            int pw = w + layout.getMarginLeft() + layout.getMarginRight();
            int ph = h + layout.getMarginTop() + layout.getMarginBottom();

            rowElements += 1;
            if (rowWidth + pw <= width)
            {
                rowWidth += pw;
                rowHeight = Math.max(rowHeight, ph);
            }
            else
            {
                row += 1;
                offsetTop += rowHeight;

                rowWidth = pw;           // 下一行的rowWidth以最后溢出的这个元素的宽为初始值
                rowHeight = ph;
                offsetLeft = 0;
            }
            // TOP RIGHT BOTTOM LEFT ROW WIDTH HEIGHT
            positions[i][0] = offsetTop;
            positions[i][1] = offsetLeft + pw;
            positions[i][2] = offsetTop + ph;
            positions[i][3] = offsetLeft;
            positions[i][4] = row;
            positions[i][5] = pw;
            positions[i][6] = ph;
            offsetLeft += pw;
        }

        // 重新计算水平方向上的TOP、LEFT、RIGHT、BOTTOM值
        // 同一行的元素，各自居中对齐
        int totalHeight = 0;
        row += 1;
        for (int i = 0, k = 0; i < row; i++)
        {
            rowWidth = 0;
            rowHeight = 0;

            int s = k;
            while (s < childCount)
            {
                if (positions[s][4] != i) break;
                rowWidth += positions[s][5];
                rowHeight = Math.max(positions[s][6], rowHeight);
                s++;
            }

            totalHeight += rowHeight;

            // 设定该行子元素的TOP值，同行默认居中对齐
            s = k;
            while (k < childCount)
            {
                if (positions[k][4] != i) break;
                
                offsetTop = 0;
                if (rAlign == Attribute.ALIGN_MIDDLE)
                	offsetTop = (rowHeight - positions[k][6]) / 2;
                else if (rAlign == Attribute.ALIGN_BOTTOM)
                	offsetTop = rowHeight - positions[k][6];
                
                positions[k][0] += offsetTop;
                positions[k][2] += offsetTop;
                k++;
            }

            // 设定水平方向上的偏移值offsetLeft
            offsetLeft = this.getPaddingLeft() + this.borderWidthLeft;
            if (hAlign == LayoutParams.ALIGN_CENTER)
            {
                offsetLeft = (width - this.getPaddingLeft() - this.getPaddingRight() - rowWidth) / 2;
                // android.util.Log.e("offsetLeft", offsetLeft);
            }

            if (hAlign == LayoutParams.ALIGN_RIGHT)
            {
                offsetLeft = width - rowWidth;
                offsetLeft -= this.getPaddingRight() + this.borderWidthRight;
            }

            while (s < childCount)
            {
                if (positions[s][4] != i) break;
                positions[s][3] += offsetLeft;
                positions[s][1] += offsetLeft;
                s++;
            }
        }

        // 重新计算垂直方向上的TOP、LEFT、RIGHT、BOTTOM值
        // TODO: 这里需要考虑上内边距，玛勒格彼德
        offsetTop = this.getPaddingTop() + this.borderWidthTop;
        if (vAlign == LayoutParams.ALIGN_MIDDLE) offsetTop = (height - totalHeight) / 2;
        if (vAlign == LayoutParams.ALIGN_BOTTOM)
        {
            offsetTop = height - totalHeight;
            offsetTop -= this.getPaddingBottom() + this.borderWidthBottom;
        }

        for (int i = 0; offsetTop > 0 && i < childCount; i++)
        {
            positions[i][0] += offsetTop;
            positions[i][2] += offsetTop;
        }
        // android.util.Log.e("padding", "Top: " + this.getPaddingTop() + ", Right: " + this.getPaddingRight() + ", Bottom: " + this.getPaddingBottom() + ", Left: " + this.getPaddingLeft());
        // 为每个子元素设定位置
        for (int i = 0; i < positions.length; i++)
        {
            View child = this.getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            ViewGroup.LayoutParams lp = child.getLayoutParams();
            if (lp instanceof FlowLayout.LayoutParams && ((FlowLayout.LayoutParams)lp).isRelativePosition()) continue;
            int l = positions[i][3] + margins[i][3];
            int t = positions[i][0] + margins[i][0];
            int r = positions[i][1] - margins[i][1];
            int b = positions[i][2] - margins[i][2];
            // child.layout(positions[i][3] + margins[i][3], positions[i][0] - margins[i][0], positions[i][1] - margins[i][1], positions[i][2] + margins[i][2]);
            child.layout(l, t, r, b);

            // android.util.Log.e(child.getClass().getName() + ".layout", "Top: " + positions[i][0] + ", Left: " + positions[i][3] + ", Bottom: " + positions[i][2] + ", Right: " + positions[i][1]);
            // android.util.Log.e(child.getClass().getName() + ".margin", margins[i][0] + "," + margins[i][2]);
            // android.util.Log.e(child.getClass().getName() + ".layout", "Top: " + t + ", Left: " + l + ", Width: " + (r - l) + ", Height: " + (b - t) + ", W: " + positions[i][5] + ", H: " + positions[i][6]);
        }
        
        // 根据zIndex排序
        // Collections.sort(layers, FlowLayout.zIndexSorter);

        // android的view的层次是按照子元素的先后顺序来的
        // for (int i = 0; i < layers.size(); i++)
        // 	this.removeView(layers.get(i));
        
        // for (int i = 0; i < layers.size(); i++)
        // 	this.addView(layers.get(i));
        
        // 继续布局定位
        for (int i = 0; i < layers.size(); i++)
        {
        	View child = layers.get(i);
        	ViewGroup.LayoutParams lp = child.getLayoutParams();
        	FlowLayout.LayoutParams layout = lp instanceof FlowLayout.LayoutParams ? (FlowLayout.LayoutParams)lp : new FlowLayout.LayoutParams(lp.width, lp.height);
        	
        	int w = child.getMeasuredWidth();
        	int h = child.getMeasuredHeight();
        	
        	int l = 0, t = 0, r = 0, b = 0;
        	
        	if (layout.getLeft() != LayoutParams.POSITION_UNSPECIFIED)
        	{
        		if (layout.getLeft() == LayoutParams.POSITION_CENTER)
        			l = (width - w) / 2;
        		else
        			l = layout.getLeft();
        	}
        	if (layout.getRight() != LayoutParams.POSITION_UNSPECIFIED)
        	{
        		if (layout.getRight() == LayoutParams.POSITION_CENTER)
        			l = (width - w) / 2;
        		else
        			l = width - w - layout.getRight();
        	}
        	if (layout.getTop() != LayoutParams.POSITION_UNSPECIFIED)
        	{
        		if (layout.getTop() == LayoutParams.POSITION_MIDDLE)
        			t = (height - h) / 2;
        		else
        			t = layout.getTop();
        	}
        	if (layout.getBottom() != LayoutParams.POSITION_UNSPECIFIED)
        	{
        		if (layout.getBottom() == LayoutParams.POSITION_MIDDLE)
        			t = (height - h) / 2;
        		else
        			t = height - h - layout.getBottom();
        	}
        	
        	r = l + w;
        	b = t + h;
        	
        	child.layout(l, t, r, b);
        }
	}

    protected ViewGroup.LayoutParams generateDefaultLayoutParams()
    {
        FlowLayout.LayoutParams layout = new FlowLayout.LayoutParams(0, 0);
        layout.setAlign(FlowLayout.LayoutParams.ALIGN_LEFT, FlowLayout.LayoutParams.ALIGN_TOP);
        return layout;
    }

    protected boolean checkLayoutParams(ViewGroup.LayoutParams params)
    {
        return params instanceof FlowLayout.LayoutParams;
    }

    // 布局属性
    public static class LayoutParams extends ViewGroup.LayoutParams
    {
        public static final int ALIGN_TOP = 0x01;               // 垂直居上
        public static final int ALIGN_MIDDLE = 0x02;            // 垂直居中
        public static final int ALIGN_BOTTOM = 0x03;            // 垂直居下
        public static final int ALIGN_LEFT = 0x04;              // 水平居左
        public static final int ALIGN_CENTER = 0x05;            // 水平居中
        public static final int ALIGN_RIGHT = 0x06;             // 水平居右
        
        public static final int POSITION_UNSPECIFIED = -100000;	// 位置未指定
        public static final int POSITION_CENTER = -100001;		// 位置水平居中
        public static final int POSITION_MIDDLE = -100002;		// 位置垂直居中

        public static final int FILL_PARENT = -1;               // 跟父元素同样大小
        public static final int WRAP_CONTENT = -2;              // 只占用自己所需大小的空间
        public static final int FILL_REST = -3;                 // 占据父元素的剩余空间

        private float widthPercent = -1;
        private float heightPercent = -1;

        private int verticalAlign = 0;
        private int horizontalAlign = 0;

        // TODO:是否使用固定定位？
        private int left = -100000;
        private int top = -100000;
        private int bottom = -100000;
        private int right = -100000;
        private boolean isRelativePosition = false;
        private int zIndex = 1;

        private int marginTop = 0;
        private int marginRight = 0;
        private int marginBottom = 0;
        private int marginLeft = 0;
        
        // 行对齐
        private int rowAlign = Attribute.ALIGN_MIDDLE;

        public String toString()
        {
            return "Width: " + this.width + ", Height: " + this.height + ", WidthPercent: " + this.widthPercent + ", HeightPercent: " + this.heightPercent + ", Margin: " + this.marginTop + ", " + this.marginRight + ", " + this.marginBottom + ", " + this.marginLeft + ", VAlign: " + this.verticalAlign + ", HAlign: " + this.horizontalAlign;
        }

        // TODO:行高lineHeight
        private int lineHeight = 0;

        public LayoutParams(Context context, AttributeSet attributeSet)
        {
            super(context, attributeSet);
        }

        public LayoutParams(int width, int height)
        {
            super(width, height);
            this.width = width;
            this.height = height;
        }

        public LayoutParams(ViewGroup.LayoutParams layout)
        {
            super(layout);
        }
        
        public void setRowAlign(int align)
        {
        	this.rowAlign = align;
        }
        
        public int getRowAlign()
        {
        	return this.rowAlign;
        }

        public void setVerticalAlign(int align)
        {
            this.verticalAlign = align;
        }

        public int getVerticalAlign()
        {
            return this.verticalAlign;
        }

        public void setHorizontalAlign(int align)
        {
            this.horizontalAlign = align;
        }

        public int getHorizontalAlign()
        {
            return this.horizontalAlign;
        }

        public void setAlign(int horizontal, int vertical)
        {
            this.verticalAlign = vertical;
            this.horizontalAlign = horizontal;
        }

        public void setMargin(int top, int right, int bottom, int left)
        {
            this.marginTop = top;
            this.marginRight = right;
            this.marginBottom = bottom;
            this.marginLeft = left;
        }

        public int getMarginTop()
        {
            return this.marginTop;
        }

        public int getMarginRight()
        {
            return this.marginRight;
        }

        public int getMarginBottom()
        {
            return this.marginBottom;
        }

        public int getMarginLeft()
        {
            return this.marginLeft;
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public final boolean isRelativePosition()
        {
        	return this.isRelativePosition;
        }
        
        public void setLeft(int left)
        {
        	this.left = left;
        	this.isRelativePosition = true;
        }
        
        public void setTop(int top)
        {
        	this.top = top;
        	this.isRelativePosition = true;
        }
        
        public void setRight(int right)
        {
        	this.right = right;
        	this.isRelativePosition = true;
        }
        
        public void setBottom(int bottom)
        {
        	this.bottom = bottom;
        	this.isRelativePosition = true;
        }
        
        public void setPosition(int top, int right, int bottom, int left)
        {
        	this.top = top;
        	this.right = right;
        	this.bottom = bottom;
            this.left = left;
            this.isRelativePosition = true;
        }
        
        public void setZIndex(int index)
        {
        	this.zIndex = index;
        }

        public int getLeft()
        {
            return this.left;
        }

        public int getTop()
        {
            return this.top;
        }
        
        public int getBottom()
        {
        	return this.bottom;
        }
        
        public int getRight()
        {
        	return this.right;
        }
        
        public final int getZIndex()
        {
        	return this.zIndex;
        }
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////

        public void setWidth(int width)
        {
            this.width = width;
        }

        public void setWidth(float percent)
        {
            this.widthPercent = percent;
        }

        public void setHeight(int height)
        {
            this.height = height;
        }

        public void setHeight(float percent)
        {
            this.heightPercent = percent;
        }

        public int getWidth()
        {
            return this.width;
        }

        public int getHeight()
        {
            return this.height;
        }

        public float getWidthPercent()
        {
            return this.widthPercent;
        }

        public float getHeightPercent()
        {
            return this.heightPercent;
        }
    }
}
