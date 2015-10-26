package com.youaix.framework.mission;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

import com.youaix.framework.common.Configuration;
import com.youaix.framework.common.JPEGCompress;
import com.youaix.framework.common.Schema;
import com.youaix.framework.common.Schema.Uri;
import com.youaix.framework.page.PageManager;

public class FormSubmitter extends Mission
{
	private String url = null;
	private boolean haveFiles = false;
	private HashMap<String, Object> data = null;
	
	public FormSubmitter(String mission, String url, boolean haveFiles, HashMap<String, Object> data)
	{
		super(mission, PageManager.getInstance().getCurrent());
		this.haveFiles = haveFiles;
		this.data = data;
		this.url = url;
	}
	
	private int countBytes(int boundaryBytes) throws Exception
	{
		int bytes = 0;
		Iterator itr = this.data.keySet().iterator();
		while (itr.hasNext())
		{
			String name = (String)itr.next();
			Object value = this.data.get(name);
			if (haveFiles)
			{
				bytes += 4 + boundaryBytes;
				bytes += 39 + name.getBytes().length;
				
				if (value instanceof Schema.Uri)
				{
					Schema.Uri uri = JPEGCompress.exec((Schema.Uri)value);
					if (uri != null)
					{
						value = uri;
						this.data.put(name, value);
					}
				}
				
				if (value instanceof Schema.Uri)
					bytes += 40 + 13 + ((Schema.Uri)value).getPath().getBytes().length;
				bytes += 4;
				if (value instanceof Schema.Uri)
					bytes += ((Schema.Uri)value).getFile().length();
				else
					bytes += String.valueOf((null == value ? "" : value)).getBytes(Configuration.ENCODING).length;
				bytes += 2;
			}
			else
			{
				bytes += (name + "=" + java.net.URLEncoder.encode(String.valueOf(value), "UTF-8") + "&").getBytes().length;
			}
		}
		
		if (haveFiles)
		{
			bytes += 5;
			bytes += boundaryBytes;
		}
		
		return bytes;
	}
	
	public Object handle() throws Exception
	{
		float percent = 0.0f;
		String boundary = "-----------------------------" + (100000000000L + (long)(Math.random() * 10000000000L));
		Iterator itr = this.data.keySet().iterator();
		
		// TODO: 发送Content-Length字段吧
		int sentBytes = 0;
		int bytes = countBytes(boundary.length());
		int ch = -1;
        String shtml = "";
        URL oUrl = null;
        HttpURLConnection urlConn = null;
        InputStream bis = null;
        OutputStream baos = null;
        ByteArrayOutputStream inBuff = null;
        byte[] buf = new byte[256];
        
        try
        {
	        oUrl = new URL(url);
	        urlConn = (HttpURLConnection)oUrl.openConnection();
	        urlConn.setDoOutput(true);
	        urlConn.setConnectTimeout(1000 * 60 * 15);
	        urlConn.setRequestProperty("Accept", "*/*");
	        urlConn.setRequestProperty("Referer", url);
	        urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.00; Windows 98)");
	        urlConn.setRequestProperty("Client", "XiaobaoUI-For-Android");
	        urlConn.setRequestProperty("Pragma", "no-cache");
	        urlConn.setRequestProperty("Cache-Control", "no-cache");
	        
	        String cookie = Configuration.getInstance().getString("user-auth-token", null);
			if (cookie == null || "".equals(cookie)) cookie = "a=11111";
			else cookie = Configuration.getInstance().getProperty("user_auth_token_name") + "=" + cookie;
			urlConn.setRequestProperty("Cookie", cookie);
			if (!haveFiles) urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			else urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
	        urlConn.setRequestProperty("Content-Length", String.valueOf(bytes));
	        urlConn.setRequestMethod("POST");
	        baos = urlConn.getOutputStream();
			
			while (itr.hasNext())
			{
				String name = (String)itr.next();
				Object value = this.data.get(name);
				
				if (haveFiles)
				{
					baos.write('-');
					baos.write('-');
					baos.write(boundary.getBytes());
					baos.write('\r');
					baos.write('\n');
					baos.write(("Content-Disposition: form-data; name=\"" + name + "\"").getBytes());
					
					sentBytes += 4 + boundary.getBytes().length + ("Content-Disposition: form-data; name=\"" + name + "\"").getBytes().length;
					
					if (value instanceof Schema.Uri)
					{
						Uri uri = (Schema.Uri)value;
						baos.write(("; filename=\"" + uri.getPath() + "\"").getBytes());
						baos.write(("\r\nContent-Type: application/octet-stream").getBytes());
						
						sentBytes += ("; filename=\"" + uri.getPath() + "\"").getBytes().length + ("\r\nContent-Type: application/octet-stream").getBytes().length;
					}
					baos.write('\r');
					baos.write('\n');
					baos.write('\r');
					baos.write('\n');
					
					sentBytes += 4;
					
					if (value instanceof Schema.Uri)
					{
						InputStream reader = ((Schema.Uri)value).getReader();
						byte[] buffer = new byte[512];
						ch = 0;
						while ((ch = reader.read(buffer)) > -1)
						{
							baos.write(buffer, 0, ch);
							sentBytes += ch;
							
							baos.flush();
							this.reportProgress((float)sentBytes / bytes);
						}
						reader.close();
					}
					else
					{
						baos.write(String.valueOf((null == value ? "" : value)).getBytes(Configuration.ENCODING));
						sentBytes += String.valueOf((null == value ? "" : value)).getBytes(Configuration.ENCODING).length;
					}
					baos.write('\r');
					baos.write('\n');
					sentBytes += 2;
					
					baos.flush();
					this.reportProgress((float)sentBytes / bytes);
				}
				else
				{
					baos.write((name + "=" + java.net.URLEncoder.encode(String.valueOf(value), "UTF-8") + "&").getBytes());
					sentBytes += (name + "=" + java.net.URLEncoder.encode(String.valueOf(value), "UTF-8") + "&").getBytes().length;
				}
			}
			
			if (haveFiles)
			{
				baos.write('-');
				baos.write('-');
				baos.write(boundary.getBytes());
				baos.write('-');
				baos.write('-');
				baos.write('\n');
				
				sentBytes += 5 + boundary.getBytes().length;
			}
			baos.flush();
			baos.close();
			
			this.reportProgress(1.0f);
			
			bis = urlConn.getInputStream();
			inBuff = new ByteArrayOutputStream(512);
	        while ((ch = bis.read(buf)) != -1)
	        {
	            inBuff.write(buf, 0, ch);
	        }
	        
			// String jsonText = new String(Network.post(url, baos.toByteArray(), headers, 1000 * 60 * 5), Configuration.ENCODING);
	        String jsonText = inBuff.toString(Configuration.ENCODING);
			JSONObject json = new JSONObject(jsonText);
			if (json.getBoolean("error")) throw new Exception(json.getString("message"));
			return json;
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw ex;
        }
        finally
        {
        	try
        	{
        		baos.close();
        		inBuff.close();
        		bis.close();
        	}
        	catch(Exception e) { }
        }
	}
}
