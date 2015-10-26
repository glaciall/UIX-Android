package com.youaix.framework.common;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.telephony.TelephonyManager;

public final class Device
{
    public static final int NETWORK_TYPE_NONE = 0;
    public static final int NETWORK_TYPE_WIFI = 1;
    public static final int NETWORK_TYPE_MOBILE = 2;

    // 获取当前网络类型
    public static int getNetworkType()
    {
    	ConnectivityManager mgr = (ConnectivityManager)PageManager.getInstance().getCurrent().getSystemService(Service.CONNECTIVITY_SERVICE);
    	NetworkInfo info = mgr.getActiveNetworkInfo();
    	if (null == info) return NETWORK_TYPE_NONE;
    	if (info.getType() == ConnectivityManager.TYPE_WIFI) return NETWORK_TYPE_WIFI;
        return NETWORK_TYPE_MOBILE;
    }

    // 获取硬件id
    public static String getDeviceID()
    {
    	TelephonyManager tm = (TelephonyManager) PageManager.getInstance().getCurrent().getSystemService(Service.TELEPHONY_SERVICE);
    	return tm.getDeviceId();
    }
    
    // 获取sim卡序列号
    public static String getImei()
    {
    	TelephonyManager tm = (TelephonyManager) PageManager.getInstance().getCurrent().getSystemService(Service.TELEPHONY_SERVICE);
    	return tm.getSimSerialNumber();
    }
    
    // 获取电话类型
    public static String getPhoneType()
    {
    	TelephonyManager tm = (TelephonyManager) PageManager.getInstance().getCurrent().getSystemService(Service.TELEPHONY_SERVICE);
    	int phone_type =  tm.getPhoneType();
        String r = "PHONE_TYPE_NONE";
    	if (phone_type == 1) r = "PHONE_TYPE_GSM";
    	else if (phone_type == 2) r = "PHONE_TYPE_CDMA";
    	
    	return r;
    }
    
    // 获取电话号码
    public static String getPhoneNumber()
    {
    	TelephonyManager tm = (TelephonyManager) PageManager.getInstance().getCurrent().getSystemService(Service.TELEPHONY_SERVICE);
    	return tm.getLine1Number();
    }
    
    // 播放音频

    // 持续震动多久
    public static void vibrate(int ms)
    {
    	Vibrator vib = (Vibrator)PageManager.getInstance().getCurrent().getSystemService(Service.VIBRATOR_SERVICE);
    	vib.vibrate(ms);
    }
    
    // 显示地图
    // Uri uri = Uri.parse("geo:38.899533,-77.036476");
    // Intent it = new Intent(Intent.Action_VIEW,uri);
    
    // 路径规则
    // Uri uri = Uri.parse("http://maps.google.com/maps?f=d&saddr=startLat%20startLng&daddr=endLat%20endLng&hl=en");
    // Intent it = new Intent(Intent.ACTION_VIEW,URI);
    
    // 调用浏览器
    public static void browse(String url)
    {
    	Uri uri = Uri.parse(url);
    	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    	PageManager.getInstance().getCurrent().startActivity(intent);
    }
    
    // 拨打电话
    public static void call(String number)
    {
    	Uri uri = Uri.parse("tel:" + number);
    	Intent intent = new Intent(Intent.ACTION_DIAL, uri);
    	PageManager.getInstance().getCurrent().startActivity(intent);
    }
    
    public static void call(Schema.Uri number)
    {
    	Uri uri = Uri.parse(number.toString());
    	Intent intent = new Intent(Intent.ACTION_DIAL, uri);
    	PageManager.getInstance().getCurrent().startActivity(intent);
    }
    
    // 发送短信到某某人
    public static void sendSms(String mobile, String text) throws Exception
    {
    	Uri uri = Uri.parse("smsto:" + mobile);
    	Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
    	intent.putExtra("sms_body", text);
    	PageManager.getInstance().getCurrent().startActivity(intent);
    }
}
