package twopiradians.blockArmor.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import twopiradians.blockArmor.common.item.ArmorSet;

public class RecipeBlockArmor extends ShapedRecipes {

	private ArmorSet set;

	public RecipeBlockArmor(ArmorSet set, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result) {
		super(group, width, height, ingredients, result);
		this.set = set;
	}

	@Override
    public boolean isHidden() {
        return !set.isEnabled();
    }
	
	@Override
    public ItemStack getRecipeOutput() {
        return set.isEnabled() ? super.getRecipeOutput() : ItemStack.EMPTY;
    }
	
}
