package com.youaix.framework.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import android.content.*;
import android.content.SharedPreferences.Editor;

public final class Configuration
{
    // 默认配置项
    public static final String ENCODING = "UTF-8";

    private static Configuration instance = null;

    private SharedPreferences preferences = null;
    private Properties properties = null;

    private Configuration()
    {
    	this.init();
        preferences = PageManager.getInstance().getCurrent().getSharedPreferences(this.getProperty("app_name"), 0x00);
    }
    
    public final String getProperty(String key)
    {
    	return this.properties.getProperty(key);
    }

    public final int getInt(String key, int defaultVal)
    {
        return preferences.getInt(key, defaultVal);
    }

    public final long getLong(String key, long defaultVal)
    {
        return preferences.getLong(key, defaultVal);
    }
    
    public final float getFloat(String key, float defaultVal)
    {
    	return preferences.getFloat(key, defaultVal);
    }

    public final String getString(String key, String defaultVal)
    {
        return preferences.getString(key, defaultVal);
    }

    public final void put(String key, long val)
    {
        Editor editor = preferences.edit();
        editor.putLong(key, val);
        editor.commit();
    }
    
    public final void put(String key, float val)
    {
    	Editor editor = preferences.edit();
    	editor.putFloat(key, val);
    	editor.commit();
    }

    public final void put(String key, String val)
    {
        Editor editor = preferences.edit();
        editor.putString(key, val);
        editor.commit();
    }

    public final void put(String key, int val)
    {
        Editor editor = preferences.edit();
        editor.putInt(key, val);
        editor.commit();
    }

    public static final Configuration getInstance()
    {
        if (null == instance) instance = new Configuration();
        return instance;
    }

	public void init()
	{
		if (null != this.properties) return;
		try
		{
			this.properties = new Properties();
			this.properties.load(Configuration.class.getResourceAsStream("/assets/app.properties"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

    /*
    public static void load(String fPath) throws Exception
    {
        // ...
    }

    public static void load() throws Exception
    {
        // load("default-config.db");

    }

    public static void set(String key, Serializable value) throws Exception
    {
        // ...
    }

    public static Object get(String key) throws Exception
    {
        return null;
    }
    */
}
