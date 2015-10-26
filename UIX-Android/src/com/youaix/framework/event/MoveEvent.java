package com.youaix.framework.event;

import com.youaix.framework.page.Page;
import com.youaix.framework.ui.Element;

public interface MoveEvent
{
    public void on(Page page, Element element, Event evt);
}
