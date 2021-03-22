package twopiradians.blockArmor.packet;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SSyncConfigPacket
{

	public SSyncConfigPacket() {}

	public static void encode(SSyncConfigPacket packet, PacketBuffer buf) {
		/*//pieces for set
		buf.writeInt(Config.piecesForSet);

		//set effects use durability
		buf.writeBoolean(Config.effectsUseDurability);

		//register disabledItems
		buf.writeBoolean(Config.registerDisabledItems);

		//disabled set effects
		buf.writeInt(Config.disabledSetEffects.size());
		for (Class clazz : Config.disabledSetEffects)
			buf.writeString(clazz.getName());

		//enabled armor sets
		ArrayList<String> enabledNames = new ArrayList<String>();
		for (ArmorSet set : ArmorSet.allSets) {
			if (set.isEnabled())
				enabledNames.add(ArmorSet.getItemStackRegistryName(set.stack));
		}		
		buf.writeInt(enabledNames.size());
		for (String name : enabledNames)
			buf.writeString(name);*/
	}

	public static SSyncConfigPacket decode(PacketBuffer buf) {
		/*//pieces for set
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
				String string = buf.readString();
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
				BlockArmor.LOGGER.log(Level.INFO, "Caught exception while reading disabledSetEffects from PacketSyncConfig: ", e);
			}
		}

		//enabled armor sets
		int numSets = buf.readInt();
		ArrayList<ArmorSet> enabledSets = new ArrayList<ArmorSet>();
		for (int i=0; i<numSets; ++i) {
			try {
				String name = buf.readString();
				ArmorSet set = ArmorSet.nameToSetMap.get(name);
				if (set != null)
					enabledSets.add(set);
			}
			catch (Exception e) {
				BlockArmor.LOGGER.log(Level.INFO, "Caught exception while reading enabled sets from PacketSyncConfig: ", e);
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
		}*/
		return new SSyncConfigPacket();
	}

	public static class Handler
	{

		public static void handle(SSyncConfigPacket packet, Supplier<NetworkEvent.Context> ctx) {
			/*Config.config.save();

				BlockArmor.LOGGER.info("Synced client config with server config.");

				Config.syncJEIIngredients();

				ctx.get().setPacketHandled(true);*/

		}
	}

}