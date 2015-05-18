package me.chris.HotLogger.Listeners;

import me.chris.HotLogger.Vars;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Others implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	public void playerJoinEvent(PlayerJoinEvent event)
	{
		Vars.addPlayer(event.getPlayer());
	}
}
