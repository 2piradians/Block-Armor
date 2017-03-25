package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectSlow_Motion extends SetEffect 
{
	protected SetEffectSlow_Motion() {
		this.color = TextFormatting.GRAY;
		this.description = "Live life in the slow lane";
		this.attributeModifiers.add(new AttributeModifier(MOVEMENT_SPEED_UUID, 
				SharedMonsterAttributes.MOVEMENT_SPEED.getName(), -0.6d, 0));
	}


	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack && !player.isSneaking()) {
			player.fallDistance = 0;
			player.motionY *= 0.4d;
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {	
		if (block instanceof BlockSoulSand)
			return true;	
		return false;
	}	
}