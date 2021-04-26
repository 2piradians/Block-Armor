package twopiradians.blockArmor.common.seteffect;

import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeSet;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
			float maxYawOffset = 360f;
			float yawIncrement = 20f;
			float rollIncrement = 3;
			int radius = 0;
			int distance = 30;

			HashSet<BlockPos> checkedPositions = Sets.newHashSet();
			// small -> big yaw offset
			for (float yawOffset = 0; yawOffset <= maxYawOffset; yawOffset += yawIncrement)
				// spin roll around (only check once for yawOffset = 0)
				for (float roll = 0; roll <= 360; roll += (yawOffset == 0 ? 999 : rollIncrement)) {
					RayTraceResult result = this.pick(player, distance, yawOffset, roll);
					((ServerWorld)world).spawnParticle(ParticleTypes.BARRIER, result.getHitVec().getX()+0.5f, result.getHitVec().getY()+0.5d, result.getHitVec().getZ()+0.5d, 1, 0, 0, 0, 1); // TODO remove
					if (result.getType() == RayTraceResult.Type.BLOCK) {
						// sort positions by distance from hit
						TreeSet<BlockPos> positions = Sets.newTreeSet(new Comparator<BlockPos>() {
							@Override
							public int compare(BlockPos o1, BlockPos o2) {
								return Double.compare(o1.distanceSq(new BlockPos(result.getHitVec())), o2.distanceSq(new BlockPos(result.getHitVec())));
							}
						});
						// add all positions in radius
						for (int x = -radius; x <= radius; ++x) 
							for (int y = -radius; y <= radius; ++y)
								for (int z = -radius; z <= radius; ++z) {
									BlockPos pos = new BlockPos(result.getHitVec().getX()+x, result.getHitVec().getY()+y, result.getHitVec().getZ()+z);
									System.out.println("trying to add: "+pos+", distance: "+pos.distanceSq(new BlockPos(result.getHitVec()))); // TODO remove
									if (!checkedPositions.contains(pos)) {
										System.out.println("adding: "+pos+", distance: "+pos.distanceSq(new BlockPos(result.getHitVec()))); // TODO remove
										positions.add(pos);
										//((ServerWorld)world).spawnParticle(ParticleTypes.BARRIER, pos.getX()+0.5f, pos.getY()+0.5d, pos.getZ()+0.5d, 1, 0, 0, 0, 1); // TODO remove
									}
								}
						// check each position to see if it can be tp'd to
						for (BlockPos pos : positions) {
							System.out.println("checking: "+pos+", distance: "+pos.distanceSq(new BlockPos(result.getHitVec()))); // TODO remove
							checkedPositions.add(pos);
							Vector3d beforePos = player.getPositionVec();
							/*if (player.attemptTeleport(pos.getX()+0.5d, pos.getY(), pos.getZ()+0.5d, true)) {
								player.setPositionAndUpdate(beforePos.x, beforePos.y, beforePos.z); // TODO remove
								if (player.isPassenger())
									player.stopRiding();
								world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
								world.playSound((PlayerEntity)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
								player.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
								for (int j = 0; j < 64; ++j) {
									((ServerWorld)world).spawnParticle(ParticleTypes.PORTAL, player.getPosX()+2*world.rand.nextDouble(), player.getPosY()+world.rand.nextDouble()+1, player.getPosZ()+2*world.rand.nextDouble(), 1, 0, 0, 0, 1);
									((ServerWorld)world).spawnParticle(ParticleTypes.PORTAL, player.getPosX()+2*world.rand.nextDouble(), player.getPosY()+world.rand.nextDouble()+1.0D, player.getPosZ()+2*world.rand.nextDouble(), 1, 0, 0, 0, 1);
								}
								player.fallDistance = 0;
								this.setCooldown(player, 30);
								this.damageArmor(player, 2, false);
								return;
							}*/
						}
					}
				}
			// no valid pos found
			if (player instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)player).connection.sendPacket(new SPlaySoundPacket(SoundEvents.BLOCK_NOTE_BLOCK_BASS.getName(), SoundCategory.PLAYERS, player.getPositionVec(), 1.0F, world.rand.nextFloat() + 0.5F));	
				this.setCooldown(player, 10);
			}
		}
	}

	/**Edited from Entity#pick*/ // TEST fluids // FIXME
	public RayTraceResult pick(PlayerEntity player, double distance, float yawOffset, float roll) {
		Vector3d start = player.getEyePosition(0);
		Vector3d look = player.getLook(0).rotateRoll(roll);//this.getVectorForRotation(player.rotationPitch, player.rotationYawHead); 
		Vector3d end = start.add(look.x * distance, look.y * distance, look.z * distance);
		System.out.println("yawOffset: "+yawOffset+", roll: "+roll); // TODO remove
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
	      return new Vector3d((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
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
		if (SetEffect.registryNameContains(block, new String[] {"end_stone", "end_bricks", "ender", "EndStone", "chorus", "purpur"}))
			return true;
		return false;
	}
}