package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoulSandBlock;
import net.minecraft.world.phys.Vec3;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectSlow_Motion extends SetEffect 
{
	protected SetEffectSlow_Motion() {
		super();
		this.color = ChatFormatting.GRAY;
		this.potionEffects.add(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 2, true, false));
	}


	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		// particles
		if (world.isClientSide && world.random.nextInt(10) == 0 && 
				(player.getDeltaMovement().x != 0 || player.getDeltaMovement().z != 0 || !player.isOnGround())) 
			world.addParticle(ParticleTypes.SOUL, player.getX()+world.random.nextDouble()-0.5D, 
					player.getY()+world.random.nextDouble()+0.3d, player.getZ()+world.random.nextDouble()-0.5D, 
					0, 0, 0);

		if (ArmorSet.getFirstSetItem(player, this) == stack && !player.isShiftKeyDown()) {
			player.fallDistance = 0;
			if (world.isClientSide) { // don't add motion y to try to slow down in case they're in a place with lower gravity
				Vec3 motion = player.getDeltaMovement();
				player.setDeltaMovement(motion.x*0.7d, motion.y < 0 ? motion.y*0.7d : motion.y, motion.z*0.7d);
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {	
		if (block instanceof SoulSandBlock || 
				block == Blocks.SOUL_SOIL ||
				SetEffect.registryNameContains(block, new String[] {"soul_soil", "soul_sand"}))
			return true;	
		return false;
	}	
}