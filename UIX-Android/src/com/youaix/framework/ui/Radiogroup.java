package com.youaix.framework.ui;

import java.util.ArrayList;
import java.util.LinkedList;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Radiogroup extends Element {
	private RadioGroup radioGroup;
	private int count = 0;

	private static int RGID = 1000;

	public Radiogroup()
	{
		super();
		radioGroup = new RadioGroup(getContext());
		RGID += 1000;
	}

	public Radiogroup setDirection(int direction) {
		radioGroup.setOrientation(direction);
		return this;
	}


	public Radiobox getCheckedRadioBox() {
		int checkedId = radioGroup.getCheckedRadioButtonId();
		LinkedList<Element> e = this.getChildren();
		for (Element element : e) {
			Radiobox radiobox = (Radiobox) element;
			RadioButton radiobutton = radiobox.getRadioButton();
			if (radiobutton.getId() == checkedId) {
				return radiobox;
			}
		}
		return null;

	}

	public String getValue() {
		Radiobox checkedItem = getCheckedRadioBox();
		return checkedItem == null ? null : checkedItem.getValue();
	}

	public String getText() {
		Radiobox checkedItem = getCheckedRadioBox();
		return checkedItem == null ? null : checkedItem.getRadioButton()
				.getText().toString();
	}

	public boolean isFormElement() {
		return true;
	}

	public Radiogroup append(Element el) {
		if (el == null)
			return this;
		((Radiobox) el).setId(RGID + count++);
		super.append(el);
		return this;
	}

	public View getContentView() {
		LayoutParams layout = this.getLayout();
		if (layout != null)
			this.radioGroup.setLayoutParams(layout);
		return this.radioGroup;
	}
}
