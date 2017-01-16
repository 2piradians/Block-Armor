package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;

public class SetEffectInvisibility extends SetEffect {

	protected SetEffectInvisibility() {
		this.color = TextFormatting.DARK_GRAY;
		this.description = "Provides invisibility";
		this.potionEffects.add(new PotionEffect(MobEffects.INVISIBILITY, 10, 0, true, true));
	}
	
	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"glass"}))
			return true;
		return false;
	}
}