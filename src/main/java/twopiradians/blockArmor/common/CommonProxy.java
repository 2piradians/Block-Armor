package twopiradians.blockArmor.common;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import twopiradians.blockArmor.common.block.ModBlocks;
import twopiradians.blockArmor.common.command.CommandDev;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ModItems;
import twopiradians.blockArmor.common.seteffect.SetEffect;
import twopiradians.blockArmor.common.tileentity.ModTileEntities;
import twopiradians.blockArmor.packet.PacketActivateSetEffect;
import twopiradians.blockArmor.packet.PacketDevColors;
import twopiradians.blockArmor.packet.PacketSyncConfig;

public class CommonProxy 
{
	public ArrayList<ItemStack> itemsToDisable = new ArrayList<ItemStack>();
	
	public void preInit(FMLPreInitializationEvent event) {
		registerPackets();
		BlockArmor.configFile = event.getSuggestedConfigurationFile();
		BlockArmor.logger = event.getModLog();
		ModBlocks.preInit();
		ModTileEntities.preInit();
	}

	public void init(FMLInitializationEvent event) {
		registerEventListeners();
	}

	public void postInit(FMLPostInitializationEvent event) {
		ArmorSet.postInit();
		SetEffect.postInit();
		Config.postInit(BlockArmor.configFile);
		ModItems.postInit();
		Config.syncConfig();
	}

	private void registerPackets() { // Side is where the packet goes TO
		int id = 0;
		BlockArmor.network.registerMessage(PacketDevColors.Handler.class, PacketDevColors.class, id++, Side.CLIENT);
		BlockArmor.network.registerMessage(PacketActivateSetEffect.Handler.class, PacketActivateSetEffect.class, id++, Side.SERVER);
		BlockArmor.network.registerMessage(PacketSyncConfig.Handler.class, PacketSyncConfig.class, id++, Side.CLIENT);
	}

	private void registerEventListeners() {
		MinecraftForge.EVENT_BUS.register(new Config());
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent(receiveCanceled = true)
	public void commandDev(CommandEvent event) {
		try {
		if (event.getCommand().getName().equalsIgnoreCase("dev") && 
				event.getCommand().checkPermission(event.getSender().getServer(), event.getSender()) &&
				CommandDev.runCommand(event.getSender().getServer(), event.getSender(), event.getParameters())) 
			event.setCanceled(true);
		}
		catch (Exception e) {}
	}

	@SubscribeEvent
	public void playerJoin(PlayerLoggedInEvent event) {
		if (!event.player.world.isRemote && event.player instanceof EntityPlayerMP)
			BlockArmor.network.sendTo(new PacketDevColors(), (EntityPlayerMP) event.player);
	}

	public Object getBlockArmorModel(int height, int width, int currentFrame, int nextFrame, EntityEquipmentSlot slot) {
		return null;
	}
	public void loadComplete(FMLLoadCompleteEvent event) {}
}
