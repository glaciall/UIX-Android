package com.youaix.framework.event;

import com.youaix.framework.page.Page;
import com.youaix.framework.ui.Element;

public interface TouchEvent
{
    public void down(Page page, Element element);
    public void up(Page page, Element element);
    public void move(Page page, Element element);
    public void cancel(Page page, Element element);
}
