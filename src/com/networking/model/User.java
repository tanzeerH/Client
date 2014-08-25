package com.networking.model;

public class User {
	
	private int SId;
	private String ip;
	public User(int id,String ip) {
		this.SId=id;
		this.ip=ip;
	}
	public void setSid(int id)
	{
		this.SId=id;
	}
	public void setIp(String ip)
	{
		this.ip=ip;
	}
	public int getSId()
	{
		return this.SId;
	}
	public String getIp()
	{
		return this.ip;
	}

}
