package twopiradians.blockArmor.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

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
	
	/**Adds disabled items and removes enabled items from JEI's ingredients*/
	@SuppressWarnings("deprecation")
	public static void syncJEIIngredients() { 
		if (registry != null) {
			List<ItemStack> ingredients = new ArrayList<ItemStack>(registry.getIngredientRegistry().getAllIngredients(ItemStack.class));
			List<ItemStack> ingredientsToAdd = new ArrayList<ItemStack>();
			List<ItemStack> ingredientsToRemove = new ArrayList<ItemStack>();
			for (ArmorSet set : ArmorSet.allSets)
				if (set.isEnabled()) {
					for (ItemBlockArmor armor : new ItemBlockArmor[] {set.helmet, set.chestplate, set.leggings, set.boots})
						if (registry.getJeiHelpers().getStackHelper().containsAnyStack(ingredients, Collections.singletonList(new ItemStack(armor))) == null)
							ingredientsToAdd.add(new ItemStack(armor));
				}
				else
					for (final ItemBlockArmor armor : new ItemBlockArmor[] {set.helmet, set.chestplate, set.leggings, set.boots})
						if (registry.getJeiHelpers().getStackHelper().containsAnyStack(ingredients, Collections.singletonList(new ItemStack(armor))) != null)
							ingredientsToRemove.add(registry.getJeiHelpers().getStackHelper().containsAnyStack(ingredients, Collections.singletonList(new ItemStack(armor))));

			if (!ingredientsToAdd.isEmpty()) {
				registry.getIngredientRegistry().addIngredientsAtRuntime(ItemStack.class, ingredientsToAdd);
				BlockArmor.logger.info("Added "+ingredientsToAdd.size()+" items to JEI");
			}
			if (!ingredientsToRemove.isEmpty()) {
				registry.getIngredientRegistry().removeIngredientsAtRuntime(ItemStack.class, ingredientsToRemove);
				BlockArmor.logger.info("Removed "+ingredientsToRemove.size()+" items from JEI");
			}
		}
	}
}
