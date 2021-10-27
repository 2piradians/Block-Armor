package twopiradians.blockArmor.common.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.seteffect.SetEffect;

@Mod.EventBusSubscriber(modid = BlockArmor.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

	/**Change this to have {Set Effects} reset to default values*/
	private static final double CONFIG_VERSION = 1.1D;

	/** How many pieces of armor must be worn to activate set effects */
	public static int piecesForSet;
	/** Should items from disabled sets be registered */
	/*** Sets that should not have items generated - only used by ModItems.postInit()*/
	public static ArrayList<ArmorSet> disabledSets;
	/** Should set effects use durability */
	public static boolean effectsUseDurability;
	public static double globalToughnessModifier;
	public static double globalEnchantabilityModifier;
	public static double globalDamageReductionModifier;
	public static double globalKnockbackResistanceModifier;
	public static double globalDurabilityModifier;

	public static ForgeConfigSpec SERVER_SPEC;
	public static Server SERVER;

	/**Init config - must be called after ArmorSet.setup()*/
	public static ForgeConfigSpec init() {
		final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
		SERVER_SPEC = specPair.getRight();
		SERVER = specPair.getLeft();
		return SERVER_SPEC;
	}

	private static class ArmorSetOptions {
		private ForgeConfigSpec.BooleanValue enabled;
		private ConfigValue<List<? extends String>> setEffects;
		private ConfigValue<Double> armorDamageReduction;
		private ConfigValue<Integer> armorDurability;
		private ConfigValue<Double> armorToughness;
		private ConfigValue<Integer> armorKnockbackResistance;
		private ConfigValue<Integer> armorEnchantability;
	}

	public static class Server {

		/**Config options for each armor set*/
		private static HashMap<ArmorSet, ArmorSetOptions> armorSetOptions = Maps.newHashMap();
		private static IntValue piecesForSet; 
		private static BooleanValue effectsUseDurability;
		private static DoubleValue globalToughnessModifier;
		private static DoubleValue globalEnchantabilityModifier;
		private static DoubleValue globalDamageReductionModifier;
		private static DoubleValue globalKnockbackResistanceModifier;
		private static DoubleValue globalDurabilityModifier;
		private static ConfigValue<Double> configVersion;

		private Server(ForgeConfigSpec.Builder builder) {
			// config version
			configVersion = builder.define("Config version - DO NOT CHANGE", CONFIG_VERSION);
			// general
			builder.comment("General settings");
			builder.push("General");
			//Armor pieces required to activate set effect
			builder.comment("Specifies how many armor pieces must be worn for a set's effects to work.");
			Server.piecesForSet = builder.defineInRange("Armor pieces required for Set Effects", 2, 1, 4);
			//Should set effects use durability
			builder.comment("Should Set Effects use durability of worn armor to work");
			Server.effectsUseDurability = builder.define("Set Effects use durability", false);
			builder.pop();

			// armor stats
			builder.comment("Settings that affect armor stats for all sets");
			builder.push("Armor Stats");
			builder.comment("Multiplied by armor's durability");
			globalDurabilityModifier = builder.defineInRange("Global Durability Modifier", 1.0d, 0, 999999);
			builder.comment("Multiplied by armor's damage reduction");
			globalDamageReductionModifier = builder.defineInRange("Global Damage Reduction Modifier", 1.0d, 0, 999999);
			builder.comment("Multiplied by armor's toughness");
			globalToughnessModifier = builder.defineInRange("Global Tougness Modifier", 1.0d, 0, 999999);
			builder.comment("Multiplied by armor's knockback resistance");
			globalKnockbackResistanceModifier = builder.defineInRange("Global Knockback Resistance Modifier", 1.0d, 0, 999999);
			builder.comment("Multiplied by armor's enchantability");
			globalEnchantabilityModifier = builder.defineInRange("Global Enchantability Modifier", 1.0d, 0, 999999);
			builder.pop();

			// armor sets
			builder.comment("Configure armor sets and their set effects");
			builder.push("Armor_Sets");
			for (String modid : ArmorSet.modidToSetMap.keySet()) {
				builder.push(modid);
				for (ArmorSet set : ArmorSet.modidToSetMap.get(modid)) {
					ArmorSetOptions options = new ArmorSetOptions();
					// they go in alphabetically here, but are not alphabetical in config for some reason..
					builder.push(set.registryName);
					// enabled
					options.enabled = builder.define("Enabled", true);
					// armor values
					options.armorDurability = builder.define("Armor_Durability", set.armorDurability);
					options.armorDamageReduction = builder.define("Armor_Damage_Reduction", (double) set.armorDamageReduction);
					options.armorToughness = builder.define("Armor_Toughness", (double) set.armorToughness);
					options.armorKnockbackResistance = builder.define("Armor_Knockback_Resistance", set.armorKnockbackResistance);
					options.armorEnchantability = builder.define("Armor_Enchantability", set.armorEnchantability);
					// Set Effects
					options.setEffects = builder.defineList("Set_Effects", set.defaultSetEffects.stream().map(SetEffect::writeToString).collect(Collectors.toList()), 
							(effect) -> SetEffect.getEffectFromString((String) effect) != null);
					builder.pop();
					Server.armorSetOptions.put(set, options);
				}
				builder.pop();
			}
			builder.pop();
		}

	}

	/**Read values from config*/
	public static void sync() {
		boolean oldVersion = Server.configVersion.get() != CONFIG_VERSION;
		// pieces for set
		Config.piecesForSet = Server.piecesForSet.get();
		// effects use durability
		Config.effectsUseDurability = Server.effectsUseDurability.get() == null ||
				Server.effectsUseDurability.get();
		// global armor stats
		Config.globalDamageReductionModifier = Server.globalDamageReductionModifier.get();
		Config.globalDurabilityModifier = Server.globalDurabilityModifier.get();
		Config.globalEnchantabilityModifier = Server.globalEnchantabilityModifier.get();
		Config.globalKnockbackResistanceModifier = Server.globalKnockbackResistanceModifier.get();
		Config.globalToughnessModifier = Server.globalToughnessModifier.get();
		for (ArmorSet set : ArmorSet.allSets) {
			ArmorSetOptions options = Server.armorSetOptions.get(set);
			// Enabled (wait until after items are registered to enable)
			if (set.helmet != null) {
				Boolean enabled = options.enabled.get();
				if (enabled == null || enabled == true)
					set.enable();
				else
					set.disable();
			}
			// armor values
			set.armorDurability = options.armorDurability.get();
			set.armorDamageReduction = options.armorDamageReduction.get().floatValue();
			set.armorToughness = options.armorToughness.get().floatValue();
			set.armorEnchantability = options.armorEnchantability.get();
			set.armorKnockbackResistance = options.armorKnockbackResistance.get();
			set.createMaterial();
			// Set Effects
			// change set effects to default if old config version
			if (oldVersion)
				options.setEffects.set(set.defaultSetEffects.stream().map(SetEffect::writeToString).collect(Collectors.toList()));
			set.setEffects.clear();
			for (String str : options.setEffects.get()) {
				SetEffect effect = SetEffect.getEffectFromString(str);
				if (effect != null)
					set.setEffects.add(effect);
				else
					BlockArmor.LOGGER.warn("Invalid set effect in config for "+set.registryName+": "+str);
			}
		}
		// update config version
		if (oldVersion) 
			Server.configVersion.set(CONFIG_VERSION);
	}

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent configEvent) {
		BlockArmor.LOGGER.info("Syncing config!");
		sync();
	}

}