package twopiradians.blockArmor.common.item;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import twopiradians.blockArmor.client.ClientProxy;
import twopiradians.blockArmor.client.model.ModelBlockArmor;
import twopiradians.blockArmor.common.seteffect.SetEffect;

public class BlockArmorItem extends ArmorItem {
	/** The ArmorSet that this item belongs to */
	public ArmorSet set;

	public ItemGroup group;

	public BlockArmorItem(BlockArmorMaterial material, int renderIndex, EquipmentSlotType equipmentSlot, ArmorSet set) {
		super(material, equipmentSlot, new Item.Properties().group(ItemGroup.COMBAT));
		this.set = set;
	}

	/** Change armor texture based on block */
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		TextureAtlasSprite sprite = ArmorSet.getSprite(this);
		String texture = sprite.getName() + ".png"; // TODO
		int index = texture.indexOf(":");
		texture = texture.substring(0, index + 1) + "textures/" + texture.substring(index + 1);
		return texture;
	}

	@Override
	public BipedModel getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlotType slot, BipedModel oldModel) {
		TextureAtlasSprite sprite = ArmorSet.getSprite(this);
		int width = sprite.getWidth();
		int height = sprite.getHeight() * sprite.getFrameCount();
		boolean isTranslucent = set.isTranslucent;
		int currentFrame = ArmorSet.getCurrentAnimationFrame(this);
		int nextFrame = ArmorSet.getNextAnimationFrame(this);
		int color = ArmorSet.getColor(this);
		float alpha = ArmorSet.getAlpha(this);
		ModelBlockArmor model = (ModelBlockArmor) ClientProxy.getBlockArmorModel(entity, height, width, currentFrame, nextFrame, slot);
		model.translucent = isTranslucent;
		model.color = color;
		model.alpha = alpha;
		return model;
	}

	/** Don't display item in creative tab/JEI if disabled */
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (!set.isEnabled())
			return;
		else
			super.fillItemGroup(group, items);
	}

	@Override
	protected boolean isInGroup(ItemGroup group) {
		return group != null && (group == ItemGroup.SEARCH || group == this.group);
	}

	/** Change display name based on the block */
	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return ArmorSet.getItemStackDisplayName(stack, this.getEquipmentSlot());
	}

	/** Handles the attributes when wearing an armor set */
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot) {
		/*Multimap<String, AttributeModifier> map = this.getItemAttributeModifiers(slot);
		if (slot != this.armorType)
			return map;

		for (SetEffect effect : set.setEffects)
			map = effect.getAttributeModifiers(map, slot, stack);

		return map;*/ // TODO
		return super.getAttributeModifiers(slot);
	}

	/*	*//** Set to have tooltip color show if item has effect *//*
																	@Override
																	public EnumRarity getRarity(ItemStack stack) {
																	if (stack.isItemEnchanted())
																	return EnumRarity.RARE;
																	else if (!set.setEffects.isEmpty())
																	return EnumRarity.UNCOMMON;
																	else
																	return EnumRarity.COMMON;
																	}*/

	/** Deals with armor tooltips */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (stack.hasTag() && stack.getTag().contains("devSpawned"))
			tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "" + TextFormatting.BOLD + "Dev Spawned"));

		if (!set.setEffects.isEmpty() && set.setEffects.get(0).isEnabled()) {
			// add header if shifting
			if (Screen.hasShiftDown())
				tooltip.add(new StringTextComponent(TextFormatting.ITALIC + "" + TextFormatting.GOLD + "Set Effects: " + TextFormatting.ITALIC
						+ "(requires 4" /*+ Config.piecesForSet + (Config.piecesForSet == 4 ? "" : "+")*/
						+ " pieces to be worn)"));

			// set effect names and descriptions if shifting
			for (SetEffect effect : set.setEffects)
				tooltip = effect.addInformation(stack, Screen.hasShiftDown(), Minecraft.getInstance().player,
						tooltip, flagIn);
		}
	}

	/** Mostly handles nbt and enchanting armor */
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		// delete dev spawned items if not in dev's inventory and delete disabled items
		// (except missingTexture items in SMP)
		if (stack.isEmpty() || (!set.isEnabled() && !world.isRemote & entity instanceof PlayerEntity)
				|| (!world.isRemote && entity instanceof PlayerEntity && stack.hasTag()
						&& stack.getTag().contains(
								"devSpawned")/* && !CommandDev.DEVS.contains(entity.getUniqueID())*/
						&& ((PlayerEntity) entity).inventory.getStackInSlot(slot.getSlotIndex()) == stack)) {
			if (((PlayerEntity) entity).inventory.getStackInSlot(slot.getSlotIndex()) == stack)
				((PlayerEntity) entity).inventory.setInventorySlotContents(slot.getSlotIndex(), ItemStack.EMPTY);
			return;
		}

		if (!stack.hasTag())
			stack.setTag(new CompoundNBT());

		for (SetEffect effect : set.setEffects)
			effect.onUpdate(stack, world, entity, slot.getSlotIndex(), isSelected);
	}

	/** Delete dev spawned dropped items */
	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entityItem) {
		// delete dev spawned items if not worn by dev and delete disabled items (except
		// missingTexture items in SMP)
		if ((!set.isEnabled() && !entityItem.world.isRemote)
				|| (!entityItem.world.isRemote && entityItem != null && entityItem.getItem() != null
				&& entityItem.getItem().hasTag() && entityItem.getItem().getTag().contains("devSpawned"))) {
			entityItem.remove();
			return true;
		}
		return false;
	}

	/** Handles most of the armor set special effects and bonuses. */
	@Override
	public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
		// delete dev spawned items if not worn by dev and delete disabled items (except
		// missingTexture items in SMP)
		if (stack.isEmpty() || (!set.isEnabled() && !world.isRemote)
				|| (!world.isRemote && stack != null && stack.hasTag() && stack.getTag().contains("devSpawned")
				/*&& !CommandDev.DEVS.contains(player.getUniqueID())*/
				&& player.getItemStackFromSlot(this.slot) == stack)) {
			player.setItemStackToSlot(this.slot, ItemStack.EMPTY);
			return;
		}

		if (!stack.hasTag())
			stack.setTag(new CompoundNBT());

		for (SetEffect effect : set.setEffects)
			if (ArmorSet.getWornSetEffects(player).contains(effect))
				effect.onArmorTick(world, player, stack);
	}

	public void setGroup(ItemGroup group) {
		this.group = group; // TEST
	}

}