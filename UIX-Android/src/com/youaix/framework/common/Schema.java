package com.youaix.framework.common;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;
import android.provider.MediaStore;

public class Schema
{
	public static final int URI_MEDIA = 0x01;
	public static final int URI_HTTP = 0x02;
	public static final int URI_TEL = 0x03;
	public static final int URI_SMS = 0x04;
	public static final int URI_FILE = 0x05;
	public static final int URI_RES = 0x06;
	public static final int URI_ASSETS = 0x07;
	public static final int URI_PAGE = 0x08;
	public static final int URI_CONTENT = 0x09;
	public static final int URI_CACHE = 0x0a;
	public static final int URI_MARKET = 0x0b;
	
	public static Uri parse(String uri)
	{
		return uri(uri);
	}
	
	@Deprecated
	public static Uri uri(String uri)
	{
		int type = 0;
		if (uri.startsWith("media")) type = URI_MEDIA;
		else if (uri.startsWith("http")) type = URI_HTTP;
		else if (uri.startsWith("tel")) type = URI_TEL;
		else if (uri.startsWith("sms")) type = URI_SMS;
		else if (uri.startsWith("file")) type = URI_FILE;
		else if (uri.startsWith("res")) type = URI_RES;
		else if (uri.startsWith("assets")) type = URI_ASSETS;
		else if (uri.startsWith("page")) type = URI_PAGE;
		else if (uri.startsWith("content")) type = URI_CONTENT;
		else if (uri.startsWith("cache")) type = URI_CACHE;
		else if (uri.startsWith("market")) type = URI_MARKET;
		return new Uri(type, uri);
	}
	
	public static final class Uri
	{
		int type;
		String url;
		
		static BitmapFactory.Options rgb_options = null;
		static BitmapFactory.Options argb_options = null;
		
		static
		{
			rgb_options = new BitmapFactory.Options();
			rgb_options.inPreferredConfig = Bitmap.Config.RGB_565;
			rgb_options.inPurgeable = true;
			rgb_options.inInputShareable = true;
			
			argb_options = new BitmapFactory.Options();
			argb_options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			argb_options.inPurgeable = true;
			argb_options.inInputShareable = true;
		}
		
		public Uri(int type, String url)
		{
			this.type = type;
			this.url = url;
		}
		
		public int getType()
		{
			return this.type;
		}
		
		public String getUrl()
		{
			return this.url;
		}
		
		public java.io.InputStream getReader() throws Exception
		{
			return FileSystem.getReader(this);
		}
		
		public java.io.OutputStream getWriter() throws Exception
		{
			OutputStream writer = FileSystem.getWriter(this);
			return writer;
		}
		
		public String getPath()
		{
			if (this.type == Schema.URI_MEDIA) return this.url;
			else
			{
				String path = this.url.substring(this.url.indexOf("://") + 3);
				int idx = path.indexOf("?");
				if (idx > -1) path = path.substring(0, idx);
				return path;
			}
		}
		
		public int getId() throws Exception
		{
			String path = this.getPath();
			int idx = path.indexOf('/');
			String dir = path.substring(0, idx);
			String fname = path.substring(idx + 1, path.indexOf('.'));
			Class cls = Class.forName(Global.getProperty("android_r_class") + "$" + dir);
			return cls.getField(fname).getInt(null);
		}
		
		public File getFile()
		{
			if (this.type == Schema.URI_CONTENT)
			{
				File file = null;
				android.net.Uri uri = android.net.Uri.parse(this.url);
				String[] fields = { MediaStore.Images.Media.DATA };
				Cursor cursor = null;
				
				try
				{
					cursor = PageManager.getInstance().getCurrent().managedQuery(uri, fields, null, null, null);
					int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					if (cursor.moveToFirst())
					{
						file = new File(cursor.getString(idx));
					}
				}
				finally
				{
					try { cursor.close(); } catch(Exception e) { }
				}
				
				return file;
			}
			if (this.type == Schema.URI_CACHE)
	        {
	        	Context context = PageManager.getInstance().getCurrent();
	    		String cachePath = null;
	    		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
	    			cachePath = context.getExternalCacheDir().getAbsolutePath();
	    		else
	    			cachePath = context.getCacheDir().getAbsolutePath();
	    		
	    		return new File(cachePath + File.separator + this.getPath());
	        }
	        if (this.type == Schema.URI_FILE)
	        {
	        	String root = Environment.getExternalStorageDirectory().getAbsolutePath();
	    		String fPath = this.getPath();
	    		if (fPath.indexOf('/') > -1)
	    			fPath = fPath.substring(0, fPath.lastIndexOf('/'));
	    		else
	    			fPath = "";
	    		File dir = new File(root + File.separator + PageManager.getInstance().getCurrent().getPackageName() + File.separator + fPath);
	    		if (!dir.exists() || !dir.isDirectory())
	    			dir.mkdirs();
	    		fPath = this.getPath();
	    		return new File(dir, fPath.substring(fPath.lastIndexOf('/') + 1));
	        }
			else return null;
		}
		
		public Bitmap getBitmap() throws Exception
		{
			java.io.InputStream reader = null;
			try
			{
				if (this.type == Schema.URI_RES)
					return BitmapFactory.decodeStream(reader = this.getReader(), null, argb_options);
				else if (this.type == Schema.URI_CONTENT)
				{
					if (this.url.indexOf('?') > -1)
						return FileSystem.getThumbnail(this.url);
					else
						return BitmapFactory.decodeStream(reader = this.getReader(), null, rgb_options);
				}
				else
				{
					reader = this.getReader();
					Bitmap bm = BitmapFactory.decodeStream(reader, null, rgb_options);
					return bm;
				}
			}
			catch(OutOfMemoryError err)
			{
				return null;
			}
			finally
			{
				try { reader.close(); } catch(Exception e) { }
			}
		}
		
		public String toString()
		{
			return this.url;
		}
	}
}
