package com.youaix.framework.html;

import java.io.ByteArrayInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class UniversalUtil
{
	public static Document getDOM(Uri uri) throws Exception
	{
		DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder = domfac.newDocumentBuilder();
		return dombuilder.parse(new ByteArrayInputStream(FileSystem.read(uri)));
	}
	
	public static Document getDOM(String path) throws Exception
	{
		DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder = domfac.newDocumentBuilder();
		Document doc=dombuilder.parse(new ByteArrayInputStream(path.getBytes("UTF-8")));
		return doc;
	}
	
	/**
	 * 获得 参数列表的 类型数组
	 * 
	 * @param methodValue
	 *            参数列表的值
	 * @return 类型数组
	 */
	public static Class[] getParameterTypes(Object[] methodValue) {

		Class[] clazz = new Class[methodValue.length];
		for (int i = 0; i < methodValue.length; i++) {
			clazz[i] = getRealClass(methodValue[i]);
		}
		return clazz;
	}

	/**
	 * 获得对应的类
	 * 
	 * @param o
	 * @return
	 */
	private static Class<?> getRealClass(Object o) {
		Class<?> clazz = null;
		if (o instanceof Integer) {
			clazz = Integer.TYPE;
		} else if (o instanceof Float) {
			clazz = Float.TYPE;
		} else {
			clazz = o.getClass();
		}
		return clazz;
	}


	public static String replaceBlank(String str) {
		if(str==null){
			return null;
		}
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher m = p.matcher(str);
		String after = m.replaceAll("");
		return after;
	}
}
