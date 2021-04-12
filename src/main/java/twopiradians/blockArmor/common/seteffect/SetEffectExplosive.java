package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectExplosive extends SetEffect {

	protected SetEffectExplosive() {
		super();
		this.color = TextFormatting.RED;
		this.description = "Explodes and uses some durability";
		this.usesButton = true;
	}
	
	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldownTracker().hasCooldown(stack.getItem()) && player.isAllowEdit()) {
			this.setCooldown(player, 20);
			world.createExplosion(player, player.getPosX(), player.getPosY()+0.5d, player.getPosZ(), 6f, false, Explosion.Mode.BREAK);
			this.damageArmor(player, 10, true);
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