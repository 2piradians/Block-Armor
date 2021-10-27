package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.item.ArmorSet;

@Mod.EventBusSubscriber
public class SetEffectSoft_Fall extends SetEffect {
	
	protected SetEffectSoft_Fall() {
		super();
		this.color = ChatFormatting.WHITE;
	}

	/**Prevent fall damage*/
	@SubscribeEvent
	public static void onEvent(LivingFallEvent event) {
		if (ArmorSet.getWornSetEffects(event.getEntityLiving()).contains(SetEffect.SOFT_FALL)) {
			if (!event.getEntityLiving().level.isClientSide && event.getDistance() > 2)
				event.getEntityLiving().level.playSound(null, event.getEntityLiving().blockPosition(), 
						SoundEvents.WOOL_FALL, SoundSource.PLAYERS, 
						Math.min(event.getDistance()/20f, 1), 
						event.getEntityLiving().level.random.nextFloat()+0.8f);
			event.setDistance(0);
			event.setDamageMultiplier(0);
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {	
		if (SetEffect.registryNameContains(block, new String[] {"wool", "hay", "soft"}))
			return true;	
		return false;
	}	
}