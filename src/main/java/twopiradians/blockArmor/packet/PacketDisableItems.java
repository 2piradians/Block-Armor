/*package twopiradians.blockArmor.packet;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import twopiradians.blockArmor.common.item.ArmorSet;

public class PacketDisableItems implements IMessage
{
	protected ArrayList<ItemStack> itemsToDisable;

	public PacketDisableItems() 
	{

	}

	public PacketDisableItems(ArrayList<ItemStack> itemsToDisable) 
	{
		this.itemsToDisable = itemsToDisable;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		int num = buf.readInt();
		this.itemsToDisable = new ArrayList<ItemStack>();
		for (int i=0; i<num; i++)
			this.itemsToDisable.add(ByteBufUtils.readItemStack(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(this.itemsToDisable.size());
		for (ItemStack stack : this.itemsToDisable)
			ByteBufUtils.writeItemStack(buf, stack);
	}

	public static class Handler implements IMessageHandler<PacketDisableItems, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketDisableItems packet, final MessageContext ctx) 
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.world;
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					if (ArmorSet.disabledItems == null || ArmorSet.disabledItems.isEmpty()) {
						ArmorSet.disabledItems = packet.itemsToDisable;
						ArmorSet.disableItems(0);
					}
				}
			});
			return null;
		}
	}
}*/