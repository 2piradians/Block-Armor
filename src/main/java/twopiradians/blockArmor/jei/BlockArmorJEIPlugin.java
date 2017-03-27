package twopiradians.blockArmor.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.config.Config;
import mezz.jei.config.Config.IngredientBlacklistType;
import mezz.jei.gui.ItemListOverlay;
import net.minecraft.item.ItemStack;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

@JEIPlugin
public class BlockArmorJEIPlugin extends BlankModPlugin 
{
	private static IIngredientHelper ingredientHelper;
	private static IJeiRuntime runtime;

	@Override
	public void register(IModRegistry registry) {
		ingredientHelper = registry.getIngredientRegistry().getIngredientHelper(ItemStack.class);
		syncJEIBlacklist();
		//add recipes for disabled sets (that 
		for (ArmorSet set : ArmorSet.allSets)
			if (!set.isEnabled())
				registry.addRecipes(set.recipes);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		runtime = jeiRuntime;
	}

	public static void setFilterText(String text) {
		try {
			if (runtime != null && runtime.getItemListOverlay() instanceof ItemListOverlay) {
				ItemListOverlay list = (ItemListOverlay) runtime.getItemListOverlay();
				list.getInternal().setFilterText("");
				list.getInternal().setKeyboardFocus(true);
				for (char letter : text.toCharArray())
					list.getInternal().onKeyPressed(letter, 0);
				list.getInternal().setKeyboardFocus(false);
			}
		}
		catch (Exception e) {
			BlockArmor.logger.warn("JEI threw an exception when attempting to set filter text");
		}
	}

	/**Adds disabled items and removes enabled items from JEI's blacklist
	 * @return true if JEI's blacklist was updated and needs to be reloaded*/
	public static boolean syncJEIBlacklist() { 
		if (ingredientHelper != null) {
			int removedItems = 0;
			int addedItems = 0;
			for (ArmorSet set : ArmorSet.allSets)
				if (set.isEnabled()) {
					for (ItemBlockArmor armor : new ItemBlockArmor[] {set.helmet, set.chestplate, set.leggings, set.boots})
						if (Config.isIngredientOnConfigBlacklist(new ItemStack(armor), ingredientHelper)) {
							Config.removeIngredientFromConfigBlacklist(new ItemStack(armor), IngredientBlacklistType.ITEM, ingredientHelper);
							removedItems++;
						}
				}
				else
					for (ItemBlockArmor armor : new ItemBlockArmor[] {set.helmet, set.chestplate, set.leggings, set.boots})
						if (!Config.isIngredientOnConfigBlacklist(new ItemStack(armor), ingredientHelper)) {
							Config.addIngredientToConfigBlacklist(new ItemStack(armor), IngredientBlacklistType.ITEM, ingredientHelper);
							addedItems++;
						}

			if (addedItems > 0)
				BlockArmor.logger.info("Added "+addedItems+" items to JEI blacklist");
			if (removedItems > 0)
				BlockArmor.logger.info("Removed "+removedItems+" items from JEI blacklist");

			return addedItems > 0 || removedItems > 0;
		}
		return false;
	}
}
