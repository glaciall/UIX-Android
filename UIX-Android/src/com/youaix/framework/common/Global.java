package com.bobaoo.xiaobao.common;

import com.baidu.android.pushservice.PushManager;
import com.bobaoo.xiaobao.bean.User;
import com.bobaoo.xiaobao.mission.JsonRequestor;
import com.bobaoo.xiaobao.page.PageManager;

public class Global
{
	private static Global instance = null;
	private User user = null;
	
	private Global()
	{
		// ...
	}
	
	public static String getProperty(String key)
	{
		return Configuration.getInstance().getProperty(key);
	}
	
	public static User getLoginUser() throws Exception
	{
		User user = getInstance().user;
		if (null == user)
		{
			user = (User)Class.forName(getProperty("custom_user_class")).newInstance();
			user.setUserId(Configuration.getInstance().getInt("user-id", 0));
			if (user.getUserId() == 0) return null;
			user.setUsername(Configuration.getInstance().getString("user-name", null));
			getInstance().user = user;
		}
		return getInstance().user;
	}
	
	public static void login(String token, User user)
	{
		Configuration.getInstance().put("user-auth-token", token);
		Global global = getInstance();
		global.user = user;
	}
	
	public static void logout()
	{
		Global global = getInstance();
		global.user = null;
		Configuration conf = Configuration.getInstance();
		conf.put("user-id", 0);
		conf.put("user-token", null);
		conf.put("user-auth-token", null);
		conf.put("baidu-push-user-bind", 0);
		
		// TODO: 添加退出的处理，比如通知系统的注销
	}
	
	public synchronized static Global getInstance()
	{
		if (null == instance) instance = new Global();
		return instance;
	}

	private static String extraMsg = null;
	public static void setPushMsg(String extra)
	{
		getInstance();
		synchronized(instance)
		{
			extraMsg = extra;
		}
	}
	
	public static String getPushMsg()
	{
		String msg = null;
		synchronized(instance)
		{
			msg = extraMsg;
			extraMsg = null;
		}
		return msg;
	}
}
