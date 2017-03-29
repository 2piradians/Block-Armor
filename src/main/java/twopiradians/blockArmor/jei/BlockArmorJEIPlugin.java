package twopiradians.blockArmor.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IItemBlacklist;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
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
		syncJEIBlacklist(false);
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

	/**Adds disabled items and removes enabled items from JEI's blacklist*/
	public static void syncJEIBlacklist(boolean reload) { 
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

			if (!reload) {
				if (addedItems > 0)
					BlockArmor.logger.info("Added "+addedItems+" items to JEI blacklist");
			}
			else {
				//rebuild JEI's item filter
				if (addedItems > 0 || removedItems > 0 && runtime != null) {
					//never detects removed items after reloading, so print here
					if (removedItems > 0)
						BlockArmor.logger.info("Removed "+removedItems+" items from JEI blacklist");
					
					BlockArmor.logger.info("Reloading JEI item list...");
					try {
						registry.getJeiHelpers().reload();
					}
					catch (Exception e) {
						BlockArmor.logger.error("JEI did not reload correctly: ", e);
					}
				}
			}
		}
	}
}
