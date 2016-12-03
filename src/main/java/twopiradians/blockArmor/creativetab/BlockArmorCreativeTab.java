package twopiradians.blockArmor.creativetab;

import net.minecraft.creativetab.CreativeTabs;
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
		return ArmorSet.MANUALLY_ADDED_SETS.get(0).chestplate;
	}
}

