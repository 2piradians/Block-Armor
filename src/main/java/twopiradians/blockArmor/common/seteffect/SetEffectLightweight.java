package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectLightweight extends SetEffect 
{
	protected SetEffectLightweight() {
		this.color = TextFormatting.GREEN;
		this.description = "Fall slowly, like a leaf in the wind";
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	/**Reduce fall distance for less damage*/
	@SubscribeEvent
	public void onEvent(LivingFallEvent event) {//TODO change to damage based on velocity?
		/*if (ArmorSet.getWornSetEffects(event.getEntityLiving()).contains(this)) {
			event.setDistance(0);
			event.setDamageMultiplier(0);
		}*/
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);
		if (!player.isSneaking() && ArmorSet.getFirstSetItem(player, this) == stack && player.motionY < 0 && !player.onGround &&
				world.isAirBlock(player.getPosition().down(2))) {
			player.fallDistance = Math.min(player.fallDistance, 5);
			if (player.fallDistance > 1 && player.ticksExisted % 8 == 0)
				player.fallDistance--;

			if (world.isRemote) {
				double driftX = world.rand.nextDouble()-0.5d;
				double driftZ = world.rand.nextDouble()-0.5d;

				player.addVelocity(driftX/5, 0.065d, driftZ/5);
				if (Math.abs(driftX) + Math.abs(driftZ) >= 0.75d)
					world.playSound(player, player.getPosition(), SoundEvents.BLOCK_GRASS_STEP, 
							SoundCategory.PLAYERS, 0.08F, world.rand.nextFloat()/5);
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {	
		try {
			if (SetEffect.registryNameContains(block, meta, new String[] {"leaves", "feather"}) || 
					block.isLeaves(null, null, BlockPos.ORIGIN))
				return true;	
		}
		catch (Exception e) {
			return false;
		}
		return false;
	}	
}