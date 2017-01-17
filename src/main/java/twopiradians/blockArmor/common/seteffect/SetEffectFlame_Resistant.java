package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.text.TextFormatting;

public class SetEffectFlame_Resistant extends SetEffect {

	protected SetEffectFlame_Resistant() {
		this.color = TextFormatting.DARK_RED;
		this.description = "Provides Fire Protection 4";
		this.enchantments.add(new EnchantmentData(Enchantments.FIRE_PROTECTION, (short) 4, EntityEquipmentSlot.HEAD));
		this.enchantments.add(new EnchantmentData(Enchantments.FIRE_PROTECTION, (short) 4, EntityEquipmentSlot.CHEST));
		this.enchantments.add(new EnchantmentData(Enchantments.FIRE_PROTECTION, (short) 4, EntityEquipmentSlot.LEGS));
		this.enchantments.add(new EnchantmentData(Enchantments.FIRE_PROTECTION, (short) 4, EntityEquipmentSlot.FEET));
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (SetEffect.registryNameContains(block, new String[] {"obsidian", "magma", "fire", "flame", "lava"}))
			return true;		
		return false;
	}
}