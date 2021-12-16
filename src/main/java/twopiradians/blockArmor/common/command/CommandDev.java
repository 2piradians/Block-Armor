package twopiradians.blockArmor.common.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.BlockArmorItem;
import twopiradians.blockArmor.packet.SDevColorsPacket;

public class CommandDev  {

	/**
	 * Map of Armor Sets and their block's display name (with space replaced by
	 * underscore)
	 */ 
	private static HashMap<String, ArmorSet> setMap = Maps.newHashMap();
	public static final ArrayList<UUID> DEVS = new ArrayList<UUID>() {{
		add(UUID.fromString("f08951bc-e379-4f19-a113-7728b0367647")); //Furgl
		add(UUID.fromString("93d28330-e1e2-447b-b552-00cb13e9afbd")); //2piradians
	}};
	public static HashMap<UUID, Float[]> devColors = Maps.newHashMap();			
	private static final SuggestionProvider<CommandSourceStack> SETS_SUGGESTION = SuggestionProviders.register(new ResourceLocation(BlockArmor.MODID, "armor_sets"), (context, builder) -> { 
		return SharedSuggestionProvider.suggestResource(setMap.keySet().stream().map((str) -> new ResourceLocation(str)).collect(Collectors.toList()), builder);  
	});

	/** Add block to list of all block names for created Armor Sets */
	public static void addBlockName(ArmorSet set) {
		if (!setMap.containsKey(set)) {
			String name = "";
			try {
				name = ArmorSet.getItemRegistryName(set.item);
				name = name.replace(" ", "_");
				name = ChatFormatting.stripFormatting(name);
				name = name.toLowerCase();
			}
			catch (Exception e) {}

			if (!name.equals(""))
				setMap.put(name, set);
		}
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("dev")
				.requires((source) -> {
					if (source.getEntity() instanceof Player)
						return DEVS.contains(((Player) source.getEntity()).getUUID());
					return false;
				})
				// /dev armor <set>
				.then(Commands.literal("armor")
						.then(Commands.argument("set", ResourceLocationArgument.id())
								.suggests(SETS_SUGGESTION)
								.executes((context) -> {
									return setArmorSet(context.getSource(), context);
								})))
				// /dev color r g b
				.then(Commands.literal("color")
						.then(Commands.argument("red", FloatArgumentType.floatArg(-1, 1))
								.then(Commands.argument("green", FloatArgumentType.floatArg(-1, 1))
										.then(Commands.argument("blue", FloatArgumentType.floatArg(-1, 1))
												.executes((context) -> {
													return setArmorColor(context.getSource(), context);
												}))))));
	}

	/**Color's player's block armor*/
	public static int setArmorColor(CommandSourceStack source, CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		float red = FloatArgumentType.getFloat(context, "red");
		float green = FloatArgumentType.getFloat(context, "green");
		float blue = FloatArgumentType.getFloat(context, "blue");
		// remove color
		if (red == -1 && green == -1 && blue == -1)
			devColors.remove(player.getUUID());
		// rainbow
		else if (red == 0 && green == 0 && blue == 0)
			devColors.put(player.getUUID(), new Float[] {0f, 0f, 0f});
		// needs to be inverted for some reason..
		else
			devColors.put(player.getUUID(), new Float[] {1-red, 1-green, 1-blue});
		BlockArmor.NETWORK.send(PacketDistributor.ALL.noArg(), new SDevColorsPacket());
		return 1;
	}

	/**Sets player's armor to armor set*/
	public static int setArmorSet(CommandSourceStack source, CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		ResourceLocation loc = ResourceLocationArgument.getId(context, "set");
		ArmorSet set = setMap.get(loc.getPath().toLowerCase());
		if (set != null) { //replace empty armor slots or slots with BlockArmorItem with new set's armor
			for (EquipmentSlot slot : ArmorSet.SLOTS) {
				ItemStack stack = player.getItemBySlot(slot);
				ItemStack newStack = new ItemStack(set.getArmorForSlot(slot));
				CompoundTag nbt = new CompoundTag();
				nbt.putBoolean("devSpawned", true);
				newStack.setTag(nbt);
				if (stack == null || stack.isEmpty() || stack.getItem() instanceof BlockArmorItem) 
					player.setItemSlot(slot, newStack);
			}
			player.sendMessage(new TranslatableComponent(ChatFormatting.GREEN+"Spawned set for "+loc.getPath()), UUID.randomUUID());
		}
		else {
			player.sendMessage(new TranslatableComponent(ChatFormatting.RED+"Invalid block"), UUID.randomUUID());
			return 0;
		}
		return 1;
	}

}