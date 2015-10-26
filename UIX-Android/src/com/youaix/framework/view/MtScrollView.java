package com.youaix.framework.view;

import android.widget.ScrollView;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ScrollView;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ScrollView;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MtScrollView extends ScrollView
{
    private Context mContext;
    private int mMaxYOverscrollDistance;
    private int maxOverscrollDistance = 0;
    private int overscrollRange = 0;
    private boolean isTouchEvent = false;
    private boolean isOverscroll = false;
    private boolean isTrigged = false;
    
    private OnScrollListener scrollListener = null;
    private OnTopOverScrollListener topOverScrollListener = null;
    private OnBottomOverScrollListener bottomOverScrollListener = null;
      
    public MtScrollView(Context context)
    {
        super(context);
        mContext = context;
        // initBounceListView();
    }
    
    public void setOverscrollDistance(int distance)
    {
    	this.maxOverscrollDistance = distance;
    	this.initBounceListView();
    }
    
    private void initBounceListView()
    {
        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float density = metrics.density;
        this.mMaxYOverscrollDistance = (int) (density * this.maxOverscrollDistance);
        this.overscrollRange = (int)(this.mMaxYOverscrollDistance * 0.8f);
    }
    
    public static interface OnScrollListener
    {
    	public void on(int distance, boolean isOverscroll);
    }
    
    public static interface OnBottomOverScrollListener
    {
    	public void on();
    }
    
    public static interface OnTopOverScrollListener
    {
    	public void on();
    }
    
    public void setOnScrollListener(OnScrollListener scrollListener)
    {
    	this.scrollListener = scrollListener;
    }
    
    public void setOnTopOverScrollListener(OnTopOverScrollListener overscrollListener)
    {
    	this.topOverScrollListener = overscrollListener;
    }
    
    public void setOnBottomOverScrollListener(OnBottomOverScrollListener overscrollListener)
    {
    	this.bottomOverScrollListener = overscrollListener;
    }
    
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
    	super.onScrollChanged(l, t, oldl, oldt);
    	
    	// TODO: 看看有哪些view是需要request或是dispose的
    	// 告知子元素它的view area
    	// if (null == this.contentView) this.contentView = (FlowLayout)this.getChildAt(0);
    	// contentView.setViewArea(t, l + this.getMeasuredWidth(), t + this.getMeasuredHeight(), l);
    	
    	if (this.scrollListener != null && this.isTouchEvent)
    	{
    		int distance = t - oldt;
    		this.scrollListener.on(distance, this.isOverscroll);
    	}
    }
    
    // 如果向下滚动，并且在发生自动回滚且开始回滚时是在overscrollRange的80%高度时
    
    // 如果自动回滚，且开始回滚时高度是80%的高度时，触发onverscroll事件
    
    // 如果向上滚动，并且在发生自动回滚且开始回滚时是在overscrollRange的80%高度时
    private int maxScrollTop = 0;
    private FlowLayout contentView = null;
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY)
    {
    	if (null == this.contentView) this.contentView = (FlowLayout)this.getChildAt(0);
    	// 主要看自动滚回去的距离是不是大于maxScrollRange
    	// 如果是自动回滚，并且己经是overscroll了
    	if (!this.isTouchEvent && (!this.isTrigged))
    	{
    		// 如果当前的scrollY是
    		this.isTrigged = true;
    		boolean isOver = true;
    		
    		if (maxScrollTop < 0) isOver = (this.maxScrollTop + this.overscrollRange) <= 0;
    		else isOver = this.contentView.getMeasuredHeight() + this.overscrollRange <= this.maxScrollTop + this.getMeasuredHeight();
    		
    		if (isOver && scrollY < 0 && this.topOverScrollListener != null)
    		{
    			this.topOverScrollListener.on();
    		}
    		if (isOver && scrollY > 0 && this.bottomOverScrollListener != null)
    		{
    			this.bottomOverScrollListener.on();
    		}
    	}
    	super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }
    
    private int scrollDistance = 0;
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent)
    {
    	this.isTouchEvent = isTouchEvent;
    	if (this.isTouchEvent)
    	{
    		this.isTrigged = false;
    		this.maxScrollTop = scrollY;
    		this.scrollDistance = 0;
    	}
    	else
    	{
    		this.scrollDistance += deltaY;
    	}
        // 这块是关键性代码
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
        
        // 自己放的
        // 如果己经发生了80%的overscroll并且后面的overscroll事件都是非触摸事件
        
        // 自己移回去的
        // 如果己经发生了overscroll并且后面的overscroll事件都是触摸事件
    }
}
