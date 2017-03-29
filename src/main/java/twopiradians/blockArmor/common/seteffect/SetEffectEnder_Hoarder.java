package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectEnder_Hoarder extends SetEffect {

	protected SetEffectEnder_Hoarder() {
		this.color = TextFormatting.DARK_PURPLE;
		this.description = "Provides access to your ender chest";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack && BlockArmor.key.isKeyDown(player)) {
			world.playSound(null, player.getPosition(), SoundEvents.BLOCK_ENDERCHEST_OPEN, 
					SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
			if (!(player.openContainer instanceof ContainerChest)) {
				player.displayGUIChest(player.getInventoryEnderChest());
				this.damageArmor(player, 1, false);
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {	
		if (SetEffect.registryNameContains(block, meta, new String[] {"ender"}) || block instanceof BlockEnderChest)
			return true;		
		return false; 
	}	
}
