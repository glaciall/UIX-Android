package com.youaix.framework.event;

import com.youaix.framework.page.Page;
import com.youaix.framework.ui.Element;

public interface ClickEvent
{
	public void on(Page page, Element element);
}
