package twopiradians.blockArmor.common.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import twopiradians.blockArmor.common.item.ArmorSet;

/**Used for slime's armor effect.*/
public class StopFallDamageEvent 
{
	/**Static is fine bc this is only used on client*/
	private static EntityPlayer bouncingPlayer;
	private static double motionY;

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onFall(LivingFallEvent event) 
	{
		ArmorSet set = ArmorSet.getSet(Blocks.SLIME_BLOCK, 0);

		if (ArmorSet.isWearingFullSet(event.getEntityLiving(), set) && ArmorSet.isSetEffectEnabled(set))
		{
			if (!(event.getEntity() instanceof EntityPlayer))
				return;
			
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
					player.worldObj.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.PLAYERS, 1.0F, 1.0F);
				}
				else if (event.getDistance() > 40 && event.getDistance() <= 100)
				{
					player.motionY = Math.abs(player.motionY * 0.9d * 1.5D);
					player.onGround = true;
					bouncingPlayer = player;
					motionY = player.motionY;
					player.worldObj.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.PLAYERS, 1.0F, 1.0F);
				}
				else if (event.getDistance() > 100)
				{
					player.motionY = Math.abs(player.motionY * 0.9d * 2D);
					player.onGround = true;
					bouncingPlayer = player;
					motionY = player.motionY;
					player.worldObj.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.PLAYERS, 1.0F, 1.0F);
				}
			}
		}
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void playerTickPost(TickEvent.PlayerTickEvent event) 
	{
		if (!event.player.worldObj.isRemote)
			return;
		
		ArmorSet set = ArmorSet.getSet(Blocks.SLIME_BLOCK, 0);

		if (ArmorSet.isSetEffectEnabled(set))
			if (bouncingPlayer != null && event.player == bouncingPlayer && bouncingPlayer.worldObj.isRemote) 
			{
				bouncingPlayer.motionY = motionY;
				bouncingPlayer.fallDistance = 0;
				bouncingPlayer = null;
			}
	}
}
