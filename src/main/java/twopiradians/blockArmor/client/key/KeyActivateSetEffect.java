package twopiradians.blockArmor.client.key;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.packet.PacketActivateSetEffect;

public class KeyActivateSetEffect 
{
	@SideOnly(Side.CLIENT)
	public static KeyBinding ACTIVATE_SET_EFFECT;
	/**True for initial button press (used for one-time activation)*/
	public boolean isKeyPressed;
	/**True if key is pressed down (used for holding key)*/
	public boolean isKeyDown;

	public KeyActivateSetEffect() {}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void playerTick(ClientTickEvent event) {
		if (event.phase == Phase.END) {
			boolean isPressed = ACTIVATE_SET_EFFECT.isPressed();
			boolean isDown = ACTIVATE_SET_EFFECT.isKeyDown();
			if (isPressed != isKeyPressed || isDown != isKeyDown) {
				isKeyPressed = isPressed;
				isKeyDown = isDown;
				BlockArmor.network.sendToServer(new PacketActivateSetEffect(isKeyPressed, isKeyDown));
			}
		}
	}
}
