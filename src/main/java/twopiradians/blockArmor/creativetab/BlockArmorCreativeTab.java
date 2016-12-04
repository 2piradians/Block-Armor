package twopiradians.blockArmor.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import twopiradians.blockArmor.common.item.ArmorSet;

public class BlockArmorCreativeTab extends CreativeTabs 
{
	public BlockArmorCreativeTab(String label) 
	{
		super(label);
	}

	@Override
	public Item getTabIconItem() {
		return ArmorSet.getSet(Blocks.BEDROCK, 0).chestplate;
	}
}

