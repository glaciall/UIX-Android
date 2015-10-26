package com.youaix.framework.html;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.youaix.framework.common.Schema;
import com.youaix.framework.common.Schema.Uri;
import com.youaix.framework.ui.A;
import com.youaix.framework.ui.Browser;
import com.youaix.framework.ui.Button;
import com.youaix.framework.ui.Checkbox;
import com.youaix.framework.ui.Div;
import com.youaix.framework.ui.Element;
import com.youaix.framework.ui.File;
import com.youaix.framework.ui.Gif;
import com.youaix.framework.ui.Hidden;
import com.youaix.framework.ui.Hr;
import com.youaix.framework.ui.Image;
import com.youaix.framework.ui.Input;
import com.youaix.framework.ui.Option;
import com.youaix.framework.ui.Password;
import com.youaix.framework.ui.ProgressBar;
import com.youaix.framework.ui.Radiobox;
import com.youaix.framework.ui.Radiogroup;
import com.youaix.framework.ui.Select;
import com.youaix.framework.ui.Shape;
import com.youaix.framework.ui.Slideshow;
import com.youaix.framework.ui.Span;
import com.youaix.framework.ui.Submit;
import com.youaix.framework.ui.Textarea;

public class UIFactory {
	private static String FMT_INTEGER = "(?is)^(bg_(top|middle|bottom|left|center|right)|pos_center|pos_middle|horizontal|vertical|left|center|wrap_content|right|top|middle|bottom|red|blue|green|black|grey|white|fill_rest)|(#[0-9a-f]{6,8})|(-?\\d+(px)?)$";
	private static String FMT_FLOAT = "^\\d+%$";
	private static String FMT_BOOLEAN = "(?is)^(true)|(false)$";

