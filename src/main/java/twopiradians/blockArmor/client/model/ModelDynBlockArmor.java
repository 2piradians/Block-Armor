package twopiradians.blockArmor.client.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.ModelStateComposition;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

public final class ModelDynBlockArmor implements IModel, IModelCustomData, IRetexturableModel
{
	public static final ModelResourceLocation LOCATION = new ModelResourceLocation(BlockArmor.MODID+":block_armor", "inventory");

	// minimal Z offset to prevent depth-fighting
	private static final float NORTH_Z_BASE = 7.496f / 16f;
	private static final float SOUTH_Z_BASE = 8.503f / 16f;
	private static final float NORTH_Z_FLUID = 7.498f / 16f;
	private static final float SOUTH_Z_FLUID = 8.502f / 16f;

	public static final IModel MODEL = new ModelDynBlockArmor();

	public ModelDynBlockArmor()
	{

	}

	@Override
	public Collection<ResourceLocation> getDependencies()
	{
		return ImmutableList.of();
	}

	@Override
	public Collection<ResourceLocation> getTextures()
	{
		ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
		return builder.build();
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,	Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
	{
		return new BakedDynBlockArmor(format);
	}

	@Override
	public IModelState getDefaultState()
	{
		return TRSRTransformation.identity();
	}

	@Override
	public ModelDynBlockArmor process(ImmutableMap<String, String> customData)
	{
		return new ModelDynBlockArmor();
	}

	@Override
	public ModelDynBlockArmor retexture(ImmutableMap<String, String> textures)
	{
		return new ModelDynBlockArmor();
	}

	public enum LoaderDynBlockArmor implements ICustomModelLoader
	{
		INSTANCE;

		@Override
		public boolean accepts(ResourceLocation modelLocation)
		{
			return (modelLocation.getResourceDomain().equals(BlockArmor.MODID) && (modelLocation.getResourcePath().contains("helmet") 
					|| modelLocation.getResourcePath().contains("chestplate") || modelLocation.getResourcePath().contains("leggings") ||
					modelLocation.getResourcePath().contains("boots")));
		}

		@Override
		public IModel loadModel(ResourceLocation modelLocation)
		{
			return MODEL;
		}

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager)
		{

		}
	}

	public static final class BakedDynBlockArmorOverrideHandler extends ItemOverrideList
	{
		private static HashMap<Item, ImmutableList<BakedQuad>> itemQuadsMap = Maps.newHashMap();

		public static final BakedDynBlockArmorOverrideHandler INSTANCE = new BakedDynBlockArmorOverrideHandler();
		private BakedDynBlockArmorOverrideHandler()
		{
			super(ImmutableList.<ItemOverride>of());
		}

		/**Creates inventory icons (via quads) for each Block Armor piece and adds to itemQuadsMap*/
		public static int createInventoryIcons() {
			ModelDynBlockArmor.BakedDynBlockArmorOverrideHandler.itemQuadsMap = Maps.newHashMap();
			int numIcons = 0;
			
			ImmutableMap.Builder<TransformType, TRSRTransformation> builder2 = ImmutableMap.builder();
			builder2.put(TransformType.GROUND, new TRSRTransformation(new Vector3f(0.25f, 0.375f, 0.25f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f), new Vector3f(0.5f, 0.5f, 0.5f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.HEAD, new TRSRTransformation(new Vector3f(1.0f, 0.8125f, 1.4375f), new Quat4f(0.0f, 1.0f, 0.0f, -4.371139E-8f), new Vector3f(1.0f, 1.0f, 1.0f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.FIRST_PERSON_RIGHT_HAND, new TRSRTransformation(new Vector3f(0.910625f, 0.24816513f, 0.40617055f), new Quat4f(-0.15304594f, -0.6903456f, 0.15304594f, 0.6903456f), new Vector3f(0.68000007f, 0.68000007f, 0.68f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.FIRST_PERSON_LEFT_HAND, new TRSRTransformation(new Vector3f(0.910625f, 0.24816513f, 0.40617055f), new Quat4f(-0.15304594f, -0.6903456f, 0.15304594f, 0.6903456f), new Vector3f(0.68000007f, 0.68000007f, 0.68f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.THIRD_PERSON_RIGHT_HAND, new TRSRTransformation(new Vector3f(0.225f, 0.4125f, 0.2875f), new Quat4f(0.0f, 0.0f, 0.0f, 0.99999994f), new Vector3f(0.55f, 0.55f, 0.55f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.THIRD_PERSON_LEFT_HAND, new TRSRTransformation(new Vector3f(0.225f, 0.4125f, 0.2875f), new Quat4f(0.0f, 0.0f, 0.0f, 0.99999994f), new Vector3f(0.55f, 0.55f, 0.55f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			ImmutableMap<TransformType, TRSRTransformation> transformMap = builder2.build();
			
			for (ArmorSet set : ArmorSet.allSets) {
				ItemBlockArmor[] armor = new ItemBlockArmor[] {set.helmet, set.chestplate, set.leggings, set.boots};
				for (ItemBlockArmor item : armor) {
					//Initialize variables
					TextureAtlasSprite sprite = ArmorSet.getSprite(item);
					ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
					IModelState state = new SimpleModelState(transformMap);
					TRSRTransformation transform = TRSRTransformation.identity();
					state = new ModelStateComposition(state, transform);
					VertexFormat format = DefaultVertexFormats.ITEM;
					String armorType = "";
					EntityEquipmentSlot slot = item.getEquipmentSlot();
					if (slot == EntityEquipmentSlot.HEAD)
						armorType = "helmet";
					else if (slot == EntityEquipmentSlot.CHEST)
						armorType = "chestplate";
					else if (slot == EntityEquipmentSlot.LEGS)
						armorType = "leggings";
					else if (slot == EntityEquipmentSlot.FEET)
						armorType = "boots";
					
					//Block texture background
					//builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, NORTH_Z_BASE, sprite, EnumFacing.NORTH, 0xffffffff));
					//builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, SOUTH_Z_BASE, sprite, EnumFacing.SOUTH, 0xffffffff));	            
						
					//Base texture and model
					ResourceLocation baseLocation = new ResourceLocation(BlockArmor.MODID+":items/icons/block_armor_"+armorType+"_base");
					IBakedModel model = (new ItemLayerModel(ImmutableList.of(baseLocation))).bake(state, format, new Function<ResourceLocation, TextureAtlasSprite>() {
						public TextureAtlasSprite apply(ResourceLocation location)
						{
							return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
						}
					});
					builder.addAll(model.getQuads(null, null, 0));
					
					//Template texture for left half
					String templateLocation = new ResourceLocation(BlockArmor.MODID+":items/icons/block_armor_"+armorType+"1_template").toString();
					TextureAtlasSprite templateTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(templateLocation);
					builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, templateTexture, sprite, NORTH_Z_FLUID, EnumFacing.NORTH, 0xffffffff));
					builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, templateTexture, sprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, 0xffffffff));
					
					//Template texture for right half
					templateLocation = new ResourceLocation(BlockArmor.MODID+":items/icons/block_armor_"+armorType+"2_template").toString();
					templateTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(templateLocation);
					builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, templateTexture, sprite, NORTH_Z_FLUID, EnumFacing.NORTH, 0xffffffff));
					builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, templateTexture, sprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, 0xffffffff));
					
					//Cover texture
					String coverLocation = new ResourceLocation(BlockArmor.MODID+":items/icons/block_armor_"+armorType+"_cover").toString();
					TextureAtlasSprite coverTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(coverLocation);
					builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, NORTH_Z_BASE, coverTexture, EnumFacing.NORTH, 0xffffffff));
					builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, SOUTH_Z_BASE, coverTexture, EnumFacing.SOUTH, 0xffffffff));
					
					itemQuadsMap.put(item, builder.build());
					numIcons++;
				}
			}
			return numIcons;
		}
		
		/**Called every tick - sets inventory icon (via quads) from map*/
		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity)
		{
			ImmutableList<BakedQuad> quads = itemQuadsMap.get(stack.getItem());
			((BakedDynBlockArmor)originalModel).quads = quads;

			return originalModel;
		}
	}

