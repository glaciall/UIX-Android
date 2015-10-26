package com.youaix.framework.ui;

import com.youaix.framework.event.ClickEvent;
import com.youaix.framework.event.DragEvent;
import com.youaix.framework.event.HoverEvent;
import com.youaix.framework.event.MoveEvent;
import com.youaix.framework.event.PressEvent;
import com.youaix.framework.event.TouchEvent;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioButton;

public class Radiobox extends Element
{
	private RadioButton radiobutton;
	private String value;

	public Radiobox()
	{
		super();
		radiobutton = new RadioButton(getContext());
		radiobutton.setTextColor(0xff000000);
	}

	public Radiobox setChecked(boolean ischeck)
	{
		this.radiobutton.setChecked(ischeck);
		return this;
	}

	public Radiobox setColor(int color)
	{
		radiobutton.setTextColor(color);
		return this;
	}

	public Radiobox setTextSize(int size)
	{
		this.radiobutton.setTextSize(size);
		return this;
	}

	public String getValue()
	{
		return value;
	}

	public Radiobox setValue(String value)
	{
		this.value = value;
		return this;
	}

	public Radiobox setText(String text)
	{
		this.radiobutton.setText(text);
		return this;
	}

	public Radiobox setId(int id)
	{
		this.radiobutton.setId(id);
		return this;
	}

	public View getContentView()
	{
		LayoutParams layout = this.getLayout();
		if (layout != null) this.radiobutton.setLayoutParams(layout);
		return this.radiobutton;
	}

	public RadioButton getRadioButton()
	{
		return this.radiobutton;
	}

	public Radiobox onClick(final ClickEvent event)
	{
		super.onClick(event);
		return this;
	}

	public Radiobox onPress(final PressEvent event)
	{
		super.onPress(event);
		return this;
	}

	public Radiobox onDrag(final DragEvent event)
	{
		super.onDrag(event);
		return this;
	}

	public Radiobox onHover(final HoverEvent event)
	{
		super.onHover(event);
		return this;
	}

	public Radiobox onMove(final MoveEvent event)
	{
		super.onMove(event);
		return this;
	}

	public Radiobox onTouch(TouchEvent event)
	{
		super.onTouch(event);
		return this;
	}
}
