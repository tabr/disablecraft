package me.tabr.disablecraft;

import java.util.List;
import java.util.ListIterator;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class DCContrabandScanner
	{
	static public boolean started=false;
//	public int scan (DCMain plugin)
	public int scan ()
		{
		Log.info("[DDCS] scan started");
		//List<Player> players_online = Configs.DCM.getServer().getOnlinePlayers();
		List<String> contrabandList	= Configs.DCM.config.getStringList("contrabandList");//TODO может, сделать статическим?
		int found					= 0;
		int itemID					= 0;
		byte itemData				= 0;
		//for (int i=0; i<players.length; i++)
		for (Player player : Configs.DCM.getServer().getOnlinePlayers())
			{
			Player p						= player;
			PlayerInventory inv				= p.getInventory();
			ListIterator<ItemStack> iter	= inv.iterator();
			ItemStack item;
			//перебираем ¬≈—№ инвентарь, не забываем про армор
			while (iter.hasNext())
				{
				item	= iter.next();
				try
					{
					itemID		= item.getTypeId();
					}
				catch (NullPointerException e)
					{
					continue;//всЄ норм, просто итема не существует
					}
				itemData	= item.getData().getData();
//				Log.debug("[DDCS] procesing item ["+itemID+":"+itemData+"]");
				//TODO: 2!!! одинаковых блока!!!!
				if (contrabandList.contains(itemID+"::"+itemData) || contrabandList.contains(itemID+"::-1"))
					{
					Log.debug("DDCS] Player ["+p.getName()+"] has contraband item ["+itemID+":"+itemData+"]");
					if (!p.hasPermission("disablecraft.canwear."+itemID+"."+itemData) && !p.hasPermission("disablecraft.canwear."+itemID+".-1"))
						{
						inv.removeItem(item);
						Log.info("[DDCS] Player ["+p.getName()+"]: contraband item found and removed ["+itemID+":"+itemData+"]");
						}
					}
				else
					{
					Log.debug("[DDCS] item ["+itemID+":"+itemData+"] is not disabled");
					}
				}
			//->сраный хак:
			for (int j=0;j<4;j++)
				{
				switch (j)
					{
					case 0:
						item	= inv.getBoots();
						break;
					case 1:
						item	= inv.getChestplate();
						break;
					case 2:
						item	= inv.getHelmet();
						break;
					case 3:
						item	= inv.getLeggings();
						break;
					default:
						continue;//хмм....
					}
				
				try
					{
					itemID		= item.getTypeId();
					}
				catch (NullPointerException e)
					{
					continue;
					//всЄ норм, просто итема не существует
					}
				itemData	= item.getData().getData();
				if (contrabandList.contains(itemID+"::"+itemData) || contrabandList.contains(itemID+"::-1"))
					if (!p.hasPermission("disablecraft.canwear."+itemID+"."+itemData) && !p.hasPermission("disablecraft.canwear."+itemID+".-1"))
					{
					inv.removeItem(item);
					switch (j)
						{
						case 0:
							inv.setBoots(null);
							break;
						case 1:
							inv.setChestplate(null);
							break;
						case 2:
							inv.setHelmet(null);
							break;
						case 3:
							inv.setLeggings(null);
							break;
						default:
							continue;//хмм....
						}
					Log.info("[DC][DDCS] Player ["+player.getName()+"]: contraband item found and removed ["+itemID+":"+itemData+"]");
					}
				else
					{
					Log.debug("[DDCS] item ["+itemID+":"+itemData+"] is not disabled");
					}
				}
			}
		return found;//TODO выводить количество
		}
	}
