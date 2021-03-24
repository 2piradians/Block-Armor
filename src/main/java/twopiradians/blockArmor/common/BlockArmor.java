package twopiradians.blockArmor.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import twopiradians.blockArmor.client.ClientProxy;
import twopiradians.blockArmor.client.key.KeyActivateSetEffect;
import twopiradians.blockArmor.creativetab.BlockArmorCreativeTab;

@Mod(value = BlockArmor.MODID/*, guiFactory = "twopiradians.blockArmor.client.gui.config.BlockArmorGuiFactory"*/)
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class BlockArmor {

	/**TODO partially ported on 8/31/20, but gave up
	 * and continued on 3/18/21*/

	public static final String MODNAME = "Block Armor"; 
	public static final String MODID = "blockarmor";
	public static final String VERSION = "2.4.12";
	public static BlockArmorCreativeTab vanillaTab;
	public static BlockArmorCreativeTab moddedTab;
	//@DistExecutor(clientDist = "twopiradians.blockArmor.client.ClientProxy", serverDist = "twopiradians.blockArmor.common.CommonProxy")
	//public static CommonProxy proxy;
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	private static final String PROTOCOL_VERSION = NetworkRegistry.ABSENT;
	public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MODID, "main_channel"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();
	public static KeyActivateSetEffect key = new KeyActivateSetEffect();
	//public static CommandDev commandDev = new CommandDev();
	//public static File configFile;

	public BlockArmor() {}
	
	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		ClientProxy.onSetup(event);
	}

	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		CommonProxy.onSetup(event);
	}

	@Mod.EventBusSubscriber
	public static class MissingMappingsHandler {

		@SubscribeEvent
		public static void missingItemMappings(final RegistryEvent.MissingMappings<Item> event) {
			for (Mapping<Item> mapping : event.getMappings(MODID))
				mapping.ignore();
		}

		@SubscribeEvent
		public static void missingBlockMappings(final RegistryEvent.MissingMappings<Block> event) {
			for (Mapping<Block> mapping : event.getMappings(MODID))
				mapping.ignore();
		}
	}

}