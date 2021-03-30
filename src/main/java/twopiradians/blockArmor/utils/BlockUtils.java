package twopiradians.blockArmor.utils;

import java.lang.reflect.Field;

import com.google.common.collect.Lists;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import twopiradians.blockArmor.common.seteffect.SetEffect;

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

	/**Get light level for block*/
	@SuppressWarnings("deprecation")
	public static int getLightLevel(Block block) {
		int lightLevel = block.getDefaultState().getLightValue();
		if (lightLevel <= 0) {
			Material material = BlockUtils.getMaterial(block);
			if (SetEffect.registryNameContains(block, new String[] {"lamp"}) ||
					Lists.newArrayList(Material.REDSTONE_LIGHT).contains(material))
				lightLevel = 15;
		}
		return lightLevel;
	}



}