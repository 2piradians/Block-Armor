package twopiradians.blockArmor.common.seteffect;

import java.util.Queue;

import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.BlockArmorItem;

public class SetEffectAbsorbent extends SetEffect {

	protected SetEffectAbsorbent() {
		super();
		this.color = ChatFormatting.YELLOW;
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isClientSide && player.getCooldowns().isOnCooldown(stack.getItem())) 
			((ServerLevel)world).sendParticles(ParticleTypes.FALLING_WATER, player.getX(), player.getY()+1.0d,player.getZ(), 
					3, 0.2d, 0.5d, 0.2d, 0);

		if (!world.isClientSide && !player.getCooldowns().isOnCooldown(stack.getItem())) {
			ArmorSet wornSet = ((BlockArmorItem)stack.getItem()).set;
			ArmorSet drySet = ArmorSet.getSet(Blocks.SPONGE);
			ArmorSet wetSet = ArmorSet.getSet(Blocks.WET_SPONGE);

			if (wornSet != null) {
				//change wet sponge back to normal
				if (wornSet.block == Blocks.WET_SPONGE) { 
					for (EquipmentSlot slot : ArmorSet.SLOTS) {
						ItemStack oldStack = player.getItemBySlot(slot);
						if (oldStack != null && oldStack.getItem() instanceof BlockArmorItem && 
								((BlockArmorItem)oldStack.getItem()).set == wornSet) { //only change if wet sponge 
							CompoundTag nbt = new CompoundTag();
							oldStack.save(nbt);
							nbt.putString("id", drySet.getArmorForSlot(slot).getRegistryName().toString());
							player.setItemSlot(slot, ItemStack.of(nbt));
						}
					}
				}
				else if (wornSet.block == Blocks.SPONGE &&
						!world.isClientSide && player.mayBuild() && BlockArmor.key.isKeyDown(player) &&
						this.absorb(world, player.blockPosition(), player, stack)) {
					world.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.BUCKET_FILL, 
							SoundSource.PLAYERS, 1.0F, world.random.nextFloat() + 0.5f);

					//change dry sponge to wet sponge
					for (EquipmentSlot slot : ArmorSet.SLOTS) {
						ItemStack oldStack = player.getItemBySlot(slot);
						if (oldStack != null && oldStack.getItem() instanceof BlockArmorItem && 
								((BlockArmorItem)oldStack.getItem()).set == wornSet) { //only change if dry sponge 
							CompoundTag nbt = new CompoundTag();
							oldStack.save(nbt);
							nbt.putString("id", wetSet.getArmorForSlot(slot).getRegistryName().toString());
							player.setItemSlot(slot, ItemStack.of(nbt));
						}
					}
					
					this.setCooldown(player, 60);
					this.damageArmor(player, 1, false);
				}

			}
		}

	}
	
	/**Copied from SpongeBlock.class*/
	private boolean absorb(Level worldIn, BlockPos pos, Player player, ItemStack stack) {
	      Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
	      queue.add(new Tuple<>(pos, 0));
	      int i = 0;

	      while(!queue.isEmpty()) {
	         Tuple<BlockPos, Integer> tuple = queue.poll();
	         BlockPos blockpos = tuple.getA();
	         int j = tuple.getB();

	         for(Direction direction : Direction.values()) {
	            BlockPos blockpos1 = blockpos.relative(direction);
	            BlockState blockstate = worldIn.getBlockState(blockpos1);
	            FluidState fluidstate = worldIn.getFluidState(blockpos1);
	            Material material = blockstate.getMaterial();
	            if (fluidstate.is(FluidTags.WATER) && player.mayUseItemAt(blockpos1, Direction.UP, stack)) {
	               if (blockstate.getBlock() instanceof BucketPickup && !((BucketPickup)blockstate.getBlock()).pickupBlock(worldIn, blockpos1, blockstate).isEmpty()) {
	                  ++i;
	                  if (j < 6) {
	                     queue.add(new Tuple<>(blockpos1, j + 1));
	                  }
	               } else if (blockstate.getBlock() instanceof LiquidBlock) {
	                  worldIn.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 3);
	                  ++i;
	                  if (j < 6) {
	                     queue.add(new Tuple<>(blockpos1, j + 1));
	                  }
	               } else if (material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
	                  BlockEntity tileentity = blockstate.hasBlockEntity() ? worldIn.getBlockEntity(blockpos1) : null;
	                  Block.dropResources(blockstate, worldIn, blockpos1, tileentity);
	                  worldIn.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 3);
	                  ++i;
	                  if (j < 6) {
	                     queue.add(new Tuple<>(blockpos1, j + 1));
	                  }
	               }
	            }
	         }

	         if (i > 64) {
	            break;
	         }
	      }

	      return i > 0;
	   }

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {	
		if (SetEffect.registryNameContains(block, new String[] {"sponge", "absorb"}))
			return true;		
		return false;
	}
}