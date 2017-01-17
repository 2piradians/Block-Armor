package twopiradians.blockArmor.common.seteffect;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

public class SetEffectPrickly extends SetEffect {

	protected SetEffectPrickly() {
		this.color = TextFormatting.GREEN;
		this.description = "Pricks colliding enemies and provides thorns";
		this.hasCooldown = true;
		this.enchantments.add(new EnchantmentData(Enchantments.THORNS, (short) 1, EntityEquipmentSlot.HEAD));
		this.enchantments.add(new EnchantmentData(Enchantments.THORNS, (short) 1, EntityEquipmentSlot.CHEST));
		this.enchantments.add(new EnchantmentData(Enchantments.THORNS, (short) 1, EntityEquipmentSlot.LEGS));
		this.enchantments.add(new EnchantmentData(Enchantments.THORNS, (short) 1, EntityEquipmentSlot.FEET));
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (((ItemBlockArmor)stack.getItem()).armorType == EntityEquipmentSlot.FEET && 
				!world.isRemote && stack.getTagCompound().getInteger("cooldown") <= 0)	{
			AxisAlignedBB axisAlignedBB = player.getEntityBoundingBox();
			List<EntityLivingBase> list = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisAlignedBB);
			list.remove(player);

			if (!list.isEmpty()) {
				Iterator<EntityLivingBase> iterator = list.iterator();
				if (iterator.next().attackEntityFrom(DamageSource.cactus, 1.0F)) {
					world.playSound((EntityPlayer)null, player.getPosition(), SoundEvents.ENTITY_BLAZE_HURT, 
							SoundCategory.PLAYERS, 1.0F, world.rand.nextFloat() * 0.4F + 8F);
					stack.getTagCompound().setInteger("cooldown", 20);
				}
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (SetEffect.registryNameContains(block, new String[] {"cactus", "sharp", "spike", "spine", "needle", "thorn"}))
			return true;

		return false;
	}
}