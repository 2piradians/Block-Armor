package twopiradians.blockArmor.packets;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import twopiradians.blockArmor.common.item.ArmorSet;

public class DisableItemsPacket implements IMessage
{
	protected ArrayList<Item> itemsToDisable;

	public DisableItemsPacket() 
	{

	}

	public DisableItemsPacket(ArrayList<Item> itemsToDisable) 
	{
		this.itemsToDisable = itemsToDisable;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		int num = buf.readInt();
		this.itemsToDisable = new ArrayList<Item>();
		for (int i=0; i<num; i++)
			this.itemsToDisable.add(ByteBufUtils.readItemStack(buf).getItem());
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(this.itemsToDisable.size());
		for (Item item : this.itemsToDisable)
			ByteBufUtils.writeItemStack(buf, new ItemStack(item));
	}

	public static class Handler implements IMessageHandler<DisableItemsPacket, IMessage>
	{
		@Override
		public IMessage onMessage(final DisableItemsPacket packet, final MessageContext ctx) 
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					ArmorSet.disabledItems = packet.itemsToDisable;
					ArmorSet.disableItems();
				}
			});
			return null;
		}
	}
}