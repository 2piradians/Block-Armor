package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.text.TextFormatting;
import twopiradians.blockArmor.utils.BlockUtils;

public class SetEffectImmovable extends SetEffect {

	protected SetEffectImmovable(double knockbackResistance) {
		this.color = TextFormatting.GRAY;
		this.description = "Gives "+(int)(knockbackResistance*100d)+"% Knockback Resistance";
		this.attributeModifiers.add(new AttributeModifier(KNOCKBACK_RESISTANCE_UUID, 
				"Knockback Resistance", knockbackResistance, AttributeModifier.Operation.ADDITION));
	}

	/**Can be overwritten to return a new instance depending on the given block*/
	@Override
	protected SetEffect create(Block block) {
		double hardness = BlockUtils.getHardness(block);
		if (hardness == -1) //if unbreakable
			hardness = 100;
		else 
			hardness = Math.max(25, Math.min(100, hardness*1.5d)); //bound between 25-100
		return new SetEffectImmovable(hardness/100d);
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"bedrock", "obsidian", "brick", "heavy", "sturdy"})) {
			float hardness = BlockUtils.getHardness(block);
			if (hardness == -1 || hardness >= 2)
				return true;
		}
		return false;
	}
}