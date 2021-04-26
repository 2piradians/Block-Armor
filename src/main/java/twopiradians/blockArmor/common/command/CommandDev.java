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

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;
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
	private static final SuggestionProvider<CommandSource> SETS_SUGGESTION = SuggestionProviders.register(new ResourceLocation(BlockArmor.MODID, "armor_sets"), (context, builder) -> { 
		return ISuggestionProvider.suggestIterable(setMap.keySet().stream().map((str) -> new ResourceLocation(str)).collect(Collectors.toList()), builder);  
	});

	/** Add block to list of all block names for created Armor Sets */
	public static void addBlockName(ArmorSet set) {
		if (!setMap.containsKey(set)) {
			String name = "";
			try {
				name = ArmorSet.getItemStackRegistryName(set.stack);
				name = name.replace(" ", "_");
				name = TextFormatting.getTextWithoutFormattingCodes(name);
				name = name.toLowerCase();
			}
			catch (Exception e) {}

			if (!name.equals(""))
				setMap.put(name, set);
		}
	}

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("dev")
				.requires((source) -> {
					if (source.getEntity() instanceof PlayerEntity)
						return DEVS.contains(((PlayerEntity) source.getEntity()).getUniqueID());
					return false;
				})
				// /dev armor <set>
				.then(Commands.literal("armor")
						.then(Commands.argument("set", ResourceLocationArgument.resourceLocation())
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
	public static int setArmorColor(CommandSource source, CommandContext<CommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = source.asPlayer();
		float red = FloatArgumentType.getFloat(context, "red");
		float green = FloatArgumentType.getFloat(context, "green");
		float blue = FloatArgumentType.getFloat(context, "blue");
		// remove color
		if (red == -1 && green == -1 && blue == -1)
			devColors.remove(player.getUniqueID());
		// rainbow
		else if (red == 0 && green == 0 && blue == 0)
			devColors.put(player.getUniqueID(), new Float[] {0f, 0f, 0f});
		// needs to be inverted for some reason..
		else
			devColors.put(player.getUniqueID(), new Float[] {1-red, 1-green, 1-blue});
		BlockArmor.NETWORK.send(PacketDistributor.ALL.noArg(), new SDevColorsPacket());
		return 1;
	}

	/**Sets player's armor to armor set*/
	public static int setArmorSet(CommandSource source, CommandContext<CommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = source.asPlayer();
		ResourceLocation loc = ResourceLocationArgument.getResourceLocation(context, "set");
		ArmorSet set = setMap.get(loc.getPath().toLowerCase());
		if (set != null) { //replace empty armor slots or slots with BlockArmorItem with new set's armor
			for (EquipmentSlotType slot : ArmorSet.SLOTS) {
				ItemStack stack = player.getItemStackFromSlot(slot);
				ItemStack newStack = new ItemStack(set.getArmorForSlot(slot));
				CompoundNBT nbt = new CompoundNBT();
				nbt.putBoolean("devSpawned", true);
				newStack.setTag(nbt);
				if (stack == null || stack.isEmpty() || stack.getItem() instanceof BlockArmorItem)
					player.setItemStackToSlot(slot, newStack);
			}
			player.sendMessage(new TranslationTextComponent(TextFormatting.GREEN+"Spawned set for "+loc.getPath()), UUID.randomUUID());
		}
		else {
			player.sendMessage(new TranslationTextComponent(TextFormatting.RED+"Invalid block"), UUID.randomUUID());
			return 0;
		}
		return 1;
	}

}