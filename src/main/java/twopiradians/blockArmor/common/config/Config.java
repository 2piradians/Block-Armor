package twopiradians.blockArmor.common.config;

import java.io.File;
import java.util.ArrayList;

import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import twopiradians.blockArmor.common.item.ModItems;

public class Config 
{
	public static Configuration config;
	/**0 = enabled, 1 = disabled, 2 = custom*/
	private static int setEffects;
	private static final ArrayList<String> setEffectsArray = new ArrayList<String>() {{add("Enabled"); add("Disabled"); add("Custom");}};
	private static boolean netherrackEffects;
	private static boolean obsidianEffects;
	private static boolean redstoneEffects;
	private static boolean snowEffects;
	private static boolean lapisEffects;
	private static boolean endstoneEffects;
	private static boolean slimeEffects;
	private static boolean sugarcaneEffects;
	private static boolean darkprismarineEffects;
	private static boolean emeraldEffects;
	private static boolean brickEffects;
	private static boolean bedrockEffects;
	private static boolean quartzEffects;


	public static void postInit(final File file)
	{
		Config.config = new Configuration(file);
		Config.config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, "Determines whether or not an individual armor set can use its effect.");
		Config.config.load();
		Config.syncConfig();
		Config.config.save();
	}
	
	public static void syncConfig()
	{
		syncConfig(false);
	}

	public static void syncConfig(boolean initAllConfig) 
	{
		Property setEffectsProp = Config.config.get(Configuration.CATEGORY_GENERAL, "Armor Set Effects", setEffectsArray.get(0), "Allows you to choose which armor set effects you want enabled.", setEffectsArray.toArray(new String[setEffectsArray.size()]));
		Config.setEffects = setEffectsArray.indexOf(setEffectsProp.getString());
		Property netherrackEffectsProp = Config.config.get(Configuration.CATEGORY_GENERAL, "Netherrack Armor Effects", true, "Determines whether or not the netherrack armor set effect can be used.");
		Config.netherrackEffects = netherrackEffectsProp.getBoolean();
		netherrackEffectsProp.setShowInGui(setEffects == 2 || initAllConfig);
		Property obsidianEffectsProp = Config.config.get(Configuration.CATEGORY_GENERAL, "Obsidian Armor Effects", true, "Determines whether or not the obsidian armor set effect can be used.");
		Config.obsidianEffects = obsidianEffectsProp.getBoolean();
		obsidianEffectsProp.setShowInGui(setEffects == 2 || initAllConfig);
		Property redstoneEffectsProp = Config.config.get(Configuration.CATEGORY_GENERAL, "Redstone Armor Effects", true, "Determines whether or not the redstone armor set effect can be used.");
		Config.redstoneEffects = redstoneEffectsProp.getBoolean();
		redstoneEffectsProp.setShowInGui(setEffects == 2 || initAllConfig);
		Property snowEffectsProp = Config.config.get(Configuration.CATEGORY_GENERAL, "Snow Armor Effects", true, "Determines whether or not the snow armor set effect can be used.");
		Config.snowEffects = snowEffectsProp.getBoolean();
		snowEffectsProp.setShowInGui(setEffects == 2 || initAllConfig);
		Property lapisEffectsProp = Config.config.get(Configuration.CATEGORY_GENERAL, "Lapis Armor Effects", true, "Determines whether or not the lapis armor set effect can be used.");
		Config.lapisEffects = lapisEffectsProp.getBoolean();
		lapisEffectsProp.setShowInGui(setEffects == 2 || initAllConfig);
		Property endstoneEffectsProp = Config.config.get(Configuration.CATEGORY_GENERAL, "Endstone Armor Effects", true, "Determines whether or not the endstone armor set effect can be used.");
		Config.endstoneEffects = endstoneEffectsProp.getBoolean();
		endstoneEffectsProp.setShowInGui(setEffects == 2 || initAllConfig);
		Property slimeEffectsProp = Config.config.get(Configuration.CATEGORY_GENERAL, "Slime Armor Effects", true, "Determines whether or not the slime armor set effect can be used..");
		Config.slimeEffects = slimeEffectsProp.getBoolean();
		slimeEffectsProp.setShowInGui(setEffects == 2 || initAllConfig);
		Property sugarcaneEffectsProp = Config.config.get(Configuration.CATEGORY_GENERAL, "Sugarcane Armor Effects", true, "Determines whether or not the sugarcane armor set effect can be used.");
		Config.sugarcaneEffects = sugarcaneEffectsProp.getBoolean();
		sugarcaneEffectsProp.setShowInGui(setEffects == 2 || initAllConfig);
		Property darkprismarineEffectsProp = Config.config.get(Configuration.CATEGORY_GENERAL, "Dark Prismarine Armor Effects", true, "Determines whether or not the dark prismarine armor set effect can be used.");
		Config.darkprismarineEffects = darkprismarineEffectsProp.getBoolean();
		darkprismarineEffectsProp.setShowInGui(setEffects == 2 || initAllConfig);
		Property emeraldEffectsProp = Config.config.get(Configuration.CATEGORY_GENERAL, "Emerald Armor Effects", true, "Determines whether or not the emerald armor set effect can be used.");
		Config.emeraldEffects = emeraldEffectsProp.getBoolean();
		emeraldEffectsProp.setShowInGui(setEffects == 2 || initAllConfig);
		Property brickEffectsProp = Config.config.get(Configuration.CATEGORY_GENERAL, "Brick Armor Effects", true, "Determines whether or not the brick armor set effect can be used.");
		Config.brickEffects = brickEffectsProp.getBoolean();
		brickEffectsProp.setShowInGui(setEffects == 2 || initAllConfig);
		Property bedrockEffectsProp = Config.config.get(Configuration.CATEGORY_GENERAL, "Bedrock Armor Effects", true, "Determines whether or not the bedrock armor set effect can be used.");
		Config.bedrockEffects = bedrockEffectsProp.getBoolean();
		bedrockEffectsProp.setShowInGui(setEffects == 2 || initAllConfig);
		Property quartzEffectsProp = Config.config.get(Configuration.CATEGORY_GENERAL, "Quartz Armor Effects", true, "Determines whether or not the quartz armor set effect can be used.");
		Config.quartzEffects = quartzEffectsProp.getBoolean();
		quartzEffectsProp.setShowInGui(setEffects == 2 || initAllConfig);
		Config.config.save();

		//TODO remove enchants armor
	}

	public static boolean isSetEffectEnabled(ArmorMaterial material) 
	{
		if (Config.setEffects == 0)
			return true;
		else if (Config.setEffects == 1)
			return false;
		else if (material == ModItems.netherrack && Config.netherrackEffects 
				||	material == ModItems.obsidian && Config.obsidianEffects 
				||	material == ModItems.redstone && Config.redstoneEffects 
				||	material == ModItems.snow && Config.snowEffects 
				||	material == ModItems.lapis && Config.lapisEffects 
				||	material == ModItems.endstone && Config.endstoneEffects 
				||	material == ModItems.slime && Config.slimeEffects 
				||	material == ModItems.sugarcane && Config.sugarcaneEffects 
				||	material == ModItems.darkprismarine && Config.darkprismarineEffects
				||	material == ModItems.emerald && Config.emeraldEffects 
				||	material == ModItems.brick && Config.brickEffects 
				||	material == ModItems.bedrock && Config.bedrockEffects
				||	material == ModItems.quartz && Config.quartzEffects)
			return true;
		else
			return false;
	}

	public static boolean hasSetEffect(ArmorMaterial material) 
	{
		if (material == ModItems.netherrack || material == ModItems.obsidian || material == ModItems.redstone || material == ModItems.snow 
				|| material == ModItems.lapis || material == ModItems.endstone ||material == ModItems.slime 
				|| material == ModItems.sugarcane || material == ModItems.darkprismarine ||	material == ModItems.emerald 
				|| material == ModItems.brick || material == ModItems.bedrock || material == ModItems.quartz)
			return true;
		return false;
	}
}
