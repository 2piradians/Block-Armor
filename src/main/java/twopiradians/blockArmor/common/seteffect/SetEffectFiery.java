package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectFiery extends SetEffect {

	protected SetEffectFiery() {
		this.color = TextFormatting.DARK_RED;
		this.description = "Ignites enemies after attacking or being attacked";
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**Ignites attackers/attackees*/
	@SubscribeEvent
	public void onEvent(LivingAttackEvent event) {		
		if (event.getSource().getSourceOfDamage() instanceof EntityLivingBase 
				&& !event.getSource().getSourceOfDamage().worldObj.isRemote) {
			EntityLivingBase attacker = (EntityLivingBase) event.getSource().getSourceOfDamage();
			EntityLivingBase attacked = event.getEntityLiving();

			//Lights the entity that attacks the wearer of the armor
			ArmorSet set = ArmorSet.getWornSet(attacked);
			if (ArmorSet.isSetEffectEnabled(set) && set.setEffects.contains(this) && !attacker.isInWater())	{
				if (!attacker.isBurning())
					attacker.worldObj.playSound(null, attacker.posX, 
							attacker.posY, attacker.posZ, SoundEvents.ITEM_FIRECHARGE_USE, 
							SoundCategory.PLAYERS, 1.0f, attacker.worldObj.rand.nextFloat());
				attacker.setFire(5);
			}
			//Lights the target of the wearer when the wearer attacks
			set = ArmorSet.getWornSet(attacker);
			if (ArmorSet.isSetEffectEnabled(set) && set.setEffects.contains(this) && !attacked.isInWater())	{
				if (!attacked.isBurning())
					attacker.worldObj.playSound(null, attacked.posX, attacked.posY, attacked.posZ, 
							SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0f, attacker.worldObj.rand.nextFloat());
				attacked.setFire(5);
			}
		}
	}
	
	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"netherrack", "magma", "fire", "flame", "lava"}))
			return true;		
		return false;
	}
}