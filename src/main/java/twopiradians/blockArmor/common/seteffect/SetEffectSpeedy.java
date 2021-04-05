package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.text.TextFormatting;

public class SetEffectSpeedy extends SetEffect {

	protected SetEffectSpeedy() {
		super();
		this.color = TextFormatting.YELLOW;
		this.description = "Increases Speed";
		this.attributes.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MOVEMENT_SPEED_UUID, 
				"Movement Speed", 0.1d, AttributeModifier.Operation.ADDITION));
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {
		if (SetEffect.registryNameContains(block, new String[] {"fast", "speed", "sugar"}) || 
				block == Blocks.SUGAR_CANE ||
				block.getSpeedFactor() > 1f)
			return true;		
		return false;
	}
}