package com.youaix.framework.ui;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.View;

public class ImageButton extends Element {

	private ImageButtonView imageButtonView;

	public ImageButton()
	{
		super();
		this.imageButtonView = new ImageButtonView(getContext());
	}

	@Override
	public View getContentView() {
		return this.imageButtonView;
	}

	public ImageButton setText(String text) {
		this.imageButtonView.setText(text);
		return this;
	}

	public ImageButton setWidth(int width) {
		super.setWidth(width);
		return this;
	}

	public ImageButton setHeight(int height) {
		super.setHeight(height);
		return this;
	}

	public ImageButton setBitmap(Bitmap bitmap, int width, int height) {
		imageButtonView.setBitmap(bitmap, width, height);
		return this;
	}

	public ImageButton setBitmap(Bitmap bitmap, int height) {
		
		int newWidth = height * (bitmap.getWidth() / bitmap.getHeight());
		imageButtonView.setBitmap(bitmap, newWidth, height);
		return this;
	}

}
