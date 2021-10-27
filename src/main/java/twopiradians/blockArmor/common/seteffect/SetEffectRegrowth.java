package twopiradians.blockArmor.common.seteffect;

import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.NetherrackBlock;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.IPlantable;
import twopiradians.blockArmor.utils.BlockUtils;

public class SetEffectRegrowth extends SetEffect {

	protected SetEffectRegrowth() {
		super();
		this.color = ChatFormatting.DARK_GREEN;
	}
	
	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);
		
		if (!world.isClientSide && world.random.nextInt(200) == 0 && stack.isDamaged())
			stack.setDamageValue(stack.getDamageValue()-1);
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {	
		// ignore netherrack (would be accepted otherwise bc it's IGrowable)
		if (block instanceof NetherrackBlock)
			return false;
		
		if (block instanceof BonemealableBlock || block instanceof IPlantable || 
				SetEffect.registryNameContains(block, new String[] {"moss", "plant", "mycelium", "mushroom", "flower",
						"log", "wood", "stem", "plank", "grass", "nether_wart"}))
			return true;	
		
		Material material = BlockUtils.getMaterial(block);
		if (Lists.newArrayList(Material.BAMBOO, Material.BAMBOO_SAPLING, Material.CACTUS, 
				Material.VEGETABLE, Material.LEAVES, Material.PLANT, Material.WATER_PLANT, Material.REPLACEABLE_FIREPROOF_PLANT,
				Material.WATER_PLANT, Material.REPLACEABLE_WATER_PLANT, Material.REPLACEABLE_PLANT, Material.NETHER_WOOD)
				.contains(material))
			return true;
		
		return false;
	}
}