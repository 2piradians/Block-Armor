package twopiradians.blockArmor.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import twopiradians.blockArmor.common.block.BlockMovingLightSource;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.seteffect.SetEffect;
import twopiradians.blockArmor.common.seteffect.SetEffectIlluminated;

/**Used for armor set effects that produce light*/
public class TileEntityMovingLightSource extends TileEntity implements ITickable
{
	private SetEffect effect;
	
	public TileEntityMovingLightSource() {}
	
	public TileEntityMovingLightSource(int meta) {
		effect = new SetEffectIlluminated(meta);
	}

	@Override
	public void update() {
		//check if player has moved away from the tile entity
		EntityPlayer player = world.getClosestPlayer(getPos().getX()+0.5D, getPos().getY()+0.5D, getPos().getZ()+0.5D, 2.0D, false);		
		ItemStack stack = ArmorSet.getFirstSetItem(player, effect);
		if ((player == null || stack == null || (stack.hasTagCompound() && stack.getTagCompound().getBoolean("deactivated"))) &&
				world.getBlockState(getPos()).getBlock() instanceof BlockMovingLightSource)
			world.setBlockToAir(getPos());
	}
}