package com.youaix.framework.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;

public class AnchorView extends ViewGroup
{
	public AnchorView(Context context)
	{
		super(context);
	}
	
	public boolean onInterceptTouchEvent(MotionEvent evt)
	{
		// 拦截事件，不向下传递
		return true;
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        // 减去内间距、边框这部分不可用的空间部分
        int width = measuredWidth - this.getPaddingLeft() - this.getPaddingRight();
        int height = measuredHeight - this.getPaddingTop() - this.getPaddingBottom();
        
		for (int i = 0, l = this.getChildCount(); i < l && i < 1; i++)
		{
			final View child = this.getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            int wSpec = 0, hSpec = 0;

            ViewGroup.LayoutParams param = child.getLayoutParams();
            FlowLayout.LayoutParams layout = param instanceof FlowLayout.LayoutParams ? (FlowLayout.LayoutParams)param : new FlowLayout.LayoutParams(param.width, param.height);

            int w = layout.getWidth();
            int h = layout.getHeight();

            int borderWidth = 0;
            int borderHeight = 0;

            float wPercent = layout.getWidthPercent();
            float hPercent = layout.getHeightPercent();
            if (wPercent > -1.0f) wSpec = MeasureSpec.makeMeasureSpec(((int)(wPercent * width)) - layout.getMarginLeft() - layout.getMarginRight(), MeasureSpec.EXACTLY);
            else
            {
                if (w > 0) wSpec = MeasureSpec.makeMeasureSpec(w + borderWidth, MeasureSpec.EXACTLY);
                else if (w == FlowLayout.LayoutParams.FILL_REST) wSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
                else wSpec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.UNSPECIFIED); 
            }
            if (hPercent > -1.0f) hSpec = MeasureSpec.makeMeasureSpec(((int)(hPercent * height)) - layout.getMarginTop() - layout.getMarginBottom(), MeasureSpec.EXACTLY);
            else
            {
                if (h > 0) hSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
                else if (h == FlowLayout.LayoutParams.FILL_REST) hSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
                else hSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.UNSPECIFIED);
            }

            child.measure(wSpec, hSpec);

            // 计算一遍，子元素的实际总高度
            w = child.getMeasuredWidth();
            h = child.getMeasuredHeight();

            // 实际占据大小需要加上外边距
            w += layout.getMarginLeft() + layout.getMarginRight();
            h += layout.getMarginTop() + layout.getMarginBottom();
            
            width = w;
            height = h;
		}
		
		super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
	}
	
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		for (int i = 0, l = this.getChildCount(); i < l && i < 1; i++)
		{
			View child = this.getChildAt(i);
			child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
		}
	}
}
