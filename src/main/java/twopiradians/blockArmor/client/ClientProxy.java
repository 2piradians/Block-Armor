package twopiradians.blockArmor.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import twopiradians.blockArmor.client.gui.armorDisplay.OpenGuiEvent;
import twopiradians.blockArmor.client.model.ModelBlockArmor;
import twopiradians.blockArmor.client.model.ModelDynBlockArmor;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.CommonProxy;
import twopiradians.blockArmor.common.block.ModBlocks;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ModItems;
import twopiradians.blockArmor.jei.BlockArmorJEIPlugin;
import twopiradians.blockArmor.packets.DisableItemsPacket;

public class ClientProxy extends CommonProxy
{
	/**Map of models to their constructor fields - generated as needed*/
	private HashMap<String, ModelBlockArmor> modelMaps = Maps.newHashMap();
	/**Send disable packet next tick (bc can't send packets on world load)*/
	private boolean sendDisablePacket;

	@Override
	public void preInit() {
		ModelLoaderRegistry.registerLoader(ModelDynBlockArmor.LoaderDynBlockArmor.INSTANCE);
	}

	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new OpenGuiEvent());
	}

	@Override
	public void postInit() {
		ModBlocks.registerRenders();
		ModItems.registerRenders();
	}

	/**Get model based on model's constructor parameters*/
	@Override
	public Object getBlockArmorModel(int height, int width, int currentFrame, int nextFrame, EntityEquipmentSlot slot) {
		String key = height+""+width+""+currentFrame+""+nextFrame+""+slot.getName();
		ModelBlockArmor model = modelMaps.get(key);
		if (model == null) {
			model = new ModelBlockArmor(height, width, currentFrame, nextFrame, slot);
			modelMaps.put(key, model);
		}
		return model;
	}

	@SubscribeEvent
	public void worldLoad(WorldEvent.Load event)
	{
		//tell server to remove recipes of disabled items next tick
		this.sendDisablePacket = true;
	}

	@Override
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		//set MC to map textures and reload JEI (if present) when resources reloaded
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new IResourceManagerReloadListener() {
			@Override
			public void onResourceManagerReload(IResourceManager resourceManager) {
				mapTextures();

				//If there are disabled items, reload JEI to make sure they're added to the JEI blacklist
				if (ArmorSet.disabledItems != null && !ArmorSet.disabledItems.isEmpty() &&
						Loader.isModLoaded("JEI") && BlockArmorJEIPlugin.helpers != null)
					try {//reload only seems to be needed when compiled
						BlockArmor.logger.info("Reloading JEI...");
						BlockArmorJEIPlugin.helpers.reload();
					} catch (Exception e) {
						BlockArmor.logger.error("Another mod caused an exception while reloading JEI: ", e);
					}
			}
		});
	}

	@SubscribeEvent
	public void clientTick(TickEvent.ClientTickEvent event)
	{
		//send server packet to remove recipes for disabled items when player loaded (can't send packet on world load)
		if (sendDisablePacket && Minecraft.getMinecraft().thePlayer != null) {
			this.sendDisablePacket = false;
			BlockArmor.network.sendToServer(new DisableItemsPacket(ArmorSet.disabledItems));
		}

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
	private void mapTextures() {
		//reset model and item quad maps
		modelMaps = Maps.newHashMap();

		//find block textures
		int numTextures = 0;
		ArmorSet.disabledItems = new ArrayList<ItemStack>();
		for (ArmorSet set : ArmorSet.allSets) 
			numTextures += set.initTextures();

		//textures not loaded yet
		if (numTextures == 0) {
			BlockArmor.logger.info("Textures not loaded yet, clearing disabled items");
			ArmorSet.disabledItems.clear();
			return;
		}

		BlockArmor.logger.info("Found "+numTextures+" block textures for Block Armor");

		//send disabled sets to server and remove their recipes and remove them from creative tabs/JEI
		this.sendDisablePacket = true;
		ArmorSet.disableItems();

		//create inventory icons
		int numIcons = ModelDynBlockArmor.BakedDynBlockArmorOverrideHandler.createInventoryIcons();
		BlockArmor.logger.info("Created "+numIcons+" inventory icons for Block Armor");
	}

	/**Used to register block textures to override inventory textures and for inventory icons*/
	@SubscribeEvent
	public void textureStitch(TextureStitchEvent.Pre event)
	{
		//textures for overriding
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/reeds"));

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
