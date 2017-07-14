package twopiradians.blockArmor.client;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraftforge.fml.common.Mod;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import twopiradians.blockArmor.client.gui.armorDisplay.OpenGuiEvent;
import twopiradians.blockArmor.client.gui.config.GuiConfigUpdater;
import twopiradians.blockArmor.client.key.KeyActivateSetEffect;
import twopiradians.blockArmor.client.model.ModelBlockArmor;
import twopiradians.blockArmor.client.model.ModelDynBlockArmor;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.CommonProxy;
import twopiradians.blockArmor.common.block.ModBlocks;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ModItems;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	/**Map of models to their constructor fields - generated as needed*/
	private HashMap<String, ModelBlockArmor> modelMaps = Maps.newHashMap();

	@Override
	public void preInit(FMLPreInitializationEvent event) 
	{
		super.preInit(event);
		KeyActivateSetEffect.ACTIVATE_SET_EFFECT = new KeyBinding("Activate Set Effect", Keyboard.KEY_R, BlockArmor.MODNAME);
		ModelLoaderRegistry.registerLoader(ModelDynBlockArmor.LoaderDynBlockArmor.INSTANCE);
	}

	@Override
	public void init(FMLInitializationEvent event) 
	{
		super.init(event);
		MinecraftForge.EVENT_BUS.register(BlockArmor.key);
		ClientRegistry.registerKeyBinding(KeyActivateSetEffect.ACTIVATE_SET_EFFECT);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) 
	{
		super.postInit(event);
		ModBlocks.registerRenders();
		ModItems.registerRenders();
	}

	/**Get model based on model's constructor parameters*/
	@Override
	public Object getBlockArmorModel(int height, int width, int currentFrame, int nextFrame, EntityEquipmentSlot slot) 
	{
		String key = height+""+width+""+currentFrame+""+nextFrame+""+slot.getName();
		ModelBlockArmor model = modelMaps.get(key);
		if (model == null) {
			model = new ModelBlockArmor(height, width, currentFrame, nextFrame, slot);
			modelMaps.put(key, model);
		}
		return model;
	}

	@Override
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		//set MC to map textures when resources reloaded
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new IResourceManagerReloadListener() {
			@Override
			public void onResourceManagerReload(IResourceManager resourceManager) {
				mapTextures();
				Config.syncJEIBlacklist();
			}
		});
	}

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event)
	{
		//manage all animated set's frames (ticks at same rate as TextureAtlasSprite's updateAnimation())
		if (!Minecraft.getMinecraft().isGamePaused() && event.side == Side.CLIENT) 
			for (ArmorSet set : ArmorSet.allSets) 
				for (int i=0; i<4; i++) { //through valid slots
					if (set.animations[i] != null) {//if animated
						set.frames[i] += 0.5f/set.animations[i].getFrameTimeSingle((int) set.frames[i]);
						if (set.frames[i] >= set.animations[i].getFrameCount())
							set.frames[i] -= set.animations[i].getFrameCount();
					}
				}
	}

	/**Resets model and item quads and maps block textures (called when client joins world or resource pack loaded)*/
	private void mapTextures() 
	{
		//reset model and item quad maps
		modelMaps = Maps.newHashMap();

		//find block textures
		ArrayList<ArmorSet> setsToDisable = new ArrayList<ArmorSet>();
		int numTextures = 0;
		for (ArmorSet set : ArmorSet.allSets) {
			Tuple<Integer, Boolean> tup = set.initTextures();
			numTextures += tup.getFirst();
			if (tup.getSecond())
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
				BlockArmor.logger.info("Disabled "+disabledSets+" armor set"+(disabledSets > 1 ? "s" : "")+" without textures");
		}

		BlockArmor.logger.info("Found "+numTextures+" block textures for Block Armor");

		//create inventory icons
		int numIcons = ModelDynBlockArmor.BakedDynBlockArmorOverrideHandler.createInventoryIcons();
		BlockArmor.logger.info("Created "+numIcons+" inventory icons for Block Armor");
	}

	/**Used to register block textures to override inventory textures and for inventory icons*/
	@SubscribeEvent
	public static void textureStitch(TextureStitchEvent.Pre event)
	{
		//textures for overriding
		for (Item item : ArmorSet.TEXTURE_OVERRIDES)
			for (EntityEquipmentSlot slot : ArmorSet.SLOTS)
				event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/overrides/"+
						item.getRegistryName().getResourcePath().toLowerCase().replace(" ", "_")+"_"+slot.getName()));

		//textures for inventory icons
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_helmet_base"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_helmet_cover"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_helmet1_template"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_helmet2_template"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_chestplate_base"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_chestplate_cover"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_chestplate1_template"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_chestplate2_template"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_leggings_base"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_leggings_cover"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_leggings1_template"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_leggings2_template"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_boots_base"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_boots_cover"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_boots1_template"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/icons/block_armor_boots2_template"));
	}
}