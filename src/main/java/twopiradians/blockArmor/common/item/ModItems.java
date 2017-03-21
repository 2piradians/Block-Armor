package twopiradians.blockArmor.common.item;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import twopiradians.blockArmor.common.BlockArmor;

public class ModItems
{
	public static ArrayList<ItemBlockArmor> allArmors = new ArrayList<ItemBlockArmor>();

	public static void postInit() {
		int vanillaItems = 0;
		int moddedItems = 0;

		for (ArmorSet set : ArmorSet.allSets) { 
			//			if (set.enabled) { //if enabled in config
			String registryName = ArmorSet.getItemStackRegistryName(set.stack);
			set.helmet = (ItemBlockArmor) registerItem(new ItemBlockArmor(set.material, 0, EntityEquipmentSlot.HEAD, set), registryName+"_helmet", true);
			set.chestplate = (ItemBlockArmor) registerItem(new ItemBlockArmor(set.material, 0, EntityEquipmentSlot.CHEST, set), registryName+"_chestplate", true);
			set.leggings = (ItemBlockArmor) registerItem(new ItemBlockArmor(set.material, 0,EntityEquipmentSlot.LEGS, set), registryName+"_leggings", true);
			set.boots = (ItemBlockArmor) registerItem(new ItemBlockArmor(set.material, 0, EntityEquipmentSlot.FEET, set), registryName+"_boots", true);
			if (set.isFromModdedBlock)
				moddedItems += 4;
			else
				vanillaItems += 4;
//			BlockArmor.logger.debug("Generated armor for: "+set.stack.getDisplayName());
			/*}
			else
				BlockArmor.logger.debug("Armor generation disabled for: "+set.stack.getDisplayName());*/
			ArrayList<IRecipe> recipes = new ArrayList<IRecipe>(); //first disable() doesn't actually remove recipe..
			ItemStack A = set.block == Blocks.EMERALD_BLOCK ? new ItemStack(Items.EMERALD) : set.stack;
			ItemStack B = ItemStack.EMPTY;
			recipes.add(new ShapedRecipes(3, 2, new ItemStack[] {A,A,A, A,B,A}, new ItemStack(set.helmet)));
			recipes.add(new ShapedRecipes(3, 3, new ItemStack[] {A,B,A, A,A,A, A,A,A}, new ItemStack(set.chestplate)));
			recipes.add(new ShapedRecipes(3, 3, new ItemStack[] {A,A,A, A,B,A, A,B,A}, new ItemStack(set.leggings)));
			recipes.add(new ShapedRecipes(3, 2, new ItemStack[] {A,B,A, A,B,A}, new ItemStack(set.boots)));
			set.recipes = recipes;
		}

		BlockArmor.logger.info("Generated "+vanillaItems+" Block Armor items from Vanilla Blocks");
		if (moddedItems > 0)
			BlockArmor.logger.info("Generated "+moddedItems+" Block Armor items from Modded Blocks");
	}

	public static void registerRenders() {
		for (ItemBlockArmor item : allArmors)
			registerRender(item);
	}

	/**Add item to creative tab and add recipe for the item*/
	/*public static void addRecipeAndTab(ItemBlockArmor item, boolean isFromModdedBlock) {
		//add to tab
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

		//add recipe
		ItemStack stack = item.set.block == Blocks.EMERALD_BLOCK ? new ItemStack(Items.EMERALD) : item.set.stack;
		switch (item.armorType) {
		case HEAD:
			GameRegistry.addShapedRecipe(new ItemStack(item),"AAA","A A",'A', stack);
			break;
		case CHEST:
			GameRegistry.addShapedRecipe(new ItemStack(item),"A A","AAA","AAA",'A', stack);
			break;
		case LEGS:
			GameRegistry.addShapedRecipe(new ItemStack(item),"AAA","A A","A A",'A', stack);
			break;
		case FEET:
			GameRegistry.addShapedRecipe(new ItemStack(item),"A A","A A",'A', stack);
			break;
		}
	}

	*//**Remove item from creative tab an remove recipe for the item*//*
	public static boolean removeRecipeAndTab(ItemBlockArmor item, boolean isFromModdedBlock) {
		//remove from creative tab
		item.setCreativeTab(null);

		//remove from vanilla tab
		if (BlockArmor.vanillaTab != null && BlockArmor.vanillaTab.orderedStacks != null)
			for (ItemStack tabStack : BlockArmor.vanillaTab.orderedStacks)
				if (tabStack.getItem() == item) {
					BlockArmor.vanillaTab.orderedStacks.remove(tabStack);
					break;
				}

		//remove from modded tab
		if (BlockArmor.moddedTab != null && BlockArmor.moddedTab.orderedStacks != null)
			for (ItemStack tabStack : BlockArmor.moddedTab.orderedStacks)
				if (tabStack.getItem() == item) {
					BlockArmor.moddedTab.orderedStacks.remove(tabStack);
					break;
				}

		//remove recipe
		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		for (IRecipe recipe : recipes)
			if (recipe.getRecipeOutput() != null && recipe.getRecipeOutput().getItem() == item) {
				BlockArmor.logger.debug("Disabling item: "+new ItemStack(item).getDisplayName());
				recipes.remove(recipe);
				return true;
			}
		
		return false;
	}*/

	private static ItemBlockArmor registerItem(ItemBlockArmor item, String unlocalizedName, boolean isFromModdedBlock) {
		allArmors.add(item);
		item.setUnlocalizedName(unlocalizedName);
		item.setRegistryName(BlockArmor.MODID, unlocalizedName);
		GameRegistry.register(item);
//		addRecipeAndTab(item, isFromModdedBlock);
		return item;
	}

	private static void registerRender(Item item) {		
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(BlockArmor.MODID+":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
}