package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.item.ArmorSet;

@Mod.EventBusSubscriber
public class SetEffectLightweight extends SetEffect 
{
	protected SetEffectLightweight() {
		super();
		this.color = TextFormatting.GREEN;
		this.description = "Falls slowly, like a leaf in the wind";
	}

	/**Reduce fall distance for less damage*/
	@SubscribeEvent
	public static void onEvent(LivingFallEvent event) {
		if (ArmorSet.getWornSetEffects(event.getEntityLiving()).contains(SetEffect.LIGHTWEIGHT)) {
			if (event.getEntityLiving().isSneaking()) {
				event.setDamageMultiplier(0.1f);
			}
			else {
				event.setDamageMultiplier(0.01f);
				event.setDistance(event.getDistance()/10f);
			}
		}
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);
		if (!player.isSneaking() && ArmorSet.getFirstSetItem(player, this) == stack && player.getMotion().getY() < 0 && !player.isOnGround() &&
				world.isAirBlock(player.getPosition().down(2))) {

			if (world.isRemote && player.fallDistance > 1 && player.getMotion().getY() < -0.3d) {
				player.addVelocity(0, 0.076D, 0);
				if (player.fallDistance > 5) {			
					double driftX = world.rand.nextDouble()-0.5d;
					double driftZ = world.rand.nextDouble()-0.5d;

					player.addVelocity(driftX/5d, 0, driftZ/5d);
					if (Math.abs(driftX) + Math.abs(driftZ) >= 0.75d)
						world.playSound(player, player.getPosition(), SoundEvents.BLOCK_GRASS_STEP, 
								SoundCategory.PLAYERS, 0.08F, world.rand.nextFloat()/5);
				}
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {	
		try {
			if (SetEffect.registryNameContains(block, new String[] {"leaves", "feather"}) || 
					block instanceof LeavesBlock)
				return true;	
		}
		catch (Exception e) {
			return false;
		}
		return false;
	}	
}