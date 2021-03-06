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
		this.description = "Creates an explosion around you";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldownTracker().hasCooldown(stack.getItem())) 
			SetEffectExplosive.tryExplode(this, world, player);
	}

	/**Create explosion around the player*/
	protected static void tryExplode(SetEffect effect, World world, PlayerEntity player) {
		if (!world.isRemote && player.isAllowEdit()) {
			effect.setCooldown(player, 20);
			world.createExplosion(player, player.getPosX(), player.getPosY()+0.5d, player.getPosZ(), 6f, false, Explosion.Mode.BREAK);
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