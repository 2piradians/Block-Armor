package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectFeeder extends SetEffect {

	protected SetEffectFeeder() {
		super();
		this.color = ChatFormatting.RED;
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!player.getCooldowns().isOnCooldown(stack.getItem()) && 
				!world.isClientSide && ArmorSet.getFirstSetItem(player, this) == stack)
			if (player.canEat(false)) {
				int foodLevel = player.getFoodData().getFoodLevel();
				int foodToFeed = Math.min(20-foodLevel, 4);
				player.getFoodData().eat(foodToFeed,0.1f);

				if (foodLevel < 16)
					for (int i=0; i<2; ++i)
						world.playSound((Player)null, player.blockPosition(), SoundEvents.GENERIC_EAT, 
								SoundSource.PLAYERS, 0.15F, world.random.nextFloat()*0.2f + 1.0f);
				else
					world.playSound((Player)null, player.blockPosition(), SoundEvents.PLAYER_BURP, 
							SoundSource.PLAYERS, 0.15F, world.random.nextFloat()*0.2f + 1.0f);
				
				this.setCooldown(player, 80);
				this.damageArmor(player, foodToFeed, true);
			}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {	
		if (SetEffect.registryNameContains(block, new String[] {"pumpkin", "melon", "food", "berry", "feed", "pork"}))
			return true;		
		return false;
	}	
}