package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectSnowy extends SetEffect {

	protected SetEffectSnowy() {
		super();
		this.color = ChatFormatting.WHITE;
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (BlockArmor.key.isKeyDown(player) && ArmorSet.getFirstSetItem(player, this) == stack) {
			int radius = 3;
			if (!world.isClientSide) {
				((ServerLevel)world).sendParticles(ParticleTypes.WHITE_ASH, player.getX()+(world.random.nextDouble()-0.5D)*radius, 
						player.getY()+world.random.nextDouble()+2D, player.getZ()+(world.random.nextDouble()-0.5D)*radius, 
						5, 0, 0, 0, 0);
				for (int i=0; i<2; ++i)
					((ServerLevel)world).sendParticles(ParticleTypes.CLOUD, player.getX()+(world.random.nextDouble()-0.5D)*radius, 
							player.getY()+world.random.nextDouble()*0.5d+2.5D, player.getZ()+(world.random.nextDouble()-0.5D)*radius, 
							5, 0, 0, 0, 0);
				if (world.random.nextInt(2) == 0)
					world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WEATHER_RAIN, 
							player.getSoundSource(), 0.1f, world.random.nextFloat());
				for (int x=-radius/2; x<=radius/2; x++)
					for (int z=-radius/2; z<=radius/2; z++)
						for (int y=0; y<=2; y++) {
							BlockPos pos = player.blockPosition().offset(x, y, z);
							BlockPos posBelow = player.blockPosition().offset(x, y-1, z);
							if (player.mayBuild() && world.random.nextInt(100) == 0 && world.isEmptyBlock(pos)) {
								if (Blocks.SNOW.defaultBlockState().canSurvive(world, pos))
									world.setBlockAndUpdate(pos, Blocks.SNOW.defaultBlockState());
								else if (world.getBlockState(posBelow) == Blocks.WATER.defaultBlockState())
									world.setBlockAndUpdate(posBelow, Blocks.FROSTED_ICE.defaultBlockState());
								else if (world.getBlockState(posBelow).getBlock() == Blocks.FROSTED_ICE)
									world.setBlockAndUpdate(posBelow, Blocks.FROSTED_ICE.defaultBlockState());
							}
						}
				//spawn snowballs
				if (world.random.nextInt(15) == 0) {
					ItemEntity item = new ItemEntity(world, player.getX()+(world.random.nextDouble()-0.5D)*radius, 
							player.getY()+world.random.nextDouble()+1.5D, player.getZ()+(world.random.nextDouble()-0.5D)*radius,
							new ItemStack(Items.SNOWBALL));
					item.setPickUpDelay(40);
					world.addFreshEntity(item);
					world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOW_STEP, 
							player.getSoundSource(), 1.0f, world.random.nextFloat());
				}
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"snow"}))
			return true;		
		return false;
	}
}