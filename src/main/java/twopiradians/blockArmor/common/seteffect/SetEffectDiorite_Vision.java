package twopiradians.blockArmor.common.seteffect;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectDiorite_Vision extends SetEffect {

	private HashMap<UUID, BlockPos> dioriteSpots;

	protected SetEffectDiorite_Vision() {
		this.color = TextFormatting.OBFUSCATED;
		this.description = "You can never have enough DIORITE!";
		this.dioriteSpots = Maps.newHashMap();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		//remove diorite spot
		if (this.dioriteSpots.containsKey(event.player.getPersistentID()) &&
				event.player instanceof EntityPlayerMP && 
				!ArmorSet.getWornSetEffects(event.player).contains(this)) {
			this.changeBlocks(this.dioriteSpots.get(event.player.getPersistentID()), (EntityPlayerMP) event.player, false);
			this.dioriteSpots.remove(event.player.getPersistentID());
		}
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		//create new diorite spot
		if (!dioriteSpots.containsKey(player.getPersistentID()) && 
				!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack &&
				player instanceof EntityPlayerMP && player.ticksExisted > 200) {
			this.changeBlocks(player.getPosition(), (EntityPlayerMP) player, true);
			this.dioriteSpots.put(player.getPersistentID(), player.getPosition());
		}

		//add diorite to player's inventory
		if (world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack) {
			player.inventory.addItemStackToInventory(new ItemStack(Blocks.STONE, 1, 3));
		}
	}

	/**Send packets to change all blocks in radius to diorite or reset them*/
	@SuppressWarnings("deprecation")
	private void changeBlocks(BlockPos startPos, EntityPlayerMP player, boolean toDiorite) {
		int radius = 50;
		Iterable<BlockPos> list = BlockPos.getAllInBox(player.getPosition().add(-radius, -radius, -radius), 
				player.getPosition().add(radius, radius, radius));
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
	public List<String> addInformation(ItemStack stack, boolean isShiftDown, EntityPlayer player, List<String> tooltip, boolean advanced) {
		boolean deobfuscate = ArmorSet.getWornSetEffects(player).contains(this);
		if (deobfuscate)
			this.color = TextFormatting.WHITE;
		List<String> list = super.addInformation(stack, isShiftDown, player, tooltip, advanced);
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