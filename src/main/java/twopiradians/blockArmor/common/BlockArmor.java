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
	
	// TEST on server (mainly new set effect caching)
		
	/**Changelog
	 * Added Vanilla armor sets:
	 *  All Beds
	 * 	All Shulker Boxes
	 *  Chest
	 *  Composter
	 *  Blast Furnace
	 *  Smoker
	 *  Bee Nest
	 *  BeeHive
	 *  Barrel
	 *  Honey Block
	 *  Jukebox
	 * Added Set Effects:
	 * 	Rocky: Sink like a rock underwater
	 *  Sleepy: Sleep anywhere instantly
	 *  Hoarder: TODO
	 *  Undying: Saves you from death
	 *  Respawn: Teleports you to your respawn point before death
	 * Changed Set Effects:
	 *  Auto Smelt: now works with mob drops as well
	 * 	Diving Suit: added Aqua Affinity
	 *  Ender: TODO
	 *  Tweaked which armors get set effects (may need to delete your config to update)
	 * Fixes:
	 *  Fixed the issue with inventory icons sometimes missing textures
	 *  Fixed Cactus armor texture
	 *  Fixed Enchanting Table armor texture
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
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		event.enqueueWork(ClientProxy::setup);
	}

	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		event.enqueueWork(CommonProxy::setup);
	}

}