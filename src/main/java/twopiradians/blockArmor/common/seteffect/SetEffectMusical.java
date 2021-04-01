package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectMusical extends SetEffect {
	
	protected SetEffectMusical() {
		super();
		this.color = TextFormatting.LIGHT_PURPLE;
		this.description = "Every step you take becomes a musical melody";
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack &&
				!player.getCooldownTracker().hasCooldown(stack.getItem())) {
			
			double velX = player.getPosX() - player.chasingPosX;
			double velZ = player.getPosZ() - player.chasingPosZ;
			double motion = Math.sqrt(velX*velX + velZ*velZ);
			if (player.isOnGround() && motion > 0.1d) {
				this.setCooldown(player, (int) Math.min(8/(motion),100));
				
				((ServerWorld)world).spawnParticle(ParticleTypes.NOTE, 
						player.getPosX(), player.getPosY()+1f, player.getPosZ(),1, 0.8f, 0.4f, 0.8f, world.rand.nextDouble());
				world.playSound((PlayerEntity)null, player.getPosition(), 
						NoteBlockInstrument.byState(world.getBlockState(player.getPosition().down())).getSound(), 
						SoundCategory.RECORDS, 3.0F, world.rand.nextFloat()*2);
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {	
		if (SetEffect.registryNameContains(block, new String[] {"music", "note", "sound"}))
			return true;		
		return false;
	}	
}