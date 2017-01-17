package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class SetEffectDiving_Suit extends SetEffect {

	protected SetEffectDiving_Suit() {
		this.color = TextFormatting.DARK_AQUA;
		this.description = "Provides Depth Strider, Respiration, and Night Vision in water";
		this.potionEffects.add(new PotionEffect(MobEffects.NIGHT_VISION, 10, 0, true, true));
		this.enchantments.add(new EnchantmentData(Enchantments.RESPIRATION, (short) 3, EntityEquipmentSlot.HEAD));
		this.enchantments.add(new EnchantmentData(Enchantments.DEPTH_STRIDER, (short) 3, EntityEquipmentSlot.FEET));
	}
	
	/**Should player be given potionEffect now*/
	@Override
	public boolean shouldApplyPotionEffect(PotionEffect potionEffect, World world, EntityPlayer player, ItemStack stack) {
		return player.isInWater() && world.getBlockState(new BlockPos(player.posX, player.posY+1.7, player.posZ)).getBlock() instanceof BlockLiquid;
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (SetEffect.registryNameContains(block, new String[] {"water", "prismarine"}))
			return true;		
		return false;
	}
}