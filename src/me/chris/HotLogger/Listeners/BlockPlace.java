package me.chris.HotLogger.Listeners;

import me.chris.HotLogger.Vars;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlace implements Listener
{
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void blockPlaceEventMonitor(BlockPlaceEvent event)
	{
		if(!event.isCancelled())
			Vars.addBlockPlace(event.getPlayer().getUniqueId(),event.getBlock());
	}
}
