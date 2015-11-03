package com.youaix.framework.ui;

import java.util.HashMap;

import com.youaix.framework.common.Schema;
import com.youaix.framework.event.ClickEvent;
import com.youaix.framework.event.DragEvent;
import com.youaix.framework.event.FocusEvent;
import com.youaix.framework.event.HoverEvent;
import com.youaix.framework.event.MoveEvent;
import com.youaix.framework.event.PressEvent;
import com.youaix.framework.event.TouchEvent;
import com.youaix.framework.page.Page;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.SingleLineTransformationMethod;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

public class Input extends Element
{
	public static final int METHOD_NUMBER = 0x02;
	public static final int METHOD_PHONE = 0x03;
	public static final int METHOD_URL = 0x01 | 0x10;
	public static final int METHOD_EMAIL = 0xd0;
	public static final int METHOD_DATE = 0x04;
	public static final int METHOD_PASSWORD = 0x01 | 0x80;
	public static final int METHOD_NUMBER_DECIMAL = 0x00002002;

	public static final HashMap<String, Schema.Uri> emotions = new HashMap<String, Schema.Uri>()
	{
		{
			put("开心", Schema.parse("res://emot_1.png"));
			put("大笑", Schema.parse("res://emot_2.png"));
			put("叶舌", Schema.parse("res://emot_3.png"));
			put("惊讶", Schema.parse("res://emot_4.png"));
			put("深沉", Schema.parse("res://emot_5.png"));
			put("愤怒", Schema.parse("res://emot_6.png"));
			put("微笑", Schema.parse("res://emot_7.png"));
			put("流汗", Schema.parse("res://emot_8.png"));
			put("大哭", Schema.parse("res://emot_9.png"));
			put("呵呵", Schema.parse("res://emot_10.png"));
			put("鄙视", Schema.parse("res://emot_11.png"));
			put("哀伤", Schema.parse("res://emot_12.png"));
			put("大拇指", Schema.parse("res://emot_13.png"));
			put("脸红", Schema.parse("res://emot_14.png"));
			put("疑惑", Schema.parse("res://emot_15.png"));
			put("脸红", Schema.parse("res://emot_16.png"));
			put("呕吐", Schema.parse("res://emot_17.png"));
			put("哦？", Schema.parse("res://emot_18.png"));
			put("难为情", Schema.parse("res://emot_19.png"));
			put("亲嘴", Schema.parse("res://emot_20.png"));
			put("呃", Schema.parse("res://emot_21.png"));
			put("咯咯", Schema.parse("res://emot_22.png"));
			put("鄙视的笑", Schema.parse("res://emot_23.png"));
			put("可爱", Schema.parse("res://emot_24.png"));
			put("阴笑", Schema.parse("res://emot_25.png"));
			put("眨眼", Schema.parse("res://emot_26.png"));
			put("大汗", Schema.parse("res://emot_27.png"));
			put("可怜", Schema.parse("res://emot_28.png"));
			put("睡觉", Schema.parse("res://emot_29.png"));
			put("震惊", Schema.parse("res://emot_30.png"));
			put("狂怒", Schema.parse("res://emot_31.png"));
			put("啊！", Schema.parse("res://emot_32.png"));
			put("噗", Schema.parse("res://emot_33.png"));
			put("爱心", Schema.parse("res://emot_34.png"));
			put("心碎", Schema.parse("res://emot_35.png"));
			put("玫瑰", Schema.parse("res://emot_36.png"));
			put("礼物", Schema.parse("res://emot_37.png"));
			put("彩虹", Schema.parse("res://emot_38.png"));
			put("月亮", Schema.parse("res://emot_39.png"));
			put("太阳", Schema.parse("res://emot_40.png"));
			put("金钱", Schema.parse("res://emot_41.png"));
			put("灯泡", Schema.parse("res://emot_42.png"));
			put("咖啡", Schema.parse("res://emot_43.png"));
			put("蛋糕", Schema.parse("res://emot_44.png"));
			put("音乐", Schema.parse("res://emot_45.png"));
			put("我爱你", Schema.parse("res://emot_46.png"));
			put("胜利", Schema.parse("res://emot_47.png"));
			put("了不起", Schema.parse("res://emot_48.png"));
			put("差劲", Schema.parse("res://emot_49.png"));
			put("OKAY", Schema.parse("res://emot_50.png"));
		}
	};
	
	protected EditText view = null;

