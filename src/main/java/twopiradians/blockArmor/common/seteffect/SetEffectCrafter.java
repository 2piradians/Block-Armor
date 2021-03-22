package twopiradians.blockArmor.common.seteffect;


import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectCrafter extends SetEffect {

	protected SetEffectCrafter() {
		this.color = TextFormatting.GOLD;
		this.description = "Opens a crafting screen";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack && BlockArmor.key.isKeyDown(player)) {
			player.openContainer(new CrafterProvider());
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
	
	private class CrafterProvider implements INamedContainerProvider {

		@Override
		public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
			return new CrafterContainer(id, playerInventory, player.world, player.getPosition());
		}

		@Override
		public ITextComponent getDisplayName() {
			return new TranslationTextComponent("container.crafting");
		}
	}

	private class CrafterContainer extends WorkbenchContainer {
		public CrafterContainer(int id, PlayerInventory playerInventory, World world, BlockPos pos) {
			super(id, playerInventory, IWorldPosCallable.of(world, pos));
		}

		@Override
		public boolean canInteractWith(PlayerEntity playerIn) {
			return true;
		}
	}
}