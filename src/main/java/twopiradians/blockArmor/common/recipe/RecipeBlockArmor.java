package twopiradians.blockArmor.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import twopiradians.blockArmor.common.item.ArmorSet;

public class RecipeBlockArmor extends ShapedRecipe {

	private ArmorSet set;

	public RecipeBlockArmor(ResourceLocation loc, ArmorSet set, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result) {
		super(loc, group, width, height, ingredients, result);
		this.set = set;
	}
	
	@Override
	public ItemStack getRecipeOutput() {
		return set.isEnabled() ? super.getRecipeOutput() : ItemStack.EMPTY;
	}

}
