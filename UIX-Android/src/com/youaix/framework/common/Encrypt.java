package com.youaix.framework.common;

import java.security.MessageDigest;

public class Encrypt
{
	private final static String[] hexDigits = 
		{
			"0", "1", "2", "3", "4", "5", "6", "7",
			"8", "9", "a", "b", "c", "d", "e", "f"
		};
	
    public static byte[] toBase64(byte[] data)
    {
        return null;
    }

    public static byte[] fromBase64(byte[] data)
    {
        return null;
    }

    public static String MD5(byte[] data)
    {
        return null;
    }
    
    public static String MD5(String source)
    {
    	String resultString = null;

    	try
    	{
    		resultString = new String(source);
    		MessageDigest md = MessageDigest.getInstance("MD5");
    		resultString = byteArrayToString(md.digest(resultString.getBytes()));
    	}
    	catch (Exception ex)
    	{
    		// ...
    	}
    	return resultString;
    }

    public static String AES(byte[] data)
    {
        return null;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////
    ////
    ////		MD5
    ////
    
    public static String byteArrayToString(byte[] b)
    {
    	StringBuffer resultSb = new StringBuffer();
    	for (int i = 0; i < b.length; i++)
    	{
    		resultSb.append(byteToHexString(b[i]));
    	}
    	return resultSb.toString();
    }
    
    private static String byteToHexString(byte b)
    {
    	int n = b;
    	if (n < 0)
    	{
    		n = 256 + n;
    	}
    	int d1 = n / 16;
    	int d2 = n % 16;
    	return hexDigits[d1] + hexDigits[d2];
    }

}
