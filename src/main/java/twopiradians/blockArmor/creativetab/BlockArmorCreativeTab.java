package twopiradians.blockArmor.creativetab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import twopiradians.blockArmor.common.item.ArmorSet;

public class BlockArmorCreativeTab extends CreativeTabs 
{
	public ArrayList<ItemStack> orderedStacks = new ArrayList<ItemStack>();

	public BlockArmorCreativeTab(String label) 
	{
		super(label);
	}

	@Override
	public Item getTabIconItem() {
		if (orderedStacks.size() > 2)
			return orderedStacks.get(1).getItem();
		else
			return ArmorSet.getSet(Blocks.BEDROCK, 0).chestplate;
	}

	@Override
	public void displayAllRelevantItems(List<ItemStack> list)
	{
		list.clear();
		Iterator<ItemStack> it = orderedStacks.iterator();
		
		//remove disabled items from tab
		while (it.hasNext()) 
			if (ArmorSet.disabledItems.contains(it.next().getItem()))
				it.remove();
		
		list.addAll(orderedStacks);
	}
}

