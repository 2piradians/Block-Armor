package twopiradians.blockArmor.common.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.OreBlock;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SilverfishBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.command.CommandDev;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.seteffect.SetEffect;
import twopiradians.blockArmor.creativetab.BlockArmorCreativeTab;
import twopiradians.blockArmor.utils.BlockUtils;

@Mod.EventBusSubscriber
@SuppressWarnings({ "deprecation" })
public class ArmorSet {

	/**Used to add ItemStacks that will be approved for sets that would otherwise not be valid*/
	private static final ArrayList<Block> MANUALLY_ADDED_SETS;
	static {
		MANUALLY_ADDED_SETS = new ArrayList<Block>() {{
			add(Blocks.SUGAR_CANE);
			add(Blocks.CACTUS);
			add(Blocks.DISPENSER);
			add(Blocks.DROPPER);
			add(Blocks.BEACON);
			add(Blocks.FURNACE);
			add(Blocks.ENCHANTING_TABLE);
			add(Blocks.COMMAND_BLOCK);
			add(Blocks.CHAIN_COMMAND_BLOCK);
			add(Blocks.REPEATING_COMMAND_BLOCK);
			add(Blocks.BROWN_MUSHROOM_BLOCK);
			add(Blocks.RED_MUSHROOM_BLOCK);
			add(Blocks.SOUL_SAND);
			add(Blocks.ENDER_CHEST);
			add(Blocks.BLAST_FURNACE);
			add(Blocks.COMPOSTER);
			add(Blocks.SMOKER);
			add(Blocks.BEE_NEST);
			add(Blocks.BEEHIVE);
			add(Blocks.BARREL);
			add(Blocks.HONEY_BLOCK);
			add(Blocks.CHEST);
			add(Blocks.NOTE_BLOCK);
			add(Blocks.JUKEBOX);
		}};
	}
	/**Armor slots*/
	public static final EquipmentSlotType[] SLOTS = new EquipmentSlotType[] 
			{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};
	/**Used to add Items that have overriding textures*/
	public static final HashMap<Block, TextureOverrideInfo> TEXTURE_OVERRIDES;
	static {
		TEXTURE_OVERRIDES = new HashMap<Block, TextureOverrideInfo>() {{
			TextureOverrideInfo info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.CHEST, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/enchanting_table_chest"));
			info.addSlot(EquipmentSlotType.LEGS, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/enchanting_table_legs"));
			put(Blocks.ENCHANTING_TABLE, info);
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/cactus_head"));
			info.addSlot(EquipmentSlotType.CHEST, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/cactus_chest"));
			info.addSlot(EquipmentSlotType.LEGS, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/cactus_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/cactus_feet"));
			put(Blocks.CACTUS, info);
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/sugar_cane_head"));
			info.addSlot(EquipmentSlotType.CHEST, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/sugar_cane_chest"));
			info.addSlot(EquipmentSlotType.LEGS, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/sugar_cane_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/sugar_cane_feet"));
			put(Blocks.SUGAR_CANE, info);
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/ender_chest_head"));
			info.addSlot(EquipmentSlotType.CHEST, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/ender_chest_chest"));
			info.addSlot(EquipmentSlotType.LEGS, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/ender_chest_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/ender_chest_feet"));
			put(Blocks.ENDER_CHEST, info);
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/chest_head"));
			info.addSlot(EquipmentSlotType.CHEST, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/chest_chest"));
			info.addSlot(EquipmentSlotType.LEGS, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/chest_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/chest_feet"));
			put(Blocks.CHEST, info);
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/composter_head"));
			put(Blocks.COMPOSTER, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/shulker_box_feet"));
			put(Blocks.SHULKER_BOX, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, ShulkerBoxBlock.getColorFromBlock(Blocks.WHITE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, ShulkerBoxBlock.getColorFromBlock(Blocks.WHITE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ShulkerBoxBlock.getColorFromBlock(Blocks.WHITE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, ShulkerBoxBlock.getColorFromBlock(Blocks.WHITE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_feet"));
			put(Blocks.WHITE_SHULKER_BOX, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, ShulkerBoxBlock.getColorFromBlock(Blocks.ORANGE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, ShulkerBoxBlock.getColorFromBlock(Blocks.ORANGE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ShulkerBoxBlock.getColorFromBlock(Blocks.ORANGE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, ShulkerBoxBlock.getColorFromBlock(Blocks.ORANGE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_feet"));
			put(Blocks.ORANGE_SHULKER_BOX, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, ShulkerBoxBlock.getColorFromBlock(Blocks.MAGENTA_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, ShulkerBoxBlock.getColorFromBlock(Blocks.MAGENTA_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ShulkerBoxBlock.getColorFromBlock(Blocks.MAGENTA_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, ShulkerBoxBlock.getColorFromBlock(Blocks.MAGENTA_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_feet"));
			put(Blocks.MAGENTA_SHULKER_BOX, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, ShulkerBoxBlock.getColorFromBlock(Blocks.LIGHT_BLUE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, ShulkerBoxBlock.getColorFromBlock(Blocks.LIGHT_BLUE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ShulkerBoxBlock.getColorFromBlock(Blocks.LIGHT_BLUE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, ShulkerBoxBlock.getColorFromBlock(Blocks.LIGHT_BLUE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_feet"));
			put(Blocks.LIGHT_BLUE_SHULKER_BOX, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, ShulkerBoxBlock.getColorFromBlock(Blocks.YELLOW_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, ShulkerBoxBlock.getColorFromBlock(Blocks.YELLOW_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ShulkerBoxBlock.getColorFromBlock(Blocks.YELLOW_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, ShulkerBoxBlock.getColorFromBlock(Blocks.YELLOW_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_feet"));
			put(Blocks.YELLOW_SHULKER_BOX, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, ShulkerBoxBlock.getColorFromBlock(Blocks.LIME_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, ShulkerBoxBlock.getColorFromBlock(Blocks.LIME_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ShulkerBoxBlock.getColorFromBlock(Blocks.LIME_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, ShulkerBoxBlock.getColorFromBlock(Blocks.LIME_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_feet"));
			put(Blocks.LIME_SHULKER_BOX, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, ShulkerBoxBlock.getColorFromBlock(Blocks.PINK_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, ShulkerBoxBlock.getColorFromBlock(Blocks.PINK_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ShulkerBoxBlock.getColorFromBlock(Blocks.PINK_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, ShulkerBoxBlock.getColorFromBlock(Blocks.PINK_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_feet"));
			put(Blocks.PINK_SHULKER_BOX, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, ShulkerBoxBlock.getColorFromBlock(Blocks.GRAY_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, ShulkerBoxBlock.getColorFromBlock(Blocks.GRAY_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ShulkerBoxBlock.getColorFromBlock(Blocks.GRAY_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, ShulkerBoxBlock.getColorFromBlock(Blocks.GRAY_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_feet"));
			put(Blocks.GRAY_SHULKER_BOX, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, ShulkerBoxBlock.getColorFromBlock(Blocks.LIGHT_GRAY_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, ShulkerBoxBlock.getColorFromBlock(Blocks.LIGHT_GRAY_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ShulkerBoxBlock.getColorFromBlock(Blocks.LIGHT_GRAY_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, ShulkerBoxBlock.getColorFromBlock(Blocks.LIGHT_GRAY_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_feet"));
			put(Blocks.LIGHT_GRAY_SHULKER_BOX, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, ShulkerBoxBlock.getColorFromBlock(Blocks.CYAN_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, ShulkerBoxBlock.getColorFromBlock(Blocks.CYAN_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ShulkerBoxBlock.getColorFromBlock(Blocks.CYAN_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, ShulkerBoxBlock.getColorFromBlock(Blocks.CYAN_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_feet"));
			put(Blocks.CYAN_SHULKER_BOX, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, ShulkerBoxBlock.getColorFromBlock(Blocks.PURPLE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, ShulkerBoxBlock.getColorFromBlock(Blocks.PURPLE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ShulkerBoxBlock.getColorFromBlock(Blocks.PURPLE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, ShulkerBoxBlock.getColorFromBlock(Blocks.PURPLE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_feet"));
			put(Blocks.PURPLE_SHULKER_BOX, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, ShulkerBoxBlock.getColorFromBlock(Blocks.BLUE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, ShulkerBoxBlock.getColorFromBlock(Blocks.BLUE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ShulkerBoxBlock.getColorFromBlock(Blocks.BLUE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, ShulkerBoxBlock.getColorFromBlock(Blocks.BLUE_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_feet"));
			put(Blocks.BLUE_SHULKER_BOX, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, ShulkerBoxBlock.getColorFromBlock(Blocks.BROWN_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, ShulkerBoxBlock.getColorFromBlock(Blocks.BROWN_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ShulkerBoxBlock.getColorFromBlock(Blocks.BROWN_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, ShulkerBoxBlock.getColorFromBlock(Blocks.BROWN_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_feet"));
			put(Blocks.BROWN_SHULKER_BOX, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, ShulkerBoxBlock.getColorFromBlock(Blocks.GREEN_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, ShulkerBoxBlock.getColorFromBlock(Blocks.GREEN_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ShulkerBoxBlock.getColorFromBlock(Blocks.GREEN_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, ShulkerBoxBlock.getColorFromBlock(Blocks.GREEN_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_feet"));
			put(Blocks.GREEN_SHULKER_BOX, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, ShulkerBoxBlock.getColorFromBlock(Blocks.RED_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, ShulkerBoxBlock.getColorFromBlock(Blocks.RED_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ShulkerBoxBlock.getColorFromBlock(Blocks.RED_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, ShulkerBoxBlock.getColorFromBlock(Blocks.RED_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_feet"));
			put(Blocks.RED_SHULKER_BOX, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, ShulkerBoxBlock.getColorFromBlock(Blocks.BLACK_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_head"));
			info.addSlot(EquipmentSlotType.CHEST, ShulkerBoxBlock.getColorFromBlock(Blocks.BLACK_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ShulkerBoxBlock.getColorFromBlock(Blocks.BLACK_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_legs"));
			info.addSlot(EquipmentSlotType.FEET, ShulkerBoxBlock.getColorFromBlock(Blocks.BLACK_SHULKER_BOX).getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_shulker_box_feet"));
			put(Blocks.BLACK_SHULKER_BOX, info);
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_head"));
			info.addSlot(EquipmentSlotType.CHEST, ((BedBlock)Blocks.WHITE_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ((BedBlock)Blocks.WHITE_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_feet"));
			put(Blocks.WHITE_BED, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_head"));
			info.addSlot(EquipmentSlotType.CHEST, ((BedBlock)Blocks.ORANGE_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ((BedBlock)Blocks.ORANGE_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_feet"));
			put(Blocks.ORANGE_BED, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_head"));
			info.addSlot(EquipmentSlotType.CHEST, ((BedBlock)Blocks.MAGENTA_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ((BedBlock)Blocks.MAGENTA_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_feet"));
			put(Blocks.MAGENTA_BED, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_head"));
			info.addSlot(EquipmentSlotType.CHEST, ((BedBlock)Blocks.LIGHT_BLUE_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ((BedBlock)Blocks.LIGHT_BLUE_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_feet"));
			put(Blocks.LIGHT_BLUE_BED, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_head"));
			info.addSlot(EquipmentSlotType.CHEST, ((BedBlock)Blocks.YELLOW_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ((BedBlock)Blocks.YELLOW_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_feet"));
			put(Blocks.YELLOW_BED, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_head"));
			info.addSlot(EquipmentSlotType.CHEST, ((BedBlock)Blocks.LIME_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ((BedBlock)Blocks.LIME_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_feet"));
			put(Blocks.LIME_BED, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_head"));
			info.addSlot(EquipmentSlotType.CHEST, ((BedBlock)Blocks.PINK_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ((BedBlock)Blocks.PINK_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_feet"));
			put(Blocks.PINK_BED, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_head"));
			info.addSlot(EquipmentSlotType.CHEST, ((BedBlock)Blocks.GRAY_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ((BedBlock)Blocks.GRAY_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_feet"));
			put(Blocks.GRAY_BED, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_head"));
			info.addSlot(EquipmentSlotType.CHEST, ((BedBlock)Blocks.LIGHT_GRAY_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ((BedBlock)Blocks.LIGHT_GRAY_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_feet"));
			put(Blocks.LIGHT_GRAY_BED, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_head"));
			info.addSlot(EquipmentSlotType.CHEST, ((BedBlock)Blocks.CYAN_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ((BedBlock)Blocks.CYAN_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_feet"));
			put(Blocks.CYAN_BED, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_head"));
			info.addSlot(EquipmentSlotType.CHEST, ((BedBlock)Blocks.PURPLE_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ((BedBlock)Blocks.PURPLE_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_feet"));
			put(Blocks.PURPLE_BED, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_head"));
			info.addSlot(EquipmentSlotType.CHEST, ((BedBlock)Blocks.BLUE_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ((BedBlock)Blocks.BLUE_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_feet"));
			put(Blocks.BLUE_BED, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_head"));
			info.addSlot(EquipmentSlotType.CHEST, ((BedBlock)Blocks.BROWN_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ((BedBlock)Blocks.BROWN_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_feet"));
			put(Blocks.BROWN_BED, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_head"));
			info.addSlot(EquipmentSlotType.CHEST, ((BedBlock)Blocks.GREEN_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ((BedBlock)Blocks.GREEN_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_feet"));
			put(Blocks.GREEN_BED, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_head"));
			info.addSlot(EquipmentSlotType.CHEST, ((BedBlock)Blocks.RED_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ((BedBlock)Blocks.RED_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_feet"));
			put(Blocks.RED_BED, info); 
			info = new TextureOverrideInfo();
			info.addSlot(EquipmentSlotType.HEAD, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_head"));
			info.addSlot(EquipmentSlotType.CHEST, ((BedBlock)Blocks.BLACK_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_chest"));
			info.addSlot(EquipmentSlotType.LEGS, ((BedBlock)Blocks.BLACK_BED).getColor().getColorValue(), new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_legs"));
			info.addSlot(EquipmentSlotType.FEET, -1, new ResourceLocation(BlockArmor.MODID, "items/overrides/white_bed_feet"));
			put(Blocks.BLACK_BED, info);
		}};
	}
	/**All sets, including disabled sets*/
	public static ArrayList<ArmorSet> allSets = Lists.newArrayList();
	/**All sets, mapped by their stack's display name*/
	public static HashMap<String, ArmorSet> nameToSetMap = Maps.newHashMap();
	/**All sets, mapped by their block's modid*/
	public static HashMap<String, TreeSet<ArmorSet>> modidToSetMap = Maps.newHashMap();

	/**Used to get textures, set recipe, and as repair material*/
	public ItemStack stack;
	public Item item;
	public Block block;
	public BlockArmorMaterial material;      
	public BlockArmorItem helmet;
	public BlockArmorItem chestplate;
	public BlockArmorItem leggings;
	public BlockArmorItem boots;
	public boolean isFromModdedBlock;
	public ArrayList<SetEffect> setEffects;
	public ArrayList<SetEffect> defaultSetEffects;
	public String modid;
	public String registryName;
	/**should only be modified through enable() and disable(); enabled = in creative tab and has recipe*/
	private boolean enabled;
	/**Only changed on client*/
	public boolean missingTextures; 
	/**Array of block's textures sorted by EquipmentSlotType id*/
	private TextureAtlasSprite[] sprites;
	/**Array of TextureAtlasSprite's animation (or null if not animated) sorted by EquipmentSlotType id*/
	public AnimationMetadataSection[] animations;
	/**Array of TextureAtlasSprite's current frame number (including decimals between frames) sorted by EquipmentSlotType id*/
	public float[] frames;
	/**Array of quad's color (or -1 if none) sorted by EquipmentSlotType id*/
	private int[] colors;
	/**Minecraft's default missing texture sprite, assigned in initTextures()*/
	public static TextureAtlasSprite missingSprite;
	// armor values calculated from block / config
	public float armorDamageReduction;
	public float armorToughness;
	public int armorDurability;
	public int armorEnchantability;
	public int armorKnockbackResistance;

	/**Map of player UUID to their worn set effects*/
	private static HashMap<UUID, HashSet<SetEffect>> playerSetEffectsClient = Maps.newHashMap();
	/**Map of player UUID to their worn set effects*/
	private static HashMap<UUID, HashSet<SetEffect>> playerSetEffectsServer = Maps.newHashMap();

	public ArmorSet(ItemStack stack) {
		this.stack = stack;
		this.item = stack.getItem();
		this.block = ((BlockItem) item).getBlock();
		this.registryName = ArmorSet.getItemStackRegistryName(this.stack);
		try {
			ResourceLocation loc = this.item.getRegistryName();
			this.modid = loc.getNamespace().toLowerCase();
			if (!this.modid.equals("minecraft"))
				isFromModdedBlock = true;
		}
		catch (Exception e) {
			this.modid = "???";
			isFromModdedBlock = true;
		}
		//calculate values for and set material
		boolean indestructible = BlockUtils.getHardness(block) == -1;
		float hardness = Math.min(BlockUtils.getHardness(block), 100);
		if (indestructible) 
			hardness = 100;
		float blastResistance = BlockUtils.getBlastResistance(block);
		boolean requiresTool = BlockUtils.getRequiresTool(block);
		boolean isSolid = BlockUtils.getIsSolid(block);
		//BlockArmor.LOGGER.info(getItemStackDisplayName(stack, null).getString()+": hardness = "+hardness+", blastResistance = "+blastResistance+", requiresTool = "+requiresTool+", isSolid = "+isSolid); 
		this.armorDamageReduction = hardness >= 8 ? (hardness * 0.003f + 4.5f) : (hardness * 0.65f); 
		if (requiresTool)
			this.armorDamageReduction *= 1.2f;
		if (!isSolid)
			this.armorDamageReduction *= 0.5f;
		if (indestructible)
			this.armorDamageReduction *= 20f;
		this.armorDamageReduction = Math.max((armorDamageReduction * 0.6f), 1);
		this.armorDurability = (int) (indestructible ? 0 : Math.min(30, armorDamageReduction * 8f));
		this.armorToughness = Math.min(20, blastResistance > 100 ? blastResistance / 400f : 0);
		this.armorEnchantability = 12;
		this.armorKnockbackResistance = 0;  // leaving 0 cuz of set effects that give it
		this.createMaterial();

		CommandDev.addBlockName(this); 
	}

	/**Create material for this set based on block values and update armor items with it (if they're created already)*/
	public void createMaterial() {
		int[] reductionAmounts = new int[] {(int) (armorDamageReduction), (int) (armorDamageReduction*2f), (int) (armorDamageReduction*2.5f), (int) (armorDamageReduction*1.45f)};
		this.material = new BlockArmorMaterial(getItemStackDisplayName(stack, null)+" Material", 
				armorDurability, reductionAmounts, armorEnchantability, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, armorToughness,
				armorKnockbackResistance, () -> {
					return Ingredient.fromItems(new IItemProvider[]{this.item});
				});
		for (EquipmentSlotType slot : SLOTS) {
			BlockArmorItem armor = this.getArmorForSlot(slot);
			if (armor != null)
				armor.setMaterial(material);
		}
		//BlockArmor.LOGGER.info(getItemStackDisplayName(stack, null).getString()+": blockHardness = "+armorDamageReduction+", toughness = "+armorToughness+", durability = "+armorDurability+", reductionAmounts = "+Arrays.toString(reductionAmounts)); 
	}

	/**Returns armor item for slot*/
	public BlockArmorItem getArmorForSlot(EquipmentSlotType slot) {
		switch(slot) {
		case HEAD:
			return helmet;
		case CHEST:
			return chestplate;
		case LEGS:
			return leggings;
		case FEET:
			return boots;
		default:
			return null;
		}
	}

	/**Creates ArmorSets for each valid registered item and puts them in allSets
	 * Have to use these items instead of looking through block registry or else
	 * creative tabs will have all modded blocks as air for some reason... (from using new ItemStack(block))*/
	public static void setup(Collection<Item> items) {
		//create list of all ItemStacks with different display names and list of the display names
		ArrayList<String> displayNames = new ArrayList<String>();
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		for (Item item : items) {
			try { 
				if (item instanceof BlockItem) {
					Block block = ((BlockItem)item).getBlock();
					ItemStack stack = new ItemStack(block);
					boolean manuallyAdded = false;
					for (Block manualBlock : MANUALLY_ADDED_SETS)
						if (block == manualBlock)
							manuallyAdded = true;

					if (manuallyAdded || (stack.getItem() != null && !stack.getDisplayName().getString().isEmpty() && 
							!displayNames.contains(stack.getDisplayName()))) {
						stacks.add(stack);
						displayNames.add(stack.getDisplayName().getString());
					}
				}
			} catch (Exception e) {continue;}
		}

		//creates list of names that the items will be registered with to prevent duplicates
		ArrayList<String> registryNames = new ArrayList<String>();

		//checks list of ItemStacks for valid ones and creates set and adds to allSets
		allSets = new ArrayList<ArmorSet>();
		nameToSetMap = Maps.newHashMap();
		modidToSetMap = Maps.newHashMap();
		for (ItemStack stack : stacks) {
			if (isValid(stack) && ArmorSet.getSet(stack.getItem()) == null) {
				String registryName = getItemStackRegistryName(stack);
				if (!registryNames.contains(registryName) && !registryName.isEmpty()) {
					try {
						ArmorSet set = new ArmorSet(stack);
						allSets.add(set);
						nameToSetMap.put(set.registryName, set);
						TreeSet<ArmorSet> list = modidToSetMap.containsKey(set.modid) ? modidToSetMap.get(set.modid) : Sets.newTreeSet(new Comparator<ArmorSet>() {
							@Override
							public int compare(ArmorSet s1, ArmorSet s2) {
								return s1.registryName.compareTo(s2.registryName);
							}
						});
						list.add(set);
						modidToSetMap.put(set.modid, list);
						registryNames.add(registryName);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**Returns TextureAtlasSprite corresponding to given ItemModArmor*/
	@OnlyIn(Dist.CLIENT)
	public static TextureAtlasSprite getSprite(BlockArmorItem item) {		
		if (item != null) {
			if (item.set.sprites == null)
				item.set.initTextures();
			TextureAtlasSprite sprite = item.set.sprites[item.getEquipmentSlot().getIndex()];
			return sprite == null ? missingSprite : sprite;
		}
		else
			return missingSprite;
	}

	/**Returns current alpha for animation overlay corresponding to given ItemModArmor*/
	public static float getAlpha(BlockArmorItem item) {
		if (item != null) {
			float frame = item.set.frames[item.getEquipmentSlot().getIndex()];
			return frame - (int) frame;
		}
		else
			return 0;
	}

	/**Returns current animation frame corresponding to given ItemModArmor*/
	public static int getCurrentAnimationFrame(BlockArmorItem item) {
		AnimationMetadataSection animation;
		if (item != null && (animation = item.set.animations[item.getEquipmentSlot().getIndex()]) != null) {
			int frame = (int) item.set.frames[item.getEquipmentSlot().getIndex()];
			return animation.getFrameIndex(frame);
		}
		else
			return 0;
	}

	/**Returns next animation frame corresponding to given ItemModArmor*/
	public static int getNextAnimationFrame(BlockArmorItem item) {
		AnimationMetadataSection animation;
		if (item != null && (animation = item.set.animations[item.getEquipmentSlot().getIndex()]) != null) {
			int frame = (int) item.set.frames[item.getEquipmentSlot().getIndex()];
			if (frame++ >= animation.getFrameCount()-1)
				frame -= animation.getFrameCount();
			return animation.getFrameIndex(frame);
		}
		else
			return 0;
	}

	/**Returns color corresponding to given ItemModArmor*/
	public static int getColor(BlockArmorItem item) {
		if (item != null)
			return item.set.colors[item.getEquipmentSlot().getIndex()];
		else
			return -1;
	}

	/**Used to uniformly create registry name*/
	public static String getItemStackRegistryName(ItemStack stack) {
		try {
			String registryName = stack.getItem().getRegistryName().getPath().toLowerCase().replace(" ", "_");
			return registryName;
		} 
		catch (Exception e) {
			return "";
		}
	}

	/**Change display name based on the block*/
	public static ITextComponent getItemStackDisplayName(ItemStack stack, EquipmentSlotType slot)	{
		String name;
		if (stack != null && stack.getItem() instanceof BlockArmorItem) {
			name = ((BlockArmorItem) stack.getItem()).set.stack.getDisplayName().getString();
		}
		else if (stack != null && stack.getItem() != null)
			name = stack.getDisplayName().getString();
		else
			name = "";

		//manually set display names
		name = name.replace("Block of ", "") 
				.replace("Block ", "")
				.replace(" Block", "")
				.replace("Sugar Canes", "Sugar Cane")
				.replace("Bricks", "Brick")
				.replace("Planks", "Plank");

		name = TextFormatting.getTextWithoutFormattingCodes(name);

		if (slot != null)
			switch (slot) {
			case HEAD:
				name += " Helmet";
				break;
			case CHEST:
				name += " Chestplate";
				break;
			case LEGS:
				name += " Leggings";
				break;
			case FEET:
				name += " Boots";
				break;
			default:
				break;
			}

		return new StringTextComponent(name);
	}

	/**Returns first piece of armor of the entity's worn set, or null*/
	@Nullable
	public static ItemStack getFirstSetItem(LivingEntity entity, SetEffect effect) {
		ArrayList<ItemStack> stacks = getAllSetItems(entity, effect);
		return stacks.isEmpty() ? null : stacks.get(0);
	}

	/**Returns all pieces of armor of the entity's worn set, or null*/
	@Nullable
	public static ArrayList<ItemStack> getAllSetItems(LivingEntity entity, SetEffect effect) {
		ArrayList<ItemStack> ret = Lists.newArrayList();
		if (effect != null && getWornSetEffects(entity).contains(effect))
			for (EquipmentSlotType slot : SLOTS) {
				ItemStack stack = entity.getItemStackFromSlot(slot);
				if (stack != null && stack.getItem() instanceof BlockArmorItem &&
						((BlockArmorItem)stack.getItem()).set.setEffects.contains(effect))
					ret.add(stack);
			}
		return ret;
	}

	/**Returns the active set effects of the armor that the entity is wearing*/
	public static HashSet<SetEffect> getWornSetEffects(LivingEntity entity) {
		return entity != null && getPlayerSetEffects(entity.world.isRemote).containsKey(entity.getUniqueID()) ?
				getPlayerSetEffects(entity.world.isRemote).get(entity.getUniqueID()) : Sets.newHashSet();
	}

	/**Does this entity have this worn set effect type active*/
	public static boolean hasSetEffect(LivingEntity entity, SetEffect effect) {
		for (SetEffect effect2 : getWornSetEffects(entity))
			if (effect.getClass() == effect2.getClass())
				return true;
		return false;
	}

	/**Returns armor set corresponding to given block and meta, or null if none exists*/
	public static ArmorSet getSet(Block block) {
		for (ArmorSet set : allSets)
			if (set.block == block)
				return set;
		return null;
	}

	/**Returns armor set corresponding to given item, or null if none exists*/
	public static ArmorSet getSet(Item item) {
		for (ArmorSet set : allSets)
			if (set.item == item)
				return set;
		return null;
	}

	/**Should an armor set be made from this item*/
	private static boolean isValid(ItemStack stack) {
		try {			
			// not BlockItem
			if (stack == null || !(stack.getItem() instanceof BlockItem))
				return false;
			BlockItem item = (BlockItem) stack.getItem();
			Block block = item.getBlock();
			// manually added blocks (only vanilla cuz we're overriding the textures)
			if ((block instanceof ShulkerBoxBlock ||
					block instanceof BedBlock) && 
					block.getRegistryName().getNamespace().equals("minecraft"))
				return true;
			for (Block manualBlock : MANUALLY_ADDED_SETS)
				if (stack != null && (item == manualBlock.asItem()))
					return true;
			// bad modded item, ore/ingot, or unnamed
			if (item.getRegistryName().getNamespace().contains("one_point_twelve_concrete") ||
					item.getRegistryName().getNamespace().contains("railcraft") ||
					item.getRegistryName().getNamespace().contains("ore") || 
					item.getRegistryName().getNamespace().contains("ingot") || 
					stack.getDisplayName().getString().contains(".name") || 
					stack.getDisplayName().getString().contains("Ore") ||
					stack.getDisplayName().getString().contains("%") || 
					stack.getDisplayName().getString().contains("Ingot"))
				return false;
			// bad blocks
			if (block instanceof FlowingFluidBlock || 
					block instanceof ContainerBlock || 
					block.hasTileEntity(block.getDefaultState()) || 
					block instanceof OreBlock || 
					block instanceof CropsBlock || 
					block instanceof BushBlock ||
					block == Blocks.BARRIER || 
					block instanceof SlabBlock || 
					block instanceof SilverfishBlock ||
					block.getRenderType(block.getDefaultState()) != BlockRenderType.MODEL ||
					block == Blocks.IRON_BLOCK || 
					block == Blocks.GOLD_BLOCK || 
					block == Blocks.DIAMOND_BLOCK ||
					block == Blocks.AIR ||
					block == Blocks.SNOW ||
					block == Blocks.NETHERITE_BLOCK)
				return false;
			// bad modded items
			String registryName = block.getRegistryName().toString();
			if (registryName.equalsIgnoreCase("evilcraft:darkBlock") || 
					registryName.equalsIgnoreCase("evilcraft:obscuredGlass") ||
					registryName.equalsIgnoreCase("evilcraft:hardenedBlood") ||
					registryName.equalsIgnoreCase("evilcraft:darkPowerGemBlock") ||
					registryName.equalsIgnoreCase("darkutils:filter") || 
					registryName.equalsIgnoreCase("darkutils:filter_inverted") ||
					registryName.equalsIgnoreCase("agriculturalrevolution:rustedanalyser") ||
					registryName.equalsIgnoreCase("agriculturalrevolution:rustedbot") ||
					registryName.equalsIgnoreCase("agriculturalrevolution:rustedmutationstation") ||
					registryName.equalsIgnoreCase("agriculturalrevolution:rustedinscriber") ||
					registryName.equalsIgnoreCase("agriculturalrevolution:rustedhydrophonic") ||
					registryName.equalsIgnoreCase("agriculturalrevolution:rustedresearch") ||
					registryName.equalsIgnoreCase("agriculturalrevolution:rustedpipe") ||
					registryName.equalsIgnoreCase("agriculturalrevolution:rustedironscaff") ||
					registryName.equalsIgnoreCase("tconstruct:clear_glass"))
				return false;

			//Check if full block
			if (block.getDefaultState().getCollisionShape(null, BlockPos.ZERO, ISelectionContext.dummy()) != VoxelShapes.fullCube())
				return false;

			return true;
		}
		catch (Exception e) { return false; }
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	/**Adds set items to creative tab and adds recipes*/
	public boolean enable() {
		if (this.enabled || this.missingTextures) //don't enable sets with missing textures
			return false;
		else
			this.enabled = true;

		BlockArmorItem[] armors = new BlockArmorItem[] {this.helmet, this.chestplate, this.leggings, this.boots};
		for (BlockArmorItem armor : armors) {
			if (armor != null) {
				//add to tab
				if (isFromModdedBlock) {
					if (BlockArmorCreativeTab.moddedTab == null)
						BlockArmorCreativeTab.moddedTab = new BlockArmorCreativeTab("blockArmorModded");
					BlockArmorCreativeTab.moddedTab.orderedStacks.add(new ItemStack(armor));
					armor.group = BlockArmorCreativeTab.moddedTab;
				}
				else {
					if (BlockArmorCreativeTab.vanillaTab == null)
						BlockArmorCreativeTab.vanillaTab = new BlockArmorCreativeTab("blockArmorVanilla");
					BlockArmorCreativeTab.vanillaTab.orderedStacks.add(new ItemStack(armor));
					armor.group = BlockArmorCreativeTab.vanillaTab;
				}
			}
		}

		return true;
	}

	/**Remove set items from creative tab and removes recipes*/
	public boolean disable() {
		if (this.enabled)
			this.enabled = false;
		else
			return false;

		BlockArmorItem[] armors = new BlockArmorItem[] {this.helmet, this.chestplate, this.leggings, this.boots};
		for (BlockArmorItem armor : armors) {
			//remove from creative tab
			armor.group = null;

			//remove from vanilla tab
			if (BlockArmorCreativeTab.vanillaTab != null && BlockArmorCreativeTab.vanillaTab.orderedStacks != null)
				for (ItemStack tabStack : BlockArmorCreativeTab.vanillaTab.orderedStacks)
					if (tabStack.getItem() == armor) {
						BlockArmorCreativeTab.vanillaTab.orderedStacks.remove(tabStack);
						break;
					}

			//remove from modded tab
			if (BlockArmorCreativeTab.moddedTab != null && BlockArmorCreativeTab.moddedTab.orderedStacks != null)
				for (ItemStack tabStack : BlockArmorCreativeTab.moddedTab.orderedStacks)
					if (tabStack.getItem() == armor) {
						BlockArmorCreativeTab.moddedTab.orderedStacks.remove(tabStack);
						break;
					}
		}

		return true;
	}

	/**Initialize set's texture variable*/
	@OnlyIn(Dist.CLIENT)
	public Tuple<Integer, Boolean> initTextures() {
		boolean missingTextures = false;

		if (missingSprite == null)
			missingSprite = MissingTextureSprite
			.create(new AtlasTexture(MissingTextureSprite.getLocation()), 0, 16, 16, 0, 0);

		int numTextures = 0;
		this.sprites = new TextureAtlasSprite[EquipmentSlotType.values().length];
		this.animations = new AnimationMetadataSection[EquipmentSlotType.values().length];
		this.frames = new float[EquipmentSlotType.values().length];
		this.colors = new int[EquipmentSlotType.values().length];
		for (int i=0; i<colors.length; i++)
			this.colors[i] = -1;
		BlockState state = this.block.getDefaultState();
		// state overrides
		if (this.block == Blocks.REDSTONE_LAMP)
			state = state.with(RedstoneLampBlock.LIT, true);

		//Gets textures from item model's BakedQuads (textures for each side)
		List<BakedQuad> list = new ArrayList<BakedQuad>();
		try {
			IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);

			Random rand = new Random();
			//getting quads may throw exception if a mod's modeler doesn't obey @Nullable
			list.addAll(model.getQuads(state, null, rand));
			for (Direction facing : Direction.values()) 
				list.addAll(model.getQuads(state, facing, rand));

			for (BakedQuad quad : list) {
				TextureAtlasSprite sprite = quad.getSprite();
				AnimationMetadataSection animation = (AnimationMetadataSection) (sprite.getFrameCount() > 1 ? ObfuscationReflectionHelper.getPrivateValue(TextureAtlasSprite.class, sprite, "field_110982_k") : null); //animationMetadata
				int color = quad.hasTintIndex() ? Minecraft.getInstance().getItemColors().getColor(this.stack, quad.getTintIndex()) : -1;

				if (sprite.getName().toString().contains("overlay")) //overlays not supported by forge so we can't account for them
					continue;

				if (quad.getFace() == Direction.UP) { //top
					if (sprite != missingSprite)
						numTextures++;
					this.sprites[EquipmentSlotType.HEAD.getIndex()] = sprite;
					this.animations[EquipmentSlotType.HEAD.getIndex()] = animation;
					this.colors[EquipmentSlotType.HEAD.getIndex()] = color;
				}
				else if (quad.getFace() == Direction.NORTH) { //front
					if (sprite != missingSprite)
						numTextures++;
					this.sprites[EquipmentSlotType.CHEST.getIndex()] = sprite;
					this.animations[EquipmentSlotType.CHEST.getIndex()] = animation;
					this.colors[EquipmentSlotType.CHEST.getIndex()] = color;
				}
				else if (quad.getFace() == Direction.SOUTH) { //back
					if (sprite != missingSprite)
						numTextures++;
					this.sprites[EquipmentSlotType.LEGS.getIndex()] = sprite;
					this.animations[EquipmentSlotType.LEGS.getIndex()] = animation;
					this.colors[EquipmentSlotType.LEGS.getIndex()] = color;
				}
				else if (quad.getFace() == Direction.DOWN) { //bottom
					if (sprite != missingSprite)
						numTextures++;
					this.sprites[EquipmentSlotType.FEET.getIndex()] = sprite;
					this.animations[EquipmentSlotType.FEET.getIndex()] = animation;
					this.colors[EquipmentSlotType.FEET.getIndex()] = color;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		//Check for block texture overrides
		if (TEXTURE_OVERRIDES.containsKey(block)) 
			for (EquipmentSlotType slot : TEXTURE_OVERRIDES.get(block).overrides.keySet()) {
				ResourceLocation shortLoc = TEXTURE_OVERRIDES.get(block).overrides.get(slot).shortLoc;
				ResourceLocation longLoc = TEXTURE_OVERRIDES.get(block).overrides.get(slot).longLoc;
				try {
					// look for override texture
					Minecraft.getInstance().getResourceManager().getResource(longLoc); //does texture exist?
					TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).getSprite(shortLoc);
					this.sprites[slot.getIndex()] = sprite;
					this.colors[slot.getIndex()] = TEXTURE_OVERRIDES.get(block).overrides.get(slot).color;
					this.animations[slot.getIndex()] = null;
					//BlockArmor.LOGGER.info("Override texture for "+this.stack.getDisplayName().getString()+" "+slot.getName()+" found at: "+longLoc);
				} catch (Exception e) {
					BlockArmor.LOGGER.error("Override texture for "+this.stack.getDisplayName().getString()+" "+slot.getName()+" NOT found at: "+longLoc); 
				}
			}

		//If a sprite is missing, disable the set
		if (this.sprites[EquipmentSlotType.HEAD.getIndex()] == null || 
				this.sprites[EquipmentSlotType.CHEST.getIndex()] == null || 
				this.sprites[EquipmentSlotType.LEGS.getIndex()] == null || 
				this.sprites[EquipmentSlotType.FEET.getIndex()] == null ||
				this.sprites[EquipmentSlotType.HEAD.getIndex()] == missingSprite ||
				this.sprites[EquipmentSlotType.CHEST.getIndex()] == missingSprite ||
				this.sprites[EquipmentSlotType.LEGS.getIndex()] == missingSprite || 
				this.sprites[EquipmentSlotType.FEET.getIndex()] == missingSprite) 
			missingTextures = true;

		return new Tuple(numTextures, missingTextures);
	}

	// ================== WORN SET EFFECTS =================

	private static HashMap<UUID, HashSet<SetEffect>> getPlayerSetEffects(boolean isRemote) {
		return isRemote ? playerSetEffectsClient : playerSetEffectsServer;
	}

	/**Call onStop for set effects on logout - only called serverside in SSP*/
	@SubscribeEvent
	public static void onEvent(PlayerEvent.PlayerLoggedOutEvent event) {
		HashMap<UUID, HashSet<SetEffect>> playerSetEffects = getPlayerSetEffects(event.getPlayer().world.isRemote);
		if (playerSetEffects.containsKey(event.getPlayer().getUniqueID())) {
			for (SetEffect effect : playerSetEffects.get(event.getPlayer().getUniqueID()))
				effect.onStop(event.getPlayer());
			playerSetEffects.remove(event.getPlayer().getUniqueID());
		}
	}

	/**Update player set effects each tick*/
	@SubscribeEvent
	public static void onEvent(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START && Minecraft.getInstance().world != null)
			updateWornSetEffects(getPlayerSetEffects(true), Minecraft.getInstance().world.getPlayers());
	}

	/**Update player set effects each tick*/
	@SubscribeEvent
	public static void onEvent(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.START)
			updateWornSetEffects(getPlayerSetEffects(false), ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()); 
	}

	/**Update player set effects each tick for efficiency and onStart and onStop*/
	private static void updateWornSetEffects(HashMap<UUID, HashSet<SetEffect>> playerSetEffects, 
			List<? extends PlayerEntity> onlinePlayers) {
		for (PlayerEntity player : onlinePlayers) {
			// get old/new effect lists
			HashSet<SetEffect> oldEffects = playerSetEffects.get(player.getUniqueID());
			if (oldEffects == null)
				oldEffects = Sets.newHashSet();
			HashSet<SetEffect> newEffects = calculateWornSetEffects(player);
			// new effects not in old -> onStart
			for (SetEffect newEffect : newEffects)
				if (!oldEffects.contains(newEffect)) 
					newEffect.onStart(player);
			// old effects not in new -> onStop
			for (SetEffect oldEffect : oldEffects) 
				if (!newEffects.contains(oldEffect)) 
					oldEffect.onStop(player);
			// update set effects
			playerSetEffects.put(player.getUniqueID(), newEffects);
		}
	}

	/**Returns the active set effects of the armor that the entity is wearing
	 * to be cached every tick*/
	private static HashSet<SetEffect> calculateWornSetEffects(LivingEntity entity) {
		HashSet<SetEffect> effects = Sets.newHashSet();
		HashMap<String, Tuple<SetEffect, Integer>> setCounts = Maps.newHashMap();
		if (entity != null) {
			for (EquipmentSlotType slot : SLOTS) {
				ItemStack stack = entity.getItemStackFromSlot(slot);
				if (stack != null && stack.getItem() instanceof BlockArmorItem) {
					BlockArmorItem armor = (BlockArmorItem) stack.getItem();
					for (SetEffect effect : armor.set.setEffects) {
						if (effect.isEnabled()) {
							Tuple t = null;
							for (String description : setCounts.keySet())
								if (description.equals(effect.description))
									t = setCounts.get(effect.description);
							if (t != null)
								setCounts.put(effect.description, new Tuple(t.getA(), ((Integer)t.getB())+1));
							else
								setCounts.put(effect.description, new Tuple(effect, 1));
						}
					}
				}
			}
			for (String description : setCounts.keySet())
				if (setCounts.get(description).getB() >= Config.piecesForSet) 
					effects.add(setCounts.get(description).getA());
		}
		return effects;
	}

}