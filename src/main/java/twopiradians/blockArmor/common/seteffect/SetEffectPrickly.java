package twopiradians.blockArmor.common.seteffect;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectPrickly extends SetEffect {

	protected SetEffectPrickly() {
		super();
		this.color = TextFormatting.GREEN;
		this.description = "Pricks colliding enemies and provides Thorns";
		this.enchantments.add(new EnchantmentData(Enchantments.THORNS, (short) 1, EquipmentSlotType.HEAD));
		this.enchantments.add(new EnchantmentData(Enchantments.THORNS, (short) 1, EquipmentSlotType.CHEST));
		this.enchantments.add(new EnchantmentData(Enchantments.THORNS, (short) 1, EquipmentSlotType.LEGS));
		this.enchantments.add(new EnchantmentData(Enchantments.THORNS, (short) 1, EquipmentSlotType.FEET));
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack && 
				!world.isRemote && !player.getCooldownTracker().hasCooldown(stack.getItem()))	{
			AxisAlignedBB axisAlignedBB = player.getBoundingBox();
			List<LivingEntity> list = player.world.getEntitiesWithinAABB(LivingEntity.class, axisAlignedBB);
			list.remove(player);

			if (!list.isEmpty()) {
				Iterator<LivingEntity> iterator = list.iterator();
				if (iterator.next().attackEntityFrom(DamageSource.CACTUS, 1.0F)) {
					world.playSound((PlayerEntity)null, player.getPosition(), SoundEvents.ENCHANT_THORNS_HIT, 
							SoundCategory.PLAYERS, 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
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