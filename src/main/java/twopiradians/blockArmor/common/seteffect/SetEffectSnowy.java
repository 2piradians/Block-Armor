package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectSnowy extends SetEffect {

	protected SetEffectSnowy() {
		super();
		this.color = TextFormatting.WHITE;
		this.description = "Spawns snow and snowballs";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (BlockArmor.key.isKeyDown(player) && ArmorSet.getFirstSetItem(player, this) == stack) {
			int radius = 3;
			if (!world.isRemote) {
				((ServerWorld)world).spawnParticle(ParticleTypes.ITEM_SNOWBALL, player.getPosX()+(world.rand.nextDouble()-0.5D)*radius, 
						player.getPosY()+world.rand.nextDouble()+2D, player.getPosZ()+(world.rand.nextDouble()-0.5D)*radius, 
						1, 0, 0, 0, 0);
				((ServerWorld)world).spawnParticle(ParticleTypes.CLOUD, player.getPosX()+(world.rand.nextDouble()-0.5D)*radius, 
						player.getPosY()+world.rand.nextDouble()*0.5d+2.5D, player.getPosZ()+(world.rand.nextDouble()-0.5D)*radius, 
						3, 0, 0, 0, 0);
				if (world.rand.nextInt(2) == 0)
					world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.WEATHER_RAIN, 
							player.getSoundCategory(), 0.1f, world.rand.nextFloat());
				for (int x=-radius/2; x<=radius/2; x++)
					for (int z=-radius/2; z<=radius/2; z++)
						for (int y=0; y<=2; y++) {
							BlockPos pos = player.getPosition().add(x, y, z);
							BlockPos posBelow = player.getPosition().add(x, y-1, z);
							if (player.isAllowEdit() && world.rand.nextInt(100) == 0 && world.isAirBlock(pos)) {
								if (Blocks.SNOW.getDefaultState().isValidPosition(world, pos))
									world.setBlockState(pos, Blocks.SNOW.getDefaultState());
								else if (world.getBlockState(posBelow) == Blocks.WATER.getDefaultState())
									world.setBlockState(posBelow, Blocks.FROSTED_ICE.getDefaultState());
								else if (world.getBlockState(posBelow).getBlock() == Blocks.FROSTED_ICE)
									world.setBlockState(posBelow, Blocks.FROSTED_ICE.getDefaultState());
							}
						}
				//spawn snowballs
				if (world.rand.nextInt(15) == 0) {
					ItemEntity item = new ItemEntity(world, player.getPosX()+(world.rand.nextDouble()-0.5D)*radius, 
							player.getPosY()+world.rand.nextDouble()+1.5D, player.getPosZ()+(world.rand.nextDouble()-0.5D)*radius,
							new ItemStack(Items.SNOWBALL));
					item.setPickupDelay(40);
					world.addEntity(item);
					world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.BLOCK_SNOW_STEP, 
							player.getSoundCategory(), 1.0f, world.rand.nextFloat());
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