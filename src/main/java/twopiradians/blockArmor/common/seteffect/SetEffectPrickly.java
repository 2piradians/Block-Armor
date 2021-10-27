package twopiradians.blockArmor.common.seteffect;

import java.util.Iterator;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectPrickly extends SetEffect {

	protected SetEffectPrickly() {
		super();
		this.color = ChatFormatting.GREEN;
		this.enchantments.add(new EnchantmentData(Enchantments.THORNS, (short) 1, EquipmentSlot.HEAD));
		this.enchantments.add(new EnchantmentData(Enchantments.THORNS, (short) 1, EquipmentSlot.CHEST));
		this.enchantments.add(new EnchantmentData(Enchantments.THORNS, (short) 1, EquipmentSlot.LEGS));
		this.enchantments.add(new EnchantmentData(Enchantments.THORNS, (short) 1, EquipmentSlot.FEET));
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack && 
				!world.isClientSide && !player.getCooldowns().isOnCooldown(stack.getItem()))	{
			AABB axisAlignedBB = player.getBoundingBox();
			List<LivingEntity> list = player.level.getEntitiesOfClass(LivingEntity.class, axisAlignedBB);
			list.remove(player);

			if (!list.isEmpty()) {
				Iterator<LivingEntity> iterator = list.iterator();
				if (iterator.next().hurt(DamageSource.CACTUS, 1.0F)) {
					world.playSound((Player)null, player.blockPosition(), SoundEvents.THORNS_HIT, 
							SoundSource.PLAYERS, 1.0F, world.random.nextFloat() * 0.4F + 0.8F);
					this.setCooldown(player, 20);
				}
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"cactus", "sharp", "spike", "spine", "needle", "thorn"}))
			return true;

		return false;
	}
}