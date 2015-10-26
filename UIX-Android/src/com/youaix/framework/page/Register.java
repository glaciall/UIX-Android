package com.youaix.framework.page;

import org.json.JSONObject;

public class Register extends Page
{
	protected Element createBody() throws Exception
	{
		return UIFactory.build(Schema.parse("assets://common/register.body.html"));
	}

	protected Element createTitle() throws Exception
	{
		return null;
	}

	protected Element createNavigator() throws Exception
	{
		return null;
	}
	
	private boolean created = false;
	private boolean showLoading = false;
	private boolean obtainedCaptcha = false;
	
	public final void showLoading()
	{
		new Loading().JLoad();
		/*
		Div layer = (Div)Element.getById("mask-layer");
		layer.setBackgroundColor(0x80000000).setWidth(1.0f).setHeight(1.0f);
		layer.setAlign(Attribute.ALIGN_CENTER, Attribute.ALIGN_MIDDLE);
		if (!this.created)
		{
			layer.append(new Div().setWidth(120).setHeight(120).setBackgroundColor(0x66000000).setAlign(Attribute.ALIGN_CENTER, Attribute.ALIGN_MIDDLE).append(new Gif().setWidth(80).setHeight(80)));
			layer.onTouch(new TouchEvent()
			{
				public void down(Page page, Element element) { }
				public void up(Page page, Element element) { }
				public void move(Page page, Element element) { }
				public void cancel(Page page, Element element) { }
			});
		}
		this.created = true;
		this.showLoading = true;
		layer.show();
		*/
	}
	
	public final void hideLoading()
	{
		Element.getById("mask-layer").vanish();
		this.showLoading = false;
	}
	
	public void onRegisterSuccess(int uid, String username, String token)
	{
		Configuration conf = Configuration.getInstance();
		conf.put("user-id", uid);
		conf.put("user-name", username);
		conf.put("user-token", token);
		
		// 获取用户信息或是直接跳转走了
		// 发送用户的其它信息到服务器上，通知本次用户登陆过了
		// 还有什么事情要做呢？
		
		// 跳转回之前的页面去
		
	}
	
	public final void back()
	{
		try
		{
			// PageManager.getInstance().redirect(Class.forName(getString("referer")), true);
			this.finish();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void doRegister()
	{
		// 获取参数
		String username = ((Input)Element.getById("username")).getValue();
		String captcha = ((Input)Element.getById("authcode")).getValue();
		String password = ((Password)Element.getById("password")).getValue();
		
		if ("".equals(username))
		{
			hideLoading();
			tip("请填写您的手机号进行注册");
			return;
		}
		
		if (!this.obtainedCaptcha)
		{
			hideLoading();
			tip("您填写了手机号，但是没有获取手机验证码，请点击“获取手机验证码”按钮获取。");
			return;
		}
		
		if ("".equals(password))
		{
			hideLoading();
			tip("请设置您的账号登录密码");
			return;
		}
		
		if (password.length() < 6)
		{
			hideLoading();
			tip("账号登录密码最低不能少于6个字符，请重新输入。");
			((Password)Element.getById("password")).setText("");
			return;
		}
		
		this.showLoading();
		Configuration conf = Configuration.getInstance();
		String query = "?app=" + Configuration.getInstance().getProperty("app_name") + "&username=" + username + "&password=" + password + "&authcode=" + captcha;
		query += "&buserid=" + conf.getString("baidu-push-user-id", "") + "&bchannelid=" + conf.getString("baidu-push-channel-id", "");
		query += "&longitude=" + this.getLongitude() + "&latitude=" + this.getLatitude();
		new JsonRequestor("register-submit", "http://user.artxun.com/mobile/user/register.jsp" + query).go();
	}
	
	private int seconds = 0;
	public void complete(String mission, Object data) throws Exception
	{
		super.complete(mission, data);
		if ("time-countdown".equals(mission))
		{
			if (seconds > 1) new Timeout("time-countdown", 1000).go();
			this.seconds -= 1;
			String text = "重新发送(" + this.seconds + ")";
			if (this.seconds == 0) text = "重新获取验证码";
			((Span)Element.getById("btn_authcode_span")).setText(text);
			return;
		}
		if ("send_authcode".equals(mission))
		{
			((Span)Element.getById("btn_authcode_span")).setText("重新获取(60秒)");
			this.seconds = 60;
			new Timeout("time-countdown", 1000).go();
			tip("短信己发送，请查收手机短信");
			this.obtainedCaptcha = true;
			return;
		}
		if ("register-submit".equals(mission))
		{
			JSONObject json = ((JSONObject)data).getJSONObject("data");
			this.onRegisterSuccess(json.getInt("uid"), json.getString("username"), json.getString("token"));
			return;
		}
	}
	
	public void error(String mission, Exception ex) throws Exception
	{
		super.error(mission, ex);
		if ("send_authcode".equals(mission))
		{
			tip(ex.getMessage());
			this.hideLoading();
			return;
		}
		if ("register-submit".equals(mission))
		{
			tip(ex.getMessage());
			this.hideLoading();
			return;
		}
	}
	
	public void doSendAuthcode()
	{
		if (this.seconds > 0)
		{
			tip("请等待" + this.seconds + "秒后再次获取验证码");
			return;
		}

		String mobile = ((Input)Element.getById("username")).getValue();
		new JsonRequestor("send_authcode", "http://user.artxun.com/mobile/user/captcha.jsp?mobile=" + mobile).go();
	}
	
	protected boolean onBack()
	{
		if (this.showLoading)
		{
			this.hideLoading();
			return true;
		}
		return false;
	}

	private double longitude = 0.0d;
	private double latitude = 0.0d;
	
	protected double getLatitude()
	{
		return this.latitude;
	}
	
	protected double getLongitude()
	{
		return this.longitude;
	}
	
	public void updateLocation(double longitude, double latitude)
	{
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	protected void onLoad() throws Exception
	{
		this.registerLocationListener(new LocationListener()
		{
			public void onLocationChanged(Location location)
			{
				updateLocation(location.getLongitude(), location.getLatitude());
			}
		});
		Element.getById("btn_register").onClick(new ClickEvent()
		{
			public void on(Page page, Element element)
			{
				Register regPage = (Register)page;
				regPage.showLoading();
				regPage.doRegister();
			}
		});
		
		Element.getById("btn_authcode").onClick(new ClickEvent()
		{
			public void on(Page page, Element el)
			{
				Div btn = (Div)el;
				String mobile = ((Input)Element.getById("username")).getValue();
				if ("".equals(mobile))
				{
					tip("请填写手机号以获取验证码");
					return;
				}
				((Register)page).doSendAuthcode();
			}
		});
		
		//add by wy
		Element.getById("goback").onClick
		(
				new ClickEvent()
				{
					public void on(Page page, Element element)
					{
						page.finish();
					}
				}
			);
		
		Element.getById("btn_login").onClick(new ClickEvent()
		{
			public void on(Page page, Element element)
			{
				try
				{
					PageManager.getInstance().redirect(Class.forName(Global.getProperty("user_login_page")), null, true);
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}
