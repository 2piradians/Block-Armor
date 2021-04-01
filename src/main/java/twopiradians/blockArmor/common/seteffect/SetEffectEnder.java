package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectEnder extends SetEffect {

	protected SetEffectEnder() {
		super();
		this.color = TextFormatting.DARK_PURPLE;
		this.description = "Teleports in the direction you're looking";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldownTracker().hasCooldown(stack.getItem()))	{    
			this.setCooldown(player, 30);
			int distance = player.getRNG().nextInt(10) + 16;
			double rotX = - Math.sin(player.rotationYaw*Math.PI/180);
			double rotY = - Math.sin(player.rotationPitch*Math.PI/180);
			double rotZ = Math.cos(player.rotationYaw*Math.PI/180);
			double viewVectorLength = Math.sqrt(Math.pow(rotX, 2)+Math.pow(rotY, 2)+Math.pow(rotZ, 2));
			double x = player.getPosX() + distance/viewVectorLength * rotX;
			double y = player.getPosY() + distance/viewVectorLength * rotY;
			double z = player.getPosZ() + distance/viewVectorLength * rotZ;

			BlockPos pos = new BlockPos(x, y, z);
			boolean posFound = false;
			for (int i = 0; i < 128; ++i) {
				double newX = 8*(world.rand.nextDouble()-0.5D);
				double newY = 8*(world.rand.nextDouble()-0.5D);
				double newZ = 8*(world.rand.nextDouble()-0.5D);
				if (!posFound && player.world.isAirBlock(pos.add(newX, newY, newZ)) 
						&& player.world.isAirBlock(pos.add(newX, newY+1, newZ)) 
						&& !player.world.isAirBlock(pos.add(newX, newY-1, newZ))) { 
					pos = pos.add(newX, newY, newZ);
					posFound = true;
					break;
				}
			}
			if (posFound && player.attemptTeleport(pos.getX()+0.5d, pos.getY(), pos.getZ()+0.5d, true)) {//if pos found and can tp
				if (player.isPassenger())
					player.stopRiding();
				world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
				world.playSound((PlayerEntity)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
				player.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
				for (int j = 0; j < 64; ++j) {
					((ServerWorld)world).spawnParticle(ParticleTypes.PORTAL, player.getPosX()+2*world.rand.nextDouble(), player.getPosY()+world.rand.nextDouble()+1, player.getPosZ()+2*world.rand.nextDouble(), 1, 0, 0, 0, 1);
					((ServerWorld)world).spawnParticle(ParticleTypes.PORTAL, player.getPosX()+2*world.rand.nextDouble(), player.getPosY()+world.rand.nextDouble()+1.0D, player.getPosZ()+2*world.rand.nextDouble(), 1, 0, 0, 0, 1);
				}
				player.fallDistance = 0;
			}
			else { //no valid pos found
				world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.BLOCK_NOTE_BLOCK_BASS, 
						SoundCategory.PLAYERS, 1.0F, world.rand.nextFloat() + 0.5F);	
				this.setCooldown(player, 10);
				this.damageArmor(player, 2, false);
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"end_stone", "end_bricks", "ender", "EndStone"}))
			return true;
		return false;
	}
}