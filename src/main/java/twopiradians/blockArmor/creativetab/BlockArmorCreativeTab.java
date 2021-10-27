package twopiradians.blockArmor.creativetab;

import java.util.ArrayList;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import twopiradians.blockArmor.common.item.ArmorSet;

public class BlockArmorCreativeTab extends CreativeModeTab {
	
	public static BlockArmorCreativeTab vanillaTab;
	public static BlockArmorCreativeTab moddedTab;
	public ArrayList<ItemStack> orderedStacks = new ArrayList<ItemStack>();

	public BlockArmorCreativeTab(String label) {
		super(label);
	}

	@Override
	public ItemStack makeIcon() {
		if (moddedTab == this && orderedStacks.size() > 2)
			return orderedStacks.get(1);
		else if (ArmorSet.getSet(Blocks.BEDROCK) != null)
			return new ItemStack(ArmorSet.getSet(Blocks.BEDROCK).chestplate);
		else if (orderedStacks.size() > 2)
			return orderedStacks.get(1);
		else
			return new ItemStack(Items.IRON_CHESTPLATE);
	}

}