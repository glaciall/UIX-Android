package com.youaix.framework.common;

import android.graphics.*;
import android.graphics.Bitmap.CompressFormat;

public class JPEGCompress
{
	public static Schema.Uri exec(Schema.Uri in)
	{
		return exec(in, 80);
	}
	
	public static Schema.Uri exec(Schema.Uri in, int quality)
	{
		Schema.Uri out = null;
		Bitmap bitmap = null;
		try
		{
			out = Schema.parse("cache://jpeg-" + Encrypt.MD5(in.toString()));
			bitmap = in.getBitmap();
			
			int destWidth = bitmap.getWidth();
			int destHeight = bitmap.getHeight();
			
			if (destWidth > 2000 || destHeight > 2000)
			{
				if (destWidth > destHeight)
				{
					destHeight = (int)((float)destHeight / ((float)destWidth / 2000));
					destWidth = 2000;
				}
				else
				{
					destWidth = (int)((float)destWidth / ((float)destHeight / 2000));
					destHeight = 2000;
				}
			}
			
			bitmap = Bitmap.createScaledBitmap(bitmap, destWidth, destHeight, true);
			bitmap.compress(CompressFormat.JPEG, quality, out.getWriter());
			return out;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
