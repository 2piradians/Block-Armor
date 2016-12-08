package twopiradians.blockArmor.client;

import java.util.HashMap;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.client.model.ModelBlockArmor;
import twopiradians.blockArmor.client.model.ModelDynBlockArmor;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.CommonProxy;
import twopiradians.blockArmor.common.block.ModBlocks;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ModItems;

public class ClientProxy extends CommonProxy
{
	private HashMap<String, ModelBlockArmor> modelMaps = Maps.newHashMap();

	@Override
	public void preInit() {
		ModelLoaderRegistry.registerLoader(ModelDynBlockArmor.LoaderDynBlockArmor.INSTANCE);
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {
		ModBlocks.registerRenders();
		ModItems.registerRenders();
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public Object getBlockArmorModel(int height, int width, boolean isTranslucent, int frame) {
		String key = height+""+width+""+isTranslucent+""+frame;
		ModelBlockArmor model = modelMaps.get(key);
		if (model == null) {
			model = new ModelBlockArmor(height, width, isTranslucent, frame);
			modelMaps.put(key, model);
		}
		return model;
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		if (event.getWorld().isRemote) {
			for (ArmorSet set : ArmorSet.allSets)
				set.initTextures();
		}
	}

	/**Used to register block textures to override inventory textures and for inventory icons*/
	@SubscribeEvent
	public void textureStitch(TextureStitchEvent.Pre event)
	{
		System.out.println("texture stitch pre");

		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/sugar_canes"));
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
	
	/**Used to register block textures to override inventory textures and for inventory icons*/
	@SubscribeEvent
	public void textureStitch(TextureStitchEvent.Post event)
	{
		System.out.println("texture stitch post");
		
		if (Minecraft.getMinecraft().thePlayer != null) {
			System.out.println("initializing textures");
			for (ArmorSet set : ArmorSet.allSets)
				set.initTextures();
		}
	}
}
