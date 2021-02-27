package me.tabr.disablecraft;

/*
 * alpha: no "RP2 Alloy" & "IC2 Furnace" support
 * IC2 and  all other(?) plugin support
 * !!!!!!!!!!!!!!!!!!!!!изменить canwear=>canhave
 * !!!!!!!!!!!!!!!!!!!!!canHave for contrabandScanner
 * IC2 support removed
 * */
//import ic2.api.Ic2Recipes;
//import ic2.core.Ic2Items;
//import ic2.core.item.ItemIC2;

//import ic2.api.Ic2Recipes;
//import ic2.core.Ic2Items;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//import java.util.NoSuchElementException;
import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.inventory.ItemStack;
//import net.minecraft.s;
//import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

//import org.bukkit.inventory.CraftingInventory;
//import org.bukkit.util.Java15Compat;

public class DCMain extends JavaPlugin
	{
	public FileConfiguration config;
	private Set<String> forbidden			= new HashSet<String>();
	private Set<String> forbiddenIC2		= new HashSet<String>();
//	private Map<String, Object> recipes		= new HashMap<String, Object>();
	private Map<String, Recipe> recipes		= new HashMap<String, Recipe>();
	private static final String VERSION		= "0.8.2";//сампл создается с этой версией - лучше не менять =)
	private String configVERSION			= "0";
	private DCContrabandScanner DDC			= new DCContrabandScanner();
	private long contrabandScanTimer		= 0;
//	private DCMain SELF						= this;
	private boolean listenersStarted		= false;
	private DCListener DCL;
	private boolean usePermissions			= false;
	private ItemStacks IS					= new ItemStacks();

	private void CreateSampleConfig()
		{
		List<String> disallow			= Arrays.asList("46::-1");
		List<String> disallowIC2		= Arrays.asList("46::-1");
		this.config.set("LogLevel",		Log.LOGLEVEL_DEFAULT);
		this.config.set("disallow",		disallow);
		this.config.set("disallowIC2",	disallowIC2);
		this.config.set("version",		VERSION);
		this.saveConfig();
		Log.info("Default configuration created.");
		}
	public void onEnable()
		{
		Configs.DCM					= this;//т.к. объекты передаются по ссылке, вс равно где это делать
		boolean RPDetected			= false;
		boolean IC2Detected			= false;
		boolean ForgeOreDetected	= false;
		long start					= System.currentTimeMillis();
		this.config					= this.getConfig();
		Log.info("Rework of NoCraft[ic2 compatible]");
		this.IS.init();
//		ItemStacks.reloadConfig();
		try
			{
 			Class.forName("eloraam.core.CraftLib");
			RPDetected	= true;
			Log.info("Redpower detected");
			}
		catch (ClassNotFoundException ex)
			{
			Log.info("Redpower not found");
			RPDetected	= false;
			}
		/*try
			{
			CraftLib.alloyRecipes.iterator();
			}
		catch (NoClassDefFoundError e)
			{
			Log.info("No Rp2");
			RPDetected	= false;
			}
		catch (IllegalAccessError e)
			{
			Log.info("Can't access Redpower alloy recipes - probably old version!");
			RPDetected	= false;
			}*/
		RPDetected	= false;
		try
			{
 			Class.forName("ic2.api.Ic2Recipes");
			IC2Detected	= true;
			Log.info("IC2 detected");
			}
		catch (ClassNotFoundException ex)
			{
			Log.info("IC2 not found");
			IC2Detected	= false;
			}
		try
			{
			Class.forName("forge.oredict.ShapedOreRecipe");
			ForgeOreDetected	= true;
			Log.info("ForgeOre detected");
			}
		catch (ClassNotFoundException ex)
			{
			Log.info("ForgeOre not found");
			ForgeOreDetected	= false;
			}
		if (!(config.isSet("disallow")) && !(config.isSet("LogLevel")))
			{
			CreateSampleConfig();
			}

		if (!(config.isSet("version")))
			{
			this.config.set("version",		"0.7");
			this.saveConfig();
			}
		this.configVERSION	= this.config.getString("version");
		switch (this.config.getString("version"))
			{
			case "0.6.1":
			case "0.6.2":
			case "0.7":
			case "0.8":
			case "0.8.1":
			case "0.8.2":
			ArrayList<String> contrabandList = new ArrayList<String>();
			contrabandList.add("46::0");
			this.config.set("contrabandList", contrabandList);
			
			this.config.set("contrabandScanTimer", 0);
			Log.info("config updated from '"+this.configVERSION+"' to 0.9.1");
			this.configVERSION	= "0.9";
			case "0.9":
			case "0.9.1":
				ArrayList<String> permDisabledPermanently = new ArrayList<String>();
				permDisabledPermanently.add("46::0");
				this.config.set("permDisabledPermanently", permDisabledPermanently);
				this.config.set("version", "0.10");
				this.saveConfig();
				Log.info("config updated from '"+this.configVERSION+"' to 0.10");
//				this.configVERSION	= this.config.getString("version");
				this.configVERSION	= "0.10";
			case "0.10":
			case "0.10.1":
			case "0.10.2":
				this.usePermissions	= true;	
				this.config.set("usePermissions", this.usePermissions);
				this.config.set("version", "0.11");
				this.saveConfig();
				Log.info("config updated from '"+this.configVERSION+"' to 0.11");
//				this.configVERSION	= this.config.getString("version");
				this.configVERSION	= "0.11";
			break;
			case "0.11":
			case "0.11.1":
				//if (ItemStacks.config == null || ItemStacks.configFile == null)
				/*
				if (this.IS.isNotInit())
					{
					//Log.warning("can't save config file is null");
					throw new Exception("can't save config file is null");
					return;
					}
				*/
				try
					{
					/*
					if (ItemStacks.config == null)
						{
						ItemStacks.reloadConfig();
						}
					*/
					this.IS.reloadConfig();
					ArrayList<String> ItemStacksTmp = new ArrayList<String>();
					ItemStacksTmp.add("0::0");
					this.IS.getConfig().set("is1",ItemStacksTmp);
					
					this.IS.save();
					}
				catch (IOException e)
					{
					Log.error("Could not save config to ["+this.IS.getConfigFile()+"]:"+e);
					break;
					}

				ArrayList<String> disallowRP2alloy = new ArrayList<String>();
				disallowRP2alloy.add("is1");
				this.config.set("disallowRP2alloy", disallowRP2alloy);
				this.config.set("version", "0.12");
				this.saveConfig();

				Log.info("config updated from '"+this.configVERSION+"' to 0.12");
//				log.info("DEBUG: ok");
			break;
			case "0.12":
			case "0.12.1":
			case "0.12.2":
			case "1.0":
			case "1.0.a":
			case "1.a":
			case "1.b":
			case "1.1":
				//текущая версия
			break;
			default:
				Log.warning("WARNING! Unknown config version:'"+this.configVERSION+"'");
			break;
			}
		
		Log.logLevel					= (byte) this.config.getInt("LogLevel");
		this.usePermissions				= this.config.getBoolean("usePermissions");
		
		List<String> disallow			= this.config.getStringList("disallow");
		List<String> disallowIC2		= this.config.getStringList("disallowIC2");
		List<String> disallowRP2alloy	= this.config.getStringList("disallowRP2alloy");
		boolean NeedConfigSave			= false;

		for (int i=0;i<disallow.size();i++)
			{
			String tmpStr	= disallow.get(i).toString();
//			this.Log(DCMain.LOGLEVEL_WARNING,"[DC][disable ME] processing parameter "+tmpStr);
			if (!(tmpStr.contains(":")))
				{
				Log.warning("trying to fix broken config parameter ["+tmpStr+"] to ["+tmpStr+"::-1]");
				disallow.set(i, tmpStr+"::-1");
				NeedConfigSave=true;
				}
			else if (!(tmpStr.contains("::")) && tmpStr.contains(":"))
				{
				String newParameter	= tmpStr.replace(":", "::");
				Log.warning("trying to fix config parameter ["+tmpStr+"] to ["+newParameter+"]");
				disallow.set(i, newParameter);
				NeedConfigSave=true;
				}
			}
		for (int i=0;i<disallowIC2.size();i++)
			{
			String tmpStr	= disallowIC2.get(i).toString();
			if (!(tmpStr.contains(":")))
				{
				Log.warning("trying to fix config parameter ["+tmpStr+"] to ["+tmpStr+"::-1]");
				disallowIC2.set(i, tmpStr+"::-1");
				NeedConfigSave=true;
				}
			else if (!(tmpStr.contains("::")) && tmpStr.contains(":"))
				{
				String newParameter	= tmpStr.replace(":", "::");
				Log.warning("trying to fix config parameter ["+tmpStr+"] to ["+newParameter+"]");
				disallowIC2.set(i, newParameter);
				NeedConfigSave=true;
				}
			}
		if (NeedConfigSave)
			{
			Log.debug("saving config");
			this.config.set("disallow", disallow);
			this.config.set("disallowIC2", disallowIC2);
			saveConfig();
			}
		int itemID;
		byte itemData;
		List l;
		this.forbidden			= new HashSet<String>(disallow);
		this.forbiddenIC2		= new HashSet<String>(disallowIC2);
//		Iterator<Object> itr 	= CraftingManager.getInstance().getRecipes().iterator();
//		Iterator<Object> itr 	= CraftingManager.getInstance().getRecipies().iterator();
//		Iterator<Recipe> itr = getServer().recipeIterator();
		Iterator<Recipe> itr	= org.bukkit.Bukkit.recipeIterator();
		while (itr.hasNext())
			{
			Recipe item	= itr.next();
//			item.getResult().
			itemID		= item.getResult().getTypeId();
			itemData	= item.getResult().getData().getData();
			Log.debug("[all] Processing item ["+itemID+"::"+itemData+"]...");
			if (this.forbidden.contains(itemID+"::-1") ||  this.forbidden.contains(itemID+"::"+itemData))
				{
				Log.detailed("[all] Disabling item ["+itemID+"::"+itemData+"]");
				itr.remove();
				this.recipes.put(itemID+"::"+itemData, item);
				}
			/*
			Object o	= itr.next();
			if (o instanceof ShapedRecipe)
				{
//				itemID		= ((ShapedRecipes)o).b().id;
//				itemData	= (byte) ((ShapedRecipes)o).b().getData();
				itemID		= ((ShapedRecipe)o).getResult().getTypeId();
				itemData	= ((ShapedRecipe)o).getResult().getData().getData();
				Log.debug("[1][ShapedRecipe] Processing item ["+itemID+"::"+itemData+"]...");
				if (this.forbidden.contains(itemID+"::-1") ||  this.forbidden.contains(itemID+"::"+itemData))
	  				{
					Log.detailed("[1] Disabling item ["+itemID+"::"+itemData+"]");
					itr.remove();
					this.recipes.put(itemID+"::"+itemData, o);
	  				}
				}
			else if (o instanceof ShapelessRecipe)
	  			{
//				itemID		= ((ShapelessRecipes)o).b().id;
//				itemData	= (byte) ((ShapelessRecipes)o).b().getData();
				itemID		= ((ShapelessRecipe)o).getResult().getTypeId();
				itemData	= ((ShapelessRecipe)o).getResult().getData().getData();
				Log.debug("[2][ShapelessRecipe] Processing item ["+itemID+"::"+itemData+"]...");
				if (this.forbidden.contains(itemID+"::-1") || this.forbidden.contains(itemID+"::"+itemData))
	  				{
					Log.detailed("[2] Disabling item ["+itemID+"::"+itemData+"]");
					itr.remove();
					this.recipes.put(itemID+"::"+itemData, o);
	  				}
	  			}
			else if (o instanceof CustomModRecipe)
  				{
				itemID		= ((CustomModRecipe)o).getResult().getTypeId();
				itemData	= (byte) ((CustomModRecipe)o).getResult().getData().getData();
				Log.debug("[3][CustomModRecipe] Processing item ["+itemID+"::"+itemData+"]...");
				if (this.forbidden.contains(itemID+"::-1") || this.forbidden.contains(itemID+"::"+itemData))
  					{
					Log.detailed("[3] Disabling item ["+itemID+"::"+itemData+"]");
					itr.remove();
					this.recipes.put(itemID+"::"+itemData, o);
  					}
  				}
			else if (ForgeOreDetected && o instanceof ShapedOreRecipe)
				{
//				itemID		= ((ShapedOreRecipe)o).b().id;
//				itemData	= (byte) ((ShapedOreRecipe)o).b().getData();
				itemID		= ((ShapedOreRecipe)o).toBukkitRecipe().getResult().getTypeId();
				itemData	= (byte) ((ShapedOreRecipe)o).toBukkitRecipe().getResult().getData().getData();
				Log.debug("[4][ShapedOreRecipe] Processing item ["+itemID+"::"+itemData+"]...");
				if (this.forbidden.contains(itemID+"::-1") || this.forbidden.contains(itemID+"::"+itemData))
	  				{
					Log.detailed("[4] Disabling item ["+itemID+"::"+itemData+"]");
					itr.remove();
					this.recipes.put(itemID+"::"+itemData, o);
	  				}
				
				}
			else if (ForgeOreDetected && o instanceof ShapelessOreRecipe)
				{
//				itemID		= ((ShapelessOreRecipe)o).b().id;
//				itemData	= (byte) ((ShapelessOreRecipe)o).b().getData();
				itemID		= ((ShapelessOreRecipe)o).toBukkitRecipe().getResult().getTypeId();
				itemData	= (byte) ((ShapelessOreRecipe)o).toBukkitRecipe().getResult().getData().getData();
				Log.debug("[5][ShapelessOreRecipe] Processing item ["+itemID+"::"+itemData+"]...");
				if (this.forbidden.contains(itemID+"::-1") || this.forbidden.contains(itemID+"::"+itemData))
  					{
					Log.detailed("[5] Disabling item ["+itemID+"::"+itemData+"]");
					itr.remove();
					this.recipes.put(itemID+"::"+itemData, o);
  					}

				}
			else if (IC2Detected && o instanceof AdvRecipe)
	  			{
				((AdvRecipe)o).b().
				itemID		= ((AdvRecipe)o).b().id;
				itemData	= (byte) ((AdvRecipe)o).b().getData();
				Log.debug("[3] Processing item ["+itemID+"::"+itemData+"]...");
				if (this.forbidden.contains(itemID+"::-1") || this.forbiddenIC2.contains(itemID+"::-1") ||
						this.forbidden.contains(itemID+"::"+itemData) || this.forbiddenIC2.contains(itemID+"::"+itemData)	)
	  				{
					Log.detailed("[3] Disabling item ["+itemID+"::"+itemData+"]");
					itr.remove();
					this.recipes.put(itemID+"::"+itemData, o);
	  				}
	  			}
			else if (IC2Detected && o instanceof AdvShapelessRecipe)
	  			{
				itemID		= ((AdvShapelessRecipe)o).b().id;
				itemData	= (byte) ((AdvShapelessRecipe)o).b().getData();
				Log.debug("[4] Processing item ["+itemID+"::"+itemData+"]...");
				if (this.forbidden.contains(itemID+"::-1") || this.forbiddenIC2.contains(itemID+"::-1") || 
					this.forbidden.contains(itemID+"::"+itemData) || this.forbiddenIC2.contains(itemID+"::"+itemData)	)
	  				{
					Log.detailed("[4] Disabling item ["+itemID+"::"+itemData+"]");
					itr.remove();
					this.recipes.put(itemID+"::"+itemData, o);
	  				}
	  			}
			else
	  			{
				Log.detailed("[n] Unknown instance ["+o.toString()+"]");
	  			}
*/
			}
		Log.debug("Finished process default recipes");
/*
		if (RPDetected) for (itr=CraftLib.alloyRecipes.iterator();itr.hasNext();) //for (int i=0;i<CraftLib.alloyRecipes.size();i++)
			{
			Object localObject		= (List)itr.next();
			Object[] arrayOfObject	= ((List)localObject).toArray();
			ItemStack Item 			= (ItemStack)arrayOfObject[1];
			
//			Log.info("RP: arrayOfObject["+arrayOfObject.length+"]");
			ItemStack[] is1 		= (ItemStack[])arrayOfObject[0];
			for (String DRP2 : disallowRP2alloy)//õå...ïëîõî ýòî...
				{
				List<String> tmp	= ItemStacks.list.get(DRP2);
				if (tmp != null && is1.length == tmp.size())
					{
					boolean found	= false;
					//Log.debug("key:!!!!!!!!!!!!!ok");
					for (int i=0;i<is1.length;i++)//TODO:äà âàùå æîïà
						{
						ItemStack is	= is1[i];
//						Log.info("RP:"+is1[i].id+":"+is1[i].getData());
						Log.debug("checking"+is.id+"::"+is.getData());
						if (tmp.contains(is.id+"::"+is.getData()) || tmp.contains(is.id+"::-1"))
							{
							found=true;
							Log.debug("Item Found!!!");
							}
						else
							{
							found=false;
							break;
							}
						}
					if (found)
						{
						itr.remove();
						Log.detailed("disabling RP2 alloy item:"+is1);
						break;//ïî èäåå....
						}
					}
				}
			
			//ItemStack Item	= (ItemStack) itr.next();
			itemID			= Item.id;
			itemData		= (byte) Item.getData();
			Log.info("[RP:] processing recipe Result["+itemID+"::"+itemData+"]");
//			Log.info("RP:"+CraftLib.alloyRecipes.get(i);
			if (this.forbidden.contains(itemID+"::-1") || this.forbiddenIC2.contains(itemID+"::-1")) 
					{
					itr.remove();
					Log.detailed("[RP:] Disabling item ["+itemID+"::"+itemData+"]");
					}
			}
<--RP2
*/
/*
IC2 ->
*/
/*111
		if (IC2Detected) for (int i = 0;i<3;i++)
			{
			Log.info("1");
			switch (i)
				{
				case 0:
					l = Ic2Recipes.getMaceratorRecipes();
				break;
				case 1:
					l = Ic2Recipes.getCompressorRecipes();
				break;
				default:
					l = Ic2Recipes.getExtractorRecipes();
				}
111*/
			/*
			for (Map.Entry entry : l1)
				{
				if ((((ur)entry.getKey()).a(input)) && (input.a >= ((ur)entry.getKey()).a))
					{
					if (adjustInput) input.a -= ((ur)entry.getKey()).a;
					}
				
				return ((ur)entry.getValue()).l();
				}
*/			
/*111			
			
			Log.info("2");
//			for (itr=l.iterator();itr.hasNext();)
			itr=l.iterator();
//			Iterator tmpI	= l.iterator();
//			Log.info("3");
			List<Map.Entry<ItemStack, ItemStack> > TileEntityCompressor_recipes;
			Iterator tmpI;
			try
				{
				net.minecraft.item.ItemStack zzz	= Ic2Items.airCell;
				Log.info("3");
				TileEntityCompressor_recipes	= (List<Map.Entry<ItemStack, ItemStack> >) Class.forName("ic2.core.block.machine.tileentity.TileEntityCompressor").getField("recipes").get(null);
				Log.info("4");
				tmpI=TileEntityCompressor_recipes.iterator();
				Log.info("5");
				while (tmpI.hasNext())
					{
					Log.info("6");
					Map.Entry entry = (Map.Entry)itr.next();
					Log.info("7");
					Object o		= entry.getKey();
					Log.info("8");
					ItemStack Item	= (ItemStack)o;
					Log.info("9");
					}
				}
			catch (NoSuchElementException e)
				{
				//ïçäö ïðîñòî
				Log.info("ex: "+e);
				continue;
				}
			catch (IllegalAccessException e)
				{
				Log.info("ex: "+e);
				}
			catch (NoSuchFieldException e)
				{
				Log.info("ex: "+e);
				}
			catch (ClassNotFoundException e)
				{
				Log.info("ex: "+e);
				}
111*/
			//			while (itr.hasNext())
			/*
			while (tmpI.hasNext())
				{
//				ItemStack aaa;
				Log.info("5");
				Map.Entry entry = (Map.Entry)itr.next();
				Log.info("6");
//				ur aaa	= (ur) entry.getValue();
//				Log.info("7");
//				ur Item	= (ur)entry.getKey();
				Log.info("8");
				if (true) return;
				itemID			= 0;
				itemData		= 0;
//				Recipe r		= itr.next();
//				Recipe r		= (Recipe) tmpI.next();
				tmpI.next();
				Map.Entry entry = (Map.Entry)tmpI.next();
				Log.info("6");
				ItemStack Item	= (ItemStack)entry.getValue();
				Log.info("7");
				itemID			= Item.getTypeId();
				Log.info("8");
				itemData		= (byte) Item.getData().getData();
				Log.info("9");
//				Map.Entry entry = (Map.Entry)itr.next();
//				ItemStack Item	= (ItemStack)entry.getValue();
//				itemID			= Item.getTypeId();
//				itemData		= (byte) Item.getData().getData();
				itemID			= r.getResult().getTypeId();
				Log.info("7");
				itemData		= (byte) r.getResult().getData().getData();
				Log.info("8");
				Log.debug("[u] Processing item ["+itemID+"::"+itemData+"]");
				if (this.forbidden.contains(itemID+"::-1") || this.forbiddenIC2.contains(itemID+"::-1") || 
					this.forbidden.contains(itemID+"::"+itemData) || this.forbiddenIC2.contains(itemID+"::"+itemData)	)
					{
					itr.remove();
					Log.detailed("[u] Warning! Can't undonde this disable (for now:) )");
					Log.detailed("[5:"+i+"] Disabling item ["+itemID+"::"+itemData+"]");
					}
				}
				*/
/*111
			}

		Log.info(this.recipes.size() + " of " + (this.forbidden.size()+this.forbiddenIC2.size()) + "(counter may lie) recipes disabled.");
111*/
		/*
<- IC2
*/
		if (usePermissions && !listenersStarted)
			{
			Log.detailed("starting permissions support system =)");
			this.DCL					= new DCListener();
			getServer().getPluginManager().registerEvents(this.DCL, this);//интересно, при "/dc reload" перечитает ли конфиг...
			Log.detailed("permissions listener started");
			listenersStarted=true;
			}
		if (!DCContrabandScanner.started)
			{
			this.contrabandScanTimer	= this.config.getLong("contrabandScanTimer")*20;
			if (this.contrabandScanTimer > 0)
				{
				this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable()
					{
					public void run()
						{
						DDC.scan();
						}
					},100L,this.contrabandScanTimer);
				DCContrabandScanner.started	= true;
				Log.info("Contraband scanner started for every "+this.contrabandScanTimer+" ticks");
				}
			}

		Log.info("init time: ["+(System.currentTimeMillis()-start)+"] ms");
		}

	public void onDisable()
		{
		Log.info("Warning! restore deleted recipes is not implemented!");
		//return IC2 alloy recipes
		//return RP2 alloy recipes
//		CraftingManager.getInstance().recipies.addAll(this.recipes.values());
//		CraftingManager.getInstance().recipes.addAll(this.recipes.values());//TODO: 
		this.recipes.clear();
		}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
		{
		if (args.length<1)
			{
			sender.sendMessage("/"+cmd.getName()+" reload");
			return true;
			}
		if (args[0].equalsIgnoreCase("reload"))
			{
			if (!sender.hasPermission("disablecraft.command.reload"))
				{
				sender.sendMessage("access denied");
				return true;
				}
			this.onDisable();
			this.onEnable();
			this.DCL.DCLReloadConfig();
			return true;
			}
		else if (args[0].equalsIgnoreCase("test"))
		{
		// Class.forName("ic2.api.Ic2Recipes.this");
		try
			{
			// ic2.api.Ic2Recipes.this.
			Class.forName("ic2.api.Ic2Recipes");
			Log.info("IC2 detected");
			}
		catch (ClassNotFoundException ex)
			{
			Log.info("IC2 not found"+ex);
			}

		}
		return true;
		}
	}

