package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.BlockArmor;
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
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player)) {
			if (block == Blocks.REPEATING_COMMAND_BLOCK)
				if (world.getWorldTime() < 21)
					world.setWorldTime(23999 + world.getWorldTime() - 21);
				else
					world.setWorldTime(world.getWorldTime() - 21);
			else if (block == Blocks.CHAIN_COMMAND_BLOCK && world.getGameRules().getBoolean("doDaylightCycle"))
				world.setWorldTime(world.getWorldTime() - 1);
			else if (block == Blocks.COMMAND_BLOCK)
				world.setWorldTime(world.getWorldTime() + 19);
		}
	}

	/**Can be overwritten to return a new instance depending on the given block*/
	@Override
	protected SetEffect create(Block block) {
		return new SetEffectTime_Control(block);
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (block == Blocks.REPEATING_COMMAND_BLOCK || block == Blocks.CHAIN_COMMAND_BLOCK ||
				block == Blocks.COMMAND_BLOCK)
			return true;
		return false;
	}
}