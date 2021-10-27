package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EnderChestBlock;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectEnder_Hoarder extends SetEffect {

	protected SetEffectEnder_Hoarder() {
		super();
		this.color = ChatFormatting.DARK_PURPLE;
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isClientSide && ArmorSet.getFirstSetItem(player, this) == stack && BlockArmor.key.isKeyDown(player)) {
			world.playSound(null, player.blockPosition(), SoundEvents.ENDER_CHEST_OPEN, 
					SoundSource.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
			if (!(player.containerMenu instanceof ChestMenu)) {
				player.openMenu(new SimpleMenuProvider((id, inventory, playerIn) -> {
		               return ChestMenu.threeRows(id, inventory, player.getEnderChestInventory());
		            }, new TranslatableComponent("container.enderchest")));
				this.damageArmor(player, 1, false);
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {	
		if (block instanceof EnderChestBlock)
			return true;		
		return false; 
	}
	
}