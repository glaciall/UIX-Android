package com.youaix.framework.view;

import com.youaix.framework.common.ImageCache;
import com.youaix.framework.common.Schema;
import com.youaix.framework.ui.Attribute;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.graphics.*;
import android.graphics.Bitmap.Config;

public class ImageView extends android.widget.ImageView
{
	Paint paint = null;
	int width = 0;
	int height = 0;
	int radius = 0;
	boolean isDirty = true;
	boolean isMeasured = false;
	boolean isInSight = true;
	boolean fitToRect = false;
	boolean isZoom = true;
	int offsetTop = Attribute.POSITION_UNSPECIFIED;
	int offsetLeft = Attribute.POSITION_UNSPECIFIED;
	
	static Bitmap placeHolder = null;
	
	static
	{
		try
		{
			placeHolder = Bitmap.createScaledBitmap(Schema.parse("res://placeholder.png").getBitmap(), Resolution.pixels(40), Resolution.pixels(40), true);
		}
		catch(Exception ex)
		{
			// ...
		}
	}
	
	public ImageView(Context context)
	{
		super(context);
		paint = new Paint();
		paint.setColor(0xff006699);
	}
	
	public void setOffsetPosition(int top, int left)
	{
		this.offsetTop = top;
		this.offsetLeft = left;
	}
	
	public int getOffsetLeft()
	{
		return this.offsetLeft;
	}
	
	public int getOffsetTop()
	{
		return this.offsetTop;
	}
	
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		this.offsetLeft = left;
		this.offsetTop = top;
	}
	
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		
		// TODO: 应该依据图片的原始大小和实际显示大小，进行重新取样
		if (this.isMeasured)
		{
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}
		
		int wMode = MeasureSpec.getMode(widthMeasureSpec);
		int hMode = MeasureSpec.getMode(heightMeasureSpec);
		
		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);
		
		if (!this.loaded)
		{
			if (wMode != MeasureSpec.EXACTLY && hMode == MeasureSpec.EXACTLY)
			{
				super.onMeasure(MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
				return;
			}
			if (wMode == MeasureSpec.EXACTLY && hMode != MeasureSpec.EXACTLY)
			{
				super.onMeasure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY));
				return;
			}
			
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}
		
		// android.util.Log.e("image-view", "MeasureSpec: " + (wMode == MeasureSpec.EXACTLY) + ", " + (hMode == MeasureSpec.EXACTLY));
		
		// 如果宽高都没有设定，按原始尺寸来显示
		if (wMode != MeasureSpec.EXACTLY && hMode != MeasureSpec.EXACTLY)
		{
			w = width;
			h = height;
		}
		
		// 如果只设定了宽，则高度自适应
		if (wMode == MeasureSpec.EXACTLY && hMode != MeasureSpec.EXACTLY)
		{
			h = (int)((float)height * ((float)w / (float)width));
		}
		 
		// 如果只设定了高，则高度自适应
		if (wMode != MeasureSpec.EXACTLY && hMode == MeasureSpec.EXACTLY)
		{
			
			w = (int)((float)width * ((float)h / (float)height));
		}
		
		if ((w != width || h != height || isDirty) && this.loaded && this.isZoom)
		{
			// 如果宽高不一致，应该进行缩放
			// 一切以w、h为准
			int ww = 0;
			int hh = 0;
			if (width >= height)
			{
				ww = w;
				hh = (int)((float)ww / (float)width * (float)height);
			}
			else
			{
				hh = h;
				ww = (int)((float)hh / (float)height * (float)width);
			}
			
			// TODO: 通知ImageCache去缩放图片？
			// if (this.bitmap != null) this.bitmap = Bitmap.createScaledBitmap(this.bitmap, ww, hh, true);
			
			if (this.fitToRect)
			{
				android.util.Log.e("image-view-gg", Boolean.toString(this.fitToRect));
				// 计算一下显示成那个样子的时候的图片宽高的要求是多少
				// width 原图宽度
				// height 原图高度
				// w 要显示的宽度
				// h 要显示的高度
				ww = width;
				hh = height;
			}
			
			ImageCache.getInstance().reset(uri, iWidth = ww, iHeight = hh, this.radius);
			
			isDirty = false;
			width = w;
			height = h;
		}
		isMeasured = true;
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	// 真实的需要绘制出来的图片宽高
	private int iWidth = 0;
	private int iHeight = 0;
	
	private boolean loaded = false;
	public void setImageBitmap(Bitmap bm)
	{
		super.setImageBitmap(null);
		if (null == bm) return;
		this.loaded = bm != null;
		if (this.width == 0) this.width = bm.getWidth();
		if (this.height == 0) this.height = bm.getHeight();
		this.isDirty = true;
		this.isMeasured = false;
		
		// 自己不保存这个bitmap参数的引用，通知缓存那里去保存，需要的时候，通过getBitmap去获取
		this.invalidate();
	}
	
	private String uri = null;
	public void setImageUri(String uri)
	{
		this.uri = uri;
		ImageCache.getInstance().put(this, uri);
	}
	
	public Bitmap getBitmap()
	{
		return ImageCache.getInstance().get(this.uri, this.iWidth, this.iHeight, this.radius);
	}
	
	public void setFitRect(boolean fit)
	{
		this.fitToRect = fit;
	}
	
	public void setZoom(boolean zoom)
	{
		this.isZoom = zoom;
	}
	
	protected void onDraw(Canvas canvas)
	{
		try
		{
			Bitmap bm = this.getBitmap();
			int width = this.getMeasuredWidth();
			int height = this.getMeasuredHeight();
			if (bm != null && !bm.isRecycled())
			{
				// 
				if (this.fitToRect)
				{
					
					canvas.drawBitmap(bm, 0, 0, paint);
				}
				else
					canvas.drawBitmap(bm, (width - bm.getWidth()) / 2, (height - bm.getHeight()) / 2, paint);
				return;
			}
			
			// Animation anim = AnimationUtils.loadAnimation(this.getContext(), android.R.anim.fade_in);
			// this.startAnimation(anim);
			
			paint.setColor(0xfff0f0f0);
			canvas.drawRect(0, 0, width, height, paint);
			if (width < placeHolder.getWidth() || height < placeHolder.getHeight()) return;
			
			canvas.drawBitmap(placeHolder, (width - placeHolder.getWidth()) / 2, (height - placeHolder.getHeight()) / 2, paint);
		}
		catch(Throwable e)
		{
			this.postInvalidateDelayed(100);
			e.printStackTrace();
		}
	}
	
	public final void setIsInSight(boolean isInSight)
	{
		this.isInSight = isInSight;
	}
	
	public final boolean isInSight()
	{
		return this.isInSight;
	}
	
	private boolean isRemoved = false;
	public boolean isRemoved()
	{
		return this.isRemoved;
	}
	
	protected void onDetachedFromWindow()
	{
		// TODO: 通知ImageCache释放俺的图片占用
		// TODO: 这个一般是被删除后会触发的吧
		this.isInSight = false;
		this.isRemoved = true;
		ImageCache.getInstance().dispose(this, uri);
		super.onDetachedFromWindow();
	}

	public void setRadius(int radius)
	{
		this.radius = radius;
	}
}
