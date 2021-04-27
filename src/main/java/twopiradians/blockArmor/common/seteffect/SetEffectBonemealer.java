package twopiradians.blockArmor.common.seteffect;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectBonemealer extends SetEffect {

	protected SetEffectBonemealer() {
		super();
		this.color = TextFormatting.WHITE;
		this.description = "Applies bonemeal to nearby blocks";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldownTracker().hasCooldown(stack.getItem())) {
			int radius = 2;
			ArrayList<BlockPos> bonemealed = new ArrayList<BlockPos>();
			for (int x=-radius; x<radius; x++)
				for (int y=-radius; y<radius; y++)
					for (int z=-radius; z<radius; z++)
						if (BoneMealItem.applyBonemeal(new ItemStack(Items.WHITE_DYE), 
								world, player.getPosition().add(x, y, z), player)) 
							bonemealed.add(player.getPosition().add(x, y, z));
			if (!bonemealed.isEmpty()) {
				this.setCooldown(player, 100);
				for (BlockPos pos : bonemealed)
					((ServerWorld)world).spawnParticle(ParticleTypes.HAPPY_VILLAGER, 
							pos.getX(), pos.getY()+1d, pos.getZ(), 10, 2, 0.1d, 2, 0);
				world.playSound(null, player.getPosition(), SoundEvents.ITEM_HOE_TILL, 
						SoundCategory.PLAYERS, 0.5f, world.rand.nextFloat()+0.5f);
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