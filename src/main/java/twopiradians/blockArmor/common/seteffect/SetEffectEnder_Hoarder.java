package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectEnder_Hoarder extends SetEffect {

	protected SetEffectEnder_Hoarder() {
		this.color = TextFormatting.DARK_PURPLE;
		this.description = "Provides access to your ender chest";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack && BlockArmor.key.isKeyDown(player)) {
			world.playSound(null, player.getPosition(), SoundEvents.BLOCK_ENDER_CHEST_OPEN, 
					SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
			if (!(player.openContainer instanceof ChestContainer)) {
				player.openContainer(new SimpleNamedContainerProvider((id, inventory, playerIn) -> {
		               return ChestContainer.createGeneric9X3(id, inventory, player.getInventoryEnderChest());
		            }, new TranslationTextComponent("container.enderchest")));
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