	private static final class BakedDynBlockArmor implements IPerspectiveAwareModel
	{
		private final ImmutableMap<TransformType, TRSRTransformation> transforms;
		private ImmutableList<BakedQuad> quads;

		public BakedDynBlockArmor(VertexFormat format)
		{
			ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();
			builder.put(TransformType.GROUND, new TRSRTransformation(new Vector3f(0.25f, 0.375f, 0.25f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f), new Vector3f(0.5f, 0.5f, 0.5f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.HEAD, new TRSRTransformation(new Vector3f(1.0f, 0.8125f, 1.4375f), new Quat4f(0.0f, 1.0f, 0.0f, -4.371139E-8f), new Vector3f(1.0f, 1.0f, 1.0f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.FIRST_PERSON_RIGHT_HAND, new TRSRTransformation(new Vector3f(0.910625f, 0.24816513f, 0.40617055f), new Quat4f(-0.15304594f, -0.6903456f, 0.15304594f, 0.6903456f), new Vector3f(0.68000007f, 0.68000007f, 0.68f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.FIRST_PERSON_LEFT_HAND, new TRSRTransformation(new Vector3f(0.910625f, 0.24816513f, 0.40617055f), new Quat4f(-0.15304594f, -0.6903456f, 0.15304594f, 0.6903456f), new Vector3f(0.68000007f, 0.68000007f, 0.68f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.THIRD_PERSON_RIGHT_HAND, new TRSRTransformation(new Vector3f(0.225f, 0.4125f, 0.2875f), new Quat4f(0.0f, 0.0f, 0.0f, 0.99999994f), new Vector3f(0.55f, 0.55f, 0.55f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.THIRD_PERSON_LEFT_HAND, new TRSRTransformation(new Vector3f(0.225f, 0.4125f, 0.2875f), new Quat4f(0.0f, 0.0f, 0.0f, 0.99999994f), new Vector3f(0.55f, 0.55f, 0.55f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			ImmutableMap<TransformType, TRSRTransformation> transformMap = builder.build();
			this.transforms = Maps.immutableEnumMap(transformMap);
		}

		@Override
		public ItemOverrideList getOverrides()
		{
			return BakedDynBlockArmorOverrideHandler.INSTANCE;
		}

		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
		{
			return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, transforms, cameraTransformType);
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
		{
			if (side == null) return quads;
			return ImmutableList.of();
		}

		public boolean isAmbientOcclusion() { return true;  }
		public boolean isGui3d() { return false; }
		public boolean isBuiltInRenderer() { return false; }
		public TextureAtlasSprite getParticleTexture() { return null; }
		public ItemCameraTransforms getItemCameraTransforms() { return ItemCameraTransforms.DEFAULT; }
	}
}