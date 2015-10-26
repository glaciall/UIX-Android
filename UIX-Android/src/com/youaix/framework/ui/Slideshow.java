package com.youaix.framework.ui;

import java.util.ArrayList;

import com.youaix.framework.common.ImageCache;
import com.youaix.framework.event.LoadEvent;
import com.youaix.framework.page.Page;
import com.youaix.framework.page.Resolution;
import com.youaix.framework.view.CircleGifView;
import com.youaix.framework.view.FlowLayout;
import com.youaix.framework.view.GalleryLayout;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class Slideshow extends Element
{
	private GalleryLayout gallery = null;
	private FlowLayout linear = null;
	private LoadEvent loadEvent = null;
	private int childIndex = 0;
	private ArrayList<Drawable> pictures = new ArrayList<Drawable>(10);
	private ArrayList<String> uris = new ArrayList<String>(10);
	private boolean loaded = false;
	private int childCount = 0;
	private boolean fitHeight = false;
	
	public Slideshow()
	{
		super();
		linear = new FlowLayout(getContext());
		FlowLayout.LayoutParams gifLayout = new FlowLayout.LayoutParams(80, 80);
		gifLayout.setTop(FlowLayout.LayoutParams.POSITION_MIDDLE);
		gifLayout.setLeft(FlowLayout.LayoutParams.POSITION_CENTER);
		linear.addView(new CircleGifView(getContext()), gifLayout);
		gallery = new GalleryLayout(getContext(), linear);
	}
	
	public void setImage(int index, Bitmap bitmap)
	{
		this.pictures.set(index, new BitmapDrawable(bitmap));
		this.childCount += 1;
		this.loaded = this.childCount == this.pictures.size();
		if (this.loaded)
		{
			this.linear.removeAllViews();
			this.gallery.renew(this.pictures, this.uris);
		}
	}
	
	@Override
	public Slideshow append(Element element)
	{
		if (null == element) return null;
		if (!(element instanceof Image)) return null;
		Bitmap bitmap = ((Image)element).getBitmap();
		if (null == bitmap)
		{
			bitmap = Bitmap.createBitmap(new int[] { 0xffcccccc }, 1, 1, Bitmap.Config.RGB_565);
			
			final Slideshow self = this;
			final int pictureIndex = childIndex;
			element.onLoad(new LoadEvent()
			{
				public void on(Page page, Element element)
				{
					Image image = (Image)element;
					Bitmap bitmap = ImageCache.getInstance().get(image.getSrc());
					self.setImage(pictureIndex, bitmap);
					self.fitHeight(bitmap.getWidth(), bitmap.getHeight());
				}
			});
		}
		else this.childCount += 1;
		
		this.pictures.add(new BitmapDrawable(bitmap));
		this.uris.add(element.getAttribute("href"));
		this.childIndex += 1;
		return this;
	}
	
	public void fitHeight(int width, int height)
	{
		if (!this.fitHeight) return;
		int w = this.linear.getMeasuredWidth();
		int h = this.linear.getMeasuredHeight();
		
		if (w == 0 || h == 0) return;
		
		h = (int)(((float)w / (float)width) * (float)height);
		this.setHeight(Resolution.dip(h));
	}
	
	public Slideshow setFitHeight(boolean fit)
	{
		this.fitHeight = fit;
		return this;
	}

	public View getContentView()
	{
		if (this.loaded)
		{
			this.gallery.renew(pictures, uris);
			this.loaded = false;
		}
		linear.setLayoutParams(this.getLayout());
		return this.linear;
	}
}
