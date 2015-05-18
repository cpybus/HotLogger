package me.chris.HotLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class QueryThread_Block implements Runnable
{
	ArrayList<String> lines = new ArrayList<String>();
	Player p;
	Block b;
	
	
	public QueryThread_Block(Player p, Block b)
	{
		this.p = p;
		this.b = b;
	}
	
	
	@Override
	public void run()
	{
		Connection conn;
		try
		{
			conn = Vars.getConnection();
		}
		catch(MyException e)
		{
			Vars.log.log(Level.SEVERE, "[HotLogger] Error establishing connection with SQL server.");
			Vars.log.log(Level.SEVERE, "[HotLogger] " + e.getString());
			return;
		}
		
		
		try
		{
			PreparedStatement st = conn.prepareStatement("SELECT action, timestamp, player, data FROM blockPlace WHERE x=? and y=? and z = ? "
					+ "UNION "
					+ "SELECT action, timestamp, player, data FROM blockBreak WHERE x=? and y=? and z = ? "
					+ "UNION "
					+ "SELECT action, timestamp, player, data FROM chestAccess WHERE x=? and y=? and z = ? "
					+ "ORDER BY timestamp");
			
			st.setDouble(1, b.getX());
			st.setDouble(2, b.getY());
			st.setDouble(3, b.getZ());
			
			st.setDouble(4, b.getX());
			st.setDouble(5, b.getY());
			st.setDouble(6, b.getZ());
			
			st.setDouble(7, b.getX());
			st.setDouble(8, b.getY());
			st.setDouble(9, b.getZ());
			
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				String action = rs.getString("action");
				Timestamp t = rs.getTimestamp("timestamp");
				String playerUUID = rs.getString("player");
				String data = rs.getString("data");
				String playerName = "Unknown";
				
				PreparedStatement name = conn.prepareStatement("SELECT playerName FROM playerList WHERE playerUUID=?");
				name.setString(1, playerUUID);
				
				ResultSet nameQuery = name.executeQuery();
				if(nameQuery.next())
					playerName = nameQuery.getString("playerName");
				
				
				if(action.equalsIgnoreCase("blockPlace"))
				{
					lines.add("§7" + t + " §8§l: §c" + playerName + " placed " + data.toLowerCase() + " here.");
				}
				else if(action.equalsIgnoreCase("blockBreak"))
				{
					lines.add("§7" + t + " §8§l: §c" + playerName + " broke " + data.toLowerCase() + " here.");
				}
				else if(action.equalsIgnoreCase("chestAccess"))
				{
					lines.add("§7" + t + " §8§l: §c" + playerName + " accessed a chest here.");
				}
			}
		}
		catch (SQLException e)
		{
			Vars.log.log(Level.SEVERE, "" + e);
			p.sendMessage("§a[HotLogger] §cThere was an internal error");
		}
		
		if(lines.isEmpty())
		{
			p.sendMessage("§a[HotLogger] §cNo records found for this block");
			return;
		}
		
		if(lines.size() > 10)
		{
			p.sendMessage("§5======§c [ HotLogger ] §5==================================");
			for(int index = 0; index < 10; index++)
			{
				p.sendMessage(lines.get(index));
			}
			p.sendMessage(" §e" + lines.size() + " records found. Paginate with §c/hl page [num]");
			p.sendMessage("§5==============================================§7 Page 1");
		}
		else
		{
			p.sendMessage("§5====§c [ HotLogger ] §5===============================");
			for(String l : lines)
			{
				p.sendMessage(l);
			}
			p.sendMessage("§5=====================================================");
		}
		
		Vars.lines.put(p, lines);
	}
	
}
