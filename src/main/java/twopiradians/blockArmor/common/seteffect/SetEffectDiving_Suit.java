package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class SetEffectDiving_Suit extends SetEffect {

	protected SetEffectDiving_Suit() {
		super();
		this.color = ChatFormatting.DARK_AQUA;
		this.potionEffects.add(new MobEffectInstance(MobEffects.NIGHT_VISION, 210, 0, true, false));
		this.enchantments.add(new EnchantmentData(Enchantments.AQUA_AFFINITY, (short) 3, EquipmentSlot.HEAD));
		this.enchantments.add(new EnchantmentData(Enchantments.RESPIRATION, (short) 3, EquipmentSlot.HEAD));
		this.enchantments.add(new EnchantmentData(Enchantments.DEPTH_STRIDER, (short) 3, EquipmentSlot.FEET));
	}
	
	/**Should player be given potionEffect now*/
	@Override
	public boolean shouldApplyEffect(MobEffectInstance potionEffect, Level world, Player player, ItemStack stack) {
		return !world.getBlockState(new BlockPos(player.getX(), player.getY()+1.7d, player.getZ())).getFluidState().isEmpty();
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"water", "prismarine", "sea", "coral", "kelp"}))
			return true;		
		return false;
	}
}