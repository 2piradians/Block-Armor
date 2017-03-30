package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectArrow_Defence extends SetEffect {

	protected SetEffectArrow_Defence() {
		this.color = TextFormatting.WHITE;
		this.description = "Fires arrows outwards";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);
		
		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldownTracker().hasCooldown(stack.getItem())) {
			
			int numArrows = 16;
			for(int i = 0; i < numArrows; i++) {
				ItemArrow itemArrow = (ItemArrow) Items.ARROW;
				EntityArrow entityArrow = itemArrow.createArrow(world, new ItemStack(itemArrow), player);
				entityArrow.setAim(player, 0.0F, player.rotationYaw + i*(360/numArrows), 0.0F, 2.0F, 0.0F);
				entityArrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
				world.spawnEntity(entityArrow);
			}
			world.playSound((EntityPlayer)null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, 
					SoundCategory.PLAYERS, 1.0F, world.rand.nextFloat() + 0.5f);
			
			this.setCooldown(player, 40);
			this.damageArmor(player, 4, false);
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (SetEffect.registryNameContains(block, meta, new String[] {"dispense", "shoot", "arrow"}))
			return true;
		return false;
	}
}