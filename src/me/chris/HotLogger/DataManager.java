package me.chris.HotLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class DataManager implements Runnable
{
	@Override
	public void run()
	{
		do
		{
			try
			{
				Thread.sleep(30000);
			}
			catch(Throwable t)
			{
				Vars.log.log(Level.SEVERE, "[HotLogger] Problem occured while waiting 30 seconds between DB calls.");
				continue;
			}
			
			Connection conn;
			try
			{
				conn = Vars.getConnection();
			}
			catch(MyException e)
			{
				Vars.log.log(Level.SEVERE, "[HotLogger] Error establishing connection with SQL server.");
				Vars.log.log(Level.SEVERE, "[HotLogger] " + e.getString());
				continue;
			}
			
			try
			{
				conn.setAutoCommit(false);
				
				PreparedStatement bb = conn.prepareStatement("INSERT INTO `blockBreak` (timestamp, player, x, y, z, data) " + "VALUES (?,?,?,?,?,?)");
				PreparedStatement bp = conn.prepareStatement("INSERT INTO `blockPlace` (timestamp, player, x, y, z, data) " + "VALUES (?,?,?,?,?,?)");
				PreparedStatement ca = conn.prepareStatement("INSERT INTO `chestAccess` (timestamp, player, x, y, z, data) " + "VALUES (?,?,?,?,?,?)");
				
				if(Vars.queueUsage) //A is being filled up and should be used now.
				{
					Vars.queueUsage = false;
					
					int bbC = 0;
					int bpC = 0;
					int caC = 0;
					
					for (int i = 0; i < Vars.queueA.size(); i++) {
						DataEntry entry = Vars.queueA.get(i);
						
						if(entry.action.equals("blockBreak"))
						{
							bb.setTimestamp(1, entry.t);
							bb.setString(2, entry.p.toString());
							bb.setDouble(3, entry.x);
							bb.setDouble(4, entry.y);
							bb.setDouble(5, entry.z);
							bb.setString(6, entry.data);
							bb.addBatch();
							
							bbC++;
						}
						else if(entry.action.equals("blockPlace"))
						{
							bp.setTimestamp(1, entry.t);
							bp.setString(2, entry.p.toString());
							bp.setDouble(3, entry.x);
							bp.setDouble(4, entry.y);
							bp.setDouble(5, entry.z);
							bp.setString(6, entry.data);
							bp.addBatch();
							
							bpC++;
						}
						else if(entry.action.equals("chestAccess"))
						{
							ca.setTimestamp(1, entry.t);
							ca.setString(2, entry.p.toString());
							ca.setDouble(3, entry.x);
							ca.setDouble(4, entry.y);
							ca.setDouble(5, entry.z);
							ca.setString(6, "Chest");
							ca.addBatch();
							
							caC++;
						}
						else
						{
							Vars.log.log(Level.SEVERE, "[HotLogger] Unknown action: " + entry.action);
							continue;
						}
						
					
						if (bbC % 1000 == 0) bb.executeBatch(); //If the batchsize is divisible by 1000, execute!
						if (bpC % 1000 == 0) bp.executeBatch();
						if (caC % 1000 == 0) ca.executeBatch();
					}
					
					bb.executeBatch();
					bp.executeBatch();
					ca.executeBatch();
					conn.commit();
					conn.setAutoCommit(true); 
					
					Vars.queueA.clear();
				}
				else //B is being filled up and should be used now
				{
					Vars.queueUsage = true;
					
					int bbC = 0;
					int bpC = 0;
					int caC = 0;
					
					for (int i = 0; i < Vars.queueB.size(); i++) {
						DataEntry entry = Vars.queueB.get(i);
						
						if(entry.action.equals("blockBreak"))
						{
							bb.setTimestamp(1, entry.t);
							bb.setString(2, entry.p.toString());
							bb.setDouble(3, entry.x);
							bb.setDouble(4, entry.y);
							bb.setDouble(5, entry.z);
							bb.setString(6, entry.data);
							bb.addBatch();
							
							bbC++;
						}
						else if(entry.action.equals("blockPlace"))
						{
							bp.setTimestamp(1, entry.t);
							bp.setString(2, entry.p.toString());
							bp.setDouble(3, entry.x);
							bp.setDouble(4, entry.y);
							bp.setDouble(5, entry.z);
							bp.setString(6, entry.data);
							bp.addBatch();
							
							bpC++;
						}
						else if(entry.action.equals("chestAccess"))
						{
							ca.setTimestamp(1, entry.t);
							ca.setString(2, entry.p.toString());
							ca.setDouble(3, entry.x);
							ca.setDouble(4, entry.y);
							ca.setDouble(5, entry.z);
							ca.setString(6, entry.data);
							ca.addBatch();
							
							caC++;
						}
						else
						{
							Vars.log.log(Level.SEVERE, "[HotLogger] Unknown action: " + entry.action);
							continue;
						}
						
					
						if (bbC % 1000 == 0) bb.executeBatch(); //If the batchsize is divisible by 1000, execute!
						if (bpC % 1000 == 0) bp.executeBatch();
						if (caC % 1000 == 0) ca.executeBatch();
					}
					
					bb.executeBatch();
					bp.executeBatch();
					ca.executeBatch();
					conn.commit();
					conn.setAutoCommit(true); 
					
					Vars.queueA.clear();
					
					Vars.queueB.clear();
				}
				
				conn.close();
			}
			catch (SQLException e)
			{
				Vars.log.log(Level.SEVERE, "[HotLogger] Problem occured with DB: " + e.toString());
				continue;
			}
			
		}
		while(true);
	}
}
