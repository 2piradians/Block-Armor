package twopiradians.blockArmor.packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemCooldowns.CooldownInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.NetworkEvent;

public class SSyncCooldownsPacket {

	private static final Field COOLDOWNS_FIELD = ObfuscationReflectionHelper.findField(ItemCooldowns.class, "f_41515_");
	private static final Field COOLDOWNS_TICKS_FIELD = ObfuscationReflectionHelper.findField(ItemCooldowns.class, "f_41516_");
	private static final Constructor<CooldownInstance> COOLDOWNS_CONSTRUCTOR = ObfuscationReflectionHelper.findConstructor(ItemCooldowns.CooldownInstance.class, int.class, int.class);
	private static final Field COOLDOWNS_CREATE_TICKS_FIELD = ObfuscationReflectionHelper.findField(ItemCooldowns.CooldownInstance.class, "f_41533_");
	private static final Field COOLDOWNS_EXPIRE_TICKS_FIELD = ObfuscationReflectionHelper.findField(ItemCooldowns.CooldownInstance.class, "f_41534_");

	private Map<Item, ItemCooldowns.CooldownInstance> cooldowns;
	private int ticks;

	public SSyncCooldownsPacket() {}

	public SSyncCooldownsPacket(Player player) {
		try {
			cooldowns = (Map<Item, CooldownInstance>) COOLDOWNS_FIELD.get(player.getCooldowns());
			ticks = COOLDOWNS_TICKS_FIELD.getInt(player.getCooldowns());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public static void encode(SSyncCooldownsPacket packet, FriendlyByteBuf buf) {
		try {
			if (packet.cooldowns != null) {
				buf.writeInt(packet.ticks);
				buf.writeInt(packet.cooldowns.size());
				for (Item item : packet.cooldowns.keySet()) {
					buf.writeItem(new ItemStack(item));
					ItemCooldowns.CooldownInstance cooldown = packet.cooldowns.get(item);
					buf.writeInt(COOLDOWNS_CREATE_TICKS_FIELD.getInt(cooldown));
					buf.writeInt(COOLDOWNS_EXPIRE_TICKS_FIELD.getInt(cooldown));
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static SSyncCooldownsPacket decode(FriendlyByteBuf buf) {
		SSyncCooldownsPacket packet = new SSyncCooldownsPacket();
		packet.cooldowns = Maps.newHashMap();
		try {
			packet.ticks = buf.readInt();
			int count = buf.readInt();
			for (int i=0; i<count; ++i) 
				packet.cooldowns.put(buf.readItem().getItem(),
						COOLDOWNS_CONSTRUCTOR.newInstance(buf.readInt(), buf.readInt()));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return packet;
	}

	public static class Handler {

		public static void handle(SSyncCooldownsPacket packet, Supplier<NetworkEvent.Context> ctx) {
			try {
				COOLDOWNS_TICKS_FIELD.set(Minecraft.getInstance().player.getCooldowns(), packet.ticks);
				Map<Item, ItemCooldowns.CooldownInstance> cooldowns = (Map<Item, CooldownInstance>) COOLDOWNS_FIELD.get(Minecraft.getInstance().player.getCooldowns());
				cooldowns.clear();
				for (Item item : packet.cooldowns.keySet())
					cooldowns.put(item, packet.cooldowns.get(item));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			ctx.get().setPacketHandled(true);
		}

	}
}