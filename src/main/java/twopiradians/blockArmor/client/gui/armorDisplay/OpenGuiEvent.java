package twopiradians.blockArmor.client.gui.armorDisplay;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.common.BlockArmor;

public class OpenGuiEvent 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onConfigChanged(ClientChatReceivedEvent event) 
	{
		if (BlockArmor.DISPLAY_ARMOR_GUI) 
			Minecraft.getMinecraft().displayGuiScreen(new GuiArmorDisplay());
	}
}
