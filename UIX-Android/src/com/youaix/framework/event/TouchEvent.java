package com.youaix.framework.event;

public interface TouchEvent
{
    public void down(Page page, Element element);
    public void up(Page page, Element element);
    public void move(Page page, Element element);
    public void cancel(Page page, Element element);
}
