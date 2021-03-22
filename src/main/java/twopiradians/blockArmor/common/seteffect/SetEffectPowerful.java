package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.text.TextFormatting;

public class SetEffectPowerful extends SetEffect {

	protected SetEffectPowerful() {
		this.color = TextFormatting.WHITE;
		this.description = "Increases Attack, Speed, and Strength";
		this.attributeModifiers.add(new AttributeModifier(ATTACK_SPEED_UUID, 
				"Attack Speed", 1d, AttributeModifier.Operation.ADDITION));
		this.attributeModifiers.add(new AttributeModifier(ATTACK_DAMAGE_UUID, 
				"Attack Damage", 3d, AttributeModifier.Operation.ADDITION));
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"quartz", "strong", "power"}))
			return true;		
		return false;
	}
}