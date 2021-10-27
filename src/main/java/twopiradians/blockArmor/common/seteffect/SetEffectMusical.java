package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectMusical extends SetEffect {
	
	protected SetEffectMusical() {
		super();
		this.color = ChatFormatting.LIGHT_PURPLE;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isClientSide && ArmorSet.getFirstSetItem(player, this) == stack &&
				!player.getCooldowns().isOnCooldown(stack.getItem())) {
			
			double velX = player.getX() - player.xCloak;
			double velZ = player.getZ() - player.zCloak;
			double motion = Math.sqrt(velX*velX + velZ*velZ);
			if (player.isOnGround() && motion > 0.1d) {
				this.setCooldown(player, 10);
				
				((ServerLevel)world).sendParticles(ParticleTypes.NOTE, 
						player.getX(), player.getY()+1f, player.getZ(),1, 0.8f, 0.4f, 0.8f, world.random.nextDouble());
				world.playSound((Player)null, player.blockPosition(), 
						NoteBlockInstrument.byState(world.getBlockState(player.blockPosition().below())).getSoundEvent(), 
						SoundSource.RECORDS, 3.0F, world.random.nextFloat()*2);
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {	
		if (SetEffect.registryNameContains(block, new String[] {"music", "note", "sound", "jukebox"}))
			return true;		
		return false;
	}	
}