package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectSlippery extends SetEffect {

	protected SetEffectSlippery() {
		super();
		this.color = ChatFormatting.AQUA;
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (world.isClientSide && ArmorSet.getFirstSetItem(player, this) == stack && player.isOnGround())	{    
			if (player.zza == 0 && player.xxa == 0) {
				Block block = world.getBlockState(player.blockPosition().below()).getBlock();
				if (!(block instanceof AirBlock) && block.getFriction() <= 0.6f) {
					if (Math.abs(player.getDeltaMovement().x()) < 0.4d)
						player.setDeltaMovement(new Vec3(player.getDeltaMovement().x()*(player.isInWater() ? 1.2d : 1.6d), player.getDeltaMovement().y(), player.getDeltaMovement().z()));
					if (Math.abs(player.getDeltaMovement().z()) < 0.4d)
						player.setDeltaMovement(new Vec3(player.getDeltaMovement().x(), player.getDeltaMovement().y(), player.getDeltaMovement().z()*(player.isInWater() ? 1.2d : 1.6d)));
				}
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"waxed"}) || 
				(block.getFriction() > 0.6f && !SetEffect.registryNameContains(block, new String[] {"slime"})))
			return true;

		return false;
	}
}