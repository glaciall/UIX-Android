package com.youaix.framework.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.youaix.framework.common.Schema;
import com.youaix.framework.page.PageManager;
import com.youaix.framework.page.Resolution;
import com.youaix.framework.ui.Attribute;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ImageView.ScaleType;
import android.widget.RadioGroup;

public class GalleryLayout extends View
{
	private Gallery myGallery;
	private RadioGroup gallery_points;
	private RadioButton[] gallery_point;
	private Context context;
	private FlowLayout linearLayout;
	private ArrayList<Drawable> list = new ArrayList<Drawable>();
	private ArrayList<String> uris = new ArrayList<String>();
	
	private boolean started = false;
	
	public GalleryLayout(Context context)
	{
		super(context);
		this.context = context;
	}
	
	public void renew(ArrayList<Drawable> pictures, ArrayList<String> uris)
	{
		this.list = pictures;
		this.uris = uris;
		this.start();
	}
	
	public void start()
	{
		this.init();
		this.bindEvent();
		if (this.started) return;
		this.autoPlay();
		this.started = true;
	}
	
	public GalleryLayout(Context context, FlowLayout linearLayout)
	{
		super(context);
		this.linearLayout = linearLayout;
		this.context = context;
		myGallery = new DetailGallery(context);
		gallery_points = new RadioGroup(context);
	}

	// 初始化
	private final void init()
	{
		gallery_points.removeAllViews();
		gallery_points.setOrientation(RadioGroup.HORIZONTAL);
		GalleryIndexAdapter adapter = new GalleryIndexAdapter(list, context);
		myGallery.setAdapter(adapter);
		gallery_point = new RadioButton[list.size()];
		int margin = Resolution.pixels(3);
		for (int i = 0; i < gallery_point.length; i++)
		{
			gallery_point[i] = new Indicator(context);
			gallery_point[i].setId(i);
			int wh = 10;
			RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(wh, wh);
			gallery_point[i].setLayoutParams(layoutParams);
			layoutParams.setMargins(margin, 0, margin, 0);
			gallery_point[i].setClickable(false);
			gallery_points.addView(gallery_point[i]);
		}
		
		if (this.linearLayout.getChildCount() > 0) return;
		FlowLayout.LayoutParams layout = new FlowLayout.LayoutParams(-1, -1);
		layout.setWidth(1.0f);
		layout.setHeight(1.0f);
		layout.setTop(0);
		layout.setLeft(0);
		this.linearLayout.addView(myGallery, layout);
		
		FlowLayout.LayoutParams pointsLayout = new FlowLayout.LayoutParams(-2, -2);
		pointsLayout.setBottom(10);
		pointsLayout.setLeft(Attribute.POSITION_CENTER);
		this.linearLayout.addView(this.gallery_points, pointsLayout);
	}

	// 添加事件
	private final void bindEvent()
	{
		final GalleryLayout self = this;
		myGallery.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3)
			{
				String uri = self.uris.get(index % gallery_point.length);
				if (uri != null)
					PageManager.getInstance().redirect(Schema.parse(uri));
			}
		});
		myGallery.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				gallery_points.check(gallery_point[arg2 % gallery_point.length].getId());
			}
			
			public void onNothingSelected(AdapterView<?> arg0)
			{
				// ...
			}
		});
	}

	/** 展示图控制器，实现展示图切换 */
	final Handler handler_gallery = new Handler()
	{
		public void handleMessage(Message msg)
		{
			MotionEvent e1 = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 89.333336f, 265.33334f, 0);
			MotionEvent e2 = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 300.0f, 238.00003f, 0);
			myGallery.onFling(e2, e1, -800, 0);
			super.handleMessage(msg);
		}
	};

	private void autoPlay()
	{
		Timer time = new Timer();
		TimerTask task = new TimerTask()
		{
			public void run()
			{
				Message m = new Message();
				handler_gallery.sendMessage(m);
			}
		};
		time.schedule(task, 5000, 5000);
	}

	public class GalleryIndexAdapter extends BaseAdapter
	{
		List<Drawable> imagList;
		Context context;

		public GalleryIndexAdapter(List<Drawable> list, Context cx)
		{
			imagList = list;
			context = cx;
		}

		public int getCount()
		{
			return Integer.MAX_VALUE;
		}

		public Object getItem(int position)
		{
			return null;
		}

		public long getItemId(int position)
		{
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup container)
		{
			ImageView imageView = new ImageView(context)
			{
				@Override
				protected void onDraw(Canvas canvas)
				{
					try
					{
						super.onDraw(canvas);
					}
					catch(Exception ex)
					{
						this.postInvalidateDelayed(100);
					}
				}
			};
			Drawable drawable = imagList.get(position % imagList.size());
			imageView.setImageDrawable(drawable);
			imageView.setLayoutParams(new Gallery.LayoutParams(-1, -1));
			return imageView;
		}
	}
}