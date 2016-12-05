package twopiradians.blockArmor.client.render.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.base.Optional;
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
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

public final class ModelDynBlockArmor implements IModel, IModelCustomData, IRetexturableModel
{
	public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation(BlockArmor.MODID, "block_armor"), "inventory");

	// minimal Z offset to prevent depth-fighting
	private static final float NORTH_Z_BASE = 7.496f / 16f;
	private static final float SOUTH_Z_BASE = 8.504f / 16f;
	private static final float NORTH_Z_FLUID = 7.498f / 16f;
	private static final float SOUTH_Z_FLUID = 8.502f / 16f;

	public static final IModel MODEL = new ModelDynBlockArmor();

	private final ResourceLocation baseLocation;
	private final ResourceLocation liquidLocation;
	private final ResourceLocation coverLocation;

	private final Fluid fluid;
	private final boolean flipGas;

	public ModelDynBlockArmor()
	{
		this(null, null, null, null, false);
	}

	public ModelDynBlockArmor(ResourceLocation baseLocation, ResourceLocation liquidLocation, ResourceLocation coverLocation, Fluid fluid, boolean flipGas)
	{
		this.baseLocation = baseLocation;
		this.liquidLocation = liquidLocation;
		this.coverLocation = coverLocation;
		this.fluid = fluid;
		this.flipGas = flipGas;
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
		/*        if (baseLocation != null)
            builder.add(baseLocation);
        if (liquidLocation != null)
            builder.add(liquidLocation);
        if (coverLocation != null)
            builder.add(coverLocation);*/

		//builder.add(new ResourceLocation(BlockArmor.MODID, "assets/blockarmor/textures/items/andesite_chestplate.png"));
		//builder.add(new ResourceLocation(BlockArmor.MODID, "blockarmor/textures/items/andesite_chestplate.png"));
		//builder.add(new ResourceLocation(BlockArmor.MODID, "assets/blockarmor/textures/items/andesite_chestplate"));
		//builder.add(new ResourceLocation(BlockArmor.MODID, "blockarmor/textures/items/andesite_chestplate"));
		//builder.add(new ResourceLocation(BlockArmor.MODID, "textures/items/andesite_chestplate.png"));
		//builder.add(new ResourceLocation(BlockArmor.MODID, "textures/items/andesite_chestplate"));
		//builder.add(new ResourceLocation(BlockArmor.MODID, "items/andesite_chestplate.png"));
		/*		builder.add(new ResourceLocation(BlockArmor.MODID, "items/andesite_chestplate"));
		builder.add(new ResourceLocation(BlockArmor.MODID, "items/andesite_chestplate"));
		builder.add(new ResourceLocation(BlockArmor.MODID, "items/andesite_chestplate"));*/
		//builder.add(new ResourceLocation(BlockArmor.MODID, "andesite_chestplate"));

		return builder.build();
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
	{

		ImmutableMap<TransformType, TRSRTransformation> transformMap = IPerspectiveAwareModel.MapWrapper.getTransforms(state);

		// if the fluid is a gas wi manipulate the initial state to be rotated 180? to turn it upside down
		/*        if (flipGas && fluid != null && fluid.isGaseous())
        {
            state = new ModelStateComposition(state, TRSRTransformation.blockCenterToCorner(new TRSRTransformation(null, new Quat4f(0, 0, 1, 0), null, null)));
        }*/

		TRSRTransformation transform = state.apply(Optional.<IModelPart>absent()).or(TRSRTransformation.identity());
		TextureAtlasSprite fluidSprite = null;
		ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

		/*if(fluid != null) {
            fluidSprite = bakedTextureGetter.apply(fluid.getStill());
        }

        if (baseLocation != null)
        {
            // build base (insidest)
            IBakedModel model = (new ItemLayerModel(ImmutableList.of(baseLocation))).bake(state, format, bakedTextureGetter);
            builder.addAll(model.getQuads(null, null, 0));
        }
        if (liquidLocation != null && fluidSprite != null)
        {
            TextureAtlasSprite liquid = bakedTextureGetter.apply(liquidLocation);
            // build liquid layer (inside)
            builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite, NORTH_Z_FLUID, EnumFacing.NORTH, fluid.getColor()));
            builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, fluid.getColor()));
        }
        if (coverLocation != null)
        {
            // cover (the actual item around the other two)
            TextureAtlasSprite base = bakedTextureGetter.apply(coverLocation);
            builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, NORTH_Z_BASE, base, EnumFacing.NORTH, 0xffffffff));
            builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, SOUTH_Z_BASE, base, EnumFacing.SOUTH, 0xffffffff));
        }*/


		//builder.add(new ResourceLocation(BlockArmor.MODID, "textures/items/andesite_chestplate.png"));
		//builder.add(new ResourceLocation(BlockArmor.MODID, "textures/items/andesite_chestplate"));
		//builder.add(new ResourceLocation(BlockArmor.MODID, "andesite_chestplate"));
		/*TextureAtlasSprite base = bakedTextureGetter.apply(new ResourceLocation(BlockArmor.MODID, "items/andesite_chestplate"));
		builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, NORTH_Z_BASE, base, EnumFacing.NORTH, 0xffffffff));
		builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, SOUTH_Z_BASE, base, EnumFacing.SOUTH, 0xffffffff));*/

		return new BakedDynBlockArmor(this, null/*builder.build()*/, fluidSprite, format, Maps.immutableEnumMap(transformMap), Maps.<String, IBakedModel>newHashMap());
	}

	@Override
	public IModelState getDefaultState()
	{
		return TRSRTransformation.identity();
	}

	/**
	 * Sets the liquid in the model.
	 * fluid - Name of the fluid in the FluidRegistry
	 * flipGas - If "true" the model will be flipped upside down if the liquid is a gas. If "false" it wont
	 * <p/>
	 * If the fluid can't be found, water is used
	 */
	@Override
	public ModelDynBlockArmor process(ImmutableMap<String, String> customData)
	{
		String fluidName = customData.get("fluid");
		Fluid fluid = FluidRegistry.getFluid(fluidName);

		if (fluid == null) fluid = this.fluid;

		boolean flip = flipGas;
		if (customData.containsKey("flipGas"))
		{
			String flipStr = customData.get("flipGas");
			if (flipStr.equals("true")) flip = true;
			else if (flipStr.equals("false")) flip = false;
			else
				throw new IllegalArgumentException(String.format("DynBlockArmor custom data \"flipGas\" must have value \'true\' or \'false\' (was \'%s\')", flipStr));
		}

		// create new model with correct liquid
		return new ModelDynBlockArmor(baseLocation, liquidLocation, coverLocation, fluid, flip);
	}

	/**
	 * Allows to use different textures for the model.
	 * There are 3 layers:
	 * base - The empty bucket/container
	 * fluid - A texture representing the liquid portion. Non-transparent = liquid
	 * cover - An overlay that's put over the liquid (optional)
	 * <p/>
	 * If no liquid is given a hardcoded variant for the bucket is used.
	 */
	@Override
	public ModelDynBlockArmor retexture(ImmutableMap<String, String> textures)
	{

		/*	ResourceLocation base = baseLocation;
		ResourceLocation liquid = liquidLocation;
		ResourceLocation cover = coverLocation;
		 */
		/*       if (textures.containsKey("base"))
            base = new ResourceLocation(textures.get("base"));
        if (textures.containsKey("fluid"))
            liquid = new ResourceLocation(textures.get("fluid"));
        if (textures.containsKey("cover"))
            cover = new ResourceLocation(textures.get("cover"));*/
		/*
		ResourceLocation test = new ResourceLocation(BlockArmor.MODID, "items/andesite_chestplate");
		base = test;
		liquid = test;
		cover = test;*/

		return new ModelDynBlockArmor(null, null, null, null, false);
		//return new ModelDynBlockArmor(base, liquid, cover, fluid, flipGas);
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
			//modelLocation.getResourceDomain().equals(BlockArmor.MODID);//modelLocation.getResourceDomain().equals("forge") && modelLocation.getResourcePath().contains("forgebucket");
		}

		@Override
		public IModel loadModel(ResourceLocation modelLocation)
		{
			return MODEL;
		}

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager)
		{
			// no need to clear cache since we create a new model instance
			System.out.println("resource manager reload");
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
		{
			if (originalModel instanceof BakedDynBlockArmor && ArmorSet.getInventoryTextureLocation((ItemBlockArmor) stack.getItem()) != null) {
				ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
				TRSRTransformation transform = new SimpleModelState(((BakedDynBlockArmor)originalModel).transforms).apply(Optional.<IModelPart>absent()).or(TRSRTransformation.identity());
				VertexFormat format = ((BakedDynBlockArmor)originalModel).format;
				//Full block texture
				String textureLocation = ArmorSet.getInventoryTextureLocation((ItemBlockArmor) stack.getItem()).toString();
				TextureAtlasSprite blockTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(textureLocation);
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
				//Template texture
				String templateLocation = new ResourceLocation("blockarmor:items/block_armor_"+armorType+"_template").toString();
				TextureAtlasSprite templateTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(templateLocation);
				builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, templateTexture, blockTexture, NORTH_Z_FLUID, EnumFacing.NORTH, 0xffffffff));
				builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, templateTexture, blockTexture, SOUTH_Z_FLUID, EnumFacing.SOUTH, 0xffffffff));
				//Cover texture
				String coverLocation = new ResourceLocation("blockarmor:items/block_armor_"+armorType+"_cover").toString();
				TextureAtlasSprite coverTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(coverLocation);
				builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, NORTH_Z_BASE, coverTexture, EnumFacing.NORTH, 0xffffffff));
				builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, SOUTH_Z_BASE, coverTexture, EnumFacing.SOUTH, 0xffffffff));
				((BakedDynBlockArmor)originalModel).quads = builder.build();
			}

			return originalModel;/*

            FluidStack fluidStack = FluidUtil.getFluidContained(stack);

            // not a fluid item apparently
            if (fluidStack == null)
            {
                // empty bucket
                return originalModel;
            }

            BakedDynBlockArmor model = (BakedDynBlockArmor)originalModel;

            Fluid fluid = fluidStack.getFluid();
            String name = fluid.getName();

            if (!model.cache.containsKey(name))
            {
                IModel parent = model.parent.process(ImmutableMap.of("fluid", name));
                Function<ResourceLocation, TextureAtlasSprite> textureGetter;
                textureGetter = new Function<ResourceLocation, TextureAtlasSprite>()
                {
                    public TextureAtlasSprite apply(ResourceLocation location)
                    {
                        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
                    }
                };

                IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.format, textureGetter);
                model.cache.put(name, bakedModel);
                return bakedModel;
            }

            return model.cache.get(name);*/
		}
	}

	// the dynamic bucket is based on the empty bucket
	private static final class BakedDynBlockArmor implements IPerspectiveAwareModel
	{

		private final ModelDynBlockArmor parent;

		private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
		private final ImmutableMap<TransformType, TRSRTransformation> transforms;
		private ImmutableList<BakedQuad> quads;
		private final TextureAtlasSprite particle;
		private final VertexFormat format;

		public BakedDynBlockArmor(ModelDynBlockArmor parent,
				ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms,
				Map<String, IBakedModel> cache)
		{
			this.quads = quads;
			this.particle = particle;
			this.format = format;
			this.parent = parent;
			this.transforms = transforms;
			this.cache = cache;
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
			if(side == null) return quads;
			return ImmutableList.of();
		}

		public boolean isAmbientOcclusion() { return true;  }
		public boolean isGui3d() { return false; }
		public boolean isBuiltInRenderer() { return false; }
		public TextureAtlasSprite getParticleTexture() { return particle; }
		public ItemCameraTransforms getItemCameraTransforms() { return ItemCameraTransforms.DEFAULT; }
	}
}