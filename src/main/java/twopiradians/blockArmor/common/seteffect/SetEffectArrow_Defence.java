package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectArrow_Defence extends SetEffect {

	protected SetEffectArrow_Defence() {
		super();
		this.color = TextFormatting.WHITE;
		this.description = "Fires arrows outwards";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);
		
		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldownTracker().hasCooldown(stack.getItem())) {
			
			int numArrows = 16;
			for(int i = 0; i < numArrows; i++) {
				ArrowItem itemArrow = (ArrowItem) Items.ARROW;
				AbstractArrowEntity entityArrow = itemArrow.createArrow(world, new ItemStack(itemArrow), player);
				entityArrow.setDirectionAndMovement(player, 0.0F, player.rotationYaw + i*(360/numArrows), 0.0F, 2.0F, 0.0F);
				entityArrow.pickupStatus = ArrowEntity.PickupStatus.DISALLOWED;
				world.addEntity(entityArrow);
			}
			world.playSound((PlayerEntity)null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, 
					SoundCategory.PLAYERS, 1.0F, world.rand.nextFloat() + 0.5f);
			
			this.setCooldown(player, 40);
			this.damageArmor(player, 4, false);
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"dispense", "shoot", "arrow", "fletch"}))
			return true;
		return false;
	}
}