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

	/**Block properties -> material*/
	private static final Field MATERIAL_FIELD;
	/**Block properties -> isSolid*/
	private static final Field IS_SOLID_FIELD;
	/**Block properties -> requiresTool*/
	private static final Field REQUIRES_TOOL_FIELD;

	static {
		MATERIAL_FIELD = ObfuscationReflectionHelper.findField(AbstractBlock.Properties.class, "field_149764_J");
		MATERIAL_FIELD.setAccessible(true);
		IS_SOLID_FIELD = ObfuscationReflectionHelper.findField(AbstractBlock.Properties.class, "field_235707_k_");
		IS_SOLID_FIELD.setAccessible(true);
		REQUIRES_TOOL_FIELD = ObfuscationReflectionHelper.findField(AbstractBlock.Properties.class, "field_235806_h_");
		REQUIRES_TOOL_FIELD.setAccessible(true);
	}
	
	/**Get block properties*/
	public static AbstractBlock.Properties getProperties(Block block) {
		return AbstractBlock.Properties.from(block);
	}

	/**Get block material*/
	public static Material getMaterial(Block block) {
		try {
			return (Material) MATERIAL_FIELD.get(getProperties(block));
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

	/**Get blast resistance for block*/
	@SuppressWarnings("deprecation")
	public static float getBlastResistance(Block block) {
		return block.getExplosionResistance();
	}

	/**Get if block is solid*/
	public static boolean getIsSolid(Block block) {
		try {
			return IS_SOLID_FIELD.getBoolean(getProperties(block));
		}
		catch (Exception e) {
			return true;
		}
	}
	
	/**Get if block requires tool to be broken*/
	public static boolean getRequiresTool(Block block) {
		try {
			return REQUIRES_TOOL_FIELD.getBoolean(getProperties(block));
		}
		catch (Exception e) {
			return true;
		}
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