package com.youaix.framework.page;

import java.util.*;

import com.youaix.framework.common.Device;
import com.youaix.framework.common.Schema;
import com.youaix.framework.mission.Callbackable;
import com.youaix.framework.ui.Element;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;

public class PageManager
{
	private static PageManager manager = null;
	private Page current = null;
	private HashMap<Page, HashMap<String, Element>> dom = null; 		// DOM结构
	// TODO: 这个对照表可能可以去掉
	private HashMap<Page, HashMap<View, Element>> elements = null; 	// 元素表
	
	// TODO: 事物如何控制？？？
	private Stack<Object> history = null;
	
	// 遍历当前页面上的元素进行某某操作
	public void traverse(String regexp, Executable executable)
	{
		HashMap<String, Element> els = this.dom.get(this.getCurrent());
		Iterator itr = els.keySet().iterator();
		while (itr.hasNext())
		{
			String id = (String)itr.next();
			if (id.matches(regexp)) executable.modify(els.get(id));
		}
	}
	
	// 获取所有的表单元素
	public HashMap<String, Object> getFormData()
	{
		HashMap<String, Element> els = this.dom.get(this.getCurrent());
		HashMap<String, Object> formMap = new HashMap<String, Object>();

		Set<String> nameSet = els.keySet();
		for (String key : nameSet)
		{
			Element ele = els.get(key);
			if (!ele.isFormElement()) continue;
			formMap.put(key, ele.getValue());
		}
		return formMap;
	}

	// 添加页面元素
	public void addPageElement(View v, Element el)
	{
		Page page = this.getCurrent();
		HashMap<String, Element> els = dom.get(page);
		if (null == els) els = new HashMap<String, Element>();
		if (el.getId() != null) els.put(el.getId(), el);
		dom.put(page, els);
		// //////////////////////////////////////////////////////
		HashMap<View, Element> doc = elements.get(page);
		if (null == doc) doc = new HashMap<View, Element>();
		doc.put(v, el);
		elements.put(page, doc);
	}

	// 根据ID获取元素
	public Element getElementById(String id)
	{
		Page page = this.getCurrent();
		HashMap<String, Element> els = dom.get(page);
		return els.get(id);
	}
	
	// 删除一个元素
	public void removeElement(String id)
	{
		dom.get(this.getCurrent()).remove(id);
	}

	// 根据view获取element
	public Element getElementByView(View view)
	{
		Page page = this.getCurrent();
		HashMap<View, Element> els = elements.get(page);
		return els.get(view);
	}

	private PageManager()
	{
		this.dom = new HashMap<Page, HashMap<String, Element>>();
		this.elements = new HashMap<Page, HashMap<View, Element>>();
		this.history = new Stack<Object>();
	}

	public void dump()
	{
		Page page = this.getCurrent();
	}

	public void setCurrent(Page page)
	{
		this.current = page;
	}

	public Page getCurrent()
	{
		return this.current;
	}

	public boolean isCurrent(Callbackable callbackable)
	{
		return this.current == ((Page) callbackable);
	}

	public void dispose(Page page)
	{
		this.dom.remove(page);
		this.elements.remove(page);
	}
	
	public void redirect(Schema.Uri uri)
	{
		this.redirect(uri, false);
	}
	
	public void redirect(Schema.Uri uri, boolean finish)
	{
		int idx = 0;
		if (uri.getType() == Schema.URI_PAGE)
		{
			String param = "", clsName = uri.getUrl().substring(7);
			if ((idx = clsName.indexOf('?')) > -1)
			{
				clsName = clsName.substring(0, idx);
				param = uri.getUrl().substring(7 + idx + 1);
			}
			try
			{
				this.redirect(Class.forName(clsName), Page.parameter(param), finish);
			}
			catch (Exception e)
			{
				this.getCurrent().onError(e);
			}
			return;
		}
		if (uri.getType() == Schema.URI_TEL)
		{
			Device.call(uri);
			return;
		}
		if (uri.getType() == Schema.URI_HTTP)
		{
			Device.browse(uri.getUrl());
		}
		if (uri.getType() == Schema.URI_MARKET)
		{
			getCurrent().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri.getUrl())));
		}
	}
	
	public void request(final Class destPage, int requestCode, Bundle parameter)
	{
		Page page = this.getCurrent();
		Intent intent = new Intent(page, destPage);
		if (parameter != null) intent.putExtras(parameter);
		intent.putExtra("__request_code__", requestCode);
		intent.putExtra("__request_page__", page.getClass().getName());
		page.startActivityForResult(intent, requestCode);
		page.overrideTransition(getPageTransition(destPage));
	}

	public void redirect(final Class destPage, boolean finish)
	{
		Page page = this.getCurrent();
		Intent intent = new Intent(page, destPage);
		intent.putExtra("__request_page__", page.getClass().getName());
		page.startActivity(intent);
		if (finish) page.finish();
		else page.overrideTransition(getPageTransition(destPage));
	}

	public void redirect(final Class destPage, final boolean finish, int timeout)
	{
		new Handler().postDelayed(new Runnable()
		{
			public void run()
			{
				PageManager.getInstance().redirect(destPage, finish);
			}
		}, timeout);
	}

	public void redirect(final Class destPage, Bundle parameter, boolean finish)
	{
		Page page = this.getCurrent();
		Intent intent = new Intent(page, destPage);
		if (parameter != null) intent.putExtras(parameter);
		intent.putExtra("__request_page__", page.getClass().getName());
		page.startActivity(intent);
		if (finish) page.finish();
		else page.overrideTransition(getPageTransition(destPage));
	}
	
	private final int getPageTransition(Class page)
	{
		int transition = 0;
		try
		{
			Page instance = (Page)page.newInstance();
			return instance.enterTransition();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return transition;
	}

	public static PageManager getInstance()
	{
		if (null == manager) manager = new PageManager();
		return manager;
	}
}
