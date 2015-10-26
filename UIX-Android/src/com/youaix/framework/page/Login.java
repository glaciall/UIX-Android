package com.youaix.framework.page;

import org.json.JSONObject;

public class Login extends Page
{
	protected Element createBody() throws Exception
	{
		return UIFactory.build(Schema.parse("assets://common/login.body.html"));
	}

	protected Element createTitle() throws Exception
	{
		return null;
	}

	protected Element createNavigator() throws Exception
	{
		return null;
	}
	
	protected boolean onBack()
	{
		if(isComment){
			isComment = false;
			Element.getById("mask-layer").setDisplay("none").getParentNode()
  			.removeChild(Element.getById("alert"));
			return true;
		}
		return false;
	}
	
	private boolean isComment = false;
	private boolean created = false;
	private boolean showLoading = false;
	private double latitude = 0.0d;
	private double longitude = 0.0d;
	
	protected double getLatitude()
	{
		return this.latitude;
	}
	
	protected double getLongitude()
	{
		return this.longitude;
	}
	
	public void showLoading()
	{
		new Loading().JLoad();
//		Div layer = (Div)Element.getById("mask-layer");
//		layer.setBackgroundColor(0x80000000).setWidth(1.0f).setHeight(1.0f);
//		layer.setAlign(Attribute.ALIGN_CENTER, Attribute.ALIGN_MIDDLE);
//		if (!this.created)
//		{
//			layer.append(new Div().setWidth(120).setHeight(120).setBackgroundColor(0x66000000).setAlign(Attribute.ALIGN_CENTER, Attribute.ALIGN_MIDDLE).append(new Gif().setWidth(80).setHeight(80)));
//			layer.onTouch(new TouchEvent()
//			{
//				public void down(Page page, Element element){ }
//				public void up(Page page, Element element) { }
//				public void move(Page page, Element element) { }
//				public void cancel(Page page, Element element) { }
//			});
//		}
//		this.created = true;
//		this.showLoading = true;
//		layer.show();
		
	}
	
	public void hideLoading()
	{
		Element.getById("mask-layer").vanish();
		this.showLoading = false;
	}
	public void doLogin()
	{
		// 获取登陆信息
		// 提交到服务器，提交到哪去呢？
		String username = ((Input)Element.getById("username")).getValue();
		String password = ((Password)Element.getById("password")).getValue();
		if ("".equals(username))
		{
			hideLoading();
			tip("请输入您的用户名。");
			return;
		}
		if ("".equals(password))
		{
			hideLoading();
			tip("请输入您的登录密码。");
			return;
		}
		
		try
		{
			Configuration conf = Configuration.getInstance();
			String query = "?app=" + java.net.URLEncoder.encode(Configuration.getInstance().getProperty("app_name"),"UTF-8") + "&username=" + java.net.URLEncoder.encode(username, "UTF-8") + "&password=" + password;
			query += "&buserid=" + conf.getString("baidu-push-user-id", "") + "&bchannelid=" + conf.getString("baidu-push-channel-id", "");
			query += "&longitude=" + this.getLongitude() + "&latitude=" + this.getLatitude();
			new JsonRequestor("login-submit", "http://user.artxun.com/mobile/user/login.jsp" + query).go();
		}
		catch (Exception e) { }
		this.showLoading();
	}
	
	@Override
	public void complete(String mission, Object data) throws Exception 
	{
		super.complete(mission, data);
		if ("login-submit".equals(mission))
		{
			JSONObject json = ((JSONObject)data).getJSONObject("data");
			this.onLoginSuccess(json.getInt("uid"), json.getString("username"), json.getString("token"));
		}
		if ("oauth_login".equals(mission))
		{
			this.hideLoading();
			JSONObject json = ((JSONObject)data).getJSONObject("data");
			this.onLoginSuccess(json.getInt("uid"), json.getString("username"), json.getString("token"));
		}
	}
	
	public void onLoginSuccess(int uid, String username, String token)
	{
		Configuration conf = Configuration.getInstance();
		conf.put("user-id", uid);
		conf.put("user-name", username);
		conf.put("user-token", token);
	}
	
