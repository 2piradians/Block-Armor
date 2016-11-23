package twopiradians.blockArmor.common.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.block.ModBlocks;
import twopiradians.blockArmor.common.item.ItemModArmor;
import twopiradians.blockArmor.common.item.ModItems;

/**Used for armor set effects that produce light.*/
public class TileEntityMovingLightSource extends TileEntity implements ITickable
{
	public EntityPlayer thePlayer;

	public TileEntityMovingLightSource()
	{
		// after constructing the tile entity instance, remember to call 
		// the setPlayer() method.
	}

	@Override
	public void update() 
	{
		// check if player has moved away from the tile entity
		EntityPlayer thePlayer = worldObj.getClosestPlayer(getPos().getX()+0.5D, getPos().getY()+0.5D, getPos().getZ()+0.5D, 2.0D, false);
		if (thePlayer == null || !thePlayer.isSneaking() 
				|| (ItemModArmor.wearingFullSet(thePlayer, ModItems.netherrack) && thePlayer.isInWater()))
			if (worldObj.getBlockState(getPos()).getBlock() == ModBlocks.movinglightsource)
				worldObj.setBlockToAir(getPos());
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		return (oldState.getBlock() != newSate.getBlock());
	}

	public void setPlayer(EntityPlayer parPlayer)
	{
		thePlayer = parPlayer;
	}
}
