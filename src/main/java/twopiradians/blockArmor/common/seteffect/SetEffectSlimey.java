package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
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
		this.color = TextFormatting.GREEN;
		this.description = "Bounces off walls and floors";
	}

	/** Only called when player wearing full, enabled set */
	@Override
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack && world.isRemote && !player.isSneaking()) {	
			//increased movement speed while bouncing
			if (!player.isOnGround() && !player.isElytraFlying()) 
				player.setMotion(player.getMotion().x*1.07d, player.getMotion().y, player.getMotion().z*1.07d);
		
			if (!player.getCooldownTracker().hasCooldown(stack.getItem()) && player.collidedHorizontally 
					&& Math.sqrt(Math.pow(player.getPosX() - player.prevChasingPosX, 2) + 
							Math.pow(player.getPosZ() - player.prevChasingPosZ, 2)) >= 1.1D) {	
				this.setCooldown(player, 10);
				double multiplier = 0.1d;
				if (player.getMotion().x == 0) 
					player.setMotion(
							-(player.getPosX() - player.prevChasingPosX)*multiplier, 
							player.getMotion().y+0.1d, 
							(player.getPosZ() - player.prevChasingPosZ)*multiplier);
				else if (player.getMotion().z == 0) 
					player.setMotion(
							(player.getPosX() - player.prevChasingPosX)*multiplier, 
							player.getMotion().y+0.1d, 
							-(player.getPosZ() - player.prevChasingPosZ)*multiplier);
				world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), 
						SoundEvents.BLOCK_SLIME_BLOCK_FALL, SoundCategory.BLOCKS, 0.4F, 1.0F);
			}
		}
	}

	@SubscribeEvent
	public static void onEvent(LivingFallEvent event) {
		if (ArmorSet.hasSetEffect(event.getEntityLiving(), SetEffect.SLIMEY)) {
			if (!(event.getEntity() instanceof PlayerEntity))
				return;

			PlayerEntity player = (PlayerEntity) event.getEntity();
			if (!player.isSneaking()) {
				event.setDamageMultiplier(0);
				if (player.world.isRemote) {
					if (event.getDistance() <= 40 && event.getDistance() > 2D) 
						player.setMotion(player.getMotion().x, Math.abs(player.getMotion().y * 0.9d), player.getMotion().z);
					else if (event.getDistance() > 40 && event.getDistance() <= 100) 
						player.setMotion(player.getMotion().x, Math.abs(player.getMotion().y * 0.9d * 1.5D), player.getMotion().z);
					else if (event.getDistance() > 100) 
						player.setMotion(player.getMotion().x, Math.abs(player.getMotion().y * 0.9d * 2D), player.getMotion().z);
				
					if (event.getDistance() > 2D)
						player.world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), 
								event.getDistance() > 40 ? SoundEvents.ENTITY_SLIME_JUMP : SoundEvents.ENTITY_SLIME_SQUISH, 
										SoundCategory.PLAYERS, 0.4F, 1.0F);
					player.isAirBorne = true;
					player.setOnGround(false);
					player.velocityChanged = true;
					bouncingEntity = player;
					motionY = player.getMotion().getY();
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
		if (bouncingEntity != null && event.player == bouncingEntity && bouncingEntity.world.isRemote &&
				event.phase == TickEvent.Phase.END) {
			bouncingEntity.setMotion(bouncingEntity.getMotion().x, motionY, bouncingEntity.getMotion().z);
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