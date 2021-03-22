package twopiradians.blockArmor.common.block;


import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import twopiradians.blockArmor.common.tileentity.TileEntityMovingLightSource;

/**Used for armor sets that produce light*/
public class BlockMovingLightSource extends Block
{
	public static final Property<Integer> LIGHT_LEVEL = IntegerProperty.create("light_level", 1, 15);

	public BlockMovingLightSource() {
		super(AbstractBlock.Properties.create(Material.AIR));
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos)
	{
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

}