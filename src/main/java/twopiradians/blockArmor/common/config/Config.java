package twopiradians.blockArmor.common.config;

import java.io.File;
import java.util.ArrayList;

import mezz.jei.JustEnoughItems;
import mezz.jei.ProxyCommon;
import mezz.jei.ProxyCommonClient;
import mezz.jei.config.Constants;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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

	/**How many pieces of armor must be worn to activate set effects*/
	public static int piecesForSet;
	/**Classes of any set effects that are disabled in config*/
	public static ArrayList<Class> disabledSetEffects;

	public static void postInit(final File file) 
	{
		Config.config = new Configuration(file);
		Config.config.load();
		syncConfig();
	}

	public static void syncConfig() 
	{
		disabledSetEffects = new ArrayList<Class>();

		//Armor sets
		Config.config.getCategory(ARMOR_SETS_CATEGORY).setComment("Enable or disable armor sets.");
		for (ArmorSet set : ArmorSet.allSets) {
			ModContainer mod = Loader.instance().getIndexedModList().get(set.modid);
			if (mod != null) {
				ConfigCategory category = Config.config.getCategory(ARMOR_SETS_CATEGORY+"."+mod.getName());
				category.setComment("Enable or disable armor sets from "+mod.getName()+" blocks.");
				Property prop = getArmorSetProp(mod.getName(), set);
				if (prop.getBoolean()) 
					set.enable();
				else
					set.disable();
			}
		}
		/*for (String modid : ArmorSet.armorSetMods.keySet()) {
			ModContainer mod = Loader.instance().getIndexedModList().get(modid);
			if (mod != null) {
				ConfigCategory category = Config.config.getCategory(ARMOR_SETS_CATEGORY+"."+mod.getName());
				category.setComment("Enable or disable armor sets from "+mod.getName()+" blocks.");
				for (ArmorSet set : ArmorSet.armorSetMods.get(modid)) {
					Property prop = getArmorSetProp(mod.getName(), set);
					set.enabled = prop.getBoolean();
				}
			}
		}*/

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

		Config.config.save();

		syncJEIBlacklist();
	}

	/**Get setEffect prop for given set effect name*/
	public static Property getSetEffectProp(String effectName) {
		Property prop = Config.config.get(Config.SET_EFFECTS_CATEGORY, effectName, true, "Determines whether or not the "+effectName+" set effect can be used.");
		return prop;
	}

	/**Get armorSet config prop for given modName and armorSetName*/
	public static Property getArmorSetProp(String modName, ArmorSet set) {
		String name = ArmorSet.getItemStackDisplayName(set.stack, null);
		Property prop = Config.config.get(ARMOR_SETS_CATEGORY+"."+modName, name+" Armor Set", true,
				"Determines whether or not the "+name+" armor set should be generated.");
		return prop;
	}

	/**Get piecesForSet config prop and makes sure it's between 1-4*/
	public static Property getPiecesForSetProp() {
		Property prop = Config.config.get(Configuration.CATEGORY_GENERAL, "Armor pieces required for Set Effect", 4, "Specifies how many armor pieces must be worn for a set's effect(s) to work.", 3, 4);
		if (prop.getInt() > 4)
			prop.set(4);
		else if (prop.getInt() < 1)
			prop.set(1);
		return prop;
	}

	/**Updates JEI's blacklist and reloads JEI's item list, if needed*/
	public static void syncJEIBlacklist() {
		if (Loader.isModLoaded("jei"))
			try {
				if (BlockArmorJEIPlugin.syncJEIBlacklist()) {
					ProxyCommon proxy = JustEnoughItems.getProxy();
					if (proxy instanceof ProxyCommonClient) {
						BlockArmor.logger.info("Reloading JEI item list...");
						((ProxyCommonClient)proxy).onConfigChanged(
								new ConfigChangedEvent.OnConfigChangedEvent(Constants.MOD_ID, null, true, false));
					} 
				}
			}
		catch (Exception e) {
			BlockArmor.logger.error("Another mod caused an exception while reloading JEI: ", e);
		} 
	}

	/**Send PacketSyncConfig when a player joins a server*/
	@SubscribeEvent
	public void onJoinWorld(EntityJoinWorldEvent event) {
		if (!event.getWorld().isRemote && event.getEntity() != null && event.getEntity() instanceof EntityPlayerMP) 
			BlockArmor.network.sendTo(new PacketSyncConfig(), (EntityPlayerMP) event.getEntity());
	}

	/**Sync to config when changed*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) 
	{
		if (event.getModID().equals(BlockArmor.MODID)) 
			Config.syncConfig();
	}
}