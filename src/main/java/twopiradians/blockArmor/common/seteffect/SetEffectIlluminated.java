package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.block.BlockMovingLightSource;
import twopiradians.blockArmor.common.block.ModBlocks;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.utils.BlockUtils;

@SuppressWarnings("deprecation")
public class SetEffectIlluminated extends SetEffect {

	private int lightLevel;

	public SetEffectIlluminated(int lightLevel) {
		this.lightLevel = MathHelper.clamp(lightLevel, 1, 15);
		this.color = TextFormatting.GOLD;
		this.description = "Produces light level "+this.lightLevel;
		this.usesButton = true;
	}

	@Override
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack &&
				!world.isRemote && BlockArmor.key.isKeyDown(player) && !player.getCooldownTracker().hasCooldown(stack.getItem())) {
			boolean deactivated = !stack.getTag().getBoolean("deactivated");
			stack.getTag().putBoolean("deactivated", deactivated);
			player.sendMessage(new TranslationTextComponent(TextFormatting.GRAY+"[Block Armor] "+TextFormatting.ITALIC+"Illuminated set effect "
					+ (deactivated ? TextFormatting.RED+""+TextFormatting.ITALIC+"disabled." : TextFormatting.GREEN+""+TextFormatting.ITALIC+"enabled.")), null);
			this.setCooldown(player, 10);
		}

		//set block at head level to BlockMovingLightSource
		if (ArmorSet.getFirstSetItem(player, this) == stack &&
				!world.isRemote && world.isAirBlock(player.getPosition().up()) && 
				world.getLightFor(LightType.BLOCK, player.getPosition().up()) < lightLevel &&
				!stack.getTag().getBoolean("deactivated")) 
			world.setBlockState(player.getPosition().up(), 
					ModBlocks.MOVING_LIGHT_SOURCE.getDefaultState().with(BlockMovingLightSource.LIGHT_LEVEL, lightLevel));
	}

	/**Can be overwritten to return a new instance depending on the given block*/
	@Override
	protected SetEffect create(Block block) {
		return new SetEffectIlluminated(BlockUtils.getLightLevel(block));
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		try {
			if (BlockUtils.getLightLevel(block) > 0)
				return true;
		} catch (Exception e) {}
		return false;
	}
}