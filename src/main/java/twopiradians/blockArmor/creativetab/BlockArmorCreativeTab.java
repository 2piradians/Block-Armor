package twopiradians.blockArmor.creativetab;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
	public Item getTabIconItem() {
		if (BlockArmor.moddedTab == this && orderedStacks.size() > 2)
			return orderedStacks.get(1).getItem();
		else
			return ArmorSet.getSet(Blocks.BEDROCK, 0).chestplate;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllRelevantItems(List<ItemStack> list) {
		list.clear();
		list.addAll(orderedStacks);
	}
}