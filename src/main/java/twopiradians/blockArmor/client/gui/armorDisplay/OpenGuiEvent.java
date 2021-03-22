package twopiradians.blockArmor.client.gui.armorDisplay;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class OpenGuiEvent {
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public static void onConfigChanged(ClientChatReceivedEvent event) {
		//if (GuiArmorDisplay.DISPLAY_ARMOR_GUI) 
		//	Minecraft.getInstance().displayGuiScreen(new GuiArmorDisplay());
	}
	
}
