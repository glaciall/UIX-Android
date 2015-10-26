package com.youaix.framework.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;
import android.text.style.TypefaceSpan;
import android.view.Gravity;
import android.view.View;

/**
 * 
 * @author wsh
 * 
 */
public class ImageButtonView extends View {

	private Bitmap bitmap = null;
	private String text = null;
	private int borderColor = 0xff000000;
	private String fontFamiy = "宋体";

	public ImageButtonView(Context context) {
		super(context);
	}

	int borderWidth = 0;
	private int textSize = 20;
	int textAlign = Gravity.CENTER;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = this.getMeasuredWidth();
		int height = this.getMeasuredHeight();
		Paint paint = new Paint();
		paint.setColor(borderColor);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(0, 0, width, height, paint);

		Paint paintBitmap = new Paint();
		FontMetrics imgfm = paintBitmap.getFontMetrics();
		int y = (int) ((height - imgfm.bottom - imgfm.top) / 2);
		canvas.drawBitmap(bitmap, 5, 5, paintBitmap);

		Paint paintText = new Paint();
		paintText.setTextSize(this.textSize);
		Typeface tf = Typeface.create(this.fontFamiy, Typeface.ITALIC);
		paintText.setTypeface(tf);
		FontMetrics fm = paintText.getFontMetrics();
		y = (int) (height - fm.bottom - fm.top) / 2;
		int x = bitmap.getWidth() + 5;
		canvas.drawText(this.text, x, y, paintText);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		android.util.Log.e("width:", widthMeasureSpec + "");
		android.util.Log.e("heightMeasureSpec:", heightMeasureSpec + "");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap, int width, int height) {
		this.bitmap = resizeBitmap(bitmap, width, height);
	}

	public static Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int newWidth = w;
			int newHeight = h;
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
					height, matrix, true);
			return resizedBitmap;
		} else {
			return null;
		}
	}

	public String getText() {

		return text;

	}

	public void setText(String text) {
		this.text = text;
		this.invalidate();
	}

}
