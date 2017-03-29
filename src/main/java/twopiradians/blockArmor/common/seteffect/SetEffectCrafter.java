package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWorkbench.InterfaceCraftingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
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
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack && BlockArmor.key.isKeyDown(player)) {
			player.displayGui(new InterfaceCrafter(world, player.getPosition()));
			this.damageArmor(player, 1, false);
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {	
		if (SetEffect.registryNameContains(block, meta, new String[] {"crafting"}))
			return true;		
		return false;
	}	
	
	private class InterfaceCrafter extends InterfaceCraftingTable {
		private final World world;
		private final BlockPos position;

		public InterfaceCrafter(World worldIn, BlockPos pos) {
			super(worldIn, pos);
			this.world = worldIn;
			this.position = pos;
		}

		@Override
		public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
			return new ContainerCrafter(playerInventory, this.world, this.position);
		}
	}

	private class ContainerCrafter extends ContainerWorkbench {
		public ContainerCrafter(InventoryPlayer playerInventory, World worldIn, BlockPos posIn) {
			super(playerInventory, worldIn, posIn);
		}

		@Override
		public boolean canInteractWith(EntityPlayer playerIn) {
			return true;
		}
	}
}