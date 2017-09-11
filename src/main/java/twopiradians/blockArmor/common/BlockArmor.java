package twopiradians.blockArmor.common;

import java.io.File;

import org.apache.logging.log4j.Logger;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import twopiradians.blockArmor.client.key.KeyActivateSetEffect;
import twopiradians.blockArmor.common.command.CommandDev;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.creativetab.BlockArmorCreativeTab;

@Mod(modid = BlockArmor.MODID, version = BlockArmor.VERSION, name = BlockArmor.MODNAME, guiFactory = "twopiradians.blockArmor.client.gui.config.BlockArmorGuiFactory", updateJSON = "https://raw.githubusercontent.com/2piradians/Block-Armor/1.10.2/update.json")
public class BlockArmor
{
	public static final String MODNAME = "Block Armor"; 
	public static final String MODID = "blockarmor";
	public static final String VERSION = "2.4.8";
	public static BlockArmorCreativeTab vanillaTab;
	public static BlockArmorCreativeTab moddedTab;
	@SidedProxy(clientSide = "twopiradians.blockArmor.client.ClientProxy", serverSide = "twopiradians.blockArmor.common.CommonProxy")
	public static CommonProxy proxy;
	public static Logger logger;
	public static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
	public static KeyActivateSetEffect key = new KeyActivateSetEffect();
	public static CommandDev command = new CommandDev();
	protected static File configFile;

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
	
	/**Replace armor from old versions to new auto-generated armor and ignore other missing mappings*/
	@Mod.EventHandler
	public void missingMapping(FMLMissingMappingsEvent event) {
		for (MissingMapping mapping : event.get()) {
			try {
				String block = mapping.name.replace("blockarmor:", "");
				String armor = block.substring(block.indexOf("_")+1);
				block = block.substring(0, block.indexOf("_"));
				ArmorSet set = null;
				if (block.equalsIgnoreCase("sugarcane"))
					set = ArmorSet.getSet(Blocks.REEDS, 0);
				else if (block.equalsIgnoreCase("stone"))
					set = ArmorSet.getSet(Blocks.STONE, 0);
				else if (block.equalsIgnoreCase("sprucewood"))
					set = ArmorSet.getSet(Blocks.LOG, 1);
				else if (block.equalsIgnoreCase("sprucewoodplanks"))
					set = ArmorSet.getSet(Blocks.PLANKS, 1);
				else if (block.equalsIgnoreCase("snow"))
					set = ArmorSet.getSet(Blocks.SNOW, 0);
				else if (block.equalsIgnoreCase("smoothgranite"))
					set = ArmorSet.getSet(Blocks.STONE, 2);
				else if (block.equalsIgnoreCase("smoothdiorite"))
					set = ArmorSet.getSet(Blocks.STONE, 4);
				else if (block.equalsIgnoreCase("smoothandesite"))
					set = ArmorSet.getSet(Blocks.STONE, 6);
				else if (block.equalsIgnoreCase("slime"))
					set = ArmorSet.getSet(Blocks.SLIME_BLOCK, 0);
				else if (block.equalsIgnoreCase("redstone"))
					set = ArmorSet.getSet(Blocks.REDSTONE_BLOCK, 0);
				else if (block.equalsIgnoreCase("quartz"))
					set = ArmorSet.getSet(Blocks.QUARTZ_BLOCK, 0);
				else if (block.equalsIgnoreCase("obsidian"))
					set = ArmorSet.getSet(Blocks.OBSIDIAN, 0);
				else if (block.equalsIgnoreCase("oakwood"))
					set = ArmorSet.getSet(Blocks.LOG, 0);
				else if (block.equalsIgnoreCase("oakwoodplanks"))
					set = ArmorSet.getSet(Blocks.PLANKS, 0);
				else if (block.equalsIgnoreCase("netherrack"))
					set = ArmorSet.getSet(Blocks.NETHERRACK, 0);
				else if (block.equalsIgnoreCase("lapis"))
					set = ArmorSet.getSet(Blocks.LAPIS_BLOCK, 0);
				else if (block.equalsIgnoreCase("junglewood"))
					set = ArmorSet.getSet(Blocks.LOG, 3);
				else if (block.equalsIgnoreCase("junglewoodplanks"))
					set = ArmorSet.getSet(Blocks.PLANKS, 3);
				else if (block.equalsIgnoreCase("granite"))
					set = ArmorSet.getSet(Blocks.STONE, 1);
				else if (block.equalsIgnoreCase("endstone"))
					set = ArmorSet.getSet(Blocks.END_STONE, 0);
				else if (block.equalsIgnoreCase("emerald"))
					set = ArmorSet.getSet(Blocks.EMERALD_BLOCK, 0);
				else if (block.equalsIgnoreCase("dirt"))
					set = ArmorSet.getSet(Blocks.DIRT, 0);
				else if (block.equalsIgnoreCase("diorite"))
					set = ArmorSet.getSet(Blocks.STONE, 3);
				else if (block.equalsIgnoreCase("darkprismarine"))
					set = ArmorSet.getSet(Blocks.PRISMARINE, 2);
				else if (block.equalsIgnoreCase("darkoakwood"))
					set = ArmorSet.getSet(Blocks.LOG2, 1);
				else if (block.equalsIgnoreCase("darkoakwoodplanks"))
					set = ArmorSet.getSet(Blocks.PLANKS, 5);
				else if (block.equalsIgnoreCase("cobble"))
					set = ArmorSet.getSet(Blocks.COBBLESTONE, 0);
				else if (block.equalsIgnoreCase("brick"))
					set = ArmorSet.getSet(Blocks.BRICK_BLOCK, 0);
				else if (block.equalsIgnoreCase("birchwood"))
					set = ArmorSet.getSet(Blocks.LOG, 2);
				else if (block.equalsIgnoreCase("birchwoodplanks"))
					set = ArmorSet.getSet(Blocks.PLANKS, 2);
				else if (block.equalsIgnoreCase("bedrock"))
					set = ArmorSet.getSet(Blocks.BEDROCK, 0);
				else if (block.equalsIgnoreCase("andesite"))
					set = ArmorSet.getSet(Blocks.STONE, 5);
				else if (block.equalsIgnoreCase("acaciawood"))
					set = ArmorSet.getSet(Blocks.LOG2, 0);
				else if (block.equalsIgnoreCase("acaciawoodplanks"))
					set = ArmorSet.getSet(Blocks.PLANKS, 4);
				
				Item item = null;
				
				if (set != null) {
					if (armor.equalsIgnoreCase("helmet"))
						item = set.helmet;
					else if (armor.equalsIgnoreCase("chestplate"))
						item = set.chestplate;
					else if (armor.equalsIgnoreCase("leggings"))
						item = set.leggings;
					else if (armor.equalsIgnoreCase("boots"))
						item = set.boots;
				}
				
				if (item != null)
					mapping.remap(item);
				else
					mapping.ignore();
			}
			catch (Exception e) {}
		}
	}
}