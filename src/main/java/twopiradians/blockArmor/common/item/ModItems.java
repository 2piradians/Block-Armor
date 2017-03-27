package twopiradians.blockArmor.common.item;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.config.Config;

public class ModItems
{
	public static ArrayList<ItemBlockArmor> allArmors = new ArrayList<ItemBlockArmor>();

	public static void postInit() {
		int vanillaItems = 0;
		int moddedItems = 0;

		for (ArmorSet set : ArmorSet.allSets) { 
			if (!Config.disabledSets.contains(set)) {
				String registryName = ArmorSet.getItemStackRegistryName(set.stack);
				set.helmet = (ItemBlockArmor) registerItem(new ItemBlockArmor(set.material, 0, EntityEquipmentSlot.HEAD, set), registryName+"_helmet", true);
				set.chestplate = (ItemBlockArmor) registerItem(new ItemBlockArmor(set.material, 0, EntityEquipmentSlot.CHEST, set), registryName+"_chestplate", true);
				set.leggings = (ItemBlockArmor) registerItem(new ItemBlockArmor(set.material, 0,EntityEquipmentSlot.LEGS, set), registryName+"_leggings", true);
				set.boots = (ItemBlockArmor) registerItem(new ItemBlockArmor(set.material, 0, EntityEquipmentSlot.FEET, set), registryName+"_boots", true);
				if (set.isFromModdedBlock)
					moddedItems += 4;
				else
					vanillaItems += 4;

				ArrayList<IRecipe> recipes = new ArrayList<IRecipe>(); 
				ItemStack A = set.stack;
				ItemStack B = ItemStack.EMPTY;
				recipes.add(new ShapedRecipes(3, 2, new ItemStack[] {A,A,A, A,B,A}, new ItemStack(set.helmet)));
				recipes.add(new ShapedRecipes(3, 3, new ItemStack[] {A,B,A, A,A,A, A,A,A}, new ItemStack(set.chestplate)));
				recipes.add(new ShapedRecipes(3, 3, new ItemStack[] {A,A,A, A,B,A, A,B,A}, new ItemStack(set.leggings)));
				recipes.add(new ShapedRecipes(3, 2, new ItemStack[] {A,B,A, A,B,A}, new ItemStack(set.boots)));
				set.recipes = recipes;
			}
		}
		for (ArmorSet set : Config.disabledSets)
			ArmorSet.allSets.remove(set);

		BlockArmor.logger.info("Generated "+vanillaItems+" Block Armor items from Vanilla Blocks");
		if (moddedItems > 0)
			BlockArmor.logger.info("Generated "+moddedItems+" Block Armor items from Modded Blocks");
	}

	public static void registerRenders() {
		for (ItemBlockArmor item : allArmors)
			registerRender(item);
	}

	private static ItemBlockArmor registerItem(ItemBlockArmor item, String unlocalizedName, boolean isFromModdedBlock) {
		allArmors.add(item);
		item.setUnlocalizedName(unlocalizedName);
		item.setRegistryName(BlockArmor.MODID, unlocalizedName);
		GameRegistry.register(item);
		return item;
	}

	private static void registerRender(Item item) {		
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(BlockArmor.MODID+":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
}