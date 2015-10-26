package com.youaix.framework.mission;

import org.json.JSONObject;

import com.youaix.framework.common.Configuration;
import com.youaix.framework.common.Device;
import com.youaix.framework.common.Encrypt;
import com.youaix.framework.common.FileSystem;
import com.youaix.framework.common.Network;
import com.youaix.framework.common.Schema;
import com.youaix.framework.issue.JsonException;
import com.youaix.framework.page.PageManager;

import java.io.File;
import java.io.FileInputStream;
import java.text.*;
import java.util.*;

public class JsonRequestor extends Mission
{
	private String url;
	private int timeout = 30000;
	private int cacheTime = 0;
	
	public JsonRequestor(String mission, String url)
	{
		super(mission, PageManager.getInstance().getCurrent());
		this.url = url;
	}
	
	public JsonRequestor(String mission, String url, int cacheTime)
	{
		super(mission, PageManager.getInstance().getCurrent());
		this.url = url;
		this.cacheTime = cacheTime;
	}
	
	public JsonRequestor(String mission, String url, int timeout, int cacheTime)
	{
		super(mission, PageManager.getInstance().getCurrent());
		this.url = url;
		this.timeout = timeout;
		this.cacheTime = cacheTime;
	}

	public Object handle() throws Exception
	{
		// cache://json-EEF977E7ABA236ED26D71B295988DD2C
		String jsonText = null;
		Schema.Uri resource = Schema.parse("cache://json-" + Encrypt.MD5(this.url));
		if (this.cacheTime > 0)
		{
			// 缓存文件是否存在
			File cacheFile = resource.getFile();
			if (cacheFile.exists() && cacheFile.lastModified() + this.cacheTime > System.currentTimeMillis())
				jsonText = new String(FileSystem.read(new FileInputStream(cacheFile)));
		}
		if (Device.getNetworkType() == Device.NETWORK_TYPE_NONE)
		{
			throw new Exception("无法连接到网络，请稍后再试。");
		}
		if (jsonText == null)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			String cookie = Configuration.getInstance().getString("user-auth-token", null);
			if (cookie == null || "".equals(cookie)) cookie = "a=11111";
			else cookie = Configuration.getInstance().getProperty("user_auth_token_name") + "=" + cookie;
			byte[] dBytes = Network.get(this.url, new Network.Header[] { new Network.Header("If-Modified-Since", sdf.format(new java.util.Date())), new Network.Header("Cookie", cookie) }, this.timeout);
			jsonText = new String(dBytes, Configuration.ENCODING);
			if (this.cacheTime > 0) FileSystem.write(resource.getWriter(), dBytes);
		}
		JSONObject json = new JSONObject(jsonText);
		if (json.getBoolean("error")) // throw new PageException(json.getString("message"));
			throw new JsonException(json.getString("message"));
		return json;
	}
}
