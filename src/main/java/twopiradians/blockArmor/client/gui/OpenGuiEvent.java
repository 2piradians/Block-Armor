package twopiradians.blockArmor.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.command.CommandDev;

@Mod.EventBusSubscriber
public class OpenGuiEvent {

	@SuppressWarnings("unused")
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public static void onChat(ClientChatReceivedEvent event) {
		if (GuiArmorDisplay.DISPLAY_ARMOR_GUI && CommandDev.DEVS.contains(event.getSenderUUID())) 
			Minecraft.getInstance().displayGuiScreen(new GuiArmorDisplay());
	}

}