	private static final Pattern VAR_PATTERNS = Pattern.compile("\\{\\$(\\w+|\\?)\\}");

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	////
	////		优化版相关方法
	////
	public static void build(Snippet snippet, JSONArray list, Element parent) throws Exception
	{
		for (int i = 0; i < list.length(); i++)
			parent.append(snippet.copy(i, list.get(i)));
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static Element build(Schema.Uri uri) throws Exception
	{
		return execute(UniversalUtil.getDOM(uri).getDocumentElement(), null, null);
	}

	public static Element build(Uri uri, JSONArray list) throws Exception
	{
		return build(UniversalUtil.getDOM(uri), list, new Div());
	}

	public static Element build(Uri uri, JSONArray list, Element root) throws Exception
	{
		return build(UniversalUtil.getDOM(uri), list, root);
	}

	public static Element build(String html) throws Exception
	{
		return execute(UniversalUtil.getDOM(html).getDocumentElement(), null, null);
	}

	public static Element build(String html, JSONArray list, Element root) throws Exception
	{
		return build(UniversalUtil.getDOM(html), list, root);
	}

	public static Element build(Document doc, JSONArray list, Element root) throws Exception
	{
		for (int i = 0; i < list.length(); i++)
		{
			final Object json = list.get(i);// 当前 json对象
			JSONArray jar = null;
			final int index = i;
			root.append(execute(doc.getDocumentElement(), new Formatter()
			{
				public Object format(String text) throws Exception
				{
					if (text.indexOf("{$") == -1) return text;
					StringBuilder sb = new StringBuilder(32);
					Matcher matcher = VAR_PATTERNS.matcher(text);
					while (matcher.find())
					{
						String var = matcher.group(1);
						if (json instanceof JSONObject) text = text.replace("{$" + var + "}", String.valueOf(((JSONObject)json).get(var)));
						else if (json instanceof JSONArray) text = text.replace("{$" + var + "}", String.valueOf(((JSONArray)json).get(index)));
						else text = text.replace("{$" + var + "}", String.valueOf(json));
					}
					return text;
				}
			}, json));
		}
		return root;
	}

	public static Element execute(Node rootNode, Formatter formatter, Object data) throws Exception
	{
		if (rootNode.getNodeType() == 3)
		{
			return null;
		}
		
		Class tagCls = createElement(rootNode.getNodeName());
		Element node = (Element) tagCls.newInstance();
		// build all the children nodes
		NodeList children = rootNode.getChildNodes();
		for (int i = 0; !rootNode.getNodeName().equals("span")
				&& !rootNode.getNodeName().equals("option")
				&& !rootNode.getNodeName().equals("radiobox")
				&& i < children.getLength(); i++)
		{
			if (rootNode.getNodeType() == 3)
			{
				return null;
			}
			
			if (children.item(i).getNodeName().equals("foreach"))
			{
				NodeList childs = children.item(i).getChildNodes();
				String from = children.item(i).getAttributes().getNamedItem("from").getNodeValue();
				JSONArray newList = (JSONArray)((JSONObject)data).get(from);
				
				Node foreach = null;
				for (int s = 0; s < childs.getLength(); s++)
				{
					if (childs.item(s).getNodeType() != 3)
					{
						foreach = childs.item(s);
						break;
					}
				}
				
				for (int s = 0; s < newList.length(); s++)
				{
					final Object newData = newList.get(s);
					node.append(execute(foreach, new Formatter()
					{
						public Object format(String text) throws Exception
						{
							if (text.indexOf("{$") == -1) return text;
							StringBuilder sb = new StringBuilder(32);
							Matcher matcher = VAR_PATTERNS.matcher(text);
							while (matcher.find())
							{
								String var = matcher.group(1);
								if (newData instanceof JSONObject) text = text.replace("{$" + var + "}", String.valueOf(((JSONObject)newData).get(var)));
								else text = text.replace("{$" + var + "}", String.valueOf(newData));
							}
							return text;
						}
					}, newData));
				}
			}
			else node.append(execute(children.item(i), formatter, data));
		}

		// List attributes = element.getAttributes();
		NamedNodeMap attributes = rootNode.getAttributes();
		ArrayList parameterValues = new ArrayList();

		for (int i = 0; i < attributes.getLength(); i++)
		{
			// 根据属性名，猜方法
			// 根据方法的参数及类型，猜属性值可以改成什么样的

			// Attribute attr = (Attribute)attributes.get(i);
			Node attr = attributes.item(i);

			String attrValue = attr.getNodeValue();

			if (formatter != null)
				attrValue = (String) formatter.format(attrValue);
			Method method = getMethod(tagCls, attr.getNodeName(), attrValue, parameterValues);

			// 需要测试一下，attributes里的顺序是不是跟写在标签里的顺序是否一致，如果是的话，可以不用对scrollable做单独的处理，要求一律写在标签属性的最后一个就可以了

			if (null == method)
			{
				method = tagCls.getMethod("setAttribute", String.class, String.class);
				if (formatter != null) attrValue = (String)formatter.format(attrValue);
				method.invoke(node, attr.getNodeName(), attrValue);
			}
			else
			{
				method.invoke(node, parameterValues.toArray());
			}
		}

		// 處理 span的 text
		if (rootNode.getNodeName().equals("span")
				|| rootNode.getNodeName().equals("option")
				|| rootNode.getNodeName().equals("radiobox")) {
			String text = rootNode.getTextContent().trim();

			if (formatter != null) {
				text = (String) formatter.format(text);
			}
			
			if (text != null && text.length() > 0)
			{
				Method method = tagCls.getMethod("setText", String.class);
				method.invoke(node, text);
			}
		}
		return node;
	}

	public static Method getMethod(Class cls, String attrName,
			String attrValue, ArrayList parameterValues) throws Exception {
		// 先用空格分开attrValue，针对单块去构建参数类型数组

		String[] params = attrValue.split("\\s+");
		Class[] parameterTypes = new Class[params.length];
		parameterValues.clear();
		for (int i = 0; i < params.length; i++)
		{
			String val = params[i];
			if (val.matches(FMT_INTEGER))
			{
				parameterValues.add(getIntValue(val));
				parameterTypes[i] = Integer.TYPE;
			}
			else if (val.matches(FMT_FLOAT))
			{
				parameterValues.add(getFloatValue(val));
				parameterTypes[i] = Float.TYPE;
			}
			else if (val.matches(FMT_BOOLEAN))
			{
				parameterValues.add(getBooleanValue(val));
				parameterTypes[i] = Boolean.TYPE;
			}
			else
			{
				parameterValues.add(val);
				parameterTypes[i] = String.class;
			}
		}

		// 试一下能不能get到method
		attrName = getMethodName(attrName);
		Method method = null;
		try
		{
			method = cls.getMethod(attrName, parameterTypes);
		}
		catch (Exception ex) { }
		if (null != method) return method;

		// 如果不能找到Method呢？试一下，是不是有单String参数的method
		try
		{
			method = cls.getMethod(attrName, String.class);
			parameterValues.clear();
			parameterValues.add(attrValue);
		}
		catch (Exception ex) { }

		// default-text="1px 1px 1px"
		// 如果还找不到，返回null，便于调用setAttribute
		return method;
	}

	private static int getIntValue(String val) throws Exception
	{
		if (val.charAt(0) == '#')
		{
			long clr = Long.parseLong(val.substring(1), 16);
			int color = (int)clr;
			if (val.length() == 7) color = color | 0xff000000;
			return color;
		}
		else if (val.matches("^-?\\d+(px)?$"))
		{
			return (int)Long.parseLong(val.replace("px", ""));
		}
		
		val = val.toLowerCase();

		if (val.equals("wrap_content")) return -2;
		if (val.equals("fill_rest")) return -3;
		if (val.equals("top")) return 1;
		if (val.equals("middle")) return 2;
		if (val.equals("bottom")) return 3;
		if (val.equals("left")) return 4;
		if (val.equals("center")) return 5;
		if (val.equals("right")) return 6;

		if (val.equals("red")) return 0xffff0000;
		if (val.equals("green")) return 0xff00ff00;
		if (val.equals("blue")) return 0xff0000ff;
		if (val.equals("white")) return 0xffffffff;
		if (val.equals("black")) return 0xff000000;
		if (val.equals("gray")) return 0xffcccccc;
		if(val.equals("theme"))return 0x5E5E5E;

		if (val.equals("horizontal")) return 0;
		if (val.equals("vertical")) return 1;
		
		if (val.equals("bg_left")) return -10000001;
		if (val.equals("bg_center")) return -10000002;
		if (val.equals("bg_right")) return -10000003;
		if (val.equals("bg_top")) return -10000004;
		if (val.equals("bg_middle")) return -10000005;
		if (val.equals("bg_right")) return -10000006;
		if (val.equals("pos_center")) return -100001;
		if (val.equals("pos_middle")) return -100002;

		return 0;
	}

	private static float getFloatValue(String val) throws Exception {
		return Float.parseFloat(val.substring(0, val.indexOf('%'))) / 100;
	}

	private static boolean getBooleanValue(String val) throws Exception {
		return val.equalsIgnoreCase("true");
	}

	private static String getMethodName(String attrName) {
		StringBuilder methodName = new StringBuilder("set");
		boolean upperCase = true;
		for (int i = 0, l = attrName.length(); i < l; i++) {
			char ch = attrName.charAt(i);
			if (upperCase) {
				ch = Character.toUpperCase(ch);
				upperCase = false;
			}
			if (ch == '-' || ch == ':')
			{
				upperCase = true;
				continue;
			}
			methodName.append(ch);
		}
		return methodName.toString();
	}

	private static Class<? extends Element> createElement(String tagName) throws Exception
	{
		if ("span".equals(tagName)) return Span.class;
		if ("div".equals(tagName)) return Div.class;
		if ("img".equals(tagName)) return Image.class;
		if ("hr".equals(tagName)) return Hr.class;
		if ("button".equals(tagName)) return Button.class;
		if ("input".equals(tagName)) return Input.class;
		if ("password".equals(tagName)) return Password.class;
		if ("select".equals(tagName)) return Select.class;
		if ("option".equals(tagName)) return Option.class;
		if ("checkbox".equals(tagName)) return Checkbox.class;
		if ("radiogroup".equals(tagName)) return Radiogroup.class;
		if ("radiobox".equals(tagName)) return Radiobox.class;
		if ("gif".equals(tagName)) return Gif.class;
		if ("hidden".equals(tagName)) return Hidden.class;
		if ("file".equals(tagName)) return File.class;
		if ("textarea".equals(tagName)) return Textarea.class;
		if ("submit".equals(tagName)) return Submit.class;
		if ("a".equals(tagName)) return A.class;
		if ("browser".equals(tagName)) return Browser.class;
		if ("slideshow".equals(tagName)) return Slideshow.class;
		if ("progressbar".equals(tagName)) return ProgressBar.class;
		if ("shape".equals(tagName)) return Shape.class;

		throw new Exception("undefined tag: " + tagName);
	}

	interface Formatter {
		public Object format(String text) throws Exception;
	}
}
