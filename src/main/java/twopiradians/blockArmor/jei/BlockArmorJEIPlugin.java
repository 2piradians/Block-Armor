package twopiradians.blockArmor.jei;

import java.util.ArrayList;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IItemBlacklist;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

@JEIPlugin
public class BlockArmorJEIPlugin extends BlankModPlugin 
{
	private static IJeiRuntime runtime;
	private static IModRegistry registry;

	@Override
	public void register(IModRegistry registry) {
		BlockArmorJEIPlugin.registry = registry;
		syncJEIBlacklist();
		//add recipes for disabled sets (that aren't added normally via set.enable())
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
			if (runtime != null) 
				runtime.getItemListOverlay().setFilterText(text);
		}
		catch (Exception e) {}
	}

	/**Adds disabled items and removes enabled items from JEI's blacklist
	 * @return true if JEI's blacklist was updated and needs to be reloaded*/
	public static boolean syncJEIBlacklist() { 
		if (registry != null) {
			IItemBlacklist blacklist = registry.getJeiHelpers().getItemBlacklist();
			int removedItems = 0;
			int addedItems = 0;
			for (ArmorSet set : ArmorSet.allSets)
				if (set.isEnabled()) {
					for (ItemBlockArmor armor : new ItemBlockArmor[] {set.helmet, set.chestplate, set.leggings, set.boots})
						if (blacklist.isItemBlacklisted(new ItemStack(armor))) {
							blacklist.removeItemFromBlacklist(new ItemStack(armor));
							removedItems++;
						}
				}
				else
					for (final ItemBlockArmor armor : new ItemBlockArmor[] {set.helmet, set.chestplate, set.leggings, set.boots})
						if (!blacklist.isItemBlacklisted(new ItemStack(armor))) {
							blacklist.addItemToBlacklist(new ItemStack(armor));
							addedItems++;
						}

			if (addedItems > 0)
				BlockArmor.logger.info("Added "+addedItems+" items to JEI blacklist");
			if (removedItems > 0)
				BlockArmor.logger.info("Removed "+removedItems+" items from JEI blacklist");

			//rebuild JEI's item filter
			if (addedItems > 0 || removedItems > 0 && 
					runtime != null) {
				BlockArmor.logger.info("Reloading JEI item list...");
				try {
					registry.getIngredientRegistry().addIngredientsAtRuntime(ItemStack.class, 
							new ArrayList<ItemStack>() {{add(new ItemStack(Blocks.STONE));}});
				}
				catch (Exception e) {
					BlockArmor.logger.error("JEI did not reload correctly: ", e);
				}
			}
		}
		return false;
	}
}
