package twopiradians.blockArmor.common.seteffect;

import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.block.BlockMovingLightSource;
import twopiradians.blockArmor.common.block.ModBlocks;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.utils.BlockUtils;

public class SetEffectIlluminated extends SetEffect { 

	private int lightLevel;

	public SetEffectIlluminated(int lightLevel) {
		super();
		this.lightLevel = Mth.clamp(lightLevel, 1, 15);
		this.color = ChatFormatting.GOLD;
		this.usesButton = true;
	}

	/**Extra objects needed for description*/
	@Override
	public Object[] getDescriptionObjects() {
		return new Object[] { lightLevel };
	}
	
	@Override
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		// enable/disable
		if (ArmorSet.getFirstSetItem(player, this) == stack &&
				!world.isClientSide && BlockArmor.key.isKeyDown(player) && !player.getCooldowns().isOnCooldown(stack.getItem())) {
			boolean deactivated = !stack.getTag().getBoolean("deactivated");
			stack.getTag().putBoolean("deactivated", deactivated);
			player.sendMessage(new TranslatableComponent(ChatFormatting.GRAY+""+ChatFormatting.ITALIC+"Illuminated set effect "
					+ (deactivated ? ChatFormatting.RED+""+ChatFormatting.ITALIC+"disabled" : ChatFormatting.GREEN+""+ChatFormatting.ITALIC+"enabled")), UUID.randomUUID());
			this.setCooldown(player, 10);
		}

		//set block at head level to BlockMovingLightSource
		if (ArmorSet.getFirstSetItem(player, this) == stack &&
				!world.isClientSide && world.isEmptyBlock(player.blockPosition().above()) && 
				world.getBrightness(LightLayer.BLOCK, player.blockPosition().above()) < lightLevel &&
				!stack.getTag().getBoolean("deactivated")) {
			BlockPos pos = player.blockPosition().above();
			BlockState state = ModBlocks.MOVING_LIGHT_SOURCE.defaultBlockState().setValue(BlockMovingLightSource.LIGHT_LEVEL, lightLevel);
			world.setBlockAndUpdate(pos, state);
			world.setBlockEntity(ModBlocks.MOVING_LIGHT_SOURCE.newBlockEntity(pos, state));
		}
	}

	/**Can be overwritten to return a new instance depending on the given block*/
	@Override
	protected SetEffect create(Block block) {
		return new SetEffectIlluminated(BlockUtils.getLightLevel(block));
	}

	/**Write this effect to string for config (variables need to be included)*/
	@Override
	public String writeToString() {
		return this.name+" ("+this.lightLevel+")";
	}

	/**Read an effect from this string in config (takes into account variables in parenthesis)*/
	@Override
	public SetEffect readFromString(String str) throws Exception {
		return new SetEffectIlluminated(Integer.valueOf(str.substring(str.indexOf("(")+1, str.indexOf(")"))));
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