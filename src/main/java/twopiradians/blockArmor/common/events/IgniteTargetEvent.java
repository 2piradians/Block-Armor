package twopiradians.blockArmor.common.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.item.ItemModArmor;
import twopiradians.blockArmor.common.item.ModItems;

/**Used for netherrack's armor effect.*/
public class IgniteTargetEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(LivingAttackEvent event)
	{
		if (event.getSource().getSourceOfDamage() instanceof EntityLivingBase 
				&& !event.getSource().getSourceOfDamage().worldObj.isRemote 
				&& Config.isSetEffectEnabled(ModItems.netherrack))
		{
			//Lights the entity that attacks the wearer of the armor
			if(ItemModArmor.wearingFullSet(event.getEntityLiving(), ModItems.netherrack)
					&& !event.getSource().getEntity().isInWater())
			{
				if(!event.getSource().getEntity().isBurning())
					event.getSource().getEntity().getEntityWorld().playSound(null, event.getSource().getEntity().posX, 
							event.getSource().getEntity().posY, event.getSource().getEntity().posZ, SoundEvents.ITEM_FIRECHARGE_USE, 
							SoundCategory.MASTER, 1.0f, event.getSource().getEntity().getEntityWorld().rand.nextFloat());
				event.getSource().getEntity().setFire(5);
			}
			//Lights the target of the wearer when the wearer attacks
			if(ItemModArmor.wearingFullSet((EntityLivingBase) event.getSource().getSourceOfDamage(), ModItems.netherrack)
					&& !event.getEntityLiving().isInWater())
			{
				if(!event.getEntityLiving().isBurning())
					event.getSource().getEntity().getEntityWorld().playSound(null, event.getEntityLiving().posX, 
							event.getEntityLiving().posY, event.getEntityLiving().posZ, SoundEvents.ITEM_FIRECHARGE_USE, 
							SoundCategory.MASTER, 1.0f, event.getSource().getEntity().getEntityWorld().rand.nextFloat());
				event.getEntityLiving().setFire(5);
			}
		}
	}
}
