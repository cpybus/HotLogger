package me.chris.HotLogger;

public class MyException extends Exception
{
	private static final long	serialVersionUID	= 4067764453857480895L;
	private String s;
	public MyException(String s)
	{
		this.s = s;
	}
	
	public String getString()
	{
		return s;
	}
}
