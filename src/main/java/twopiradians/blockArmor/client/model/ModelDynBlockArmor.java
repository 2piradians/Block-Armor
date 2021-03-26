package twopiradians.blockArmor.client.model;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.BlockModelConfiguration;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.ModelTransformComposition;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;
import twopiradians.blockArmor.client.ClientProxy;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.BlockArmorItem;

public final class ModelDynBlockArmor implements IModelGeometry<ModelDynBlockArmor>
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
		//System.out.println("gettextures: "+BakedDynBlockArmorOverrideHandler.itemQuadsMap); // TODO remove
		return builder.build();
	}

	/**Cannot assign quads here as BlockArmorItem quads rely on their block's quads to be baked first*/
	@Override
	public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function spriteGetter,
			IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
		//System.out.println("bake: "+BakedDynBlockArmorOverrideHandler.itemQuadsMap); // TODO remove
		return new BakedDynBlockArmor();
	}

	@Override
	public Collection<? extends IModelGeometryPart> getParts() {
		System.out.println("getparts: "+BakedDynBlockArmorOverrideHandler.itemQuadsMap); // TODO remove
		return Collections.emptyList();
	}

	@Override
	public Optional<? extends IModelGeometryPart> getPart(String name) {
		System.out.println("getpart: "+BakedDynBlockArmorOverrideHandler.itemQuadsMap); // TODO remove
		return Optional.empty();
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

	public enum LoaderDynBlockArmor implements IModelLoader<ModelDynBlockArmor> {
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
		public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
			System.out.println("onResourceManagerReload1: "+BakedDynBlockArmorOverrideHandler.itemQuadsMap); // TODO remove
			if (resourcePredicate.test(getResourceType())) {
				onResourceManagerReload(resourceManager);
			}
		}

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
			System.out.println("onResourceManagerReload2 ================================================= "); // TODO remove
		}

		@Override
		public ModelDynBlockArmor read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
			System.out.println("read: "+BakedDynBlockArmorOverrideHandler.itemQuadsMap); // TODO remove
			return new ModelDynBlockArmor();
		}

		@Override
		public IResourceType getResourceType() {
			System.out.println("getResourceType: "+BakedDynBlockArmorOverrideHandler.itemQuadsMap); // TODO remove
			return VanillaResourceType.MODELS;
		}

		/**Called when resources are reloaded (not called during initial load)*/
		@Override
		public CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
			System.out.println("modeldynblockarmor reload ======================================================"); // TODO remove
			// problem with this is that ModelBakery is prepared async and doesn't have any hooks during reload that I know of
			// so reload still doesn't find the models
			ClientProxy.mapUnbakedModels(); // need to do models in here because they're needed before onResourceManagerReload is called
			ClientProxy.mapTextures();
			return stage.markCompleteAwaitingOthers(Unit.INSTANCE).thenRunAsync(() -> {
				reloadProfiler.startTick();
				reloadProfiler.startSection("listener");
				this.onResourceManagerReload(resourceManager);
				reloadProfiler.endSection();
				reloadProfiler.endTick();
			}, gameExecutor);
		}
	}

	public static final class BakedDynBlockArmorOverrideHandler extends ItemOverrideList {
		/**Map of item to quads for the item's inventory model*/
		private static HashMap<Item, ImmutableList<BakedQuad>> itemQuadsMap = Maps.newHashMap();

		public static final BakedDynBlockArmorOverrideHandler INSTANCE = new BakedDynBlockArmorOverrideHandler();

		private BakedDynBlockArmorOverrideHandler()	{
			super();
		}

		/**Creates inventory icons (via quads) for each Block Armor piece and adds to itemQuadsMap*/
		@SuppressWarnings("deprecation")
		public static int createInventoryIcons(ModelBakery bakery) {
			ModelDynBlockArmor.BakedDynBlockArmorOverrideHandler.itemQuadsMap = Maps.newHashMap();
			int numIcons = 0;

			ImmutableMap.Builder<TransformType, TransformationMatrix> builder2 = ImmutableMap.builder();
			builder2.put(TransformType.GROUND, new TransformationMatrix(new Vector3f(0.0f, 0.125f, 0.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), new Vector3f(0.5f, 0.5f, 0.5f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.HEAD, new TransformationMatrix(new Vector3f(0.0f, 0.8125f, 0.4375f), new Quaternion(0.0f, 1.0f, 0.0f, -4.371139E-8f), new Vector3f(1.0f, 1.0f, 1.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.FIRST_PERSON_RIGHT_HAND, new TransformationMatrix(new Vector3f(0.070625f, 0.2f, 0.070625f), new Quaternion(-0.15304594f, -0.6903456f, 0.15304594f, 0.6903456f), new Vector3f(0.68000007f, 0.68000007f, 0.68f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.FIRST_PERSON_LEFT_HAND, new TransformationMatrix(new Vector3f(0.070625f, 0.2f, 0.070625f), new Quaternion(-0.15304594f, -0.6903456f, 0.15304594f, 0.6903456f), new Vector3f(0.68000007f, 0.68000007f, 0.68f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.THIRD_PERSON_RIGHT_HAND, new TransformationMatrix(new Vector3f(0.0f, 0.1875f, 0.0625f), new Quaternion(0.0f, 0.0f, 0.0f, 0.99999994f), new Vector3f(0.55f, 0.55f, 0.55f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.THIRD_PERSON_LEFT_HAND, new TransformationMatrix(new Vector3f(0.0f, 0.1875f, 0.0625f), new Quaternion(0.0f, 0.0f, 0.0f, 0.99999994f), new Vector3f(0.55f, 0.55f, 0.55f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			ImmutableMap<TransformType, TransformationMatrix> transformMap = builder2.build();

			for (ArmorSet set : ArmorSet.allSets) {
				BlockArmorItem[] armor = new BlockArmorItem[] {set.helmet, set.chestplate, set.leggings, set.boots};
				for (BlockArmorItem item : armor) {
					//Initialize variables
					TextureAtlasSprite sprite = ArmorSet.getSprite(item);
					if (sprite == null)
						BlockArmor.LOGGER.warn("Missing sprite for: "+new ItemStack(item).getDisplayName().getUnformattedComponentText());
					ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
					IModelTransform state = new SimpleModelTransform(transformMap);
					TransformationMatrix transform = TransformationMatrix.identity();
					state = new ModelTransformComposition(state, SimpleModelTransform.IDENTITY);
					//VertexFormat format = DefaultVertexFormats.ITEM;
					String armorType = "";
					EquipmentSlotType slot = item.getEquipmentSlot();
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
					IBakedModel model = (new ItemLayerModel(ImmutableList.of(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, baseLocation))))
							.bake(new BlockModelConfiguration(new BlockModel(baseLocation, Lists.newArrayList(), Maps.newHashMap(), false, null, ItemCameraTransforms.DEFAULT, INSTANCE.getOverrides())), 
									bakery, new Function<RenderMaterial, TextureAtlasSprite>() {
								public TextureAtlasSprite apply(RenderMaterial mat)
								{
									return bakery.getSpriteMap().getAtlasTexture(mat.getAtlasLocation()).getSprite(mat.getTextureLocation());
								}
							}, state, INSTANCE, baseLocation);
					builder.addAll(model.getQuads(null, null, new Random()));

					int color = ArmorSet.getColor(item);
					if (color != -1) {
						float r = ((color >> 16) & 0xFF) / 255f;
						float g = ((color >> 8) & 0xFF) / 255f;
						float b = ((color >> 0) & 0xFF) / 255f; 
						color = new Color(r, g, b, 1).getRGB(); //set alpha to 1.0f (since sometimes 0f)
					}

					//Template texture for left half
					ResourceLocation templateLocation = new ResourceLocation(BlockArmor.MODID+":items/icons/block_armor_"+armorType+"1_template");
					TextureAtlasSprite templateTexture = Minecraft.getInstance().getModelManager().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).getSprite(templateLocation);
					builder.addAll(ItemTextureQuadConverter.convertTexture(transform, templateTexture, sprite, NORTH_Z_FLUID, Direction.NORTH, color, 1));
					builder.addAll(ItemTextureQuadConverter.convertTexture(transform, templateTexture, sprite, SOUTH_Z_FLUID, Direction.SOUTH, color, 1));

					//Template texture for right half
					templateLocation = new ResourceLocation(BlockArmor.MODID+":items/icons/block_armor_"+armorType+"2_template");
					templateTexture = Minecraft.getInstance().getModelManager().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).getSprite(templateLocation);
					builder.addAll(ItemTextureQuadConverter.convertTexture(transform, templateTexture, sprite, NORTH_Z_FLUID, Direction.NORTH, color, 1));
					builder.addAll(ItemTextureQuadConverter.convertTexture(transform, templateTexture, sprite, SOUTH_Z_FLUID, Direction.SOUTH, color, 1));

					//Cover texture
					ResourceLocation coverLocation = new ResourceLocation(BlockArmor.MODID+":items/icons/block_armor_"+armorType+"_cover");
					TextureAtlasSprite coverTexture = templateTexture = Minecraft.getInstance().getModelManager().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).getSprite(coverLocation);
					builder.add(ItemTextureQuadConverter.genQuad(transform, 0, 0, 16, 16, NORTH_Z_BASE, coverTexture, Direction.NORTH, color, 1));
					builder.add(ItemTextureQuadConverter.genQuad(transform, 0, 0, 16, 16, SOUTH_Z_BASE, coverTexture, Direction.SOUTH, color, 1));

					ImmutableList<BakedQuad> quads = builder.build();
					itemQuadsMap.put(item, quads);
					numIcons++;
				}
			}
			return numIcons;
		}

		/**Called every tick - sets inventory icon (via quads) from map if null*/
		@Override
		public IBakedModel getOverrideModel(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity) {
			if (originalModel instanceof BakedDynBlockArmor && 
					(((BakedDynBlockArmor)originalModel).quads == null || ((BakedDynBlockArmor)originalModel).quads.isEmpty()) &&
					itemQuadsMap.get(stack.getItem()) != null)
				((BakedDynBlockArmor)originalModel).quads = itemQuadsMap.get(stack.getItem());
			return originalModel;
		}
	}

	private static final class BakedDynBlockArmor implements IBakedModel {
		private final ImmutableMap<TransformType, TransformationMatrix> transforms;
		private ImmutableList<BakedQuad> quads = ImmutableList.of();

		public BakedDynBlockArmor() {
			ImmutableMap.Builder<TransformType, TransformationMatrix> builder = ImmutableMap.builder();
			builder.put(TransformType.GROUND, new TransformationMatrix(new Vector3f(0.0f, 0.125f, 0.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), new Vector3f(0.5f, 0.5f, 0.5f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.HEAD, new TransformationMatrix(new Vector3f(0.0f, 0.8125f, 0.4375f), new Quaternion(0.0f, 1.0f, 0.0f, -4.371139E-8f), new Vector3f(1.0f, 1.0f, 1.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.FIRST_PERSON_RIGHT_HAND, new TransformationMatrix(new Vector3f(0.070625f, 0.2f, 0.070625f), new Quaternion(-0.15304594f, -0.6903456f, 0.15304594f, 0.6903456f), new Vector3f(0.68000007f, 0.68000007f, 0.68f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.FIRST_PERSON_LEFT_HAND, new TransformationMatrix(new Vector3f(0.070625f, 0.2f, 0.070625f), new Quaternion(-0.15304594f, -0.6903456f, 0.15304594f, 0.6903456f), new Vector3f(0.68000007f, 0.68000007f, 0.68f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.THIRD_PERSON_RIGHT_HAND, new TransformationMatrix(new Vector3f(0.0f, 0.1875f, 0.0625f), new Quaternion(0.0f, 0.0f, 0.0f, 0.99999994f), new Vector3f(0.55f, 0.55f, 0.55f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.THIRD_PERSON_LEFT_HAND, new TransformationMatrix(new Vector3f(0.0f, 0.1875f, 0.0625f), new Quaternion(0.0f, 0.0f, 0.0f, 0.99999994f), new Vector3f(0.55f, 0.55f, 0.55f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			ImmutableMap<TransformType, TransformationMatrix> transformMap = builder.build();
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

		public boolean isSideLit() { return false; }
		public boolean isAmbientOcclusion() { return true;  }
		public boolean isGui3d() { return false; }
		public boolean isBuiltInRenderer() { return false; }
		public TextureAtlasSprite getParticleTexture() { return null; }
		public ItemCameraTransforms getItemCameraTransforms() { return ItemCameraTransforms.DEFAULT; }
	}

}