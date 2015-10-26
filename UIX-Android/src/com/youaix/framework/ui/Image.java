package com.youaix.framework.ui;

import com.youaix.framework.common.Schema;
import com.youaix.framework.event.ClickEvent;
import com.youaix.framework.event.DragEvent;
import com.youaix.framework.event.HoverEvent;
import com.youaix.framework.event.LoadEvent;
import com.youaix.framework.event.MoveEvent;
import com.youaix.framework.event.PressEvent;
import com.youaix.framework.event.TouchEvent;
import com.youaix.framework.page.PageManager;
import com.youaix.framework.page.Resolution;
import com.youaix.framework.view.ImageView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.ViewGroup.LayoutParams;

public final class Image extends Element
{
	private int radius = 0;
	private String src = null;
	private String srcHover = null;
	private ImageView view = null;
	private Bitmap bitmap = null;
	private LoadEvent loadEvent = null;
	private boolean isStyleStatable = false;
	
	public Image()
	{
		super();
		this.view = new ImageView(getContext());
	}
	
	public Image setSrcLazyLoad(String uri)
	{
		this.src = uri;
		this.view.setImageUri(uri);
		PageManager.getInstance().getCurrent().loadImage(this, this.src);
		return this;
	}

	public Image setSrc(String uri)
	{
		try
		{
			this.src = uri;
			if (!uri.startsWith("http://"))
			{
				this.view.setImageUri(uri);
				this.setBitmap(Schema.parse(uri).getBitmap());
			}
			else
			{
				this.view.setImageUri(uri);
				PageManager.getInstance().getCurrent().loadImage(this, this.src);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return this;
	}
	
	public Image setHoverSrc(String uri)
	{
		try
		{
			this.srcHover = uri;
			this.isStyleStatable = true;
			// 如果是网络图片，加入到加载队列里去吧，不过目标Image就故意给带歪了去
			if (uri.startsWith("http://"))
				PageManager.getInstance().getCurrent().loadImage(new Image(), this.srcHover);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return this;
	}
	
	@Override
	public boolean isStyleStatable()
	{
		return this.isStyleStatable;
	}
	
	@Override
	public StatableStyle generateStyle()
	{
		final String imgSrc = this.src;
		final String imgSrcHover = this.srcHover;
		return new StatableStyle()
		{
			@Override
			public void onHover(Element element)
			{
				Image self = (Image)element;
				if (imgSrcHover != null) self.setSrc(imgSrcHover);
			}

			@Override
			public void onRelease(Element element)
			{
				Image self = (Image)element;
				if (imgSrc != null) self.setSrc(imgSrc);
			}
		};
	}

	public String getSrc()
	{
		return this.src;
	}

	boolean isOK = true;

	public Image setBitmap(Bitmap graph)
	{
		// TODO: 显示网络图片时，应该会有一个淡入的效果
		if (this.src.startsWith("http://")) this.setAnimation("xiaobao_fade_in");
		this.view.setImageBitmap(graph);
		if (this.loadEvent != null) this.loadEvent.on(getContext(), this);
		return this;
	}
	
	public Bitmap getBitmap()
	{
		return this.bitmap;
	}
	
	public Image setZoom(boolean zoom)
	{
		this.view.setZoom(zoom);
		return this;
	}
	
	public Image setRadius(int radius)
	{
		this.radius = Resolution.pixels(radius);
		this.view.setRadius(this.radius);
		return this;
	}

	@Override
	public Image setWidth(int width)
	{
		return (Image)super.setWidth(width);
	}
	
	public Image setFitRect(boolean fit)
	{
		this.view.setFitRect(fit);
		return this;
	}
	
	@Override
	public Image setWidth(float widthPercent)
	{
		return (Image)super.setWidth(widthPercent);
	}
	
	@Override
	public Image setHeight(int height)
	{
		return (Image)super.setHeight(height);
	}
	
	@Override
	public Image setHeight(float heightPercent)
	{
		return (Image)super.setHeight(heightPercent);
	}

	private boolean isSetted = false;
	public ImageView getContentView()
	{
		LayoutParams layout = this.getLayout();
		if (layout != null && !this.isSetted)
		{
			this.view.setLayoutParams(layout);
			this.isSetted = true;
		}
		return this.view;
	}
	
	public Image onLoad(final LoadEvent event)
	{
		this.loadEvent = event;
		return this;
	}

	public Image onClick(final ClickEvent event)
	{
		super.onClick(event);
		return this;
	}

	public Image onPress(final PressEvent event)
	{
		super.onPress(event);
		return this;
	}

	public Image onDrag(final DragEvent event)
	{
		super.onDrag(event);
		return this;
	}

	public Image onHover(final HoverEvent event)
	{
		super.onHover(event);
		return this;
	}

	public Image onMove(final MoveEvent event)
	{
		super.onMove(event);
		return this;
	}

	public Image onTouch(TouchEvent event)
	{
		super.onTouch(event);
		return this;
	}
}
