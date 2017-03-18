package twopiradians.blockArmor.common.item;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.creativetab.BlockArmorCreativeTab;

public class ModItems
{
	public static ArrayList<ItemBlockArmor> allArmors = new ArrayList<ItemBlockArmor>();

	public static void postInit() {
		int vanillaItems = 0;
		int moddedItems = 0;

		for (ArmorSet set : ArmorSet.allSets) 
			if (set.enabled) { //if enabled in config
				String registryName = ArmorSet.getItemStackRegistryName(set.stack);
				set.helmet = (ItemBlockArmor) registerItem(new ItemBlockArmor(set.material, 0, EntityEquipmentSlot.HEAD, set), registryName+"_helmet", true, set.isFromModdedBlock);
				set.chestplate = (ItemBlockArmor) registerItem(new ItemBlockArmor(set.material, 0, EntityEquipmentSlot.CHEST, set), registryName+"_chestplate", true, set.isFromModdedBlock);
				set.leggings = (ItemBlockArmor) registerItem(new ItemBlockArmor(set.material, 0,EntityEquipmentSlot.LEGS, set), registryName+"_leggings", true, set.isFromModdedBlock);
				set.boots = (ItemBlockArmor) registerItem(new ItemBlockArmor(set.material, 0, EntityEquipmentSlot.FEET, set), registryName+"_boots", true, set.isFromModdedBlock);
				if (set.isFromModdedBlock)
					moddedItems += 4;
				else
					vanillaItems += 4;
				BlockArmor.logger.debug("Generated armor for: "+set.stack.getDisplayName());
			}
			else
				BlockArmor.logger.debug("Armor generation disabled for: "+set.stack.getDisplayName());

		BlockArmor.logger.info("Generated "+vanillaItems+" Block Armor items from Vanilla Blocks");
		if (moddedItems > 0)
			BlockArmor.logger.info("Generated "+moddedItems+" Block Armor items from Modded Blocks");
	}

	public static void registerRenders() {
		for (ItemBlockArmor item : allArmors)
			registerRender(item);
	}

	private static Item registerItem(Item item, String unlocalizedName, boolean addToTab, boolean isFromModdedBlock) {
		if (item instanceof ItemBlockArmor)
			allArmors.add((ItemBlockArmor) item);
		item.setUnlocalizedName(unlocalizedName);
		item.setRegistryName(BlockArmor.MODID, unlocalizedName);
		if (addToTab) {
			if (isFromModdedBlock) {
				if (BlockArmor.moddedTab == null)
					BlockArmor.moddedTab = new BlockArmorCreativeTab("tabBlockArmorModded");
				BlockArmor.moddedTab.orderedStacks.add(new ItemStack(item));
				item.setCreativeTab(BlockArmor.moddedTab);
			}
			else {
				if (BlockArmor.vanillaTab == null)
					BlockArmor.vanillaTab = new BlockArmorCreativeTab("tabBlockArmorVanilla");
				BlockArmor.vanillaTab.orderedStacks.add(new ItemStack(item));
				item.setCreativeTab(BlockArmor.vanillaTab);
			}
		}
		GameRegistry.register(item);
		return item;
	}

	private static void registerRender(Item item) {		
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(BlockArmor.MODID+":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
}