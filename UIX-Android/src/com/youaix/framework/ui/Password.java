package com.youaix.framework.ui;

import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class Password extends Element
{
    private EditText view = null;

    public Password()
    {
    	super();
        this.view = new EditText(getContext());
        this.view.setBackgroundDrawable(null);
        this.view.setTextSize(14);
        this.view.setInputType(0x01 | 0x80);
        // this.view.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.onFocus(new FocusEvent()
		{
			public void focus(Page page, Element element)
			{
				
			}
			
			public void blur(Page page, Element element)
			{
				
			}
		});
    }

    public String getText()
    {
        return this.view.getText().toString();
    }

    public Password setDefaultText(String text)
    {
        this.view.setHint(text);
        return this;
    }
    
    public String getValue()
    {
    	return this.view.getText().toString();
    }

    public Password setMaxLength(int length)
    {
        this.view.setFilters(new InputFilter[] { new InputFilter.LengthFilter(length) });
        return this;
    }

	public boolean isFormElement()
	{
		return true;
	}
	
	public Password setWidth(int width)
    {
        super.setWidth(width);
		return this;
	}
	
	public Password setTextSize(int size)
	{
		this.view.setTextSize(size);
		return this;
	}
	
	public Password setTextColor(int color)
	{
		this.view.setTextColor(color);
		return this;
	}
	
	public Password setColor(int color)
	{
		return this.setTextColor(color);
	}

	public Password setHeight(int height)
    {
        super.setHeight(height);
		return this;
	}

	public View getContentView()
    {
        ViewGroup.LayoutParams layout = this.getLayout();
        if (null != layout) this.view.setLayoutParams(layout);
		return this.view;
	}

    public Password onFocus(final FocusEvent event)
    {
        super.onFocus(event);
        return this;
    }

    public Password onClick(final ClickEvent event)
    {
        super.onClick(event);
        return this;
    }

    public Password onPress(final PressEvent event)
    {
        super.onPress(event);
        return this;
    }

    public Password onDrag(final DragEvent event)
    {
        super.onDrag(event);
        return this;
    }

    public Password onHover(final HoverEvent event)
    {
        super.onHover(event);
        return this;
    }

    public Password onMove(final MoveEvent event)
    {
        super.onMove(event);
        return this;
    }

    public Password onTouch(TouchEvent event)
    {
        super.onTouch(event);
        return this;
    }

	public Password setText(String text)
	{
		this.view.setText(text);
		return this;
	}

}
