package twopiradians.blockArmor.common.tileentity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import twopiradians.blockArmor.common.block.BlockMovingLightSource;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.seteffect.SetEffect;
import twopiradians.blockArmor.common.seteffect.SetEffectIlluminated;

/**Used for armor set effects that produce light*/
public class TileEntityMovingLightSource extends BlockEntity implements EntityBlock {

	public static BlockEntityType<TileEntityMovingLightSource> type;

	private static final int INITIAL_DESPAWN_TIMER = 5;

	/**Time until this despawns (so it has time to spawn new light)*/
	private int despawnTimer; 
	private SetEffect effect;

	public TileEntityMovingLightSource(BlockPos pos, BlockState state) {
		this(state.getValue(BlockMovingLightSource.LIGHT_LEVEL).intValue(), pos, state); 
	}

	public TileEntityMovingLightSource(int lightLevel, BlockPos pos, BlockState state) {
		super(type, pos, state); 
		effect = new SetEffectIlluminated(lightLevel);
		despawnTimer = INITIAL_DESPAWN_TIMER;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return TileEntityMovingLightSource::tick;
	}

	public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T te) {
		if (!level.isClientSide && te instanceof TileEntityMovingLightSource) {
			TileEntityMovingLightSource light = (TileEntityMovingLightSource) te;
			//check if player has moved away from the tile entity
			Player player = level.getNearestPlayer(pos.getX()+0.5D, pos.getY()+0.5D, pos.getZ()+0.5D, 2.0D, false);		
			ItemStack stack = ArmorSet.getFirstSetItem(player, light.effect);
			if ((player == null || stack == null || (stack.hasTag() && stack.getTag().getBoolean("deactivated"))) &&
					level.getBlockState(pos).getBlock() instanceof BlockMovingLightSource) {
				if (--light.despawnTimer <= 0) 
					level.removeBlock(pos, false);
			}
			else
				light.despawnTimer = INITIAL_DESPAWN_TIMER;
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityMovingLightSource(pos, state);
	}

}