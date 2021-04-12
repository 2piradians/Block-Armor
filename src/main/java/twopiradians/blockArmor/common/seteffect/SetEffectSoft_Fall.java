package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.item.ArmorSet;

@Mod.EventBusSubscriber
public class SetEffectSoft_Fall extends SetEffect {
	
	protected SetEffectSoft_Fall() {
		super();
		this.color = TextFormatting.WHITE;
		this.description = "Provides immunity to fall damage";
	}

	/**Prevent fall damage*/
	@SubscribeEvent
	public static void onEvent(LivingFallEvent event) {
		if (ArmorSet.getWornSetEffects(event.getEntityLiving()).contains(SetEffect.SOFT_FALL)) {
			if (!event.getEntityLiving().world.isRemote && event.getDistance() > 2)
				event.getEntityLiving().world.playSound(null, event.getEntityLiving().getPosition(), 
						SoundEvents.BLOCK_WOOL_FALL, SoundCategory.PLAYERS, 
						Math.min(event.getDistance()/20f, 1), 
						event.getEntityLiving().world.rand.nextFloat()+0.8f);
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