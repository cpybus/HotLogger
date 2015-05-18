package me.chris.HotLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.entity.Player;

public class QueryThread_Near implements Runnable
{
	ArrayList<String> lines = new ArrayList<String>();
	Player p;
	int radius;
	
	
	public QueryThread_Near(Player p, int radius)
	{
		this.p = p;
		this.radius = radius;
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
			PreparedStatement st = conn.prepareStatement("SELECT action, timestamp, player, data, x, y, z FROM blockPlace WHERE x>=? AND x<=? AND y>=? AND y<=? AND z>=? AND z<=? "
					+ "UNION "
					+ "SELECT action, timestamp, player, data, x, y, z FROM blockBreak WHERE x>=? AND x<=? AND y>=? AND y<=? AND z>=? AND z<=? "
					+ "UNION "
					+ "SELECT action, timestamp, player, data, x, y, z FROM chestAccess WHERE x>=? AND x<=? AND y>=? AND y<=? AND z>=? AND z<=? "
					+ "ORDER BY timestamp");
			
			st.setDouble(1, p.getLocation().getBlockX()-radius);
			st.setDouble(2, p.getLocation().getBlockX()+radius);
			st.setDouble(3, p.getLocation().getBlockY()-radius);
			st.setDouble(4, p.getLocation().getBlockY()+radius);
			st.setDouble(5, p.getLocation().getBlockZ()-radius);
			st.setDouble(6, p.getLocation().getBlockZ()+radius);
			
			st.setDouble(7, p.getLocation().getBlockX()-radius);
			st.setDouble(8, p.getLocation().getBlockX()+radius);
			st.setDouble(9, p.getLocation().getBlockY()-radius);
			st.setDouble(10, p.getLocation().getBlockY()+radius);
			st.setDouble(11, p.getLocation().getBlockZ()-radius);
			st.setDouble(12, p.getLocation().getBlockZ()+radius);
			
			st.setDouble(13, p.getLocation().getBlockX()-radius);
			st.setDouble(14, p.getLocation().getBlockX()+radius);
			st.setDouble(15, p.getLocation().getBlockY()-radius);
			st.setDouble(16, p.getLocation().getBlockY()+radius);
			st.setDouble(17, p.getLocation().getBlockZ()-radius);
			st.setDouble(18, p.getLocation().getBlockZ()+radius);
			
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				String action = rs.getString("action");
				Timestamp t = rs.getTimestamp("timestamp");
				String playerUUID = rs.getString("player");
				String data = rs.getString("data");
				String playerName = "Unknown";
				
				double x = rs.getDouble("x");
				double y = rs.getDouble("y");
				double z = rs.getDouble("z");
				
				PreparedStatement name = conn.prepareStatement("SELECT playerName FROM playerList WHERE playerUUID=?");
				name.setString(1, playerUUID);
				
				ResultSet nameQuery = name.executeQuery();
				if(nameQuery.next())
					playerName = nameQuery.getString("playerName");
				
				
				if(action.equalsIgnoreCase("blockPlace"))
				{
					lines.add("§7" + t + " §8§l: §c" + playerName + " placed " + data.toLowerCase() + " at x=" + x + " y=" + y + " z=" + z);
				}
				else if(action.equalsIgnoreCase("blockBreak"))
				{
					lines.add("§7" + t + " §8§l: §c" + playerName + " broke " + data.toLowerCase() + " at x=" + x + " y=" + y + " z=" + z);
				}
				else if(action.equalsIgnoreCase("chestAccess"))
				{
					lines.add("§7" + t + " §8§l: §c" + playerName + " accessed a chest at x=" + x + " y=" + y + " z=" + z);
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
			p.sendMessage("§a[HotLogger] §cNo records found.");
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
