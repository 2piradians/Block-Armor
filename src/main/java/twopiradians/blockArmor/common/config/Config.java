package twopiradians.blockArmor.common.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.Maps;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.seteffect.SetEffect;
import twopiradians.blockArmor.jei.BlockArmorJEIPlugin;
import twopiradians.blockArmor.packet.PacketSyncConfig;

public class Config 
{
	public static Configuration config;

	public static final String SET_EFFECTS_CATEGORY = "config.setEffects"; 
	public static final String ARMOR_SETS_CATEGORY = "config.armorSets";
	/**Version of this config - if loaded version is less than this, delete the config*/
	private static final float CONFIG_VERSION = 2.4F;

	/**How many pieces of armor must be worn to activate set effects*/
	public static int piecesForSet;
	/**Classes of any set effects that are disabled in config*/
	public static ArrayList<Class> disabledSetEffects;
	/**Should disabled items be registered*/
	public static boolean registerDisabledItems;
	/**Sets that should not have items generated - only used by ModItems.postInit()*/
	public static ArrayList<ArmorSet> disabledSets;
	/**Should set effects use durability*/
	public static boolean effectsUseDurability;
	
	/**Map of lowercase modid to mod name*/
	private static HashMap<String, String> modList;

	public static void postInit(final File file) 
	{
		Config.config = new Configuration(file, String.valueOf(CONFIG_VERSION));
		Config.config.load();

		//create modList
		modList = Maps.newHashMap();
		for (String modid : Loader.instance().getIndexedModList().keySet())
			modList.put(modid.toLowerCase(), Loader.instance().getIndexedModList().get(modid).getName());
		if (!modList.containsKey("minecraft"))
			modList.put("minecraft", "Minecraft");
		
		//If loaded version < CONFIG_VERSION, delete it
		String version = Config.config.getLoadedConfigVersion();
		if (version == null || Float.parseFloat(version) < CONFIG_VERSION) {
			for (String category : Config.config.getCategoryNames())
				Config.config.removeCategory(Config.config.getCategory(category));
			BlockArmor.logger.warn("Deleted config from older version");
		}

		//Register disabled items for ModItems.postInit()
		Property prop = getRegisterDisableItemsProp();
		Config.registerDisabledItems = prop.getBoolean();

		//Get disabled armor sets for ModItems.postInit()
		disabledSets = new ArrayList<ArmorSet>();
		if (!Config.registerDisabledItems) {
			Config.config.getCategory(ARMOR_SETS_CATEGORY).setComment("Enable or disable armor sets.");
			for (ArmorSet set : ArmorSet.allSets) {
				String name = modList.get(set.modid) == null ? "???" : modList.get(set.modid);
				ConfigCategory category = Config.config.getCategory(ARMOR_SETS_CATEGORY+"."+name.replace(".", ","));
				category.setComment("Enable or disable armor made from "+name+" blocks.");
				prop = getArmorSetProp(name, set);
				if (!prop.getBoolean()) 
					disabledSets.add(set);
			}
		}
	}

	public static void syncConfig() 
	{
		disabledSetEffects = new ArrayList<Class>();

		//Armor sets
		Config.config.getCategory(ARMOR_SETS_CATEGORY).setComment("Enable or disable armor sets.");
		for (ArmorSet set : ArmorSet.allSets) {
			String name = modList.get(set.modid) == null ? "???" : modList.get(set.modid);
			ConfigCategory category = Config.config.getCategory(ARMOR_SETS_CATEGORY+"."+name.replace(".", ","));
			category.setComment("Enable or disable armor made from "+name+" blocks.");
			Property prop = getArmorSetProp(name, set);
			if (prop.getBoolean()) 
				set.enable();
			else
				set.disable();
		}

		//Set effects
		Config.config.getCategory(SET_EFFECTS_CATEGORY).setComment("Enable or disable set effects.");
		for (SetEffect effect : SetEffect.SET_EFFECTS) {
			Property prop = getSetEffectProp(effect.toString());
			if (!prop.getBoolean())
				disabledSetEffects.add(effect.getClass());
		}		

		//Armor pieces required to activate set effect
		Property prop = getPiecesForSetProp();
		Config.piecesForSet = prop.getInt();

		//Should set effects use durability
		prop = getEffectsUseDurablityProp();
		Config.effectsUseDurability = prop.getBoolean();

		//Register disabled items
		prop = getRegisterDisableItemsProp();
		Config.registerDisabledItems = prop.getBoolean();

		Config.config.save();

		syncJEIBlacklist();
	}

