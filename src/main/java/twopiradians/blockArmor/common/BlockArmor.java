package twopiradians.blockArmor.common;

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
import twopiradians.blockArmor.common.events.ConfigChangeEvent;
import twopiradians.blockArmor.common.events.IgniteTargetEvent;
import twopiradians.blockArmor.common.events.IncreaseFortuneEvent;
import twopiradians.blockArmor.common.events.StopFallDamageEvent;
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
	/**Should armor display be opened on chat event? ONLY WORKS IN SP*/
	public static final boolean DISPLAY_ARMOR_GUI = false;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ModItems.init();
		ModBlocks.init();
		ModTileEntities.init();
		Config.init(event.getSuggestedConfigurationFile());
	}
	
	@EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.registerRenders();
    	registerEventListeners();
    	
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.stone_helmet),"AAA","A A",'A', new ItemStack(Blocks.STONE,1,0));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.stone_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.STONE,1,0));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.stone_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.STONE,1,0));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.stone_boots),"A A","A A",'A', new ItemStack(Blocks.STONE,1,0));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.granite_helmet),"AAA","A A",'A', new ItemStack(Blocks.STONE,1,1));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.granite_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.STONE,1,1));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.granite_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.STONE,1,1));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.granite_boots),"A A","A A",'A', new ItemStack(Blocks.STONE,1,1));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.smoothgranite_helmet),"AAA","A A",'A', new ItemStack(Blocks.STONE,1,2));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.smoothgranite_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.STONE,1,2));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.smoothgranite_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.STONE,1,2));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.smoothgranite_boots),"A A","A A",'A', new ItemStack(Blocks.STONE,1,2));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.diorite_helmet),"AAA","A A",'A', new ItemStack(Blocks.STONE,1,3));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.diorite_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.STONE,1,3));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.diorite_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.STONE,1,3));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.diorite_boots),"A A","A A",'A', new ItemStack(Blocks.STONE,1,3));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.smoothdiorite_helmet),"AAA","A A",'A', new ItemStack(Blocks.STONE,1,4));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.smoothdiorite_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.STONE,1,4));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.smoothdiorite_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.STONE,1,4));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.smoothdiorite_boots),"A A","A A",'A', new ItemStack(Blocks.STONE,1,4));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.andesite_helmet),"AAA","A A",'A', new ItemStack(Blocks.STONE,1,5));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.andesite_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.STONE,1,5));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.andesite_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.STONE,1,5));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.andesite_boots),"A A","A A",'A', new ItemStack(Blocks.STONE,1,5));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.smoothandesite_helmet),"AAA","A A",'A', new ItemStack(Blocks.STONE,1,6));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.smoothandesite_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.STONE,1,6));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.smoothandesite_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.STONE,1,6));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.smoothandesite_boots),"A A","A A",'A', new ItemStack(Blocks.STONE,1,6));
				
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.dirt_helmet),"AAA","A A",'A', Blocks.DIRT);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.dirt_chestplate),"A A","AAA","AAA",'A', Blocks.DIRT);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.dirt_leggings),"AAA","A A","A A",'A', Blocks.DIRT);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.dirt_boots),"A A","A A",'A', Blocks.DIRT);
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.cobble_helmet),"AAA","A A",'A', Blocks.COBBLESTONE);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.cobble_chestplate),"A A","AAA","AAA",'A', Blocks.COBBLESTONE);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.cobble_leggings),"AAA","A A","A A",'A', Blocks.COBBLESTONE);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.cobble_boots),"A A","A A",'A', Blocks.COBBLESTONE);

		GameRegistry.addShapedRecipe(new ItemStack(ModItems.oakwoodplanks_helmet),"AAA","A A",'A', new ItemStack(Blocks.PLANKS,1,0));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.oakwoodplanks_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.PLANKS,1,0));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.oakwoodplanks_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.PLANKS,1,0));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.oakwoodplanks_boots),"A A","A A",'A', new ItemStack(Blocks.PLANKS,1,0));
	
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.sprucewoodplanks_helmet),"AAA","A A",'A', new ItemStack(Blocks.PLANKS,1,1));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.sprucewoodplanks_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.PLANKS,1,1));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.sprucewoodplanks_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.PLANKS,1,1));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.sprucewoodplanks_boots),"A A","A A",'A', new ItemStack(Blocks.PLANKS,1,1));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.birchwoodplanks_helmet),"AAA","A A",'A', new ItemStack(Blocks.PLANKS,1,2));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.birchwoodplanks_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.PLANKS,1,2));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.birchwoodplanks_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.PLANKS,1,2));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.birchwoodplanks_boots),"A A","A A",'A', new ItemStack(Blocks.PLANKS,1,2));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.junglewoodplanks_helmet),"AAA","A A",'A', new ItemStack(Blocks.PLANKS,1,3));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.junglewoodplanks_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.PLANKS,1,3));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.junglewoodplanks_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.PLANKS,1,3));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.junglewoodplanks_boots),"A A","A A",'A', new ItemStack(Blocks.PLANKS,1,3));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.acaciawoodplanks_helmet),"AAA","A A",'A', new ItemStack(Blocks.PLANKS,1,4));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.acaciawoodplanks_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.PLANKS,1,4));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.acaciawoodplanks_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.PLANKS,1,4));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.acaciawoodplanks_boots),"A A","A A",'A', new ItemStack(Blocks.PLANKS,1,4));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.darkoakwoodplanks_helmet),"AAA","A A",'A', new ItemStack(Blocks.PLANKS,1,5));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.darkoakwoodplanks_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.PLANKS,1,5));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.darkoakwoodplanks_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.PLANKS,1,5));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.darkoakwoodplanks_boots),"A A","A A",'A', new ItemStack(Blocks.PLANKS,1,5));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.bedrock_helmet),"AAA","A A",'A', Blocks.BEDROCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.bedrock_chestplate),"A A","AAA","AAA",'A', Blocks.BEDROCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.bedrock_leggings),"AAA","A A","A A",'A', Blocks.BEDROCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.bedrock_boots),"A A","A A",'A', Blocks.BEDROCK);

		GameRegistry.addShapedRecipe(new ItemStack(ModItems.oakwood_helmet),"AAA","A A",'A', new ItemStack(Blocks.LOG,1,0));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.oakwood_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.LOG,1,0));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.oakwood_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.LOG,1,0));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.oakwood_boots),"A A","A A",'A', new ItemStack(Blocks.LOG,1,0));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.sprucewood_helmet),"AAA","A A",'A', new ItemStack(Blocks.LOG,1,1));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.sprucewood_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.LOG,1,1));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.sprucewood_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.LOG,1,1));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.sprucewood_boots),"A A","A A",'A', new ItemStack(Blocks.LOG,1,1));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.birchwood_helmet),"AAA","A A",'A', new ItemStack(Blocks.LOG,1,2));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.birchwood_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.LOG,1,2));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.birchwood_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.LOG,1,2));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.birchwood_boots),"A A","A A",'A', new ItemStack(Blocks.LOG,1,2));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.junglewood_helmet),"AAA","A A",'A', new ItemStack(Blocks.LOG,1,3));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.junglewood_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.LOG,1,3));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.junglewood_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.LOG,1,3));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.junglewood_boots),"A A","A A",'A', new ItemStack(Blocks.LOG,1,3));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.lapis_helmet),"AAA","A A",'A', Blocks.LAPIS_BLOCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.lapis_chestplate),"A A","AAA","AAA",'A', Blocks.LAPIS_BLOCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.lapis_leggings),"AAA","A A","A A",'A', Blocks.LAPIS_BLOCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.lapis_boots),"A A","A A",'A', Blocks.LAPIS_BLOCK);
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.brick_helmet),"AAA","A A",'A', Blocks.BRICK_BLOCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.brick_chestplate),"A A","AAA","AAA",'A', Blocks.BRICK_BLOCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.brick_leggings),"AAA","A A","A A",'A', Blocks.BRICK_BLOCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.brick_boots),"A A","A A",'A', Blocks.BRICK_BLOCK);
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.obsidian_helmet),"AAA","A A",'A', Blocks.OBSIDIAN);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.obsidian_chestplate),"A A","AAA","AAA",'A', Blocks.OBSIDIAN);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.obsidian_leggings),"AAA","A A","A A",'A', Blocks.OBSIDIAN);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.obsidian_boots),"A A","A A",'A', Blocks.OBSIDIAN);
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.snow_helmet),"AAA","A A",'A', Blocks.SNOW);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.snow_chestplate),"A A","AAA","AAA",'A', Blocks.SNOW);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.snow_leggings),"AAA","A A","A A",'A', Blocks.SNOW);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.snow_boots),"A A","A A",'A', Blocks.SNOW);
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.netherrack_helmet),"AAA","A A",'A', Blocks.NETHERRACK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.netherrack_chestplate),"A A","AAA","AAA",'A', Blocks.NETHERRACK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.netherrack_leggings),"AAA","A A","A A",'A', Blocks.NETHERRACK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.netherrack_boots),"A A","A A",'A', Blocks.NETHERRACK);
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.endstone_helmet),"AAA","A A",'A', Blocks.END_STONE);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.endstone_chestplate),"A A","AAA","AAA",'A', Blocks.END_STONE);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.endstone_leggings),"AAA","A A","A A",'A', Blocks.END_STONE);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.endstone_boots),"A A","A A",'A', Blocks.END_STONE);
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.emerald_helmet),"AAA","A A",'A', Items.EMERALD);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.emerald_chestplate),"A A","AAA","AAA",'A', Items.EMERALD);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.emerald_leggings),"AAA","A A","A A",'A', Items.EMERALD);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.emerald_boots),"A A","A A",'A', Items.EMERALD);
			
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.quartz_helmet),"AAA","A A",'A', Blocks.QUARTZ_BLOCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.quartz_chestplate),"A A","AAA","AAA",'A', Blocks.QUARTZ_BLOCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.quartz_leggings),"AAA","A A","A A",'A', Blocks.QUARTZ_BLOCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.quartz_boots),"A A","A A",'A', Blocks.QUARTZ_BLOCK);
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.acaciawood_helmet),"AAA","A A",'A', new ItemStack(Blocks.LOG2,1,0));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.acaciawood_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.LOG2,1,0));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.acaciawood_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.LOG2,1,0));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.acaciawood_boots),"A A","A A",'A', new ItemStack(Blocks.LOG2,1,0));
	
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.darkoakwood_helmet),"AAA","A A",'A', new ItemStack(Blocks.LOG2,1,1));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.darkoakwood_chestplate),"A A","AAA","AAA",'A', new ItemStack(Blocks.LOG2,1,1));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.darkoakwood_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.LOG2,1,1));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.darkoakwood_boots),"A A","A A",'A', new ItemStack(Blocks.LOG2,1,1));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.slime_helmet),"AAA","A A",'A', Blocks.SLIME_BLOCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.slime_chestplate),"A A","AAA","AAA",'A', Blocks.SLIME_BLOCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.slime_leggings),"AAA","A A","A A",'A', Blocks.SLIME_BLOCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.slime_boots),"A A","A A",'A', Blocks.SLIME_BLOCK);
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.darkprismarine_helmet),"AAA","A A",'A', new ItemStack(Blocks.PRISMARINE,1,2));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.darkprismarine_chestplate),"A A","AAA","AAA",'A',new ItemStack(Blocks.PRISMARINE,1,2));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.darkprismarine_leggings),"AAA","A A","A A",'A', new ItemStack(Blocks.PRISMARINE,1,2));
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.darkprismarine_boots),"A A","A A",'A', new ItemStack(Blocks.PRISMARINE,1,2));
		
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.redstone_helmet),"AAA","A A",'A', Blocks.REDSTONE_BLOCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.redstone_chestplate),"A A","AAA","AAA",'A', Blocks.REDSTONE_BLOCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.redstone_leggings),"AAA","A A","A A",'A', Blocks.REDSTONE_BLOCK);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.redstone_boots),"A A","A A",'A', Blocks.REDSTONE_BLOCK);
	
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.sugarcane_helmet),"AAA","A A",'A', Items.REEDS);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.sugarcane_chestplate),"A A","AAA","AAA",'A', Items.REEDS);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.sugarcane_leggings),"AAA","A A","A A",'A', Items.REEDS);
		GameRegistry.addShapedRecipe(new ItemStack(ModItems.sugarcane_boots),"A A","A A",'A', Items.REEDS);
    }
	
	@EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
		
    }
	
	public void registerEventListeners()
	{
		MinecraftForge.EVENT_BUS.register(new IncreaseFortuneEvent());
		MinecraftForge.EVENT_BUS.register(new ConfigChangeEvent());
		MinecraftForge.EVENT_BUS.register(new StopFallDamageEvent());
		MinecraftForge.EVENT_BUS.register(new IgniteTargetEvent());
		MinecraftForge.EVENT_BUS.register(new OpenGuiEvent());
	}
}
