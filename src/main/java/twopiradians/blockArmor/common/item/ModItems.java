package twopiradians.blockArmor.common.item;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.seteffect.SetEffect;

public class ModItems {

	public static ArrayList<BlockArmorItem> allArmors = new ArrayList<BlockArmorItem>();

	@Mod.EventBusSubscriber(bus = Bus.MOD)
	public static class RegistrationHandler {

		@SubscribeEvent(priority=EventPriority.LOWEST)
		public static void registerItems(final RegistryEvent.Register<VillagerProfession> event) {	
			// hacky way to make sure our items are registered last
			// by registering during a later registry event and unfreezing / refreezing registry
			ForgeRegistry registry = (ForgeRegistry) ForgeRegistries.ITEMS;
			registry.unfreeze();
			
			ArmorSet.setup(registry.getValues()); 
			SetEffect.setup();
			// load config
			ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.init());

			int vanillaItems = 0;
			int moddedItems = 0;

			Iterator<ArmorSet> it = ArmorSet.allSets.iterator();
			while (it.hasNext()) { 
				ArmorSet set = it.next();
				String registryName = ArmorSet.getItemRegistryName(set.item);
				set.helmet = register(registry, new BlockArmorItem(set.material, EquipmentSlotType.HEAD, set), registryName+"_helmet");
				set.chestplate = register(registry, new BlockArmorItem(set.material, EquipmentSlotType.CHEST, set), registryName+"_chestplate");
				set.leggings = register(registry, new BlockArmorItem(set.material, EquipmentSlotType.LEGS, set), registryName+"_leggings");
				set.boots = register(registry, new BlockArmorItem(set.material, EquipmentSlotType.FEET, set), registryName+"_boots");
				if (set.isFromModdedBlock)
					moddedItems += 4;
				else
					vanillaItems += 4;

				set.enable(); // enable here so they're added to creative tab right away (can be disabled when config loads)
			}

			BlockArmor.LOGGER.info("Generated "+vanillaItems+" Block Armor items from Vanilla Blocks");
			if (moddedItems > 0)
				BlockArmor.LOGGER.info("Generated "+moddedItems+" Block Armor items from Modded Blocks");
			
			registry.freeze();
		}

	}

	private static BlockArmorItem register(IForgeRegistry<Item> registry, BlockArmorItem armor, String itemName) {
		allArmors.add(armor);
		armor.setRegistryName(BlockArmor.MODID, itemName);
		registry.register(armor);
		return armor;
	}

	public static void registerRenders() {
		for (BlockArmorItem item : allArmors) 
			Minecraft.getInstance().getItemRenderer().getItemModelMesher().register
			(item, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

}