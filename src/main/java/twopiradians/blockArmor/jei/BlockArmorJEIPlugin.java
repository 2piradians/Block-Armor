package twopiradians.blockArmor.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
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
				helpers != null && helpers.getIngredientBlacklist() != null) {
			IIngredientBlacklist itemBlacklist = helpers.getIngredientBlacklist();
			for (ItemStack stack : ArmorSet.disabledItems)
				if (!itemBlacklist.isIngredientBlacklisted(stack))
					itemBlacklist.addIngredientToBlacklist(stack);
			BlockArmor.logger.info("Added "+ArmorSet.disabledItems.size()+" disabled items to JEI blacklist");
		}
	}
}
