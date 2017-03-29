package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectEnder extends SetEffect {

	protected SetEffectEnder() {
		this.color = TextFormatting.DARK_PURPLE;
		this.description = "Teleports in the direction you're looking";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldownTracker().hasCooldown(stack.getItem()))	{    
			this.setCooldown(player, 30);
			int distance = player.getRNG().nextInt(10) + 16;
			double rotX = - Math.sin(player.rotationYaw*Math.PI/180);
			double rotY = - Math.sin(player.rotationPitch*Math.PI/180);
			double rotZ = Math.cos(player.rotationYaw*Math.PI/180);
			double viewVectorLength = Math.sqrt(Math.pow(rotX, 2)+Math.pow(rotY, 2)+Math.pow(rotZ, 2));
			double x = player.posX + distance/viewVectorLength * rotX;
			double y = player.posY + distance/viewVectorLength * rotY;
			double z = player.posZ + distance/viewVectorLength * rotZ;

			BlockPos pos = new BlockPos(x, y, z);
			boolean posFound = false;
			for (int i = 0; i < 128; ++i) {
				double newX = 8*(world.rand.nextDouble()-0.5D);
				double newY = 8*(world.rand.nextDouble()-0.5D);
				double newZ = 8*(world.rand.nextDouble()-0.5D);
				if (!posFound && player.worldObj.isAirBlock(pos.add(newX, newY, newZ)) 
						&& player.worldObj.isAirBlock(pos.add(newX, newY+1, newZ)) 
						&& !player.worldObj.isAirBlock(pos.add(newX, newY-1, newZ)) 
						&& !(player.worldObj.getBlockState(pos.add(newX, newY-1, newZ)).getBlock() instanceof BlockLiquid)) {
					pos = pos.add(newX, newY, newZ);
					posFound = true;
					break;
				}
			}
			if (posFound && player.attemptTeleport(pos.getX()+0.5d, pos.getY(), pos.getZ()+0.5d)) {//if pos found and can tp
				if (player.isRiding())
					player.dismountRidingEntity();
				world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
				world.playSound((EntityPlayer)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
				player.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
				for (int j = 0; j < 64; ++j) {
					((WorldServer)world).spawnParticle(EnumParticleTypes.PORTAL, player.posX+2*world.rand.nextDouble(), player.posY+world.rand.nextDouble()+1, player.posZ+2*world.rand.nextDouble(), 1, 0, 0, 0, 1, new int[0]);
					((WorldServer)world).spawnParticle(EnumParticleTypes.PORTAL, player.posX+2*world.rand.nextDouble(), player.posY+world.rand.nextDouble()+1.0D, player.posZ+2*world.rand.nextDouble(), 1, 0, 0, 0, 1, new int[0]);
				}
			}
			else { //no valid pos found
				world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_NOTE_BASS, 
						SoundCategory.PLAYERS, 1.0F, world.rand.nextFloat() + 0.5F);	
				this.setCooldown(player, 10);
				this.damageArmor(player, 2, false);
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (SetEffect.registryNameContains(block, meta, new String[] {"end_stone", "end_bricks", "ender", "EndStone"}))
			return true;
		return false;
	}
}