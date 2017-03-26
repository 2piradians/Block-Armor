package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectMusical extends SetEffect {
	
	protected SetEffectMusical() {
		this.color = TextFormatting.LIGHT_PURPLE;
		this.description = "Every step you take becomes a musical melody";
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack &&
				!player.getCooldownTracker().hasCooldown(stack.getItem())) {
			
			double velX = player.posX - player.chasingPosX;
			double velZ = player.posZ - player.chasingPosZ;
			double motion = Math.sqrt(velX*velX + velZ*velZ);
			if (player.onGround && motion > 0.1d) {
				this.setCooldown(player, (int) Math.min(8/(motion),100));
				
				((WorldServer)world).spawnParticle(EnumParticleTypes.NOTE, 
						player.posX, player.posY+1f, player.posZ,1, 0.8f, 0.4f, 0.8f, world.rand.nextDouble(), new int[0]);
				
				Block block = world.getBlockState(player.getPosition().down()).getBlock();
				SoundEvent sound = SoundEvents.BLOCK_NOTE_HARP;
				
				if (block instanceof BlockLog || block instanceof BlockPlanks)
					sound = SoundEvents.BLOCK_NOTE_BASS;
				else if (block instanceof BlockSand	|| block instanceof BlockGravel)
					sound = SoundEvents.BLOCK_NOTE_SNARE;
				else if (block instanceof BlockGlass)
					sound = SoundEvents.BLOCK_NOTE_HAT;
				else if (block instanceof BlockStone)
					sound = SoundEvents.BLOCK_NOTE_BASEDRUM;

				world.playSound((EntityPlayer)null, player.getPosition(), sound, 
							SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat()*2);
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {	
		if (SetEffect.registryNameContains(block, meta, new String[] {"music", "note", "sound"}))
			return true;		
		return false;
	}	
}