package me.chris.HotLogger.Commands;

import me.chris.HotLogger.QueryThread_Near;

import org.bukkit.entity.Player;

public class Command_Near
{
	public static void near(Player p, String radius)
	{
		try
		{
			Integer.parseInt(radius);
		}
		catch(Throwable t)
		{
			p.sendMessage("§a[HotLogger] §cInvalid radius");
			return;
		}
		
		int rad = Integer.parseInt(radius);
		
		QueryThread_Near qt = new QueryThread_Near(p, rad);
		Thread t = new Thread(qt);
		t.start();
		
	}
}
