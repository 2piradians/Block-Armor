package twopiradians.blockArmor.common.seteffect;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectBonemealer extends SetEffect {

	protected SetEffectBonemealer() {
		super();
		this.color = ChatFormatting.WHITE;
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isClientSide && ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldowns().isOnCooldown(stack.getItem())) {
			int radius = 2;
			ArrayList<BlockPos> bonemealed = new ArrayList<BlockPos>();
			for (int x=-radius; x<radius; x++)
				for (int y=-radius; y<radius; y++)
					for (int z=-radius; z<radius; z++)
						if (BoneMealItem.applyBonemeal(new ItemStack(Items.WHITE_DYE), 
								world, player.blockPosition().offset(x, y, z), player)) 
							bonemealed.add(player.blockPosition().offset(x, y, z));
			if (!bonemealed.isEmpty()) {
				this.setCooldown(player, 100);
				for (BlockPos pos : bonemealed)
					((ServerLevel)world).sendParticles(ParticleTypes.HAPPY_VILLAGER, 
							pos.getX(), pos.getY()+1d, pos.getZ(), 10, 2, 0.1d, 2, 0);
				world.playSound(null, player.blockPosition(), SoundEvents.HOE_TILL, 
						SoundSource.PLAYERS, 0.5f, world.random.nextFloat()+0.5f);
				this.damageArmor(player, 4, false);
			}
			else
				this.setCooldown(player, 20);
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"bone", "fertil", "manure", "poop", "grow", "compost"}))
			return true;
		return false;
	}
}