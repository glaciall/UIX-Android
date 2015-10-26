package com.youaix.framework.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.bobaoo.xiaobao.page.Executable;
import com.bobaoo.xiaobao.page.Page;
import com.bobaoo.xiaobao.page.PageManager;
import com.bobaoo.xiaobao.page.Resolution;
import com.bobaoo.xiaobao.ui.Attribute;
import com.bobaoo.xiaobao.ui.Div;
import com.bobaoo.xiaobao.ui.Element;
import com.bobaoo.xiaobao.view.FlowLayout;
import com.bobaoo.xiaobao.view.ImageView;
import com.bobaoo.xiaobao.view.MtScrollView;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.*;

public final class ImageCache implements Runnable
{
	public static int MAX_CACHE_SIZE = 1024 * 1024 * 10;
	public int totalCached = 0;
	
	public HashMap<String, Image> bucket = null;
	public HashMap<ImageView, String> map = null;
	public LinkedList<Page> pages = null;
	
	// 绫绘垚鍛�
	private Object lock = null;
	private boolean needsRecycle = false;
	
	// TODO: 杩樻湁鑳屾櫙鍥句篃闇�瑕佸洖鏀跺憿
	public static ImageCache instance = null;
	
	private ImageCache()
	{
		ActivityManager activityManager = (ActivityManager)PageManager.getInstance().getCurrent().getSystemService(Context.ACTIVITY_SERVICE);
		// TODO: 闇�瑕佽皟鏁存鍙傛暟姣斾緥锛岀幇鍦ㄦ槸鐬庡啓鐨勶紝渚夸簬娴嬭瘯
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
	
	// 鏍囪褰撳墠闇�瑕佽繘琛屽洖鏀舵搷浣滀簡
	public void noticeRecycleNeeded()
	{
		synchronized(lock)
		{
			this.needsRecycle = true;
		}
	}
	
	// 鑾峰彇缂撳瓨瀹炰緥
	public static synchronized ImageCache getInstance()
	{
		if (instance == null) instance = new ImageCache();
		return instance;
	}
	
	// 鍥剧墖宸卞氨缁�
	@java.lang.Deprecated
	public void standby(String uri)
	{
		// 鍋氫簺浠�涔堝憿锛�
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
	
	// 鏍囪涓哄彲鍥炴敹
	@Deprecated
	public void markRecycable(String uri)
	{
		// TODO: 鍋氫簺浠�涔堝ソ鍛紵
		// TODO: 瑕佷笉灏憋紝褰撳浘鐗囦笉鍦ㄥ彲瑙佽寖鍥村唴鏃讹紝灏辨妸鍥剧墖鏍囪涓哄彲鍥炴敹鐘舵�侊紝鍙娇鐢ㄤ竴涓姸鎬佹潵鎼炶繖浜�
		// TODO: 閭ｄ箞锛宒efaultView杩欎釜灞炴�ф槸涓嶆槸鍙互涓嶇敤浜嗗憿锛�
		synchronized(lock)
		{
			Image img = bucket.get(uri);
			if (null == img) return;
			// 鏀逛釜浠�涔堜笢瑗垮ソ鍛紵
		}
	}
	
	// 浠庣紦瀛樹腑鑾峰彇涓�涓浘鐗�
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
	
	// 閫氳繃ImageView鏉ヨ幏鍙栧浘鐗�
	public Bitmap get(ImageView iv)
	{
		String uri = map.get(iv);
		if (uri == null) return null;
		else return get(uri);
	}
	
	// 娣诲姞uri涓巄itmap鐨勫搴旂紦瀛�
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
	
	// 鍥剧墖缂╂斁
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
	
	// 娣诲姞ImageView涓嶶ri鐨勫搴斿叧绯�
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
			// 娣诲姞ImageView寮曠敤鍒癐mage閲屽幓
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
	
	// 绉婚櫎ImageView涓嶶ri鐨勫搴斿叧绯�
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
	
	// 璁＄畻浣嶅浘鎵�闇�瑕佸崰鐢ㄧ殑鍐呭瓨瀛楄妭鏁�
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
	
	// 浠庣紦瀛樻枃浠朵腑璇诲彇鍥剧墖
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
	
	// 缂撳瓨涓槸鍚﹀瓨鍦ㄨUri琛ㄧず鐨勫浘鐗�
	public boolean isCached(String uri)
	{
		Image img = bucket.get(uri);
		if (img != null && img.bitmap != null) return true;
		File f = Schema.parse("cache://image-" + Encrypt.MD5(uri)).getFile();
		return f.exists() && f.length() > 0;
	}
	
	public void run()
	{
		// TODO: 绾跨▼浠�涔堟椂鍊欏簲璇ュ仠姝紵浠�涔堟椂鍊欏簲璇ョ户缁紵
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
	
	// 鍥炴敹褰撳墠缂撳瓨涓殑鍥剧墖
	private void recycle()
	{
		// TODO: 濡備綍鍥炴敹锛燂紵锛�
		// TODO: 搴旇鏄彧鏈塇TTP鍗忚鐨勫浘鐗囨墠闇�瑕佺紦瀛樺埌鏂囦欢涓殑鍚�
		// TODO: 濡傛灉鏂囦欢鏈潵灏辨槸浠庣紦瀛樻枃浠朵腑璇诲嚭鏉ョ殑锛岄偅灏变笉闇�瑕佸啀娆″啓鍥炲幓浜嗗惂锛�
		// android.util.Log.i("Image-Cache", "Max Size: " + (MAX_CACHE_SIZE / 1024 / 1024) + "M");
		// android.util.Log.i("Image-Cache", "Cached Size: " + (totalCached / 1024 / 1024) + "M");
		// android.util.Log.i("Image-Cache", "Cached Count: " + bucket.size());
		// android.util.Log.i("Image-Cache", "Current PSS: " + (getTotalPss() / 1024) + "M");
		// TODO: 鏌ユ壘refCount鏈�灏忕殑Image锛屾妸bitmap缁欐瘷鍜紝鐩村埌totalCached灏忎簬MAX_CACHE_SIZE涓烘
		// TODO: 濡傛灉纭疄鏈夊鐞嗚繃鍥炴敹锛岄渶瑕佽皟鐢⊿ystem.gc()锛岃繕鏈塨itmap.recycle()
		
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
					// TODO: 鍥炴敹鍥剧墖
					this.totalCached -= img.size;
					img.recycle();
					keys.remove();
				}
			}
		}
		
		// 鍥剧墖鐨勯噴鏀惧伐浣滄斁鍦ㄨ繖涓嚎绋嬮噷濡備綍
		
		// TODO锛� 椤甸潰鍏抽棴鏃讹紝椤甸潰鍐呮墍鏈夌殑ImageView鐨刼nDettachedFromWindow搴旇閮戒細琚Е鍙戠殑鍚�		宸辩粡鍜岃皭
		// TODO锛� 椤甸潰鍒囨崲鍒板悗鍙版椂锛屽浘鐗囬噴鏀惧伐浣滃彲浠ヤ氦缁欒繖涓嚎绋嬫潵鍋�								杩欎釜
		synchronized(this.pages)
		{
			for (int i = 0, l = this.pages.size(); i < l; i++)
			{
				Page page = this.pages.remove();
				page.find(com.bobaoo.xiaobao.ui.Image.class, recycleMission);
			}
		}
		
		// TODO锛� 椤甸潰鍐呮湁鍥剧墖鍏冪礌鍦ㄤ笉鍙鍖哄煙鏃讹紝鍙互閲婃斁鍏跺唴瀛�									杩欎釜
		// 杩橳M鎬庝箞鏁达紵锛燂紵
		// 鍙洖鏀朵袱灞忎互澶栫殑鍥剧墖璧勬簮
		if (true) return;
		Page currentPage = PageManager.getInstance().getCurrent();
		// 鎵惧埌涓�涓彲婊氬姩鐨凞IV锛屽彧鎵句竴涓惂
		Div container = (Div) find(currentPage.getContentPage());
		if (null == container) return;
		// 濡傛灉杩欎釜DIV鐨勫瓙鍏冪礌鐨勯珮搴︿笉澶燂紝灏变笉澶勭悊浜嗭紝鎬庝箞鏍风畻澶燂紵灞忓箷楂樺害鐨勫洓鍊嶄互涓婂惂
		if (container.getMeasuredHeight() < Resolution.getScreenHeightDip() * 4) return;
		// 鏌ユ壘涓嬭竟鎵�鏈夌殑鍥剧墖锛屾槸鍚﹀湪鍙鑼冨洿鍐�
		// 閬嶅巻鍏冪礌鐨勬墍鏈夊瓙鍏冪礌锛屽鏋滄槸DIV锛屽垯杩涗竴姝ラ�掑綊
		// 濡傛灉鏄浘鐗囷紝鍒欑湅鏄惁鍦ㄥ彲瑙嗚寖鍥村唴
		// 鍏堝仛绗簩姝ョ湅鐪嬪惂
		MtScrollView sView = (MtScrollView)container.getWrapperView();
		
		xx(container, 0, 0, sView.getMeasuredWidth(), sView.getScrollY() - 300);
		xx(container, sView.getScrollY() + 300, 0, sView.getMeasuredWidth(), container.getMeasuredHeight() - sView.getScrollY() - 300);
		// xx(container, sView.getScrollY(), sView.getScrollX(), sView.getMeasuredWidth(), sView.getMeasuredHeight());
	}
	
