package twopiradians.blockArmor.client.key;

import java.util.HashMap;
import java.util.UUID;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.packet.PacketActivateSetEffect;

@Mod.EventBusSubscriber(Side.CLIENT)
public class KeyActivateSetEffect 
{
	@SideOnly(Side.CLIENT)
	public static KeyBinding ACTIVATE_SET_EFFECT;
	/**True if key is pressed down*/
	public static HashMap<UUID, Boolean> isKeyDown = Maps.newHashMap();

	public KeyActivateSetEffect() {}
	
	public boolean isKeyDown(EntityPlayer player) {
		if (player != null)
			return isKeyDown.containsKey(player.getPersistentID()) ? isKeyDown.get(player.getPersistentID()) : false;
		return false;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void playerTick(ClientTickEvent event) {
		if (event.phase == Phase.END && Minecraft.getMinecraft().player != null) {
			UUID player = Minecraft.getMinecraft().player.getPersistentID();
			if (!isKeyDown.containsKey(player) || ACTIVATE_SET_EFFECT.isKeyDown() != isKeyDown.get(player)) {
				isKeyDown.put(player, ACTIVATE_SET_EFFECT.isKeyDown());
				BlockArmor.network.sendToServer(new PacketActivateSetEffect(ACTIVATE_SET_EFFECT.isKeyDown(), player));
			}
		}
	}
}
