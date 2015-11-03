package com.youaix.framework.ui;
import java.util.*;

import com.youaix.framework.common.ImageCache;
import com.youaix.framework.common.Schema;
import com.youaix.framework.event.ClickEvent;
import com.youaix.framework.event.DragEvent;
import com.youaix.framework.event.FocusEvent;
import com.youaix.framework.event.HoverEvent;
import com.youaix.framework.event.LoadEvent;
import com.youaix.framework.event.MoveEvent;
import com.youaix.framework.event.PressEvent;
import com.youaix.framework.event.TouchEvent;
import com.youaix.framework.page.Page;
import com.youaix.framework.page.PageManager;
import com.youaix.framework.page.Resolution;
import com.youaix.framework.view.FlowLayout;

import android.app.Activity;
import android.content.res.Resources.NotFoundException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

public abstract class Element {
	public static final int VI_SHOWN = 0x00; // 正常显示
	public static final int VI_HIDDEN = 0x04; // 不显示但占位
	public static final int VI_VANISHED = 0x08; // 不显示且不占位

	// //////////////////////////////////////////////////////////////////////////////////////////
	// / 以下为DOM相关
	private static int ELID = 0xff0000;
	private String id = null;
	protected Element parentNode = null;
	protected LinkedList<Element> children = null;
	private HashMap<String, String> attributes = null;
	private FlowLayout.LayoutParams layout = null;

	public Element()
	{
		this.children = new LinkedList<Element>();
	}
	
	public final Element setId(String id)
	{
		this.id = id;
		return this;
	}

	public final String getId()
	{
		return this.id;
	}

	public final LinkedList<Element> getChildren()
	{
		return this.children;
	}

	public final String getAttribute(String attrName)
	{
		if (null == this.attributes) this.attributes = new HashMap<String, String>();
		return this.attributes.get(attrName);
	}

	public final Element setAttribute(String attrName, String attrValue)
	{
		if (null == this.attributes) this.attributes = new HashMap<String, String>();
		this.attributes.put(attrName, attrValue);
		return this;
	}

	public final Element setAnimation(String anim)
	{
		try
		{
			this.getContentView().setAnimation(AnimationUtils.loadAnimation(getContext(), Schema.parse("res://animator/" + anim + ".xml").getId()));
		}
		catch (Exception e) { }
		return this;
	}
	
	public static final Element getById(String id)
	{
		return PageManager.getInstance().getElementById(id);
	}
	
	public final Element find(String id)
	{
		for (int i = 0; i < this.children.size(); i++)
		{
			Element child = this.children.get(i);
			if (id.equals(child.getId())) return child;
		}
		for (int i = 0; i < this.children.size(); i++)
		{
			Element child = this.children.get(i);
			Element target = child.find(id);
			if (target != null) return target;
		}
		return null;
	}
	
	public abstract View getContentView();
	
	public View getWrapperView()
	{
		return this.getContentView();
	}

	public Element append(Element el)
	{
		if (null == el) return this;
		View view = this.getContentView();
		View elv = el.getWrapperView();
		
		PageManager.getInstance().addPageElement(elv, el);
		if (!el.isTouchEventBound && el.isStyleStatable()) el.onTouch(el.touchEvent);

		if (view instanceof ViewGroup)
		{
			children.add(el);
			if (elv != null)
			{
				((ViewGroup) view).addView(elv, el.getLayout());
				if (elv.getId() == View.NO_ID) elv.setId(ELID += 1);
			}
			el.parentNode = this;
			return this;
		}
		else return null;
	}
	
	// 获取父元素
	public final Element getParentNode()
	{
		return this.parentNode;
	}
	
	// 添加元素至特定位置
	public final Element appendAt(int index, Element el)
	{
		if (null == el) return this;
		View view = this.getContentView();
		View elv = el.getWrapperView();

		PageManager.getInstance().addPageElement(elv, el);
		if (!el.isTouchEventBound && el.isStyleStatable()) el.onTouch(el.touchEvent);

		if (view instanceof ViewGroup)
		{
			children.add(index, el);
			if (elv != null) ((ViewGroup) view).addView(elv, index, el.getLayout());
			el.parentNode = this;
			return this;
		}
		else return null;
	}
	
