package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class SetEffectDiving_Suit extends SetEffect {

	protected SetEffectDiving_Suit() {
		super();
		this.color = TextFormatting.DARK_AQUA;
		this.description = "Provides Depth Strider, Aqua Affinity, Respiration, and Night Vision in water";
		this.potionEffects.add(new EffectInstance(Effects.NIGHT_VISION, 210, 0, true, false));
		this.enchantments.add(new EnchantmentData(Enchantments.AQUA_AFFINITY, (short) 3, EquipmentSlotType.HEAD));
		this.enchantments.add(new EnchantmentData(Enchantments.RESPIRATION, (short) 3, EquipmentSlotType.HEAD));
		this.enchantments.add(new EnchantmentData(Enchantments.DEPTH_STRIDER, (short) 3, EquipmentSlotType.FEET));
	}
	
	/**Should player be given potionEffect now*/
	@Override
	public boolean shouldApplyEffect(EffectInstance potionEffect, World world, PlayerEntity player, ItemStack stack) {
		return !world.getBlockState(new BlockPos(player.getPosX(), player.getPosY()+1.7d, player.getPosZ())).getFluidState().isEmpty();
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"water", "prismarine", "sea", "coral", "kelp"}))
			return true;		
		return false;
	}
}