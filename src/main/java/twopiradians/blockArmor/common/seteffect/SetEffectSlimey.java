package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

public class SetEffectSlimey extends SetEffect {

	/**Static is fine bc this is only used on client*/
	private static EntityPlayer bouncingPlayer;
	private static double motionY;

	protected SetEffectSlimey() {
		this.color = TextFormatting.GREEN;
		this.description = "Bounces off walls and floors";
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (((ItemBlockArmor)stack.getItem()).armorType == EntityEquipmentSlot.FEET && world.isRemote && !player.isSneaking()) {	
			if (!player.getCooldownTracker().hasCooldown(stack.getItem()) && player.isCollidedHorizontally 
					&& Math.sqrt(Math.pow(player.posX - player.prevChasingPosX, 2) + 
							Math.pow(player.posZ - player.prevChasingPosZ, 2)) >= 0.9D) {	
				this.setCooldown(player, 10);
				if (player.motionX == 0) {
					player.motionX = -(player.posX - player.prevChasingPosX)*1.5D;
					player.motionZ = (player.posZ - player.prevChasingPosZ)*1.5D;
				}
				else if (player.motionZ == 0) {
					player.motionX = (player.posX - player.prevChasingPosX)*1.5D;
					player.motionZ = -(player.posZ - player.prevChasingPosZ)*1.5D;
				}
				player.motionY += 0.1;
				world.playSound(player, player.posX, player.posY, player.posZ, 
						SoundEvents.BLOCK_SLIME_FALL, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	@SubscribeEvent
	public void onEvent(LivingFallEvent event) {
		ArmorSet set = ArmorSet.getWornSet(event.getEntityLiving());
		if (ArmorSet.isSetEffectEnabled(set) && set.setEffects.contains(this)) {
			if (!(event.getEntity() instanceof EntityPlayer))
				return;

			EntityPlayer player = (EntityPlayer) event.getEntity();
			event.setDamageMultiplier(0);

			if (!player.isSneaking() && player.world.isRemote) {
				if (event.getDistance() <= 40 && event.getDistance() > 2.5D)
					player.motionY = Math.abs(player.motionY * 0.9d);
				else if (event.getDistance() > 40 && event.getDistance() <= 100)
					player.motionY = Math.abs(player.motionY * 0.9d * 1.5D);
				else if (event.getDistance() > 100)
					player.motionY = Math.abs(player.motionY * 0.9d * 2D);

				if (event.getDistance() > 2.5D)
					player.world.playSound(player, player.posX, player.posY, player.posZ, 
							event.getDistance() > 40 ? SoundEvents.ENTITY_SLIME_JUMP : SoundEvents.ENTITY_SLIME_SQUISH, 
									SoundCategory.PLAYERS, 1.0F, 1.0F);
				player.onGround = true;
				bouncingPlayer = player;
				motionY = player.motionY;
			}
		}
	}

	@SubscribeEvent
	public void onEvent(TickEvent.PlayerTickEvent event) {
		if (!event.player.world.isRemote)
			return;

		ArmorSet set = ArmorSet.getWornSet(event.player);
		if (ArmorSet.isSetEffectEnabled(set) && set.setEffects.contains(this))
			if (bouncingPlayer != null && event.player == bouncingPlayer && bouncingPlayer.world.isRemote) {
				bouncingPlayer.motionY = motionY;
				bouncingPlayer.fallDistance = 0;
				bouncingPlayer = null;
			}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (SetEffect.registryNameContains(block, new String[] {"slime"}))
			return true;
		return false;
	}
}