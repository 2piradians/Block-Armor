package twopiradians.blockArmor.common.tileentity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.block.ModBlocks;

public class ModTileEntities
{
	@Mod.EventBusSubscriber
	public static class RegistrationHandler {

		@SubscribeEvent
		public static void registerTE(RegistryEvent.Register<TileEntityType<?>> event) {
			TileEntityMovingLightSource.type = TileEntityType.Builder.create(TileEntityMovingLightSource::new, ModBlocks.MOVING_LIGHT_SOURCE).build(null);
			TileEntityMovingLightSource.type.setRegistryName(BlockArmor.MODID, "light_tile_entity");
			event.getRegistry().register(TileEntityMovingLightSource.type);
		}
	}

}
