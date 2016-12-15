package twopiradians.blockArmor.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IItemBlacklist;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

@JEIPlugin
public class BlockArmorJEIPlugin extends BlankModPlugin 
{
	public static IJeiHelpers helpers;

	@Override
	public void register(IModRegistry registry) {
		helpers = registry.getJeiHelpers();
		addItemsToBlacklist();
	}

	/**Adds disabled items (without textures) to JEI's blacklist (so they're not displayed)*/
	private static void addItemsToBlacklist() {
		if (ArmorSet.disabledItems != null && !ArmorSet.disabledItems.isEmpty() && 
				helpers != null && helpers.getItemBlacklist() != null) {
			IItemBlacklist itemBlacklist = helpers.getItemBlacklist();
			for (ItemStack stack : ArmorSet.disabledItems)
				if (!itemBlacklist.isItemBlacklisted(stack))
					itemBlacklist.addItemToBlacklist(stack);
			BlockArmor.logger.info("Added "+ArmorSet.disabledItems.size()+" disabled items to JEI blacklist");
		}
	}
}
