package me.chris.HotLogger;

import java.sql.Timestamp;
import java.util.UUID;

public class DataEntry
{
	public String action;
	public Timestamp t;
	public UUID p;
	public double x;
	public double y;
	public double z;
	public String data;
	
	public DataEntry(String action, Timestamp t, UUID p, double x, double y, double z, String data)
	{
		super();
		this.action = action;
		this.t = t;
		this.p = p;
		this.x = x;
		this.y = y;
		this.z = z;
		this.data = data;
	}
	
	
}
