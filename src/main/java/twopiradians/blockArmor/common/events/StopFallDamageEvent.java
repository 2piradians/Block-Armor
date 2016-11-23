package twopiradians.blockArmor.common.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.item.ItemModArmor;
import twopiradians.blockArmor.common.item.ModItems;

/**Used for slime's armor effect.*/
public class StopFallDamageEvent 
{

	private static EntityPlayer bouncingPlayer;
	private static double motionY;

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onFall(LivingFallEvent event) 
	{
		if (ItemModArmor.wearingFullSet(event.getEntityLiving(), ModItems.slime) && Config.isSetEffectEnabled(ModItems.slime))
		{
			EntityPlayer player = (EntityPlayer) event.getEntity();
			event.setDamageMultiplier(0);

			if (!player.isSneaking() && player.worldObj.isRemote) 
			{
				if (event.getDistance() <= 40 && event.getDistance() > 2.5D) 
				{
					player.motionY = Math.abs(player.motionY * 0.9d);
					player.onGround = true;
					bouncingPlayer = player;
					motionY = player.motionY;
					event.getEntity().worldObj.playSound(((EntityPlayer) event.getEntity()), ((EntityPlayer) event.getEntity()).posX, ((EntityPlayer) event.getEntity()).posY, ((EntityPlayer) event.getEntity()).posZ, SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.HOSTILE, 1.0F, 1.0F);
				}
				else if(event.getDistance() > 40 && event.getDistance() <= 100)
				{
					player.motionY = Math.abs(player.motionY * 0.9d * 1.5D);
					player.onGround = true;
					bouncingPlayer = player;
					motionY = player.motionY;
					event.getEntity().worldObj.playSound(((EntityPlayer) event.getEntity()), ((EntityPlayer) event.getEntity()).posX, ((EntityPlayer) event.getEntity()).posY, ((EntityPlayer) event.getEntity()).posZ, SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.HOSTILE, 1.0F, 1.0F);
				}
				else if(event.getDistance() > 100)
				{
					player.motionY = Math.abs(player.motionY * 0.9d * 2D);
					player.onGround = true;
					bouncingPlayer = player;
					motionY = player.motionY;
					event.getEntity().worldObj.playSound(((EntityPlayer) event.getEntity()), ((EntityPlayer) event.getEntity()).posX, ((EntityPlayer) event.getEntity()).posY, ((EntityPlayer) event.getEntity()).posZ, SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.HOSTILE, 1.0F, 1.0F);
				}
			}
		}
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void playerTickPost(TickEvent.PlayerTickEvent event) 
	{
		if (Config.isSetEffectEnabled(ModItems.slime))
		{
			if (bouncingPlayer != null && event.player == bouncingPlayer && bouncingPlayer.worldObj.isRemote) 
			{
				bouncingPlayer.motionY = motionY;
				bouncingPlayer.fallDistance = 0;
				bouncingPlayer = null;
			}
		}
	}
}
