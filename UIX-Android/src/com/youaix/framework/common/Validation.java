package com.youaix.framework.common;
import java.util.ArrayList;

public final class Validation
{
	public static abstract class Rule
	{
		private String message = null;
		public Rule(String msg)
		{
			this.message = msg;
		}
		public abstract boolean check(String value);
		public String getMessage()
		{
			return this.message;
		}
	}
	
	// 不能为空也不能全是空格
	public static class Required extends Rule
	{
		public Required(String message)
		{
			super(message);
		}
		public boolean check(String value)
		{
			if (null == value) return false;
			if ("".equals(value.trim())) return false;
			return true;
		}
	}
	
	// 只能是数字
	public static class NumberOnly extends Rule
	{
		public NumberOnly(String msg)
		{
			super(msg);
		}
		public boolean check(String value)
		{
			return (value.matches("^\\d+$"));
		}
	}
	
	// 只能是日期格式，如：2013-05-04
	public static class DateOnly extends Rule
	{
		public DateOnly(String msg)
		{
			super(msg);
		}
		public boolean check(String value)
		{
			return value.matches("^(\\d{4})\\-(\\d{1,2})\\-(\\d{1,2})$");
		}
	}
	
	// 只能是既定的格式
	public static class FormatOnly extends Rule
	{
		private String format = null;
		public FormatOnly(String regexp, String msg)
		{
			super(msg);
			this.format = regexp;
		}
		
		public boolean check(String value)
		{
			return value.matches(this.format);
		}
	}
	
	// 最长不能超过多少
	public static class MinLength extends Rule
	{
		private int minLength = 0;
		public MinLength(int minLength, String msg)
		{
			super(msg);
			this.minLength = minLength;
		}
		
		public boolean check(String value)
		{
			return value.length() >= this.minLength;
		}
	}
	
	// 最大不能大于多少
	public static class MaxNumber extends Rule
	{
		private int maxNumber;
		public MaxNumber(int max, String msg)
		{
			super(msg);
			this.maxNumber = max;
		}
		
		public boolean check(String value)
		{
			return ((int)Float.parseFloat(value)) < this.maxNumber;
		}
	}
	
	// 最小不能小于多少
	public static class MinNumber extends Rule
	{
		private int minNumber;
		public MinNumber(int min, String msg)
		{
			super(msg);
			this.minNumber = min;
		}
		
		public boolean check(String value)
		{
			return ((int)Float.parseFloat(value)) > this.minNumber;
		}
	}
	
	// 介于多少和多少之间的数值
	public static class RangeAt extends Rule
	{
		private int min, max;
		public RangeAt(int min, int max, String msg)
		{
			super(msg);
			this.min = min;
			this.max = max;
		}
		
		public boolean check(String value)
		{
			if (!value.matches("^\\-?\\d+(\\.\\d+)?$")) return false;
			int val = (int)Float.parseFloat(value);
			return val >= min && val <= max;
		}
	}
	
	// 以什么什么开头
	public static class BeginWith extends Rule
	{
		private String prefix;
		public BeginWith(String prefix, String msg)
		{
			super(msg);
			this.prefix = prefix;
		}
		
		public boolean check(String value)
		{
			return value.startsWith(this.prefix);
		}
	}
	
	// 以什么什么结束
	public static class EndWith extends Rule
	{
		private String suffix;
		public EndWith(String suffix, String msg)
		{
			super(msg);
			this.suffix = suffix;
		}
		
		public boolean check(String value)
		{
			return value.endsWith(suffix);
		}
	}
	
	// 必须包含什么什么
	public static class Include extends Rule
	{
		public String text;
		public Include(String text, String msg)
		{
			super(msg);
			this.text = text;
		}
		
		public boolean check(String value)
		{
			return value.indexOf(this.text) > -1;
		}
	}
	
	// 创建出单个的验证规则
	public static Rule include(String text, String msg) { return new Include(text, msg); }
	public static Rule endWith(String suffix, String msg) { return new EndWith(suffix, msg); }
	public static Rule beginWith(String prefix, String msg) { return new BeginWith(prefix, msg); }
	public static Rule rangeAt(int min, int max, String msg) { return new RangeAt(min, max, msg); }
	public static Rule minNumber(int min, String msg) { return new MinNumber(min, msg); }
	public static Rule maxNumber(int max, String msg) { return new MaxNumber(max, msg); }
	public static Rule minLength(int minLength, String msg) { return new MinLength(minLength, msg); }
	public static Rule formatOnly(String regexp, String msg) { return new FormatOnly(regexp, msg); }
	public static Rule dateOnly(String msg) { return new DateOnly(msg); }
	public static Rule numberOnly(String msg) { return new NumberOnly(msg); }
	public static Rule required(String msg) { return new Required(msg); }
	
	// 创建出一个验证规则数组
	public static ArrayList<Rule> make(Object... args) throws Exception
	{
		ArrayList<Rule> rules = new ArrayList<Rule>();
		for (int i = 0; i < args.length; i++)
			rules.add((Rule)args[i]);
		return rules;
	}
	
	
}