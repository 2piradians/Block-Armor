package twopiradians.blockArmor.common.seteffect;

import java.util.HashSet;
import java.util.LinkedHashSet;

import com.google.common.collect.Sets;
import com.mojang.math.Vector3d;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectEnder extends SetEffect {

	protected SetEffectEnder() {
		super();
		this.color = ChatFormatting.DARK_PURPLE;
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isClientSide && ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldowns().isOnCooldown(stack.getItem()))	{    

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
					HitResult result = this.pick(player, distance, yawOffset, roll);
					//((ServerWorld)world).spawnParticle(ParticleTypes.BARRIER, result.getHitVec().getX(), result.getHitVec().getY(), result.getHitVec().getZ(), 1, 0, 0, 0, 1); 
					if (result.getType() == HitResult.Type.BLOCK) {
						LinkedHashSet<BlockPos> positions = Sets.newLinkedHashSet();
						// prioritize closest position and position up 1
						BlockPos resultPos = new BlockPos(result.getLocation().x(), result.getLocation().y(), result.getLocation().z());
						positions.add(resultPos);
						positions.add(resultPos.above());
						// add all positions in radius
						for (int x = -radius; x <= radius; ++x) 
							for (int y = -radius; y <= radius; ++y)
								for (int z = -radius; z <= radius; ++z) {
									BlockPos pos = new BlockPos(result.getLocation().x()+x, result.getLocation().y()+y, result.getLocation().z()+z);
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
								world.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
								world.playSound((Player)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
								player.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
								player.fallDistance = 0;
								this.setCooldown(player, 30);
								this.damageArmor(player, 2, false);
								return;
							}
						}
					}
				}
			// no valid pos found
			if (player instanceof ServerPlayer) {
				((ServerPlayer)player).connection.send(new ClientboundCustomSoundPacket(SoundEvents.NOTE_BLOCK_BASS.getRegistryName(), SoundSource.PLAYERS, player.position(), 1.0F, world.random.nextFloat() + 0.5F));	
				this.setCooldown(player, 10);
			}
		}
	}

	/**Modified from LivingEntity#attemptTeleport to be more strict about y level*/
	@SuppressWarnings("deprecation")
	private boolean attemptTeleport(Player player, double x, double y, double z, int maxYOffset) {
		double d0 = player.getX();
		double d1 = player.getY();
		double d2 = player.getZ();
		double d3 = y;
		boolean flag = false;
		BlockPos blockpos = new BlockPos(x, y, z);
		Level world = player.level;
		if (world.hasChunkAt(blockpos)) {
			boolean flag1 = false;

			while(!flag1 && blockpos.getY() > 0 && maxYOffset-- > 0) {
				BlockPos blockpos1 = blockpos.below();
				BlockState blockstate = world.getBlockState(blockpos1);
				if (blockstate.getMaterial().blocksMotion()) {
					flag1 = true;
				} else {
					--d3;
					blockpos = blockpos1;
				}
			}

			if (flag1) {
				player.teleportTo(x, d3, z);
				if (world.noCollision(player) && !world.containsAnyLiquid(player.getBoundingBox())) {
					flag = true;
				}
			}
		}

		if (!flag) {
			player.teleportTo(d0, d1, d2);
			return false;
		} 
		else { 
			world.broadcastEntityEvent(player, (byte)46); // particles

			return true;
		}
	}

	/**Edited from Entity#pick - could use some improvement (forms oval instead of circle farther up/down)*/ 
	public HitResult pick(Player player, double distance, float yawOffset, float roll) {
		Vec3 start = player.getEyePosition(0);
		Vec3 look = this.getVectorForRotation(player.getXRot()+Mth.sin(roll)*yawOffset, player.yHeadRot+Mth.cos(roll)*yawOffset);		
		Vec3 end = start.add(look.x * distance, look.y * distance, look.z * distance);
		return player.level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
	}

	/**Copied from Entity#getVectorForRotation cuz protected*/
	public final Vec3 getVectorForRotation(float pitch, float yaw) {
		float f = pitch * ((float)Math.PI / 180F);
		float f1 = -yaw * ((float)Math.PI / 180F);
		float f2 = Mth.cos(f1);
		float f3 = Mth.sin(f1);
		float f4 = Mth.cos(f);
		float f5 = Mth.sin(f);
		return new Vec3((double) (f3 * f4), (double) (-f5), (double) (f2 * f4));
	}

	/**Copied from {@link Vector3d#rotateRoll(float)} bc clientside*/
	public Vec3 rotateRoll(Vec3 vec, float roll) {
		float f = Mth.cos(roll);
		float f1 = Mth.sin(roll);
		double d0 = vec.x * (double)f + vec.y * (double)f1;
		double d1 = vec.y * (double)f - vec.x * (double)f1;
		double d2 = vec.z;
		return new Vec3(d0, d1, d2);
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"end_stone", "end_bricks", "ender", "EndStone", "chorus", "purpur", "teleport"}))
			return true;
		return false;
	}
}