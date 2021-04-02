package twopiradians.blockArmor.common.tileentity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
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
		public static void registerTE(final RegistryEvent.Register<TileEntityType<?>> event) {
			TileEntityMovingLightSource.type = TileEntityType.Builder.create(TileEntityMovingLightSource::new, ModBlocks.MOVING_LIGHT_SOURCE).build(Util.attemptDataFix(TypeReferences.BLOCK_ENTITY, "light_tile_entity"));
			TileEntityMovingLightSource.type.setRegistryName(BlockArmor.MODID, "light_tile_entity");
			event.getRegistry().register(TileEntityMovingLightSource.type);
		}
	}

}