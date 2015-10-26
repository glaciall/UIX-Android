package com.youaix.framework.event;

public interface FocusEvent
{
	public void focus(Page page, Element element);
	public void blur(Page page, Element element);
}
