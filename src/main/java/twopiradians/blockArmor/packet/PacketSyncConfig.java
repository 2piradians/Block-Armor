package twopiradians.blockArmor.packet;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
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
import twopiradians.blockArmor.common.seteffect.SetEffect;

public class PacketSyncConfig implements IMessage
{

	public PacketSyncConfig() 
	{
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		//pieces for set
		Config.piecesForSet = buf.readInt();
		Property prop = Config.getPiecesForSetProp();
		prop.set(Config.piecesForSet);

		//set effects use durability
		Config.effectsUseDurability = buf.readBoolean();
		prop = Config.getEffectsUseDurablityProp();
		prop.set(Config.effectsUseDurability);

		//register disabled items
		Config.registerDisabledItems = buf.readBoolean();
		prop = Config.getRegisterDisableItemsProp();
		prop.set(Config.registerDisabledItems);

		//disabled set effects
		Config.disabledSetEffects.clear();
		int numEffects = buf.readInt();
		for (SetEffect effect : SetEffect.SET_EFFECTS) {
			prop = Config.getSetEffectProp(effect.toString());
			prop.set(true);
		}	
		for (int i=0; i<numEffects; ++i) {
			try {
				String string = ByteBufUtils.readUTF8String(buf);
				Class clazz = Class.forName(string);
				if (clazz != null) {
					Config.disabledSetEffects.add(clazz);
					for (SetEffect effect : SetEffect.SET_EFFECTS) {
						if (effect.getClass() == clazz) {
							prop = Config.getSetEffectProp(effect.toString());
							prop.set(false);
						}
					}	
				}
			}
			catch (Exception e) {
				BlockArmor.logger.debug("Caught exception while reading disabledSetEffects from PacketSyncConfig: ", e);
			}
		}

		//enabled armor sets
		int numSets = buf.readInt();
		ArrayList<ArmorSet> enabledSets = new ArrayList<ArmorSet>();
		for (int i=0; i<numSets; ++i) {
			try {
				String name = ByteBufUtils.readUTF8String(buf);
				ArmorSet set = ArmorSet.nameToSetMap.get(name); // TODO change map to something other than display name
				// when language is changed during runtime, the packet won't recognize display names and won't enable sets
				if (set != null)
					enabledSets.add(set);
			}
			catch (Exception e) {
				BlockArmor.logger.debug("Caught exception while reading enabled sets from PacketSyncConfig: ", e);
			}
		}
		for (ArmorSet set : ArmorSet.allSets) {
			boolean enabled = enabledSets.contains(set);
			if (enabled != set.isEnabled()) {
				if (enabled)
					set.enable();
				else
					set.disable();
			}
			ModContainer mod = Loader.instance().getIndexedModList().get(set.modid);
			if (mod != null) {
				prop = Config.getArmorSetProp(mod.getName(), set);
				prop.set(enabled);
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		//pieces for set
		buf.writeInt(Config.piecesForSet);

		//set effects use durability
		buf.writeBoolean(Config.effectsUseDurability);

		//register disabledItems
		buf.writeBoolean(Config.registerDisabledItems);

		//disabled set effects
		buf.writeInt(Config.disabledSetEffects.size());
		for (Class clazz : Config.disabledSetEffects)
			ByteBufUtils.writeUTF8String(buf, clazz.getName());

		//disabled armor sets
		ArrayList<String> enabledNames = new ArrayList<String>();
		for (ArmorSet set : ArmorSet.allSets) {
			if (set.isEnabled())
				enabledNames.add(ArmorSet.getItemStackRegistryName(set.stack));
		}		
		buf.writeInt(enabledNames.size());
		for (String name : enabledNames)
			ByteBufUtils.writeUTF8String(buf, name);

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