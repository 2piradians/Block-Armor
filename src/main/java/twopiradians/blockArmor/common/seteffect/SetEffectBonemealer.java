package twopiradians.blockArmor.common.seteffect;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

public class SetEffectBonemealer extends SetEffect {

	protected SetEffectBonemealer() {
		this.color = TextFormatting.WHITE;
		this.description = "Applies bonemeal to nearby blocks";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && ArmorSet.getFirstSetItem(player) == stack &&
				BlockArmor.key.isKeyDown(player) && !player.getCooldownTracker().hasCooldown(stack.getItem())) {
			int radius = 2;
			ArrayList<BlockPos> bonemealed = new ArrayList<BlockPos>();
			for (int x=-radius; x<radius; x++)
				for (int y=-radius; y<radius; y++)
					for (int z=-radius; z<radius; z++)
						if (ItemDye.applyBonemeal(new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getMetadata()), 
								world, player.getPosition().add(x, y, z), player)) 
							bonemealed.add(player.getPosition().add(x, y, z));
			if (!bonemealed.isEmpty()) {
				this.setCooldown(player, 100);
				for (BlockPos pos : bonemealed)
					((WorldServer)world).spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, 
							pos.getX(), pos.getY()+1d, pos.getZ(), 10, 2, 0.1d, 2, 0, new int[0]);
				world.playSound(null, player.getPosition(), SoundEvents.ITEM_HOE_TILL, 
						SoundCategory.PLAYERS, 0.5f, world.rand.nextFloat()+0.5f);
				for (EntityEquipmentSlot slot : ArmorSet.SLOTS) {
					ItemStack armor = player.getItemStackFromSlot(slot);
					if (armor != null && armor.getItem() instanceof ItemBlockArmor && 
							((ItemBlockArmor)armor.getItem()).set.setEffects.contains(this))
						armor.damageItem(1, player);
				}
			}
			else
				this.setCooldown(player, 20);
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (SetEffect.registryNameContains(block, new String[] {"bone"}))
			return true;
		return false;
	}
}