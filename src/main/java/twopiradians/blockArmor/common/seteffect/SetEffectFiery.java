package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.item.ArmorSet;

@Mod.EventBusSubscriber
public class SetEffectFiery extends SetEffect {

	protected SetEffectFiery() {
		super();
		this.color = TextFormatting.RED;
		this.description = "Ignites enemies after attacking or being attacked";
	}

	/**Ignites attackers/attackees*/ 
	@SubscribeEvent
	public static void onEvent(LivingAttackEvent event) {		
		if (SetEffect.FIERY.isEnabled() && event.getSource().getTrueSource() instanceof LivingEntity 
				&& !event.getSource().getTrueSource().world.isRemote) {
			LivingEntity attacker = (LivingEntity) event.getSource().getTrueSource();
			LivingEntity attacked = event.getEntityLiving();

			//Lights the entity that attacks the wearer of the armor
			if (ArmorSet.hasSetEffect(attacked, SetEffect.FIERY) && !attacker.isInWater())	{
				if (!attacker.isBurning() && !attacker.isImmuneToFire())
					attacker.world.playSound(null, attacker.getPosX(), 
							attacker.getPosY(), attacker.getPosZ(), SoundEvents.ITEM_FIRECHARGE_USE, 
							SoundCategory.PLAYERS, 0.2f, 1.0f);
				attacker.setFire(5);
			}
			//Lights the target of the wearer when the wearer attacks
			if (ArmorSet.hasSetEffect(attacker, SetEffect.FIERY) && !attacked.isInWater())	{
				if (!attacked.isBurning() && !attacked.isImmuneToFire())
					attacker.world.playSound(null, attacked.getPosX(), attacked.getPosY(), attacked.getPosZ(), 
							SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.4f, 1.0f);
				attacked.setFire(5);
			}
		}
	}
	
	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"netherrack", "magma", "fire", "flame", "lava", "nylium"}) &&
				!SetEffect.registryNameContains(block, new String[] {"coral"}))
			return true;		
		return false;
	}
}