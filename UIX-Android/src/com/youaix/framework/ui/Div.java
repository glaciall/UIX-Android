package com.youaix.framework.ui;

import com.youaix.framework.common.Schema;
import com.youaix.framework.event.ClickEvent;
import com.youaix.framework.event.DragEvent;
import com.youaix.framework.event.HoverEvent;
import com.youaix.framework.event.MoveEvent;
import com.youaix.framework.event.OverScrollEvent;
import com.youaix.framework.event.PressEvent;
import com.youaix.framework.event.ScrollEvent;
import com.youaix.framework.event.TouchEvent;
import com.youaix.framework.page.PageManager;
import com.youaix.framework.page.Resolution;
import com.youaix.framework.view.FlowLayout;
import com.youaix.framework.view.MtScrollView;
import com.youaix.framework.view.MtScrollView.OnScrollListener;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.*;

public final class Div extends Element
{
	private FlowLayout view = null;
	private MtScrollView wrapperVView = null;
	private HorizontalScrollView wrapperHView = null;
	
	private int overScrollDistance = 0;
	
	private int backgroundColor = 0x00ffffff;
	private int backgroundColorHover = 0x00ffffff;
	
	private int[] borderColor = new int[] { 0x00, 0x00, 0x00, 0x00 };
	private int[] borderColorHover = new int[] { 0x00, 0x00, 0x00, 0x00 };
	
	private String backgroundImage = null;
	private String backgroundImageHover = null;
	
	private boolean isStyleStatable = false;
	
    private boolean isScrollable = false;
    private int direction = Attribute.DIR_VERTICAL;

    public Div()
    {
    	super();
        this.view = new FlowLayout(getContext());
        this.setWidth(1.0f);
    }
    
    public boolean isScrollable()
    {
    	return this.isScrollable;
    }

    public int getBackgroundColor()
    {
    	return this.backgroundColor;
    }
    
    public Div setBackgroundColor(int color)
    {
        this.view.setBackgroundColor(this.backgroundColor = color);
        this.view.setBgColor(color);
        return this;
    }
    
    public Div setHoverBackgroundColor(int color)
    {
    	this.backgroundColorHover = color;
    	this.isStyleStatable = true;
    	return this;
    }
    
    @Override
    public boolean isStyleStatable()
    {
    	return isStyleStatable;
    }
    
    @Override
    public StatableStyle generateStyle()
    {
    	final int bgColor = this.backgroundColor;
    	final int bgColorHover = this.backgroundColorHover;
    	
    	final String bgImg = this.backgroundImage;
    	final String bgImgHover = this.backgroundImageHover;
    	
    	return new StatableStyle()
    	{
			public void onHover(Element element)
			{
				Div self = (Div)element;
				self.setBackgroundColor(bgColorHover);
				if (bgImgHover != null) self.setBackgroundImage(bgImgHover);
			}

			public void onRelease(Element element)
			{
				Div self = (Div)element;
				self.setBackgroundColor(bgColor);
				if (bgImg != null) self.setBackgroundImage(bgImg);
			}
    	};
    }
    
    public Div setBackgroundSize(int width, int height)
    {
    	if (width > 0) width = Resolution.pixels(width);
    	if (height > 0) height = Resolution.pixels(height);
    	this.view.setBackgroundSize(width, height);
    	return this;
    }
    
    public Div setBackgroundRepeat(boolean repeatX, boolean repeatY)
    {
    	this.view.setBackgroundRepeat(repeatX, repeatY);
    	return this;
    }
    
    public Div setBackgroundPosition(int left, int top)
    {
    	this.view.setBackgroundPosition(left, top);
    	return this;
    }

    public Div setBackgroundImage(Bitmap bitmap)
    {
        // this.view.setBackgroundImage(bitmap);
        return this;
    }
    
    public Div setRadius(int radius)
    {
    	return this.setRadius(radius, radius);
    }
    
    public Div setRadius(int topRadius, int bottomRadius)
    {
    	this.view.setRadius(Resolution.pixels(topRadius), Resolution.pixels(bottomRadius));
    	return this;
    }
    
    public Div setBackgroundImage(String url)
    {
    	Schema.Uri uri = Schema.parse(url);
    	if (uri.getType() == Schema.URI_RES)
    	{
			this.view.setBackgroundImageUri(this.backgroundImage = url);
    	}
    	return this;
    }
    
    public Div setHoverBackgroundImage(String url)
    {
    	Schema.Uri uri = Schema.parse(url);
    	if (uri.getType() == Schema.URI_RES)
    	{
    		this.isStyleStatable = true;
    		this.backgroundImageHover = url;
    	}
    	return this;
    }

    public Div setBorderColor(int color)
    {
        return this.setBorderColor(color, color, color, color);
    }

    public Div setBorderColor(int top, int right, int bottom, int left)
    {
        this.view.setBorderColor(top, right, bottom, left);
        return this;
    }

    public Div setBorderWidth(int width)
    {
        return this.setBorderWidth(width, width, width, width);
    }

    public Div setBorderWidth(int top, int right, int bottom, int left)
    {
        this.view.setBorderWidth(Resolution.pixels(top), Resolution.pixels(right), Resolution.pixels(bottom), Resolution.pixels(left));
        return this;
    }
    
