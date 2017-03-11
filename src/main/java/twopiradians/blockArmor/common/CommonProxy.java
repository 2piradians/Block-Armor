package twopiradians.blockArmor.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
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
import twopiradians.blockArmor.packet.PacketDisableItems;
import twopiradians.blockArmor.packet.PacketSyncConfig;

public class CommonProxy 
{
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
		registerRecipes();
	}

	private void registerPackets() { // Side is where the packets goes TO
		int id = 0;
		BlockArmor.network.registerMessage(PacketDisableItems.Handler.class, PacketDisableItems.class, id++, Side.SERVER);
		BlockArmor.network.registerMessage(PacketDevColors.Handler.class, PacketDevColors.class, id++, Side.CLIENT);
		BlockArmor.network.registerMessage(PacketActivateSetEffect.Handler.class, PacketActivateSetEffect.class, id++, Side.SERVER);
		BlockArmor.network.registerMessage(PacketSyncConfig.Handler.class, PacketSyncConfig.class, id++, Side.CLIENT);
	}

	private void registerEventListeners() {
		MinecraftForge.EVENT_BUS.register(new Config());
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void registerRecipes() {
		for (ArmorSet set : ArmorSet.allSets) {
			ItemStack stack = set.block == Blocks.EMERALD_BLOCK ? new ItemStack(Items.EMERALD) : set.stack;
			GameRegistry.addShapedRecipe(new ItemStack(set.helmet),"AAA","A A",'A', stack);
			GameRegistry.addShapedRecipe(new ItemStack(set.chestplate),"A A","AAA","AAA",'A', stack);
			GameRegistry.addShapedRecipe(new ItemStack(set.leggings),"AAA","A A","A A",'A', stack);
			GameRegistry.addShapedRecipe(new ItemStack(set.boots),"A A","A A",'A', stack);
		}
	}

	@SubscribeEvent(receiveCanceled = true)
	public void commandDev(CommandEvent event) {
		if (event.getCommand() instanceof CommandDev && 
				event.getCommand().checkPermission(event.getSender().getServer(), event.getSender()) &&
				((CommandDev) event.getCommand()).runCommand(event.getSender().getServer(), event.getSender(), event.getParameters())) 
			event.setCanceled(true);
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
