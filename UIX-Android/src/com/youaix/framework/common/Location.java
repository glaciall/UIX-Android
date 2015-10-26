package com.youaix.framework.common;

public final class Location
{
	private double latitude;
	private double longitude;
	private double altitude;
	private float radius;
	private String address;
	private String province;
	private String city;
	private String district;
	private float direction;
	
	public Location()
	{
		// ..
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public float getRadius()
	{
		return radius;
	}
	
	public boolean hasRadius()
	{
		return this.radius > 0.0f;
	}

	public void setRadius(float radius)
	{
		this.radius = radius;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getProvince()
	{
		return province;
	}

	public void setProvince(String province)
	{
		this.province = province;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getDistrict()
	{
		return district;
	}

	public void setDistrict(String district)
	{
		this.district = district;
	}

	public float getDirection()
	{
		return direction;
	}

	public void setDirection(float direction)
	{
		this.direction = direction;
	}
	
	public String toString()
	{
		return "Latitude: " + this.latitude + ", Longitude: " + this.longitude + ", Radius: " + this.radius + ", Province: " + this.province + ", City: " + this.city;
	}

	public void setAltitude(double altitude)
	{
		this.altitude = altitude;
	}
	
	public double getAltitude()
	{
		return this.altitude;
	}
}