	public final void back()
	{
		try
		{
			this.finish();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void error(String mission, Exception ex) throws Exception
	{
		super.error(mission, ex);
		if ("login-submit".equals(mission))
		{
			this.hideLoading();
			ex.printStackTrace();
			tip("登录出错：" + ex.getMessage());
		}
	}
	
//	protected boolean onBack()
//	{
//		if (this.showLoading)
//		{
//			this.hideLoading();
//			return true;
//		}
//		else
//		{
//			try
//			{
//				// PageManager.getInstance().redirect(Class.forName(getString("page-before-login")), true);
//				this.finish();
//			}
//			catch(Exception e) { }
//			return true;
//		}
//	}
	
	public void updateLocation(double longitude, double latitude)
	{
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public void onLoad() throws Exception
	{
		this.registerLocationListener(new LocationListener()
		{
			public void onLocationChanged(Location location)
			{
				updateLocation(location.getLongitude(), location.getLatitude());
			}
		});
		
		Element.getById("psz").onClick(new ClickEvent()
		{
			public void on(Page page, Element element)
			{
				isComment = true;
				Div layer = (Div)Element.getById("mask-layer");
				Div mask = (Div)Element.getById("mask-layer").getParentNode();
				layer.setBackgroundColor(0x66000000).show();
				try
				{
					mask.append(UIFactory.build(Schema.uri("assets://me/me.kefu.alert.html"))).show();
				}catch (Exception e){}
				// 
				layer.onTouch(new TouchEvent()
		  		{
		  			public void down(Page page, Element element) {
		  				isComment = false;
		  				element.setDisplay("none").getParentNode()
			  			.removeChild(Element.getById("alert"));
		  			}
		  			public void up(Page page, Element element) {
		  			}
		  			public void move(Page page, Element element) {
		  			}
		  			public void cancel(Page page, Element element) {
		  			}
		  		});
				Element.getById("tel").onClick(new ClickEvent()
				{
					public void on(Page page, Element element)
					{
						Device.call("010-68703488");
					}
				});
			}
		});
		
		Element.getById("btn_login").onClick
		(
			new ClickEvent()
			{
				public void on(Page page, Element element)
				{
					Login loginPage = (Login)page;
					loginPage.showLoading();
					loginPage.doLogin();
				}
			}
		);
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
		Element.getById("btn_register").onClick
		(
			new ClickEvent()
			{
				public void on(Page page, Element element)
				{
					try
					{
						PageManager.getInstance().redirect(Class.forName(Global.getProperty("user_register_page")), null, true);
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					}
					
//					isComment = true;
//					Div layer= (Div)Element.getById("mask-layer").getParentNode();
//		        	Element.getById("mask-layer").setBackgroundColor(0x66000000).show();
//		        	try {
//		        		layer.append(UIFactory.build(Schema.uri("assets://common/register.360tip.html")));
//					} catch (Exception e) {
//						
//					}
//		        	
//		        	
//		        	layer.onClick(new ClickEvent()
//					{
//						public void on(Page page, Element element)
//						{
//							isComment = false;
//			  				element.setDisplay("none").getParentNode()
//				  			.removeChild(Element.getById("alert"));
//						}
//					});
//
//		        	Element.getById("clear").onClick(new ClickEvent()
//					{
//						public void on(Page page, Element element)
//						{
//							isComment = false;
//							Element.getById("mask-layer").setDisplay("none").getParentNode()
//				  			.removeChild(Element.getById("alert"));
//						}
//					});
//		        	
//		        	Element.getById("ok").onClick(new ClickEvent()
//					{
//						public void on(Page page, Element element)
//						{
//							try
//							{
//								PageManager.getInstance().redirect(Class.forName(Global.getProperty("user_register_page")), null, true);
//							}
//							catch (ClassNotFoundException e)
//							{
//								e.printStackTrace();
//							}
//						}
//					});
					
					
				}
			}
		);
		//第三方登录
		this.check_user_aleadry_login();
		Element.getById("weixin").onClick(new ClickEvent()
		{
			public void on(Page page, Element element)
			{
				do_login_weixin("weixin");
			}
		});
		Element.getById("qq").onClick(new ClickEvent()
		{
			public void on(Page page, Element element)
			{
				do_login_qqzone("qq");
			}
		});
		Element.getById("weibo").onClick(new ClickEvent()
		{
			public void on(Page page, Element element)
			{
				do_login_weibo("weibo");
			}
		});
	}
	
	private FrontiaAuthorization mAuthorization;
	public void check_user_aleadry_login()
	{
		this.mAuthorization = Frontia.getAuthorization();
		
		// 判断微信是否已经在本手机登陆
		if (this.mAuthorization.isAuthorizationReady(FrontiaAuthorization.MediaType.WEIXIN.toString()))
		{
			// 可用同步图标点亮
			//this.weixin_loginok = true;
			//((Span)Element.getById("weixin_span")).setColor(0xffff0000);
		}
		
		// 判断新浪微博是否已经在本手机登陆
		if(this.mAuthorization.isAuthorizationReady(FrontiaAuthorization.MediaType.SINAWEIBO.toString()))
		{
			// 可用同步登陆图标点亮
			//this.weibo_loginok = true;
			//((Span)Element.getById("weibo_span")).setColor(0xffff0000);
		}
		
		// 判断QQ空间是否已经在本手机登陆
		if(this.mAuthorization.isAuthorizationReady(FrontiaAuthorization.MediaType.QZONE.toString()))
		{
			// 可用同步登陆图标点亮
			//((Span)Element.getById("qq_span")).setColor(0xffff0000);
			//this.qq_loginok = true;
		}
		
	}
	// oauth统一注册或登陆入口
	public void oauth_login(String access_token)
	{
		showLoading();
		Configuration conf = Configuration.getInstance();
		
		String query = "?app=" + Configuration.getInstance().getProperty("app_name");
		query += "&buserid=" + conf.getString("baidu-push-user-id", "") + "&bchannelid=" + conf.getString("baidu-push-channel-id", "");
		query += "&access_token=" + access_token;
 		//query += "&longitude=" + this.getLongitude() + "&latitude=" + this.getLatitude();
		//this.load();
		//android.util.Log.e("oauth_api:", "http://user.artxun.com/mobile/user/oauth_login.jsp" + query);
		new JsonRequestor("oauth_login", "http://user.artxun.com/mobile/user/oauth_login.jsp" + query).go();
	}
	private void get_userinfo(final String accessToken,final String access_token) 
	{
		oauth_login(access_token);
	}
	// 微信登陆
	private void do_login_weixin(final String id)
	{
		//FrontiaAuthorization.MediaType.
		mAuthorization.authorize(this, FrontiaAuthorization.MediaType.WEIXIN.toString(), new AuthorizationListener(){
			@Override
			public void onSuccess(FrontiaUser result)
			{
				//((Span)Element.getById("weixin_span")).setColor(0xffff0000);
				//weixin_loginok = true;
				
				get_userinfo(MediaType.WEIXIN.toString(), result.getAccessToken());
				
			}
			
			@Override
			public void onFailure(int errorCode, String errorMessage) {	
				
			}

			@Override
			public void onCancel() {				
				android.util.Log.e("sina_cancel","cancel");
			}
			
		});
	}
	//新浪登录
	private void do_login_weibo(final String id) 
	{
		mAuthorization.authorize(this, FrontiaAuthorization.MediaType.SINAWEIBO.toString(), new AuthorizationListener() {
			@Override
			public void onSuccess(FrontiaUser result)
			{
				//((Span)Element.getById("weibo_span")).setColor(0xffff0000);
				//weibo_loginok = true;
				
//					alert(result.getAccessToken());
//					String call_back_url = "https://openapi.baidu.com/social/api/2.0/user/info?access_token="+result.getAccessToken()+
//							"&social_uid="+result.getId()+"&meida_type="+MediaType.SINAWEIBO.toString()+"&username="+result.getName()+
//							"&media_uid=&is_verified=0";
//					
//					android.util.Log.e("xxcbu:", call_back_url);

				
				get_userinfo(MediaType.SINAWEIBO.toString(), result.getAccessToken());
				
//					String log = id +"_id:" + result.getId() + "\n"
//							+ "username:" + result.getName() + "\n";
//							+ "birthday:" + result.getBirthday() + "\n"	
//							+ "city:" + result.getCity() + "\n"		
//							+ "province:" + result.getProvince() + "\n"		
//							+ "sex:" + result.getSex() + "\n"		
//							+ "pic url:" + result.getHeadUrl() + "\n";
//					alert(log);
			}
			@Override
			public void onFailure(int errorCode, String errorMessage) {
				
			}

			@Override
			public void onCancel() {
				
			}
		});		
	}
	//QQ空间登录
	private void do_login_qqzone(final String id) 
	{
		mAuthorization.authorize(this, FrontiaAuthorization.MediaType.QZONE.toString(),
			new AuthorizationListener() 
			{
				@Override
				public void onSuccess(FrontiaUser result)
				{
					//((Span)Element.getById("qq_span")).setColor(0xffff0000);
					//qq_loginok = true;
					
					get_userinfo(MediaType.QZONE.toString(), result.getAccessToken());
				}

				@Override
				public void onFailure(int errorCode, String errorMessage)
				{
					
				}

				@Override
				public void onCancel()
				{
					
				}

			});
	}
}
