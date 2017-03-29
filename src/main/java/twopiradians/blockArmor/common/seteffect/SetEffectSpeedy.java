package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.util.text.TextFormatting;

public class SetEffectSpeedy extends SetEffect {

	protected SetEffectSpeedy() {
		this.color = TextFormatting.YELLOW;
		this.description = "Increases Speed";
		this.attributeModifiers.add(new AttributeModifier(MOVEMENT_SPEED_UUID, 
				SharedMonsterAttributes.MOVEMENT_SPEED.getAttributeUnlocalizedName(), 0.1d, 0));
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {
		if (SetEffect.registryNameContains(block, meta, new String[] {"fast", "speed", "sugar"}) || block == Blocks.REEDS)
			return true;		
		return false;
	}
}