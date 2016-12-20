package twopiradians.blockArmor.common.block;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import twopiradians.blockArmor.common.BlockArmor;

public class ModBlocks 
{
	public static Block movinglightsource;

	public static void preInit()
	{
		movinglightsource = registerBlockWithoutTab(new BlockMovingLightSource(), "movinglightsource");
	}

	public static void registerRenders() 
	{
		registerRender(movinglightsource);
	}

	public static Block registerBlockWithoutTab(final Block block, final String unlocalizedName) 
	{
		block.setUnlocalizedName(unlocalizedName);
		GameRegistry.register(block.setRegistryName(unlocalizedName));
		return block;
	}

	public static void registerRender(Block block)
	{	
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(BlockArmor.MODID + ":" + block.getUnlocalizedName().substring(5), "inventory"));
	}
}
