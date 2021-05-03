package twopiradians.blockArmor.common.seteffect;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class SetEffectRocky extends SetEffect {

	private static final Field IN_WATER_FIELD;
	private static final Field FIRST_UPDATE_FIELD;

	static {
		IN_WATER_FIELD = ObfuscationReflectionHelper.findField(Entity.class, "field_70171_ac");
		IN_WATER_FIELD.setAccessible(true);
		FIRST_UPDATE_FIELD = ObfuscationReflectionHelper.findField(Entity.class, "field_70148_d");
		FIRST_UPDATE_FIELD.setAccessible(true);
	}

	protected SetEffectRocky() {
		super();
		this.color = TextFormatting.GRAY;
		this.description = "Sink like a rock underwater";
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		Vector3d motion = player.getMotion();
		if (player.isInWater()) {
			player.setSwimming(false); // prevent swimming
			try {	
				// set to not in water (so player sinks and can move normally)
				IN_WATER_FIELD.set(player, false); 
				// set firstUpdate to true so it doesn't spawn a ton of bubble particles in and make sounds
				FIRST_UPDATE_FIELD.set(player, true);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			// slow down under water a bit (if no depth strider)
			if ((Math.abs(motion.x) > 0 || Math.abs(motion.z) > 0 || motion.y > 0) && 
					EnchantmentHelper.getDepthStriderModifier(player) == 0) {
				player.setSprinting(false); // prevent sprinting
				player.setMotion(
						motion.getX()*(player.isOnGround() ? 0.14d : 1d), 
						motion.getY(), 
						motion.getZ()*(player.isOnGround() ? 0.14d : 1d));
				if (player.isOnGround())
					player.velocityChanged = true;
				player.isAirBorne = true;
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"rock", "stone"}))
			return true;
		return false;
	}
}