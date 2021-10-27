package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;

public class SetEffectFrosty extends SetEffect {

	protected SetEffectFrosty() {
		super();
		this.color = ChatFormatting.AQUA;
		this.enchantments.add(new EnchantmentData(Enchantments.FROST_WALKER, (short) 2, EquipmentSlot.FEET));
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"ice", "snow", "frozen", "frost"}))
			return true;		
		return false;
	}
	
}