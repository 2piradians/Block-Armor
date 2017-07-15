package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.common.item.ArmorSet;

@Mod.EventBusSubscriber
public class SetEffectSoft_Fall extends SetEffect 
{
	protected SetEffectSoft_Fall() {
		this.color = TextFormatting.WHITE;
		this.description = "Provides immunity to fall damage";
	}

	/**Reduce fall distance for less damage*/
	@SubscribeEvent
	public void onEvent(LivingFallEvent event) {
		if (ArmorSet.getWornSetEffects(event.getEntityLiving()).contains(this)) {
			if (!event.getEntityLiving().world.isRemote && event.getDistance() > 2)
				event.getEntityLiving().world.playSound(null, event.getEntityLiving().getPosition(), 
						SoundEvents.BLOCK_CLOTH_FALL, SoundCategory.PLAYERS, 
						Math.min(event.getDistance()/30f, 1), 
						event.getEntityLiving().world.rand.nextFloat()+0.8f);
			event.setDistance(0);
			event.setDamageMultiplier(0);
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {	
		if (SetEffect.registryNameContains(block, meta, new String[] {"wool", "hay", "soft"}))
			return true;	
		return false;
	}	
}