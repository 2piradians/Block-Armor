package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectArrow_Defence extends SetEffect {

	protected SetEffectArrow_Defence() {
		super();
		this.color = ChatFormatting.WHITE;
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);
		
		if (!world.isClientSide && ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldowns().isOnCooldown(stack.getItem())) {
			
			int numArrows = 16;
			for(int i = 0; i < numArrows; i++) {
				ArrowItem itemArrow = (ArrowItem) Items.ARROW;
				AbstractArrow entityArrow = itemArrow.createArrow(world, new ItemStack(itemArrow), player);
				entityArrow.shootFromRotation(player, 0.0F, player.getYRot() + i*(360/numArrows), 0.0F, 2.0F, 0.0F);
				entityArrow.pickup = Arrow.Pickup.DISALLOWED;
				world.addFreshEntity(entityArrow);
			}
			world.playSound((Player)null, player.blockPosition(), SoundEvents.ARROW_SHOOT, 
					SoundSource.PLAYERS, 1.0F, world.random.nextFloat() + 0.5f);
			
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