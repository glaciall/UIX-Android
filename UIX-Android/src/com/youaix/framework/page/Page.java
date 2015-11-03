package com.youaix.framework.page;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.youaix.framework.common.Configuration;
import com.youaix.framework.common.Global;
import com.youaix.framework.common.ImageCache;
import com.youaix.framework.common.Location;
import com.youaix.framework.common.Schema;
import com.youaix.framework.common.SensorMonitor;
import com.youaix.framework.common.ShakeListener;
import com.youaix.framework.common.Validation.Rule;
import com.youaix.framework.event.OnDatePickedEvent;
import com.youaix.framework.event.OnTimePickedEvent;
import com.youaix.framework.mission.Callbackable;
import com.youaix.framework.mission.FormSubmitter;
import com.youaix.framework.mission.ImageLoader;
import com.youaix.framework.ui.Div;
import com.youaix.framework.ui.Element;
import com.youaix.framework.ui.Image;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

public abstract class Page extends Activity implements Callbackable
{
	// 常量定义
	public static final int CHOOSE_PHOTO = 0xf001;
	public static final int TAKE_PHOTO = 0xf001;
	public static final int CHOOSE_VIDEO = 0xf004;
	public static final int CHOOSE_PHOTO_FROM_ALBUM = 0xf002;
	
	public static final int STATE_INIT = 0x00;
	public static final int STATE_CREATED = 0x01;
	public static final int STATE_RESUMED = 0x02;
	public static final int STATE_PAUSED = 0x03;
	public static final int STATE_DESTROYED = 0x04;
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////

	ContextMenu contextMenu = null;
	private Bundle parameters = null;
	private final Page instance = this;
	private Handler handler = null;
	
	private int pageState = STATE_INIT;
	private int requestCode = 0;
	private String requestPage = null;
	private String formAction = null;
	protected boolean loadingImage = false;
	
	private HashMap<Integer, Image> images = null;
	private HashMap<String, Object> missions = null;
	private ArrayList<Integer> pictures = null;
	
	private boolean isResumed = false;

