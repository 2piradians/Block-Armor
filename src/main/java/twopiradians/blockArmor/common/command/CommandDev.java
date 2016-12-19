package twopiradians.blockArmor.common.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Maps;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import twopiradians.blockArmor.common.item.ArmorSet;

public class CommandDev implements ICommand 
{
	/**Map of Armor Sets and their block's display name (with space replaced by underscore)*/
	private static HashMap<String, ArmorSet> blockNamesMap = Maps.newHashMap();
	private static final String ARMOR = "armor";
	/**List of command names*/
	private static ArrayList<String> ALL_COMMAND_NAMES = new ArrayList<String>() {{
		add(ARMOR);
	}};
	private static final ArrayList<UUID> DEVS= new ArrayList<UUID>() {{
		add(UUID.fromString("f08951bc-e379-4f19-a113-7728b0367647")); //Furgl
		add(UUID.fromString("93d28330-e1e2-447b-b552-00cb13e9afbd")); //2piradians
	}};

	/**Add block to list of all block names for created Armor Sets*/
	public static void addBlockName(ArmorSet set) {
		if (!blockNamesMap.containsKey(set)) {
			String name = "";
			try {
				name = new ItemStack(set.block).getDisplayName();
				name = name.replace(" ", "_");
			}
			catch (Exception e) {}

			if (!name.equals(""))
				blockNamesMap.put(name, set);
		}
	}

	@Override
	public int compareTo(ICommand o) 
	{
		return 0;
	}

	@Override
	public String getCommandName() 
	{
		return "dev";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "";
	}

	@Override
	public List<String> getCommandAliases() 
	{
		return new ArrayList<String>();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (sender instanceof EntityPlayer) {
			if (args.length == 2 && args[0].equalsIgnoreCase(ARMOR)) {
				ArmorSet set = blockNamesMap.get(args[1]);
				if (set != null) {
					((EntityPlayer) sender).inventory.addItemStackToInventory(new ItemStack(set.helmet));
					((EntityPlayer) sender).inventory.addItemStackToInventory(new ItemStack(set.chestplate));
					((EntityPlayer) sender).inventory.addItemStackToInventory(new ItemStack(set.leggings));
					((EntityPlayer) sender).inventory.addItemStackToInventory(new ItemStack(set.boots));
					sender.addChatMessage(new TextComponentTranslation(TextFormatting.GREEN+"Spawned set for "+args[1].replace("_", " ")));
				}
				else
					sender.addChatMessage(new TextComponentTranslation(TextFormatting.RED+"Invalid block"));
			}
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) 
	{
		if (sender instanceof EntityPlayer)
			return DEVS.contains(((EntityPlayer) sender).getPersistentID());
		return false;
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) 
	{
		if (args.length == 1)
			return ALL_COMMAND_NAMES;
		else if (args.length == 2 && args[0].equalsIgnoreCase(ARMOR))
			return new ArrayList<String>(blockNamesMap.keySet());
		else
			return new ArrayList<String>();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) 
	{
		return false;
	}
}
