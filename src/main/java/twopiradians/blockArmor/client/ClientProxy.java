package twopiradians.blockArmor.client;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.client.render.item.RenderArmor;
import twopiradians.blockArmor.common.CommonProxy;
import twopiradians.blockArmor.common.block.ModBlocks;
import twopiradians.blockArmor.common.item.ModItems;

public class ClientProxy extends CommonProxy
{
	private Set<RenderPlayer> addedLayer = Sets.newHashSet();

	@Override
	public void preInit() {}
	
	@Override
	public void init() {
		registerRenders();
	}
	
	@Override
	public void postInit() {}

	public void registerRenders()
	{
		ModItems.registerRenders();
		ModBlocks.registerRenders();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void addRenderLayer(RenderPlayerEvent.Post event)
	{
		if (addedLayer.contains(event.getRenderer())) { return; }
		event.getRenderer().addLayer(new RenderArmor(event.getRenderer()));
		addedLayer.add(event.getRenderer());
	}
}
