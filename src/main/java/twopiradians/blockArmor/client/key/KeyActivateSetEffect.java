package twopiradians.blockArmor.client.key;

import java.util.HashMap;
import java.util.UUID;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.packet.CActivateSetEffectPacket;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class KeyActivateSetEffect 
{

	public static KeyBinding ACTIVATE_SET_EFFECT;
	/**True if key is pressed down*/
	public static HashMap<UUID, Boolean> isKeyDown = Maps.newHashMap();

	public KeyActivateSetEffect() {}
	
	public boolean isKeyDown(PlayerEntity player) {
		if (player != null)
			return isKeyDown.containsKey(player.getUniqueID()) ? isKeyDown.get(player.getUniqueID()) : false;
		return false;
	}

	@SubscribeEvent

	public static void playerTick(ClientTickEvent event) {
		if (event.phase == Phase.END && Minecraft.getInstance().player != null) {
			UUID player = Minecraft.getInstance().player.getUniqueID();
			if (!isKeyDown.containsKey(player) || ACTIVATE_SET_EFFECT.isKeyDown() != isKeyDown.get(player)) {
				isKeyDown.put(player, ACTIVATE_SET_EFFECT.isKeyDown());
				BlockArmor.NETWORK.sendToServer(new CActivateSetEffectPacket(ACTIVATE_SET_EFFECT.isKeyDown(), player));
			}
		}
	}
}
