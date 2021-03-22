package twopiradians.blockArmor.common.seteffect;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectPuller extends SetEffect {

	protected SetEffectPuller() {
		this.color = TextFormatting.GRAY;
		this.description = "Pulls in nearby entities";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldownTracker().hasCooldown(stack.getItem())) {
			AxisAlignedBB aabb = player.getBoundingBox().grow(10, 10, 10);
			List<Entity> list = player.world.getEntitiesWithinAABBExcludingEntity(player, aabb);
			
			if (!list.isEmpty()) {
				Iterator<Entity> iterator = list.iterator();            
				while (iterator.hasNext()) {
					Entity entityCollided = iterator.next();
					if (!entityCollided.isImmuneToExplosions()) {
						double xVel = entityCollided.getPosX() - player.getPosX();
						double yVel = entityCollided.getPosY() - player.getPosY();
						double zVel = entityCollided.getPosZ() - player.getPosZ();
						double velScale = 4 / Math.sqrt(xVel * xVel + yVel * yVel + zVel * zVel);
						entityCollided.addVelocity(-velScale*xVel, -velScale*yVel, -velScale*zVel); 
						entityCollided.velocityChanged = true;
					}
				}
				world.playSound((PlayerEntity)null, player.getPosition(), SoundEvents.BLOCK_PISTON_CONTRACT, 
						SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() + 0.5f);
				
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
		if (SetEffect.registryNameContains(block, new String[] {"pull", "attract", "magnet"}) ||
				(SetEffect.registryNameContains(block, new String[] {"piston"}) && 
						SetEffect.registryNameContains(block, new String[] {"sticky"})))
			return true;
		return false;
	}
}