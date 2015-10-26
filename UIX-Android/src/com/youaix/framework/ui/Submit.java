package com.youaix.framework.ui;

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
