package com.youaix.framework.ui;

import com.youaix.framework.event.ClickEvent;
import com.youaix.framework.event.DragEvent;
import com.youaix.framework.event.HoverEvent;
import com.youaix.framework.event.MoveEvent;
import com.youaix.framework.event.PressEvent;
import com.youaix.framework.event.TouchEvent;
import com.youaix.framework.view.CheckBoxView;

import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class Checkbox extends Element
{
	private CheckBoxView checkbox;
	private String value;

	public Checkbox()
	{
		super();
		checkbox = new CheckBoxView(getContext());
	}

	public Checkbox setChecked(boolean checked)
	{
		this.checkbox.setChecked(checked);
		return this;
	}
	
	public boolean isChecked()
	{
		return this.checkbox.isChecked();
	}

	public Checkbox setWidth(int width)
	{
		checkbox.setWidth(width);
		return this;
	}

	public Checkbox setHeight(int height)
	{
		checkbox.setHeight(height);
		return this;
	}

	public String getValue()
	{
		if (this.checkbox.isChecked()) return this.value;
		return null;
	}

	public Checkbox setValue(String value)
	{
		this.value = value;
		return this;
	}

	public boolean isFormElement()
	{
		return true;
	}

	public View getContentView()
	{
		LayoutParams layout = this.getLayout();
		if (layout != null) this.checkbox.setLayoutParams(layout);
		return this.checkbox;
	}

	public Checkbox onClick(final ClickEvent event)
	{
		super.onClick(event);
		return this;
	}

	public Checkbox onPress(final PressEvent event)
	{
		super.onPress(event);
		return this;
	}

	public Checkbox onDrag(final DragEvent event)
	{
		super.onDrag(event);
		return this;
	}

	public Checkbox onHover(final HoverEvent event)
	{
		super.onHover(event);
		return this;
	}

	public Checkbox onMove(final MoveEvent event)
	{
		super.onMove(event);
		return this;
	}

	public Checkbox onTouch(TouchEvent event)
	{
		super.onTouch(event);
		return this;
	}
}
