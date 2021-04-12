package twopiradians.blockArmor.client.key;

import java.util.HashMap;
import java.util.UUID;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.packet.CActivateSetEffectPacket;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class KeyActivateSetEffect {

	@OnlyIn(Dist.CLIENT)
	public static KeyBinding ACTIVATE_SET_EFFECT;
	/**True if key is pressed down*/
	public static HashMap<UUID, Boolean> isKeyDown = Maps.newHashMap();

	public KeyActivateSetEffect() {}
	
	/**Is this player pressing/holding down the set effects key*/
	public boolean isKeyDown(PlayerEntity player) {
		if (player == null)
			return false;
		Boolean keyDown = isKeyDown.get(player.getUniqueID());
		return keyDown != null && keyDown.booleanValue() == true;
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
