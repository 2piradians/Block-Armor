package twopiradians.blockArmor.client.gui.armorDisplay;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.collect.Multimap;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ItemBlockArmor;
import twopiradians.blockArmor.common.seteffect.SetEffect;

@SuppressWarnings({"all"})
@SideOnly(Side.CLIENT)
public class GuiArmorDisplay extends GuiScreen
{
	/**Should armor display be opened on chat event?*/
	public static final boolean DISPLAY_ARMOR_GUI = false;
	/**0 = vanilla sets, 1 = modded sets, 2 = set effects w/armor, 3 = set effect tooltips, 4 set effect itemstacks*/
	public static final int GUI_MODE = 4; //do modes 3 and 4 in normal gui size

	private final ResourceLocation backgroundWhite = new ResourceLocation(BlockArmor.MODID+":textures/gui/white.png");
	private final ResourceLocation backgroundTooltipColor = new ResourceLocation(BlockArmor.MODID+":textures/gui/tooltip_color.png");
	private EntityGuiPlayer guiPlayer;
	private float partialTicks;
	/**List of all armors with set effects*/
	private ArrayList<ItemBlockArmor> armors;
	/**List of all unique set effect tooltips and blocks they are valid for*/
	private Map<String, ArrayList<ItemStack>> tooltips;

