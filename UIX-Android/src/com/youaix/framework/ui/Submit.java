package com.youaix.framework.ui;

import com.youaix.framework.event.ClickEvent;
import com.youaix.framework.page.Page;

public class Submit extends Button
{
	public Submit()
	{
		super();
		
		super.onClick
		(
			new ClickEvent()
			{
				public void on(Page page, Element el)
				{
					page.submit();
				}
			}
		);
	}
	
	public Submit onClick(ClickEvent evt)
	{
		return this;
	}
}
