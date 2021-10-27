package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.item.ArmorSet;

@Mod.EventBusSubscriber
public class SetEffectSlimey extends SetEffect {

	/**
	 * Static is fine bc this is only used on client - chances of two players
	 * bouncing same tick is very slim
	 */
	private static LivingEntity bouncingEntity;
	private static double motionY;

	protected SetEffectSlimey() {
		super();
		this.color = ChatFormatting.GREEN;
	}

	/** Only called when player wearing full, enabled set */
	@Override
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack && world.isClientSide && !player.isShiftKeyDown()) {	
			//increased movement speed while bouncing
			if (!player.isOnGround() && !player.isFallFlying()) 
				player.setDeltaMovement(player.getDeltaMovement().x*1.07d, player.getDeltaMovement().y, player.getDeltaMovement().z*1.07d);
		
			if (!player.getCooldowns().isOnCooldown(stack.getItem()) && player.horizontalCollision 
					&& Math.sqrt(Math.pow(player.getX() - player.xCloakO, 2) + 
							Math.pow(player.getZ() - player.zCloakO, 2)) >= 1.1D) {	
				this.setCooldown(player, 10);
				double multiplier = 0.1d;
				if (player.getDeltaMovement().x == 0) 
					player.setDeltaMovement(
							-(player.getX() - player.xCloakO)*multiplier, 
							player.getDeltaMovement().y+0.1d, 
							(player.getZ() - player.zCloakO)*multiplier);
				else if (player.getDeltaMovement().z == 0) 
					player.setDeltaMovement(
							(player.getX() - player.xCloakO)*multiplier, 
							player.getDeltaMovement().y+0.1d, 
							-(player.getZ() - player.zCloakO)*multiplier);
				world.playSound(player, player.getX(), player.getY(), player.getZ(), 
						SoundEvents.SLIME_BLOCK_FALL, SoundSource.BLOCKS, 0.4F, 1.0F);
			}
		}
	}

	@SubscribeEvent
	public static void onEvent(LivingFallEvent event) {
		if (ArmorSet.hasSetEffect(event.getEntityLiving(), SetEffect.SLIMEY)) {
			if (!(event.getEntity() instanceof Player))
				return;

			Player player = (Player) event.getEntity();
			if (!player.isShiftKeyDown()) {
				event.setDamageMultiplier(0);
				if (player.level.isClientSide) {
					if (event.getDistance() <= 40 && event.getDistance() > 2D) 
						player.setDeltaMovement(player.getDeltaMovement().x, Math.abs(player.getDeltaMovement().y * 0.9d), player.getDeltaMovement().z);
					else if (event.getDistance() > 40 && event.getDistance() <= 100) 
						player.setDeltaMovement(player.getDeltaMovement().x, Math.abs(player.getDeltaMovement().y * 0.9d * 1.5D), player.getDeltaMovement().z);
					else if (event.getDistance() > 100) 
						player.setDeltaMovement(player.getDeltaMovement().x, Math.abs(player.getDeltaMovement().y * 0.9d * 2D), player.getDeltaMovement().z);
				
					if (event.getDistance() > 2D)
						player.level.playSound(player, player.getX(), player.getY(), player.getZ(), 
								event.getDistance() > 40 ? SoundEvents.SLIME_JUMP : SoundEvents.SLIME_SQUISH, 
										SoundSource.PLAYERS, 0.4F, 1.0F);
					player.hasImpulse = true;
					player.setOnGround(false);
					player.hurtMarked = true;
					bouncingEntity = player;
					motionY = player.getDeltaMovement().y();
				}
				else 
					event.setCanceled(true);
			}
			else
				event.setDamageMultiplier(0.1f);
		}
	}

	@SubscribeEvent
	public static void onEvent(TickEvent.PlayerTickEvent event) {
		if (bouncingEntity != null && event.player == bouncingEntity && bouncingEntity.level.isClientSide &&
				event.phase == TickEvent.Phase.END) {
			bouncingEntity.setDeltaMovement(bouncingEntity.getDeltaMovement().x, motionY, bouncingEntity.getDeltaMovement().z);
			bouncingEntity.fallDistance = 0;
			bouncingEntity = null;
		}
	}

	/** Should block be given this set effect */
	@Override
	protected boolean isValid(Block block) {
		if (SetEffect.registryNameContains(block, new String[] { "slime" }))
			return true;
		return false;
	}
}