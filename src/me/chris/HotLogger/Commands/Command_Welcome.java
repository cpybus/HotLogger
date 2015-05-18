package me.chris.HotLogger.Commands;

import me.chris.HotLogger.Vars;

import org.bukkit.entity.Player;

public class Command_Welcome
{
	public static void welcome(Player p)
	{
		p.sendMessage("§5=====================================================");
		p.sendMessage("§a Welcome to §cHotLogger §aPlugin §9(Version " + Vars.versionNumber + ")");
		p.sendMessage("§a Designed & Programmed by §9Hotshot2162");
		p.sendMessage("§5=====================================================");
	}
}
