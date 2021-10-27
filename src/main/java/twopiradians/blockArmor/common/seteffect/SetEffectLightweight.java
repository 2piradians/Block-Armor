package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.item.ArmorSet;

@Mod.EventBusSubscriber
public class SetEffectLightweight extends SetEffect 
{
	protected SetEffectLightweight() {
		super();
		this.color = ChatFormatting.GREEN;
	}

	/**Reduce fall distance for less damage*/
	@SubscribeEvent
	public static void onEvent(LivingFallEvent event) {
		if (ArmorSet.getWornSetEffects(event.getEntityLiving()).contains(SetEffect.LIGHTWEIGHT)) {
			if (event.getEntityLiving().isShiftKeyDown()) {
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
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);
		if (!player.isShiftKeyDown() && ArmorSet.getFirstSetItem(player, this) == stack && player.getDeltaMovement().y() < 0 && !player.isOnGround() &&
				world.isEmptyBlock(player.blockPosition().below(2))) {

			if (world.isClientSide && player.fallDistance > 1 && player.getDeltaMovement().y() < -0.3d) {
				player.push(0, 0.076D, 0);
				if (player.fallDistance > 5) {			
					double driftX = world.random.nextDouble()-0.5d;
					double driftZ = world.random.nextDouble()-0.5d;

					player.push(driftX/5d, 0, driftZ/5d);
					if (Math.abs(driftX) + Math.abs(driftZ) >= 0.75d)
						world.playSound(player, player.blockPosition(), SoundEvents.GRASS_STEP, 
								SoundSource.PLAYERS, 0.08F, world.random.nextFloat()/5);
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