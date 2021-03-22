package twopiradians.blockArmor.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.Maps;
import com.sun.glass.events.KeyEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import twopiradians.blockArmor.client.key.KeyActivateSetEffect;
import twopiradians.blockArmor.client.model.ModelBlockArmor;
import twopiradians.blockArmor.client.model.ModelDynBlockArmor;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.block.ModBlocks;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ModItems;

@SuppressWarnings("deprecation")
@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientProxy
{
	/**Map of models to their constructor fields - generated as needed*/
	private static HashMap<String, ModelBlockArmor> modelMaps = Maps.newHashMap();

	/**Set world time*/
	public static void setWorldTime(World world, long time) {
		if (world instanceof ClientWorld)
			((ClientWorld)world).setDayTime(time);
	}

	public static void onSetup(FMLClientSetupEvent event) {
		event.enqueueWork(ClientProxy::setup);
	}
	
	public static void setup() {
		mapTextures();
		KeyActivateSetEffect.ACTIVATE_SET_EFFECT = new KeyBinding("Activate Set Effect", KeyEvent.getKeyCodeForChar('R'), BlockArmor.MODNAME);
		ClientRegistry.registerKeyBinding(KeyActivateSetEffect.ACTIVATE_SET_EFFECT);
		ModBlocks.registerRenders();
		ModItems.registerRenders();
	}
	
	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		ModelLoaderRegistry.registerLoader(new ResourceLocation(BlockArmor.MODID, "blockArmorModels"), ModelDynBlockArmor.LoaderDynBlockArmor.INSTANCE);
	}

	/**Get model based on model's constructor parameters*/
	public static Object getBlockArmorModel(int height, int width, int currentFrame, int nextFrame, EquipmentSlotType slot) {
		String key = height+""+width+""+currentFrame+""+nextFrame+""+slot.getName();
		ModelBlockArmor model = modelMaps.get(key);
		if (model == null) {
			model = new ModelBlockArmor(height, width, currentFrame, nextFrame, slot);
			modelMaps.put(key, model);
		}
		return model;
	}

	@SubscribeEvent
	public static void loadComplete(FMLLoadCompleteEvent event) {
		//set MC to map textures when resources reloaded
		((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(new IResourceManagerReloadListener() {
			@Override
			public void onResourceManagerReload(IResourceManager resourceManager) {
				mapTextures();
				//Config.syncJEIIngredients(); TODO
			}
		});
	}

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event) {
		//manage all animated set's frames (ticks at same rate as TextureAtlasSprite's updateAnimation())
		if (!Minecraft.getInstance().isGamePaused() && event.side == LogicalSide.CLIENT) 
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
	private static void mapTextures() {
		//reset model and item quad maps
		modelMaps = Maps.newHashMap();

		//find block textures
		ArrayList<ArmorSet> setsToDisable = new ArrayList<ArmorSet>();
		int numTextures = 0;
		for (ArmorSet set : ArmorSet.allSets) {
			Tuple<Integer, Boolean> tup = set.initTextures();
			numTextures += tup.getA();
			if (tup.getB())
				setsToDisable.add(set);
		}

		//textures not loaded yet
		if (numTextures == 0) 
			return;
		else if (!setsToDisable.isEmpty()) { //disable sets with missing textures
			int disabledSets = 0;
			for (ArmorSet set : setsToDisable) {
				set.missingTextures = true;
				set.disable();
				disabledSets++;
			}
			if (disabledSets > 0)
				BlockArmor.LOGGER.info("Disabled "+disabledSets+" armor set"+(disabledSets > 1 ? "s" : "")+" without textures");
		}

		BlockArmor.LOGGER.info("Found "+numTextures+" block textures for Block Armor");

		//create inventory icons
		int numIcons = ModelDynBlockArmor.BakedDynBlockArmorOverrideHandler.createInventoryIcons();
		BlockArmor.LOGGER.info("Created "+numIcons+" inventory icons for Block Armor");
	}

	/**Used to register block textures to override inventory textures and for inventory icons*/
	@SubscribeEvent
	public static void textureStitch(TextureStitchEvent.Pre event) {
		//textures for overriding
		for (Item item : ArmorSet.TEXTURE_OVERRIDES)
			for (EquipmentSlotType slot : ArmorSet.SLOTS)
				event.addSprite(new ResourceLocation(BlockArmor.MODID, "items/overrides/"+
						item.getRegistryName().getPath().toLowerCase().replace(" ", "_")+"_"+slot.getName()));

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