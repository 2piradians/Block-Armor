package twopiradians.blockArmor.common.seteffect;

import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectExperience_Giving extends SetEffect {

	protected SetEffectExperience_Giving() {
		super();
		this.color = TextFormatting.GREEN;
		this.description = "Gives experience over time";
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack && !world.isRemote && 
				!player.getCooldownTracker().hasCooldown(stack.getItem())) {
			this.setCooldown(player, 50);
			
			// give exp this way for mending
			// modified from EntityXPOrb#onCollideWithPlayer
			boolean hasMending = false;
			Entry<EquipmentSlotType, ItemStack> entry = EnchantmentHelper.getRandomEquippedWithEnchantment(Enchantments.MENDING, player, ItemStack::isDamaged);
			if (entry != null) {
				ItemStack itemstack = entry.getValue();
				if (!itemstack.isEmpty() && itemstack.isDamaged()) {
					itemstack.setDamage(itemstack.getDamage() - 1);
					hasMending = true;
				}
			}

			if (!hasMending)
				player.giveExperiencePoints(1);

			this.damageArmor(player, 1, false);
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {	
		if (SetEffect.registryNameContains(block, new String[] {"lapis", "enchant", "experience",
				"amethyst", "ruby", "peridot", "topaz", "tanzanite", "malachite", "sapphire", "amber"}))
			return true;		
		return false;
	}
}