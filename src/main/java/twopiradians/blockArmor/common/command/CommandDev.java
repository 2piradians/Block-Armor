package twopiradians.blockArmor.common.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Maps;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ItemBlockArmor;
import twopiradians.blockArmor.packet.PacketDevColors;

public class CommandDev implements ICommand 
{
	/**Map of Armor Sets and their block's display name (with space replaced by underscore)*/
	private static HashMap<String, ArmorSet> setMap = Maps.newHashMap();
	private static final String ARMOR = "armor";
	private static final String COLOR = "color";
	/**List of command names*/
	private static ArrayList<String> ALL_COMMAND_NAMES = new ArrayList<String>() {{
		add(ARMOR);
		add(COLOR);
	}};
	public static final ArrayList<UUID> DEVS = new ArrayList<UUID>() {{
		add(UUID.fromString("f08951bc-e379-4f19-a113-7728b0367647")); //Furgl
		add(UUID.fromString("93d28330-e1e2-447b-b552-00cb13e9afbd")); //2piradians
	}};
	public static HashMap<UUID, Float[]> devColors = Maps.newHashMap();

	/**Add block to list of all block names for created Armor Sets*/
	public static void addBlockName(ArmorSet set) {
		if (!setMap.containsKey(set)) {
			String name = "";
			try {
				name = set.stack.getDisplayName();
				name = name.replace(" ", "_");
				name = TextFormatting.getTextWithoutFormattingCodes(name);
			}
			catch (Exception e) {}

			if (!name.equals(""))
				setMap.put(name, set);
		}
	}

	@Override
	public int compareTo(ICommand o) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "dev";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "";
	}

	@Override
	public List<String> getCommandAliases() {
		return new ArrayList<String>();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
	}
	
	/**Actually runs the command (for chat event), returns if message was a valid command (and chat should be hidden)*/
	public static boolean runCommand(MinecraftServer server, ICommandSender sender, String[] args) {
		if (sender instanceof EntityPlayer) {
			if (args.length == 2 && args[0].equalsIgnoreCase(ARMOR)) {
				ArmorSet set = setMap.get(args[1]);
				if (set != null) { //replace empty armor slots or slots with ItemBlockArmor with new set's armor
					for (EntityEquipmentSlot slot : ArmorSet.SLOTS) {
						ItemStack stack = ((EntityPlayer) sender).getItemStackFromSlot(slot);
						ItemStack newStack = new ItemStack(set.getArmorForSlot(slot));
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setBoolean("devSpawned", true);
						newStack.setTagCompound(nbt);
						if (stack == null || stack.getItem() instanceof ItemAir || stack.getItem() instanceof ItemBlockArmor)
							((EntityPlayer) sender).setItemStackToSlot(slot, newStack);
					}
					sender.addChatMessage(new TextComponentTranslation(TextFormatting.GREEN+"Spawned set for "+args[1].replace("_", " ")));
				}
				else
					sender.addChatMessage(new TextComponentTranslation(TextFormatting.RED+"Invalid block"));
				return true;
			}
			else if (args.length == 4 && args[0].equalsIgnoreCase(COLOR)) {
				try {
					Float[] color = new Float[] {Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3])};
					if (color[0] == -1 && color[1] == -1 && color[2] == -1)
						devColors.remove(((EntityPlayer) sender).getPersistentID());
					else
						devColors.put(((EntityPlayer) sender).getPersistentID(), color);
					BlockArmor.network.sendToAll(new PacketDevColors());
				}
				catch (Exception e) {
					sender.addChatMessage(new TextComponentTranslation(TextFormatting.RED+"Color must be 3 floats (-1 to 1 for standard results)"));
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		if (sender instanceof EntityPlayer)
			return DEVS.contains(((EntityPlayer) sender).getPersistentID());
		return false;
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
		if (args.length == 1)
			return  CommandBase.getListOfStringsMatchingLastWord(args, ALL_COMMAND_NAMES);
		else if (args.length == 2 && args[0].equalsIgnoreCase(ARMOR))
			return CommandBase.getListOfStringsMatchingLastWord(args, (Collection<String>)setMap.keySet());
		else if (args.length < 5 && args[0].equalsIgnoreCase(COLOR))
			return new ArrayList<String>() {{add("-1"); add("0");}};
			else
				return new ArrayList<String>();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}
}