package twopiradians.blockArmor.common.seteffect;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectHoarder extends SetEffect {


	protected SetEffectHoarder() {
		super();
		this.color = TextFormatting.GOLD;
		this.description = "Provides storage wherever you go";
		this.usesButton = true;
	}

	/** Only called when player wearing full, enabled set */
	@Override
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && BlockArmor.key.isKeyDown(player)
				&& !player.getCooldownTracker().hasCooldown(stack.getItem())) {
			try {
				player.openContainer(new HoarderProvider());
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.damageArmor(player, 1, false);
		}

	}

	/** Should block be given this set effect */
	@Override
	protected boolean isValid(Block block) {
		if (SetEffect.registryNameContains(block, new String[] { "chest", "shulker", "storage", "crate", "barrel" })
				|| block instanceof ChestBlock || block instanceof ShulkerBoxBlock || block instanceof BarrelBlock)
			return true;
		return false;
	}
	
	/**Get all stacks stored in this player's Hoarder items*/
	public static ArrayList<ItemStack> getStoredItems(PlayerEntity player) {
		ArrayList<ItemStack> stacks = Lists.newArrayList();
		for (ItemStack wornItem : ArmorSet.getAllSetItems(player, SetEffect.HOARDER))
			stacks.addAll(getStoredItems(wornItem));
		
		return stacks;
	}
	
	/**Get stacks stored in this Hoarder item*/
	public static ArrayList<ItemStack> getStoredItems(ItemStack stack) {
		ArrayList<ItemStack> stacks = Lists.newArrayList();
		
		return stacks;
	}

	private static class HoarderProvider implements INamedContainerProvider {

		@Override
		public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
			return new HoarderContainer(id, playerInventory);
		}

		@Override
		public ITextComponent getDisplayName() {
			return new StringTextComponent("Hoarder Storage");// new TranslationTextComponent("container.crafting"); //
																// TODO translation
		}
	}

	private static class HoarderContainer extends ChestContainer {
		public HoarderContainer(int id, PlayerInventory playerInventory) {
			super(ContainerType.GENERIC_9X4, id, playerInventory, new Inventory(36) {
				public void markDirty() {
					// TODO save to armor
					super.markDirty();
				}
			}, 4);
		}

		@Override
		public boolean canInteractWith(PlayerEntity playerIn) {
			return true;
		}
	}
}