package twopiradians.blockArmor.client.gui.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.config.Config;

public class BlockArmorGuiConfig extends GuiConfig 
{	
	public BlockArmorGuiConfig(GuiScreen parent) 
	{
		super(parent, getConfigElements(), BlockArmor.MODID, false, false, "Block Armor Configuration");
	}

	private static List<IConfigElement> getConfigElements() {
		final List<IConfigElement> list = new ArrayList<IConfigElement>();
		list.add(new ConfigElement(Config.config.getCategory(Config.ARMOR_SETS_CATEGORY).setLanguageKey(Config.ARMOR_SETS_CATEGORY)));
		list.add(new ConfigElement(Config.config.getCategory(Config.SET_EFFECTS_CATEGORY).setLanguageKey(Config.SET_EFFECTS_CATEGORY)));
		list.addAll(new ConfigElement(Config.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements());
		return list;
	}
}