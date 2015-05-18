package me.chris.HotLogger.Commands;

import java.util.ArrayList;

import me.chris.HotLogger.Vars;

import org.bukkit.entity.Player;

public class Command_Page
{
	public static void page(Player p, String arg)
	{
		if(!Vars.lines.containsKey(p))
		{
			p.sendMessage("§a[HotLogger] §cNo logger records found for you.");
			return;
		}
		
		try
		{
			Integer.parseInt(arg);
		}
		catch(Throwable t)
		{
			p.sendMessage("§a[HotLogger] §cInvalid page");
			return;
		}
		
		int rad = Integer.parseInt(arg);
		
		if(rad <= 0)
		{
			p.sendMessage("§a[HotLogger] §cInvalid page");
			return;
		}
		
		ArrayList<String> lines = Vars.lines.get(p);
		
		int startIndex = (rad - 1) * 10;
		int endIndex = (rad * 10) - 1;
		
		if(startIndex > (lines.size()-1))
		{
			p.sendMessage("§a[HotLogger] §cInvalid page");
			return;
		}
		
		if(endIndex > (lines.size()-1))
		{
			endIndex = (lines.size()-1);
		}
		
		p.sendMessage("§5======§c [ HotLogger ] §5==================================");
		for(int ind = startIndex; ind <= endIndex; ind++)
		{
			p.sendMessage(lines.get(ind));
		}
		p.sendMessage("§5==============================================§7 Page " + arg);
	}
}
