package twopiradians.blockArmor.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import twopiradians.blockArmor.common.block.ModBlocks;
import twopiradians.blockArmor.common.item.ArmorSet;

/**Used for armor set effects that produce light.*/
public class TileEntityMovingLightSource extends TileEntity implements ITickable
{
	@Override
	public void update() 
	{
		// check if player has moved away from the tile entity
		EntityPlayer thePlayer = worldObj.getClosestPlayer(getPos().getX()+0.5D, getPos().getY()+0.5D, getPos().getZ()+0.5D, 2.0D, false);
		if (thePlayer == null || !thePlayer.isSneaking() 
				|| (ArmorSet.isWearingFullSet(thePlayer, ArmorSet.getSet(Blocks.NETHERRACK, 0)) && thePlayer.isInWater()))
			if (worldObj.getBlockState(getPos()).getBlock() == ModBlocks.movinglightsource)
				worldObj.setBlockToAir(getPos());
	}
}
