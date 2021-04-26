package twopiradians.blockArmor.client.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.BlockArmorItem;
import twopiradians.blockArmor.common.seteffect.SetEffect;

@SuppressWarnings({"all"})
@OnlyIn(Dist.CLIENT)
public class GuiArmorDisplay extends Screen {

	/** Should armor display be opened on chat event? */
	public static final boolean DISPLAY_ARMOR_GUI = false;
	/**
	 * 0 = vanilla sets 
	 * 1 = modded sets
	 * 2 = set effects w/armor (not used)
	 * 3 = set effect tooltips 
	 * 4 = set effect itemstacks
	 * 5 = 3 & 4
	 */
	private static final int GUI_MODE = 5; // do modes 3, 4, and 5 in gui size 2
	/**Pages of items (each page displays 20)*/
	private static final int GUI_PAGE = 1;

	private final ResourceLocation backgroundBlue = new ResourceLocation(BlockArmor.MODID+":textures/gui/blue.jpg");
	private final ResourceLocation backgroundWhite = new ResourceLocation(BlockArmor.MODID+":textures/gui/white.png");
	private final ResourceLocation backgroundTooltipColor = new ResourceLocation(BlockArmor.MODID+":textures/gui/tooltip_color.png");
	private EntityGuiPlayer guiPlayer;
	private float partialTicks;
	/** List of all armors with set effects */

	private ArrayList<BlockArmorItem> armors;
	/** List of all unique set effect tooltips and blocks they are valid for */
	private Map<StringTextComponent, ArrayList<ItemStack>> tooltips;

