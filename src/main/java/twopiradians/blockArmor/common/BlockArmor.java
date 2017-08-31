package twopiradians.blockArmor.common;

import java.io.File;

import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import twopiradians.blockArmor.client.key.KeyActivateSetEffect;
import twopiradians.blockArmor.common.command.CommandDev;
import twopiradians.blockArmor.creativetab.BlockArmorCreativeTab;

@Mod(modid = BlockArmor.MODID, version = BlockArmor.VERSION, name = BlockArmor.MODNAME, guiFactory = "twopiradians.blockArmor.client.gui.config.BlockArmorGuiFactory", updateJSON = "https://raw.githubusercontent.com/2piradians/Block-Armor/1.12/update.json")
public class BlockArmor
{
	public static final String MODNAME = "Block Armor"; 
	public static final String MODID = "blockarmor";
	public static final String VERSION = "2.4.7";
	public static BlockArmorCreativeTab vanillaTab;
	public static BlockArmorCreativeTab moddedTab;
	@SidedProxy(clientSide = "twopiradians.blockArmor.client.ClientProxy", serverSide = "twopiradians.blockArmor.common.CommonProxy")
	public static CommonProxy proxy;
	public static Logger logger;
	public static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
	public static KeyActivateSetEffect key = new KeyActivateSetEffect();
	public static CommandDev command = new CommandDev();
	public static File configFile;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(command);
	}

	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {
		proxy.loadComplete(event);
	}

	@Mod.EventBusSubscriber
	public static class MissingMappingsHandler {

		@SubscribeEvent
		public static void missingItemMappings(final RegistryEvent.MissingMappings<Item> event) {
			for (Mapping<Item> mapping : event.getMappings())
				mapping.ignore();
		}

		@SubscribeEvent
		public static void missingBlockMappings(final RegistryEvent.MissingMappings<Block> event) {
			for (Mapping<Block> mapping : event.getMappings())
				mapping.ignore();
		}
	}

}