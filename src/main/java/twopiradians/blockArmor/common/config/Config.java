package twopiradians.blockArmor.common.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.Maps;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.seteffect.SetEffect;

@Mod.EventBusSubscriber(modid = BlockArmor.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

	/** How many pieces of armor must be worn to activate set effects */
	public static int piecesForSet;
	/** Should items from disabled sets be registered */
	//public static boolean registerDisabledItems; // not gonna work bc config is read after items are registered
	/*** Sets that should not have items generated - only used by ModItems.postInit()*/
	public static ArrayList<ArmorSet> disabledSets;
	/** Should set effects use durability */
	public static boolean effectsUseDurability;

	/** Map of lowercase modid to mod name */
	private static HashMap<String, String> modList;

	public static ForgeConfigSpec COMMON_SPEC;
	public static Common COMMON;

	public static class Common {

		private static HashMap<ArmorSet, ForgeConfigSpec.BooleanValue> enabledMap = Maps.newHashMap();
		private static HashMap<ArmorSet, ConfigValue<List<? extends String>>> setEffectsMap = Maps.newHashMap();
		//private static BooleanValue registerDisabledItems;
		private static IntValue piecesForSet; // TEST do these update dynamically?
		private static BooleanValue effectsUseDurability;

		private Common(ForgeConfigSpec.Builder builder) {
			// general
			builder.comment("General settings");
			builder.push("General");
			// Register disabled items
			//builder.worldRestart();
			//builder.comment("Should only need to be changed in the very rare scenario that your world is using all of its item ID's (32k).\n"
			//+ "\tTrue: all armor sets will be registered and you can freely enable/disable armor sets without restarting.\n"
			//+ "\tFalse: only enabled armor sets will be registered and you need to restart whenever armor sets are enabled/disabled.\n"
			//+ "Players joining a server with disabled armor sets may need to restart their clients after joining to sync their registered items.");
			//Common.registerDisabledItems = builder.define("Register disabled items", true);
			//Armor pieces required to activate set effect
			builder.comment("Specifies how many armor pieces must be worn for a set's effect(s) to work.");
			Common.piecesForSet = builder.defineInRange("Armor pieces required for Set Effects", 4, 1, 4);
			//Should set effects use durability
			builder.comment("Should Set Effects use durability of worn armor to work");
			Common.effectsUseDurability = builder.define("Set Effects use durability", false);
			builder.pop();

			// armor sets
			builder.comment("Configure armor sets and their set effects");
			builder.push("Armor_Sets");
			for (String modid : ArmorSet.modidToSetMap.keySet()) {
				builder.push(modid);
				for (ArmorSet set : ArmorSet.modidToSetMap.get(modid)) {
					// they go in alphabetically here, but are not alphabetical in config for some reason..
					builder.push(set.registryName);
					// Enabled
					Common.enabledMap.put(set, builder.define("Enabled", true));
					// Set Effects
					Common.setEffectsMap.put(set, builder.defineList("Set_Effects", set.setEffects.stream().map(SetEffect::writeToString).collect(Collectors.toList()), 
							(effect) -> SetEffect.getEffectFromString((String) effect) != null));
					builder.pop();
				}
				builder.pop();
			}
			builder.pop();
		}

	}

	public static ForgeConfigSpec init() {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
		return COMMON_SPEC;
	}

	public static void loadConfig(ForgeConfigSpec spec, Path path) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(path)
				.sync()
				.autosave()
				.autoreload()
				.preserveInsertionOrder()
				.writingMode(WritingMode.REPLACE)
				.build();

		configData.load();
		spec.setConfig(configData);
	}

	/**Read values from config*/
	public static void sync() {
		// register disabled items
		//Config.registerDisabledItems = Common.registerDisabledItems.get() == null ||
		//		Common.registerDisabledItems.get();
		// pieces for set
		Config.piecesForSet = Common.piecesForSet.get();
		// effects use durability
		Config.effectsUseDurability = Common.effectsUseDurability.get() == null ||
				Common.effectsUseDurability.get();
		for (ArmorSet set : ArmorSet.allSets) {
			// Enabled
			Boolean enabled = Common.enabledMap.get(set).get();
			if (enabled == null || enabled == true)
				set.enable();
			else
				set.disable();
			// Set Effects
			set.setEffects.clear(); // TODO need read/write string for non-basic set effects
			for (String str : Common.setEffectsMap.get(set).get()) {
				SetEffect effect = SetEffect.getEffectFromString(str);
				if (effect != null)
					set.setEffects.add(effect);
				else
					BlockArmor.LOGGER.warn("Invalid set effect in config for "+set.registryName+": "+str);
			}
		}
	}

	@SubscribeEvent
	public static void onLoad(final ModConfig.ModConfigEvent configEvent) { // TODO figure out how to reload in-game?
		BlockArmor.LOGGER.info("Loaded config file! Syncing!");
		sync();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent) {
		BlockArmor.LOGGER.info("Loaded config file {}", configEvent.getConfig().getFileName());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfig.Reloading configEvent) {
		BlockArmor.LOGGER.info("Config just got changed on the file system!");
	}

}