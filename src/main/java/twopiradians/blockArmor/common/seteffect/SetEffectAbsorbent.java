package twopiradians.blockArmor.common.seteffect;

import java.util.Queue;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.BlockArmorItem;

public class SetEffectAbsorbent extends SetEffect {

	protected SetEffectAbsorbent() {
		super();
		this.color = TextFormatting.YELLOW;
		this.description = "Absorbs nearby liquids";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && player.getCooldownTracker().hasCooldown(stack.getItem())) 
			((ServerWorld)world).spawnParticle(ParticleTypes.FALLING_WATER, player.getPosX(), player.getPosY()+1.0d,player.getPosZ(), 
					3, 0.2d, 0.5d, 0.2d, 0);

		if (!world.isRemote && !player.getCooldownTracker().hasCooldown(stack.getItem())) {
			ArmorSet wornSet = ((BlockArmorItem)stack.getItem()).set;
			ArmorSet drySet = ArmorSet.getSet(Blocks.SPONGE);
			ArmorSet wetSet = ArmorSet.getSet(Blocks.WET_SPONGE);

			if (wornSet != null) {
				//change wet sponge back to normal
				if (wornSet.block == Blocks.WET_SPONGE) { 
					for (EquipmentSlotType slot : ArmorSet.SLOTS) {
						ItemStack oldStack = player.getItemStackFromSlot(slot);
						if (oldStack != null && oldStack.getItem() instanceof BlockArmorItem && 
								((BlockArmorItem)oldStack.getItem()).set == wornSet) { //only change if wet sponge 
							CompoundNBT nbt = new CompoundNBT();
							oldStack.write(nbt);
							nbt.putString("id", drySet.getArmorForSlot(slot).getRegistryName().toString());
							player.setItemStackToSlot(slot, ItemStack.read(nbt));
						}
					}
				}
				else if (wornSet.block == Blocks.SPONGE &&
						!world.isRemote && player.isAllowEdit() && BlockArmor.key.isKeyDown(player) &&
						this.absorb(world, player.getPosition(), player, stack)) {
					world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ITEM_BUCKET_FILL, 
							SoundCategory.PLAYERS, 1.0F, world.rand.nextFloat() + 0.5f);

					//change dry sponge to wet sponge
					for (EquipmentSlotType slot : ArmorSet.SLOTS) {
						ItemStack oldStack = player.getItemStackFromSlot(slot);
						if (oldStack != null && oldStack.getItem() instanceof BlockArmorItem && 
								((BlockArmorItem)oldStack.getItem()).set == wornSet) { //only change if dry sponge 
							CompoundNBT nbt = new CompoundNBT();
							oldStack.write(nbt);
							nbt.putString("id", wetSet.getArmorForSlot(slot).getRegistryName().toString());
							player.setItemStackToSlot(slot, ItemStack.read(nbt));
						}
					}
					
					this.setCooldown(player, 60);
					this.damageArmor(player, 1, false);
				}

			}
		}

	}
	
	/**Copied from SpongeBlock.class*/
	private boolean absorb(World worldIn, BlockPos pos, PlayerEntity player, ItemStack stack) {
	      Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
	      queue.add(new Tuple<>(pos, 0));
	      int i = 0;

	      while(!queue.isEmpty()) {
	         Tuple<BlockPos, Integer> tuple = queue.poll();
	         BlockPos blockpos = tuple.getA();
	         int j = tuple.getB();

	         for(Direction direction : Direction.values()) {
	            BlockPos blockpos1 = blockpos.offset(direction);
	            BlockState blockstate = worldIn.getBlockState(blockpos1);
	            FluidState fluidstate = worldIn.getFluidState(blockpos1);
	            Material material = blockstate.getMaterial();
	            if (fluidstate.isTagged(FluidTags.WATER) && player.canPlayerEdit(blockpos1, Direction.UP, stack)) {
	               if (blockstate.getBlock() instanceof IBucketPickupHandler && ((IBucketPickupHandler)blockstate.getBlock()).pickupFluid(worldIn, blockpos1, blockstate) != Fluids.EMPTY) {
	                  ++i;
	                  if (j < 6) {
	                     queue.add(new Tuple<>(blockpos1, j + 1));
	                  }
	               } else if (blockstate.getBlock() instanceof FlowingFluidBlock) {
	                  worldIn.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 3);
	                  ++i;
	                  if (j < 6) {
	                     queue.add(new Tuple<>(blockpos1, j + 1));
	                  }
	               } else if (material == Material.OCEAN_PLANT || material == Material.SEA_GRASS) {
	                  TileEntity tileentity = blockstate.hasTileEntity() ? worldIn.getTileEntity(blockpos1) : null;
	                  Block.spawnDrops(blockstate, worldIn, blockpos1, tileentity);
	                  worldIn.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 3);
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