	protected final void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		try
		{
			// 框架初始化
			PageManager.getInstance().setCurrent(this);
			Resolution.init(this);
			Configuration.getInstance();
			Page.Transition.init();

			// 页面初始化，回调及参数等
			this.init();
			
			this.createPage();
			this.formAction = this.createForm();
			this.pageState = STATE_CREATED;
		}
		catch (Exception ex)
		{
			this.onError(ex);
		}
	}
	
	private Div contentPage = null;
	protected void createPage() throws Exception
	{
		int bodyHeight = 0;
		contentPage = new Div().setBackgroundColor(0xffffffff);
		contentPage.setWidth(Resolution.getScreenWidthDip()).setHeight(bodyHeight = Resolution.getScreenHeightDip());

		Element titleBar = this.createTitle();
		Element navigator = this.createNavigator();

		if (null != titleBar)
		{
			bodyHeight -= titleBar.getHeightDip();
			contentPage.append(titleBar);
		}

		if (null != navigator) bodyHeight -= navigator.getHeightDip();

		Element ele = this.createBody();
		contentPage.append(ele.setHeight(bodyHeight)).append(navigator).append(new Div().setHeightPercent(1.0f).setTop(0).setLeft(0).vanish().setId("mask-layer"));
		
		this.setContentView(contentPage.getContentView(), contentPage.getLayout());
	}
	
	public Element getContentPage()
	{
		return this.contentPage;
	}
	
	public void find(Class<? extends Element> targetClass, Executable executable)
	{
		if (null == this.contentPage) return;
		this.find(this.contentPage, targetClass, executable);
	}
	
	private void find(Element node, Class<? extends Element> targetClass, Executable executable)
	{
		LinkedList<Element> childs = node.getChildren();
		for (int i = 0; i < childs.size(); i++)
		{
			find(childs.get(i), targetClass, executable);
		}
		if (targetClass.isInstance(node))
		{
			executable.modify(node);
		}
	}

	protected abstract Element createBody() throws Exception;

	protected abstract Element createTitle() throws Exception;

	protected abstract Element createNavigator() throws Exception;

	protected void debug()
	{
		// ...
	}

	protected void log(String message)
	{
		android.util.Log.e(this.getClass().getName(), message);
	}

	protected void log(Exception ex)
	{
		log(ex.getMessage());
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		// getMenuInflater().inflate(contextMenu, menu);
		return true;
	}

	protected void onPause()
	{
		super.onPause();
		this.pageState = STATE_PAUSED;
		ImageCache.getInstance().pageStopped(this);
		pause();
	}

	private boolean isChecked = false;
	protected void onResume()
	{
		super.onResume();
		try
		{
			// 检查推送消息
			if (this.getClass().getName().equals(Configuration.getInstance().getProperty("index_page")))
			{
				if (this.checkNotification()) return;
			}
			
			this.pageState = STATE_RESUMED;
			this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
			PageManager.getInstance().setCurrent(this);
			ImageCache.getInstance().pageResumed(this);
			if (this.isResumed || this.isChecked)
			{
				this.resume();
			}
			else
			{
				this.isResumed = true;
				this.onLoad();
				this.debug();
			}
		}
		catch(Exception ex)
		{
			this.onError(ex);
		}
	}
	
	private final boolean checkNotification()
	{
		String extra = null;
		try
		{
			extra = Configuration.getInstance().getString("_push_message_extra_", null);
			if (null == extra) return false;
			Configuration.getInstance().put("_push_message_extra_", null);
			this.onNotification(extra);
			return true;
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	protected void onNotification(String message)
	{
		// ...
	}
	
	protected void onStop()
	{
		stop();
		this.unregisterShakeListener();
		if (locationManager != null)
		{
			locationManager.removeUpdates(locationListener);
		}
		super.onStop();
	}
	
	private SensorMonitor sensorMonitor = null;
	public final void registerShakeListener(ShakeListener shakeListener)
	{
		sensorMonitor = new SensorMonitor(this, shakeListener);
		sensorMonitor.register();
	}
	
	private final void unregisterShakeListener()
	{
		if (sensorMonitor != null) sensorMonitor.unRegister();
	}

	protected void onDestroy()
	{
		super.onDestroy();
		PageManager.getInstance().dispose(this);
		this.pageState = STATE_DESTROYED;
	}
	
	public int getState()
	{
		return this.pageState;
	}
	
	// 当准备要退出时
	protected void onReadyExit()
	{
		tip("再按一次后退键返回到桌面");
	}
	
	// 当确定要退出时
	protected void onExit()
	{
		// do nothing
	}
	
	public final String getRequestPage()
	{
		return this.requestPage;
	}
	
	// 当按下后退键时，false表示系统处理后退，true表示程序自己处理后退事件
	protected boolean onBack()
	{
		// do nothing
		return false;
	}
	
	private boolean isNotSupportBackKey()
	{
		if ("3GW100".equals(Build.MODEL)) return true;
		if ("3GW101".equals(Build.MODEL)) return true;
		if ("3GC101".equals(Build.MODEL)) return true;
		return false;
	}
	
	private void backToDesktop()
	{
		Intent homeIntent = null;
		if (isNotSupportBackKey())
		{
			homeIntent = new Intent(Intent.ACTION_MAIN, null);
			homeIntent.putExtra("GOHOME", "GOHOME");
			homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			homeIntent.setClassName("com.android.launcher", "com.android.launcher.HomeScreen");
		}
		else
		{
			homeIntent = new Intent(Intent.ACTION_MAIN);
			homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			homeIntent.addCategory(Intent.CATEGORY_HOME);
		}
		startActivity(homeIntent);
	}
	
    private long tryToExitTime = 0L;
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (this.isTaskRoot())
            {
                if (System.currentTimeMillis() - tryToExitTime < 2000)
                {
                    this.onExit();
                    
                    return super.onKeyDown(keyCode, event);
                    // this.backToDesktop();
                    // return true;
                }
                else
                {
                    this.onReadyExit();
                    tryToExitTime = System.currentTimeMillis();
                    return true;
                }
            }
            else
            {
                if (this.onBack()) return true;
                else return super.onKeyDown(keyCode, event);
            }
        }
		return super.onKeyDown(keyCode, event);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// 日期选择器
	public final void pickDate(int year, int month, int date, final OnDatePickedEvent evt)
	{
		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
		{
			public void onDateSet(DatePicker picker, int year, int month, int date)
			{
				evt.on(year, month, date);
			}
		}, year, month, date).show();
	}
	
	// 时间选择器
	public final void pickTime(int hour, int minute, final OnTimePickedEvent evt)
	{
		new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
		{
			public void onTimeSet(TimePicker picker, int hour, int minute)
			{
				evt.on(hour, minute);
			}
		}, hour, minute, true).show();
	}
	
	// alert提示对话框
	public final void alert(String title, String text)
	{
		new AlertDialog.Builder(this)
		.setIcon(null)
		.setTitle(title)
		.setMessage(text)
		.setPositiveButton("确定", null)
		.show();
	}
	
	public final void alert(String text)
	{
		this.alert("温馨提示", text);
	}
	
	// confirm选择对话框
	public final void confirm(String title, String text, final ConfirmHandler confirmHandler)
	{
		new AlertDialog.Builder(this)
		
		.setIcon(null)
		.setTitle(title)
		.setMessage(text)
		.setPositiveButton
		(
			"确定",
			new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					confirmHandler.confirm();
				}
			}
		)
		.setNegativeButton
		(
			"取消",
			new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					confirmHandler.cancel();
				}
			}
		)
		.show();
	}
	
	// 提示框
	public final void tip(String message)
	{
		Toast.makeText(this, message, 1).show();
	}

	public final void choosePictures() {
		CharSequence[] items = { "从相册里选一张", "拍摄一张新的照片" };
		new AlertDialog.Builder(this).setTitle("选择图片来源")
				.setItems(items, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							
							
							Intent intent = new Intent(
									Intent.ACTION_GET_CONTENT);
							intent.addCategory(Intent.CATEGORY_OPENABLE);
							intent.setType("image/*");
							startActivityForResult(
									Intent.createChooser(intent, "选择图片"),
									CHOOSE_PHOTO);
							
							/*
							PageManager.getInstance().request(AlbumSelector.class, CHOOSE_PHOTO_FROM_ALBUM, parameter("max-pictures", 4));
							*/
						}
						else
						{
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							startActivityForResult(intent, CHOOSE_PHOTO);
						}
					}
				}).create().show();
	}

	
	public final void chooseVideo()
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("video/*");
		Intent wrapperIntent = Intent.createChooser(intent, "选择一个视频");
		startActivityForResult(wrapperIntent, Page.CHOOSE_VIDEO);
	}
	
	// 播放视频
	public final void playVideo(String videoUrl)
	{
		// PageManager.getInstance().redirect(VideoPlayer.class, parameter("video-url", videoUrl), false);
	}

	// 输入法
	public final void showSoftInput()
	{
		this.getWindow().setSoftInputMode(0x04 | 0x20);
	}

	protected final void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		PageManager.getInstance().setCurrent(this);
		if (resultCode == RESULT_OK)
		{
			if (requestCode == CHOOSE_PHOTO)
			{
				Bundle bundle = new Bundle();
				String uri = data.getDataString();
				if (uri == null)
					uri = MediaStore.Images.Media.insertImage(this.getContentResolver(), (Bitmap)data.getExtras().get("data"), null, null);
				bundle.putString("selected-image", uri);
				this.onResult(requestCode, bundle);
			}
			else if (requestCode == CHOOSE_VIDEO)
			{
				Bundle bundle = new Bundle();
				bundle.putString("selected-video", data.getDataString());
				this.onResult(requestCode, bundle);
			}
			else this.onResult(requestCode, data.getExtras());
		}
	}
	
	public void setResult(Bundle results)
	{
		try
		{
			Intent intent = new Intent(this, Class.forName(this.requestPage));
			intent.putExtras(results);
			super.setResult(RESULT_OK, intent);
			this.finish();
		}
		catch(Exception ex)
		{
			this.onError(ex);
		}
	}
	
	protected void onResult(int requestCode, Bundle bundle)
	{
		// ...
	}

	protected void onChoose(Uri uri)
	{
		// ...
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /// 页面事件
	protected void onLoad() throws Exception
	{
		// 当页面加载完毕时调用
	}

	// 当页面出错时调用
	protected void onError(Exception ex)
	{
		ex.printStackTrace();
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /// 页面相关
	// setTitle(String)是自带的
	// 构造参数
	public static final Bundle parameter(Object... args)
	{
		if (args.length % 2 != 0) return null;
		Bundle bundle = new Bundle();
		for (int i = 0, l = args.length; i < l; i += 2)
		{
			String key = (String) args[i];
			Object val = args[i + 1];

			String type = val.getClass().getName();
			if (type.equals("java.lang.Integer")) bundle.putInt(key, ((Integer) val).intValue());
			if (type.equals("java.lang.Float")) bundle.putFloat(key, ((Float) val).floatValue());
			if (type.equals("java.lang.String")) bundle.putString(key, val.toString());
		}
		return bundle;
	}

	// 把id=32&name=2222的URL形式改为和谐的HASHMAP形式
	public static final Bundle parameter(String url)
	{
		
		Bundle bundle = new Bundle();
		String[] keyValue = url.split("&");

		for (String kv : keyValue)
		{
			int index = kv.indexOf("=");
			if (index == -1) continue;
			bundle.putString(kv.substring(0, index), kv.substring(index + 1));
		}

		return bundle;
	}

	// 获取参数类
	public final int getInt(String name)
	{
		return parameters.getInt(name);
	}

	public final float getFloat(String name)
	{
		return parameters.getFloat(name);
	}

	public final String getString(String name)
	{
		return parameters.getString(name);
	}
	
	private HashMap<String, ArrayList<Rule>> validations = new HashMap<String, ArrayList<Rule>>();
	protected void createValidations(Object... args) throws Exception
	{
		int len = args.length;
		if (len % 2 == 1) throw new Exception("需要对成的参数");
		for (int i = 0; i < len; i += 2)
		{
			validations.put((String)args[i], (ArrayList<Rule>)args[i + 1]);
		}
	}
	
	protected String createForm() throws Exception
	{
		return null;
	}
	
	public final void submit()
	{
		boolean haveFiles = false;
		
		try
		{
			// 收集参数
			HashMap<String, Object> formValues = PageManager.getInstance().getFormData();
			
			// 参数验证
			Iterator itr = this.validations.keySet().iterator();
			while (itr.hasNext())
			{
				String id = (String)itr.next();
				ArrayList<Rule> rules = this.validations.get(id);
				Element el = Element.getById(id);
				if (null == el) throw new Exception("cannot bind validation rules to undefined element which id is " + id);
				
				Object value = formValues.get(id);
				for (int i = 0; i < rules.size(); i++)
				{
					Rule rule = rules.get(i);
					if (!rule.check(value instanceof Schema.Uri ? value.toString() : (String)value))
					{
						this.onValidationError(el, rule.getMessage());
						return;
					}
				}
			}
			
			if (!this.onSubmit()) return;
			
			itr = formValues.values().iterator();
			while (itr.hasNext())
			{
				Object val = itr.next();
				if (val instanceof Schema.Uri)
				{
					haveFiles = true;
					Schema.Uri f = (Schema.Uri)val;
					break;
				}
			}
			
			// 提交内容，有没有文件？有的话，得准备文件提交
			new FormSubmitter("form-submit", this.formAction, haveFiles, formValues).go();
		}
		catch(Exception ex)
		{
			this.onError(ex);
		}
	}
	
	// 表单元素校验通过后，提交前调用
	protected boolean onSubmit() throws Exception
	{
		return true;
	}
	
	// 当表单元素格式校验不通过时
	protected void onValidationError(Element el, String msg)
	{
		this.tip(msg);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /// 任务回调
	private final void init()
	{
		this.getWindow().setBackgroundDrawable(null);
		this.parameters = this.getIntent().getExtras();
		if (null == this.parameters) this.parameters = new Bundle();
		this.requestCode = this.getIntent().getIntExtra("__request_code__", 0);
		this.requestPage = this.getIntent().getStringExtra("__request_page__");
		this.missions = new HashMap<String, Object>();
		this.handler = new Handler(Looper.getMainLooper())
		{
			public void handleMessage(Message msg)
			{
				Bundle bundle = msg.getData();
				boolean error = bundle.getBoolean("error");
				String mission = bundle.getString("mission");

				try
				{
					if (error) instance.error(mission, (Exception) missions.remove(mission));
					else instance.complete(mission, missions.remove(mission));
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					android.util.Log.e("Page.handleMessage", ex.toString());
				}
			}
		};
	} 
	
	public void echo(String mission, Object data)
	{
		synchronized (this.missions)
		{
			this.missions.put(mission, data);
		}

		this._postMessage(mission, data);
	}
	
	public void _postMessage(String mission, Object data)
	{
		Bundle bundle = new Bundle();
		bundle.putBoolean("error", (data instanceof Exception));
		bundle.putString("mission", mission);
		Message msg = Message.obtain();
		msg.setData(bundle);
		this.handler.sendMessage(msg);
	}

	public void complete(String mission, Object data) throws Exception
	{
		if (mission.startsWith("image-loader:"))
		{
			this.loadingImage = false;
			Image image = null;
			synchronized (this.images)
			{
				image = this.images.remove(Integer.parseInt(mission.substring(13)));
			}
			if (image != null) image.setBitmap((Bitmap) data);
			this.loadNextImage();
			return;
		}
	}

	public void error(String mission, Exception ex) throws Exception
	{
		if (mission.startsWith("image-loader:"))
		{
			ex.printStackTrace();
			this.loadingImage = false;
			this.loadNextImage();
			return;
		}
	}

	// 加载图片
	public final void loadImage(Image image, String uri)
	{
		if (null == this.images)
		{
			this.images = new HashMap<Integer, Image>(20);
			this.pictures = new ArrayList<Integer>(20);
		}
		synchronized (this.images)
		{
			this.pictures.add(image.hashCode());
			this.images.put(image.hashCode(), image);
		}
		if (this.loadingImage) return;
		this.loadNextImage();
		this.loadingImage = true;
	}

	// 加载下一个图片
	private final void loadNextImage()
	{
		Image image = null;
		synchronized (this.images)
		{
			// Iterator itr = this.images.keySet().iterator();
			// if (itr.hasNext()) image = this.images.get(itr.next());
			if (this.pictures.size() == 0) return;
			int iCode = this.pictures.remove(0);
			image = this.images.get(iCode);
		}
		if (null == image) return;
		new ImageLoader("image-loader:" + image.hashCode(), image.getSrc()).go();
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// 遍历符合某种规则的ID的元素，进行某种操作
	public final void traverse(String regexp, Executable executable)
	{
		PageManager.getInstance().traverse(regexp, executable);
	}
	
	protected void redirect(final Class<? extends Page> target, final boolean finish, HashMap<String, Object> params) throws Exception
	{
		PageManager.getInstance().redirect(target, finish);
	}

	protected void redirect(final Class<? extends Page> target, final boolean finish, HashMap<String, Object> params, int timeout) throws Exception
	{
		PageManager.getInstance().redirect(target, finish, timeout);
	}

	public void resume()
	{

	}

	public void start()
	{

	}

	public void pause()
	{

	}

	public void menu()
	{

	}

	public void stop()
	{

	}
	
	@Override
	public void finish()
	{
		super.finish();
		overrideTransition(exitTransition());
	}
	
	public final void overrideTransition(int transition)
	{
		// int transition = type == TRANSITION_EXIT ? exitTransition() : enterTransition();
		if (transition == Transition.STYLE_NONE)
			this.overridePendingTransition(Transition.ANIM_NONE, Transition.ANIM_NONE);
		else if (transition == Transition.STYLE_SLIDE_RIGHT_TO_LEFT)
			this.overridePendingTransition(Transition.SLIDE_IN_RIGHT, Transition.SLIDE_OUT_LEFT);
		else if (transition == Transition.STYLE_SLIDE_LEFT_TO_RIGHT)
			this.overridePendingTransition(Transition.SLIDE_IN_LEFT, Transition.SLIDE_OUT_RIGHT);
		else if (transition == Transition.STYLE_ZOOM_IN)
			this.overridePendingTransition(0, Transition.ZOOM_IN);
		else if (transition == Transition.STYLE_ZOOM_OUT)
			this.overridePendingTransition(0, Transition.ZOOM_OUT);
		else if (transition == Transition.STYLE_FADE)
			this.overridePendingTransition(Transition.FADE_IN, Transition.FADE_OUT);
		else if (transition == Transition.STYLE_SLIDE_TOP_TO_BOTTOM)
			this.overridePendingTransition(Transition.SLIDE_IN_TOP, Transition.SLIDE_OUT_BOTTOM);
		else if (transition == Transition.STYLE_SLIDE_BOTTOM_TO_TOP)
			this.overridePendingTransition(Transition.SLIDE_IN_BOTTOM, Transition.SLIDE_OUT_TOP);
	}
	
	public int exitTransition()
	{
		return Page.Transition.STYLE_SLIDE_LEFT_TO_RIGHT;
	}
	
	public int enterTransition()
	{
		return Page.Transition.STYLE_SLIDE_RIGHT_TO_LEFT;
	}
	
	public static final class Transition
	{
		public static final int STYLE_NONE = 0x00;
		public static final int STYLE_SLIDE_RIGHT_TO_LEFT = 0x01;
		public static final int STYLE_SLIDE_LEFT_TO_RIGHT = 0x02;
		public static final int STYLE_ZOOM_IN = 0x03;
		public static final int STYLE_ZOOM_OUT = 0x04;
		public static final int STYLE_FADE = 0x05;
		public static final int STYLE_SLIDE_BOTTOM_TO_TOP = 0x06;
		public static final int STYLE_SLIDE_TOP_TO_BOTTOM = 0x07;
		
		public static final int FADE_IN = android.R.anim.fade_in;
		public static final int FADE_OUT = android.R.anim.fade_out;
		public static final int SLIDE_IN_LEFT = android.R.anim.slide_in_left;
		public static final int SLIDE_OUT_RIGHT = android.R.anim.slide_out_right;
		public static int ZOOM_IN = 0;
		public static int ZOOM_OUT = 0;
		public static int SLIDE_OUT_LEFT = 0x00;
		public static int SLIDE_IN_RIGHT = 0x00;
		public static int SLIDE_IN_TOP = 0x00;
		public static int SLIDE_OUT_BOTTOM = 0x00;
		public static int SLIDE_IN_BOTTOM = 0x00;
		public static int SLIDE_OUT_TOP = 0x00;
		public static final int ANIM_NONE = 0x00;
		
		public static void init()
		{
			if (SLIDE_OUT_LEFT != 0x00) return;
			try
			{
				// Class animator = null;
				Class cls = Class.forName(Global.getProperty("android_r_class") + "$animator");
				SLIDE_OUT_LEFT = cls.getField("slide_out_left").getInt(null);
				SLIDE_IN_RIGHT = cls.getField("slide_in_right").getInt(null);
				SLIDE_IN_TOP = cls.getField("slide_in_top").getInt(null);
				SLIDE_OUT_BOTTOM = cls.getField("slide_out_bottom").getInt(null);
				SLIDE_IN_BOTTOM = cls.getField("slide_in_bottom").getInt(null);
				SLIDE_OUT_TOP = cls.getField("slide_out_top").getInt(null);
				ZOOM_IN = cls.getField("zoom_in").getInt(null);
				ZOOM_OUT = cls.getField("zoom_out").getInt(null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	// 定位相关
	android.location.LocationManager locationManager = null;
	android.location.LocationListener locationListener = null;
	public final void registerLocationListener(final LocationListener listener)
	{
		if (null != locationManager) return;
		locationManager = (android.location.LocationManager) getSystemService(LOCATION_SERVICE);
		boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		String provider = isGpsEnabled ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;
		locationManager.requestLocationUpdates(provider, 10000, 1000, locationListener = new android.location.LocationListener()
		{
			public void onLocationChanged(android.location.Location location)
			{
				Location loc = new Location();
				loc.setLongitude(location.getLongitude());
				loc.setLatitude(location.getLatitude());
				loc.setAltitude(location.getAltitude());
				listener.onLocationChanged(loc);
			}
			public void onProviderDisabled(String arg0) { }
			public void onProviderEnabled(String arg0) { }
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) { }
		});
	}
	
	public static interface LocationListener
	{
		public void onLocationChanged(Location location);
	}
	
	/////////////////////////////////////////////////////////////////////////
	//// 增强型Mission相关
	/*
	public final void stopMission(String missionName)
	{
		((UIXApplication)this.getApplication()).stopMission(missionName);
	}
	
	public final void pauseMission(String missionName)
	{
		((UIXApplication)this.getApplication()).pauseMission(missionName);
	}
	
	public final void resumeMission(String missionName)
	{
		((UIXApplication)this.getApplication()).resumeMission(missionName);
	}
	
	public final Object getMissionData(String missionName, String field)
	{
		return ((UIXApplication)this.getApplication()).getMissionData(missionName, field);
	}
	*/
}
