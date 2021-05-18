package twopiradians.blockArmor.common.seteffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.BlockArmorItem;

public class SetEffectHoarder extends SetEffect {

	public static ContainerType<HoarderContainer> containerType_9x1;
	public static ContainerType<HoarderContainer> containerType_9x2;
	public static ContainerType<HoarderContainer> containerType_9x3;
	public static ContainerType<HoarderContainer> containerType_9x4;
	public static ContainerType<HoarderContainer> containerType_9x5;
	public static ContainerType<HoarderContainer> containerType_9x6;

	private static final HashMap<EquipmentSlotType, Integer> SLOT_TO_SIZE;

	static {
		SLOT_TO_SIZE = Maps.newHashMap();
		SLOT_TO_SIZE.put(EquipmentSlotType.HEAD, 9);
		SLOT_TO_SIZE.put(EquipmentSlotType.CHEST, 18);
		SLOT_TO_SIZE.put(EquipmentSlotType.LEGS, 18);
		SLOT_TO_SIZE.put(EquipmentSlotType.FEET, 9);
	}

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

		if (!world.isRemote && BlockArmor.key.isKeyDown(player) &&
				ArmorSet.getFirstSetItem(player, this) == stack &&
				!player.getCooldownTracker().hasCooldown(stack.getItem())) {
			if (player instanceof ServerPlayerEntity)
				((ServerPlayerEntity)player).connection.sendPacket(new SPlaySoundPacket(SetEffect.HOARDER.getSoundEvent(player, true).getRegistryName(), SoundCategory.PLAYERS, player.getPositionVec(), 0.6f, 1));	
			player.openContainer(new HoarderProvider());
			this.damageArmor(player, 1, false); 
			this.setCooldown(player, 20);
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

	/**Set effect name and description if shifting*/
	@OnlyIn(Dist.CLIENT)
	public List<ITextComponent> addInformation(ItemStack stack, boolean isShiftDown, PlayerEntity player, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip = super.addInformation(stack, isShiftDown, player, tooltip, flagIn);

		// add stored items
		if (stack.hasTag() && stack.getTag().contains(BlockArmor.MODID+":hoarderItems")) {
			NonNullList<ItemStack> storedItems = getStoredItems(stack);
			ArrayList<ItemStack> nonEmptyItems = Lists.newArrayList();
			for (ItemStack storedItem : storedItems)
				if (!storedItem.isEmpty())
					nonEmptyItems.add(storedItem);
			for (int i=0; i<4 && i<nonEmptyItems.size(); ++i) {
				ItemStack storedItem = nonEmptyItems.get(i);
				IFormattableTextComponent comp = new StringTextComponent("  - ")
						.appendSibling(storedItem.getDisplayName().deepCopy().mergeStyle(this.color));
				comp.appendString(" x").appendString(String.valueOf(storedItem.getCount()));
				tooltip.add(comp);
			}
			if (nonEmptyItems.size() > 4)
				tooltip.add((new StringTextComponent("  ")
						.appendSibling(new TranslationTextComponent("container.shulkerBox.more", nonEmptyItems.size()-4)).mergeStyle(TextFormatting.ITALIC, this.color)));
		}

		return tooltip;
	}

	/**Get open/close sound event based on this block*/
	public SoundEvent getSoundEvent(PlayerEntity player, boolean open) {
		ItemStack stack = ArmorSet.getFirstSetItem(player, SetEffect.HOARDER);
		if (stack != null) {
			Block block = ((BlockArmorItem)stack.getItem()).set.block;
			if (block instanceof BarrelBlock)
				return open ? SoundEvents.BLOCK_BARREL_OPEN : SoundEvents.BLOCK_BARREL_CLOSE;
			else if (block instanceof ShulkerBoxBlock)
				return open ? SoundEvents.BLOCK_SHULKER_BOX_OPEN : SoundEvents.BLOCK_SHULKER_BOX_CLOSE;				
		}
		return open ? SoundEvents.BLOCK_CHEST_OPEN : SoundEvents.BLOCK_CHEST_CLOSE;
	}

	/**Called when an item with Hoarder is broken*/
	public void onBreak(ItemStack stack) {
		// get player, item frame, or item entity with this item
		Entity entity = stack.getAttachedEntity();
		if (entity == null && ServerLifecycleHooks.getCurrentServer() != null) {
			for (PlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
				if (player.inventory.hasItemStack(stack)) {
					entity = player;
					break;
				}
		}

		// spawn stored items
		if (entity != null && !entity.world.isRemote) {
			NonNullList<ItemStack> storedItems = getStoredItems(stack);
			if (!storedItems.isEmpty()) {
				for (ItemStack storedItem : storedItems)
					entity.entityDropItem(storedItem);
				stack.setCount(0);
				// if hoarder gui is open - close it (to prevent dupes)
				if (entity instanceof PlayerEntity && 
						((PlayerEntity)entity).openContainer instanceof HoarderContainer)
					((PlayerEntity)entity).closeScreen();
			}
		}

	}

	/**Get container type based on which/how many slots have Hoarder items*/
	private static ContainerType getContainerType(PlayerEntity player) {
		int size = 0;
		for (ItemStack wornItem : ArmorSet.getAllSetItems(player, SetEffect.HOARDER))
			size += SLOT_TO_SIZE.get(((BlockArmorItem)wornItem.getItem()).getEquipmentSlot());
		if (size == 9)
			return containerType_9x1;
		else if (size == 18)
			return containerType_9x2;
		else if (size == 27)
			return containerType_9x3;
		else if (size == 36)
			return containerType_9x4;
		else if (size == 45)
			return containerType_9x5;
		else if (size == 54)
			return containerType_9x6;
		else
			return containerType_9x1;
	}

	/**Get all stacks stored in this player's Hoarder items*/
	public static NonNullList<ItemStack> getStoredItems(PlayerEntity player) {
		NonNullList<ItemStack> stacks = NonNullList.create();
		for (ItemStack wornItem : ArmorSet.getAllSetItems(player, SetEffect.HOARDER))
			stacks.addAll(getStoredItems(wornItem));
		return stacks;
	}

	/**Set stored stacks for this player's Hoarder items*/
	public static void setStoredItems(PlayerEntity player, ItemStack[] stacks) {
		int index = 0;
		for (ItemStack wornItem : ArmorSet.getAllSetItems(player, SetEffect.HOARDER)) {
			int size = SLOT_TO_SIZE.get(((BlockArmorItem)wornItem.getItem()).getEquipmentSlot());
			if (index + size <= stacks.length)
				setStoredItems(wornItem, Arrays.copyOfRange(stacks, index, index+size));
			index += size;
		}
	}

	/**Get stacks stored in this Hoarder item*/
	public static NonNullList<ItemStack> getStoredItems(ItemStack wornItem) {
		NonNullList<ItemStack> stacks = NonNullList.withSize(SLOT_TO_SIZE.get(((BlockArmorItem)wornItem.getItem()).getEquipmentSlot()), ItemStack.EMPTY);
		if (wornItem != null && !wornItem.isEmpty() && wornItem.hasTag()) {
			CompoundNBT nbt = wornItem.getTag();
			ListNBT list = nbt.getList(BlockArmor.MODID+":hoarderItems", NBT.TAG_COMPOUND);
			for (int i=0; i<list.size() && i<stacks.size(); ++i) {
				CompoundNBT itemNbt = list.getCompound(i);
				stacks.set(i, ItemStack.read(itemNbt));
			}
		}
		return stacks;
	}

	/**Set stacks stored in this Hoarder item*/
	public static void setStoredItems(ItemStack wornItem, ItemStack[] stacks) {
		if (wornItem != null) {
			CompoundNBT nbt = wornItem.hasTag() ? wornItem.getTag() : new CompoundNBT();
			ListNBT list = nbt.getList(BlockArmor.MODID+":hoarderItems", NBT.TAG_COMPOUND);
			for (int i=0; i<stacks.length; ++i)
				list.add(i, stacks[i].write(new CompoundNBT()));
			nbt.put(BlockArmor.MODID+":hoarderItems", list);
			wornItem.setTag(nbt);
		}
	}

	private static class HoarderProvider implements INamedContainerProvider {

		@Override
		public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
			return new HoarderContainer(id, playerInventory, getStoredItems(player).toArray(new ItemStack[0]), player);
		}

		@Override
		public ITextComponent getDisplayName() {
			return new StringTextComponent("Hoarder Storage");
		}
	}

	private static class HoarderContainer extends ChestContainer {

		public HoarderContainer(int id, PlayerInventory playerInventory) {
			this(id, playerInventory, getStoredItems(playerInventory.player).toArray(new ItemStack[0]), playerInventory.player);
		}

		public HoarderContainer(int id, PlayerInventory playerInventory, ItemStack[] storedItems, PlayerEntity player) {
			this(id, playerInventory, player, new Inventory(storedItems) {
				public void markDirty() {
					// update stored items in nbt
					ItemStack[] stacks = new ItemStack[this.getSizeInventory()];
					for (int i=0; i<this.getSizeInventory(); ++i)
						stacks[i] = this.getStackInSlot(i);
					setStoredItems(player, stacks);

					super.markDirty();
				}
			}, storedItems.length / 9);
		}

		public HoarderContainer(int id, PlayerInventory playerInventory, PlayerEntity player, Inventory inv, int numRows) {
			super(getContainerType(player), id, playerInventory, inv, numRows);

			// custom slots to prevent storing more hoarder items
			this.inventorySlots.clear();
			int i = (numRows - 4) * 18;

			for(int j = 0; j < numRows; ++j) 
				for(int k = 0; k < 9; ++k) 
					this.addSlot(new HoarderSlot(this.getLowerChestInventory(), k + j * 9, 8 + k * 18, 18 + j * 18));

			for(int l = 0; l < 3; ++l) 
				for(int j1 = 0; j1 < 9; ++j1) 
					this.addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));

			for(int i1 = 0; i1 < 9; ++i1) 
				this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i));
		}

		@Override
		public void onContainerClosed(PlayerEntity player) {
			super.onContainerClosed(player);

			if (!player.world.isRemote && player instanceof ServerPlayerEntity)
				((ServerPlayerEntity)player).connection.sendPacket(new SPlaySoundPacket(SetEffect.HOARDER.getSoundEvent(player, false).getRegistryName(), SoundCategory.PLAYERS, player.getPositionVec(), 0.6f, 1));	
		}

		@Override
		public boolean canInteractWith(PlayerEntity playerIn) {
			return true;
		}

		/**Create this container clientside*/
		public static HoarderContainer createContainerClientSide(int id, PlayerInventory playerInventory, PacketBuffer extraData) {
			return new HoarderContainer(id, playerInventory);
		}

	}

	public static class HoarderSlot extends Slot {
		public HoarderSlot(IInventory inventoryIn, int slotIndexIn, int xPosition, int yPosition) {
			super(inventoryIn, slotIndexIn, xPosition, yPosition);
		}

		/**Prevent putting in Hoarder items*/
		public boolean isItemValid(ItemStack stack) {
			return !(stack.getItem() instanceof BlockArmorItem && 
					((BlockArmorItem)stack.getItem()).set.setEffects.contains(SetEffect.HOARDER));
		}
	}	

	@Mod.EventBusSubscriber(bus = Bus.MOD)
	public static class RegistrationHandler {

		@SubscribeEvent
		public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
			containerType_9x1 = IForgeContainerType.create(HoarderContainer::createContainerClientSide);
			containerType_9x1.setRegistryName("hoarder_container_9x1");
			event.getRegistry().register(containerType_9x1);
			containerType_9x2 = IForgeContainerType.create(HoarderContainer::createContainerClientSide);
			containerType_9x2.setRegistryName("hoarder_container_9x2");
			event.getRegistry().register(containerType_9x2);
			containerType_9x3 = IForgeContainerType.create(HoarderContainer::createContainerClientSide);
			containerType_9x3.setRegistryName("hoarder_container_9x3");
			event.getRegistry().register(containerType_9x3);
			containerType_9x4 = IForgeContainerType.create(HoarderContainer::createContainerClientSide);
			containerType_9x4.setRegistryName("hoarder_container_9x4");
			event.getRegistry().register(containerType_9x4);
			containerType_9x5 = IForgeContainerType.create(HoarderContainer::createContainerClientSide);
			containerType_9x5.setRegistryName("hoarder_container_9x5");
			event.getRegistry().register(containerType_9x5);
			containerType_9x6 = IForgeContainerType.create(HoarderContainer::createContainerClientSide);
			containerType_9x6.setRegistryName("hoarder_container_9x6");
			event.getRegistry().register(containerType_9x6);
		}

	}

}