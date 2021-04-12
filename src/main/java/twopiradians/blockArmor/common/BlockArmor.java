package twopiradians.blockArmor.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import twopiradians.blockArmor.client.ClientProxy;
import twopiradians.blockArmor.client.key.KeyActivateSetEffect;

@Mod(value = BlockArmor.MODID)
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class BlockArmor {

	// f3+t breaking textures (seems to be race-condition)
	
	/**Changelog
	 *  Rebalanced armor stats
	 *  Revamped config to allow more customization like changing armor stats and set effects
	 *  Added sound effects to Time Control set effect
	 *  Health Boost set effect will restore your boosted health on login / after respawning*/

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
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		event.enqueueWork(ClientProxy::setup);
	}

	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		event.enqueueWork(CommonProxy::setup);
	}

}