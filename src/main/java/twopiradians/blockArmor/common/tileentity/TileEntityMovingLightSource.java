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

	private static final int INITIAL_DESPAWN_TIMER = 5;

	/**Time until this despawns (so it has time to spawn new light)*/
	private int despawnTimer; 
	private SetEffect effect;

	public TileEntityMovingLightSource() {
		this(15);
	}

	public TileEntityMovingLightSource(int lightLevel) {
		super(type);
		effect = new SetEffectIlluminated(lightLevel);
		despawnTimer = INITIAL_DESPAWN_TIMER;
	}

	@Override
	public void tick() {
		if (!world.isRemote) {
			//check if player has moved away from the tile entity
			PlayerEntity player = world.getClosestPlayer(getPos().getX()+0.5D, getPos().getY()+0.5D, getPos().getZ()+0.5D, 2.0D, false);		
			ItemStack stack = ArmorSet.getFirstSetItem(player, effect);
			if ((player == null || stack == null || (stack.hasTag() && stack.getTag().getBoolean("deactivated"))) &&
					world.getBlockState(getPos()).getBlock() instanceof BlockMovingLightSource) {
				if (--despawnTimer <= 0) 
					world.removeBlock(getPos(), false);
			}
			else
				despawnTimer = INITIAL_DESPAWN_TIMER;
		}
	}

}