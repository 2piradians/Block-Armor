package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectSlimey extends SetEffect {

	/**Static is fine bc this is only used on client - chances of two players bouncing same tick is very slim*/
	private static EntityLivingBase bouncingEntity;
	private static double motionY;

	protected SetEffectSlimey() {
		this.color = TextFormatting.GREEN;
		this.description = "Bounces off walls and floors";
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack && world.isRemote && !player.isSneaking()) {	
			//increased movement speed while bouncing
			if (!player.onGround) {
				player.motionX *= 1.07d;
				player.motionZ *= 1.07d;
			}

			if (!player.getCooldownTracker().hasCooldown(stack.getItem()) && player.isCollidedHorizontally 
					&& Math.sqrt(Math.pow(player.posX - player.prevChasingPosX, 2) + 
							Math.pow(player.posZ - player.prevChasingPosZ, 2)) >= 1.1D) {	
				this.setCooldown(player, 10);
				double multiplier = 0.1d;
				if (player.motionX == 0) {
					player.motionX = -(player.posX - player.prevChasingPosX)*multiplier;
					player.motionZ = (player.posZ - player.prevChasingPosZ)*multiplier;
				}
				else if (player.motionZ == 0) {
					player.motionX = (player.posX - player.prevChasingPosX)*multiplier;
					player.motionZ = -(player.posZ - player.prevChasingPosZ)*multiplier;
				}
				player.motionY += 0.1;
				world.playSound(player, player.posX, player.posY, player.posZ, 
						SoundEvents.BLOCK_SLIME_FALL, SoundCategory.BLOCKS, 0.4F, 1.0F);
			}
		}
	}

	@SubscribeEvent
	public void onEvent(LivingFallEvent event) {
		if (ArmorSet.getWornSetEffects(event.getEntityLiving()).contains(this)) {
			if (!(event.getEntity() instanceof EntityPlayer))
				return;

			EntityPlayer player = (EntityPlayer) event.getEntity();

			if (!player.isSneaking()) {
				event.setDamageMultiplier(0);
				if (player.worldObj.isRemote) {
					if (event.getDistance() <= 40 && event.getDistance() > 2D) 
						player.motionY = Math.abs(player.motionY * 0.9d);
					else if (event.getDistance() > 40 && event.getDistance() <= 100) 
						player.motionY = Math.abs(player.motionY * 0.9d * 1.5D);
					else if (event.getDistance() > 100) 
						player.motionY = Math.abs(player.motionY * 0.9d * 2D);

					if (event.getDistance() > 2D)
						player.worldObj.playSound(player, player.posX, player.posY, player.posZ, 
								event.getDistance() > 40 ? SoundEvents.ENTITY_SLIME_JUMP : SoundEvents.ENTITY_SLIME_SQUISH, 
										SoundCategory.PLAYERS, 0.4F, 1.0F);
					player.isAirBorne = true;
					player.onGround = false;
					bouncingEntity = player;
					motionY = player.motionY;
				}
				else 
					event.setCanceled(true);
			}
			else
				event.setDamageMultiplier(0.1f);
		}
	}

	@SubscribeEvent
	public void onEvent(TickEvent.PlayerTickEvent event) {

		if (bouncingEntity != null && event.player == bouncingEntity && bouncingEntity.worldObj.isRemote &&
				event.phase == TickEvent.Phase.END) {
			bouncingEntity.motionY = motionY;
			bouncingEntity.fallDistance = 0;
			bouncingEntity = null;
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (SetEffect.registryNameContains(block, meta, new String[] {"slime"}))
			return true;
		return false;
	}
}