	// 瀵绘壘涓嶅彲瑙佺殑鍥剧墖鍏冪礌锛屽洖鏀舵帀
	private void xx(Element target, int top, int left, int width, int height)
	{
		if (null == target) return;
		LinkedList<Element> childs = target.getChildren();
		for (int i = 0; i < childs.size(); i++)
		{
			Element sub = childs.get(i);
			
			int t = 0, l = 0, w = 0, h = 0;
			// 濡傛灉鏄釜鍥剧墖
			if (sub instanceof com.bobaoo.xiaobao.ui.Image)
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
				Image img = bucket.get(((com.bobaoo.xiaobao.ui.Image)sub).getSrc());
				if (null == img) return;
				img.recycle();
			}
			if (!(sub instanceof Div)) continue;
			// 濡傛灉瀹屽叏鍦ㄥ彲瑙佸尯鍩熷唴锛屽垯涓嶉渶瑕佹墽琛寈x鏂规硶浜�
			// 濡傛灉鍏冪礌鐨勫ぇ灏忥紝涓嶈冻涓�涓睆骞曞ぇ灏忥紝骞朵笖鏈変竴閮ㄥ垎鍦ㄥ彲瑙佸尯鍩熷唴锛屼篃涓嶉渶瑕佹墽琛寈x鏂规硶浜�
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
	
	// 鏌ユ壘涓�涓瀭鐩存柟鍚戜笂婊氬姩鐨凞IV鍏冪礌
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
			Image img = bucket.get(((com.bobaoo.xiaobao.ui.Image)element).getSrc());
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
			// TODO: ImageView鐨刼nDettachedFromWindow鍙互瑙ｅ喅removeChild鐨勯棶棰�
			// TODO: 濂藉儚闇�瑕佽褰曡鍥剧墖閮借鍝簺ImageView缁欏紩鐢ㄤ簡锛屽綋琚紩鐢ㄦ暟涓洪浂鏃讹紝鍒欎负鍙洖鏀剁姸鎬�
			// TODO: 鐢ㄨ寮曠敤璁℃暟濂藉憿锛熻繕鏄寮曠敤鐨勯槦鍒楀ソ鍛紵
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
			// NOTE: 纭繚refCount鐨勫彲鐢ㄦ��
			if (this.refCount <= 0) return true;
			
			// 濡傛灉鏈�鍚庤闂椂闂存湁鐐归暱浜嗭紝涔熷洖鏀舵帀璇曡瘯
			// if (System.currentTimeMillis() - this.atime > 3000) return true;
			
			// NOTE: 妫�鏌ユ墍鏈夊紩鐢ㄧ殑ImageView鏄惁鍦ㄨ閲庤寖鍥村唴
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
				// TODO: 娣诲姞鍒板紩鐢ㄩ槦鍒椾腑鍘�
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
				// TODO: 鏌ユ壘涓�涓嬮摼琛ㄩ噷锛岃繖鏄摢涓浼�
				// 搴旇鍙湁褰揤iew琚垹鎺夌殑鏃跺�欙紝鎵嶅彲浠ユ柇寮�杩欎釜鑱旂郴鐨勫惂
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
			// TODO: 娴嬭瘯锛屼笉鐭ラ亾refCount鏄惁鍑嗙‘
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
