package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

public class SetEffectFalling extends SetEffect {

	private static final SetEffectDiving_Suit DIVING_SUIT = new SetEffectDiving_Suit();

	protected SetEffectFalling() {
		this.color = TextFormatting.GRAY;
		this.description = "Falls faster in air and sinks faster in water while sneaking";
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		//particles
		if (!world.isRemote && ((ItemBlockArmor)stack.getItem()).armorType == EntityEquipmentSlot.FEET &&
				world.rand.nextInt(16) == 0) {
			ArmorSet set = ArmorSet.getWornSet(player);
			if (set != null && set.block instanceof BlockFalling)
				((WorldServer)world).spawnParticle(EnumParticleTypes.FALLING_DUST, 
						player.posX, player.posY+1d,player.posZ, 
						1, 0.3f, 0.5f, 0.3f, 0, new int[] {Block.getStateId(set.block.getDefaultState())});
		}			
		//fall faster
		if (world.isRemote && ((ItemBlockArmor)stack.getItem()).armorType == EntityEquipmentSlot.FEET &&
				player.isSneaking() && Math.abs(player.motionY) < 3.5d && player.motionY < 0)
			player.motionY *= 1.3d;
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {	
		if (block instanceof BlockFalling || DIVING_SUIT.isValid(block, meta))
			return true;		
		return false;
	}
}