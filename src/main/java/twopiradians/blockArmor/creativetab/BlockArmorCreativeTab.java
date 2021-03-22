package twopiradians.blockArmor.creativetab;

import java.util.ArrayList;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class BlockArmorCreativeTab extends ItemGroup 
{
	public ArrayList<ItemStack> orderedStacks = new ArrayList<ItemStack>();

	public BlockArmorCreativeTab(String label) {
		super(label);
	}

	@Override
	public ItemStack createIcon() {
		if (BlockArmor.moddedTab == this && orderedStacks.size() > 2)
			return orderedStacks.get(1);
		else if (ArmorSet.getSet(Blocks.BEDROCK) != null)
			return new ItemStack(ArmorSet.getSet(Blocks.BEDROCK).chestplate);
		else if (orderedStacks.size() > 2)
			return orderedStacks.get(1);
		else
			return new ItemStack(Items.IRON_CHESTPLATE);
	}

}