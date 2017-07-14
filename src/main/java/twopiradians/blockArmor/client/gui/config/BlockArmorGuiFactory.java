package twopiradians.blockArmor.client.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class BlockArmorGuiFactory implements IModGuiFactory 
{
	@Override
	public void initialize(Minecraft minecraftInstance) 
	{

	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() 
	{
		return null;
	}

	@Override
	public boolean hasConfigGui() { //TODO these two methods were added for some reason, test that everything still works
		return false;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return null;
	}
}
