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
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
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
	public static final int GUI_MODE = 5; // do modes 3, 4, and 5 in gui size 2
	/**Pages of items (each page displays 20)*/
	public static final int GUI_PAGE = 2;

	private final ResourceLocation backgroundBlue = new ResourceLocation(BlockArmor.MODID+":textures/gui/blue.jpg");
	private final ResourceLocation backgroundWhite = new ResourceLocation(BlockArmor.MODID+":textures/gui/white.png");
	private final ResourceLocation backgroundTooltipColor = new ResourceLocation(BlockArmor.MODID+":textures/gui/tooltip_color.png");
	private EntityGuiPlayer guiPlayer;
	private float partialTicks;
	/** List of all armors with set effects */

	private ArrayList<BlockArmorItem> armors;
	/** List of all unique set effect tooltips and blocks they are valid for */
	private Map<TextComponent, ArrayList<ItemStack>> tooltips;

	public GuiArmorDisplay() {
		super(new TextComponent("Armor Display"));
		guiPlayer = new EntityGuiPlayer(Minecraft.getInstance().level, Minecraft.getInstance().player.getGameProfile(), Minecraft.getInstance().player);
		//initialize armors with all armor that has a set effect
		armors = new ArrayList<BlockArmorItem>();
		tooltips = new TreeMap<TextComponent, ArrayList<ItemStack>>(new Comparator<TextComponent>() {
			@Override
			public int compare(TextComponent arg0, TextComponent arg1) {
				return ChatFormatting.stripFormatting(arg0.getString()).compareTo(
						ChatFormatting.stripFormatting(arg1.getString()));
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
							Component tooltip = effect.addInformation(new ItemStack(set.helmet), true, guiPlayer, Lists.newArrayList(), Default.NORMAL).get(0);
							ArrayList<ItemStack> stacks = new ArrayList<ItemStack>(); 
							stacks.add(0, set.getStack());
							if (tooltips.containsKey(tooltip)) 
								stacks.addAll(0, tooltips.get(tooltip));
							tooltips.put((TextComponent) tooltip, stacks);
						}
					}						
			}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		try {
			guiPlayer.setInvisible(GUI_MODE == 0 || GUI_MODE == 1);
			// background
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			if (GUI_MODE == 4 || GUI_MODE == 5)
				RenderSystem.setShaderTexture(0, backgroundTooltipColor);
			else if (GUI_MODE == 0)
				RenderSystem.setShaderTexture(0, backgroundBlue);
			else
				RenderSystem.setShaderTexture(0, backgroundWhite);
			matrix.pushPose();
			float scale = 1f;
			if (GUI_MODE == 0)
				matrix.scale(2.2f, 2, scale);
			else if (GUI_MODE == 1)
				matrix.scale(1.47f*2, 1.445f*2, scale);
			else if (GUI_MODE == 2)
				matrix.scale(2.5f, 1.31f, scale);
			this.blit(matrix, 0, 0, 0, 0, this.width, this.height);
			matrix.popPose();

			// tooltips / items
			if (GUI_MODE == 3 || GUI_MODE == 4 || GUI_MODE == 5) {
				int index = 0;
				for (int i=0; i<tooltips.size(); i++) {
					int numStacks = tooltips.get(tooltips.keySet().toArray(new TextComponent[0])[i]).size();
					int itemsPerRow = numStacks > 10 ? numStacks/2+1 : numStacks;
					float x = 0;
					float y = 0;
					matrix.pushPose();
					float spaceBetween = 49.5f;
					index -= GUI_PAGE*20;
					// first column
					if (index >= 0 && index <= 8) {
						x = width/3.8f;
						y = 20+index*spaceBetween;
					}
					// second column
					else if (index > 8 && index <= 19) {
						x = width/1.35f;
						y = 155+(index-10)*spaceBetween;
					}
					// not visible
					else {
						x = 20035;
						y = 200+(index-24)*spaceBetween;
					}
					if (numStacks > itemsPerRow)
						y += 10;
					index += GUI_PAGE*20;
					List<String> tooltip = new ArrayList<String>();
					tooltip.add(tooltips.keySet().toArray(new TextComponent[0])[i].getString());
					if (numStacks > itemsPerRow)
						index += 2;
					else
						++index;
					// formatting
					String name = tooltip.get(0).substring(0, tooltip.get(0).indexOf(":"));
					String description = tooltip.get(0).substring(tooltip.get(0).indexOf(":"));
					SetEffect effect = SetEffect.nameToSetEffectMap.get(name);
					if (effect != null) 
						tooltip.set(0, effect.color+""+ChatFormatting.BOLD+name+ChatFormatting.RESET+effect.color+description);
					// widen tooltips
					if (tooltip.get(0).contains("Regrowth")) 
						tooltip.set(0, "                     "+tooltip.get(0)+"                     ");
					else if (tooltip.get(0).contains("Invisibility")) 
						tooltip.set(0, "                                 "+tooltip.get(0)+"                                 ");
					else if (tooltip.get(0).contains("Soft Fall")) 
						tooltip.set(0, "                         "+tooltip.get(0)+"                         ");
					else if (tooltip.get(0).contains("Falling"))
						tooltip.set(0, "              "+tooltip.get(0)+"              ");
					else if (tooltip.get(0).contains("Hoarder") && !tooltip.get(0).contains("Ender")) 
						tooltip.set(0, "                         "+tooltip.get(0)+"                         ");
					else if (tooltip.get(0).contains("Rocky"))
						tooltip.set(0, "                                                            "+tooltip.get(0)+"                                                            ");
					else if (tooltip.get(0).contains("Sleepy"))
						tooltip.set(0, "                        "+tooltip.get(0)+"                        ");

					tooltip.add("");
					tooltip.add("");
					tooltip.add("");
					if (numStacks > itemsPerRow) {
						tooltip.add("");
						tooltip.add("");
						tooltip.add("");
					}
					
					int length = 0;
					for (String string : tooltip)
						if (this.font.width(string) > length)
							length = this.font.width(string);
					// tooltips
					if (GUI_MODE == 3 || GUI_MODE == 5)
						this.renderComponentTooltip(matrix, tooltip.stream().map(str -> new TextComponent(str)).collect(Collectors.toList()), (int)x-length/2, (int)y, font);
					// items
					if (GUI_MODE != 3) {
						Lighting.setupFor3DItems();
						length = numStacks * 20;
						scale = 1.6f;
						for (int j=0; j<numStacks; j++) {
							int rowSize = j > itemsPerRow ? (numStacks % itemsPerRow+1) : Math.min(numStacks, itemsPerRow);
							int k = j > itemsPerRow ? (rowSize == 0 ? 0 : j % (rowSize+0)) + 0 : j;
							spaceBetween = 26 - (rowSize-1)/5;
							this.itemRenderer.blitOffset = 500;
							ItemStack stack = tooltips.get(tooltips.keySet().toArray(new TextComponent[0])[i]).get(j);
							if (j > itemsPerRow) {
								this.renderGuiItem(stack,
										(int) (x+2+(k*spaceBetween - (rowSize-1)*spaceBetween/2)), (int)y+32, scale);
							}
							else {
								this.renderGuiItem(stack,
										(int) (x+2+(k*spaceBetween - (rowSize-(numStacks > itemsPerRow ? 0 : 1))*spaceBetween/2)), (int)y+5, scale);
							}
						}
						Lighting.setupForFlatItems();
					}
					matrix.popPose();
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
					guiPlayer.setItemSlot(EquipmentSlot.HEAD, helmet);
					guiPlayer.setItemSlot(EquipmentSlot.CHEST, chestplate);
					guiPlayer.setItemSlot(EquipmentSlot.LEGS, leggings);
					guiPlayer.setItemSlot(EquipmentSlot.FEET, boots);
					//update items
					helmet.getItem().inventoryTick(helmet, guiPlayer.level, guiPlayer, EquipmentSlot.HEAD.getIndex(), false);
					chestplate.getItem().inventoryTick(chestplate, guiPlayer.level, guiPlayer, EquipmentSlot.CHEST.getIndex(), false);
					leggings.getItem().inventoryTick(leggings, guiPlayer.level, guiPlayer, EquipmentSlot.LEGS.getIndex(), false);
					boots.getItem().inventoryTick(boots, guiPlayer.level, guiPlayer, EquipmentSlot.FEET.getIndex(), false);
					//draw gui player
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					matrix.pushPose();
					double spaceBetween = 0;
					if (GUI_MODE == 0) {
						scale = 25;
						int perRow = 27;
						int row = index/4 / perRow;
						double heightBetween = 49d + (row == 10 ? 100 : 0);
						spaceBetween = 20.4d;
						matrix.translate(-6+(index/4 % perRow)*spaceBetween, row*heightBetween+42, row);
					}
					else if (GUI_MODE == 1) {
						scale = 29f;
						double heightBetween = 45.2d;
						int perRow = 17*2;
						int row = index/4 / perRow;
						spaceBetween = 21.8d;
						matrix.translate(-160+(index/4 % perRow)*spaceBetween, row*heightBetween+(row>15?100:0), row);
					}
					else if (GUI_MODE == 2) {
						scale = 50f;
						spaceBetween = 22.5d;
						if (index/4 < 7)
							matrix.translate(-250+index*spaceBetween, 2, 0);
						else
							matrix.translate(-915+index*(spaceBetween+2), 179, 0);
					}
					//RenderSystem.rotatef(30f, -0.5f, 1f, -0.1f);
					guiPlayer.yHeadRot = 0.0F;
					guiPlayer.yBodyRot = 0.0F;
					Lighting.setupNetherLevel(new Matrix4f(new Quaternion(5, 1, 1, 1)));
					InventoryScreen.renderEntityInInventory(0, 0, (int) scale, 0, 0, guiPlayer); 
					//Lighting.turnOff();
					matrix.popPose();
					//render tooltip
					if (GUI_MODE == 2) {
						ItemStack stack = new ItemStack(armors.get(index));
						matrix.pushPose();
						scale = 0.651f;
						if (index/4 < 7)
							matrix.translate(40+index*spaceBetween, 116, 0);
						else
							matrix.translate(-625+index*(spaceBetween+2), 293, 0);
						matrix.scale(scale, scale, scale);
						int length = 0;
						ArrayList<Component> tooltip = new ArrayList<Component>();
						tooltip.add(new TextComponent(ChatFormatting.AQUA+""+ChatFormatting.UNDERLINE+stack.getHoverName().getString().replace("Helmet", "Armor")));
						//armors.get(index).addFullSetEffectTooltip(tooltip);
						this.addStatTooltips(tooltip, new ItemStack[] {helmet, chestplate, leggings, boots});
						for (Component string : tooltip) {
							if (this.font.width(string) > length)
								length = this.font.width(string);
						}
						this.renderComponentTooltip(matrix, tooltip, -length/2, 0, font);
						matrix.popPose();
						Lighting.setupFor3DItems();
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
	private ArrayList<Component> addStatTooltips(ArrayList<Component> tooltip, ItemStack[] armor) {
		ArrayList<Multimap<Attribute, AttributeModifier>> list = new ArrayList<Multimap<Attribute, AttributeModifier>>();
		list.add(armor[0].getAttributeModifiers(EquipmentSlot.HEAD));
		list.add(armor[1].getAttributeModifiers(EquipmentSlot.CHEST));
		list.add(armor[2].getAttributeModifiers(EquipmentSlot.LEGS));
		list.add(armor[3].getAttributeModifiers(EquipmentSlot.FEET));

		//add boot enchantments
		if (armor[3].hasTag())
		{
			ListTag nbttaglist = armor[3].getEnchantmentTags();
			if (nbttaglist != null)
				for (int j = 0; j < nbttaglist.size(); ++j)
				{
					int k = nbttaglist.getCompound(j).getShort("id");
					int l = nbttaglist.getCompound(j).getShort("lvl");

					if (Enchantment.byId(k) != null)
						tooltip.add(new TextComponent(ChatFormatting.GRAY+Enchantment.byId(k).getFullname(l).getString()));
				}
		}

		for (ItemStack stack : armor) {
			int i = 0;
			for(EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
				Multimap<Attribute, AttributeModifier> multimap = stack.getAttributeModifiers(equipmentslottype);
				if (!multimap.isEmpty()) {
					tooltip.add(TextComponent.EMPTY);
					tooltip.add((new TranslatableComponent("item.modifiers." + equipmentslottype.getName())).withStyle(ChatFormatting.GRAY));

					for(Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
						AttributeModifier attributemodifier = entry.getValue();
						double d0 = attributemodifier.getAmount();
						boolean flag = false;
						if (guiPlayer != null) {
							if (attributemodifier.getId() == SetEffect.ATTACK_DAMAGE_UUID) {
								d0 = d0 + guiPlayer.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
								d0 = d0 + (double)EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
								flag = true;
							} else if (attributemodifier.getId() == SetEffect.ATTACK_SPEED_UUID) {
								d0 += guiPlayer.getAttributeBaseValue(Attributes.ATTACK_SPEED);
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
							tooltip.add((new TextComponent(" ")).append(new TranslatableComponent("attribute.modifier.equals." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslatableComponent(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN));
						} else if (d0 > 0.0D) {
							tooltip.add((new TranslatableComponent("attribute.modifier.plus." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslatableComponent(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.BLUE));
						} else if (d0 < 0.0D) {
							d1 = d1 * -1.0D;
							tooltip.add((new TranslatableComponent("attribute.modifier.take." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslatableComponent(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.RED));
						}
					}
				}
			}
		}
		return tooltip;
	}


	/**Copied & modified to adjust scale
	 * @param scale */
	public void renderGuiItem(ItemStack p_115124_, int p_115125_, int p_115126_, float scale) {
		this.renderGuiItem(p_115124_, p_115125_, p_115126_, Minecraft.getInstance().getItemRenderer().getModel(p_115124_, (Level)null, (LivingEntity)null, 0), scale);
	}

	/**Copied & modified to adjust scale*/
	protected void renderGuiItem(ItemStack stack, int x, int y, BakedModel model, float scale) {
		Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
		RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		PoseStack posestack = RenderSystem.getModelViewStack();
		posestack.pushPose();
		posestack.translate((double)x, (double)y, (double)(100.0F + Minecraft.getInstance().getItemRenderer().blitOffset));
		posestack.translate(8.0D, 8.0D, 0.0D);
		posestack.scale(1.0F, -1.0F, 1.0F);
		posestack.scale(16.0F, 16.0F, 16.0F);
		posestack.scale(scale, scale, scale);
		RenderSystem.applyModelViewMatrix();
		PoseStack posestack1 = new PoseStack();
		MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
		boolean flag = !model.usesBlockLight();
		if (flag) 
			Lighting.setupForFlatItems();

		// white glow behind
		for (int i=0; i<1; ++i) {
			posestack1.pushPose();
			posestack1.scale(1.1F, 1.1F, 1.1F);
			posestack1.translate(0, 0, -1);
			//posestack1.translate(10.0D, 0.0D, 0.0D);
			Minecraft.getInstance().getItemRenderer().render(stack, ItemTransforms.TransformType.GUI, false, posestack1, multibuffersource$buffersource, 15728880, OverlayTexture.pack(8, 10), model);
			multibuffersource$buffersource.endBatch();
			posestack1.popPose();
		}

		Minecraft.getInstance().getItemRenderer().render(stack, ItemTransforms.TransformType.GUI, false, posestack1, multibuffersource$buffersource, 15728880, OverlayTexture.NO_OVERLAY, model);
		multibuffersource$buffersource.endBatch();
		RenderSystem.enableDepthTest();
		if (flag) {
			Lighting.setupFor3DItems();
		}

		posestack.popPose();
		RenderSystem.applyModelViewMatrix();
	}

}