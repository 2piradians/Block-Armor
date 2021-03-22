package twopiradians.blockArmor.client.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import twopiradians.blockArmor.common.BlockArmor;

public final class ModelDynBlockArmor implements IModelGeometry
{
	public static final ModelResourceLocation LOCATION = new ModelResourceLocation(BlockArmor.MODID+":block_armor", "inventory");

	// minimal Z offset to prevent depth-fighting
	private static final float NORTH_Z_BASE = 7.496f / 16f;
	private static final float SOUTH_Z_BASE = 8.503f / 16f;
	private static final float NORTH_Z_FLUID = 7.498f / 16f;
	private static final float SOUTH_Z_FLUID = 8.502f / 16f;

	public ModelDynBlockArmor() {}

	/*	@Override
		public Collection<ResourceLocation> getDependencies() {
			return ImmutableList.of();
		}*/

	@Override
	public Collection getTextures(IModelConfiguration owner, Function modelGetter, Set missingTextureErrors) {
		ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
		return builder.build();
	}

	/**Cannot assign quads here as BlockArmorItem quads rely on their block's quads to be baked first*/
	@Override
	public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function spriteGetter,
			IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
		return new BakedDynBlockArmor();
	}

	/*@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}
	
	@Override
	public ModelDynBlockArmor process(ImmutableMap<String, String> customData) {
		return this;
	}
	
	@Override
	public ModelDynBlockArmor retexture(ImmutableMap<String, String> textures) {
		return this;
	}*/

	public enum LoaderDynBlockArmor implements IModelLoader {
		INSTANCE;

		/*@Override
		public boolean accepts(ResourceLocation modelLocation) {
			return (modelLocation.getNamespace().equals(BlockArmor.MODID) && (modelLocation.getResourcePath().contains("helmet") 
					|| modelLocation.getResourcePath().contains("chestplate") || modelLocation.getResourcePath().contains("leggings") ||
					modelLocation.getResourcePath().contains("boots")));
		}
		
		@Override
		public IModel loadModel(ResourceLocation modelLocation)	{
			return new ModelDynBlockArmor();
		}*/ // TODO

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {}

		@Override
		public IModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
			return null;
		}
	}

	public static final class BakedDynBlockArmorOverrideHandler extends ItemOverrideList {
		private static HashMap<Item, ImmutableList<BakedQuad>> itemQuadsMap = Maps.newHashMap();

		public static final BakedDynBlockArmorOverrideHandler INSTANCE = new BakedDynBlockArmorOverrideHandler();

		private BakedDynBlockArmorOverrideHandler()	{
			super();
		}

		/**Creates inventory icons (via quads) for each Block Armor piece and adds to itemQuadsMap*/
		public static int createInventoryIcons() {
			ModelDynBlockArmor.BakedDynBlockArmorOverrideHandler.itemQuadsMap = Maps.newHashMap();
			int numIcons = 0;

			/*ImmutableMap.Builder<TransformType, TRSRTransformation> builder2 = ImmutableMap.builder();
			builder2.put(TransformType.GROUND, new TRSRTransformation(new Vector3f(0.25f, 0.375f, 0.25f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f), new Vector3f(0.5f, 0.5f, 0.5f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.HEAD, new TRSRTransformation(new Vector3f(1.0f, 0.8125f, 1.4375f), new Quat4f(0.0f, 1.0f, 0.0f, -4.371139E-8f), new Vector3f(1.0f, 1.0f, 1.0f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.FIRST_PERSON_RIGHT_HAND, new TRSRTransformation(new Vector3f(0.910625f, 0.24816513f, 0.40617055f), new Quat4f(-0.15304594f, -0.6903456f, 0.15304594f, 0.6903456f), new Vector3f(0.68000007f, 0.68000007f, 0.68f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.FIRST_PERSON_LEFT_HAND, new TRSRTransformation(new Vector3f(0.910625f, 0.24816513f, 0.40617055f), new Quat4f(-0.15304594f, -0.6903456f, 0.15304594f, 0.6903456f), new Vector3f(0.68000007f, 0.68000007f, 0.68f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.THIRD_PERSON_RIGHT_HAND, new TRSRTransformation(new Vector3f(0.225f, 0.4125f, 0.2875f), new Quat4f(0.0f, 0.0f, 0.0f, 0.99999994f), new Vector3f(0.55f, 0.55f, 0.55f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.THIRD_PERSON_LEFT_HAND, new TRSRTransformation(new Vector3f(0.225f, 0.4125f, 0.2875f), new Quat4f(0.0f, 0.0f, 0.0f, 0.99999994f), new Vector3f(0.55f, 0.55f, 0.55f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			ImmutableMap<TransformType, TRSRTransformation> transformMap = builder2.build();
			
			for (ArmorSet set : ArmorSet.allSets) {
				BlockArmorItem[] armor = new BlockArmorItem[] {set.helmet, set.chestplate, set.leggings, set.boots};
				for (BlockArmorItem item : armor) {
					//Initialize variables
					TextureAtlasSprite sprite = ArmorSet.getSprite(item);
					if (sprite == null)
						BlockArmor.LOGGER.warn("Missing sprite for: "+new ItemStack(item).getDisplayName());
					ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
					IModelState state = new SimpleModelState(transformMap);
					TRSRTransformation transform = TRSRTransformation.identity();
					state = new ModelStateComposition(state, transform);
					VertexFormat format = DefaultVertexFormats.ITEM;
					String armorType = "";
					EquipmentSlotType slot = item.armorType;
					if (slot == EquipmentSlotType.HEAD)
						armorType = "helmet";
					else if (slot == EquipmentSlotType.CHEST)
						armorType = "chestplate";
					else if (slot == EquipmentSlotType.LEGS)
						armorType = "leggings";
					else if (slot == EquipmentSlotType.FEET)
						armorType = "boots";
			
					//Block texture background
					//builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, NORTH_Z_BASE, sprite, EnumFacing.NORTH, 0xffffffff));
					//builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, SOUTH_Z_BASE, sprite, EnumFacing.SOUTH, 0xffffffff));	            
			
					//Base texture and model
					ResourceLocation baseLocation = new ResourceLocation(BlockArmor.MODID+":items/icons/block_armor_"+armorType+"_base");
					IBakedModel model = (new ItemLayerModel(ImmutableList.of(baseLocation))).bake(state, format, new Function<ResourceLocation, TextureAtlasSprite>() {
						public TextureAtlasSprite apply(ResourceLocation location)
						{
							return Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(location.toString());
						}
					});
					builder.addAll(model.getQuads(null, null, 0));
			
					int color = ArmorSet.getColor(item);
					if (color != -1) {
						float r = ((color >> 16) & 0xFF) / 255f;
						float g = ((color >> 8) & 0xFF) / 255f;
						float b = ((color >> 0) & 0xFF) / 255f; 
						color = new Color(r, g, b).getRGB(); //set alpha to 1.0f (since sometimes 0f)
					}
			
					//Template texture for left half
					String templateLocation = new ResourceLocation(BlockArmor.MODID+":items/icons/block_armor_"+armorType+"1_template").toString();
					TextureAtlasSprite templateTexture = Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(templateLocation);
					builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, templateTexture, sprite, NORTH_Z_FLUID, EnumFacing.NORTH, color));
					builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, templateTexture, sprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, color));
			
					//Template texture for right half
					templateLocation = new ResourceLocation(BlockArmor.MODID+":items/icons/block_armor_"+armorType+"2_template").toString();
					templateTexture = Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(templateLocation);
					builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, templateTexture, sprite, NORTH_Z_FLUID, EnumFacing.NORTH, color));
					builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, templateTexture, sprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, color));
			
					//Cover texture
					String coverLocation = new ResourceLocation(BlockArmor.MODID+":items/icons/block_armor_"+armorType+"_cover").toString();
					TextureAtlasSprite coverTexture = Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(coverLocation);
					builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, NORTH_Z_BASE, coverTexture, EnumFacing.NORTH, 0xffffffff));
					builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, SOUTH_Z_BASE, coverTexture, EnumFacing.SOUTH, 0xffffffff));
			
					itemQuadsMap.put(item, builder.build());
					numIcons++;
				}
			}*/
			return numIcons;
		}

		/**Called every tick - sets inventory icon (via quads) from map if null*/
		@Override
		   public IBakedModel getOverrideModel(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity) {
			if (originalModel instanceof BakedDynBlockArmor && ((BakedDynBlockArmor)originalModel).quads == null)
				((BakedDynBlockArmor)originalModel).quads = itemQuadsMap.get(stack.getItem());

			return originalModel;
		}
	}

	private static final class BakedDynBlockArmor implements IBakedModel {
		private final ImmutableMap<TransformType, TransformationMatrix> transforms;
		private ImmutableList<BakedQuad> quads;

		public BakedDynBlockArmor() {
			ImmutableMap.Builder<TransformType, TransformationMatrix> builder = ImmutableMap.builder(); // TODO
			/*builder.put(TransformType.GROUND, new TRSRTransformer(new Vector3f(0.25f, 0.375f, 0.25f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f), new Vector3f(0.5f, 0.5f, 0.5f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.HEAD, new TRSRTransformation(new Vector3f(1.0f, 0.8125f, 1.4375f), new Quat4f(0.0f, 1.0f, 0.0f, -4.371139E-8f), new Vector3f(1.0f, 1.0f, 1.0f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.FIRST_PERSON_RIGHT_HAND, new TRSRTransformation(new Vector3f(0.910625f, 0.24816513f, 0.40617055f), new Quat4f(-0.15304594f, -0.6903456f, 0.15304594f, 0.6903456f), new Vector3f(0.68000007f, 0.68000007f, 0.68f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.FIRST_PERSON_LEFT_HAND, new TRSRTransformation(new Vector3f(0.910625f, 0.24816513f, 0.40617055f), new Quat4f(-0.15304594f, -0.6903456f, 0.15304594f, 0.6903456f), new Vector3f(0.68000007f, 0.68000007f, 0.68f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.THIRD_PERSON_RIGHT_HAND, new TRSRTransformation(new Vector3f(0.225f, 0.4125f, 0.2875f), new Quat4f(0.0f, 0.0f, 0.0f, 0.99999994f), new Vector3f(0.55f, 0.55f, 0.55f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.THIRD_PERSON_LEFT_HAND, new TRSRTransformation(new Vector3f(0.225f, 0.4125f, 0.2875f), new Quat4f(0.0f, 0.0f, 0.0f, 0.99999994f), new Vector3f(0.55f, 0.55f, 0.55f), new Quat4f(0.0f, 0.0f, 0.0f, 1.0f)));
			*/ImmutableMap<TransformType, TransformationMatrix> transformMap = builder.build();
			this.transforms = Maps.immutableEnumMap(transformMap);
		}

		@Override
		public ItemOverrideList getOverrides() {
			return BakedDynBlockArmorOverrideHandler.INSTANCE;
		}

		@Override
	    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
			return PerspectiveMapWrapper.handlePerspective(this, transforms, cameraTransformType, mat);
		}

		@Override
		public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
			if (side == null) return quads;
			return ImmutableList.of();
		}

		public boolean isSideLit() { return true; }
		public boolean isAmbientOcclusion() { return true;  }
		public boolean isGui3d() { return false; }
		public boolean isBuiltInRenderer() { return false; }
		public TextureAtlasSprite getParticleTexture() { return null; }
		public ItemCameraTransforms getItemCameraTransforms() { return ItemCameraTransforms.DEFAULT; }
	}

}