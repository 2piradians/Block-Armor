package twopiradians.blockArmor.common.item;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.seteffect.SetEffect;

public class ModItems
{
	public static ArrayList<BlockArmorItem> allArmors = new ArrayList<BlockArmorItem>();

	@Mod.EventBusSubscriber(modid = BlockArmor.MODID, bus = Bus.MOD)
	public static class RegistrationHandler {

		@SubscribeEvent(priority=EventPriority.LOWEST)
		public static void registerItems(final RegistryEvent.Register<Item> event) {						
			ArmorSet.setup();
			SetEffect.setup();
			//Config.setup(BlockArmor.configFile);

			int vanillaItems = 0;
			int moddedItems = 0;

			for (ArmorSet set : ArmorSet.allSets) { 
				/*if (!Config.disabledSets.contains(set))*/ {
					String registryName = ArmorSet.getItemStackRegistryName(set.stack);
					set.helmet = register(event.getRegistry(), new BlockArmorItem(set.material, 0, EquipmentSlotType.HEAD, set), registryName+"_helmet", true);
					set.chestplate = register(event.getRegistry(), new BlockArmorItem(set.material, 0, EquipmentSlotType.CHEST, set), registryName+"_chestplate", true);
					set.leggings = register(event.getRegistry(), new BlockArmorItem(set.material, 0, EquipmentSlotType.LEGS, set), registryName+"_leggings", true);
					set.boots = register(event.getRegistry(), new BlockArmorItem(set.material, 0, EquipmentSlotType.FEET, set), registryName+"_boots", true);
					if (set.isFromModdedBlock)
						moddedItems += 4;
					else
						vanillaItems += 4;
					set.enable(); // TODO remove eventually?
				}
			}
			//for (ArmorSet set : Config.disabledSets)
			//	ArmorSet.allSets.remove(set);

			BlockArmor.LOGGER.info("Generated "+vanillaItems+" Block Armor items from Vanilla Blocks");
			if (moddedItems > 0)
				BlockArmor.LOGGER.info("Generated "+moddedItems+" Block Armor items from Modded Blocks");

			//Config.syncConfig();
		}

		private static BlockArmorItem register(IForgeRegistry<Item> registry, BlockArmorItem armor, String itemName, boolean isFromModdedBlock) {
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