/**
 * 
 */
package me.chris.HotLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * @author Christopher Pybus
 * @date Mar 25, 2012
 * @file SimpleChatVariables.java
 * @package me.chris.SimpleChat
 * 
 * @purpose
 */

public class Vars 
{
	public static FileConfiguration						config;
	public static Permission							perms;
	public static Logger								log;
	public static HotLoggerMain							plugin;
	
	public static File									configFile;
	
	public static String								pluginName;
	public static String								versionNumber;
	
	public static String								pluginWithVersion;
	
	public static HashMap<Player, ArrayList<String>>	lines;
	
	public static ArrayList<DataEntry>					queueA;
	public static ArrayList<DataEntry>					queueB;
	
	public static boolean								queueUsage	= true; // true means A is being filled up. //false means B is being filled up
	
	// SQL Connection shit
	// public static Connection con;
	
	// YAML VARIABLES
	public static String								database;
	public static String								hostname;
	public static int									port;
	public static String								username;
	public static String								password;
	
	public Vars(HotLoggerMain plugin)
	{
		Vars.plugin = plugin;
		log = Logger.getLogger("Minecraft");
		
		pluginName = plugin.getDescription().getName();
		versionNumber = plugin.getDescription().getVersion();
		
		queueA = new ArrayList<DataEntry>();
		queueB = new ArrayList<DataEntry>();
		
		pluginWithVersion = pluginName + " " + versionNumber;
		
		lines = new HashMap<Player, ArrayList<String>>();
		
		configFile = new File(plugin.getDataFolder(), "config.yml");
		
		config = new YamlConfiguration();
	}
	
	public static void importVariables() throws MyException
	{
		database = config.getString("database", null);
		hostname = config.getString("hostname", null);
		port = config.getInt("port", -2162);
		username = config.getString("username", null);
		password = config.getString("password", null);
		
		// Check that all fields are properly filled out
		if (database == null || hostname == null || port == -2162 || username == null || password == null)
		{
			throw new MyException("Config file could not be properly read. Please review it and fix.");
		}
		
		Connection testConnection;
		try
		{
			testConnection = getConnection();
		}
		catch(MyException e)
		{
			log.log(Level.SEVERE, "[HotLogger] Error establishing connection with SQL server.");
			log.log(Level.SEVERE, "[HotLogger] " + e.getString());
			throw new MyException(e.getString());
		}
		
		try
		{
			testConnection.createStatement().execute(
					"CREATE TABLE IF NOT EXISTS `blockBreak` (" + "`data_id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT , " + "`action` varchar(255) DEFAULT 'blockBreak',"
							+ "`timestamp` datetime NOT NULL ," + "`player` varchar(36) ," + "`x` double NOT NULL ," + "`y` double NOT NULL ," + "`z` double NOT NULL ,"
							+ "`data` varchar(500) DEFAULT NULL )");
			
			testConnection.createStatement().execute(
					"CREATE TABLE IF NOT EXISTS `blockPlace` (" + "`data_id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT , " + "`action` varchar(255) DEFAULT 'blockPlace',"
							+ "`timestamp` datetime NOT NULL ," + "`player` varchar(36) ," + "`x` double NOT NULL ," + "`y` double NOT NULL ," + "`z` double NOT NULL ,"
							+ "`data` varchar(500) DEFAULT NULL )");
			
			testConnection.createStatement().execute(
					"CREATE TABLE IF NOT EXISTS `chestAccess` (" + "`data_id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT , " + "`action` varchar(255) DEFAULT 'chestAccess',"
							+ "`timestamp` datetime NOT NULL ," + "`player` varchar(36) ," + "`x` double NOT NULL ," + "`y` double NOT NULL ," + "`z` double NOT NULL ,"
							+ "`data` varchar(500) DEFAULT 'Chest' )");
			
			testConnection.createStatement().execute("CREATE TABLE IF NOT EXISTS `playerList` (" + "`playerUUID` varchar(36) PRIMARY KEY," + "`playerName` varchar(16) )");
		}
		catch (SQLException e)
		{
			throw new MyException("Error creating one or more of the SQL tables.");
		}
		
		for (Player p : plugin.getServer().getOnlinePlayers())
		{
			
			try
			{
				testConnection.createStatement().execute(
						"INSERT INTO `playerList` (playerUUID, playerName) VALUES (" + p.getUniqueId() + ", " + p.getName() + ") ON DUPLICATE KEY UPDATE playerName = VALUES (" + p.getName()
								+ ")");
			}
			catch (SQLException e)
			{
				log.log(Level.SEVERE, "[HotLogger] Problem occured while adding a player to the player list");
			}
		}
	}
	
