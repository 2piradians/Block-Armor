package twopiradians.blockArmor.common.block;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import twopiradians.blockArmor.common.BlockArmor;

public class ModBlocks 
{
	public static final BlockMovingLightSource MOVING_LIGHT_SOURCE = new BlockMovingLightSource();

	public static ArrayList<Block> allBlocks = new ArrayList<Block>();

	@Mod.EventBusSubscriber(bus = Bus.MOD)
	public static class RegistrationHandler {

		@SubscribeEvent
		public static void registerBlocks(final RegistryEvent.Register<Block> event) {
			register(event.getRegistry(), MOVING_LIGHT_SOURCE, "moving_light_source");
		}

		private static void register(IForgeRegistry<Block> registry, Block block, String blockName) {
			allBlocks.add(block);
			block.setRegistryName(BlockArmor.MODID, blockName);
			registry.register(block);
		}

	}

	@SuppressWarnings("deprecation")
	public static void registerRenders() {
		for (Block block : allBlocks)
			Minecraft.getInstance().getItemRenderer().getItemModelMesher().register
			(Item.getItemFromBlock(block), new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}

}