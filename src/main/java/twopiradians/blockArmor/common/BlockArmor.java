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

@Mod(value = BlockArmor.MODID)
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class BlockArmor {

	// TODO adjust armor texture lineup (see dispenser / logs)
	// TODO add stats to config
	// FIXME item rendering
	// FIXME translucent armor
	// FIXME f3+t breaking textures
	// FIXME can't auto-fill crafting recipes
	// TEST on server
	// TEST all set effects
	// TEST resource packs

	public static final String MODNAME = "Block Armor"; 
	public static final String MODID = "blockarmor";
	public static final String VERSION = "2.4.12";
	public static BlockArmorCreativeTab vanillaTab;
	public static BlockArmorCreativeTab moddedTab;
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
		ClientProxy.onSetup(event);
	}

	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		CommonProxy.onSetup(event);
	}

	@Mod.EventBusSubscriber
	public static class MissingMappingsHandler { // TEST missingmappingshandler - is this needed still?

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