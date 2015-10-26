package com.youaix.framework.html;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Snippet
{
	private static final Pattern VAR_PATTERNS = Pattern.compile("\\{\\$(\\w+|\\?)\\}");
	protected static final String format(int index, Object item, String templet) throws Exception
	{
		if (templet.indexOf("{$") == -1) return templet;
		StringBuilder sb = new StringBuilder(32);
		Matcher matcher = VAR_PATTERNS.matcher(templet);
		while (matcher.find())
		{
			String var = matcher.group(1);
			if (item instanceof JSONObject) templet = templet.replace("{$" + var + "}", String.valueOf(((JSONObject)item).get(var)));
			else if (item instanceof JSONArray) templet = templet.replace("{$" + var + "}", String.valueOf(((JSONArray)item).get(index)));
			else templet = templet.replace("{$" + var + "}", String.valueOf(item));
		}
		return templet;
	}
	
	protected static final Element foreach(Element parent, Snippet snippet, Object dataArray) throws Exception
	{
	    JSONArray list = (JSONArray)dataArray;
	    for (int i = 0; i < list.length(); i++)
	    {
	        parent.append(snippet.copy(i, list.get(i)));
	    }
	    return parent;
	}
	
	public abstract Element copy(int index, Object item) throws Exception;
}
