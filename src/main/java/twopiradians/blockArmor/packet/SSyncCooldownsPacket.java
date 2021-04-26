package twopiradians.blockArmor.packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.CooldownTracker.Cooldown;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.NetworkEvent;

public class SSyncCooldownsPacket {

	private static final Field COOLDOWNS_FIELD;
	private static final Field COOLDOWNS_TICKS_FIELD;
	private static final Constructor<Cooldown> COOLDOWNS_CONSTRUCTOR;
	private static final Field COOLDOWNS_CREATE_TICKS_FIELD;
	private static final Field COOLDOWNS_EXPIRE_TICKS_FIELD;

	static {
		COOLDOWNS_FIELD = ObfuscationReflectionHelper.findField(CooldownTracker.class, "field_185147_a");
		COOLDOWNS_FIELD.setAccessible(true);
		COOLDOWNS_TICKS_FIELD = ObfuscationReflectionHelper.findField(CooldownTracker.class, "field_185148_b");
		COOLDOWNS_TICKS_FIELD.setAccessible(true);
		COOLDOWNS_CONSTRUCTOR = ObfuscationReflectionHelper.findConstructor(CooldownTracker.Cooldown.class, CooldownTracker.class, int.class, int.class);
		COOLDOWNS_CONSTRUCTOR.setAccessible(true);
		COOLDOWNS_CREATE_TICKS_FIELD = ObfuscationReflectionHelper.findField(CooldownTracker.Cooldown.class, "field_185137_a");
		COOLDOWNS_CREATE_TICKS_FIELD.setAccessible(true);
		COOLDOWNS_EXPIRE_TICKS_FIELD = ObfuscationReflectionHelper.findField(CooldownTracker.Cooldown.class, "field_185138_b");
		COOLDOWNS_EXPIRE_TICKS_FIELD.setAccessible(true);
	}

	private Map<Item, CooldownTracker.Cooldown> cooldowns;
	private int ticks;

	public SSyncCooldownsPacket() {}

	public SSyncCooldownsPacket(PlayerEntity player) {
		try {
			cooldowns = (Map<Item, Cooldown>) COOLDOWNS_FIELD.get(player.getCooldownTracker());
			ticks = COOLDOWNS_TICKS_FIELD.getInt(player.getCooldownTracker());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public static void encode(SSyncCooldownsPacket packet, PacketBuffer buf) {
		try {
			if (packet.cooldowns != null) {
				buf.writeInt(packet.ticks);
				buf.writeInt(packet.cooldowns.size());
				for (Item item : packet.cooldowns.keySet()) {
					buf.writeItemStack(new ItemStack(item));
					CooldownTracker.Cooldown cooldown = packet.cooldowns.get(item);
					buf.writeInt(COOLDOWNS_CREATE_TICKS_FIELD.getInt(cooldown));
					buf.writeInt(COOLDOWNS_EXPIRE_TICKS_FIELD.getInt(cooldown));
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static SSyncCooldownsPacket decode(PacketBuffer buf) {
		SSyncCooldownsPacket packet = new SSyncCooldownsPacket();
		packet.cooldowns = Maps.newHashMap();
		try {
			CooldownTracker tracker = new CooldownTracker();
			packet.ticks = buf.readInt();
			int count = buf.readInt();
			for (int i=0; i<count; ++i) 
				packet.cooldowns.put(buf.readItemStack().getItem(),
						COOLDOWNS_CONSTRUCTOR.newInstance(tracker, buf.readInt(), buf.readInt()));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return packet;
	}

	public static class Handler {

		public static void handle(SSyncCooldownsPacket packet, Supplier<NetworkEvent.Context> ctx) {
			try {
				COOLDOWNS_TICKS_FIELD.set(Minecraft.getInstance().player.getCooldownTracker(), packet.ticks);
				Map<Item, CooldownTracker.Cooldown> cooldowns = (Map<Item, Cooldown>) COOLDOWNS_FIELD.get(Minecraft.getInstance().player.getCooldownTracker());
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