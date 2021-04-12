package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectSlow_Motion extends SetEffect 
{
	protected SetEffectSlow_Motion() {
		super();
		this.color = TextFormatting.GRAY;
		this.description = "Live life in the slow lane";
		this.potionEffects.add(new EffectInstance(Effects.SLOWNESS, 10, 2, true, false));
	}


	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		// particles
		if (world.isRemote && world.rand.nextInt(10) == 0) 
			world.addParticle(ParticleTypes.SOUL, player.getPosX()+world.rand.nextDouble()-0.5D, 
					player.getPosY()+world.rand.nextDouble()+0.3d, player.getPosZ()+world.rand.nextDouble()-0.5D, 
					0, 0, 0);

		if (ArmorSet.getFirstSetItem(player, this) == stack && !player.isSneaking()) {
			player.fallDistance = 0;
			if (world.isRemote) { // don't add motion y to try to slow down in case they're in a place with lower gravity
				Vector3d motion = player.getMotion();
				player.setMotion(motion.x*0.7d, motion.y < 0 ? motion.y*0.7d : motion.y, motion.z*0.7d);
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {	
		if (block instanceof SoulSandBlock || 
				block == Blocks.SOUL_SOIL)
			return true;	
		return false;
	}	
}