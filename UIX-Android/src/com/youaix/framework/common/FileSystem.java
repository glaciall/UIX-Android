package com.youaix.framework.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.bobaoo.xiaobao.page.PageManager;
import com.bobaoo.xiaobao.page.Resolution;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

public final class FileSystem
{
    public static Bitmap readImageFile(String fPath) throws Exception
    {
        return BitmapFactory.decodeStream(new java.io.FileInputStream(fPath));
    }

    public static Bitmap readImageUri(Uri uri) throws Exception
    {
        int id = Integer.parseInt(uri.toString().replaceAll("^.*/(\\d+)$", "$1"));
        int degree = ImageProc.readPictureDegree(uri.getPath());
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(PageManager.getInstance().getCurrent().getContentResolver(), id, 1, new BitmapFactory.Options());
        return ImageProc.rotaingImage(degree, bitmap);
    }

    public static Bitmap readImageRes(String fPath) throws Exception
    {
        return BitmapFactory.decodeStream(FileSystem.class.getResourceAsStream(fPath));
    }
    
    public static Bitmap readImage(String srcName) throws Exception
    {
    	return BitmapFactory.decodeStream(FileSystem.class.getResourceAsStream("/res/drawable-xxhdpi/" + srcName));
    }
    
    public static Drawable readDrawable(String srcName) throws Exception
    {
    	return Drawable.createFromStream(FileSystem.class.getResourceAsStream("/res/drawable-xxhdpi/" + srcName + ".png"), srcName);
    }

    // 读文件
    public static byte[] read(Schema.Uri uri) throws Exception
    {
        return read(getReader(uri));
    }
    
    // 获取缩略图
    public static Bitmap getThumbnail(String uri) throws Exception
    {
    	long albumId = 0;
    	int type = Images.Thumbnails.MICRO_KIND;
    	albumId = Integer.parseInt(uri.replaceAll("^content://media/external/images/media/(\\d+).*$", "$1"));
    	if (uri.indexOf("mini") > -1) type = Images.Thumbnails.MINI_KIND;
    	return MediaStore.Images.Thumbnails.getThumbnail(PageManager.getInstance().getCurrent().getContentResolver(), albumId, type, null);
    }
    
    // 打开输入流
    public static InputStream getReader(Schema.Uri uri) throws Exception
    {
    	if (uri.getType() == Schema.URI_ASSETS) return PageManager.getInstance().getCurrent().getAssets().open(uri.getPath());
        if (uri.getType() == Schema.URI_RES) return FileSystem.class.getResourceAsStream("/res/drawable-xxhdpi/" + uri.getPath());
        if (uri.getType() == Schema.URI_CONTENT) return PageManager.getInstance().getCurrent().getContentResolver().openInputStream(Uri.parse(uri.toString()));
        if (uri.getType() == Schema.URI_CACHE)
        {
        	return new FileInputStream(uri.getFile());
        }
        if (uri.getType() == Schema.URI_FILE)
        {
        	return new FileInputStream(uri.getFile());
        }
    	return null;
    }
    
    // 打开输出流
    public static OutputStream getWriter(Schema.Uri uri) throws Exception
    {
    	if (uri.getType() == Schema.URI_CACHE)
    	{
    		return new FileOutputStream(uri.getFile());
    	}
    	if (uri.getType() == Schema.URI_FILE)
    	{
    		return new FileOutputStream(uri.getFile());
    	}
    	return null;
    }
    
    // 缓存文件是否存在
    public static boolean isExists(Schema.Uri uri)
    {
    	Context context = PageManager.getInstance().getCurrent();
		String cachePath = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			cachePath = context.getExternalCacheDir().getAbsolutePath();
		else
			cachePath = context.getCacheDir().getAbsolutePath();
		
		File f = new File(cachePath + File.separator + uri.getPath());
		return f.exists();
    }
    
    // 将内容写入输出流
    public static void write(OutputStream writer, byte[] data) throws Exception
    {
    	try
    	{
    		for (int i = 0, l = data.length; i < l; i += 512)
    		{
    			writer.write(data, i, (i + 512 > l ? l - i : 512));
    		}
    	}
    	finally
    	{
    		try { writer.close(); } catch (Exception e) { };
    	}
    }
    
    // 将输入流写入输出流
    public static void write(OutputStream writer, InputStream reader) throws Exception
    {
    	try
    	{
    		int ch = 0;
    		byte[] buffer = new byte[512];
    		while ((ch = reader.read(buffer)) > 0)
    		{
    			writer.write(buffer, 0, ch);
    		}
    	}
    	finally
    	{
    		try { reader.close(); } catch (Exception e) { }
    		try { writer.close(); } catch (Exception e) { }
    	}
    }
    
    // 读取输入流内容
    public static byte[] read(InputStream reader) throws Exception
    {
    	ByteArrayOutputStream baos = null;
    	try
    	{
    		baos = new ByteArrayOutputStream(40960);
    		byte[] bytes = new byte[4096];
            int ch = 0;
            while ((ch = reader.read(bytes)) > -1)
            {
            	baos.write(bytes, 0, ch);
            }
    		return baos.toByteArray();
    	}
    	finally
    	{
    		try { reader.close(); } catch(Exception e) { }
    		try { baos.close(); } catch(Exception e) { }
    	}
    }

    // 序列化
    public static void serialize(Schema.Uri uri, java.io.Serializable object) throws Exception
    {
    	ObjectOutputStream oos = null;
    	OutputStream writer = null;
    	try
    	{
    		oos = new ObjectOutputStream(writer = uri.getWriter());
    		oos.writeObject(object);
    	}
    	finally
    	{
    		try { writer.close(); } catch (Exception e) { }
    		try { oos.close(); } catch (Exception e) { }
    	}
    }
    
    // 反序列化
    public static Object unserialize(Schema.Uri uri) throws Exception
    {
    	ObjectInputStream ois = null;
    	InputStream reader = null;
    	Object data = null;
    	try
    	{
    		ois = new ObjectInputStream(reader = uri.getReader());
    		data = ois.readObject();
    	}
    	finally
    	{
    		try { reader.close(); } catch (Exception e) { }
    		try { ois.close(); } catch (Exception e) { }
    	}
    	return data;
    }
}
