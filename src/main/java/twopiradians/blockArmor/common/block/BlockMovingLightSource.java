package twopiradians.blockArmor.common.block;


import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.tileentity.TileEntityMovingLightSource;

/**Used for armor sets that produce light.*/
public class BlockMovingLightSource extends Block implements ITileEntityProvider
{
	protected static final AxisAlignedBB LIGHT = new AxisAlignedBB(0.5D, 0.5D, 0.5D, 0.5D, 0.5D, 0.5D);

	public BlockMovingLightSource()
	{
		super(Material.AIR);
		setUnlocalizedName("movinglightsource");
		setLightLevel(0.6F);
	}

	//deprecated
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.INVISIBLE;
	}

	//deprecated
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{     
		return LIGHT;
	}

	//deprecated
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
	{
		return NULL_AABB;
	}

	//deprecated
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	//deprecated
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileEntityMovingLightSource();
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) 
	{
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	//deprecated
	@SuppressWarnings("deprecation")
	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
	{
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}
}
