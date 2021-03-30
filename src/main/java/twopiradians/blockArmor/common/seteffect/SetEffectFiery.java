package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectFiery extends SetEffect {

	protected SetEffectFiery() {
		this.color = TextFormatting.RED;
		this.description = "Ignites enemies after attacking or being attacked";
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**Ignites attackers/attackees*/
	@SubscribeEvent
	public void onEvent(LivingAttackEvent event) {		
		if (this.isEnabled() && event.getSource().getTrueSource() instanceof LivingEntity 
				&& !event.getSource().getTrueSource().world.isRemote) {
			LivingEntity attacker = (LivingEntity) event.getSource().getTrueSource();
			LivingEntity attacked = event.getEntityLiving();

			//Lights the entity that attacks the wearer of the armor
			if (ArmorSet.getWornSetEffects(attacked).contains(this) && !attacker.isInWater())	{
				if (!attacker.isBurning())
					attacker.world.playSound(null, attacker.getPosX(), 
							attacker.getPosY(), attacker.getPosZ(), SoundEvents.ITEM_FIRECHARGE_USE, 
							SoundCategory.PLAYERS, 1.0f, attacker.world.rand.nextFloat());
				attacker.setFire(5);
			}
			//Lights the target of the wearer when the wearer attacks
			if (ArmorSet.getWornSetEffects(attacker).contains(this) && !attacked.isInWater())	{
				if (!attacked.isBurning())
					attacker.world.playSound(null, attacked.getPosX(), attacked.getPosY(), attacked.getPosZ(), 
							SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0f, attacker.world.rand.nextFloat());
				attacked.setFire(5);
			}
		}
	}
	
	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"netherrack", "magma", "fire", "flame", "lava", "nylium"}))
			return true;		
		return false;
	}
}