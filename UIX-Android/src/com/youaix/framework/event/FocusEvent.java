package com.youaix.framework.event;

import com.youaix.framework.page.Page;
import com.youaix.framework.ui.Element;

public interface FocusEvent
{
	public void focus(Page page, Element element);
	public void blur(Page page, Element element);
}
