package twopiradians.blockArmor.common.tileentity;

import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.block.ModBlocks;

public class ModTileEntities{
	
	@Mod.EventBusSubscriber(modid = BlockArmor.MODID, bus = Bus.MOD)
	public static class RegistrationHandler {

		@SubscribeEvent
		public static void registerTE(final RegistryEvent.Register<BlockEntityType<?>> event) {
			TileEntityMovingLightSource.type = BlockEntityType.Builder.of(TileEntityMovingLightSource::new, ModBlocks.MOVING_LIGHT_SOURCE).build(Util.fetchChoiceType(References.BLOCK_ENTITY, "light_tile_entity"));
			TileEntityMovingLightSource.type.setRegistryName(BlockArmor.MODID, "light_tile_entity");
			event.getRegistry().register(TileEntityMovingLightSource.type);
		}
	}

}