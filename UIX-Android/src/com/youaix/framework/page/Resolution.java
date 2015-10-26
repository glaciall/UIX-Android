package com.youaix.framework.page;

import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;

public final class Resolution
{
    private static int statusBarHeight = 0;
    private static DisplayMetrics metric = null;

    public static void init(Page page)
    {
        metric = null;
        metric = new DisplayMetrics();
        page.getWindowManager().getDefaultDisplay().getMetrics(metric);

        statusBarHeight = Resources.getSystem().getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
    }

    public static int pixels(int dip)
    {
        int pixels = (int)(dip * getDensity() + 0.5f);
        return pixels;
    }

    public static int dip(int pixels)
    {
        return (int)((pixels - 0.5f) / getDensity());
    }

    public static float sp(int size)
    {
        return (size * metric.scaledDensity);
    }

    public static float getDensity()
    {
        return metric.density;
    }

    public static int getPixelsWidth(float percent)
    {
        return (int)(getScreenWidth() * percent);
    }

    public static int getWidth(float percent)
    {
        return (int)(getScreenWidthDip() * percent);
    }

    public static int getHeight(float percent)
    {
        return (int)(getScreenHeightDip() * percent);
    }

    public static int getPixelsHeight(float percent)
    {
        return (int)(getScreenHeight() * percent);
    }

    public static int getScreenWidth()
    {
        return metric.widthPixels;
    }

    public static int getScreenWidthDip()
    {
        return (int)(metric.widthPixels / getDensity() + 0.5f);
    }

    public static int getScreenHeight()
    {
        return metric.heightPixels - statusBarHeight;
    }

    public static int getScreenHeightDip()
    {
        return (int)((metric.heightPixels - statusBarHeight) / getDensity() + 0.5f);
    }
    
    public static String getIdentifier()
    {
    	int w = metric.widthPixels;
    	int h = metric.heightPixels;
    	String identifier = "UGA";
    	if (w == 480 && h == 640) identifier = "VGA";
    	else if (w == 240 && h == 320) identifier = "QVGA";
    	else if (w == 320 && h == 480) identifier = "HVGA";
    	else if (w == 600 && h == 800) identifier = "SVGA";
    	else if (w == 480 && h == 800) identifier = "WVGA";
    	else if (w == 480 && h == 854) identifier = "FWVGA";
    	else if (w == 1080 && h == 1920) identifier = "HD";
    	else if (w == 540 && h == 960) identifier = "QHD";
    	else if (w == 720 && h == 1280) identifier = "720P";
    	return identifier + "(" + w + "x" + h + ")";
    }
}
