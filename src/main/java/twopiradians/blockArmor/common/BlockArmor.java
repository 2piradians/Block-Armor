package twopiradians.blockArmor.common;

import java.io.File;

import org.apache.logging.log4j.Logger;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import twopiradians.blockArmor.client.gui.armorDisplay.OpenGuiEvent;
import twopiradians.blockArmor.common.block.ModBlocks;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.events.IgniteTargetEvent;
import twopiradians.blockArmor.common.events.IncreaseFortuneEvent;
import twopiradians.blockArmor.common.events.StopFallDamageEvent;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ModItems;
import twopiradians.blockArmor.common.tileentity.ModTileEntities;
import twopiradians.blockArmor.creativetab.BlockArmorCreativeTab;

@Mod(modid = BlockArmor.MODID, version = BlockArmor.VERSION, name = BlockArmor.MODNAME, guiFactory = "twopiradians.blockArmor.client.gui.config.BlockArmorGuiFactory")
public class BlockArmor
{
	public static final String MODNAME = "Block Armor"; 
	public static final String MODID = "blockarmor";
	public static final String VERSION = "1.2.1";
	public static final BlockArmorCreativeTab tab = new BlockArmorCreativeTab("tabBlockArmor");
	@SidedProxy(clientSide = "twopiradians.blockArmor.client.ClientProxy", serverSide = "twopiradians.blockArmor.common.CommonProxy")
	public static CommonProxy proxy;
	public static Logger logger;
	/**Should armor display be opened on chat event? ONLY WORKS IN SP*/
	public static final boolean DISPLAY_ARMOR_GUI = false;
	private File configFile;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		proxy.preInit();
		ModBlocks.preInit();
		ModTileEntities.preInit();
		this.configFile = event.getSuggestedConfigurationFile();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init();
		registerEventListeners();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		ArmorSet.postInit();
		Config.postInit(configFile);
		ModItems.postInit();
		registerRecipes();
		proxy.postInit();
	}

	private void registerRecipes() {
		for (ArmorSet set : ArmorSet.allSets) {
			if (set.block == Blocks.EMERALD_BLOCK) {
				GameRegistry.addShapedRecipe(new ItemStack(set.helmet),"AAA","A A",'A', new ItemStack(Items.EMERALD));
				GameRegistry.addShapedRecipe(new ItemStack(set.chestplate),"A A","AAA","AAA",'A', new ItemStack(Items.EMERALD));
				GameRegistry.addShapedRecipe(new ItemStack(set.leggings),"AAA","A A","A A",'A', new ItemStack(Items.EMERALD));
				GameRegistry.addShapedRecipe(new ItemStack(set.boots),"A A","A A",'A', new ItemStack(Items.EMERALD));
			}
			else {
				GameRegistry.addShapedRecipe(new ItemStack(set.helmet),"AAA","A A",'A', set.stack);
				GameRegistry.addShapedRecipe(new ItemStack(set.chestplate),"A A","AAA","AAA",'A', set.stack);
				GameRegistry.addShapedRecipe(new ItemStack(set.leggings),"AAA","A A","A A",'A', set.stack);
				GameRegistry.addShapedRecipe(new ItemStack(set.boots),"A A","A A",'A', set.stack);
			}
		}
	}

	private void registerEventListeners()
	{
		MinecraftForge.EVENT_BUS.register(new IncreaseFortuneEvent());
		MinecraftForge.EVENT_BUS.register(new Config());
		MinecraftForge.EVENT_BUS.register(new StopFallDamageEvent());
		MinecraftForge.EVENT_BUS.register(new IgniteTargetEvent());
		MinecraftForge.EVENT_BUS.register(new OpenGuiEvent());
	}
}
