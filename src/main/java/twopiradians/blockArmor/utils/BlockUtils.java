package twopiradians.blockArmor.utils;

import java.lang.reflect.Field;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import twopiradians.blockArmor.common.seteffect.SetEffect;

public class BlockUtils {

	/**Block properties -> material*/
	private static final Field MATERIAL_FIELD = ObfuscationReflectionHelper.findField(BlockBehaviour.Properties.class, "f_60882_");
	/**Block properties -> isSolid*/
	private static final Field IS_SOLID_FIELD = ObfuscationReflectionHelper.findField(BlockBehaviour.Properties.class, "f_60895_");
	/**Block properties -> requiresTool*/
	private static final Field REQUIRES_TOOL_FIELD = ObfuscationReflectionHelper.findField(BlockBehaviour.Properties.class, "f_60889_");
	
	/**Get block properties*/
	public static BlockBehaviour.Properties getProperties(Block block) {
		return BlockBehaviour.Properties.copy(block);
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
			blockHardness = block.defaultBlockState().getDestroySpeed(null, BlockPos.ZERO);
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
		int lightLevel = block.defaultBlockState().getLightEmission();
		if (lightLevel <= 0) {
			Material material = BlockUtils.getMaterial(block);
			if (SetEffect.registryNameContains(block, new String[] {"lamp"}) ||
					Lists.newArrayList(Material.BUILDABLE_GLASS).contains(material))
				lightLevel = 15;
		}
		return lightLevel;
	}



}