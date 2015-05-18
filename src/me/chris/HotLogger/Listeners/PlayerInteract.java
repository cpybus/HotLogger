package me.chris.HotLogger.Listeners;

import me.chris.HotLogger.QueryThread_Block;
import me.chris.HotLogger.Vars;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInteract implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerInteractEvent(PlayerInteractEvent event)
	{
		Player p = event.getPlayer();
		
		ItemStack is = new ItemStack(Material.LOG);
		ItemMeta m = is.getItemMeta();
		m.setDisplayName("§6HotLogger");
		is.setItemMeta(m);
		
		if (p.getItemInHand().isSimilar(is))
		{
			event.setCancelled(true);
		}
		else
		{
			return;
		}
		
		//Get logs for the block that was clicked
		if(event.getAction().equals(Action.LEFT_CLICK_BLOCK))
		{
			Block b = event.getClickedBlock();
			
			QueryThread_Block qt = new QueryThread_Block(p, b);
			Thread t = new Thread(qt);
			t.start();
			
			return;
		}
		
		//Get logs for the block that will be placed
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			Block cb = event.getClickedBlock();
			BlockFace b = event.getBlockFace();
			
			Location l = new Location(event.getPlayer().getWorld(), cb.getX() + b.getModX(), cb.getY() + b.getModY(), cb.getZ() + b.getModZ());
			
			
			QueryThread_Block qt = new QueryThread_Block(p, l.getBlock());
		    Thread t = new Thread(qt);
		    t.start();
		}
		
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void playerInteractEventMonitor(PlayerInteractEvent event)
	{
		if (event.hasBlock())
		{
			if (event.getClickedBlock().getType() == Material.CHEST)
			{
				Vars.addChestAccess(event.getPlayer().getUniqueId(), event.getClickedBlock());
			}
		}
	}
}
