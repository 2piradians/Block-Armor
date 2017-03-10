package twopiradians.blockArmor.common.seteffect;

import java.util.List;
import java.util.Queue;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

public class SetEffectAbsorbent extends SetEffect {

	protected SetEffectAbsorbent() {
		this.color = TextFormatting.YELLOW;
		this.description = "Absorbs nearby liquids";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && player.getCooldownTracker().hasCooldown(stack.getItem())) 
				((WorldServer)world).spawnParticle(EnumParticleTypes.WATER_DROP, true, player.posX, player.posY+1.0d,player.posZ, 
						5, 0.2d, 0.5d, 0.2d, 0, new int[0]);

		if (!world.isRemote && ((ItemBlockArmor)stack.getItem()).armorType == EntityEquipmentSlot.FEET &&
				!player.getCooldownTracker().hasCooldown(stack.getItem())) {
			ArmorSet set = ArmorSet.getWornSet(player);
			if (set != null) {
				//change wet sponge back to normal
				if (set.block == Blocks.SPONGE && set.meta == 1) {
					ArmorSet set2 = ArmorSet.getSet(Blocks.SPONGE, 0);
					EntityEquipmentSlot[] slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD,
							EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

					for (EntityEquipmentSlot slot : slots) {
						ItemStack oldStack = player.getItemStackFromSlot(slot);
						NBTTagCompound nbt = new NBTTagCompound();
						oldStack.writeToNBT(nbt);
						ResourceLocation resourcelocation = (ResourceLocation)Item.REGISTRY.getNameForObject(set2.getArmorForSlot(slot));
						nbt.setString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
						ItemStack newStack = new ItemStack(nbt);
						player.setItemStackToSlot(slot, newStack);
					}
				}
				else if (!world.isRemote && player.isAllowEdit() && BlockArmor.key.isKeyDown(player) &&
						player.world.getBlockState(player.getPosition()).getBlock() instanceof BlockLiquid) {
					//change dry sponge to wet sponge
					if (set.block == Blocks.SPONGE && set.meta == 0) {
						ArmorSet set2 = ArmorSet.getSet(Blocks.SPONGE, 1);
						EntityEquipmentSlot[] slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD,
								EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

						for (EntityEquipmentSlot slot : slots) {
							ItemStack oldStack = player.getItemStackFromSlot(slot);
							NBTTagCompound nbt = new NBTTagCompound();
							oldStack.writeToNBT(nbt);
							ResourceLocation resourcelocation = (ResourceLocation)Item.REGISTRY.getNameForObject(set2.getArmorForSlot(slot));
							nbt.setString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
							ItemStack newStack = new ItemStack(nbt);
							player.setItemStackToSlot(slot, newStack);
						}
					}
					//absorb liquids
					Queue<Tuple<BlockPos, Integer>> queue = Lists.<Tuple<BlockPos, Integer>>newLinkedList();
					List<BlockPos> list = Lists.<BlockPos>newArrayList();
					queue.add(new Tuple(player.getPosition(), Integer.valueOf(0)));
					int i = 0;

					while (!((Queue)queue).isEmpty()) {
						Tuple<BlockPos, Integer> tuple = (Tuple)queue.poll();
						BlockPos blockpos = (BlockPos)tuple.getFirst();
						int j = ((Integer)tuple.getSecond()).intValue();

						for (EnumFacing enumfacing : EnumFacing.values()) {
							BlockPos blockpos1 = blockpos.offset(enumfacing);

							if (world.getBlockState(blockpos1).getBlock() instanceof BlockLiquid) {
								world.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 2);
								list.add(blockpos1);
								++i;

								if (j < 6) //~diameter
									queue.add(new Tuple(blockpos1, Integer.valueOf(j + 1)));
							}
						}
						if (i > 64) { break;} //max number of blocks
					}

					for (BlockPos blockpos2 : list) 
						world.notifyNeighborsOfStateChange(blockpos2, Blocks.AIR, false);

					world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_BUCKET_FILL, 
							SoundCategory.PLAYERS, 1.0F, world.rand.nextFloat() + 0.5f);

					this.setCooldown(player, 60);
				}

			}
		}

	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {	
		if (SetEffect.registryNameContains(block, new String[] {"sponge", "absorb"}) && 
				!SetEffect.registryNameContains(block, new String[] {"wet"}))
			return true;		
		return false;
	}
}