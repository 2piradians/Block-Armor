package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.BlockArmorItem;

public class SetEffectFalling extends SetEffect {

	private static final SetEffectDiving_Suit DIVING_SUIT = new SetEffectDiving_Suit();

	protected SetEffectFalling() {
		super();
		this.color = TextFormatting.GRAY;
		this.description = "Falls faster in air and sinks faster in water while sneaking";
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		//particles
		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack &&
				world.rand.nextInt(16) == 0) {
			ArmorSet set = ((BlockArmorItem)stack.getItem()).set;
			if (set != null && set.block instanceof FallingBlock)
				((ServerWorld)world).spawnParticle(new BlockParticleData(ParticleTypes.FALLING_DUST, set.block.getDefaultState()), 
						player.getPosX(), player.getPosY()+1d,player.getPosZ(), 
						1, 0.3f, 0.5f, 0.3f, 0);
		}			
		//fall faster
		if (world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack &&
				player.isSneaking() && Math.abs(player.getMotion().y) < 3.5d && player.getMotion().y < 0)
			player.setMotion(player.getMotion().x, player.getMotion().y * 1.3d, player.getMotion().z);
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {	
		if (block instanceof FallingBlock || DIVING_SUIT.isValid(block))
			return true;		
		return false;
	}
}