	// 删除子元素
	public boolean removeChild(Element el)
	{
		if (null == this.children) return false;
		View self = this.getContentView();
		if (!(self instanceof ViewGroup)) return false;
		for (int i = 0; i < this.children.size(); i++)
		{
			Element child = this.children.get(i);
			if (child == el)
			{
				this.children.remove(i);
				((ViewGroup)self).removeView(child.getWrapperView());
				if (el.getId() != null)
				{
					PageManager.getInstance().removeElement(el.getId());
				}
				return true;
			}
		}
		el.dispose();
		return false;
	}
	
	private final void dispose()
	{
		if (this.children != null)
		{
			for (int i = 0; i < children.size(); i++)
			{
				children.get(i).dispose();
			}
			this.children.clear();
		}
		// NOTE: 不需要另外做图片的释放工作，remove掉就会触发onDetachedFromWindow事件
		if (this.getId() != null) PageManager.getInstance().removeElement(this.getId());
	}

	@Deprecated
	public Element replaceChild(Element el)
	{
		ViewGroup view = (ViewGroup) this.getContentView();
		view.removeAllViews();
		return this.append(el);
	}

	/*
	protected static final Element from(View view)
	{
		return PageManager.getInstance().getElementByView(view);
	}
	*/

	protected final Page getContext()
	{
		return PageManager.getInstance().getCurrent();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////
	// / 以下为事件相关

	private ClickEvent clickEvent = null;
	public Element onClick(final ClickEvent evt)
	{
		this.clickEvent = evt;
		isTouchEventBound = true;
		this.onTouch(this.touchEvent);
		return this;
	}

	public Element onPress(final PressEvent event)
	{
		final Element self = this;
		this.getContentView().setOnLongClickListener(
				new View.OnLongClickListener()
				{
					public boolean onLongClick(View view)
					{
						try
						{
							event.on(PageManager.getInstance().getCurrent(), self);
						}
						catch(Throwable e)
						{
							e.printStackTrace();
						}
						return true;
					}
				});
		return this;
	}
	
	public Element onLoad(final LoadEvent event)
	{
		return this;
	}

	public Element onDrag(final DragEvent event)
	{
		return this;
	}

	public Element onHover(final HoverEvent event)
	{
		return this;
	}

	public Element onMove(final MoveEvent event)
	{
		return this;
	}
	
	public interface StatableStyle
	{
		public void onHover(Element element);
		public void onRelease(Element element);
	}
	
	// 创建一个状态样式响应内部处理类
	public StatableStyle generateStyle()
	{
		return null;
	}
	
	// true表示该元素支持状态样式
	public boolean isStyleStatable()
	{
		return false;
	}

	private void dispatchEvent(Element root, MotionEvent evt, boolean triggerClickEvent)
	{
		if (null == root) return;
		LinkedList<Element> children = root.getChildren();
		for (int i = 0; i < children.size(); i++)
		{
			View child = children.get(i).getContentView();
			if (child instanceof ViewGroup) dispatchEvent(children.get(i), evt, triggerClickEvent);
			// child.dispatchTouchEvent(evt);
			// child.onTouchEvent(evt);
			children.get(i).handleTouchEvent(evt, triggerClickEvent);
		}
	}
	
	boolean touchCanceled = false;
	int lastX = 0;
	int lastY = 0;
	private void handleTouchEvent(MotionEvent event, boolean triggerClickEvent)
	{
		try
		{
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				lastX = (int)event.getX();
				lastY = (int)event.getY();
				touchCanceled = false;
				if (this.style != null) this.style.onHover(this);
				if (this.touchEvent != null) this.touchEvent.down(getContext(), this);
			}
			else if (event.getAction() == MotionEvent.ACTION_UP)
			{
				touchCanceled = false;
				if (this.style != null) this.style.onRelease(this);
				if (!touchCanceled && this.touchEvent != null) this.touchEvent.up(getContext(), this);
				if (!touchCanceled && triggerClickEvent && this.clickEvent != null) this.clickEvent.on(getContext(), this);
			}
			else if (event.getAction() == MotionEvent.ACTION_MOVE)
			{
				if (Math.abs(lastX - event.getX()) > 10 || Math.abs(lastY - event.getY()) > 10)
				{
					touchCanceled = true;
					if (this.style != null) this.style.onRelease(this);
					if (this.touchEvent != null) this.touchEvent.cancel(getContext(), this);
				}
			}
			else if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_OUTSIDE)
			{
				touchCanceled = true;
				if (this.style != null) this.style.onRelease(this);
				if (this.touchEvent != null) this.touchEvent.cancel(getContext(), this);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	private Element findSupervisor()
	{
		int depth = 0;
		Element node = this;
		
		// 如果找不到上层的绑定事件的元素，以最上层的带有状态样式的元素为准
		// 如果连上层的带有状态样式的元素都找不着，则以当前元素为准
		Element supervisor = this;
		
		while ((node = node.getParentNode()) != null)
		{
			depth += 1;
			if (node instanceof Div && ((Div)node).isScrollable()) break;
			if (depth >= 5) break;
			if (node.isStyleStatable() && node.style != null) supervisor = node;
			if (node.isTouchEventBound) return node;
		}
		if (null == supervisor) supervisor = this;
		return supervisor;
	}
	
	public String toString()
	{
		return this.getClass().getSimpleName() + ":" + this.getId();
	}
	
	private final Element bindTouchEvent()
	{
		final Element self = this;
		// TODO:输入框的事件有点怪啊，绑定不上来了
		this.getContentView().setOnTouchListener(new android.view.View.OnTouchListener()
		{
			private boolean isTouchDown = false;
			@Override
			public boolean onTouch(View view, MotionEvent evt)
			{
				boolean triggerClickEvent = false;
				boolean styleOnly = true;
				if (evt.getAction() == MotionEvent.ACTION_DOWN) isTouchDown = true;
				if (evt.getAction() == MotionEvent.ACTION_UP)
				{
					if (isTouchDown) triggerClickEvent = true;
					isTouchDown = false;
				}
				if (evt.getAction() == MotionEvent.ACTION_CANCEL || evt.getAction() == MotionEvent.ACTION_OUTSIDE) isTouchDown = false;
				
				// 如果当前元素己经绑定了TouchEvent，则不需要去找谁处理了，就是自己来处理
				Element node = isTouchEventBound ? self : findSupervisor();
				// android.util.Log.e("Supervisor:" + self.toString(), node.toString() + ":" + node.hashCode());
				dispatchEvent(node, evt, triggerClickEvent);
				node.handleTouchEvent(evt, triggerClickEvent);
				// return !(self instanceof Input);
				return true;
			}
		});
		return this;
	}
	
	private boolean isTouchEventBound = false;
	private StatableStyle style = null;
	private TouchEvent touchEvent = null;
	
	public Element onTouch(final TouchEvent event)
	{
		if (event != null)
		{
			this.touchEvent = event;
			isTouchEventBound = true;
		}
		if (this.isStyleStatable() && null == this.style) this.style = this.generateStyle();
		// 阻止无事件的元素
		if (this.touchEvent != null || this.style != null || this.clickEvent != null)
			this.bindTouchEvent();
		return this;
	}
	
	/*
	public Element onTouch(final TouchEvent event)
	{
		isTouchEventBound = true;
		final Element self = this;
		if (null == style) style = self.generateStyle();
		// if (this.touchEvent != null) return this;
		this.touchEvent = event;
		this.getContentView().setOnTouchListener(new View.OnTouchListener()
		{
			private boolean isTouchDown = false;
			public boolean onTouch(View view, MotionEvent evt)
			{
				try
				{
					// 先把事件传递到最深的子元素上去
					dispatchEvent(self, evt);
					
					if (evt.getAction() == MotionEvent.ACTION_DOWN)
					{
						isTouchDown = true;
						if (style != null) style.onHover(self);
						if (event != null) event.down(PageManager.getInstance().getCurrent(), self);
					}
					else if (evt.getAction() == MotionEvent.ACTION_UP)
					{
						if (isTouchDown && style != null) style.onRelease(self);
						if (isTouchDown && event != null) event.up(PageManager.getInstance().getCurrent(), self);
						if (isTouchDown && clickEvent != null) clickEvent.on(PageManager.getInstance().getCurrent(), self);
						isTouchDown = false;
					}
					// TODO: 先屏蔽掉再说，大家不会使用这个还硬要上，不能不屏蔽啊
					// else if (evt.getAction() == MotionEvent.ACTION_MOVE) event.move(PageManager.getInstance().getCurrent(), self);
					// ACTION_CANCEL表示当前事件被取消，转交给其它元素处理的意思
					else if (evt.getAction() == MotionEvent.ACTION_OUTSIDE || evt.getAction() == MotionEvent.ACTION_CANCEL)
					{
						if (isTouchDown && style != null) style.onRelease(self);
						if (isTouchDown && event != null) event.cancel(PageManager.getInstance().getCurrent(), self);
						isTouchDown = false;
					}
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
				
				return true;
			}
		});

		return this;
	}
	*/

	public Element onFocus(final FocusEvent event)
	{
		final Element self = this;
		this.getContentView().setOnFocusChangeListener(
				new View.OnFocusChangeListener()
				{
					public void onFocusChange(View view, boolean hasFocus)
					{
						try
						{
							if (hasFocus)
							{
								Page context = PageManager.getInstance().getCurrent();
								context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
								// ((InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE)).showSoftInput(view, 0);
								event.focus(context, self);
							}
							else
							{
								// getContext().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
								event.blur(PageManager.getInstance().getCurrent(), self);
							}
						}
						catch(Throwable e)
						{
							e.printStackTrace();
						}
					}
				});
		return this;
	}
	
	public Element onKeyPress(final com.youaix.framework.event.KeyEvent evt)
	{
		final Element self = this;
		this.getContentView().setOnKeyListener(new OnKeyListener()
		{
			public boolean onKey(View view, int keyCode, KeyEvent event)
			{
				try
				{
					if (event.getAction() == KeyEvent.ACTION_DOWN) return evt.down(getContext(), self, keyCode);
					if (event.getAction() == KeyEvent.ACTION_UP) evt.up(getContext(), self, keyCode);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
				return false;
			}
		});
		return this;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////
	// // 以下为外观相关
	public Element setBackgroundColor(int color)
	{
		this.getContentView().setBackgroundColor(color);
		return this;
	}

	public Element setBorderWidth(int top, int right, int bottom, int left)
	{
		return this;
	}

	public Element setBorderColor(int top, int right, int bottom, int left)
	{
		return this;
	}

	public Element setBorderColor(int color)
	{
		return this;
	}

	public Element setBorderWidth(int size)
	{
		return this;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////
	// // 以下为布局相关

	// WRAP_CONTENT -2
	// FILL_PARENT -1
	protected int width = -2;
	protected int height = -2;

	protected float widthPercent = -1.0f;
	protected float heightPercent = -1.0f;
	protected int rowAlign = Attribute.ALIGN_MIDDLE;

	private int verticalAlign = FlowLayout.LayoutParams.ALIGN_TOP;
	private int horizontalAlign = FlowLayout.LayoutParams.ALIGN_LEFT;

	// 宽高
	public final int getWidthDip()
	{
		if (this.width >= 0) return Resolution.dip(this.width);
		else return this.width;
	}

	public final int getHeightDip()
	{
		if (this.height >= 0) return Resolution.dip(this.height);
		else return this.height;
	}

	public final int getWidth()
	{
		return this.width;
	}

	public final int getHeight()
	{
		return this.height;
	}
	
	public final int getMeasuredWidth()
	{
		return Resolution.dip(this.getContentView().getMeasuredWidth());
	}
	
	public final int getMeasuredHeight()
	{
		return Resolution.dip(this.getContentView().getMeasuredHeight());
	}

	// 外边距
	int[] margins = new int[4];

	public Element setMargin(int top, int right, int bottom, int left)
	{
		margins[0] = Resolution.pixels(top);
		margins[1] = Resolution.pixels(right);
		margins[2] = Resolution.pixels(bottom);
		margins[3] = Resolution.pixels(left);
		this.layoutChanged();
		return this;
	}
	
	public Element setRowAlign(int align)
	{
		this.rowAlign = align;
		this.layoutChanged();
		return this;
	}

	public Element setMarginTop(int top)
	{
		this.setMargin(top, 0, 0, 0);
		return this;
	}

	public Element setMarginBottom(int bottom)
	{
		this.setMargin(0, 0, bottom, 0);
		return this;
	}

	public Element setMarginLeft(int left)
	{
		this.setMargin(0, 0, 0, left);
		return this;
	}

	public Element setMarginRight(int right)
	{
		this.setMargin(0, right, 0, 0);
		return this;
	}

	public Element setMargin(int margin)
	{
		this.setMargin(margin, margin, margin, margin);
		return this;
	}

	public Element setMargin(int topBottom, int leftRight)
	{
		this.setMargin(topBottom, leftRight, topBottom, leftRight);
		return this;
	}
	
	public Element setWidth(int width)
	{
		if (width >= 0) this.width = Resolution.pixels(width);
		else this.width = width;
		this.widthPercent = -1.0f;
		this.layoutChanged();
		return this;
	}

	public Element setHeight(int height)
	{
		if (height >= 0) this.height = Resolution.pixels(height);
		else this.height = height;
		this.heightPercent = -1.0f;
		this.layoutChanged();
		return this;
	}

	public Element setWidthPercent(float percent)
	{
		this.width = -2;
		this.widthPercent = percent;
		this.layoutChanged();
		return this;
	}

	public Element setHeightPercent(float percent)
	{
		this.height = -2;
		this.heightPercent = percent;
		this.layoutChanged();
		return this;
	}

	public Element setValign(int align)
	{
		this.verticalAlign = align;
		this.layoutChanged();
		return this;
	}

	public Element setHalign(int align)
	{
		this.horizontalAlign = align;
		this.layoutChanged();
		return this;
	}

	public Element setAlign(int hAlign, int vAlign)
	{
		this.setValign(vAlign);
		this.setHalign(hAlign);
		this.layoutChanged();
		return this;
	}

	public Element show()
	{
		this.getWrapperView().setVisibility(VI_SHOWN);
		return this;
	}

	public Element hide()
	{
		// 让自身及子元素的图片可回收
		this.getWrapperView().setVisibility(VI_HIDDEN);
		this.disposeImages();
		return this;
	}

	public Element vanish()
	{
		// 让自身及子元素的图片可回收
		this.getWrapperView().setVisibility(VI_VANISHED);
		this.disposeImages();
		return this;
	}

	public Element setDisplay(String mode)
	{
		// 让自身及子元素的图片可回收
		if ("hidden".equalsIgnoreCase(mode))
		{
			this.hide();
			this.disposeImages();
		}
		if ("shown".equalsIgnoreCase(mode)) this.show();
		if ("none".equalsIgnoreCase(mode))
		{
			this.vanish();
			this.disposeImages();
		}
		return this;
	}
	
	// 标记所有的子元素里的图片元素为可回收状态
	private final void disposeImages()
	{
		LinkedList<Element> childs = this.getChildren();
		for (int i = 0; i < childs.size(); i++)
		{
			childs.get(i).disposeImages();
		}
		if (!(this instanceof Image)) return;
		Image img = (Image)this;
		// markRecycable();
		ImageCache.getInstance().dispose(img.getContentView(), img.getSrc());
	}

	public int getVisibility()
	{
		return this.getWrapperView().getVisibility();
	}

	public Element setPadding(int top, int right, int bottom, int left)
	{
		this.getContentView().setPadding(Resolution.pixels(left), Resolution.pixels(top), Resolution.pixels(right), Resolution.pixels(bottom));
		return this;
	}

	public Element setPadding(int topBottom, int leftRight)
	{
		return this.setPadding(topBottom, leftRight, topBottom, leftRight);
	}

	public Element setPaddingTop(int top)
	{
		this.setPadding(top, 0, 0, 0);
		return this;
	}

	public Element setPaddingBottom(int bottom)
	{
		this.setPadding(0, 0, bottom, 0);
		return this;
	}

	public Element setPaddingLeft(int left)
	{
		this.setPadding(0, 0, 0, left);
		return this;
	}

	public Element setPaddingRight(int right)
	{
		this.setPadding(0, right, 0, 0);
		return this;
	}

	public Element setPadding(int padding)
	{
		return this.setPadding(padding, padding, padding, padding);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private int top = -100000;
	private int right = -100000;
	private int bottom = -100000;
	private int left = -100000;
	private int zIndex = 1;
	
	public final int getTop()
	{
		return Resolution.dip(this.top);
	}

	public final int getLeft()
	{
		return Resolution.dip(this.left);
	}
	
	public final int getRight()
	{
		return Resolution.dip(this.right);
	}
	
	public final int getBottom()
	{
		return Resolution.dip(this.bottom);
	}
	
	public final int getZIndex()
	{
		return this.zIndex;
	}
	
	public final Element setZIndex(int index)
	{
		this.zIndex = index;
		this.layoutChanged();
		return this;
	}
	
	public final Element setTop(int top)
	{
		if (top > Attribute.POSITION_UNSPECIFIED) this.top = Resolution.pixels(top);
		else this.top = top;
		this.layoutChanged();
		return this;
	}

	public final Element setLeft(int left)
	{
		if (left > Attribute.POSITION_UNSPECIFIED) this.left = Resolution.pixels(left);
		else this.left = left;
		this.layoutChanged();
		return this;
	}
	
	public final Element setRight(int right)
	{
		if (right > Attribute.POSITION_UNSPECIFIED) this.right = Resolution.pixels(right);
		else this.right = right;
		this.layoutChanged();
		return this;
	}
	
	public final Element setBottom(int bottom)
	{
		if (bottom > Attribute.POSITION_UNSPECIFIED) this.bottom = Resolution.pixels(bottom);
		else this.bottom = bottom;
		this.layoutChanged();
		return this;
	}
	
	private final void layoutChanged()
	{
		if (!this.created) return;
		this.getContentView().setLayoutParams(this.getLayout());
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean created = false;
	public ViewGroup.LayoutParams getLayout()
	{
		this.layout = new FlowLayout.LayoutParams(this.getWidth(), this.getHeight());
		this.layout.setWidth(this.widthPercent);																	// 宽高比例
		this.layout.setHeight(this.heightPercent);
		this.layout.setAlign(this.horizontalAlign, this.verticalAlign);											// 对齐
		this.layout.setMargin(this.margins[0], this.margins[1], this.margins[2], this.margins[3]);				// 外间距
		this.layout.setRowAlign(this.rowAlign);
		
		// 定位属性
		if (this.left != Attribute.POSITION_UNSPECIFIED) this.layout.setLeft(this.left);
		if (this.top != Attribute.POSITION_UNSPECIFIED) this.layout.setTop(this.top);
		if (this.bottom != Attribute.POSITION_UNSPECIFIED) this.layout.setBottom(this.bottom);
		if (this.right != Attribute.POSITION_UNSPECIFIED) this.layout.setRight(this.right);
		this.layout.setZIndex(this.zIndex);
		this.created = true;
		return this.layout;
	}
	
	public Object getValue()
	{
		return null;
	}
	
	public boolean isFormElement()
	{
		return false;
	}
	
}
