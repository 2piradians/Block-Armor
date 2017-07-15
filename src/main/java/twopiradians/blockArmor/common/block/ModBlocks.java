package twopiradians.blockArmor.common.block;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import twopiradians.blockArmor.common.BlockArmor;

public class ModBlocks 
{
	public static final Block MOVING_LIGHT_SOURCE = new BlockMovingLightSource();

	public static ArrayList<Block> allBlocks = new ArrayList<Block>();
	
	@Mod.EventBusSubscriber
	public static class RegistrationHandler {

		@SubscribeEvent
		public static void registerBlocks(final RegistryEvent.Register<Block> event) {
			register(event.getRegistry(), MOVING_LIGHT_SOURCE, "movingLightSource");
		}

		private static void register(IForgeRegistry<Block> registry, Block block, String blockName) {
			allBlocks.add(block);
			block.setRegistryName(BlockArmor.MODID, blockName);
			block.setUnlocalizedName(block.getRegistryName().toString());
			registry.register(block);
		}

	}
	
	public static void registerRenders() {
		for (Block block : allBlocks)
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register
			(Item.getItemFromBlock(block), 0, new ModelResourceLocation(BlockArmor.MODID + ":" + 
			block.getUnlocalizedName().substring(5), "inventory"));
	}

}