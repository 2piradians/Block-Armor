package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.text.TextFormatting;

public class SetEffectPowerful extends SetEffect {

	protected SetEffectPowerful() {
		this.color = TextFormatting.WHITE;
		this.description = "Increases Attack Speed and Strength";
		this.attributeModifiers.add(new AttributeModifier(ATTACK_SPEED_UUID, 
				SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(), 1d, 0));
		this.attributeModifiers.add(new AttributeModifier(ATTACK_DAMAGE_UUID, 
				SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), 3d, 0));
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (SetEffect.registryNameContains(block, new String[] {"quartz", "strong", "power"}))
			return true;		
		return false;
	}
}