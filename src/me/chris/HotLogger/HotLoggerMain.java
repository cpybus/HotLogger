package me.chris.HotLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import me.chris.HotLogger.Commands.CommandHandler;
import me.chris.HotLogger.Listeners.BlockBreak;
import me.chris.HotLogger.Listeners.BlockPlace;
import me.chris.HotLogger.Listeners.Others;
import me.chris.HotLogger.Listeners.PlayerInteract;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HotLoggerMain extends JavaPlugin
{
	@Override
	public void onEnable()
	{

		new Vars(this);
    	
    	try
		{
			firstRun();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
    	
    	try
		{
			loadYamls();
		}
		catch (MyException e)
		{
			Vars.log.log(Level.SEVERE, "[HotLogger] " + e.getString());
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
    	
    	
    	
    	if (!setupPermissions())
		{
			Vars.log.log(Level.SEVERE, "[HotLogger] No Permission found! Disabling plugin!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
    	
		
		getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
		getServer().getPluginManager().registerEvents(new BlockBreak(), this);
		getServer().getPluginManager().registerEvents(new BlockPlace(), this);
		getServer().getPluginManager().registerEvents(new Others(), this);
		
		CommandHandler commandHandler = new CommandHandler();
		getCommand("HotLogger").setExecutor(commandHandler);
		getCommand("hl").setExecutor(commandHandler);
		
		DataManager dm = new DataManager();
		Thread t = new Thread(dm);
		t.start();
				
		Vars.log.log(Level.INFO, "[HotLogger] Version " + Vars.versionNumber);
		Vars.log.log(Level.INFO, "[HotLogger] Started successfully.");		
	}
	
	@Override
	public void onDisable()
	{
		Vars.log.log(Level.INFO, "[HotLogger] Stopped.");	
	}
	
	private void firstRun() throws Exception
	{
		if (!Vars.configFile.exists())
		{
			Vars.log.log(Level.INFO, "[HotLogger] No config.yml file found. Attempting to make one. ");
			Vars.configFile.getParentFile().mkdirs();
			copy(getResource("config.yml"), Vars.configFile);
			Vars.log.log(Level.INFO, "[HotLogger] File Made Successfully ");
		}
		else
		{
			Vars.log.log(Level.INFO, "[HotLogger] Config Found. Using it.  ");
		}
		
		
	}
	
	private void copy(InputStream in, File file)
	{
		try
		{
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadYamls() throws MyException
	{
		try
		{
			Vars.config.load(Vars.configFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		

		Vars.importVariables();
	}
	
	public void saveYamls()
	{
		Vars.exportVariables();
		
		try
		{
			Vars.config.save(Vars.configFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	private Boolean setupPermissions()
	{
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null)
		{
			Vars.perms = permissionProvider.getProvider();
		}
		return (Vars.perms != null);
	}
}
