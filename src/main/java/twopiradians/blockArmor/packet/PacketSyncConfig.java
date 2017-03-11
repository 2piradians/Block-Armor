package twopiradians.blockArmor.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import twopiradians.blockArmor.common.config.Config;

public class PacketSyncConfig implements IMessage
{
	protected int piecesForSet;

	public PacketSyncConfig() 
	{

	}

	public PacketSyncConfig(int piecesForSet) 
	{
		this.piecesForSet = piecesForSet;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.piecesForSet = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(this.piecesForSet);
	}

	public static class Handler implements IMessageHandler<PacketSyncConfig, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSyncConfig packet, final MessageContext ctx) 
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.world;
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					Property prop = Config.getPiecesForSetProp();
					if (prop != null) {
						prop.set(packet.piecesForSet);
						Config.syncConfig();
					}
				}
			});
			return null;
		}
	}
}