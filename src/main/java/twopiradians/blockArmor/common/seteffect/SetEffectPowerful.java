package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.text.TextFormatting;

public class SetEffectPowerful extends SetEffect {

	protected SetEffectPowerful() {
		super();
		this.color = TextFormatting.WHITE;
		this.description = "Increases Attack, Speed, and Strength";
		this.attributes.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_UUID, 
				"Attack Speed", 1d, AttributeModifier.Operation.ADDITION));
		this.attributes.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_UUID, 
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