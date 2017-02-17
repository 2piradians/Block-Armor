package twopiradians.blockArmor.packet;

import java.util.ArrayList;
import java.util.UUID;

import com.google.common.collect.Maps;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import twopiradians.blockArmor.common.command.CommandDev;

public class PacketDevColors implements IMessage
{
	public PacketDevColors() 
	{

	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		CommandDev.devColors = Maps.newHashMap();
		int count = buf.readInt();
		for (int i=0; i<count; i++) {
			UUID uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
			Float[] color = new Float[] { buf.readFloat(),  buf.readFloat(),  buf.readFloat()};
			CommandDev.devColors.put(uuid, color);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ArrayList<UUID> keys = new ArrayList<UUID>(CommandDev.devColors.keySet());
		int count = keys.size();
		buf.writeInt(count);
		for (int i=0; i<count; i++) {
			ByteBufUtils.writeUTF8String(buf, keys.get(i).toString());
			buf.writeFloat(CommandDev.devColors.get(keys.get(i))[0]);
			buf.writeFloat(CommandDev.devColors.get(keys.get(i))[1]);
			buf.writeFloat(CommandDev.devColors.get(keys.get(i))[2]);
		}
	}

	public static class Handler implements IMessageHandler<PacketDevColors, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketDevColors packet, final MessageContext ctx) 
		{
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{

				}
			});
			return null;
		}
	}
}