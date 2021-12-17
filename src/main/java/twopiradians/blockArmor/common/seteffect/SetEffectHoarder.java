package twopiradians.blockArmor.common.seteffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.server.ServerLifecycleHooks;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.BlockArmorItem;

@Mod.EventBusSubscriber
public class SetEffectHoarder extends SetEffect {

	/**Map of Hoarder item to items it stores so nbt only has to update once per tick*/
	private static HashMap<ItemStack, ItemStack[]> dirtyItems = Maps.newHashMap();

	public static MenuType<HoarderContainer> containerType_9x1;
	public static MenuType<HoarderContainer> containerType_9x2;
	public static MenuType<HoarderContainer> containerType_9x3;
	public static MenuType<HoarderContainer> containerType_9x4;
	public static MenuType<HoarderContainer> containerType_9x5;
	public static MenuType<HoarderContainer> containerType_9x6;

	private static final HashMap<EquipmentSlot, Integer> SLOT_TO_SIZE;

	static {
		SLOT_TO_SIZE = Maps.newHashMap();
		SLOT_TO_SIZE.put(EquipmentSlot.HEAD, 9);
		SLOT_TO_SIZE.put(EquipmentSlot.CHEST, 18);
		SLOT_TO_SIZE.put(EquipmentSlot.LEGS, 18);
		SLOT_TO_SIZE.put(EquipmentSlot.FEET, 9);
	}

	protected SetEffectHoarder() {
		super();
		this.color = ChatFormatting.GOLD;
		this.usesButton = true;
	}