	/**Get effectsUseDurability prop*/
	public static Property getEffectsUseDurablityProp() {
		Property prop = Config.config.get(Configuration.CATEGORY_GENERAL, "Set Effects use durability", false, 
				"Should Set Effects use durability of worn armor to work");
		return prop;
	}

	/**Get registerDisableItems prop*/
	public static Property getRegisterDisableItemsProp() {
		Property prop = Config.config.get(Configuration.CATEGORY_GENERAL, "Register disabled items", true, 
				"Should only need to be changed in the very rare scenario that your world is using all of its item ID's (32k).\n"
						+ TextFormatting.DARK_GREEN+"True: all armor sets will be registered and you can freely enable/disable armor sets without restarting.\n"
						+ TextFormatting.DARK_RED+"False: only enabled armor sets will be registered and you need to restart whenever armor sets are "
						+ "enabled/disabled. Players joining a server with disabled armor sets may need to restart their clients after joining to sync "
						+ "their registered items.");
		prop.setRequiresMcRestart(true);
		return prop;
	}

	/**Get setEffect prop for given set effect name*/
	public static Property getSetEffectProp(String effectName) {
		Property prop;
		if (effectName.equalsIgnoreCase("Diorite Vision"))
			prop = Config.config.get(Config.SET_EFFECTS_CATEGORY, effectName, false, "Determines whether or not the "+effectName+" set effect can be used.");
		else
			prop = Config.config.get(Config.SET_EFFECTS_CATEGORY, effectName, true, "Determines whether or not the "+effectName+" set effect can be used.");
		return prop;
	}

	/**Get armorSet config prop for given modName and armorSetName*/
	public static Property getArmorSetProp(String modName, ArmorSet set) {
		String name = ArmorSet.getItemStackDisplayName(set.stack, null);
		Property prop = Config.config.get(Config.ARMOR_SETS_CATEGORY+"."+modName.replace(".", ","), name+" Armor", true,
				"Determines whether or not the "+name+" armor should be generated.");
		if (!Config.registerDisabledItems)
			prop.setRequiresMcRestart(true);
		return prop;
	}

	/**Get piecesForSet config prop and makes sure it's between 1-4*/
	public static Property getPiecesForSetProp() {
		Property prop = Config.config.get(Configuration.CATEGORY_GENERAL, "Armor pieces required for Set Effects", 4, "Specifies how many armor pieces must be worn for a set's effect(s) to work.", 1, 4);
		if (prop.getInt() > 4)
			prop.set(4);
		else if (prop.getInt() < 1)
			prop.set(1);
		return prop;
	}

	/**Updates JEI's blacklist and reloads JEI's item list, if needed*/
	public static void syncJEIBlacklist() {
		if (Loader.isModLoaded("jei"))
			BlockArmorJEIPlugin.syncJEIBlacklist();
	}

	/**Send PacketSyncConfig when a player joins a server*/
	@SubscribeEvent
	public void onJoinWorld(PlayerLoggedInEvent event) {
		if (!event.player.world.isRemote && event.player != null && event.player instanceof EntityPlayerMP) {
			Config.syncConfig();
			BlockArmor.network.sendTo(new PacketSyncConfig(), (EntityPlayerMP) event.player);
		}
	}

	/**Sync to config when changed*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) 
	{
		if (event.getModID().equals(BlockArmor.MODID))
			if (event.isWorldRunning() && FMLCommonHandler.instance().getMinecraftServerInstance() == null) {
				BlockArmor.logger.warn("Config changes will not be saved while on a server.");
				Config.config.save();				
			}
			else
				Config.syncConfig();
	}
}