package com.youaix.framework.page;

import java.util.*;
import android.app.Notification;
import android.app.Service;
import android.content.res.Resources;
import android.os.Vibrator;

public class UIXApplication extends FrontiaApplication
{
	private HashMap<String, Mission> missions = null;
	
	public void onCreate()
	{
		super.onCreate();
		this.registerPushService();
		this.createMissionPool();
	}
	
	private final void createMissionPool()
	{
		this.missions = new HashMap<String, Mission>();
	}
	
	public final void newMission(String missionName, Mission mission)
	{
		this.missions.put(missionName, mission);
	}
	
	public final void missionCompleted(String missionName)
	{
		this.missions.remove(missionName);
	}
	
	public final Object getMissionData(String missionName, String field)
	{
		Mission mission = this.missions.get(missionName);
		if (mission != null) return mission.getData(field);
		else return null;
	}
	
	public final void stopMission(String missionName)
	{
		Mission mission = this.missions.get(missionName);
		if (mission != null) mission.stopMission();
	}
	
	public final void pauseMission(String missionName)
	{
		Mission mission = this.missions.get(missionName);
		if (mission != null) mission.pauseMission();
	}
	
	public final void resumeMission(String missionName)
	{
		Mission mission = this.missions.get(missionName);
		if (mission != null) mission.resumeMission();
	}
	
	private final void registerPushService()
	{
		if (!Utils.hasBind(getApplicationContext()))
		{
			// PushSettings.enableDebugMode(getApplicationContext(), true);
			PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(this, "api_key"));
			// PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "Gx83HPYkRmT69ohE4CDx6QOH");
			// 打开基于地理位置推送开关
			PushManager.enableLbs(getApplicationContext());
			// android.util.Log.e("push-service", "binded");
		}
		
		Resources resource = this.getResources();
        String pkgName = this.getPackageName();
		CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder
				(
					getApplicationContext(), resource.getIdentifier("notification_custom_builder", "layout", pkgName),
					resource.getIdentifier("notification_icon", "id", pkgName),
					resource.getIdentifier("notification_title", "id", pkgName),
					resource.getIdentifier("notification_text", "id", pkgName)
				);
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
        cBuilder.setLayoutDrawable(resource.getIdentifier("simple_notification_icon", "drawable", pkgName));
        PushManager.setNotificationBuilder(this, 1, cBuilder);
	}
}
