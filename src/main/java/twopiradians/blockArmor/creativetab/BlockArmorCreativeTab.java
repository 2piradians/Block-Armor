package twopiradians.blockArmor.creativetab;

import java.util.ArrayList;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class BlockArmorCreativeTab extends CreativeTabs 
{
	public ArrayList<ItemStack> orderedStacks = new ArrayList<ItemStack>();

	public BlockArmorCreativeTab(String label) {
		super(label);
	}

	@Override
	public ItemStack getTabIconItem() {
		if (BlockArmor.moddedTab == this && orderedStacks.size() > 2)
			return orderedStacks.get(1);
		else if (ArmorSet.getSet(Blocks.BEDROCK, 0) != null)
			return new ItemStack(ArmorSet.getSet(Blocks.BEDROCK, 0).chestplate);
		else if (orderedStacks.size() > 2)
			return orderedStacks.get(1);
		else
			return new ItemStack(Items.IRON_CHESTPLATE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllRelevantItems(NonNullList<ItemStack> list) {
		list.clear();
		list.addAll(orderedStacks);
	}
}