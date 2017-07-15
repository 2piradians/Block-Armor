package twopiradians.blockArmor.common.seteffect;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.jei.BlockArmorJEIPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber
public class SetEffectDiorite_Vision extends SetEffect {

	private HashMap<UUID, BlockPos> dioriteSpots;

	protected SetEffectDiorite_Vision() {
		this.color = TextFormatting.OBFUSCATED;
		this.description = "You can never have enough DIORITE!";
		this.dioriteSpots = Maps.newHashMap();
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		//remove diorite spot
		if (this.dioriteSpots.containsKey(event.player.getPersistentID()) &&
				event.player instanceof EntityPlayerMP && 
				!ArmorSet.getWornSetEffects(event.player).contains(this)) {
			this.changeBlocks(this.dioriteSpots.get(event.player.getPersistentID()), (EntityPlayerMP) event.player, false);
			this.dioriteSpots.remove(event.player.getPersistentID());
			//update inventory
			for (int i = 0; i < event.player.inventoryContainer.inventorySlots.size(); ++i) {
				ItemStack itemstack = ((Slot)event.player.inventoryContainer.inventorySlots.get(i)).getStack();
				((EntityPlayerMP)event.player).sendSlotContents(event.player.inventoryContainer, i, itemstack);
			}
		}
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		//don't do anything for creative players, bc clientside changes are accepted
		if (player.capabilities.isCreativeMode)
			return;
		
		//create new diorite spot
		if (!dioriteSpots.containsKey(player.getPersistentID()) && 
				!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack &&
				player instanceof EntityPlayerMP && player.ticksExisted > 200) {
			this.changeBlocks(player.getPosition(), (EntityPlayerMP) player, true);
			this.dioriteSpots.put(player.getPersistentID(), player.getPosition());
		}

		//add diorite to player's inventory
		if (world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack) {
			ItemStack diorite = new ItemStack(Blocks.STONE, 5, 3);
			//clear inventory, set jei search, set armor
			if (!ItemStack.areItemsEqualIgnoreDurability(player.getHeldItemOffhand(), diorite)) {
				if (Loader.isModLoaded("jei"))
					BlockArmorJEIPlugin.setFilterText("I LOVE |DIORITE| !!!!!!");
				player.inventory.clear();
				player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, diorite.copy());
				ArmorSet set = ArmorSet.getSet(Blocks.STONE, 3);
				if (set != null)
					for (EntityEquipmentSlot slot : ArmorSet.SLOTS)
						player.setItemStackToSlot(slot, new ItemStack(set.getArmorForSlot(slot)));
			}
			player.inventory.addItemStackToInventory(diorite);
		}
	}

	/**Send packets to change all blocks in radius to diorite or reset them*/
	@SuppressWarnings("deprecation")
	private void changeBlocks(BlockPos startPos, EntityPlayerMP player, boolean toDiorite) {
		int radius = 20;
		Iterable<BlockPos> list = BlockPos.getAllInBox(startPos.add(-radius, -radius, -radius), 
				startPos.add(radius, radius, radius));
		for (BlockPos pos : list)
			if (!player.world.isAirBlock(pos)) {
				SPacketBlockChange packet = new SPacketBlockChange(player.world, pos);
				if (toDiorite)
					packet.blockState = Blocks.STONE.getStateFromMeta(3);
				((EntityPlayerMP)player).connection.sendPacket(packet);
			}
	}

	/**Set effect name and description if shifting*/
	@SideOnly(Side.CLIENT)
	public List<String> addInformation(ItemStack stack, boolean isShiftDown, EntityPlayer player, List<String> tooltip, ITooltipFlag flagIn) {
		boolean deobfuscate = ArmorSet.getWornSetEffects(player).contains(this);
		if (deobfuscate)
			this.color = TextFormatting.WHITE;
		List<String> list = super.addInformation(stack, isShiftDown, player, tooltip, flagIn);
		if (deobfuscate)
			this.color = TextFormatting.OBFUSCATED;

		return list;
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {	
		if (SetEffect.registryNameContains(block, meta, new String[] {"diorite"}))
			return true;		
		return false;
	}
}