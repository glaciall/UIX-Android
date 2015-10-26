package com.youaix.framework.ui;

import com.youaix.framework.common.Schema;
import com.youaix.framework.event.JavascriptInterface;
import com.youaix.framework.event.LoadEvent;
import com.youaix.framework.event.ProgressChangedEvent;
import com.youaix.framework.event.RedirectEvent;
import com.youaix.framework.page.PageManager;

import android.annotation.SuppressLint;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Browser extends Element 
{
	private WebView view = null;
	
	public Browser()
	{
		super();
		this.view = new WebView(getContext());
		WebSettings settings = this.view.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		this.view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		this.view.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				if (url.startsWith("http://") || url.startsWith("javascript:") || url.startsWith("https://")) return false;
				else 
				{
					PageManager.getInstance().redirect(Schema.parse(url));
					return true;
				}
			}
			
			
		});
	}
	
	public Browser onRedirect(final RedirectEvent event)
	{
		this.view.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				event.on(url);
				if (url.startsWith("http://") || url.startsWith("javascript:") || url.startsWith("https://")) return false;
				return true;
			}
		});
		return this;
	}
	
	public Browser refresh()
	{
		this.view.reload();
		return this;
	}
	
	public Browser back()
	{
		this.view.goBack();
		return this;
	}
	
	public Browser forward()
	{
		this.view.goForward();
		return this;
	}
	
	public Browser setHtml(String html)
	{
		this.view.getSettings().setDefaultTextEncodingName("utf-8");
		this.view.loadDataWithBaseURL(null,html, "text/html", "utf-8",null);
		return this;
	}
	
	public Browser onLoading(final ProgressChangedEvent evt)
	{
		final Browser self = this;
		this.view.setWebChromeClient
		(
			new WebChromeClient()
			{
				public void onProgressChanged(WebView view, int newProgress)
				{
					try
					{
						evt.on(newProgress);
					}
					catch(Throwable e)
					{
						e.printStackTrace();
					}
				}
				
				public void onShowCustomView(View view, CustomViewCallback callback) {};
			}
		);
		return this;
	}

	public Browser onLoad(final LoadEvent evt)
	{
		final Browser self = this;
		this.view.setWebChromeClient(new WebChromeClient()
		{
			public void onProgressChanged(WebView view, int newProgress)
			{
				super.onProgressChanged(view, newProgress);
				try
				{
					if (newProgress == 100) evt.on(getContext(), self);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		});
		return this;
	}
	
	public Browser eval(String javascript)
	{
		this.view.loadUrl("javascript:" + javascript);
		return this;
	}
	
	@SuppressLint("JavascriptInterface")
	public Browser setInterface(final JavascriptInterface jInterface)
	{
		this.view.addJavascriptInterface(new Object()
		{
			public void handle(String msg)
			{
				jInterface.handle(msg);
			}
		},
		"browser");
		return this;
	}
	
	public Browser setHref(String url)
	{
		this.view.loadUrl(url);
		return this;
	}
	
	public View getContentView()
	{
		return this.view;
	}
}
