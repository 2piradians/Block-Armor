package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import twopiradians.blockArmor.client.ClientProxy;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.CommonProxy;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectTime_Control extends SetEffect {

	private Block block;

	protected SetEffectTime_Control(Block block) {
		this.block = block;
		this.color = TextFormatting.LIGHT_PURPLE;
		if (block == Blocks.REPEATING_COMMAND_BLOCK)
			this.description = "Rewinds time";
		else if (block == Blocks.CHAIN_COMMAND_BLOCK)
			this.description = "Stops Time";
		else if (block == Blocks.COMMAND_BLOCK)
			this.description = "Accelerates time";
		else
			this.description = "";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player)) {
			if (block == Blocks.REPEATING_COMMAND_BLOCK)
				if (world.getWorldInfo().getDayTime()< 21)
					setWorldTime(world, 23999 + world.getDayTime() - 21);
				else
					setWorldTime(world, world.getDayTime() - 21);
			else if (block == Blocks.CHAIN_COMMAND_BLOCK && world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE))
				setWorldTime(world, world.getDayTime() - 1);
			else if (block == Blocks.COMMAND_BLOCK)
				setWorldTime(world, world.getDayTime() + 19);
		}
	}
	
	private void setWorldTime(World world, long time) {
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> {ClientProxy.setWorldTime(world, time);});
		DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {CommonProxy.setWorldTime(world, time);});
	}

	/**Can be overwritten to return a new instance depending on the given block*/
	@Override
	protected SetEffect create(Block block) {
		return new SetEffectTime_Control(block);
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (block == Blocks.REPEATING_COMMAND_BLOCK || block == Blocks.CHAIN_COMMAND_BLOCK ||
				block == Blocks.COMMAND_BLOCK)
			return true;
		return false;
	}
}