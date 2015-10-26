package com.youaix.framework.common;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorMonitor implements SensorEventListener
{
    // 这个控制精度，越小表示反应越灵敏
    public int linmin = 500;

    private Context mContext;
    
    private ShakeListener shakeListener = null;
    public SensorMonitor(Context context, ShakeListener shakeListener)
    {
    	this.mContext = context;
    	this.shakeListener = shakeListener;
    	this.sm = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
    }

    /*********
     * Sensor 说明 Sensor.TYPE_ACCELEROMETER 加速度感应检测 Sensor.TYPE_MAGNETIC_FIELD
     * 磁场感应检测 Sensor.TYPE_ORIENTATION 方位感应检测 Sensor.TYPE_GYROSCOPE 回转仪感应检测
     * Sensor.TYPE_LIGHT 亮度感应检测 Sensor.TYPE_PRESSURE 压力感应检测
     * Sensor.TYPE_TEMPERATURE 温度感应检测 Sensor.TYPE_PROXIMITY 接近感应检测
     **********/

    /*
     * SENSOR_DELAY_FASTEST
     * 最低延迟，一般不是特别敏感的处理不推荐使用，该种模式可能造成手机电力大量消耗，由于传递的为原始数据，算法不处理好将会影响游戏逻辑和UI的性能
     * ，所以Android开发网不推荐大家使用。 SENSOR_DELAY_GAME 游戏延迟，一般绝大多数的实时性较高的游戏都使用该级别 int
     * SENSOR_DELAY_NORMAL 标准延迟，对于一般的益智类或EASY级别的游戏可以使用，但过低的采样率可能对一些赛车类游戏有跳帧现象。
     * int SENSOR_DELAY_UI 用户界面延迟，一般对于屏幕方向自动旋转使用，相对节省电能和逻辑处理，一般游戏开发中我们不使用。
     */
    private SensorManager sm;

    public void register()
    {
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    public void unRegister()
    {
        sm.unregisterListener(this);
        mLastX = -1.0f;
        mLastY = -1.0f;
        mLastZ = -1.0f;
        mLastTime = 0;
        mShakeCount = 0;
        mLastShake = 0;
        mLastForce = 0;
    }
    
    public void onSensorChanged(SensorEvent event)
    {
        acceB(event);
    }
    
    private static final int FORCE_THRESHOLD = 2500;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 1;

    private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
    private long mLastTime;
    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;

    private void acceB(SensorEvent event)
    {
        onSensorChanged(event.sensor.getType(), event.values);
    }
    
    public void onSensorChanged(int sensor, float[] values) 
    {
    	long now = System.currentTimeMillis();
    	if ((now - mLastForce) > SHAKE_TIMEOUT)
    	{
    		mShakeCount = 0;
    	}

    	if ((now - mLastTime) > TIME_THRESHOLD)
    	{
    		long diff = now - mLastTime;
    		float speed = Math.abs(values[SensorManager.DATA_X] + values[SensorManager.DATA_Y] + values[SensorManager.DATA_Z] - mLastX - mLastY - mLastZ) / diff * 10000;
    		if (speed > FORCE_THRESHOLD)
    		{
    			if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION))
    			{
    				mLastShake = now;
    				mShakeCount = 0;
    				if (shakeListener != null)
    				{
    					shakeListener.on();
    				}
    				// Log.i(Float.toString(speed), Long.toString(diff) + "sc");
    			}
    			mLastForce = now;
    		}
    		mLastTime = now;
    		mLastX = values[SensorManager.DATA_X];
    		mLastY = values[SensorManager.DATA_Y];
    		mLastZ = values[SensorManager.DATA_Z];
    	}
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // do nothing here...
    }
}