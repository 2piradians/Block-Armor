package twopiradians.blockArmor.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import twopiradians.blockArmor.client.gui.armorDisplay.OpenGuiEvent;
import twopiradians.blockArmor.client.model.ModelBlockArmor;
import twopiradians.blockArmor.client.model.ModelDynBlockArmor;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.CommonProxy;
import twopiradians.blockArmor.common.block.ModBlocks;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ModItems;
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

	@Override
	public Object getBlockArmorModel(int height, int width, boolean isTranslucent, int frame, EntityEquipmentSlot slot) {
		String key = height+""+width+""+isTranslucent+""+frame+""+slot.getName();
		ModelBlockArmor model = modelMaps.get(key);
		if (model == null) {
			model = new ModelBlockArmor(height, width, isTranslucent, frame, slot);
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
		//set MC to map textures when resources reloaded
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new IResourceManagerReloadListener() {
			@Override
			public void onResourceManagerReload(IResourceManager resourceManager) {
				mapTextures();
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
	}

	/**Resets model and item quads and maps block textures (called when client joins world or resource pack loaded)*/
	private void mapTextures() {
		//reset model and item quad maps
		modelMaps = Maps.newHashMap();

		//find block textures
		int numTextures = 0;
		ArmorSet.disabledItems = new ArrayList<Item>();
		for (ArmorSet set : ArmorSet.allSets) 
			numTextures += set.initTextures();

		//textures not loaded yet
		if (numTextures == 0)
			return;

		BlockArmor.logger.info("Found "+numTextures+" block textures for Block Armor");

		//send disabled sets to server and remove their recipes and remove them from creative tabs/JEI
		this.sendDisablePacket = true;
		ArmorSet.disableItems();
		if (BlockArmor.jeiPlugin != null)
			BlockArmor.jeiPlugin.removeDisabledItems();

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
