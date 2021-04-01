package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.TextFormatting;

public class SetEffectInvisibility extends SetEffect {

	protected SetEffectInvisibility() {
		super();
		this.color = TextFormatting.WHITE;
		this.description = "Provides Invisibility";
		this.potionEffects.add(new EffectInstance(Effects.INVISIBILITY, 10, 0, true, false));
	}
	
	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"glass", "ghost", "invisible"}))
			return true;
		return false;
	}
}