	/** Only called when player wearing full, enabled set */
	@Override
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isClientSide && BlockArmor.key.isKeyDown(player) &&
				ArmorSet.getFirstSetItem(player, this) == stack &&
				!player.getCooldowns().isOnCooldown(stack.getItem())) {
			if (player instanceof ServerPlayer)
				((ServerPlayer)player).connection.send(new ClientboundCustomSoundPacket(SetEffect.HOARDER.getSoundEvent(player, true).getRegistryName(), SoundSource.PLAYERS, player.position(), 0.6f, 1));	
			player.openMenu(new HoarderProvider());
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
	public List<Component> addInformation(ItemStack stack, boolean isShiftDown, Player player, List<Component> tooltip, TooltipFlag flagIn) {
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
				MutableComponent comp = new TextComponent("  - ")
						.append(storedItem.getHoverName().copy().withStyle(this.color));
				comp.append(" x").append(String.valueOf(storedItem.getCount()));
				tooltip.add(comp);
			}
			if (nonEmptyItems.size() > 4)
				tooltip.add((new TextComponent("  ")
						.append(new TranslatableComponent("container.shulkerBox.more", nonEmptyItems.size()-4)).withStyle(ChatFormatting.ITALIC, this.color)));
		}

		return tooltip;
	}

	@SubscribeEvent
	public static void onTick(TickEvent.ServerTickEvent event) {
		if (!dirtyItems.isEmpty()) {
			for (ItemStack armor : dirtyItems.keySet()) {
				ItemStack[] items = dirtyItems.get(armor);
				setStoredItems(armor, items);
			}
			dirtyItems.clear();
		}
	}

	/**Get open/close sound event based on this block*/
	public SoundEvent getSoundEvent(Player player, boolean open) {
		ItemStack stack = ArmorSet.getFirstSetItem(player, SetEffect.HOARDER);
		if (stack != null) {
			Block block = ((BlockArmorItem)stack.getItem()).set.block;
			if (block instanceof BarrelBlock)
				return open ? SoundEvents.BARREL_OPEN : SoundEvents.BARREL_CLOSE;
			else if (block instanceof ShulkerBoxBlock)
				return open ? SoundEvents.SHULKER_BOX_OPEN : SoundEvents.SHULKER_BOX_CLOSE;				
		}
		return open ? SoundEvents.CHEST_OPEN : SoundEvents.CHEST_CLOSE;
	}

	/**Called when an item with Hoarder is broken*/
	public void onBreak(ItemStack stack) {
		// get player, item frame, or item entity with this item
		Entity entity = stack.getEntityRepresentation();
		if (entity == null && ServerLifecycleHooks.getCurrentServer() != null) {
			for (Player player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
				if (player.getInventory().contains(stack)) {
					entity = player;
					break;
				}
		}

		// spawn stored items
		if (entity != null && !entity.level.isClientSide) {
			NonNullList<ItemStack> storedItems = getStoredItems(stack);
			if (!storedItems.isEmpty()) {
				for (ItemStack storedItem : storedItems)
					entity.spawnAtLocation(storedItem);
				stack.setCount(0);
				// if hoarder gui is open - close it (to prevent dupes)
				if (entity instanceof Player && 
						((Player)entity).containerMenu instanceof HoarderContainer)
					((Player)entity).closeContainer();
			}
		}

	}

	/**Get container type based on which/how many slots have Hoarder items*/
	private static MenuType getContainerType(Player player) {
		int size = 0;
		for (ItemStack wornItem : ArmorSet.getAllSetItems(player, SetEffect.HOARDER))
			size += SLOT_TO_SIZE.get(((BlockArmorItem)wornItem.getItem()).getSlot());
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
	public static NonNullList<ItemStack> getStoredItems(Player player) {
		NonNullList<ItemStack> stacks = NonNullList.create();
		for (ItemStack wornItem : ArmorSet.getAllSetItems(player, SetEffect.HOARDER))
			stacks.addAll(getStoredItems(wornItem));
		return stacks;
	}

	/**Set stored stacks for this player's Hoarder items*/
	public static void setStoredItems(Player player, ItemStack[] stacks) {
		int index = 0;
		for (ItemStack wornItem : ArmorSet.getAllSetItems(player, SetEffect.HOARDER)) {
			int size = SLOT_TO_SIZE.get(((BlockArmorItem)wornItem.getItem()).getSlot());
			if (index + size <= stacks.length) 
				dirtyItems.put(wornItem, Arrays.copyOfRange(stacks, index, index+size));
			index += size;
		}
	}

	/**Get stacks stored in this Hoarder item*/
	public static NonNullList<ItemStack> getStoredItems(ItemStack wornItem) {
		NonNullList<ItemStack> stacks = NonNullList.withSize(SLOT_TO_SIZE.get(((BlockArmorItem)wornItem.getItem()).getSlot()), ItemStack.EMPTY);
		if (wornItem != null && !wornItem.isEmpty() && wornItem.hasTag()) {
			CompoundTag nbt = wornItem.getTag();
			ListTag list = nbt.getList(BlockArmor.MODID+":hoarderItems", 10);
			for (int i=0; i<list.size() && i<stacks.size(); ++i) {
				CompoundTag itemNbt = list.getCompound(i);
				stacks.set(i, ItemStack.of(itemNbt));
			}
		}
		return stacks;
	}

	/**Set stacks stored in this Hoarder item*/
	public static void setStoredItems(ItemStack wornItem, ItemStack[] stacks) {
		if (wornItem != null) {
			CompoundTag nbt = wornItem.hasTag() ? wornItem.getTag() : new CompoundTag();
			ListTag list = new ListTag();
			for (int i=0; i<stacks.length; ++i)
				if (stacks[i].isEmpty())
					list.add(i, new CompoundTag());
				else
					list.add(i, stacks[i].save(new CompoundTag()));
			nbt.put(BlockArmor.MODID+":hoarderItems", list);
			wornItem.setTag(nbt);
		}
	}

	private static class HoarderProvider implements MenuProvider {

		@Override
		public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
			return new HoarderContainer(id, playerInventory, getStoredItems(player).toArray(new ItemStack[0]), player);
		}

		@Override
		public Component getDisplayName() {
			return new TextComponent("Hoarder Storage");
		}
	}

	private static class HoarderContainer extends ChestMenu {

		private static final Field LAST_SLOTS_FIELD = ObfuscationReflectionHelper.findField(AbstractContainerMenu.class, "f_38841_");
		private static final Field REMOTE_SLOTS_FIELD = ObfuscationReflectionHelper.findField(AbstractContainerMenu.class, "f_150394_");

		public HoarderContainer(int id, Inventory playerInventory) {
			this(id, playerInventory, getStoredItems(playerInventory.player).toArray(new ItemStack[0]), playerInventory.player);
		}

		public HoarderContainer(int id, Inventory playerInventory, ItemStack[] storedItems, Player player) {
			this(id, playerInventory, player, new SimpleContainer(storedItems) {
				public void setChanged() {
					// update stored items in nbt
					ItemStack[] stacks = new ItemStack[this.getContainerSize()];
					for (int i=0; i<this.getContainerSize(); ++i)
						stacks[i] = this.getItem(i);
					setStoredItems(player, stacks);

					super.setChanged();
				}
			}, storedItems.length / 9);
		}

		public HoarderContainer(int id, Inventory playerInventory, Player player, SimpleContainer inv, int numRows) {
			super(getContainerType(player), id, playerInventory, inv, numRows);

			// clear existing slots
			this.slots.clear();
			try {
				((NonNullList<ItemStack>) LAST_SLOTS_FIELD.get(this)).clear(); 
				((NonNullList<ItemStack>) REMOTE_SLOTS_FIELD.get(this)).clear(); 
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			int i = (numRows - 4) * 18;

			// add custom slots to prevent storing more hoarder items
			for(int j = 0; j < numRows; ++j) 
				for(int k = 0; k < 9; ++k) 
					this.addSlot(new HoarderSlot(this.getContainer(), k + j * 9, 8 + k * 18, 18 + j * 18));

			// add regular inventory + hotbar slots
			for(int l = 0; l < 3; ++l) 
				for(int j1 = 0; j1 < 9; ++j1) 
					this.addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));

			for(int i1 = 0; i1 < 9; ++i1) 
				this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i));
		}

		@Override
		public void removed(Player player) {
			super.removed(player);

			if (!player.level.isClientSide && player instanceof ServerPlayer)
				((ServerPlayer)player).connection.send(new ClientboundCustomSoundPacket(SetEffect.HOARDER.getSoundEvent(player, false).getRegistryName(), SoundSource.PLAYERS, player.position(), 0.6f, 1));	
		}

		@Override
		public boolean stillValid(Player playerIn) {
			return true;
		}

		/**Create this container clientside*/
		public static HoarderContainer createContainerClientSide(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
			return new HoarderContainer(id, playerInventory);
		}

	}

	public static class HoarderSlot extends Slot {
		public HoarderSlot(Container inventoryIn, int slotIndexIn, int xPosition, int yPosition) {
			super(inventoryIn, slotIndexIn, xPosition, yPosition);
		}

		/**Prevent putting in Hoarder items and shulker boxes*/
		public boolean mayPlace(ItemStack stack) {
			return !(stack.getItem() instanceof BlockArmorItem && 
					((BlockArmorItem)stack.getItem()).set.setEffects.contains(SetEffect.HOARDER)) &&
					!(stack.getItem() instanceof BlockItem && ((BlockItem)stack.getItem()).getBlock() instanceof ShulkerBoxBlock);
		}
	}	

	@Mod.EventBusSubscriber(bus = Bus.MOD)
	public static class RegistrationHandler {

		@SubscribeEvent
		public static void registerContainers(final RegistryEvent.Register<MenuType<?>> event) {
			containerType_9x1 = IForgeMenuType.create(HoarderContainer::createContainerClientSide);
			containerType_9x1.setRegistryName("hoarder_container_9x1");
			event.getRegistry().register(containerType_9x1);
			containerType_9x2 = IForgeMenuType.create(HoarderContainer::createContainerClientSide);
			containerType_9x2.setRegistryName("hoarder_container_9x2");
			event.getRegistry().register(containerType_9x2);
			containerType_9x3 = IForgeMenuType.create(HoarderContainer::createContainerClientSide);
			containerType_9x3.setRegistryName("hoarder_container_9x3");
			event.getRegistry().register(containerType_9x3);
			containerType_9x4 = IForgeMenuType.create(HoarderContainer::createContainerClientSide);
			containerType_9x4.setRegistryName("hoarder_container_9x4");
			event.getRegistry().register(containerType_9x4);
			containerType_9x5 = IForgeMenuType.create(HoarderContainer::createContainerClientSide);
			containerType_9x5.setRegistryName("hoarder_container_9x5");
			event.getRegistry().register(containerType_9x5);
			containerType_9x6 = IForgeMenuType.create(HoarderContainer::createContainerClientSide);
			containerType_9x6.setRegistryName("hoarder_container_9x6");
			event.getRegistry().register(containerType_9x6);
		}

	}

}