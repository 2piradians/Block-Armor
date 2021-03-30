package twopiradians.blockArmor.common.seteffect;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.NetherrackBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import twopiradians.blockArmor.utils.BlockUtils;

public class SetEffectRegrowth extends SetEffect {

	protected SetEffectRegrowth() {
		this.color = TextFormatting.DARK_GREEN;
		this.description = "Slowly regrows and repairs durability";
	}
	
	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);
		
		if (!world.isRemote && world.rand.nextInt(200) == 0 && stack.isDamaged())
			stack.setDamage(stack.getDamage()-1);
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {	
		// ignore netherrack (would be accepted otherwise bc it's IGrowable)
		if (block instanceof NetherrackBlock)
			return false;
		
		if (block instanceof IGrowable || block instanceof IPlantable || 
				SetEffect.registryNameContains(block, new String[] {"moss", "plant", "mycelium", "mushroom", "flower",
						"log", "wood", "stem", "plank"}))
			return true;	
		
		Material material = BlockUtils.getMaterial(block);
		if (Lists.newArrayList(Material.BAMBOO, Material.BAMBOO_SAPLING, Material.CACTUS, Material.CORAL,
				Material.GOURD, Material.LEAVES, Material.PLANTS, Material.OCEAN_PLANT, Material.NETHER_PLANTS,
				Material.OCEAN_PLANT, Material.SEA_GRASS, Material.TALL_PLANTS)
				.contains(material))
			return true;
		
		return false;
	}
}