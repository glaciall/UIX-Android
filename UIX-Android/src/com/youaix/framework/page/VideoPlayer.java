package com.youaix.framework.page;

import android.content.Intent;

public class VideoPlayer extends Page
{
	protected Element createBody() throws Exception
	{
		return new Div().setWidth(1.0f).setHeight(1.0f).setBackgroundColor(0xff000000).setId("body");
	}

	protected Element createTitle() throws Exception
	{
		return null;
	}

	protected Element createNavigator() throws Exception
	{
		return null;
	}
	
	protected void onLoad() throws Exception
	{
		BCyberPlayerFactory.init(this);
		BEngineManager mgr = BCyberPlayerFactory.createEngineManager();
		this.prepareProgressBar();
		this.installPlayer();
	}

	private final void installPlayer()
	{
		final BEngineManager mgr = BCyberPlayerFactory.createEngineManager();
		final Page self = this;
		if (mgr.EngineInstalled())
		{
			new Timeout("install-complete", 10).go();
			return;
		}
		confirm("温馨提示", "观看视频需要安装播放器插件，是否现在安装？\n\n（请在WIFI环境下安装，以免产生大量的流量费用）", new ConfirmHandler()
		{
			public void confirm()
			{
				startDownload();
				mgr.installAsync(new OnEngineListener()
				{
					public int onDownload(int total, int current)
					{
						((VideoPlayer)self).showProgress((float)current / (float)total);
						return DOWNLOAD_CONTINUE;
					}

					public void onInstalled(int result)
					{
						if (result == RET_NEW_PACKAGE_INSTALLED)
							new Timeout("install-complete", 10).go();
						else
						{
							self.tip("安装失败");
							self.finish();
						}
					}

					public int onPreInstall()
					{
						return DOWNLOAD_CONTINUE;
					}

					public boolean onPrepare()
					{
						return true;
					}
				});
			}

			public void cancel()
			{
				self.finish();
			}
		});
	}
	
	public void resume()
	{
		this.finish();
	}
	
	private String percentText = "0%";
	public final void showProgress(float percent)
	{
		((ProgressBar)Element.getById("progress-bar")).setPercent(percent);
		this.percentText = ((int)(percent * 100)) + "%";
	}
	
	public final void startDownload()
	{
		Element.getById("mask-layer").show();
		new Timeout("renew-percent", 100).go();
	}
	
	public void complete(String mission, Object data) throws Exception
	{
		super.complete(mission, data);
		if ("renew-percent".equals(mission))
		{
			((Span)Element.getById("progress-percent-text")).setText(percentText);
			if (!percentText.equals("100%")) new Timeout("renew-percent", 100).go();
			else Element.getById("mask-layer").vanish();
			return;
		}
		if ("install-complete".equals(mission))
		{
			BEngineManager mgr = BCyberPlayerFactory.createEngineManager();
			mgr.initCyberPlayerEngine(Global.getProperty("baidu_api_key"), Global.getProperty("baidu_secret_key"));
			
			Intent intent = new Intent(Intent.ACTION_VIEW); 
			android.net.Uri uri = android.net.Uri.parse(getString("video-url"));
			intent.setClassName("com.baidu.cyberplayer.engine","com.baidu.cyberplayer.engine.PlayingActivity"); 
			intent.setData(uri);
			startActivity(intent);
		}
	}
	
	private final void prepareProgressBar()
	{
		Div layer = (Div)Element.getById("mask-layer");
		layer.setBackgroundColor(0x66000000).setWidth(1.0f).setHeight(1.0f).setAlign(Attribute.ALIGN_CENTER, Attribute.ALIGN_MIDDLE);
		layer.onTouch(new TouchEvent()
		{
			public void down(Page page, Element element) { }
			public void up(Page page, Element element) { }
			public void move(Page page, Element element) { }
			public void cancel(Page page, Element element) { }
		});
		layer.append
		(
			new Div().setWidth(240).setBackgroundColor(0xffffffff).setBorderWidth(1).setBorderColor(Attribute.COLOR_GRAY).append
			(
				new Div().setHeight(25).setAlign(Attribute.ALIGN_CENTER, Attribute.ALIGN_MIDDLE).append
				(
					new Span().setText("下载视频播放插件").setSize(16)
				)
			).append
			(
				new Hr().setColor(Attribute.COLOR_GRAY)
			)
			.append
			(
				new Div().setHeight(50).setAlign(Attribute.ALIGN_CENTER, Attribute.ALIGN_MIDDLE).append
				(
					new ProgressBar().setWidth(0.8f).setHeight(10).setId("progress-bar")
				)
			).append
			(
				new Div().setAlign(Attribute.ALIGN_CENTER, Attribute.ALIGN_MIDDLE).setHeight(25).append
				(
					new Span().setText("0%").setSize(12).setId("progress-percent-text")
				)
			)
		);
	}
}
