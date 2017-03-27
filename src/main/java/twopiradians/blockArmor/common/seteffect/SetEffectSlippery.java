package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectSlippery extends SetEffect {

	protected SetEffectSlippery() {
		this.color = TextFormatting.AQUA;
		this.description = "Conserves velocity more while moving";
		this.attributeModifiers.add(new AttributeModifier(MOVEMENT_SPEED_UUID, 
				SharedMonsterAttributes.MOVEMENT_SPEED.getName(), 0.05d, 0));
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack && player.onGround)	{    
			if (player.moveForward == 0 && player.moveStrafing == 0) {
				Block block = world.getBlockState(player.getPosition().down()).getBlock();
				if (!(block instanceof BlockAir) && block.slipperiness <= 0.6f) {
					if (Math.abs(player.motionX) < 0.4d)
						player.motionX *= player.isInWater() ? 1.3d : 1.6d;
					if (Math.abs(player.motionZ) < 0.4d)
						player.motionZ *= player.isInWater() ? 1.3d : 1.6d;
				}
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (block.slipperiness > 0.6f && !SetEffect.registryNameContains(block, meta, new String[] {"slime"}))
			return true;

		return false;
	}
}