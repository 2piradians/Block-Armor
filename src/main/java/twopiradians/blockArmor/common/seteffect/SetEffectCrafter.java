package twopiradians.blockArmor.common.seteffect;


import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectCrafter extends SetEffect {

	protected SetEffectCrafter() {
		super();
		this.color = ChatFormatting.GOLD;
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isClientSide && ArmorSet.getFirstSetItem(player, this) == stack && BlockArmor.key.isKeyDown(player)) {
			player.openMenu(new CrafterProvider());
			this.damageArmor(player, 1, false);
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {	
		if (SetEffect.registryNameContains(block, new String[] {"crafting", "assemble"}))
			return true;		
		return false;
	}	
	
	private class CrafterProvider implements MenuProvider {

		@Override
		public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
			return new CrafterContainer(id, playerInventory, player.level, player.blockPosition());
		}

		@Override
		public Component getDisplayName() {
			return new TranslatableComponent("container.crafting");
		}
	}

	private class CrafterContainer extends CraftingMenu {
		public CrafterContainer(int id, Inventory playerInventory, Level world, BlockPos pos) {
			super(id, playerInventory, ContainerLevelAccess.create(world, pos));
		}

		@Override
		public boolean stillValid(Player playerIn) {
			return true;
		}
	}
}