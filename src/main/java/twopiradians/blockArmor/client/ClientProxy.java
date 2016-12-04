package twopiradians.blockArmor.client;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.client.render.item.RenderArmor;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.CommonProxy;
import twopiradians.blockArmor.common.block.ModBlocks;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ModItems;

public class ClientProxy extends CommonProxy
{
	private Set<RenderPlayer> addedLayer = Sets.newHashSet();

	@Override
	public void preInit() {}

	@Override
	public void init() {}

	@Override
	public void postInit() {
		//ModItems.registerRenders();
		ModBlocks.registerRenders();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		if (event.getWorld().isRemote) {
			System.out.println("registering item renders");
			for (ArmorSet set : ArmorSet.allSets)
				set.initTextures();
			ModItems.registerRenders();
		}
	}

	@SubscribeEvent
	public void textureStitch(TextureStitchEvent.Post event)
	{
		System.out.println("texture stitching");
		//event.getMap().getAtlasSprite("");
		ResourceLocation loc = new ResourceLocation(BlockArmor.MODID, "auto_generated_boots");
		event.getMap().setTextureEntry(new ResourceLocation(BlockArmor.MODID, "auto_generated_boots").toString(), event.getMap().getAtlasSprite("minecraft:items/shears"));
		event.getMap().setTextureEntry(BlockArmor.MODID+":items/auto_generated_boots", event.getMap().getAtlasSprite("minecraft:items/shears"));
		event.getMap().setTextureEntry(BlockArmor.MODID+":items/auto_generated_boots.json", event.getMap().getAtlasSprite("minecraft:items/shears"));
		event.getMap().setTextureEntry("blockarmor:items/andesite_chestplate", event.getMap().getAtlasSprite("minecraft:items/shears"));
		event.getMap().setTextureEntry("blockarmor:items/andesite_chestplate.png", event.getMap().getAtlasSprite("minecraft:items/shears"));
	}

	@SubscribeEvent
	public void addRenderLayer(RenderPlayerEvent.Post event)
	{
		if (addedLayer.contains(event.getRenderer())) { return; }
		event.getRenderer().addLayer(new RenderArmor(event.getRenderer()));
		addedLayer.add(event.getRenderer());
	}
}
