package twopiradians.blockArmor.common.block;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import twopiradians.blockArmor.common.BlockArmor;

public class ModBlocks 
{
	public static Block movingLightSource;

	public static void preInit() {
		movingLightSource = registerBlock(new BlockMovingLightSource(), "movingLightSource");
	}

	public static void registerRenders() {
		registerRender(movingLightSource);
	}

	private static Block registerBlock(final Block block, final String unlocalizedName) {
		block.setUnlocalizedName(unlocalizedName);
		ForgeRegistries.BLOCKS.register(block.setRegistryName(unlocalizedName));
		return block;
	}

	private static void registerRender(Block block) {	
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(BlockArmor.MODID + ":" + block.getUnlocalizedName().substring(5), "inventory"));
	}
}