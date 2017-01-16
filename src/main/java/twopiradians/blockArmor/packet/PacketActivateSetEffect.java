package twopiradians.blockArmor.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import twopiradians.blockArmor.common.BlockArmor;

public class PacketActivateSetEffect implements IMessage
{
	private boolean isKeyPressed;
	private boolean isKeyDown;

	public PacketActivateSetEffect() 
	{

	}

	public PacketActivateSetEffect(boolean isKeyPressed, boolean isKeyDown) 
	{
		this.isKeyPressed = isKeyPressed;
		this.isKeyDown = isKeyDown;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.isKeyPressed = buf.readBoolean();
		this.isKeyDown = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeBoolean(this.isKeyPressed);
		buf.writeBoolean(this.isKeyDown);
	}

	public static class Handler implements IMessageHandler<PacketActivateSetEffect, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketActivateSetEffect packet, final MessageContext ctx) 
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					BlockArmor.key.isKeyPressed = packet.isKeyPressed;
					BlockArmor.key.isKeyDown = packet.isKeyDown;
				}
			});
			return null;
		}
	}
}