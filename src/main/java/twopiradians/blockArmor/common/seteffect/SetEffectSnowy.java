package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

public class SetEffectSnowy extends SetEffect {

	protected SetEffectSnowy() {
		this.color = TextFormatting.WHITE;
		this.description = "Spawns snow and snowballs";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	@SuppressWarnings("deprecation")
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (BlockArmor.key.isKeyDown(player) && ((ItemBlockArmor)stack.getItem()).armorType != EntityEquipmentSlot.FEET) {
			int radius = 3;
			if (!world.isRemote) {
				if (player.ticksExisted % 2 == 0) 
					((WorldServer)world).spawnParticle(EnumParticleTypes.SNOW_SHOVEL, player.posX+(world.rand.nextDouble()-0.5D)*radius, 
							player.posY+world.rand.nextDouble()+2D, player.posZ+(world.rand.nextDouble()-0.5D)*radius, 
							1, 0, 0, 0, 0, new int[0]);
					((WorldServer)world).spawnParticle(EnumParticleTypes.CLOUD, player.posX+(world.rand.nextDouble()-0.5D)*radius, 
							player.posY+world.rand.nextDouble()*0.5d+2.5D, player.posZ+(world.rand.nextDouble()-0.5D)*radius, 
							3, 0, 0, 0, 0, new int[0]);
				if (world.rand.nextInt(5) == 0)
					world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.WEATHER_RAIN, 
							player.getSoundCategory(), 0.1f, world.rand.nextFloat());
			}
			else {
				for (int x=-radius/2; x<=radius/2; x++)
					for (int z=-radius/2; z<=radius/2; z++)
						for (int y=0; y<=2; y++)
							if (player.isAllowEdit() && world.rand.nextInt(100) == 0 && world.isAirBlock(player.getPosition().add(x, y, z))) {
								if (world.getBlockState(player.getPosition().add(x, y-1, z)).getBlock().isVisuallyOpaque(world.getBlockState(player.getPosition().add(x, y-1, z))))
									world.setBlockState(player.getPosition().add(x, y, z), Blocks.SNOW_LAYER.getDefaultState());
								else if (world.getBlockState(player.getPosition().add(x, y-1, z)).getBlock() == Blocks.WATER)
									world.setBlockState(player.getPosition().add(x, y-1, z), Blocks.FROSTED_ICE.getDefaultState());
								else if (world.getBlockState(player.getPosition().add(x, y-1, z)).getBlock() == Blocks.FROSTED_ICE)
									world.setBlockState(player.getPosition().add(x, y-1, z), Blocks.FROSTED_ICE.getDefaultState());
							}
				//spawn snowballs
				if (world.rand.nextInt(30) == 0) {
					EntityItem item = new EntityItem(world, player.posX+(world.rand.nextDouble()-0.5D)*radius, 
							player.posY+world.rand.nextDouble()+1.5D, player.posZ+(world.rand.nextDouble()-0.5D)*radius,
							new ItemStack(Items.SNOWBALL));
					item.setPickupDelay(40);
					world.spawnEntityInWorld(item);
					world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_SNOW_STEP, 
							player.getSoundCategory(), 1.0f, world.rand.nextFloat());
				}
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (SetEffect.registryNameContains(block, new String[] {"snow"}))
			return true;		
		return false;
	}
}