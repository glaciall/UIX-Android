package com.youaix.framework.ui;

import com.youaix.framework.view.AnchorView;

import android.view.View;

public class A extends Element
{
	private String href = null;
	private String target = "_self";
	private String frame = null;
	private AnchorView view = null;
	private boolean closeCurrentPage = false;
	
	public A()
	{
		super();
		this.view = new AnchorView(getContext());
	}
	
	public void setFrameSet(String frameId)
	{
		this.frame = frameId;
	}
	
	public String getHref()
	{
		return this.href;
	}
	
	public A setClose(String target)
	{
		this.closeCurrentPage = "self".equals(target);
		return this;
	}
	
	public boolean isCloseCurrent()
	{
		return this.closeCurrentPage;
	}
	
	public A onTouch(TouchEvent evt)
	{
		return this;
	}
	
	public A setHref(String href)
	{
		this.href = href;
		return this;
	}
	
	public View getContentView()
	{
		return this.view;
	}
	
	public View getWrapperView()
	{
		super.onTouch
		(
			new TouchEvent()
			{
				public void up(Page page, Element el)
				{
					A link = (A)el;
					Schema.Uri href = Schema.parse(link.getHref());
					// 如果是http地址，也直接使用redirect方法
					if (href.getType() == Schema.URI_HTTP)
					{
						PageManager.getInstance().redirect(href, isCloseCurrent());
						return;
					}
					
					// 如果target是_top的话或，则直接调用redirect方法就妥当了
					if (frame == null || "_top".equals(target))
					{
						PageManager.getInstance().redirect(href, isCloseCurrent());
						return;
					}
				}
				public void down(Page page, Element el) { }
				public void move(Page page, Element el) { }
				public void cancel(Page page, Element el) { }
			}
		);
		return this.getContentView();
	}
	
	public A setTarget(String target)
	{
		this.target = target;
		return this;
	}
	
	public String getTarget()
	{
		return this.target;
	}

	@Override
	public A append(Element el)
	{
		if (el == null) return this;
		// 如果是百分比的宽度，则子元素100%，父元素使用子元素的宽度比
		if (el.widthPercent > -1.0f)
		{
			this.setWidth(el.widthPercent);
			el.setWidth(1.0f);
		}
		else
		{
			this.setWidth(el.width);
		}
		
		// 如果是百分比的高度，则子元素100%，父元素使用子元素的高度比
		if (el.heightPercent > -1.0f)
		{
			this.setHeight(el.heightPercent);
			el.setHeight(1.0f);
		}
		else
		{
			this.setHeight(el.height);
		}
		
		// 如果是固定值的高度，则父子元素使用同样的高度值，父元素使用子元素的外间距，子元素无外间距
		// el.margins = el.margins;
		System.arraycopy(el.margins, 0, this.margins, 0, 4);
		
		// TODO: 子元素的相对定位属性未复制
		
		super.append(el);
		return this;
	}
}
