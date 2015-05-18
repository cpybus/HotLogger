package me.chris.HotLogger.Commands;


import me.chris.HotLogger.Vars;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor
{
	
	private static String	noPerms		= "§a[HotLogger] §4You do not have permission for that command.";
	private static String	invalidCmd	= "§a[HotLogger] §4That was not a valid command.";
	
	public boolean onCommand(CommandSender sender, Command cmd, String idk, String[] args)
	{
		
		if ((sender instanceof Player))
		{
			Player p = (Player) sender;
			
			if (args.length == 0)
			{
				if (Vars.perms.has(p, "hotlogger.welcome"))
					Command_Welcome.welcome(p);
				else
					p.sendMessage(noPerms);
			}
			else if (args.length == 1)
			{
				if(args[0].equalsIgnoreCase("tool"))
				{
					if (Vars.perms.has(p, "hotlogger.tool"))
						Command_Tool.tool(p);
					else
						p.sendMessage(noPerms);
				}
				else
				{
					p.sendMessage(invalidCmd);
				}
				
				
			}
			else if (args.length == 2)
			{
				if(args[0].equalsIgnoreCase("near"))
				{
					if (Vars.perms.has(p, "hotlogger.near"))
						Command_Near.near(p, args[1]);
					else
						p.sendMessage(noPerms);
				}
				else if(args[0].equalsIgnoreCase("page"))
				{
					if (Vars.perms.has(p, "hotlogger.page"))
						Command_Page.page(p, args[1]);
					else
						p.sendMessage(noPerms);
				}
				else
				{
					p.sendMessage(invalidCmd);
				}
				
			}
			else
			{
				p.sendMessage(invalidCmd);
			}
		}
		else
		{
			sender.sendMessage("[HotLogger] This plugin does not support console commands.");
		}
		
		return true;
	}
	
}
