/*package twopiradians.blockArmor.common.seteffect;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectDJ extends SetEffect
{
	protected SetEffectDJ() {
		super();
		this.color = TextFormatting.PURPLE;
		this.description = "Plays a record from your inventory.";
		this.usesButton = true;
	}


	*//**Only called when player wearing full, enabled set*//*
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldownTracker().hasCooldown(stack.getItem()))
		{
			ArrayList<SoundEvent> records = new ArrayList<SoundEvent>();

			for (ItemStack invStack : player.inventory.mainInventory) 
				if (invStack != null && invStack.getItem() instanceof ItemRecord)
					records.add(((ItemRecord) invStack.getItem()).getSound());
			
			if (!BlockArmor.proxy.stopMovingSound(player, true)) {//FIXME do with packet - keep map of player/sounds here?
				SoundEvent soundEvent = records.get(player.world.rand.nextInt(records.size()));
				BlockArmor.proxy.startMovingSound(player, soundEvent, true);
				player.sendMessage(new TextComponentString("Started playing "+soundEvent.toString()+"."));
			}
			else
				player.sendMessage(new TextComponentString("Stopped playing record."));
			
			this.setCooldown(player, 10);
			this.damageArmor(player, 1, false);
		}
	}


	*//**Should block be given this set effect*//*
	@Override
	protected boolean isValid(Block block) {	
		if (SetEffect.registryNameContains(block, new String[] {"record", "jukebox"}))
			return true;		
		return false;
	}	
}
*/