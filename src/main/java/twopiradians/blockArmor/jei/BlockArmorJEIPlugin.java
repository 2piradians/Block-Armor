package twopiradians.blockArmor.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IItemBlacklist;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import twopiradians.blockArmor.client.ClientProxy;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

@JEIPlugin
public class BlockArmorJEIPlugin extends BlankModPlugin 
{
	private static IJeiHelpers helpers;

	@Override
	public void register(IModRegistry registry) {
		helpers = registry.getJeiHelpers();
		MinecraftForge.EVENT_BUS.register(this);		
	}
	
	@SubscribeEvent
	public void clientTick(TickEvent.ClientTickEvent event)
	{
		if (((ClientProxy)BlockArmor.proxy).reloadJEI) {
			((ClientProxy)BlockArmor.proxy).reloadJEI = false;
			this.removeDisabledItems();
		}
	}

	/**Removes disabled items from JEI gui*/
	public void removeDisabledItems() {
		if (helpers != null) {
			IItemBlacklist blacklist = helpers.getItemBlacklist();
			boolean reload = false;
			if (blacklist != null && ArmorSet.disabledItems != null && !ArmorSet.disabledItems.isEmpty()) 
				for (Item item : ArmorSet.disabledItems) 
					if (!blacklist.isItemBlacklisted(new ItemStack(item))) {
						blacklist.addItemToBlacklist(new ItemStack(item));
						reload = true;
					}
			if (reload) {
				BlockArmor.logger.info("Removing disabled items from JEI");
				helpers.reload();
			}
		}
	}
}
