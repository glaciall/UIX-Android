package com.youaix.framework.mission;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.youaix.framework.common.Encrypt;
import com.youaix.framework.common.FileSystem;
import com.youaix.framework.common.ImageCache;
import com.youaix.framework.common.Network;
import com.youaix.framework.common.Schema;
import com.youaix.framework.page.PageManager;

import android.graphics.*;

public class ImageLoader extends Mission
{
	private String url = null;
	public ImageLoader(String mission, String url)
	{
		super(mission, PageManager.getInstance().getCurrent());
		this.url = url;
	}
	
	public String getUrl()
	{
		return this.url;
	}
	
	public Object handle() throws Exception
	{
		Schema.Uri uri = Schema.parse(this.url);
		ImageCache ic = ImageCache.getInstance();
		if (uri.getType() == Schema.URI_HTTP)
		{
			// 先从缓存中获取，如果得不到，才从网络上下载
			if (ic.isCached(this.url))
			{
				// ImageCache.getInstance().standby(this.url);
				return ic.get(this.url);
			}
			OutputStream writer = Schema.parse("cache://image-" + Encrypt.MD5(this.url)).getWriter();
			InputStream reader = Network.read(this.url, null, 1000);
			byte[] bts = new byte[128];
			int chx = 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
			while ((chx = reader.read(bts)) > -1)
			{
				baos.write(bts, 0, chx);
			}
			FileSystem.write(writer, baos.toByteArray());
			return ic.get(this.url);
		}
		else
		{
			// TODO: 非HTTP的图片资源，如何作好缓存与回收？
			// TODO: 要不TNND就当作HTTP资源一样的去处理？
			if (uri.getType() == Schema.URI_CONTENT && this.url.indexOf('?') > -1)
			{
				return FileSystem.getThumbnail(this.url);
			}
			else
			{
				// data = FileSystem.read(uri);
				// return BitmapFactory.decodeByteArray(data, 0, data.length, options);
				return uri.getBitmap();
			}
		}
	}

}
