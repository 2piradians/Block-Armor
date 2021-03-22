/*package twopiradians.blockArmor.client.gui.config;

import java.util.ArrayList;

import net.minecraft.client.gui.screen.EditGamerulesScreen.BooleanEntry;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ButtonEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.DistOnly;
import twopiradians.blockArmor.common.BlockArmor;

@Mod.EventBusSubscriber(Dist.CLIENT)
@DistOnly(Dist.CLIENT)
public class GuiConfigUpdater {

	*//**
		 * Add Enable/Disable All button to categories, from our config, beginning with
		 * a boolean entry
		 *//*
			@SubscribeEvent
			public static void onGuiConfigInit(InitGuiEvent.Post event) {
			if (event.getGui() instanceof GuiConfig && ((GuiConfig)event.getGui()).modID == BlockArmor.MODID
			&& !((GuiConfig)event.getGui()).entryList.listEntries.isEmpty() && 
			((GuiConfig)event.getGui()).entryList.listEntries.get(0) instanceof GuiConfigEntries.BooleanEntry) {
			ConfigCategory category = null;
			for (String name : Config.config.getCategoryNames())
			//				if (Config.config.getCategory(name).containsKey(((GuiConfig)event.getGui()).entryList.listEntries.get(0)))
			category = Config.config.getCategory(name);
			ConfigCategory category = Config.config.getCategory(Configuration.CATEGORY_GENERAL);
			category.setLanguageKey(" ");
			if (category != null) {
			category.setRequiresMcRestart(!((GuiConfig)event.getGui()).entryList.listEntries.get(0).enabled());
			((GuiConfig)event.getGui()).entryList.listEntries.add(0,  
			new ButtonToggleConfig(((GuiConfig)event.getGui()).entryList.owningScreen, 
			((GuiConfig)event.getGui()).entryList, 
			new ConfigElement(category)));
			}
			}
			}
			
			public static class ButtonToggleConfig extends ButtonEntry {
			protected final boolean beforeValue;
			protected boolean currentValue;
			
			public ButtonToggleConfig(GuiConfig owningScreen, GuiConfigEntries owningEntryList,	IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
			this.toolTip = new ArrayList<String>() {{add("Enable or disable everything below.");}};
			if (this.owningEntryList.listEntries.size() >= 2 && 
			this.owningEntryList.listEntries.get(1) instanceof BooleanEntry) {
			this.beforeValue = !((BooleanEntry)this.owningEntryList.listEntries.get(1)).getCurrentValue();
			}
			else
			this.beforeValue = false;
			this.currentValue = beforeValue;
			updateValueButtonText();
			}
			
			@Override
			public void updateValueButtonText() {
			if (this.currentValue)
			this.btnValue.displayString = TextFormatting.DARK_GREEN+""+TextFormatting.BOLD+"Enable All";
			else 
			this.btnValue.displayString = TextFormatting.DARK_RED+""+TextFormatting.BOLD+"Disable All";
			}
			
			@Override
			public void valueButtonPressed(int slotIndex) {
			this.currentValue = !this.currentValue;
			for (IConfigEntry entry : this.owningEntryList.listEntries) {
			if (entry instanceof BooleanEntry && ((BooleanEntry)entry).getCurrentValue() == this.currentValue) {
			((BooleanEntry)entry).valueButtonPressed(1);
			((BooleanEntry)entry).updateValueButtonText();
			}
			}
			updateValueButtonText();
			}
			
			@Override
			public boolean isDefault() {
			return !currentValue;
			}
			
			@Override
			public void setToDefault() {
			if (currentValue) 
			valueButtonPressed(0);
			}
			
			@Override
			public boolean isChanged() {
			return currentValue != beforeValue;
			}
			
			@Override
			public void undoChanges() {
			currentValue = beforeValue;
			updateValueButtonText();
			}
			
			@Override
			public boolean saveConfigElement() {
			return false;
			}
			
			@Override
			public Boolean getCurrentValue() {
			return currentValue;
			}
			
			@Override
			public Boolean[] getCurrentValues() {
			return new Boolean[] { getCurrentValue() };
			}
			}
			}*/