package com.youaix.framework.page;

import java.util.ArrayList;

import org.json.JSONArray;

import android.R.drawable;
import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.*;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.youaix.framework.ui.Div;
import com.youaix.framework.ui.Element;
import com.youaix.framework.ui.List;
import com.youaix.framework.view.FlowLayout;

public abstract class ListPage extends Page
{
	private List list = null;
	protected ArrayList dataArray;
	protected int pageIndex;
	private boolean isLoading;
	private BaseAdapter dataAdapter;
	
	private final int ACT_REFRESH = 0;
	private final int ACT_OTHER = 1;
	private int action = 1;
	
	@SuppressLint("NewApi")
	@Override
	protected void onLoad() throws Exception
	{
		// 初始化
		this.isLoading = false;
		this.dataArray = new ArrayList();
		
		// 绑定数据源
		
		PullToRefreshListView lv = (PullToRefreshListView)this.list.getContentView();
		// TextView tv = new TextView(this);
		// tv.setText("Hello Android");
		// lv.setOverScrollMode(ListView.OVER_SCROLL_ALWAYS);
		// lv.setOverscrollHeader(new BitmapDrawable(Schema.parse("res://bobaoo.png").getBitmap()));
		// lv.setFocusable(false);
		
		lv.setPullLabel("下拉刷新...");
		lv.setRefreshingLabel("正在载入...");
		lv.setReleaseLabel("放开刷新...");
		
		if (!this.enableRefresh()) lv.setMode(Mode.DISABLED);
		
		ListView oView = lv.getRefreshableView();
		if (!this.showDivider()) oView.setDividerHeight(0);
		oView.setCacheColorHint(0x00000000);
		// oView.setSelector(drawable.list_selector_background);
		oView.setSelector(new BitmapDrawable());
		
		lv.setHeaderTextColor(0xff000000);
		
		lv.setBackgroundDrawable(null);
		lv.setRefreshing();
		
		lv.setAdapter(this.dataAdapter = new BaseAdapter()
		{
			public int getCount()
			{
				return dataArray.size();
			}

			public Object getItem(int index)
			{
				return null;
			}

			public long getItemId(int index)
			{
				return index;
			}

			public View getView(int index, View oldView, ViewGroup parent)
			{
				try
				{
					int height = Resolution.pixels(rowHeight(index));
					
					FlowLayout row = (FlowLayout)new Div().setAttribute("row-index", String.valueOf(index)).append(copy(index, dataArray.get(index))).getWrapperView();
					row.setLayoutParams(new AbsListView.LayoutParams(parent.getMeasuredWidth(), height));
					
					return row;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				return null;
			}
		});
		lv.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				if (totalItemCount > 0 && firstVisibleItem + visibleItemCount == totalItemCount && !isLoading)
				{
					markLoadingStarted();
					loadMore();
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1)
			{
				// ...
			}
		});
		lv.setOnRefreshListener(new OnRefreshListener<ListView>()
				{
					@Override
					public void onRefresh(PullToRefreshBase<ListView> refreshView)
					{
						initData();
					}
				});
		
		this.markLoadingStarted();
		this.initData();
	}
	
	private final void initData()
	{
		this.action = ACT_REFRESH;
		this.reloadData();
	}
	
	protected boolean showDivider()
	{
		return false;
	}
	
	protected boolean enableRefresh()
	{
		return true;
	}
	
	protected abstract int rowHeight(int index);
	
	// - (void)reloadData;
	public abstract void reloadData();
	
	// - (void)loadMore;
	public abstract void loadMore();
	
	// 根据JSON内容重建列表元素
	public abstract Element copy(int index, Object item) throws Exception;
	
	public final Element createList()
	{
		if (this.list != null) return this.list;
		return this.list = (List)new List().setWidth(1.0f).setHeight(1.0f);
	}
	
	protected final void markLoadingStarted()
	{
		this.action = ACT_OTHER;
		this.isLoading = true;
	}
	
	protected final void markLoadingFinished()
	{
		this.isLoading = false;
		this.dataAdapter.notifyDataSetChanged();
		if (this.action == ACT_REFRESH)
			((PullToRefreshListView)this.list.getContentView()).onRefreshComplete();
	}
}
