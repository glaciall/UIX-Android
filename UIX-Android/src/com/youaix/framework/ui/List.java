package com.youaix.framework.ui;

import android.view.View;

public class List extends Element
{
	PullToRefreshListView view = null;
	
	public List()
	{
		this.view = new PullToRefreshListView(getContext());
	}
	
	public View getContentView()
	{
		return this.view;
	}
}
