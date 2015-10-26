package com.youaix.framework.event;

import com.youaix.framework.page.Page;
import com.youaix.framework.ui.Element;

public interface KeyEvent
{
	public static int KEY_ENTER = 66;
	public boolean down(Page page, Element element, int keyCode);
	public void up(Page page, Element element, int keycode);
}
