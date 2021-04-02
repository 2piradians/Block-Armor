package twopiradians.blockArmor.common.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import twopiradians.blockArmor.common.block.BlockMovingLightSource;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.seteffect.SetEffect;
import twopiradians.blockArmor.common.seteffect.SetEffectIlluminated;

/**Used for armor set effects that produce light*/
public class TileEntityMovingLightSource extends TileEntity implements ITickableTileEntity {

	public static TileEntityType<TileEntityMovingLightSource> type;

	private SetEffect effect;

	public TileEntityMovingLightSource() {
		this(0);
	}

	public TileEntityMovingLightSource(int lightLevel) {
		super(type);
		effect = new SetEffectIlluminated(lightLevel);
	}

	@Override
	public void tick() {
		//check if player has moved away from the tile entity
		PlayerEntity player = world.getClosestPlayer(getPos().getX()+0.5D, getPos().getY()+0.5D, getPos().getZ()+0.5D, 2.0D, false);		
		ItemStack stack = ArmorSet.getFirstSetItem(player, effect);
		if ((player == null || stack == null || (stack.hasTag() && stack.getTag().getBoolean("deactivated"))) &&
				world.getBlockState(getPos()).getBlock() instanceof BlockMovingLightSource)
			world.removeBlock(getPos(), false);
	}

}