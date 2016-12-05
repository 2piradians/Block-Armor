package twopiradians.blockArmor.client;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.client.render.item.RenderArmor;
import twopiradians.blockArmor.client.render.model.ModelDynBlockArmor;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.CommonProxy;
import twopiradians.blockArmor.common.block.ModBlocks;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ModItems;

public class ClientProxy extends CommonProxy
{
	private Set<RenderPlayer> addedLayer = Sets.newHashSet();

	@Override
	public void preInit() {
		ModelLoaderRegistry.registerLoader(ModelDynBlockArmor.LoaderDynBlockArmor.INSTANCE);
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {
		ModBlocks.registerRenders();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		if (event.getWorld().isRemote) {
			for (ArmorSet set : ArmorSet.allSets)
				set.initTextures();
			ModItems.registerRenders();
		}
	}

	/**Used to register block textures to override inventory textures*/
	@SubscribeEvent
	public void textureStitch(TextureStitchEvent.Pre event)
	{
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/sugar_canes"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/block_armor_helmet_cover"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/block_armor_helmet_template"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/block_armor_chestplate_cover"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/block_armor_chestplate_template"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/block_armor_leggings_cover"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/block_armor_leggings_template"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/block_armor_boots_cover"));
		event.getMap().registerSprite(new ResourceLocation(BlockArmor.MODID, "items/block_armor_boots_template"));
	}

	@SubscribeEvent
	public void modelBake(ModelBakeEvent event)
	{
		System.out.println("[Block Armor] Baking models...");
		for (ArmorSet set : ArmorSet.allSets) {
			Item[] items = {set.helmet, set.chestplate, set.leggings, set.boots};
			for (Item item : items) {
				ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition()
				{
					@Override
					public ModelResourceLocation getModelLocation(ItemStack stack)
					{
						return ModelDynBlockArmor.LOCATION;
					}
				});
				ModelBakery.registerItemVariants(item, ModelDynBlockArmor.LOCATION);
			}
		}
		System.out.println("[Block Armor] Finished baking models");
	}

	@SubscribeEvent
	public void addRenderLayer(RenderPlayerEvent.Post event)
	{
		if (addedLayer.contains(event.getRenderer())) { return; }
		event.getRenderer().addLayer(new RenderArmor(event.getRenderer()));
		addedLayer.add(event.getRenderer());
	}
}