	public GuiArmorDisplay() {
		super(new StringTextComponent("Armor Display"));
		guiPlayer = new EntityGuiPlayer(Minecraft.getInstance().world, Minecraft.getInstance().player.getGameProfile(), Minecraft.getInstance().player);
		//initialize armors with all armor that has a set effect
		armors = new ArrayList<BlockArmorItem>();
		tooltips = new TreeMap<StringTextComponent, ArrayList<ItemStack>>(new Comparator<StringTextComponent>() {
			@Override
			public int compare(StringTextComponent arg0, StringTextComponent arg1) {
				return TextFormatting.getTextWithoutFormattingCodes(arg0.getString()).compareTo(
						TextFormatting.getTextWithoutFormattingCodes(arg1.getString()));
			}
		});
		for (ArmorSet set : ArmorSet.allSets)
			if ((GUI_MODE == 0 && !set.isFromModdedBlock) ||
					(GUI_MODE == 1 && set.isFromModdedBlock) || 
					(GUI_MODE == 2 && !set.setEffects.isEmpty()) ||
					GUI_MODE == 3 || GUI_MODE == 4 || GUI_MODE == 5) {
				if (set.isEnabled()) {
					armors.add(set.helmet);
					armors.add(set.chestplate);
					armors.add(set.leggings);
					armors.add(set.boots);
				}
				if ((GUI_MODE == 3 || GUI_MODE == 4 || GUI_MODE == 5) && set.isEnabled())
					for (SetEffect effect : set.setEffects) {
						if (effect != SetEffect.REGROWTH || (
								!set.registryName.contains("wood") &&
								!set.registryName.contains("stripped") &&
								!set.registryName.contains("spruce") &&
								!set.registryName.contains("birch") &&
								!set.registryName.contains("jungle") &&
								!set.registryName.contains("acacia") &&
								!set.registryName.contains("dark_oak"))) {
							ITextComponent tooltip = effect.addInformation(new ItemStack(set.helmet), true, guiPlayer, Lists.newArrayList(), TooltipFlags.NORMAL).get(0);
							ArrayList<ItemStack> stacks = new ArrayList<ItemStack>(); 
							stacks.add(0, set.stack);
							if (tooltips.containsKey(tooltip)) 
								stacks.addAll(0, tooltips.get(tooltip));
							tooltips.put((StringTextComponent) tooltip, stacks);
						}
					}						
			}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		try {
			guiPlayer.setInvisible(GUI_MODE == 0 || GUI_MODE == 1);
			// background
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (GUI_MODE == 4 || GUI_MODE == 5)
				minecraft.getTextureManager().bindTexture(backgroundTooltipColor);
			else if (GUI_MODE == 0)
				minecraft.getTextureManager().bindTexture(backgroundBlue);
			else
				minecraft.getTextureManager().bindTexture(backgroundWhite);
			RenderSystem.pushMatrix();
			float scale = 1f;
			if (GUI_MODE == 0)
				RenderSystem.scalef(2.2f, 2, scale);
			else if (GUI_MODE == 1)
				RenderSystem.scalef(1.47f*2, 1.445f*2, scale);
			else if (GUI_MODE == 2)
				RenderSystem.scalef(2.5f, 1.31f, scale);
			this.blit(matrix, 0, 0, 0, 0, this.width, this.height);
			RenderSystem.popMatrix();

			// tooltips / items
			if (GUI_MODE == 3 || GUI_MODE == 4 || GUI_MODE == 5) {
				for (int i=0; i<tooltips.size(); i++) {
					RenderSystem.pushMatrix();
					float spaceBetween = 49.0f;
					i -= GUI_PAGE*20;
					// first column
					if (i >= 0 && i <= 9)
						RenderSystem.translatef(width/3.5f, 2+i*spaceBetween, 0);
					// second column
					else if (i > 9 && i <= 19) 
						RenderSystem.translatef(width/1.35f, 2+(i-10)*spaceBetween, 0);
					// not visible
					else 
						RenderSystem.translatef(2035, 200+(i-24)*spaceBetween, 0);
					i += GUI_PAGE*20;
					List<String> tooltip = new ArrayList<String>();
					tooltip.add(tooltips.keySet().toArray(new StringTextComponent[0])[i].getString());
					int numStacks = tooltips.get(tooltips.keySet().toArray(new StringTextComponent[0])[i]).size();
					if (tooltip.get(0).contains("Regrowth")) 
						tooltip.set(0, "                                  "+tooltip.get(0)+"                                  ");
					else if (tooltip.get(0).contains("Invisibility")) 
						tooltip.set(0, "                                 "+tooltip.get(0)+"                                 ");
					else if (tooltip.get(0).contains("Soft Fall")) 
						tooltip.set(0, "                         "+tooltip.get(0)+"                         ");
					else if (tooltip.get(0).contains("Falling"))
						tooltip.set(0, "                         "+tooltip.get(0)+"                         ");
					tooltip.add("");
					tooltip.add("");
					tooltip.add("");
					int length = 0;
					for (String string : tooltip)
						if (this.font.getStringWidth(string) > length)
							length = this.font.getStringWidth(string);
					// tooltips
					if (GUI_MODE == 3 || GUI_MODE == 5)
						this.renderWrappedToolTip(matrix, tooltip.stream().map(str -> new StringTextComponent(str)).collect(Collectors.toList()), -length/2, 0, font);
					// items
					if (GUI_MODE != 3) {
						RenderHelper.enableStandardItemLighting();
						int size = tooltips.get(tooltips.keySet().toArray(new StringTextComponent[0])[i]).size();
						length = size * 20;
						scale = 1.6f;
						RenderSystem.scalef(scale, scale, scale);
						for (int j=0; j<size; j++) {
							spaceBetween = 16 - (size-1)/11;
							this.itemRenderer.zLevel = 200;
							this.itemRenderer.renderItemIntoGUI(tooltips.get(tooltips.keySet().toArray(new StringTextComponent[0])[i]).get(j),
									(int) (j*spaceBetween - (size-1)*spaceBetween/2), 10);
						}
						RenderHelper.disableStandardItemLighting();
					}
					RenderSystem.popMatrix();
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
					guiPlayer.setItemStackToSlot(EquipmentSlotType.HEAD, helmet);
					guiPlayer.setItemStackToSlot(EquipmentSlotType.CHEST, chestplate);
					guiPlayer.setItemStackToSlot(EquipmentSlotType.LEGS, leggings);
					guiPlayer.setItemStackToSlot(EquipmentSlotType.FEET, boots);
					//update items
					helmet.getItem().inventoryTick(helmet, guiPlayer.world, guiPlayer, EquipmentSlotType.HEAD.getIndex(), false);
					chestplate.getItem().inventoryTick(chestplate, guiPlayer.world, guiPlayer, EquipmentSlotType.CHEST.getIndex(), false);
					leggings.getItem().inventoryTick(leggings, guiPlayer.world, guiPlayer, EquipmentSlotType.LEGS.getIndex(), false);
					boots.getItem().inventoryTick(boots, guiPlayer.world, guiPlayer, EquipmentSlotType.FEET.getIndex(), false);
					//draw gui player
					RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					RenderSystem.pushMatrix();
					double spaceBetween = 0;
					if (GUI_MODE == 0) {
						scale = 25;
						int perRow = 27;
						int row = index/4 / perRow;
						double heightBetween = 49d + (row == 10 ? 100 : 0);
						spaceBetween = 20.4d;
						RenderSystem.translated(-6+(index/4 % perRow)*spaceBetween, row*heightBetween+42, row);
					}
					else if (GUI_MODE == 1) {
						scale = 29f;
						double heightBetween = 45.2d;
						int perRow = 17*2;
						int row = index/4 / perRow;
						spaceBetween = 21.8d;
						RenderSystem.translated(-160+(index/4 % perRow)*spaceBetween, row*heightBetween+(row>15?100:0), row);
					}
					else if (GUI_MODE == 2) {
						scale = 50f;
						spaceBetween = 22.5d;
						if (index/4 < 7)
							RenderSystem.translated(-250+index*spaceBetween, 2, 0);
						else
							RenderSystem.translated(-915+index*(spaceBetween+2), 179, 0);
					}
					RenderSystem.rotatef(30f, -0.5f, 1f, -0.1f);
					guiPlayer.rotationYawHead = 0.0F;
					guiPlayer.renderYawOffset = 0.0F;
					RenderHelper.setupDiffuseGuiLighting(new Matrix4f(new Quaternion(5, 1, 1, 1)));
					InventoryScreen.drawEntityOnScreen(0, 0, (int) scale, 0, 0, guiPlayer); 
					RenderHelper.disableStandardItemLighting();
					RenderSystem.popMatrix();
					//render tooltip
					if (GUI_MODE == 2) {
						ItemStack stack = new ItemStack(armors.get(index));
						RenderSystem.pushMatrix();
						scale = 0.651f;
						if (index/4 < 7)
							RenderSystem.translated(40+index*spaceBetween, 116, 0);
						else
							RenderSystem.translated(-625+index*(spaceBetween+2), 293, 0);
						RenderSystem.scaled(scale, scale, scale);
						int length = 0;
						ArrayList<ITextComponent> tooltip = new ArrayList<ITextComponent>();
						tooltip.add(new StringTextComponent(TextFormatting.AQUA+""+TextFormatting.UNDERLINE+stack.getDisplayName().getString().replace("Helmet", "Armor")));
						//armors.get(index).addFullSetEffectTooltip(tooltip);
						this.addStatTooltips(tooltip, new ItemStack[] {helmet, chestplate, leggings, boots});
						for (ITextComponent string : tooltip) {
							if (this.font.getStringPropertyWidth(string) > length)
								length = this.font.getStringPropertyWidth(string);
						}
						this.renderWrappedToolTip(matrix, tooltip, -length/2, 0, font);
						RenderSystem.popMatrix();
						RenderHelper.disableStandardItemLighting();
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		super.render(matrix, mouseX, mouseY, partialTicks);
	}

	//partially copied from ItemStack.getTooltip()
	private ArrayList<ITextComponent> addStatTooltips(ArrayList<ITextComponent> tooltip, ItemStack[] armor) {
		ArrayList<Multimap<Attribute, AttributeModifier>> list = new ArrayList<Multimap<Attribute, AttributeModifier>>();
		list.add(armor[0].getAttributeModifiers(EquipmentSlotType.HEAD));
		list.add(armor[1].getAttributeModifiers(EquipmentSlotType.CHEST));
		list.add(armor[2].getAttributeModifiers(EquipmentSlotType.LEGS));
		list.add(armor[3].getAttributeModifiers(EquipmentSlotType.FEET));

		//add boot enchantments
		if (armor[3].hasTag())
		{
			ListNBT nbttaglist = armor[3].getEnchantmentTagList();
			if (nbttaglist != null)
				for (int j = 0; j < nbttaglist.size(); ++j)
				{
					int k = nbttaglist.getCompound(j).getShort("id");
					int l = nbttaglist.getCompound(j).getShort("lvl");

					if (Enchantment.getEnchantmentByID(k) != null)
						tooltip.add(new StringTextComponent(TextFormatting.GRAY+Enchantment.getEnchantmentByID(k).getDisplayName(l).getString()));
				}
		}

		for (ItemStack stack : armor) {
			int i = 0;
			for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
				Multimap<Attribute, AttributeModifier> multimap = stack.getAttributeModifiers(equipmentslottype);
				if (!multimap.isEmpty()) {
					tooltip.add(StringTextComponent.EMPTY);
					tooltip.add((new TranslationTextComponent("item.modifiers." + equipmentslottype.getName())).mergeStyle(TextFormatting.GRAY));

					for(Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
						AttributeModifier attributemodifier = entry.getValue();
						double d0 = attributemodifier.getAmount();
						boolean flag = false;
						if (guiPlayer != null) {
							if (attributemodifier.getID() == SetEffect.ATTACK_DAMAGE_UUID) {
								d0 = d0 + guiPlayer.getBaseAttributeValue(Attributes.ATTACK_DAMAGE);
								d0 = d0 + (double)EnchantmentHelper.getModifierForCreature(stack, CreatureAttribute.UNDEFINED);
								flag = true;
							} else if (attributemodifier.getID() == SetEffect.ATTACK_SPEED_UUID) {
								d0 += guiPlayer.getBaseAttributeValue(Attributes.ATTACK_SPEED);
								flag = true;
							}
						}

						double d1;
						if (attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
							if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
								d1 = d0 * 10.0D;
							} else {
								d1 = d0;
							}
						} else {
							d1 = d0 * 100.0D;
						}

						if (flag) {
							tooltip.add((new StringTextComponent(" ")).appendSibling(new TranslationTextComponent("attribute.modifier.equals." + attributemodifier.getOperation().getId(), ItemStack.DECIMALFORMAT.format(d1), new TranslationTextComponent(entry.getKey().getAttributeName()))).mergeStyle(TextFormatting.DARK_GREEN));
						} else if (d0 > 0.0D) {
							tooltip.add((new TranslationTextComponent("attribute.modifier.plus." + attributemodifier.getOperation().getId(), ItemStack.DECIMALFORMAT.format(d1), new TranslationTextComponent(entry.getKey().getAttributeName()))).mergeStyle(TextFormatting.BLUE));
						} else if (d0 < 0.0D) {
							d1 = d1 * -1.0D;
							tooltip.add((new TranslationTextComponent("attribute.modifier.take." + attributemodifier.getOperation().getId(), ItemStack.DECIMALFORMAT.format(d1), new TranslationTextComponent(entry.getKey().getAttributeName()))).mergeStyle(TextFormatting.RED));
						}
					}
				}
			}
		}
		return tooltip;
	}

}