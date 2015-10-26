package com.youaix.framework.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.*;

import com.youaix.framework.page.Executable;
import com.youaix.framework.page.Page;
import com.youaix.framework.page.PageManager;
import com.youaix.framework.page.Resolution;
import com.youaix.framework.ui.Attribute;
import com.youaix.framework.ui.Div;
import com.youaix.framework.ui.Element;
import com.youaix.framework.view.FlowLayout;
import com.youaix.framework.view.ImageView;
import com.youaix.framework.view.MtScrollView;

public final class ImageCache implements Runnable
{
	public static int MAX_CACHE_SIZE = 1024 * 1024 * 10;
	public int totalCached = 0;
	
	public HashMap<String, Image> bucket = null;
	public HashMap<ImageView, String> map = null;
	public LinkedList<Page> pages = null;
	
	// 类成员
	private Object lock = null;
	private boolean needsRecycle = false;
	
	// TODO: 还有背景图也需要回收呢
	public static ImageCache instance = null;
	
	private ImageCache()
	{
		ActivityManager activityManager = (ActivityManager)PageManager.getInstance().getCurrent().getSystemService(Context.ACTIVITY_SERVICE);
		// TODO: 需要调整此参数比例，现在是瞎写的，便于测试
		MAX_CACHE_SIZE = (int)(activityManager.getMemoryClass() * 1024 * 1024 * 0.4f);
		
		bucket = new HashMap<String, Image>(300);
		map = new HashMap<ImageView, String>(300);
		pages = new LinkedList<Page>();
		this.lock = new Object();
		new Thread(this).start();
	}
	
	public void pageStopped(Page page)
	{
		synchronized(this.pages)
		{
			this.pages.add(page);
		}
	}
	
	public void pageResumed(Page page)
	{
		synchronized(this.pages)
		{
			for (int i = 0; i < this.pages.size(); i++)
			{
				if (this.pages.get(i) == page)
				{
					this.pages.remove(i);
					return;
				}
			}
		}
	}
	
	public int getCachedBytes()
	{
		return this.totalCached;
	}
	
	public int getCachedImages()
	{
		return this.bucket.size();
	}
	
	// 标记当前需要进行回收操作了
	public void noticeRecycleNeeded()
	{
		synchronized(lock)
		{
			this.needsRecycle = true;
		}
	}
	
	// 获取缓存实例
	public static synchronized ImageCache getInstance()
	{
		if (instance == null) instance = new ImageCache();
		return instance;
	}
	
	// 图片己就绪
	@java.lang.Deprecated
	public void standby(String uri)
	{
		// 做些什么呢？
		synchronized(lock)
		{
			Image img = bucket.get(uri);
			if (null == img.bitmap)
			{
				// img.bitmap = bitmap;
				img.bitmap = this.readCacheFile(uri);
			}
			img.notifyAllViews();
		}
		
	}
	
	// 标记为可回收
	@Deprecated
	public void markRecycable(String uri)
	{
		// TODO: 做些什么好呢？
		// TODO: 要不就，当图片不在可见范围内时，就把图片标记为可回收状态，只使用一个状态来搞这事
		// TODO: 那么，defaultView这个属性是不是可以不用了呢？
		synchronized(lock)
		{
			Image img = bucket.get(uri);
			if (null == img) return;
			// 改个什么东西好呢？
		}
	}
	
