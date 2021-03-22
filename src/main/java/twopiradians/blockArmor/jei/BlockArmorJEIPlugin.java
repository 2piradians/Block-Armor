/*package twopiradians.blockArmor.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.FMLCommonHandler;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.BlockArmorItem;

@JEIPlugin
public class BlockArmorJEIPlugin implements IModPlugin 
{
	private static IModRegistry registry;

	@Override
	public void register(IModRegistry registry) {
		BlockArmorJEIPlugin.registry = registry;
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		syncJEIIngredients();
	}

	*//** Adds disabled items and removes enabled items from JEI's ingredients *//*
																					@SuppressWarnings("deprecation")
																					public static void syncJEIIngredients() { 
																					if (registry != null && FMLCommonHandler.instance().getDist() == Dist.CLIENT) {
																					List<ItemStack> ingredients = new ArrayList<ItemStack>(registry.getIngredientRegistry().getAllIngredients(ItemStack.class));
																					List<ItemStack> ingredientsToAdd = new ArrayList<ItemStack>();
																					List<ItemStack> ingredientsToRemove = new ArrayList<ItemStack>();
																					for (ArmorSet set : ArmorSet.allSets)
																					if (set.isEnabled()) {
																					for (BlockArmorItem armor : new BlockArmorItem[] {set.helmet, set.chestplate, set.leggings, set.boots})
																					if (registry.getJeiHelpers().getStackHelper().containsAnyStack(ingredients, Collections.singletonList(new ItemStack(armor))) == null)
																					ingredientsToAdd.add(new ItemStack(armor));
																					}
																					else
																					for (final BlockArmorItem armor : new BlockArmorItem[] {set.helmet, set.chestplate, set.leggings, set.boots})
																					if (registry.getJeiHelpers().getStackHelper().containsAnyStack(ingredients, Collections.singletonList(new ItemStack(armor))) != null)
																					ingredientsToRemove.add(registry.getJeiHelpers().getStackHelper().containsAnyStack(ingredients, Collections.singletonList(new ItemStack(armor))));
																					
																					if (!ingredientsToAdd.isEmpty()) {
																					Minecraft.getInstance().addScheduledTask(() -> { // prevent JEI crash - this needs to run on main thread
																					registry.getIngredientRegistry().addIngredientsAtRuntime(ItemStack.class, ingredientsToAdd);
																					});
																					BlockArmor.LOGGER.info("Added "+ingredientsToAdd.size()+" items to JEI");
																					}
																					if (!ingredientsToRemove.isEmpty()) {
																					Minecraft.getInstance().addScheduledTask(() -> { // prevent JEI crash - this needs to run on main thread
																					registry.getIngredientRegistry().removeIngredientsAtRuntime(ItemStack.class, ingredientsToRemove);
																					});
																					BlockArmor.LOGGER.info("Removed "+ingredientsToRemove.size()+" items from JEI");
																					}
																					}
																					}
																					}
																					*/