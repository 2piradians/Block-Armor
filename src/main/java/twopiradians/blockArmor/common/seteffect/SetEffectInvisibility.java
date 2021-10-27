package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;

public class SetEffectInvisibility extends SetEffect {

	protected SetEffectInvisibility() {
		super();
		this.color = ChatFormatting.WHITE;
		this.potionEffects.add(new MobEffectInstance(MobEffects.INVISIBILITY, 10, 0, true, false));
	}
	
	// was going to make other mobs ignore invisible players, but no way to recognize bosses?
	
	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"glass", "ghost", "invisible"}))
			return true;
		return false;
	}
}