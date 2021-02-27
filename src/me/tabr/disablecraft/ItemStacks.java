package me.tabr.disablecraft;

import java.io.File;
import java.io.IOException;
//import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ItemStacks
	{
	private FileConfiguration config				= null;
	private File configFile							= null;
	public final String configFileName				= "ItemStacks.yml";
//	public static HashMap<String,ArrayList<String>> list	= new HashMap<String,ArrayList<String>>();
	private HashMap<String,List<String>> ItemStacksList	= new HashMap<String,List<String>>();
	
	public void save() throws IOException
		{
		this.config.save(this.getConfigFile());
		}
	
	public boolean isNotInit()
		{
		return (this.config == null) || (this.configFile == null);
		}
	public File getConfigFile()
		{
		if (configFile == null)
			{
			configFile	= new File (Configs.DCM.getDataFolder(), this.configFileName);
			}
		return configFile;
		}

	public void init()
		{
		this.reloadConfig();
		Set<String> keys		= this.config.getKeys(true);
		Iterator<String> itr	= keys.iterator();
		while (itr.hasNext())
			{
			String key			= itr.next();
			List<String> tmp	= this.config.getStringList(key);
			this.ItemStacksList.put(key, tmp);
			Log.debug("key:"+key+"="+tmp);
			}
		}
	public void reloadConfig()
		{
		if (Configs.DCM == null)
			{
			Log.warning("Configs is not init!");
			return;
			}
		this.config			= YamlConfiguration.loadConfiguration(getConfigFile());
		//this.config.setDefaults(config);//??!
		/*
		InputStream defConfigStream = Configs.DCM.getResource(configFileName);
		if (defConfigStream != null)
			{
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			ItemStacks.config.setDefaults(defConfig);
			}
		*/
		}
	public FileConfiguration getConfig()//спорная необходимость....
		{
		if (this.config == null)
			{
			this.reloadConfig();
			}
		return this.config;
		}
	/*
	public static void saveConfig()
		{
		if (ItemStacks.config == null || ItemStacks.configFile == null)
			{
			Log.warning("can't save config file is null");
			return;
			}
		try
			{
			ItemStacks.getConfig().save(ItemStacks.configFile);
			}
		catch (IOException e)
			{
			Log.error("Could not save config to ["+ItemStacks.configFile+"]:"+e);
			}
		}*/
	}
