package twopiradians.blockArmor.common.seteffect;

import java.util.HashSet;
import java.util.LinkedHashSet;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectEnder extends SetEffect {

	protected SetEffectEnder() {
		super();
		this.color = TextFormatting.DARK_PURPLE;
		this.description = "Teleports in the direction you're looking";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldownTracker().hasCooldown(stack.getItem()))	{    

			// variables
			float maxYawOffset = 6f;
			float yawIncrement = 2f;
			float rollIncrement = (float) (Math.PI/4f);
			int radius = 0; // 0 radius for now to prevent tping through walls
			int distance = 30;
			int maxYOffset = 3;

			HashSet<BlockPos> checkedPositions = Sets.newHashSet();
			// small -> big yaw offset
			for (float yawOffset = 0; yawOffset <= maxYawOffset; yawOffset += yawIncrement)
				// spin roll around (only check once for yawOffset = 0)
				for (float roll = 0; roll <= 2*Math.PI; roll += (yawOffset == 0 ? 999 : rollIncrement)) {
					RayTraceResult result = this.pick(player, distance, yawOffset, roll);
					//((ServerWorld)world).spawnParticle(ParticleTypes.BARRIER, result.getHitVec().getX(), result.getHitVec().getY(), result.getHitVec().getZ(), 1, 0, 0, 0, 1); 
					if (result.getType() == RayTraceResult.Type.BLOCK) {
						LinkedHashSet<BlockPos> positions = Sets.newLinkedHashSet();
						// prioritize closest position and position up 1
						BlockPos resultPos = new BlockPos(result.getHitVec().getX(), result.getHitVec().getY(), result.getHitVec().getZ());
						positions.add(resultPos);
						positions.add(resultPos.up());
						// add all positions in radius
						for (int x = -radius; x <= radius; ++x) 
							for (int y = -radius; y <= radius; ++y)
								for (int z = -radius; z <= radius; ++z) {
									BlockPos pos = new BlockPos(result.getHitVec().getX()+x, result.getHitVec().getY()+y, result.getHitVec().getZ()+z);
									positions.add(pos);
								}
						// check each position to see if it can be tp'd to
						for (BlockPos pos : positions) {
							//((ServerWorld)world).spawnParticle(ParticleTypes.BARRIER, pos.getX()+0.5d, pos.getY()+0.5d, pos.getZ()+0.5d, 1, 0, 0, 0, 1);
							checkedPositions.add(pos);
							if (attemptTeleport(player, pos.getX()+0.5d, pos.getY(), pos.getZ()+0.5d, maxYOffset)) {
								//((ServerWorld)world).spawnParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX()+0.5d, pos.getY()+0.5d, pos.getZ()+0.5d, 10, 0, 0, 0, 0); 

								if (player.isPassenger())
									player.stopRiding();
								world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
								world.playSound((PlayerEntity)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
								player.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
								player.fallDistance = 0;
								this.setCooldown(player, 30);
								this.damageArmor(player, 2, false);
								return;
							}
						}
					}
				}
			// no valid pos found
			if (player instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)player).connection.sendPacket(new SPlaySoundPacket(SoundEvents.BLOCK_NOTE_BLOCK_BASS.getRegistryName(), SoundCategory.PLAYERS, player.getPositionVec(), 1.0F, world.rand.nextFloat() + 0.5F));	
				this.setCooldown(player, 10);
			}
		}
	}

	/**Modified from LivingEntity#attemptTeleport to be more strict about y level*/
	@SuppressWarnings("deprecation")
	private boolean attemptTeleport(PlayerEntity player, double x, double y, double z, int maxYOffset) {
		double d0 = player.getPosX();
		double d1 = player.getPosY();
		double d2 = player.getPosZ();
		double d3 = y;
		boolean flag = false;
		BlockPos blockpos = new BlockPos(x, y, z);
		World world = player.world;
		if (world.isBlockLoaded(blockpos)) {
			boolean flag1 = false;

			while(!flag1 && blockpos.getY() > 0 && maxYOffset-- > 0) {
				BlockPos blockpos1 = blockpos.down();
				BlockState blockstate = world.getBlockState(blockpos1);
				if (blockstate.getMaterial().blocksMovement()) {
					flag1 = true;
				} else {
					--d3;
					blockpos = blockpos1;
				}
			}

			if (flag1) {
				player.setPositionAndUpdate(x, d3, z);
				if (world.hasNoCollisions(player) && !world.containsAnyLiquid(player.getBoundingBox())) {
					flag = true;
				}
			}
		}

		if (!flag) {
			player.setPositionAndUpdate(d0, d1, d2);
			return false;
		} 
		else { 
			world.setEntityState(player, (byte)46); // particles

			return true;
		}
	}

	/**Edited from Entity#pick - could use some improvement (forms oval instead of circle farther up/down)*/ 
	public RayTraceResult pick(PlayerEntity player, double distance, float yawOffset, float roll) {
		Vector3d start = player.getEyePosition(0);
		Vector3d look = this.getVectorForRotation(player.rotationPitch+MathHelper.sin(roll)*yawOffset, player.rotationYawHead+MathHelper.cos(roll)*yawOffset);		
		Vector3d end = start.add(look.x * distance, look.y * distance, look.z * distance);
		return player.world.rayTraceBlocks(new RayTraceContext(start, end, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));
	}

	/**Copied from Entity#getVectorForRotation cuz protected*/
	public final Vector3d getVectorForRotation(float pitch, float yaw) {
		float f = pitch * ((float)Math.PI / 180F);
		float f1 = -yaw * ((float)Math.PI / 180F);
		float f2 = MathHelper.cos(f1);
		float f3 = MathHelper.sin(f1);
		float f4 = MathHelper.cos(f);
		float f5 = MathHelper.sin(f);
		return new Vector3d((double) (f3 * f4), (double) (-f5), (double) (f2 * f4));
	}

	/**Copied from {@link Vector3d#rotateRoll(float)} bc clientside*/
	public Vector3d rotateRoll(Vector3d vec, float roll) {
		float f = MathHelper.cos(roll);
		float f1 = MathHelper.sin(roll);
		double d0 = vec.x * (double)f + vec.y * (double)f1;
		double d1 = vec.y * (double)f - vec.x * (double)f1;
		double d2 = vec.z;
		return new Vector3d(d0, d1, d2);
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"end_stone", "end_bricks", "ender", "EndStone", "chorus", "purpur", "teleport"}))
			return true;
		return false;
	}
}