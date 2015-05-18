package me.chris.HotLogger.Listeners;

import me.chris.HotLogger.Vars;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener
{
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void blockBreakEventMonitor(BlockBreakEvent event)
	{
		Vars.addBlockBreak(event.getPlayer().getUniqueId(), event.getBlock());
	}
}
