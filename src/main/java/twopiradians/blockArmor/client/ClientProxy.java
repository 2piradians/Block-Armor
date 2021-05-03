package twopiradians.blockArmor.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import twopiradians.blockArmor.client.key.KeyActivateSetEffect;
import twopiradians.blockArmor.client.model.ModelBAArmor;
import twopiradians.blockArmor.client.model.ModelBAItem;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.CommonProxy;
import twopiradians.blockArmor.common.block.ModBlocks;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.BlockArmorItem;
import twopiradians.blockArmor.common.item.ModItems;
import twopiradians.blockArmor.common.item.TextureOverrideInfo;
import twopiradians.blockArmor.common.item.TextureOverrideInfo.Info;
import twopiradians.blockArmor.common.seteffect.SetEffect;

@SuppressWarnings("deprecation")
@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientProxy {

	/**Map of models to their constructor fields - generated as needed*/
	private static HashMap<String, ModelBAArmor> modelMaps = Maps.newHashMap();
	public static ModelLoader modelLoader;
	private static final Field UNBAKED_MODELS_FIELD;
	private static final Method LOAD_MODEL_METHOD;

	static {
		UNBAKED_MODELS_FIELD = ObfuscationReflectionHelper.findField(ModelBakery.class, "field_217849_F");
		UNBAKED_MODELS_FIELD.setAccessible(true);
		LOAD_MODEL_METHOD = ObfuscationReflectionHelper.findMethod(ModelBakery.class, "func_177594_c", ResourceLocation.class);
		LOAD_MODEL_METHOD.setAccessible(true);
	}

	@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD)
	public static class RegistrationHandler {

		/**Used to register block textures to override inventory textures and for inventory icons*/
		@SubscribeEvent
		public static void textureStitch(TextureStitchEvent.Pre event) {
			if (event.getMap().getTextureLocation() == AtlasTexture.LOCATION_BLOCKS_TEXTURE) {
				//textures for overriding
				for (TextureOverrideInfo override : ArmorSet.TEXTURE_OVERRIDES.values())
					for (Info info : override.overrides.values())
						event.addSprite(info.shortLoc);
				//textures for inventory icons
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_helmet_base"));
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_helmet_cover"));
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_helmet1_template"));
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_helmet2_template"));
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_chestplate_base"));
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_chestplate_cover"));
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_chestplate1_template"));
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_chestplate2_template"));
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_leggings_base"));
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_leggings_cover"));
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_leggings1_template"));
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_leggings2_template"));
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_boots_base"));
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_boots_cover"));
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_boots1_template"));
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_boots2_template"));
			}
		}

		/**Map textures after the last textures (mob_effects) are stitched*/
		@SubscribeEvent
		public static void textureStitch(TextureStitchEvent.Post event) {
			if (event.getMap().getTextureLocation().getPath().equals("textures/atlas/mob_effects.png")) 
				mapTextures();
		}

		@SubscribeEvent
		public static void onModelBake(ModelBakeEvent event) {
			modelLoader = event.getModelLoader();
			ModItems.registerRenders();
			ModBlocks.registerRenders();
		}

		@SubscribeEvent
		public static void onModelRegister(ModelRegistryEvent event) {
			ModelLoaderRegistry.registerLoader(new ResourceLocation(BlockArmor.MODID, "item_loader"), ModelBAItem.LoaderDynBlockArmor.INSTANCE);
		}

	}

	/**Set world time*/
	public static void setWorldTime(World world, long time) {
		if (world instanceof ClientWorld)
			((ClientWorld)world).setDayTime(time);
		else
			CommonProxy.setWorldTime(world, time);
	}

	/**Map block armor items to use assets/blockarmor/models/item/block_armor.json instead of looking for their own jsons
	 * This gets called frequently when loading from BlockMovingLight#getStateContainer (hacky way to get into ModelBakery#processLoading)*/
	public static void mapUnbakedModels() {
		try {
			// get unbaked models map
			Map<ResourceLocation, IUnbakedModel> unbakedModels = (Map<ResourceLocation, IUnbakedModel>) UNBAKED_MODELS_FIELD.get(ModelLoader.instance());
			if (!ArmorSet.allSets.isEmpty() && ArmorSet.allSets.get(0).helmet != null) {
				IUnbakedModel currentModel = unbakedModels.get(new ModelResourceLocation(ArmorSet.allSets.get(0).helmet.getRegistryName(), "inventory"));
				// if current model is null or missing model, replace with block_armor.json model
				if (currentModel == null || currentModel == ModelBakery.MODEL_MISSING) {
					// get block_armor.json unbaked model
					BlockModel model = (BlockModel) LOAD_MODEL_METHOD.invoke(ModelLoader.instance(), new ModelResourceLocation(new ResourceLocation(BlockArmor.MODID, "item/block_armor"), "inventory"));
					// add unbaked model to map so armor items use block_armor.json instead of looking for their registry_name.json
					for (ArmorSet set : ArmorSet.allSets)
						for (EquipmentSlotType slot : ArmorSet.SLOTS) {
							BlockArmorItem item = set.getArmorForSlot(slot);
							unbakedModels.put(new ModelResourceLocation(item.getRegistryName(), "inventory"), model);
						}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setup() {
		// keybinding
		KeyActivateSetEffect.ACTIVATE_SET_EFFECT = new KeyBinding("Activate Set Effect", 82, BlockArmor.MODNAME);
		ClientRegistry.registerKeyBinding(KeyActivateSetEffect.ACTIVATE_SET_EFFECT);
		// resource reload listener
		((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(new ISelectiveResourceReloadListener() {
			@Override
			public void onResourceManagerReload(IResourceManager resourceManager,
					Predicate<IResourceType> resourcePredicate) {
				//Config.syncJEIIngredients();
			}
		});
		// Hoarder container types
		SetEffect.HOARDER.registerScreenContainerTypes();
	}

	/**Get model based on model's constructor parameters*/
	public static Object getBlockArmorModel(LivingEntity entity, int height, int width, int currentFrame, int nextFrame, EquipmentSlotType slot) {
		String key = height+""+width+""+currentFrame+""+nextFrame+""+slot.getName();
		ModelBAArmor model = modelMaps.get(key);
		if (model == null) {
			model = new ModelBAArmor(height, width, currentFrame, nextFrame, slot);
			modelMaps.put(key, model);
		}
		model.entity = entity;
		return model;
	}

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event) {
		//manage all animated set's frames (ticks at same rate as TextureAtlasSprite's updateAnimation())
		if (event.side == LogicalSide.CLIENT) 
			for (ArmorSet set : ArmorSet.allSets) 
				for (int i=0; i<4; i++) { //through valid slots
					if (set.animations != null && set.animations[i] != null) {//if animated
						set.frames[i] += 0.5f/set.animations[i].getFrameTimeSingle((int) set.frames[i]);
						if (set.frames[i] >= set.animations[i].getFrameCount())
							set.frames[i] -= set.animations[i].getFrameCount();
					}
				}
	}

	/**Resets model and item quads and maps block textures (called when client joins world or resource pack loaded)*/
	public static void mapTextures() {
		// reset model and item quad maps
		modelMaps = Maps.newHashMap();

		// find block textures
		ArrayList<ArmorSet> setsToDisable = new ArrayList<ArmorSet>();
		int numTextures = 0;
		for (ArmorSet set : ArmorSet.allSets) {
			Tuple<Integer, Boolean> tup = set.initTextures();
			numTextures += tup.getA();
			if (tup.getB())
				setsToDisable.add(set);
		}

		// textures not loaded yet
		if (numTextures == 0) 
			return;
		// disable sets with missing textures
		else if (!setsToDisable.isEmpty()) { 
			int disabledSets = 0;
			for (ArmorSet set : setsToDisable) {
				set.missingTextures = true;
				set.disable();
				disabledSets++;
			}
			BlockArmor.LOGGER.info("Disabled "+disabledSets+" armor set"+(disabledSets > 1 ? "s" : "")+" without textures");
		}

		BlockArmor.LOGGER.info("Found "+numTextures+" block textures for Block Armor");

		//create inventory icons
		int numIcons = ModelBAItem.BakedDynBlockArmorOverrideHandler.createInventoryIcons(ClientProxy.modelLoader);
		BlockArmor.LOGGER.info("Created "+numIcons+" inventory icons for Block Armor");
	}

}