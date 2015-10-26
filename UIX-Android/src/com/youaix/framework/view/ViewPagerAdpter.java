package com.youaix.framework.view;

import java.util.List;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;

public class ViewPagerAdpter extends PagerAdapter
{
	private List<View> views;
	private Activity activity;
	
	public ViewPagerAdpter(List<View> views, Activity activity)
	{
		this.views = views;
		this.activity = activity;
	}
	
	public void destroyItem(View container, int position, Object object)
	{
		((ViewPager)container).removeView(views.get(position));
	}
	
	public int getCount()
	{
		if (this.views != null) return this.views.size();
		else return 0;
	}
	
	public Object instantiateItem(View container, int position)
	{
		View currentView = views.get(position);
		((ViewPager)container).addView(currentView, 0);
		if (position == views.size() - 1)
		{
			final GuidePage contentPage = (GuidePage)this.activity;
			currentView.setOnClickListener(new OnClickListener()
			{
				public void onClick(View arg0)
				{
					contentPage.clickOnLastPage();
				}
			});
		}
		
		return currentView;
	}

	public boolean isViewFromObject(View arg0, Object arg1)
	{
		return arg0 == arg1;
	}
}
