package com.youaix.framework.ui;

import java.util.HashMap;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

public final class Span extends Element
{
    private TextView view = null;
    private int color = 0xff000000;
    private int colorHover = 0xff000000;
    private boolean isStyleStatable = false;

    public Span()
    {
    	super();
        this.view = new TextView(getContext());
        this.view.setTextColor(this.color);
        this.view.setTextSize(12);
    }

    public int getColor()
    {
    	return this.color;
    }
    
    public Span setColor(int color)
    {
        this.view.setTextColor(this.color = color);
        return this;
    }
    
    public Span setHoverColor(int color)
    {
    	this.isStyleStatable = true;
    	this.colorHover = color;
    	return this;
    }
    
    @Override
    public boolean isStyleStatable()
    {
    	return this.isStyleStatable;
    }
    
    @Override
    public StatableStyle generateStyle()
    {
    	final int clr = this.color;
    	final int clrHover = this.colorHover;
    	return new StatableStyle()
    	{
			@Override
			public void onHover(Element element)
			{
				Span self = (Span)element;
				self.setColor(clrHover);
			}

			@Override
			public void onRelease(Element element)
			{
				Span self = (Span)element;
				self.setColor(clr);
			}
    	};
    }

    public Span setText(String text)
    {
        this.view.setText(text);
        return this;
    }
    
    public Span setRichText(String text) throws Exception
	{
    	SpannableStringBuilder builder = new SpannableStringBuilder(text);
		boolean found = false;
		int sidx = 0, eidx = 0;
		for (int i = builder.length() - 1; i >= 0; i--)
		{
			char chr = builder.charAt(i);
			if (chr == ']')
			{
				found = true;
				eidx = i;
			}
			if (chr == '[')
			{
				if (!found)
				{
					found = false;
					continue;
				}
				sidx = i;
				Schema.Uri res = Input.emotions.get(builder.subSequence(sidx + 1, eidx).toString());
				if (null == res) continue;
				builder.setSpan(new ImageSpan(res.getBitmap(), ImageSpan.ALIGN_BOTTOM), sidx, eidx + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		this.view.setText(builder);
		return this;
	}
    
    public String getText()
    {
    	return this.view.getText().toString();
    }

    public Span setSize(int size)
    {
        this.view.setTextSize(size);
        return this;
    }

    public Span setBackgroundColor(int color)
    {
        this.view.setBackgroundColor(color);
        return this;
    }

    public TextView getContentView()
    {
        LayoutParams layout = this.getLayout();
        if (layout != null) this.view.setLayoutParams(layout);
        return this.view;
    }

	public Span setWidth(int width)
    {
        super.setWidth(width);
		return this;
	}

	public Span setHeight(int height)
    {
        super.setHeight(height);
		return this;
	}

    public Span onClick(final ClickEvent evt)
    {
        super.onClick(evt);
        return this;
    }

	public Span onPress(final PressEvent event)
    {
        super.onPress(event);
		return this;
	}

	public Span onDrag(final DragEvent event)
    {
        super.onDrag(event);
		return this;
	}

	public Span onHover(final HoverEvent event)
    {
        super.onHover(event);
		return this;
	}

	public Span onMove(final MoveEvent event)
    {
        super.onMove(event);
		return this;
	}

	public Element onTouch(final TouchEvent event)
    {
        super.onTouch(event);
		return this;
	}
	
	public Span setCrossLine(boolean crossline)
	{
		this.view.getPaint().setFlags(crossline ? Paint.STRIKE_THRU_TEXT_FLAG : 0);
		return this;
	}
	
	public Span setUnderLine(boolean underline)
	{
		this.view.getPaint().setFlags(underline ? Paint.UNDERLINE_TEXT_FLAG : 0);
		return this;
	}
	
	public Span setBold(boolean bold)
	{
		this.view.setTypeface(Typeface.DEFAULT, bold ? Typeface.BOLD : Typeface.NORMAL);
		return this;
	}
	
	public Span setItalic(boolean italic)
	{
		this.view.setTypeface(Typeface.DEFAULT, italic ? Typeface.ITALIC : Typeface.NORMAL);
		return this;
	}
}
