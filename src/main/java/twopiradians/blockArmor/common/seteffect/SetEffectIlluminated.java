package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.block.BlockMovingLightSource;
import twopiradians.blockArmor.common.block.ModBlocks;

@SuppressWarnings("deprecation")
public class SetEffectIlluminated extends SetEffect {

	private int lightLevel;

	protected SetEffectIlluminated(int lightLevel) {
		this.lightLevel = Math.min(lightLevel, 15);
		this.color = TextFormatting.GOLD;
		this.description = "Produces light level "+this.lightLevel;
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (world.isRemote && world.getLightFor(EnumSkyBlock.BLOCK, player.getPosition().up()) < lightLevel) {
			world.setBlockState(player.getPosition().up(), 
					ModBlocks.movingLightSource.getDefaultState().withProperty(BlockMovingLightSource.LIGHT_LEVEL, lightLevel));
			System.out.println("placed");
		}
	}

	/**Can be overwritten to return a new instance depending on the given block*/
	@Override
	protected SetEffect create(Block block) {
		return new SetEffectIlluminated(block.getDefaultState().getLightValue());
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		try {
			int lightLevel = block.getDefaultState().getLightValue();
			if (lightLevel > 0)
				return true;
		} catch (Exception e) {}
		return false;
	}
}