package twopiradians.blockArmor.common.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.common.item.ArmorSet;

/**Used for netherrack's armor effect.*/
public class IgniteTargetEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(LivingAttackEvent event)
	{
		ArmorSet set = ArmorSet.getSet(Blocks.NETHERRACK, 0);
		
		if (event.getSource().getSourceOfDamage() instanceof EntityLivingBase 
				&& !event.getSource().getSourceOfDamage().worldObj.isRemote 
				&& ArmorSet.isSetEffectEnabled(set))
		{
			EntityLivingBase attacker = (EntityLivingBase) event.getSource().getSourceOfDamage();
			EntityLivingBase attacked = event.getEntityLiving();

			//Lights the entity that attacks the wearer of the armor
			if (ArmorSet.isWearingFullSet(attacked, set) && !attacker.isInWater())
			{
				if (!attacker.isBurning())
					attacker.worldObj.playSound(null, attacker.posX, 
							attacker.posY, attacker.posZ, SoundEvents.ITEM_FIRECHARGE_USE, 
							SoundCategory.PLAYERS, 1.0f, attacker.worldObj.rand.nextFloat());
				attacker.setFire(5);
			}
			//Lights the target of the wearer when the wearer attacks
			if (ArmorSet.isWearingFullSet(attacker, set) && !attacked.isInWater())
			{
				if (!attacked.isBurning())
					attacker.worldObj.playSound(null, attacked.posX, attacked.posY, attacked.posZ, 
							SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0f, attacker.worldObj.rand.nextFloat());
				attacked.setFire(5);
			}
		}
	}
}
