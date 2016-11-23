package twopiradians.blockArmor.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import twopiradians.blockArmor.common.item.ModItems;

public class BlockArmorCreativeTab extends CreativeTabs 
{
	public BlockArmorCreativeTab(String label) 
	{
		super(label);
	}

	@Override
	public Item getTabIconItem() {
		//return ModItems.dirtHelmet;
		return ModItems.bedrock_chestplate;
	}
	
}

