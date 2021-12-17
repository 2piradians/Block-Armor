package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.BlockArmorItem;

public class SetEffectFalling extends SetEffect {

	private static final SetEffectDiving_Suit DIVING_SUIT = new SetEffectDiving_Suit();

	protected SetEffectFalling() {
		super();
		this.color = ChatFormatting.GRAY;
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		//particles
		if (!world.isClientSide && ArmorSet.getFirstSetItem(player, this) == stack &&
				world.random.nextInt(16) == 0) {
			ArmorSet set = ((BlockArmorItem)stack.getItem()).set;
			if (set != null && set.block instanceof FallingBlock)
				((ServerLevel)world).sendParticles(new BlockParticleOption(ParticleTypes.FALLING_DUST, set.block.defaultBlockState()), 
						player.getX(), player.getY()+1d,player.getZ(), 
						1, 0.3f, 0.5f, 0.3f, 0);
		}			
		//fall faster
		if (world.isClientSide && ArmorSet.getFirstSetItem(player, this) == stack &&
				player.isShiftKeyDown() && Math.abs(player.getDeltaMovement().y) < 3.5d && player.getDeltaMovement().y < 0)
			player.setDeltaMovement(player.getDeltaMovement().x, player.getDeltaMovement().y * 1.3d, player.getDeltaMovement().z);
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {	
		if (block instanceof FallingBlock || DIVING_SUIT.isValid(block) || SetEffect.registryNameContains(block, new String[] {"dripstone"}))
			return true;		
		return false;
	}
}