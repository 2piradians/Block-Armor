package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;

public class SetEffectFlame_Resistant extends SetEffect {

	protected SetEffectFlame_Resistant() {
		super();
		this.color = ChatFormatting.DARK_RED;
		this.enchantments.add(new EnchantmentData(Enchantments.FIRE_PROTECTION, (short) 4, EquipmentSlot.HEAD));
		this.enchantments.add(new EnchantmentData(Enchantments.FIRE_PROTECTION, (short) 4, EquipmentSlot.CHEST));
		this.enchantments.add(new EnchantmentData(Enchantments.FIRE_PROTECTION, (short) 4, EquipmentSlot.LEGS));
		this.enchantments.add(new EnchantmentData(Enchantments.FIRE_PROTECTION, (short) 4, EquipmentSlot.FEET));
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