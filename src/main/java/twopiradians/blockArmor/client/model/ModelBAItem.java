package twopiradians.blockArmor.client.model;

import java.awt.Color;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.BlockModelConfiguration;
import net.minecraftforge.client.model.CompositeModelState;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import twopiradians.blockArmor.client.ClientProxy;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.BlockArmorItem;

@OnlyIn(Dist.CLIENT)
public final class ModelBAItem implements IModelGeometry<ModelBAItem> {

	public static final ModelResourceLocation LOCATION = new ModelResourceLocation(BlockArmor.MODID+":block_armor", "inventory");

	// minimal Z offset to prevent depth-fighting
	private static final float NORTH_Z_BASE = 7.496f / 16f;
	private static final float SOUTH_Z_BASE = 8.503f / 16f;
	private static final float NORTH_Z_FLUID = 7.498f / 16f;
	private static final float SOUTH_Z_FLUID = 8.502f / 16f;

	@Override
	public Collection getTextures(IModelConfiguration owner, Function modelGetter, Set missingTextureErrors) {
		return ImmutableSet.builder().build();
	}

	/**Cannot assign quads here as BlockArmorItem quads rely on their block's quads to be baked first*/
	@Override
	public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function spriteGetter,
			ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
		return new BakedDynBlockArmor();
	}

	public enum LoaderDynBlockArmor implements IModelLoader<ModelBAItem> {
		INSTANCE;

		@Override
		public void onResourceManagerReload(ResourceManager resourceManager) {
			ClientProxy.mapTextures();
		}

		@Override
		public ModelBAItem read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
			return new ModelBAItem();
		}

	}

	public static final class BakedDynBlockArmorOverrideHandler extends ItemOverrides {
		/**Map of item to quads for the item's inventory model*/
		private static HashMap<Item, HashMap<Layer, ImmutableList<BakedQuad>>> itemQuadsMap = Maps.newHashMap();

		public static final BakedDynBlockArmorOverrideHandler INSTANCE = new BakedDynBlockArmorOverrideHandler();
		
		/**Creates inventory icons (via quads) for each Block Armor piece and adds to itemQuadsMap*/
		@SuppressWarnings("deprecation")
		public static int createInventoryIcons(ModelBakery bakery) {
			ModelBAItem.BakedDynBlockArmorOverrideHandler.itemQuadsMap = Maps.newHashMap();
			int numIcons = 0;

			ImmutableMap.Builder<TransformType, Transformation> builder2 = ImmutableMap.builder();
			builder2.put(TransformType.GROUND, new Transformation(new Vector3f(0.0f, 0.125f, 0.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), new Vector3f(0.5f, 0.5f, 0.5f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.HEAD, new Transformation(new Vector3f(0.0f, 0.8125f, 0.4375f), new Quaternion(0.0f, 1.0f, 0.0f, -4.371139E-8f), new Vector3f(1.0f, 1.0f, 1.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.FIRST_PERSON_RIGHT_HAND, new Transformation(new Vector3f(0.070625f, 0.2f, 0.070625f), new Quaternion(-0.15304594f, -0.6903456f, 0.15304594f, 0.6903456f), new Vector3f(0.68000007f, 0.68000007f, 0.68f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.FIRST_PERSON_LEFT_HAND, new Transformation(new Vector3f(0.070625f, 0.2f, 0.070625f), new Quaternion(-0.15304594f, -0.6903456f, 0.15304594f, 0.6903456f), new Vector3f(0.68000007f, 0.68000007f, 0.68f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.THIRD_PERSON_RIGHT_HAND, new Transformation(new Vector3f(0.0f, 0.1875f, 0.0625f), new Quaternion(0.0f, 0.0f, 0.0f, 0.99999994f), new Vector3f(0.55f, 0.55f, 0.55f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder2.put(TransformType.THIRD_PERSON_LEFT_HAND, new Transformation(new Vector3f(0.0f, 0.1875f, 0.0625f), new Quaternion(0.0f, 0.0f, 0.0f, 0.99999994f), new Vector3f(0.55f, 0.55f, 0.55f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			ImmutableMap<TransformType, Transformation> transformMap = builder2.build();

			for (ArmorSet set : ArmorSet.allSets) {
				BlockArmorItem[] armor = new BlockArmorItem[] {set.helmet, set.chestplate, set.leggings, set.boots};
				for (BlockArmorItem item : armor) 
					if (item != null) {
						//Initialize variables
						TextureAtlasSprite sprite = item.set.getTextureInfo(item.getSlot()).sprite;
						if (sprite == null)
							BlockArmor.LOGGER.warn("Missing sprite for: "+new ItemStack(item).getHoverName().getContents());
						ModelState state = new SimpleModelState(transformMap);
						Transformation transform = Transformation.identity();
						state = new CompositeModelState(state, state);
						String armorType = "";
						EquipmentSlot slot = item.getSlot();
						if (slot == EquipmentSlot.HEAD)
							armorType = "helmet";
						else if (slot == EquipmentSlot.CHEST)
							armorType = "chestplate";
						else if (slot == EquipmentSlot.LEGS)
							armorType = "leggings";
						else if (slot == EquipmentSlot.FEET)
							armorType = "boots";

						//Block texture background
						//builder.add(ItemTextureQuadConverter.genQuad(transform, 0, 0, 16, 16, NORTH_Z_BASE, sprite, Direction.NORTH, 0xffffffff, 1));
						//builder.add(ItemTextureQuadConverter.genQuad(transform, 0, 0, 16, 16, SOUTH_Z_BASE, sprite, Direction.SOUTH, 0xffffffff, 1));	            

						//Base texture and model
						ResourceLocation baseLocation = new ResourceLocation(BlockArmor.MODID+":items/icons/block_armor_"+armorType+"_base");
						BakedModel model = (new ItemLayerModel(ImmutableList.of(new Material(TextureAtlas.LOCATION_BLOCKS, baseLocation))))
								.bake(new BlockModelConfiguration(new BlockModel(baseLocation, Lists.newArrayList(), Maps.newHashMap(), false, null, ItemTransforms.NO_TRANSFORMS, Lists.newArrayList())),  
										bakery, new Function<Material, TextureAtlasSprite>() {
									public TextureAtlasSprite apply(Material mat)
									{
										return bakery.getSpriteMap().getAtlas(mat.atlasLocation()).getSprite(mat.texture());
									}
								}, state, INSTANCE, baseLocation);

						ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
						HashMap<Layer, ImmutableList<BakedQuad>> quads = Maps.newHashMap();

						//Base
						builder.addAll(model.getQuads(null, null, new Random()));
						quads.put(Layer.BASE, builder.build());

						int color = item.set.getTextureInfo(item.getSlot()).color;
						if (color != -1) {
							float r = ((color >> 16) & 0xFF) / 255f;
							float g = ((color >> 8) & 0xFF) / 255f;
							float b = ((color >> 0) & 0xFF) / 255f; 
							color = new Color(r, g, b, 1).getRGB(); //set alpha to 1.0f (since sometimes 0f)
						}

						//Template texture for left half
						builder = ImmutableList.builder();
						ResourceLocation templateLocation = new ResourceLocation(BlockArmor.MODID+":items/icons/block_armor_"+armorType+"1_template");
						TextureAtlasSprite templateTexture = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(templateLocation);
						builder.addAll(ItemTextureQuadConverter.convertTexture(transform, templateTexture, sprite, NORTH_Z_FLUID, Direction.NORTH, color, 1));
						builder.addAll(ItemTextureQuadConverter.convertTexture(transform, templateTexture, sprite, SOUTH_Z_FLUID, Direction.SOUTH, color, 1));

						//Template texture for right half
						templateLocation = new ResourceLocation(BlockArmor.MODID+":items/icons/block_armor_"+armorType+"2_template");
						templateTexture = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(templateLocation);
						builder.addAll(ItemTextureQuadConverter.convertTexture(transform, templateTexture, sprite, NORTH_Z_FLUID, Direction.NORTH, color, 1));
						builder.addAll(ItemTextureQuadConverter.convertTexture(transform, templateTexture, sprite, SOUTH_Z_FLUID, Direction.SOUTH, color, 1));
						quads.put(Layer.TEMPLATE, builder.build());

						//Cover texture
						builder = ImmutableList.builder();
						ResourceLocation coverLocation = new ResourceLocation(BlockArmor.MODID+":items/icons/block_armor_"+armorType+"_cover");
						TextureAtlasSprite coverTexture = templateTexture = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(coverLocation);
						builder.add(ItemTextureQuadConverter.genQuad(transform, 0, 0, 16, 16, NORTH_Z_BASE, coverTexture, Direction.NORTH, color, 1));
						builder.add(ItemTextureQuadConverter.genQuad(transform, 0, 0, 16, 16, SOUTH_Z_BASE, coverTexture, Direction.SOUTH, color, 1));
						quads.put(Layer.COVER, builder.build());

						itemQuadsMap.put(item, quads);
						numIcons++;
					}
			}
			return numIcons;
		}

		/**Called every tick - sets inventory icon (via quads) from map if null*/
		@Override
		public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int num) {
			if (originalModel instanceof BakedDynBlockArmor && 
					(((BakedDynBlockArmor)originalModel).layers.get(Layer.BASE).quads == null || 
					((BakedDynBlockArmor)originalModel).layers.get(Layer.BASE).quads.isEmpty()) &&
					itemQuadsMap.get(stack.getItem()) != null) {
				for (Layer layer : Layer.values())
					((BakedDynBlockArmor)originalModel).layers.get(layer).quads = itemQuadsMap.get(stack.getItem()).get(layer);
				BlockArmorItem item = (BlockArmorItem) stack.getItem();
				((BakedDynBlockArmor)originalModel).particles = item.set.getTextureInfo(item.getSlot()).sprite;
			}
			return originalModel;
		}
	}

	private enum Layer {
		BASE, TEMPLATE, COVER;

		/**Need to render cover transparent so it isn't culled*/
		public RenderType getRenderType(ItemStack stack) {
			if (this == COVER) 
				return Sheets.translucentCullBlockSheet();
			else if (Minecraft.useShaderTransparency())
				return Sheets.cutoutBlockSheet(); // not translucent, but best we can do in Fabulous graphics?
			else
				return Sheets.translucentItemSheet(); // renders invisible in Fabulous graphics
		}
	}

	private static final class BakedDynBlockArmor implements BakedModel {

		@Nullable
		public final Layer layer;
		private HashMap<Layer, BakedDynBlockArmor> layers = Maps.newHashMap();
		private final ImmutableMap<TransformType, Transformation> transforms;
		private ImmutableList<BakedQuad> quads = ImmutableList.of();
		private TextureAtlasSprite particles;

		public BakedDynBlockArmor() {
			this(null);
			for (Layer layer : Layer.values())
				layers.put(layer, new BakedDynBlockArmor(layer));
		}

		private BakedDynBlockArmor(Layer layer) {
			this.layer = layer;
			ImmutableMap.Builder<TransformType, Transformation> builder = ImmutableMap.builder();
			builder.put(TransformType.GROUND, new Transformation(new Vector3f(0.0f, 0.125f, 0.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), new Vector3f(0.5f, 0.5f, 0.5f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.HEAD, new Transformation(new Vector3f(0.0f, 0.8125f, 0.4375f), new Quaternion(0.0f, 1.0f, 0.0f, -4.371139E-8f), new Vector3f(1.0f, 1.0f, 1.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.FIRST_PERSON_RIGHT_HAND, new Transformation(new Vector3f(0.070625f, 0.2f, 0.070625f), new Quaternion(-0.15304594f, -0.6903456f, 0.15304594f, 0.6903456f), new Vector3f(0.68000007f, 0.68000007f, 0.68f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.FIRST_PERSON_LEFT_HAND, new Transformation(new Vector3f(0.070625f, 0.2f, 0.070625f), new Quaternion(-0.15304594f, -0.6903456f, 0.15304594f, 0.6903456f), new Vector3f(0.68000007f, 0.68000007f, 0.68f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.THIRD_PERSON_RIGHT_HAND, new Transformation(new Vector3f(0.0f, 0.1875f, 0.0625f), new Quaternion(0.0f, 0.0f, 0.0f, 0.99999994f), new Vector3f(0.55f, 0.55f, 0.55f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			builder.put(TransformType.THIRD_PERSON_LEFT_HAND, new Transformation(new Vector3f(0.0f, 0.1875f, 0.0625f), new Quaternion(0.0f, 0.0f, 0.0f, 0.99999994f), new Vector3f(0.55f, 0.55f, 0.55f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f)));
			ImmutableMap<TransformType, Transformation> transformMap = builder.build();
			this.transforms = Maps.immutableEnumMap(transformMap);
		}

		@Override
		public ItemOverrides getOverrides() {
			return BakedDynBlockArmorOverrideHandler.INSTANCE;
		}

		@Override
		public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack mat) {
			return PerspectiveMapWrapper.handlePerspective(this, transforms, cameraTransformType, mat);
		}

		@Override
		public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
			if (side == null) 
				return quads;
			return ImmutableList.of();
		}

		/**Need to render cover layer transparent so it isn't culled*/
		@Override
		public List<Pair<BakedModel, RenderType>> getLayerModels(ItemStack stack, boolean fabulous) {
			List<Pair<BakedModel, RenderType>> ret = Lists.newArrayList();
			for (Layer layer : Layer.values())
				ret.add(Pair.of(this.layers.get(layer), layer.getRenderType(stack)));
			return ret;
		}

		public boolean isLayered() { return true; }
		public boolean usesBlockLight() { return false; }
		public boolean useAmbientOcclusion() { return true;  }
		public boolean isGui3d() { return false; }
		public boolean isCustomRenderer() { return false; }
		public TextureAtlasSprite getParticleIcon() { return particles; } 
		public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
	}

}