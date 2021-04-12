package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.text.TextFormatting;

public class SetEffectFlame_Resistant extends SetEffect {

	protected SetEffectFlame_Resistant() {
		super();
		this.color = TextFormatting.DARK_RED;
		this.description = "Provides Fire Protection IV";
		this.enchantments.add(new EnchantmentData(Enchantments.FIRE_PROTECTION, (short) 4, EquipmentSlotType.HEAD));
		this.enchantments.add(new EnchantmentData(Enchantments.FIRE_PROTECTION, (short) 4, EquipmentSlotType.CHEST));
		this.enchantments.add(new EnchantmentData(Enchantments.FIRE_PROTECTION, (short) 4, EquipmentSlotType.LEGS));
		this.enchantments.add(new EnchantmentData(Enchantments.FIRE_PROTECTION, (short) 4, EquipmentSlotType.FEET));
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"obsidian", "magma", "fire", "flame", "lava"}) &&
				!SetEffect.registryNameContains(block, new String[] {"coral"}))
			return true;		
		return false;
	}
}