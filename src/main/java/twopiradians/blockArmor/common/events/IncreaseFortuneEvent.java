package twopiradians.blockArmor.common.events;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.common.item.ArmorSet;

/**Used for emerald's armor effect.*/
public class IncreaseFortuneEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(HarvestDropsEvent event) //only server side
	{
		ArmorSet set = ArmorSet.getSet(Blocks.EMERALD_BLOCK, 0);
		if (ArmorSet.isWearingFullSet(event.getHarvester(), set) && ArmorSet.isSetEffectEnabled(set))
		{
			List<ItemStack> newDrops = event.getState().getBlock().getDrops(event.getWorld(), 
					event.getPos(), event.getState(), event.getFortuneLevel()+2);
			if (newDrops.size() > event.getDrops().size()) 
			{
				for (int i = 0; i < 10; ++i)
					((WorldServer)event.getWorld()).spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, 
							(float)event.getPos().getX()+(event.getWorld().rand.nextFloat()-0.5f)*3f, 
							(float)event.getPos().getY()+(event.getWorld().rand.nextFloat()+0.0f)*5f, 
							(float)event.getPos().getZ()+(event.getWorld().rand.nextFloat()-0.5f)*3f, 1, 0, 0, 1, 0, new int[0]);
				event.getWorld().playSound(null, event.getHarvester().posX, event.getHarvester().posY, event.getHarvester().posZ, 
						SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 
						0.05f*(newDrops.size()-event.getDrops().size()), event.getWorld().rand.nextFloat()+0.9f);
			}
			event.getDrops().clear();
			event.getDrops().addAll(newDrops);
		}
	}
}
