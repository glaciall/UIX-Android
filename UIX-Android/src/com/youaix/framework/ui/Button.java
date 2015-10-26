package com.youaix.framework.ui;

import android.view.View;
import android.view.MotionEvent;

public class Button extends Element
{
    private FlatButton view = null;

    public Button()
    {
    	super();
        this.view = new FlatButton(getContext());
    }

	public Button setWidth(int width)
    {
        super.setWidth(width);
		return this;
	}

	public Button setHeight(int height)
    {
        super.setHeight(height);
		return this;
	}

    public Button setBorderWidth(int size)
    {
        size = com.bobaoo.xiaobao.page.Resolution.pixels(size);
        this.view.setBorderWidth(size);
        return this;
    }

    public Button setBackgroundColor(int color)
    {
        this.view.setBackgroundColor(color);
        return this;
    }

    public Button setBorderColor(int color)
    {
        this.view.setBorderColor(color);
        return this;
    }

    public Button setTextColor(int color)
    {
        this.view.setTextColor(color);
        return this;
    }

    public Button setTextSize(int size)
    {
        this.view.setTextSize(Resolution.sp(size));
        return this;
    }

    public Button setTextAlign(int align)
    {
        this.view.setTextAlign(align);
        return this;
    }

    public Button setText(String text)
    {
        this.view.setText(text);
        return this;
    }

	public View getContentView()
    {
		return this.view;
	}

    public Button onClick(final ClickEvent event)
    {
        super.onClick(event);
        return this;
    }

    public Button onPress(final PressEvent event)
    {
        super.onPress(event);
        return this;
    }

    public Button onDrag(final DragEvent event)
    {
        super.onDrag(event);
        return this;
    }

    public Button onHover(final HoverEvent event)
    {
        super.onHover(event);
        return this;
    }

    public Button onMove(final MoveEvent event)
    {
        super.onMove(event);
        return this;
    }

    public Button onTouch(final TouchEvent event)
    {
        super.onTouch(event);
        return this;
    }
}
