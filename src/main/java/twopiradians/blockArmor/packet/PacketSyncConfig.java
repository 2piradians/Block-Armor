package twopiradians.blockArmor.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.item.ArmorSet;

public class PacketSyncConfig implements IMessage
{
	/**disable items if new ones are added*/
	protected boolean disableItems;

	public PacketSyncConfig() 
	{
		disableItems = false;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		//pieces for set
		Config.piecesForSet = buf.readInt();
		Property prop = Config.getPiecesForSetProp();
		prop.set(Config.piecesForSet);

		//disabled set effects
		Config.disabledSetEffects.clear();
		int numEffects = buf.readInt();
		for (int i=0; i<numEffects; ++i) {
			try {
				Class clazz = Class.forName(ByteBufUtils.readUTF8String(buf));
				if (clazz != null)
					Config.disabledSetEffects.add(clazz);
			}
			catch (Exception e) {}
		}

		//disabled armor sets
		int numSets = buf.readInt();
		for (int i=0; i<numSets; ++i) {
			ItemStack stack = ByteBufUtils.readItemStack(buf);
			if (stack != null) {
				ArmorSet set = ArmorSet.getSet(stack.getItem(), stack.getMetadata());
				if (set != null) {
					boolean bool = buf.readBoolean();
					if (bool != set.isEnabled()) {
						if (bool)
							set.enable();
						else
							set.disable();
					}
					ModContainer mod = Loader.instance().getIndexedModList().get(set.modid);
					if (mod != null) {
						prop = Config.getArmorSetProp(mod.getName(), set);
						prop.set(bool);
					}
				}
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		//pieces for set
		buf.writeInt(Config.piecesForSet);

		//disabled set effects
		buf.writeInt(Config.disabledSetEffects.size());
		for (Class clazz : Config.disabledSetEffects)
			ByteBufUtils.writeUTF8String(buf, clazz.toString());

		//disabled armor sets
		buf.writeInt(ArmorSet.allSets.size());
		for (ArmorSet set : ArmorSet.allSets) {
			ByteBufUtils.writeItemStack(buf, set.stack);
			buf.writeBoolean(set.isEnabled());
		}			
	}

	public static class Handler implements IMessageHandler<PacketSyncConfig, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSyncConfig packet, final MessageContext ctx) 
		{
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					Config.config.save();

					BlockArmor.logger.info("Synced client config with server config.");

					Config.syncJEIBlacklist();
				}
			});
			return null;
		}
	}
}