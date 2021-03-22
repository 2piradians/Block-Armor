package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.text.TextFormatting;
import twopiradians.blockArmor.utils.BlockUtils;

public class SetEffectHealth_Boost extends SetEffect {

	protected SetEffectHealth_Boost(double healthBoost) {
		this.color = TextFormatting.RED;
		this.description = "Increases Max Health by "+(int)healthBoost/2+" hearts";
		this.attributeModifiers.add(new AttributeModifier(MAX_HEALTH_UUID, 
				"Max Health", healthBoost, AttributeModifier.Operation.ADDITION));
	}

	/**Can be overwritten to return a new instance depending on the given block*/
	@Override
	protected SetEffect create(Block block) {
		double healthBoost = BlockUtils.getHardness(block);
		if (healthBoost == -1 || healthBoost >= 100) //if unbreakable
			healthBoost = 100;
		healthBoost = Math.max(healthBoost*0.4d, 4);
		return new SetEffectHealth_Boost(healthBoost);
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		

		if (SetEffect.registryNameContains(block, new String[] {"bedrock", "obsidian", "brick", "heal", "heart"})) {
			float hardness = BlockUtils.getHardness(block);
			if (hardness == -1 || hardness >= 2)
				return true;	
		}
		return false;
	}
}