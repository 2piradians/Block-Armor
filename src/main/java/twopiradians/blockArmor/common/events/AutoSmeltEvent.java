package twopiradians.blockArmor.common.events;

import java.util.ListIterator;

import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.common.item.ArmorSet;

/**Used for Furnace's armor effect.*/
public class AutoSmeltEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(HarvestDropsEvent event) //only server side
	{
		ArmorSet set = ArmorSet.getSet(Blocks.FURNACE, 0);
		if (ArmorSet.isWearingFullSet(event.getHarvester(), set) && ArmorSet.isSetEffectEnabled(set))
		{
			if (event.getWorld().isRemote || event.isSilkTouching())
				return;

			ListIterator<ItemStack> dropsIterator = event.getDrops().listIterator();

			while (dropsIterator.hasNext())
			{
				ItemStack oldDrops = dropsIterator.next();
				ItemStack newDrops = FurnaceRecipes.instance().getSmeltingResult(oldDrops);

				if (!(newDrops.getItem() ==	null))
				{
					newDrops = newDrops.copy();
					event.getDrops().clear();
					event.getDrops().add(newDrops);
					event.getWorld().playSound(null, event.getHarvester().posX, event.getHarvester().posY, event.getHarvester().posZ, 
							SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 0.2f, event.getWorld().rand.nextFloat()+0.7f);				
				}
			}
		}
	}
}

