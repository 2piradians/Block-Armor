package twopiradians.blockArmor.common.tileentity;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import twopiradians.blockArmor.common.block.BlockMovingLightSource;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.seteffect.SetEffectIlluminated;

/**Used for armor set effects that produce light*/
public class TileEntityMovingLightSource extends TileEntity implements ITickable
{
	@Override
	public void update() {
		//check if player has moved away from the tile entity
		EntityPlayer player = world.getClosestPlayer(getPos().getX()+0.5D, getPos().getY()+0.5D, getPos().getZ()+0.5D, 2.0D, false);
		ArrayList<ArmorSet> sets = ArmorSet.getActiveSets(player);
		for (ArmorSet set : sets)
			if ((player == null || !set.setEffects.contains(new SetEffectIlluminated(0)) || (player.getItemStackFromSlot(EntityEquipmentSlot.FEET).hasTagCompound() &&
					player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getTagCompound().getBoolean("deactivated"))) &&
					world.getBlockState(getPos()).getBlock() instanceof BlockMovingLightSource)
				world.setBlockToAir(getPos());
	}
}