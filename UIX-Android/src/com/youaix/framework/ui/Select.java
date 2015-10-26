package com.youaix.framework.ui;

import com.youaix.framework.event.ChangeEvent;
import com.youaix.framework.event.ClickEvent;
import com.youaix.framework.event.DragEvent;
import com.youaix.framework.event.HoverEvent;
import com.youaix.framework.event.MoveEvent;
import com.youaix.framework.event.PressEvent;
import com.youaix.framework.event.TouchEvent;
import com.youaix.framework.page.PageManager;
import com.youaix.framework.page.Resolution;
import com.youaix.framework.view.FlowLayout;
import com.youaix.framework.view.SpinnerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class Select extends Element
{
	private FlowLayout wrapper = null;
	private SpinnerView spinner = null;
	private int position = 0;
	private ArrayAdapter<Option> adapter = null;

	private int borderWidth = 0;
	private int borderColor = 0;
	private int textSize = 12;
	private int textColor = 0xff000000;
	private int backgroundColor;
	
	public Select()
	{
		super();
		spinner = new SpinnerView(getContext());
		adapter = new ArrayAdapter<Option>(getContext(), android.R.layout.simple_dropdown_item_1line)
				{
					public View getView(int position, View convertView, ViewGroup parent)
					{
						if (null != convertView) return convertView;
						LinearLayout select = new LinearLayout(getContext());
						TextView tv = new TextView(getContext());
						tv.setText(getItem(position).getText());
						tv.setTextSize(12);
						tv.setTextColor(0xff000000);
						select.addView(tv);
						
						return select;
					}
				};
		spinner.setBackgroundDrawable(null);
		int padding = Resolution.pixels(2);
		spinner.setPadding(padding, 0, padding, padding * 2);
		spinner.setAdapter(adapter);
		
		this.borderWidth = Resolution.pixels(1);
		this.borderColor = 0xff666666;
		this.backgroundColor = 0xffffffff;
	}

	public Element append(Element el)
	{
		if (el == null) return null;
		Option option = (Option) el;
		adapter.add(option);
		return this;
	}
	
	public boolean removeChild(Option el)
	{
		this.adapter.remove(el);
		return true;
	}
	
	public String getValue()
	{
		Option option = this.getSelectionOption();
		if (null == option) return null;
		else return option.getValue();
	}

	public String getText()
	{
		return this.getSelectionOption().getText();
	}

	public Select setSelection(int position)
	{
		spinner.setSelection(this.position = position);
		return this;
	}

	public int getSelection()
	{
		return this.spinner.getSelectedItemPosition();
	}

	public Option getSelectionOption()
	{
		return (Option)this.spinner.getSelectedItem();
		// return this.adapter.getItem(position);
	}

	public boolean isFormElement()
	{
		return true;
	}

	public Select setAdapter(SpinnerAdapter adapter)
	{
		spinner.setAdapter(adapter);
		return this;
	}

	public View getContentView()
	{
		ViewGroup.LayoutParams layout = this.getLayout();
		if (null != layout) this.spinner.setLayoutParams(layout);
		return this.spinner;
	}
	
	public View getWrapperView()
	{
		if (null == this.wrapper)
		{
			wrapper = new FlowLayout(getContext());
			
			Shape triangle = (Shape)new Shape().setColor(0xff666666).setWidth(6).setHeight(6).setRight(5).setTop(Attribute.POSITION_MIDDLE);
			wrapper.addView(triangle.getWrapperView(), triangle.getLayout());
			wrapper.addView(this.getContentView());
			wrapper.setBorderWidth(this.borderWidth, this.borderWidth, this.borderWidth, this.borderWidth);
			wrapper.setBorderColor(this.borderColor, this.borderColor, this.borderColor, this.borderColor);
			wrapper.setRadius(2, 2);
			wrapper.setBackgroundColor(this.backgroundColor);
			wrapper.setBgColor(this.backgroundColor);
		}
		ViewGroup.LayoutParams layout = this.getLayout();
		if (null != layout) this.wrapper.setLayoutParams(layout);
		return this.wrapper;
	}

	public Select onClick(final ClickEvent event)
	{
		super.onClick(event);
		return this;
	}

	public Select onPress(final PressEvent event)
	{
		super.onPress(event);
		return this;
	}

	public Select onDrag(final DragEvent event)
	{
		super.onDrag(event);
		return this;
	}

	public Select onHover(HoverEvent event)
	{
		super.onHover(event);
		return this;
	}

	public Select onMove(final MoveEvent event)
	{
		super.onMove(event);
		return this;
	}

	public Select onTouch(final TouchEvent event)
	{
		super.onTouch(event);
		return this;
	}
	
	// 操它大爷的，居然第一次加到UI里时会触发该事件
	public Select onChange(final ChangeEvent event)
	{
		final Select self = this;
		this.spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View selectedView, int position, long id)
			{
				try
				{
					event.on(PageManager.getInstance().getCurrent(), self);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
				try
				{
					event.on(PageManager.getInstance().getCurrent(), self);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		});
		return this;
	}
}