	public static void exportVariables()
	{
		
	}
	
	@SuppressWarnings("deprecation")
	public static void addBlockPlace(UUID p, Block b)
	{
		
		String data = "";
		if (b.getState().getRawData() != 0)
			data = b.getType() + ":" + b.getState().getRawData();
		else
		{
			data = b.getType().toString();
		}
		
		if (queueUsage)
		{
			queueA.add(new DataEntry("blockPlace", new Timestamp(Calendar.getInstance().getTimeInMillis()), p, b.getX(), b.getY(), b.getZ(), data));
		}
		else
		{
			queueB.add(new DataEntry("blockPlace", new Timestamp(Calendar.getInstance().getTimeInMillis()), p, b.getX(), b.getY(), b.getZ(), data));
		}
		
		/*
		try
		{
			PreparedStatement st = con.prepareStatement("INSERT INTO `blockPlace` (timestamp, player, x, y, z, data) " + "VALUES (?,?,?,?,?,?)");
			
			st.setTimestamp(1, new Timestamp(Calendar.getInstance().getTimeInMillis()));
			st.setString(2, p.toString());
			st.setDouble(3, b.getX());
			st.setDouble(4, b.getY());
			st.setDouble(5, b.getZ());
			
			
			String data = "";
			if (b.getState().getRawData() != 0)
				data =  b.getType() + ":" + b.getState().getRawData();
			else
			{
				data = b.getType().toString();
			}
			st.setString(6, data);
			
			st.execute();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, "[HotLogger] Could not add event into database.");
			System.out.print(e);
		}
		*/
	}
	
	@SuppressWarnings("deprecation")
	public static void addBlockBreak(UUID p, Block b)
	{
		String data = "";
		if (b.getState().getRawData() != 0)
			data = b.getType() + ":" + b.getState().getRawData();
		else
		{
			data = b.getType().toString();
		}
		
		if (queueUsage)
		{
			queueA.add(new DataEntry("blockBreak", new Timestamp(Calendar.getInstance().getTimeInMillis()), p, b.getX(), b.getY(), b.getZ(), data));
		}
		else
		{
			queueB.add(new DataEntry("blockBreak", new Timestamp(Calendar.getInstance().getTimeInMillis()), p, b.getX(), b.getY(), b.getZ(), data));
		}
	}
	
	public static void addChestAccess(UUID p, Block b)
	{
		
		if (queueUsage)
		{
			queueA.add(new DataEntry("chestAccess", new Timestamp(Calendar.getInstance().getTimeInMillis()), p, b.getX(), b.getY(), b.getZ(), "chest"));
		}
		else
		{
			queueB.add(new DataEntry("chestAccess", new Timestamp(Calendar.getInstance().getTimeInMillis()), p, b.getX(), b.getY(), b.getZ(), "chest"));
		}
	}
	
	public static void addPlayer(Player p)
	{
		Connection conn;
		try
		{
			conn = getConnection();
		}
		catch(MyException e)
		{
			log.log(Level.SEVERE, "[HotLogger] Error establishing connection with SQL server.");
			log.log(Level.SEVERE, "[HotLogger] " + e.getString());
			return;
		}
		
		try
		{
			PreparedStatement st = conn.prepareStatement("INSERT INTO `playerList` (playerUUID, playerName) VALUES (?,?) ON DUPLICATE KEY UPDATE playerName = (?)");
			
			st.setString(1, p.getUniqueId().toString());
			st.setString(2, p.getName());
			st.setString(3, p.getName());
			
			st.execute();
			
			conn.close();
			
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, "[HotLogger] Problem occured while adding a player to the player list");
			System.out.print(e);
		}
	}
	
	public static Connection getConnection() throws MyException
	{
		try
		{
			return DriverManager.getConnection("jdbc:mysql://" + Vars.hostname + ":" + Vars.port + "/" + Vars.database, Vars.username, Vars.password);
		}
		catch (final SQLException ex)
		{
			throw new MyException(ex.toString());
		}
	}
	
}
