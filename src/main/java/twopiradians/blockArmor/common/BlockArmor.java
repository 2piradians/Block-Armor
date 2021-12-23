package twopiradians.blockArmor.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import twopiradians.blockArmor.client.ClientProxy;
import twopiradians.blockArmor.client.key.KeyActivateSetEffect;

@Mod(value = BlockArmor.MODID)
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class BlockArmor {
			
	/**Changelog
	 * Fixed a crashing issue on startup with certain mods installed
	 * 
	 * FIXME (1.16.5) more reports of chests being empty - desert temple chests (tested in SP and not able to reproduce)
	 * FIXME (1.16.5) can't join server with MineColonies bc of "Exception: io.nettyhandler.codec.DecoderException: java.io.IOException: Payload may not be larger than 1048576 bytes"
	 */

	public static final String MODNAME = "Block Armor"; 
	public static final String MODID = "blockarmor";
	
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MODID, "main_channel"))
			.clientAcceptedVersions(NetworkRegistry.ABSENT::equals)
			.serverAcceptedVersions(NetworkRegistry.ABSENT::equals)
			.networkProtocolVersion(() -> NetworkRegistry.ABSENT)
			.simpleChannel();
	public static KeyActivateSetEffect key = new KeyActivateSetEffect();

	public BlockArmor() {}
	
	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		event.enqueueWork(CommonProxy::setup);
	}
	
	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		event.enqueueWork(ClientProxy::setup);
	}

}