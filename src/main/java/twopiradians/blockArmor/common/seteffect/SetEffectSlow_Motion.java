package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectSlow_Motion extends SetEffect 
{
	protected SetEffectSlow_Motion() {
		this.color = TextFormatting.GRAY;
		this.description = "Live life in the slow lane";
		this.potionEffects.add(new PotionEffect(MobEffects.SLOWNESS, 10, 2, true, false));
	}


	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (world.isRemote && world.rand.nextInt(10) == 0) 
			world.spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, player.posX+world.rand.nextDouble()-0.5D, 
					player.posY+world.rand.nextDouble(), player.posZ+world.rand.nextDouble()-0.5D, 
					0, 0, 0, new int[0]);
		
		if (ArmorSet.getFirstSetItem(player, this) == stack && !player.isSneaking() && 
				player.motionY < 0) {
			player.fallDistance = 0;
			if (world.isRemote) {
				player.motionX *= 0.5d;
				player.motionY *= 0.4d;
				player.motionZ *= 0.5d;
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {	
		if (block instanceof BlockSoulSand)
			return true;	
		return false;
	}	
}