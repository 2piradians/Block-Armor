package twopiradians.blockArmor.common.block;


import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import twopiradians.blockArmor.client.ClientProxy;
import twopiradians.blockArmor.common.tileentity.TileEntityMovingLightSource;

/**Used for armor sets that produce light*/
@SuppressWarnings("deprecation")
public class BlockMovingLightSource extends Block implements ITileEntityProvider {

	public static final Property<Integer> LIGHT_LEVEL = IntegerProperty.create("light_level", 1, 15);

	public BlockMovingLightSource() {
		super(AbstractBlock.Properties.create(Material.AIR));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(LIGHT_LEVEL);
	}

	@Override
	public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
		return VoxelShapes.empty();
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return state.get(LIGHT_LEVEL);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityMovingLightSource(state.get(LIGHT_LEVEL).intValue());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		world.getTileEntity(pos);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityMovingLightSource(0);
	}

	@Override
	public StateContainer<Block, BlockState> getStateContainer() {
		ClientProxy.mapUnbakedModels(); // hacky way to map models in ModelBakery#processLoading
		return super.getStateContainer();
	}

}