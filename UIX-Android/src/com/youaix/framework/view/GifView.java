package com.youaix.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.view.View;

public class GifView extends View
{
	// 自己来画个进度条
	
	private boolean running = false;
	private Movie movie = null;
	private long startTime = 0;
	
	public GifView(Context context)
	{
		super(context);
	}
	
	public void setDrawable(Movie movie)
	{
		this.movie = movie;
		this.invalidate();
	}
	
	public void onDraw(Canvas canvas)
	{
		long time = android.os.SystemClock.uptimeMillis();
		if (startTime == 0) startTime = time;
		if (movie != null)
		{
			int dur = movie.duration();
			if (dur == 0) dur = 1000;
			int relTime = (int)((time - startTime) % dur);
			movie.setTime(relTime);
			movie.draw(canvas, 0, 0);
		}
	}
}