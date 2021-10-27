package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectExplosive extends SetEffect {

	protected SetEffectExplosive() {
		super();
		this.color = ChatFormatting.RED;
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isClientSide && ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldowns().isOnCooldown(stack.getItem())) 
			SetEffectExplosive.tryExplode(this, world, player);
	}

	/**Create explosion around the player*/
	protected static void tryExplode(SetEffect effect, Level world, Player player) {
		if (!world.isClientSide && player.mayBuild()) {
			effect.setCooldown(player, 20);
			world.explode(player, player.getX(), player.getY()+0.5d, player.getZ(), 6f, false, Explosion.BlockInteraction.BREAK);
			effect.damageArmor(player, 10, true);
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {	
		if (SetEffect.registryNameContains(block, new String[] {"tnt", "explo"}))
			return true;		
		return false;
	}
}