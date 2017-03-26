package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectExplosive extends SetEffect {

	protected SetEffectExplosive() {
		this.color = TextFormatting.RED;
		this.description = "Explodes and uses some durability";
		this.usesButton = true;
	}
	
	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldownTracker().hasCooldown(stack.getItem()) && player.isAllowEdit()) {
			this.setCooldown(player, 20);
			world.newExplosion(player, player.posX, player.posY+0.5d, player.posZ, 6f, false, true);
			this.damageArmor(player, 10, false);
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {	
		if (SetEffect.registryNameContains(block, meta, new String[] {"tnt", "explo"}))
			return true;		
		return false;
	}
}