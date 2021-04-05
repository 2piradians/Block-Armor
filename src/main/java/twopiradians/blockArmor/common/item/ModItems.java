package twopiradians.blockArmor.common.item;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistry;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.seteffect.SetEffect;

public class ModItems {
	
	public static ArrayList<BlockArmorItem> allArmors = new ArrayList<BlockArmorItem>();

	@Mod.EventBusSubscriber(modid = BlockArmor.MODID, bus = Bus.MOD)
	public static class RegistrationHandler {

		@SubscribeEvent(priority=EventPriority.LOWEST)
		public static void registerItems(final RegistryEvent.Register<Item> event) {						
			ArmorSet.setup();
			SetEffect.setup();
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.init());
			Config.loadConfig(Config.COMMON_SPEC, FMLPaths.CONFIGDIR.get().resolve("blockarmor-common.toml"));

			int vanillaItems = 0;
			int moddedItems = 0;

			Iterator<ArmorSet> it = ArmorSet.allSets.iterator();
			while (it.hasNext()) { 
				ArmorSet set = it.next();
				String registryName = ArmorSet.getItemStackRegistryName(set.stack);
				set.helmet = register(event.getRegistry(), new BlockArmorItem(set.material, EquipmentSlotType.HEAD, set), registryName+"_helmet");
				set.chestplate = register(event.getRegistry(), new BlockArmorItem(set.material, EquipmentSlotType.CHEST, set), registryName+"_chestplate");
				set.leggings = register(event.getRegistry(), new BlockArmorItem(set.material, EquipmentSlotType.LEGS, set), registryName+"_leggings");
				set.boots = register(event.getRegistry(), new BlockArmorItem(set.material, EquipmentSlotType.FEET, set), registryName+"_boots");
				if (set.isFromModdedBlock)
					moddedItems += 4;
				else
					vanillaItems += 4;

				set.enable(); // enable here so they're added to creative tab right away (can be disabled when config loads)
			}

			BlockArmor.LOGGER.info("Generated "+vanillaItems+" Block Armor items from Vanilla Blocks");
			if (moddedItems > 0)
				BlockArmor.LOGGER.info("Generated "+moddedItems+" Block Armor items from Modded Blocks");
		}

		private static BlockArmorItem register(IForgeRegistry<Item> registry, BlockArmorItem armor, String itemName) {
			allArmors.add(armor);
			armor.setRegistryName(BlockArmor.MODID, itemName);
			registry.register(armor);
			return armor;
		}
		
	}

	public static void registerRenders() {
		for (BlockArmorItem item : allArmors) 
			Minecraft.getInstance().getItemRenderer().getItemModelMesher().register
			(item, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

}