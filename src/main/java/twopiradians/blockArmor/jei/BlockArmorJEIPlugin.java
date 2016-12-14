package twopiradians.blockArmor.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IItemBlacklist;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

@JEIPlugin
public class BlockArmorJEIPlugin extends BlankModPlugin 
{
	private IJeiHelpers helpers;

	@Override
	public void register(IModRegistry registry) {
		helpers = registry.getJeiHelpers();
		BlockArmor.jeiPlugin = this;
	}

	/**Removes disabled items from JEI gui*/
	public void removeDisabledItems() {
		if (helpers != null) {
			IItemBlacklist blacklist = helpers.getItemBlacklist();
			boolean reload = false;
			if (blacklist != null && ArmorSet.disabledItems != null && !ArmorSet.disabledItems.isEmpty()) 
				for (Item item : ArmorSet.disabledItems) 
					if (!blacklist.isItemBlacklisted(new ItemStack(item))) {
						blacklist.addItemToBlacklist(new ItemStack(item));
						reload = true;
					}
			if (reload) {
				BlockArmor.logger.info("Removing disabled items from JEI");
				helpers.reload();
			}
		}
	}
}
