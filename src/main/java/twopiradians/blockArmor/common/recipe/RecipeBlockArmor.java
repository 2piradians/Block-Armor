package twopiradians.blockArmor.common.recipe;

import com.google.gson.JsonObject;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;
import twopiradians.blockArmor.common.item.ArmorSet;

public class RecipeBlockArmor extends ShapedOreRecipe {

	public RecipeBlockArmor(ResourceLocation group, ItemStack result, ShapedPrimer primer) {
		super(group, result, primer);
	}

	@Override
	public ItemStack getCraftingResult(final InventoryCrafting inv) {
		// make sure all items are the same
		ItemStack item = null;
		boolean allSame = true;
		for (int i=0; i<inv.getSizeInventory(); ++i)
			if (!inv.getStackInSlot(i).isEmpty()) {
				if (item != null && !item.isEmpty() && !ItemStack.areItemsEqual(inv.getStackInSlot(i), item))
					allSame = false;
				else
					item = inv.getStackInSlot(i);
			}
		
		ArmorSet set = ArmorSet.getSet(item.getItem(), item.getMetadata());
		ItemStack defaultOutput = super.getCraftingResult(inv); 
		if (set == null || defaultOutput == null || !(defaultOutput.getItem() instanceof ItemArmor) || !allSame || item == null || item.isEmpty())
			return ItemStack.EMPTY;

		return new ItemStack(set.getArmorForSlot(((ItemArmor)defaultOutput.getItem()).getEquipmentSlot()));
	}

	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse(final JsonContext context, final JsonObject json) {
			final String group = JsonUtils.getString(json, "group", "");
			final CraftingHelper.ShapedPrimer primer = RecipeUtil.parseShaped(context, json);
			final ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

			return new RecipeBlockArmor(new ResourceLocation(group), result, primer);
		}
	}

}
