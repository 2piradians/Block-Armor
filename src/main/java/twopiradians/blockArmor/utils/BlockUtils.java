package twopiradians.blockArmor.utils;

import java.lang.reflect.Field;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class BlockUtils {
	
	private static final Field MATERIAL_FIELD;
	
	static {
		MATERIAL_FIELD = ObfuscationReflectionHelper.findField(AbstractBlock.Properties.class, "field_149764_J");
		MATERIAL_FIELD.setAccessible(true);
	}
	
	/**Get block material*/
	public static Material getMaterial(Block block) {
		Properties prop = AbstractBlock.Properties.from(block);
		try {
			return (Material) MATERIAL_FIELD.get(prop);
		}
		catch (Exception e) {}
		return Material.AIR;
	}

	/**Get hardness of a block*/
	public static float getHardness(Block block) {
		float blockHardness = 0.5f;
		try {
			blockHardness = block.getDefaultState().getBlockHardness(null, BlockPos.ZERO);
		} catch(Exception e) {
			blockHardness = 0.5f;
		}
		return blockHardness;
	}



}