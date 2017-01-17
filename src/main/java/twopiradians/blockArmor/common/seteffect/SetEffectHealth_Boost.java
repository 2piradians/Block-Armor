package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

@SuppressWarnings("deprecation")
public class SetEffectHealth_Boost extends SetEffect {

	protected SetEffectHealth_Boost(double healthBoost) {
		this.color = TextFormatting.RED;
		this.description = "Increases max health by "+(int)healthBoost;
		this.attributeModifiers.add(new AttributeModifier(MAX_HEALTH_UUID, 
				SharedMonsterAttributes.MAX_HEALTH.getAttributeUnlocalizedName(), healthBoost, 0));
	}

	/**Can be overwritten to return a new instance depending on the given block*/
	@Override
	protected SetEffect create(Block block) {
		double healthBoost = block.getBlockHardness(block.getDefaultState(), null, BlockPos.ORIGIN);
		if (healthBoost == -1 || healthBoost >= 100) //if unbreakable
			healthBoost = 100;
		healthBoost = Math.max(healthBoost*0.4d, 4);
		return new SetEffectHealth_Boost(healthBoost);
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		double hardness = 0;
		try {
			hardness = block.getBlockHardness(block.getDefaultState(), null, BlockPos.ORIGIN);
		} catch (Exception e) {}
		if (SetEffect.registryNameContains(block, new String[] {"bedrock", "obsidian", "brick", "heal", "heart"})
				&& (hardness == -1 || hardness >= 2))
			return true;		
		return false;
	}
}