package twopiradians.blockArmor.common.block;


import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import twopiradians.blockArmor.client.ClientProxy;
import twopiradians.blockArmor.common.tileentity.TileEntityMovingLightSource;

/**Used for armor sets that produce light*/
public class BlockMovingLightSource extends BaseEntityBlock {

	public static final Property<Integer> LIGHT_LEVEL = IntegerProperty.create("light_level", 1, 15);

	public BlockMovingLightSource() {
		super(BlockBehaviour.Properties.of(Material.AIR).lightLevel((state) -> state.getValue(LIGHT_LEVEL).intValue()));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(LIGHT_LEVEL);
	}

	@Override
	public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
		return Shapes.empty();
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
		return true;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		world.getBlockEntity(pos);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityMovingLightSource(state.getValue(LIGHT_LEVEL).intValue(), pos, state);
	}

	@Override
	public StateDefinition<Block, BlockState> getStateDefinition() {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {ClientProxy.mapUnbakedModels();}); // hacky way to map models in ModelBakery#processLoading
		return super.getStateDefinition();
	}

	@Override
	public boolean isAir(BlockState state) {
		return false;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide ? null : createTickerHelper(type, TileEntityMovingLightSource.type, TileEntityMovingLightSource::tick);
	}

}