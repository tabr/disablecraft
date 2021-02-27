package me.tabr.disablecraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class DCListener implements Listener
	{
	List<String> permDisabledPermanently	= new ArrayList<String>();
	
//	public DCListener(DCMain p)
	public DCListener()
		{
		this.DCLReloadConfig();
		}
	public void DCLReloadConfig()
		{
		this.permDisabledPermanently	= Configs.DCM.config.getStringList("permDisabledPermanently");
		for (int i=0;i<this.permDisabledPermanently.size();i++)
			{
			Log.debug("[DCDebug] found item in config:"+this.permDisabledPermanently.get(i));
			}
		}
    @EventHandler
    public void onCraftItem(CraftItemEvent e)
    	{
        ItemStack item					= e.getRecipe().getResult();
        HumanEntity who					= e.getWhoClicked();
        int itemID						= item.getTypeId();
        byte itemData					= item.getData().getData();
        //TODO: гадство какое-то
        String itemString				= itemID+"."+itemData;
        String itemString1				= itemID+".-1";
        String worldName				= who.getLocation().getWorld().getName();
        boolean cancelAction			= true;
        if (this.permDisabledPermanently.contains(itemID+"::"+itemData) ||
        		this.permDisabledPermanently.contains(itemID+"::-1"))
        	{
        	Log.debug("item disabled permanently!");
        	cancelAction	= true;
        	}
    	else
    		{
    		cancelAction	= false;
    		Log.debug("5.crafting allowed");
    		}

        if (who.hasPermission("disablecraft.enabled."+itemString) ||
        		who.hasPermission("disablecraft.enabled."+itemString1)
        		)
    		{
        	cancelAction	= false;
        	Log.debug("1.crafting allowed");
    		}
        else if (who.hasPermission("disablecraft.disabled."+itemString) ||
        		who.hasPermission("disablecraft.disabled."+itemString1)
        		)
    		{
        	cancelAction	= true;
        	Log.debug("2.crafting disallowed");
    		}
        else if (who.hasPermission("disablecraft."+worldName+".enabled."+itemString) ||
        		who.hasPermission("disablecraft."+worldName+".enabled."+itemString1)
        		)
    		{
        	Log.debug("3.crafting allowed");
        	cancelAction	= false;
    		}
        else if (who.hasPermission("disablecraft."+worldName+".disabled."+itemString) ||
        		who.hasPermission("disablecraft."+worldName+".disabled."+itemString1)
        		)
    		{
        	cancelAction	= true;
        	Log.debug("4.crafting stopped in world "+worldName);
    		}
        if (cancelAction)
        	{
        	e.setCancelled(cancelAction);
        	Log.debug("crafting disallowed");
        	}
        else
        	Log.debug("crafting allowed");
    	}
	}