	private int textColor = 0xff000000;
	private String defaultText = null;

	public Input()
	{
		super();
		this.view = new EditText(getContext());
		this.view.setBackgroundDrawable(null);
		this.view.setTextSize(12);
		this.view.setSingleLine();
		this.setWidthPercent(1.0f);

		// 设置为单行文本
		this.view.setTransformationMethod(SingleLineTransformationMethod.getInstance());
		
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
	
	public void hideKeyboard()
	{
		getContext().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		android.view.View view = this.getContentView();
		InputMethodManager mgr = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public Input setColor(int color)
	{
		this.view.setTextColor(this.textColor = color);
		return this;
	}

	public Input setTextSize(int size)
	{
		this.view.setTextSize(size);
		return this;
	}

	public Input setMaxLength(int length)
	{
		this.view.setFilters(new InputFilter[] { new InputFilter.LengthFilter(length) });
		return this;
	}

	public Input setText(String text)
	{
		text = null == text ? "" : text;
		this.view.setText("");
		this.view.append(text);
		this.view.setTextColor(this.textColor);
		return this;
	}
	
	public Input setValue(String text)
	{
		return this.setText(text);
	}
	
	public static interface RichTextFormatter
	{
		public SpannableString format(String sourceText);
	}
	
	public Input setReadOnly(boolean readOnly)
	{
		this.view.setCursorVisible(!readOnly);
		this.view.setFocusable(!readOnly);
		this.view.setFocusableInTouchMode(!readOnly);
		return this;
	}
	
	public Input setRichText(String text) throws Exception
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
				Schema.Uri res = emotions.get(builder.subSequence(sidx + 1, eidx).toString());
				if (null == res) continue;
				builder.setSpan(new ImageSpan(res.getBitmap(), ImageSpan.ALIGN_BOTTOM), sidx, eidx + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		this.view.setText(builder);
		return this;
	}
	
	public Input appendRichText(String text) throws Exception
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
				Schema.Uri res = emotions.get(builder.subSequence(sidx + 1, eidx).toString());
				if (null == res) continue;
				builder.setSpan(new ImageSpan(res.getBitmap(), ImageSpan.ALIGN_BOTTOM), sidx, eidx + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		this.view.append(builder);
		return this;
	}

	public Input setType(String type)
	{
		if ("email".equals(type)) this.view.setInputType(METHOD_EMAIL);
		else if ("date".equals(type)) this.view.setInputType(METHOD_DATE);
		else if ("number".equals(type)) this.view.setInputType(METHOD_NUMBER);
		else if ("numberDecimal".equals(type)) this.view.setInputType(METHOD_NUMBER_DECIMAL);
		else if ("password".equals(type)) this.view.setInputType(METHOD_PASSWORD);
		else if ("phone".equals(type)) this.view.setInputType(METHOD_PHONE);
		else if ("url".equals(type)) this.view.setInputType(METHOD_URL);
		else this.view.setInputType(0x01);
		return this;
		
	}
	
	public boolean isFormElement()
	{
		return true;
	}
	
	public Input setDefaultText(String text)
	{
		this.view.setHint(text);
		return this;
	}

	public int getTextColor()
	{
		return this.textColor;
	}

	public boolean isDefaultText()
	{
		if (null == this.defaultText) return false;
		String text = this.view.getText().toString();
		return this.defaultText.equals(text) || "".equals(text);
	}

	public String getText()
	{
		return this.view.getText().toString();
	}

	public String getValue()
	{
		return this.view.getText().toString();
	}
	
	public String getDefaultText()
	{
		return this.defaultText;
	}

	public Input setWidth(int width)
	{
		super.setWidth(width);
		return this;
	}

	public Input setHeight(int height)
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

	public Input onFocus(final FocusEvent event)
	{
		super.onFocus(event);
		return this;
	}

	public Input onClick(final ClickEvent event)
	{
		super.onClick(event);
		return this;
	}

	public Input onPress(final PressEvent event)
	{
		super.onPress(event);
		return this;
	}

	public Input onDrag(final DragEvent event)
	{
		super.onDrag(event);
		return this;
	}

	public Input onHover(final HoverEvent event)
	{
		super.onHover(event);
		return this;
	}

	public Input onMove(final MoveEvent event)
	{
		super.onMove(event);
		return this;
	}

	public Input onTouch(TouchEvent event)
	{
		super.onTouch(event);
		return this;
	}

}