    public Div setOverScroll(int distance)
    {
    	this.overScrollDistance = distance;
    	return this;
    }

	public Div setWidth(int width)
    {
        super.setWidth(width);
		return this;
	}

	public Div setHeight(int height)
    {
        super.setHeight(height);
		return this;
	}

    public Div setWidth(float width)
    {
        super.setWidth(width);
        return this;
    }

    public Div setHeight(float height)
    {
        super.setHeight(height);
        return this;
    }

    public Div setScrollable(boolean isScrollable)
    {
        this.isScrollable = isScrollable;
        return this;
    }

    public Div setTag(String tag)
    {
        this.view.setTag(tag);
        return this;
    }

    public Div setPadding(int padding)
    {
        super.setPadding(padding);
        return this;
    }
    
    public Div setPadding(int topBottom, int leftRight)
    {
    	super.setPadding(topBottom, leftRight);
    	return this;
    }

    public Div setPadding(int top, int right, int bottom, int left)
    {
        super.setPadding(top, right, bottom, left);
        return this;
    }

    public Div setMargin(int margin)
    {
        super.setMargin(margin);
        return this;
    }
    
    public Div setMargin(int topBottom, int leftRight)
    {
    	return (Div)super.setMargin(topBottom, leftRight);
    }

    public Div setMargin(int top, int right, int bottom, int left)
    {
        super.setMargin(top, right, bottom, left);
        return this;
    }
    
    public Div setScrollDirection(int direction)
    {
    	this.direction = direction;
    	this.isScrollable = true;
    	return this;
    }
    
    public void scrollTo(int top)
    {
    	this.wrapperVView.smoothScrollTo(0, Resolution.pixels(top));
    }
    
    public void scrollToTop()
    {
    	this.wrapperVView.smoothScrollTo(0, 0);
    }
    
    public void scrollToBottom()
    {
    	this.wrapperVView.smoothScrollTo(0, this.view.getMeasuredHeight() - this.wrapperVView.getMeasuredHeight());
    }
    
    public int getScrollDirection()
    {
    	if (!this.isScrollable) return -1;
    	return this.direction;
    }
    
	public View getWrapperView()
    {
    	if (!this.isScrollable) return this.getContentView();
    	
    	if (this.direction == Attribute.DIR_VERTICAL)
    	{
    		if (null == this.wrapperVView)
    		{
    			this.wrapperVView = new MtScrollView(getContext());
    			this.wrapperVView.setOverscrollDistance(this.overScrollDistance);
    			this.wrapperVView.addView(this.view, new android.view.ViewGroup.LayoutParams(this.getWidth(), -2));
    		}
    		return this.wrapperVView;
    	}
    	if (this.direction == Attribute.DIR_HORIZONTAL)
    	{
    		if (null == this.wrapperHView)
    		{
    			this.wrapperHView = new HorizontalScrollView(getContext());
    			this.wrapperHView.addView(this.view, new android.view.ViewGroup.LayoutParams(-2, -1));
    		}
    		return this.wrapperHView;
    	}
    	
    	return null;
    }

	private boolean isSetted = false;
	public View getContentView()
	{
        FlowLayout.LayoutParams layout = (FlowLayout.LayoutParams)super.getLayout();
        if (!isSetted && layout != null)
        {
        	this.view.setLayoutParams(layout);
        	this.isSetted = true;
        }
        return this.view;
	}

    public Div onClick(final ClickEvent event)
    {
        super.onClick(event);
        return this;
    }

    public Div onPress(final PressEvent event)
    {
        super.onPress(event);
        return this;
    }

    public Div onDrag(final DragEvent event)
    {
        super.onDrag(event);
        return this;
    }

    public Div onHover(final HoverEvent event)
    {
        super.onHover(event);
        return this;
    }

    public Div onMove(final MoveEvent event)
    {
        super.onMove(event);
        return this;
    }

    public Div onTouch(final TouchEvent event)
    {
        super.onTouch(event);
        return this;
    }
    
    public Div onTopOverScroll(final OverScrollEvent event)
    {
    	final Element self = this;
    	this.wrapperVView.setOnTopOverScrollListener(new MtScrollView.OnTopOverScrollListener()
    	{
			public void on()
			{
				try
				{
					event.on(PageManager.getInstance().getCurrent(), self);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		});
    	return this;
    }
    
    public Div onBottomOverScroll(final OverScrollEvent event)
    {
    	final Element self = this;
    	this.wrapperVView.setOnBottomOverScrollListener(new MtScrollView.OnBottomOverScrollListener()
    	{
			public void on()
			{
				try
				{
					event.on(PageManager.getInstance().getCurrent(), self);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		});
    	return this;
    }
    
    public Div onScroll(final ScrollEvent event)
    {
    	if (this.direction != Attribute.DIR_VERTICAL) return this;
    	this.wrapperVView.setOnScrollListener(new OnScrollListener()
    	{
			public void on(int distance, boolean isOverscroll)
			{
				try
				{
					event.on(Resolution.dip(distance));
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
    	});
    	return this;
    }
    
    public Div append(Element el)
    {
    	return (Div) super.append(el);
    }
}
