package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class SetEffectRegrowth extends SetEffect {

	protected SetEffectRegrowth() {
		this.color = TextFormatting.GREEN;
		this.description = "Slowly regrows and repairs durability";
	}
	
	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);
		
		if (!world.isRemote && world.rand.nextInt(200) == 0 && stack.isItemDamaged())
			stack.setItemDamage(stack.getItemDamage()-1);
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (block instanceof IGrowable || block instanceof IPlantable)
			return true;	
		
		try {
			if (block.isWood(null, BlockPos.ORIGIN))
				return true;
		} catch (Exception e) {}
		
		return false;
	}
}