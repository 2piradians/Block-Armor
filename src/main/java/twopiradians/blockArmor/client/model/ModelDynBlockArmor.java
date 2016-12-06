package twopiradians.blockArmor.client.model;

import java.util.Collection;
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
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
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

	private static final class BakedDynBlockArmorOverrideHandler extends ItemOverrideList
	{
		public static final BakedDynBlockArmorOverrideHandler INSTANCE = new BakedDynBlockArmorOverrideHandler();
		private BakedDynBlockArmorOverrideHandler()
		{
			super(ImmutableList.<ItemOverride>of());
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity)
		{//TODO only assign texture once
			if (originalModel instanceof BakedDynBlockArmor && ArmorSet.getInventoryTextureLocation((ItemBlockArmor) stack.getItem()) != null) {
				ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
				IModelState state = new SimpleModelState(((BakedDynBlockArmor)originalModel).transforms);
				TRSRTransformation transform = TRSRTransformation.identity();
				state = new ModelStateComposition(state, transform);
				VertexFormat format = ((BakedDynBlockArmor)originalModel).format;
				//Full block texture
				ResourceLocation textureLocation = ArmorSet.getInventoryTextureLocation((ItemBlockArmor) stack.getItem());
				TextureAtlasSprite blockTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(textureLocation.toString());
				//builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, NORTH_Z_BASE, blockTexture, EnumFacing.NORTH, 0xffffffff));
				//builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, SOUTH_Z_BASE, blockTexture, EnumFacing.SOUTH, 0xffffffff));	            
				String armorType = "";
				EntityEquipmentSlot slot = ((ItemBlockArmor) stack.getItem()).getEquipmentSlot();
				if (slot == EntityEquipmentSlot.HEAD)
					armorType = "helmet";
				else if (slot == EntityEquipmentSlot.CHEST)
					armorType = "chestplate";
				else if (slot == EntityEquipmentSlot.LEGS)
					armorType = "leggings";
				else if (slot == EntityEquipmentSlot.FEET)
					armorType = "boots";
				//Base texture and model
				ResourceLocation baseLocation = new ResourceLocation("blockarmor:items/block_armor_"+armorType+"_base");
				IBakedModel model = (new ItemLayerModel(ImmutableList.of(baseLocation))).bake(state, format, new Function<ResourceLocation, TextureAtlasSprite>() {
					public TextureAtlasSprite apply(ResourceLocation location)
					{
						return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
					}
				});
				builder.addAll(model.getQuads(null, null, 0));
				//Template texture for left half
				String templateLocation = new ResourceLocation("blockarmor:items/block_armor_"+armorType+"1_template").toString();
				TextureAtlasSprite templateTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(templateLocation);
				builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, templateTexture, blockTexture, NORTH_Z_FLUID, EnumFacing.NORTH, 0xffffffff));
				builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, templateTexture, blockTexture, SOUTH_Z_FLUID, EnumFacing.SOUTH, 0xffffffff));
				//Template texture for right half
				templateLocation = new ResourceLocation("blockarmor:items/block_armor_"+armorType+"2_template").toString();
				templateTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(templateLocation);
				builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, templateTexture, blockTexture, NORTH_Z_FLUID, EnumFacing.NORTH, 0xffffffff));
				builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, templateTexture, blockTexture, SOUTH_Z_FLUID, EnumFacing.SOUTH, 0xffffffff));
				//Cover texture
				String coverLocation = new ResourceLocation("blockarmor:items/block_armor_"+armorType+"_cover").toString();
				TextureAtlasSprite coverTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(coverLocation);
				builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, NORTH_Z_BASE, coverTexture, EnumFacing.NORTH, 0xffffffff));
				builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, SOUTH_Z_BASE, coverTexture, EnumFacing.SOUTH, 0xffffffff));
				((BakedDynBlockArmor)originalModel).quads = builder.build();
			}

			return originalModel;
		}
	}

	private static final class BakedDynBlockArmor implements IPerspectiveAwareModel
	{
		private final ImmutableMap<TransformType, TRSRTransformation> transforms;
		private ImmutableList<BakedQuad> quads;
		private final VertexFormat format;

		public BakedDynBlockArmor(VertexFormat format)
		{
			this.format = format;

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