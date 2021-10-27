package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.item.ArmorSet;

@Mod.EventBusSubscriber
public class SetEffectFiery extends SetEffect {

	protected SetEffectFiery() {
		super();
		this.color = ChatFormatting.RED;
	}

	/**Ignites attackers/attackees*/ 
	@SubscribeEvent
	public static void onEvent(LivingAttackEvent event) {		
		if (SetEffect.FIERY.isEnabled() && event.getSource().getEntity() instanceof LivingEntity 
				&& !event.getSource().getEntity().level.isClientSide) {
			LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
			LivingEntity attacked = event.getEntityLiving();

			//Lights the entity that attacks the wearer of the armor
			if (ArmorSet.hasSetEffect(attacked, SetEffect.FIERY) && !attacker.isInWater())	{
				if (!attacker.isOnFire() && !attacker.fireImmune())
					attacker.level.playSound(null, attacker.getX(), 
							attacker.getY(), attacker.getZ(), SoundEvents.FIRECHARGE_USE, 
							SoundSource.PLAYERS, 0.2f, 1.0f);
				attacker.setSecondsOnFire(5);
			}
			//Lights the target of the wearer when the wearer attacks
			if (ArmorSet.hasSetEffect(attacker, SetEffect.FIERY) && !attacked.isInWater())	{
				if (!attacked.isOnFire() && !attacked.fireImmune())
					attacker.level.playSound(null, attacked.getX(), attacked.getY(), attacked.getZ(), 
							SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.4f, 1.0f);
				attacked.setSecondsOnFire(5);
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