	public GuiArmorDisplay() {
		guiPlayer = new EntityGuiPlayer(Minecraft.getMinecraft().world, Minecraft.getMinecraft().player.getGameProfile(), Minecraft.getMinecraft().player);
		//initialize armors with all armor that has a set effect
		armors = new ArrayList<ItemBlockArmor>();
		tooltips = new TreeMap<String, ArrayList<ItemStack>>(new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				arg0 = TextFormatting.getTextWithoutFormattingCodes(arg0);
				arg1 = TextFormatting.getTextWithoutFormattingCodes(arg1);
				return arg0.compareToIgnoreCase(arg1);
			}
		});
		for (ArmorSet set : ArmorSet.allSets)
			if ((GUI_MODE == 0 && !set.isFromModdedBlock) ||
					(GUI_MODE == 1 && set.isFromModdedBlock) || 
					(GUI_MODE == 2 && !set.setEffects.isEmpty()) ||
					GUI_MODE == 3 || GUI_MODE == 4) {
				if (set.isEnabled()) {
					armors.add(set.helmet);
					armors.add(set.chestplate);
					armors.add(set.leggings);
					armors.add(set.boots);
				}
				if ((GUI_MODE == 3 || GUI_MODE == 4) && set.isEnabled())
					for (SetEffect effect : set.setEffects) {
						String tooltip = effect.addInformation(new ItemStack(set.helmet), true, guiPlayer, new ArrayList<String>(), false).get(0);
						ArrayList<ItemStack> stacks = new ArrayList<ItemStack>(); 
						stacks.add(0, set.stack);
						if (tooltips.containsKey(tooltip)) 
							stacks.addAll(0, tooltips.get(tooltip));
						tooltips.put(tooltip, stacks);
					}						
			}
		guiPlayer.setInvisible(GUI_MODE == 0 || GUI_MODE == 1);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		//background
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if (GUI_MODE == 4)
			mc.getTextureManager().bindTexture(backgroundTooltipColor);
		else
			mc.getTextureManager().bindTexture(backgroundWhite);
		GlStateManager.pushMatrix();
		float scale = 1f;
		if (GUI_MODE == 0)
			GlStateManager.scale(1.47f, 1.445f, scale);
		else if (GUI_MODE == 1)
			GlStateManager.scale(1.47f*2, 1.445f*2, scale);
		else if (GUI_MODE == 2)
			GlStateManager.scale(2.5f, 1.31f, scale);
		this.drawTexturedModalRect(0, 0, 0, 0, this.width, this.height);
		GlStateManager.popMatrix();

		if (GUI_MODE == 3 || GUI_MODE == 4) {
			for (int i=0; i<tooltips.size(); i++) {
				GlStateManager.pushMatrix();
				int spaceBetween = 52;
				i -= 24;
				if (i >= 0 && i <= 11)
					GlStateManager.translate(width/7*2, 20+i*spaceBetween, 0);
				else if (i >= 11 && i <= 23)
					GlStateManager.translate(width/7*5, 20+(i-12)*spaceBetween, 0);
				else
					GlStateManager.translate(2035, 200+(i-24)*spaceBetween, 0);
				i += 24;
				List<String> tooltip = new ArrayList<String>();
				tooltip.add(tooltips.keySet().toArray(new String[0])[i]);
				int numStacks = tooltips.get(tooltips.keySet().toArray(new String[0])[i]).size();
				if (tooltip.get(0).contains("Regrowth")) 
					tooltip.set(0, "                             "+tooltip.get(0)+"                             ");
				else if (tooltip.get(0).contains("Invisibility")) 
					tooltip.set(0, "                                               "+tooltip.get(0)+"                                               ");
				else if (tooltip.get(0).contains("Soft Fall")) 
					tooltip.set(0, "                                         "+tooltip.get(0)+"                                         ");
				tooltip.add("");
				tooltip.add("");
				tooltip.add("");
				int length = 0;
				for (String string : tooltip)
					if (this.fontRenderer.getStringWidth(string) > length)
						length = this.fontRenderer.getStringWidth(string);
				if (GUI_MODE == 3)
					this.drawHoveringText(tooltip, -length/2, 0);
				else {
					RenderHelper.enableGUIStandardItemLighting();
					length = tooltips.get(tooltips.keySet().toArray(new String[0])[i]).size() * 20;
					scale = 1.6f;
					GlStateManager.scale(scale, scale, scale);
					for (int j=0; j<tooltips.get(tooltips.keySet().toArray(new String[0])[i]).size(); j++) 
						this.itemRender.renderItemIntoGUI(tooltips.get(tooltips.keySet().toArray(new String[0])[i]).get(j), -length/2+j*20+10, 0);
					RenderHelper.disableStandardItemLighting();
				}
				GlStateManager.popMatrix();
			}
		}
		else {
			//iterate through each set of armor
			this.partialTicks += 0.3f;
			for (int index=0; index<armors.size(); index+=4) {
				ItemStack helmet = new ItemStack(armors.get(index));
				ItemStack chestplate = new ItemStack(armors.get(index+1));
				ItemStack leggings = new ItemStack(armors.get(index+2));
				ItemStack boots = new ItemStack(armors.get(index+3));
				//equip gui player
				guiPlayer.setItemStackToSlot(EntityEquipmentSlot.HEAD, helmet);
				guiPlayer.setItemStackToSlot(EntityEquipmentSlot.CHEST, chestplate);
				guiPlayer.setItemStackToSlot(EntityEquipmentSlot.LEGS, leggings);
				guiPlayer.setItemStackToSlot(EntityEquipmentSlot.FEET, boots);
				//update items
				helmet.getItem().onUpdate(helmet, guiPlayer.world, guiPlayer, EntityEquipmentSlot.HEAD.getIndex(), false);
				chestplate.getItem().onUpdate(chestplate, guiPlayer.world, guiPlayer, EntityEquipmentSlot.CHEST.getIndex(), false);
				leggings.getItem().onUpdate(leggings, guiPlayer.world, guiPlayer, EntityEquipmentSlot.LEGS.getIndex(), false);
				boots.getItem().onUpdate(boots, guiPlayer.world, guiPlayer, EntityEquipmentSlot.FEET.getIndex(), false);
				//draw gui player
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.pushMatrix();
				double spaceBetween = 0;
				if (GUI_MODE == 0) {
					scale = 29f;
					double heightBetween = 44.3d;
					int perRow = 17;
					int row = index/4 / perRow;
					spaceBetween = 21.8d + (row==7?1.3d:0);
					GlStateManager.translate(-160+(index/4 % perRow)*spaceBetween, row*heightBetween, row);
				}
				else if (GUI_MODE == 1) {
					scale = 29f;
					double heightBetween = 45.2d;
					int perRow = 17*2;
					int row = index/4 / perRow;
					spaceBetween = 21.8d;
					GlStateManager.translate(-160+(index/4 % perRow)*spaceBetween, row*heightBetween+(row>15?100:0), row);
				}
				else if (GUI_MODE == 2) {
					scale = 50f;
					spaceBetween = 22.5d;
					if (index/4 < 7)
						GlStateManager.translate(-250+index*spaceBetween, 2, 0);
					else
						GlStateManager.translate(-915+index*(spaceBetween+2), 179, 0);
				}
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.rotate(180F, 0F, 0F, 1F);
				GlStateManager.rotate(135.0F, 0.0F, 1, 0.0f);
				RenderHelper.enableStandardItemLighting();
				GlStateManager.rotate(-165.0F, 0.0F, 1, -0.0f);
				GlStateManager.rotate(-10.0F, -1F, 0F, 0.5f);
				guiPlayer.rotationYawHead = 0.0F;
				guiPlayer.renderYawOffset = 0.0F;
				mc.getRenderManager().setPlayerViewY(-20f);
				mc.getRenderManager().doRenderEntity(guiPlayer, -4D, -1.5D, 5.0D, 0.0F, this.partialTicks, true);
				RenderHelper.disableStandardItemLighting();
				this.mc.entityRenderer.disableLightmap();
				GlStateManager.popMatrix();
				//render tooltip
				if (GUI_MODE == 2) {
					ItemStack stack = new ItemStack(armors.get(index));
					GlStateManager.pushMatrix();
					scale = 0.651f;
					if (index/4 < 7)
						GlStateManager.translate(40+index*spaceBetween, 116, 0);
					else
						GlStateManager.translate(-625+index*(spaceBetween+2), 293, 0);
					GlStateManager.scale(scale, scale, scale);
					int length = 0;
					ArrayList<String> tooltip = new ArrayList<String>();
					tooltip.add(TextFormatting.AQUA+""+TextFormatting.UNDERLINE+stack.getDisplayName().replace("Helmet", "Armor"));
					//armors.get(index).addFullSetEffectTooltip(tooltip);
					this.addStatTooltips(tooltip, new ItemStack[] {helmet, chestplate, leggings, boots});
					for (String string : tooltip)
						if (this.fontRenderer.getStringWidth(string) > length)
							length = this.fontRenderer.getStringWidth(string);
					this.drawHoveringText(tooltip, -length/2, 0);
					GlStateManager.popMatrix();
					RenderHelper.disableStandardItemLighting();
				}
			}
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	//partially copied from ItemStack.getTooltip()
	private ArrayList<String> addStatTooltips(ArrayList<String> tooltip, ItemStack[] armor) {
		ArrayList<Multimap<String, AttributeModifier>> list = new ArrayList<Multimap<String, AttributeModifier>>();
		list.add(armor[0].getAttributeModifiers(EntityEquipmentSlot.HEAD));
		list.add(armor[1].getAttributeModifiers(EntityEquipmentSlot.CHEST));
		list.add(armor[2].getAttributeModifiers(EntityEquipmentSlot.LEGS));
		list.add(armor[3].getAttributeModifiers(EntityEquipmentSlot.FEET));

		//add boot enchantments
		if (armor[3].hasTagCompound())
		{
			NBTTagList nbttaglist = armor[3].getEnchantmentTagList();
			if (nbttaglist != null)
				for (int j = 0; j < nbttaglist.tagCount(); ++j)
				{
					int k = nbttaglist.getCompoundTagAt(j).getShort("id");
					int l = nbttaglist.getCompoundTagAt(j).getShort("lvl");

					if (Enchantment.getEnchantmentByID(k) != null)
						tooltip.add(ChatFormatting.GRAY+Enchantment.getEnchantmentByID(k).getTranslatedName(l));
				}
		}

		boolean flag = false;
		ArrayList<Double> finalD0s = new ArrayList<Double>();
		ArrayList<Double> finalD1s = new ArrayList<Double>();
		ArrayList<Entry<String, AttributeModifier>> entries = new ArrayList<Entry<String, AttributeModifier>>();
		for (Multimap<String, AttributeModifier> multimap : list) {
			if (!multimap.isEmpty())
			{
				for (Entry<String, AttributeModifier> entry : multimap.entries())
				{
					AttributeModifier attributemodifier = (AttributeModifier)entry.getValue();
					double d0 = attributemodifier.getAmount();
					flag = false;
					if (attributemodifier.getID() == SetEffect.ATTACK_DAMAGE_UUID)
					{
						d0 = d0 + guiPlayer.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
						d0 = d0 + (double)EnchantmentHelper.getModifierForCreature(armor[0], EnumCreatureAttribute.UNDEFINED);
						flag = true;
					}
					else if (attributemodifier.getID() == SetEffect.ATTACK_SPEED_UUID)
					{
						d0 += guiPlayer.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
						flag = true;
					}
					double d1;
					if (attributemodifier.getOperation() != 1 && attributemodifier.getOperation() != 2)
						d1 = d0;
					else
						d1 = d0 * 100.0D;

					boolean contains = false;
					int index2 = 0;
					for (int i=0; i<entries.size(); i++) 
						if (entries.get(i).getValue().getName() == attributemodifier.getName()) {
							contains = true;
							index2 = i;
						}
					if (!contains) {
						entries.add(entry);
						finalD0s.add(d0);
						finalD1s.add(d1);
					}
					else if (entries.get(index2).getValue().getName().equalsIgnoreCase("Armor toughness") || 
							entries.get(index2).getValue().getName().equalsIgnoreCase("Armor modifier")){
						finalD0s.set(index2, finalD0s.get(index2)+d0);
						finalD1s.set(index2, finalD1s.get(index2)+d1);
					}
				}
			}
		}
		for (int i=0; i<entries.size(); i++) {
			AttributeModifier attributemodifier = (AttributeModifier)entries.get(i).getValue();
			if (flag)
				tooltip.add(TextFormatting.BLUE + " +" + I18n.translateToLocalFormatted("attribute.modifier.equals." + attributemodifier.getOperation(), new Object[] {ItemStack.DECIMALFORMAT.format(finalD1s.get(i)), I18n.translateToLocal("attribute.name." + (String)entries.get(i).getKey())}));
			else if (finalD0s.get(i) > 0.0D)
				tooltip.add(TextFormatting.BLUE + " " + I18n.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier.getOperation(), new Object[] {ItemStack.DECIMALFORMAT.format(finalD1s.get(i)), I18n.translateToLocal("attribute.name." + (String)entries.get(i).getKey())}));
			else if (finalD0s.get(i) < 0.0D)
			{
				finalD1s.set(i, finalD1s.get(i) * -1.0D);
				tooltip.add(TextFormatting.RED + " " + I18n.translateToLocalFormatted("attribute.modifier.take." + attributemodifier.getOperation(), new Object[] {ItemStack.DECIMALFORMAT.format(finalD1s.get(i)), I18n.translateToLocal("attribute.name." + (String)entries.get(i).getKey())}));
			}
		}
		return tooltip;
	}
}
