package me.chris.HotLogger.Commands;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Command_Tool
{
	public static void tool(Player p)
	{
		ItemStack is = new ItemStack(Material.LOG);
		ItemMeta m = is.getItemMeta();
		m.setDisplayName("§6HotLogger");
		is.setItemMeta(m);
		
		if(p.getItemInHand().isSimilar(is))
		{
			p.getInventory().remove(is);
			p.sendMessage("§a[HotLogger] §6Logging tool removed from your inventory.");
			return;
		}
		
		if(p.getInventory().contains(is))
		{
			p.sendMessage("§a[HotLogger] §6You have a logging block elsewhere in your inventory.");
			return;
		}
		
		if(!p.getInventory().getItemInHand().getType().equals(Material.AIR))
		{
			p.sendMessage("§a[HotLogger] §6Select an empty inventory slot and do that command again.");
			return;
		}
		
		p.getInventory().setItem( p.getInventory().getHeldItemSlot(), is);
	}
}
