package twopiradians.blockArmor.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import twopiradians.blockArmor.common.block.BlockMovingLightSource;
import twopiradians.blockArmor.common.item.ArmorSet;

/**Used for armor set effects that produce light*/
public class TileEntityMovingLightSource extends TileEntity implements ITickable
{
	@Override
	public void update() {
		//check if player has moved away from the tile entity
		EntityPlayer player = world.getClosestPlayer(getPos().getX()+0.5D, getPos().getY()+0.5D, getPos().getZ()+0.5D, 2.0D, false);
		if ((player == null || !ArmorSet.isWearingFullSet(player, null) || (player.getItemStackFromSlot(EntityEquipmentSlot.FEET).hasTagCompound() &&
				player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getTagCompound().getBoolean("deactivated"))) &&
				world.getBlockState(getPos()).getBlock() instanceof BlockMovingLightSource)
			world.setBlockToAir(getPos());
	}
}