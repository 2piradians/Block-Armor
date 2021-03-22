package twopiradians.blockArmor.common;

import java.util.ArrayList;
import java.util.Optional;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;
import twopiradians.blockArmor.common.seteffect.SetEffectAutoSmelt.SetEffectAutoSmeltModifier;
import twopiradians.blockArmor.common.seteffect.SetEffectLucky.SetEffectLuckyModifier;
import twopiradians.blockArmor.packet.CActivateSetEffectPacket;
import twopiradians.blockArmor.packet.SDevColorsPacket;
import twopiradians.blockArmor.packet.SSyncConfigPacket;

@Mod.EventBusSubscriber
public class CommonProxy 
{
	public ArrayList<ItemStack> itemsToDisable = new ArrayList<ItemStack>();

	/**Set world time*/
	public static void setWorldTime(World world, long time) {
		if (world instanceof ServerWorld)
			((ServerWorld)world).setDayTime(time);
	}

	public static void onSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(CommonProxy::setup);
	}

	public static void setup() {
		registerPackets();
		//BlockArmor.configFile = event.getSuggestedConfigurationFile();
		//ModTileEntities.preInit();
	}

	private static void registerPackets() { // Dist is where the packet goes TO
		int id = 0;
		BlockArmor.NETWORK.registerMessage(id++, SDevColorsPacket.class, SDevColorsPacket::encode, SDevColorsPacket::decode, SDevColorsPacket.Handler::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		BlockArmor.NETWORK.registerMessage(id++, CActivateSetEffectPacket.class, CActivateSetEffectPacket::encode, CActivateSetEffectPacket::decode, CActivateSetEffectPacket.Handler::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		BlockArmor.NETWORK.registerMessage(id++, SSyncConfigPacket.class, SSyncConfigPacket::encode, SSyncConfigPacket::decode, SSyncConfigPacket.Handler::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}

	@SubscribeEvent
	public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
		event.getRegistry().register(new SetEffectAutoSmeltModifier.Serializer().setRegistryName(new ResourceLocation(BlockArmor.MODID,"set_effect_autosmelt")));
		event.getRegistry().register(new SetEffectLuckyModifier.Serializer().setRegistryName(new ResourceLocation(BlockArmor.MODID,"set_effect_lucky")));
	}

	@SubscribeEvent(receiveCanceled=true)
	public static void commandDev(CommandEvent event) {
		try { // FIXME commandDev
			/*if (event.getCommand().getName().equalsIgnoreCase("dev") && 
					event.getCommand().checkPermission(event.getSender().getServer(), event.getSender()) &&
					CommandDev.runCommand(event.getSender().getServer(), event.getSender(), event.getParameters())) 
				event.setCanceled(true);*/
		}
		catch (Exception e) {}
	}

	@SubscribeEvent
	public static void playerJoin(PlayerLoggedInEvent event) {
		if (!event.getPlayer().world.isRemote && event.getPlayer() instanceof ServerPlayerEntity)
			BlockArmor.NETWORK.send(PacketDistributor.PLAYER.with(()->(ServerPlayerEntity) event.getPlayer()), new SDevColorsPacket());
	}

}