	// 从缓存中获取一个图片
	public Bitmap get(String uri)
	{
		synchronized(lock)
		{
			Image img = bucket.get(uri);
			if (null != img && null != img.bitmap) return img.getBitmap();
			
			Bitmap bitmap = null;
			try
			{
				if (!uri.startsWith("http"))
					bitmap = Schema.parse(uri).getBitmap();
				else
					bitmap = readCacheFile(uri);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			if (null == bitmap) return null;
			put(uri, bitmap);
			return bitmap;
		}
	}
	
	public Bitmap get(String uri, int width, int height, int radius)
	{
		Bitmap bitmap = null;
		synchronized(lock)
		{
			Image img = bucket.get(uri);
			if (null != img && null != img.bitmap)
			{
				bitmap = img.getBitmap();
			}
			else
			{
				try
				{
					if (!uri.startsWith("http"))
						bitmap = Schema.parse(uri).getBitmap();
					else
					{
						bitmap = readCacheFile(uri);
					}
				}
				catch(Exception ex) { }
				if (null == bitmap) return null;
				put(uri, bitmap);
			}
		}
		/*
		if ((width > 0 && height > 0) && (bitmap.getWidth() != width || bitmap.getHeight() != height))
		{
			reset(uri, width, height, radius);
			return get(uri);
		}
		*/
		reset(uri, width, height, radius);
		return get(uri);
	}
	
	// 通过ImageView来获取图片
	public Bitmap get(ImageView iv)
	{
		String uri = map.get(iv);
		if (uri == null) return null;
		else return get(uri);
	}
	
	// 添加uri与bitmap的对应缓存
	public void put(String uri, Bitmap bitmap)
	{
		int size = sizeOf(bitmap);
		synchronized(lock)
		{
			this.totalCached += size;
			Image img = this.bucket.get(uri);
			if (null == img) img = new Image();
			img.size = size;
			img.bitmap = bitmap;
			img.atime = System.currentTimeMillis();
			this.bucket.put(uri, img);
		}
	}
	
	// 图片缩放
	public void reset(String uri, int width, int height, int radius)
	{
		synchronized(lock)
		{
			Image img = this.bucket.get(uri);
			if (img == null) return;
			this.totalCached -= img.size;
			img.reset(width, height, radius);
			this.totalCached += img.size;
		}
	}
	
	// 添加ImageView与Uri的对应关系
	public void put(ImageView iv, String uri)
	{
		synchronized(map)
		{
			map.put(iv, uri);
		}
		synchronized(lock)
		{
			Image img = bucket.get(uri);
			if (null == img)
			{
				img = new Image(0, null);
				bucket.put(uri, img);
			}
			// 添加ImageView引用到Image里去
			img.refCount += 1;
			img.addView(iv);
		}
	}
	
	public void dispose(String uri)
	{
		synchronized(lock)
		{
			Image img = bucket.get(uri);
			if (img != null)
			{
				
			}
		}
	}
	
	// 移除ImageView与Uri的对应关系
	public void dispose(ImageView iv, String uri)
	{
		synchronized(map)
		{
			map.remove(iv);
		}
		synchronized(lock)
		{
			Image img = bucket.get(uri);
			if (img != null)
			{
				img.removeView(iv);
			}
		}
	}
	
	// 计算位图所需要占用的内存字节数
	private static int sizeOf(Bitmap bitmap)
	{
		if (null == bitmap) return 0;
		Bitmap.Config cfg = bitmap.getConfig();
		int bits = 4;
		if (cfg == Bitmap.Config.ARGB_8888) bits = 4;
		else if (cfg == Bitmap.Config.RGB_565) bits = 2;
		else if (cfg == Bitmap.Config.ARGB_4444) bits = 2;
		else if (cfg == Bitmap.Config.ALPHA_8) bits = 1;
		return bitmap.getWidth() * bitmap.getHeight() * bits;
	}
	
	// 从缓存文件中读取图片
	private Bitmap readCacheFile(String uri)
	{
		try
		{
			// cache://image-EEF977E7ABA236ED26D71B295988DD2C
			Schema.Uri resource = Schema.parse("cache://image-" + Encrypt.MD5(uri));
			if (!resource.getFile().exists()) return null;
			Bitmap bitmap = resource.getBitmap();
			if (null == bitmap)
			{
				return null;
			}
			return bitmap;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	// 缓存中是否存在该Uri表示的图片
	public boolean isCached(String uri)
	{
		Image img = bucket.get(uri);
		if (img != null && img.bitmap != null) return true;
		File f = Schema.parse("cache://image-" + Encrypt.MD5(uri)).getFile();
		return f.exists() && f.length() > 0;
	}
	
	public void run()
	{
		// TODO: 线程什么时候应该停止？什么时候应该继续？
		while (true)
		{
			try
			{
				Thread.sleep(2000);
				recycle();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private int getTotalPss()
	{
		int pss = 0;
		ActivityManager manager = null;
		List<ActivityManager.RunningAppProcessInfo> processes = (manager = (ActivityManager)PageManager.getInstance().getCurrent().getSystemService(Page.ACTIVITY_SERVICE)).getRunningAppProcesses();
		for (int i = 0; i < processes.size(); i++)
		{
			ActivityManager.RunningAppProcessInfo app = processes.get(i);
			if (app.processName.indexOf("virtuboa") > -1 && app.processName.indexOf("bd") == -1)
			{
				pss = manager.getProcessMemoryInfo(new int[] { app.pid })[0].getTotalPss();
			}
		}
		return pss;
	}
	
	// 回收当前缓存中的图片
	private void recycle()
	{
		// TODO: 如何回收？？？
		// TODO: 应该是只有HTTP协议的图片才需要缓存到文件中的吧
		// TODO: 如果文件本来就是从缓存文件中读出来的，那就不需要再次写回去了吧？
		// android.util.Log.i("Image-Cache", "Max Size: " + (MAX_CACHE_SIZE / 1024 / 1024) + "M");
		// android.util.Log.i("Image-Cache", "Cached Size: " + (totalCached / 1024 / 1024) + "M");
		// android.util.Log.i("Image-Cache", "Cached Count: " + bucket.size());
		// android.util.Log.i("Image-Cache", "Current PSS: " + (getTotalPss() / 1024) + "M");
		// TODO: 查找refCount最小的Image，把bitmap给毙咯，直到totalCached小于MAX_CACHE_SIZE为止
		// TODO: 如果确实有处理过回收，需要调用System.gc()，还有bitmap.recycle()
		
		// if (this.totalCached < MAX_CACHE_SIZE) return;
		synchronized(this.lock)
		{
			Iterator<String> keys = bucket.keySet().iterator();
			while (keys.hasNext())
			{
				String uri = keys.next();
				Image img = bucket.get(uri);
				if (img.isRecycable())
				{
					// TODO: 回收图片
					this.totalCached -= img.size;
					img.recycle();
					keys.remove();
				}
			}
		}
		
		// 图片的释放工作放在这个线程里如何
		
		// TODO： 页面关闭时，页面内所有的ImageView的onDettachedFromWindow应该都会被触发的吧		己经和谐
		// TODO： 页面切换到后台时，图片释放工作可以交给这个线程来做								这个
		synchronized(this.pages)
		{
			for (int i = 0, l = this.pages.size(); i < l; i++)
			{
				Page page = this.pages.remove();
				page.find(com.youaix.framework.ui.Image.class, recycleMission);
			}
		}
		
		// TODO： 页面内有图片元素在不可见区域时，可以释放其内存									这个
		// 这TM怎么整？？？
		// 只回收两屏以外的图片资源
		if (true) return;
		Page currentPage = PageManager.getInstance().getCurrent();
		// 找到一个可滚动的DIV，只找一个吧
		Div container = (Div) find(currentPage.getContentPage());
		if (null == container) return;
		// 如果这个DIV的子元素的高度不够，就不处理了，怎么样算够？屏幕高度的四倍以上吧
		if (container.getMeasuredHeight() < Resolution.getScreenHeightDip() * 4) return;
		// 查找下边所有的图片，是否在可视范围内
		// 遍历元素的所有子元素，如果是DIV，则进一步递归
		// 如果是图片，则看是否在可视范围内
		// 先做第二步看看吧
		MtScrollView sView = (MtScrollView)container.getWrapperView();
		
		xx(container, 0, 0, sView.getMeasuredWidth(), sView.getScrollY() - 300);
		xx(container, sView.getScrollY() + 300, 0, sView.getMeasuredWidth(), container.getMeasuredHeight() - sView.getScrollY() - 300);
		// xx(container, sView.getScrollY(), sView.getScrollX(), sView.getMeasuredWidth(), sView.getMeasuredHeight());
	}
	
	// 寻找不可见的图片元素，回收掉
	private void xx(Element target, int top, int left, int width, int height)
	{
		if (null == target) return;
		LinkedList<Element> childs = target.getChildren();
		for (int i = 0; i < childs.size(); i++)
		{
			Element sub = childs.get(i);
			
			int t = 0, l = 0, w = 0, h = 0;
			// 如果是个图片
			if (sub instanceof com.youaix.framework.ui.Image)
			{
				ImageView iv = (ImageView)sub.getContentView();
				t = 0;
				l = 0;
				w = iv.getMeasuredWidth();
				h = iv.getMeasuredHeight();
				
				// if (t + h + 300 > top || t < top + height + 300)
				// {
				//	continue;
				// }
				
				// android.util.Log.e("ImageCache-Recycle", "ready to recycle: " + ((com.bobaoo.xiaobao.ui.Image)sub).getSrc());
				Image img = bucket.get(((com.youaix.framework.ui.Image)sub).getSrc());
				if (null == img) return;
				img.recycle();
			}
			if (!(sub instanceof Div)) continue;
			// 如果完全在可见区域内，则不需要执行xx方法了
			// 如果元素的大小，不足一个屏幕大小，并且有一部分在可见区域内，也不需要执行xx方法了
			FlowLayout fl = (FlowLayout)sub.getContentView();
			
			// t = top - fl.getOffsetTop();
			// l = left - fl.getOffsetLeft();
			// w = Math.min(width, w);
			// h = Math.min(height, Math.abs(height - h));
			t = 0;
			l = 0;
			w = fl.getMeasuredWidth();
			h = fl.getMeasuredHeight();
			
			// android.util.Log.e("ImageCache-SubView", "Top: " + t + ", Left: " + l + ", Width: " + w + ", Height: " + h);
			xx(sub, t, l, w, h);
		}
	}
	
	// 查找一个垂直方向上滚动的DIV元素
	private Element find(Element node)
	{
		if (null == node) return null;
		LinkedList<Element> childs = node.getChildren();
		for (int i = 0; i < childs.size(); i++)
		{
			Element target = find(childs.get(i));
			if (null != target) return target;
		}
		if (!(node instanceof Div)) return null;
		Div layer = (Div)node;
		if (layer.getScrollDirection() == Attribute.DIR_VERTICAL) return node;
		else return null;
	}
	
	private Executable recycleMission = new Executable()
	{
		public void modify(Element element)
		{
			Image img = bucket.get(((com.youaix.framework.ui.Image)element).getSrc());
			if (null == img) return;
			img.recycle();
		}
	};
	
	public static final class Image
	{
		public static final int STATE_NORMAL = 0x01;
		public static final int STATE_RECYCABLE = 0x02;
		public static final int STATE_DISPOSED = 0x03;
		
		public int size;
		public byte state;
		public short width;
		public short height;
		public long atime;
		public short refCount = 0;
		public Bitmap bitmap;
		public ImageView defaultView = null;
		public LinkedList<ImageView> refViews = null;
		
		public Image()
		{
			this.state = Image.STATE_NORMAL;
		}
		
		public Image(int size, Bitmap bitmap)
		{
			// TODO: ImageView的onDettachedFromWindow可以解决removeChild的问题
			// TODO: 好像需要记录该图片都被哪些ImageView给引用了，当被引用数为零时，则为可回收状态
			// TODO: 用被引用计数好呢？还是被引用的队列好呢？
			this.size = size;
			this.state = Image.STATE_NORMAL;
			this.bitmap = bitmap;
			this.atime = System.currentTimeMillis();
		}
		
		public Bitmap getBitmap()
		{
			atime = System.currentTimeMillis();
			return this.bitmap;
		}
		
		public boolean isRecycable()
		{
			// NOTE: 确保refCount的可用性
			if (this.refCount <= 0) return true;
			
			// 如果最后访问时间有点长了，也回收掉试试
			// if (System.currentTimeMillis() - this.atime > 3000) return true;
			
			// NOTE: 检查所有引用的ImageView是否在视野范围内
			if (this.defaultView != null && this.defaultView.isInSight()) return false;
			if (this.refViews == null) return true;
			for (int i = 0; i < refViews.size(); i++)
			{
				if (this.refViews.get(i).isInSight()) return false;
			}
			return true;
		}
		
		public void addView(ImageView iv)
		{
			if (defaultView == null) defaultView = iv;
			else
			{
				// TODO: 添加到引用队列中去
				if (null == this.refViews) this.refViews = new LinkedList();
				this.refViews.add(iv);
			}
		}
		
		public void removeView(ImageView iv)
		{
			if (!iv.isRemoved()) return;
			if (this.defaultView == iv)
			{
				this.defaultView = null;
				this.refCount -= 1;
			}
			else
			{
				// TODO: 查找一下链表里，这是哪个家伙
				// 应该只有当View被删掉的时候，才可以断开这个联系的吧
				if (null == this.refViews) return;
				for (int i = 0; i < this.refViews.size(); i++)
				{
					if (this.refViews.get(i) == iv)
					{
						this.refViews.remove(i);
						this.refCount -= 1;
						return;
					}
				}
			}
		}
		
		@java.lang.Deprecated
		public synchronized void notifyAllViews()
		{
			// android.util.Log.e("ImageCache.Image", "notifyAllViews");
			if (this.bitmap == null)
			{
				// android.util.Log.e("Image-Cache", "bitmap is null");
				return;
			}
			// android.util.Log.e("ImageCache.Image", "this.bitmap = " + this.bitmap);
			// android.util.Log.e("ImageCache.Image", "this.defaultView = " + this.defaultView);
			if (defaultView != null)
			{
				// android.util.Log.e("ImageCache.Image", "post invalidate delayed for 10ms.");
				// defaultView.setImageBitmap(this.bitmap);
			}
			else android.util.Log.e("Image-Cache", "defaultView is null");
		}
		
		public void recycle()
		{
			// TODO: 测试，不知道refCount是否准确
			// if (this.refCount > 1) return;
			if (this.bitmap != null && !this.bitmap.isRecycled())
			{
				this.bitmap.recycle();
			}
			this.bitmap = null;
		}
		
		public void reset(int nWidth, int nHeight, int radius)
		{
			try
			{
				if (null == this.bitmap) return;
				if ((nWidth > 0 && nHeight > 0) && (this.bitmap.getWidth() != nWidth || this.bitmap.getHeight() != nHeight))
					this.bitmap = Bitmap.createScaledBitmap(this.bitmap, nWidth, nHeight, true);
				if (radius > 0) this.bitmap = ImageProc.toRoundCorner(this.bitmap, radius);
				this.size = sizeOf(bitmap);
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}
}
