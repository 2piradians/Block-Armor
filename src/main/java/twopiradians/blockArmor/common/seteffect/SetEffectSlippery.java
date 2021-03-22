package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectSlippery extends SetEffect {

	protected SetEffectSlippery() {
		this.color = TextFormatting.AQUA;
		this.description = "Conserves velocity more while moving";
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack && player.isOnGround())	{    
			if (player.moveForward == 0 && player.moveStrafing == 0) {
				Block block = world.getBlockState(player.getPosition().down()).getBlock();
				if (!(block instanceof AirBlock) && block.getSlipperiness() <= 0.6f) {
					if (Math.abs(player.getMotion().getX()) < 0.4d)
						player.setMotion(new Vector3d(player.getMotion().getX()*(player.isInWater() ? 1.2d : 1.6d), player.getMotion().getY(), player.getMotion().getZ()));
					if (Math.abs(player.getMotion().getZ()) < 0.4d)
						player.setMotion(new Vector3d(player.getMotion().getX(), player.getMotion().getY(), player.getMotion().getZ()*(player.isInWater() ? 1.2d : 1.6d)));
				}
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (block.getSlipperiness() > 0.6f && !SetEffect.registryNameContains(block, new String[] {"slime"}))
			return true;

		return false;
	}
}