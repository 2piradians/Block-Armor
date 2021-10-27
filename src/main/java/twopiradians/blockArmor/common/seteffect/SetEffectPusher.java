package twopiradians.blockArmor.common.seteffect;

import java.util.Iterator;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectPusher extends SetEffect {

	protected SetEffectPusher() {
		super();
		this.color = ChatFormatting.GRAY;
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldowns().isOnCooldown(stack.getItem())) {
			AABB aabb = player.getBoundingBox().inflate(5, 5, 5);
			List<Entity> list = player.level.getEntities(player, aabb);
			
			if (!list.isEmpty()) {
				Iterator<Entity> iterator = list.iterator();            
				while (iterator.hasNext()) {
					Entity entityCollided = iterator.next();
					if (!entityCollided.ignoreExplosion() && !(entityCollided instanceof ItemFrame)) {
						double xVel = entityCollided.getX() - player.getX();
						double yVel = entityCollided.getY() - player.getY();
						double zVel = entityCollided.getZ() - player.getZ();
						double velScale = 5 / Math.sqrt(xVel * xVel + yVel * yVel + zVel * zVel);
						entityCollided.push(velScale*xVel, velScale*yVel, velScale*zVel); 
						entityCollided.hurtMarked = true;
					}
				}
				world.playSound((Player)null, player.blockPosition(), SoundEvents.PISTON_EXTEND, 
						SoundSource.PLAYERS, 0.5F, world.random.nextFloat() + 0.5f);
				
				this.setCooldown(player, 40);
				this.damageArmor(player, 1, false);
			}
			else
				this.setCooldown(player, 5);
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"piston", "push", "repel"}) &&
				!SetEffect.registryNameContains(block, new String[] {"sticky"}))
			return true;
		return false;
	}
}