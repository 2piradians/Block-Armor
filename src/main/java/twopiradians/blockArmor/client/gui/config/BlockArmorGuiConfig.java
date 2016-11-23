package twopiradians.blockArmor.client.gui.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
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
		Config.syncConfig(true);
		list.addAll(new ConfigElement(Config.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements());
		Config.syncConfig();
		return list;
	}

	@Override
	protected void mouseClicked(int x, int y, int mouseEvent) throws IOException
	{
		super.mouseClicked(x, y, mouseEvent);
		String before = new ConfigElement(Config.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements().get(0).get().toString();
		this.entryList.saveConfigElements();
		String after = new ConfigElement(Config.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements().get(0).get().toString();
		if (!before.equals(after))
		{
			Config.syncConfig();
			this.entryList = new GuiConfigEntries(this, mc);
		}
	}
}
