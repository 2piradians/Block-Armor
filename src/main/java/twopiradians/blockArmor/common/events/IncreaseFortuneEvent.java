package twopiradians.blockArmor.common.events;

import java.util.List;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.item.ItemModArmor;
import twopiradians.blockArmor.common.item.ModItems;

/**Used for emerald's armor effect.*/
public class IncreaseFortuneEvent 
{

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(HarvestDropsEvent event)
	{
		if (ItemModArmor.wearingFullSet(event.getHarvester(), ModItems.emerald) && Config.isSetEffectEnabled(ModItems.emerald))
		{
			List<ItemStack> newDrops = event.getState().getBlock().getDrops(event.getWorld(), 
					event.getPos(), event.getState(), event.getFortuneLevel()+2);
			if (newDrops.size() > event.getDrops().size()) 
			{
				//do particles/sounds here (remember: this is only SERVER side)
				for(int i = 0; i < 10; ++i)
				{
					event.getWorld().getMinecraftServer().getPlayerList().sendPacketToAllPlayersInDimension(
							new SPacketParticles(EnumParticleTypes.VILLAGER_HAPPY, true, 
							(float)event.getPos().getX()+(event.getWorld().rand.nextFloat()-0.5f)*3f, 
							(float)event.getPos().getY()+(event.getWorld().rand.nextFloat()+0.0f)*5f, 
							(float)event.getPos().getZ()+(event.getWorld().rand.nextFloat()-0.5f)*3f, 0, 0, 0, 1, 1),
							event.getHarvester().dimension);
				}
				event.getWorld().playSound(null, event.getHarvester().posX, event.getHarvester().posY, event.getHarvester().posZ, 
						SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 
						0.05f*(newDrops.size()-event.getDrops().size()), event.getWorld().rand.nextFloat()+0.9f);
			}
			event.getDrops().clear();
			event.getDrops().addAll(newDrops);
		}
	}
}
