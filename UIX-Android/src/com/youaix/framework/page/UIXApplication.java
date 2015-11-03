package com.youaix.framework.page;

import java.util.*;

import com.youaix.framework.mission.Mission;

import android.app.Application;
import android.app.Notification;
import android.app.Service;
import android.content.res.Resources;
import android.os.Vibrator;

public class UIXApplication extends Application
{
	/*
	// TODO: missions可能会变成null值
	private HashMap<String, Mission> missions = null;
	
	public void onCreate()
	{
		super.onCreate();
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
	*/
}
