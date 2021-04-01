package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.text.TextFormatting;

public class SetEffectFrosty extends SetEffect {

	protected SetEffectFrosty() {
		super();
		this.color = TextFormatting.AQUA;
		this.description = "Provides Frost Walking II";
		this.enchantments.add(new EnchantmentData(Enchantments.FROST_WALKER, (short) 2, EquipmentSlotType.FEET));
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"ice", "snow", "frozen", "frost"}))
			return true;		
		return false;
	}
	
}