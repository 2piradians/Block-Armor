package twopiradians.blockArmor.common;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;
import twopiradians.blockArmor.common.command.CommandDev;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.recipe.RecipeBlockArmor;
import twopiradians.blockArmor.common.seteffect.SetEffectAutoSmelt.SetEffectAutoSmeltModifier;
import twopiradians.blockArmor.common.seteffect.SetEffectLucky.SetEffectLuckyModifier;
import twopiradians.blockArmor.packet.CActivateSetEffectPacket;
import twopiradians.blockArmor.packet.SDevColorsPacket;
import twopiradians.blockArmor.packet.SSyncCooldownsPacket;

@Mod.EventBusSubscriber
public class CommonProxy {

	@Mod.EventBusSubscriber(bus = Bus.MOD)
	public static class RegistrationHandler {

		@SubscribeEvent
		public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
			event.getRegistry().register(new SetEffectAutoSmeltModifier.Serializer().setRegistryName(new ResourceLocation(BlockArmor.MODID,"set_effect_autosmelt")));
			event.getRegistry().register(new SetEffectLuckyModifier.Serializer().setRegistryName(new ResourceLocation(BlockArmor.MODID,"set_effect_lucky")));
		}

	}

	public static void setup() {
		registerPackets();
	}
	
	private static void registerPackets() {
		int id = 0;
		BlockArmor.NETWORK.registerMessage(id++, SDevColorsPacket.class, SDevColorsPacket::encode, SDevColorsPacket::decode, SDevColorsPacket.Handler::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		BlockArmor.NETWORK.registerMessage(id++, CActivateSetEffectPacket.class, CActivateSetEffectPacket::encode, CActivateSetEffectPacket::decode, CActivateSetEffectPacket.Handler::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		BlockArmor.NETWORK.registerMessage(id++, SSyncCooldownsPacket.class, SSyncCooldownsPacket::encode, SSyncCooldownsPacket::decode, SSyncCooldownsPacket.Handler::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}
	
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		CommandDev.register(event.getDispatcher());
	}

	@SubscribeEvent
	public static void serverStart(FMLServerStartedEvent event) {
		registerRecipes(event.getServer());
	}
	
	private static void registerRecipes(MinecraftServer server) {
		try {
			Field recipesField = ObfuscationReflectionHelper.findField(RecipeManager.class, "field_199522_d");
			recipesField.setAccessible(true);
			Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipes = Maps.newHashMap((Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>>) recipesField.get(server.getRecipeManager()));
			for (ArmorSet set : ArmorSet.allSets) {
				if (set.isEnabled()) {
					NonNullList<Ingredient> helmetRecipe = NonNullList.from(Ingredient.EMPTY,
							Ingredient.fromStacks(set.getStack()), Ingredient.fromStacks(set.getStack()), Ingredient.fromStacks(set.getStack()),
							Ingredient.fromStacks(set.getStack()), Ingredient.EMPTY, Ingredient.fromStacks(set.getStack()));

					NonNullList<Ingredient> armorRecipe = NonNullList.from(Ingredient.EMPTY,
							Ingredient.fromStacks(set.getStack()), Ingredient.EMPTY, Ingredient.fromStacks(set.getStack()),
							Ingredient.fromStacks(set.getStack()), Ingredient.fromStacks(set.getStack()), Ingredient.fromStacks(set.getStack()),
							Ingredient.fromStacks(set.getStack()), Ingredient.fromStacks(set.getStack()), Ingredient.fromStacks(set.getStack()));

					NonNullList<Ingredient> legsRecipe = NonNullList.from(Ingredient.EMPTY,
							Ingredient.fromStacks(set.getStack()), Ingredient.fromStacks(set.getStack()), Ingredient.fromStacks(set.getStack()),
							Ingredient.fromStacks(set.getStack()), Ingredient.EMPTY, Ingredient.fromStacks(set.getStack()),
							Ingredient.fromStacks(set.getStack()), Ingredient.EMPTY, Ingredient.fromStacks(set.getStack()));

					NonNullList<Ingredient> bootsRecipe = NonNullList.from(Ingredient.EMPTY,
							Ingredient.fromStacks(set.getStack()), Ingredient.EMPTY, Ingredient.fromStacks(set.getStack()),
							Ingredient.fromStacks(set.getStack()), Ingredient.EMPTY, Ingredient.fromStacks(set.getStack()));

					Map<ResourceLocation, IRecipe<?>> map = Maps.newHashMap(recipes.get(IRecipeType.CRAFTING));
					map.put(set.helmet.getRegistryName(),
							new RecipeBlockArmor(set.helmet.getRegistryName(), set, BlockArmor.MODID+"_"+EquipmentSlotType.HEAD.getName(), 3, 2, helmetRecipe, new ItemStack(set.helmet)));
					map.put(set.chestplate.getRegistryName(),
							new RecipeBlockArmor(set.chestplate.getRegistryName(), set, BlockArmor.MODID+"_"+EquipmentSlotType.CHEST.getName(), 3, 3, armorRecipe, new ItemStack(set.chestplate)));
					map.put(set.leggings.getRegistryName(),
							new RecipeBlockArmor(set.leggings.getRegistryName(), set, BlockArmor.MODID+"_"+EquipmentSlotType.LEGS.getName(), 3, 3, legsRecipe, new ItemStack(set.leggings)));
					map.put(set.boots.getRegistryName(),
							new RecipeBlockArmor(set.boots.getRegistryName(), set, BlockArmor.MODID+"_"+EquipmentSlotType.FEET.getName(), 3, 2, bootsRecipe, new ItemStack(set.boots)));
					recipes.put(IRecipeType.CRAFTING, ImmutableMap.copyOf(map));
					recipesField.set(server.getRecipeManager(), recipes);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public static void playerJoin(PlayerLoggedInEvent event) {
		if (!event.getPlayer().world.isRemote && event.getPlayer() instanceof ServerPlayerEntity)
			BlockArmor.NETWORK.send(PacketDistributor.PLAYER.with(()->(ServerPlayerEntity) event.getPlayer()), new SDevColorsPacket());
	}
	
	/**Set world time*/
	public static void setWorldTime(World world, long time) {
		if (world instanceof ServerWorld)
			((ServerWorld)world).setDayTime(time);
	}

}