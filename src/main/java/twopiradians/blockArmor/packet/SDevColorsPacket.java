package twopiradians.blockArmor.packet;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

import com.google.common.collect.Maps;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import twopiradians.blockArmor.common.command.CommandDev;

public class SDevColorsPacket
{
	public SDevColorsPacket() {}
	
	public static void encode(SDevColorsPacket packet, PacketBuffer buf) {
		ArrayList<UUID> keys = new ArrayList<UUID>(CommandDev.devColors.keySet());
		int count = keys.size();
		buf.writeInt(count);
		for (int i=0; i<count; i++) {
			buf.writeString(keys.get(i).toString());
			buf.writeFloat(CommandDev.devColors.get(keys.get(i))[0]);
			buf.writeFloat(CommandDev.devColors.get(keys.get(i))[1]);
			buf.writeFloat(CommandDev.devColors.get(keys.get(i))[2]);
		}
	}

	public static SDevColorsPacket decode(PacketBuffer buf) {
		CommandDev.devColors = Maps.newHashMap();
		int count = buf.readInt();
		for (int i=0; i<count; i++) {
			UUID uuid = UUID.fromString(buf.readString());
			Float[] color = new Float[] { buf.readFloat(),  buf.readFloat(),  buf.readFloat()};
			CommandDev.devColors.put(uuid, color);
		}
		return new SDevColorsPacket();
	}

	public static class Handler
	{

		public static void handle(SDevColorsPacket packet, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().setPacketHandled(true);
		}
	}
}