package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

public class SetEffectExperience_Giving extends SetEffect {

	protected SetEffectExperience_Giving() {
		this.color = TextFormatting.GREEN;
		this.description = "Gives experience over time";
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (((ItemBlockArmor)stack.getItem()).armorType == EntityEquipmentSlot.FEET &&
				!world.isRemote && !player.getCooldownTracker().hasCooldown(stack.getItem())) {
			this.setCooldown(player, 50);
			player.addExperience(1);
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {	
		if (SetEffect.registryNameContains(block, new String[] {"lapis", "enchant", "experience"}))
			return true;		
		return false;
	}
}