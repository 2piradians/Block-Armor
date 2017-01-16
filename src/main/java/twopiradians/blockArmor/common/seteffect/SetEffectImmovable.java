package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

@SuppressWarnings("deprecation")
public class SetEffectImmovable extends SetEffect {

	/**Double from 0 to 1 of knockback resistance*/
	private double knockbackResistance;

	protected SetEffectImmovable(double knockbackResistance) {
		this.knockbackResistance = knockbackResistance;
		this.color = TextFormatting.GRAY;
		this.description = "Gives "+(int)(this.knockbackResistance*100d)+"% knockback resistance";
		this.attributeModifiers.add(new AttributeModifier(KNOCKBACK_RESISTANCE_UUID, 
				SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getAttributeUnlocalizedName(), knockbackResistance, 0));
	}

	/**Can be overwritten to return a new instance depending on the given block*/
	@Override
	protected SetEffect create(Block block) {
		double hardness = block.getBlockHardness(block.getDefaultState(), null, BlockPos.ORIGIN);
		if (hardness == -1) //if unbreakable
			hardness = 100;
		else {
			hardness = Math.max(25, Math.min(100, hardness*1.5d)); //bound between 25-100
		}
		return new SetEffectImmovable(hardness/100d);
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		double hardness = 0;
		try {
			hardness = block.getBlockHardness(block.getDefaultState(), null, BlockPos.ORIGIN);
		} catch (Exception e) {}
		if (SetEffect.registryNameContains(block, new String[] {"bedrock", "obsidian", "brick", "heavy", "sturdy"})
				&& (hardness == -1 || hardness >= 2))
			return true;		
		return false;
	}
}