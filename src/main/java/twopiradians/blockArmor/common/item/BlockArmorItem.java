package twopiradians.blockArmor.common.item;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
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
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import twopiradians.blockArmor.client.ClientProxy;
import twopiradians.blockArmor.client.model.ModelBAArmor;
import twopiradians.blockArmor.common.command.CommandDev;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.seteffect.SetEffect;

public class BlockArmorItem extends ArmorItem {
	/** Copied from ArmorItem bc private */
	private static final UUID[] ARMOR_MODIFIERS = new UUID[] { UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
			UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
			UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
			UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150") };
	/** The ArmorSet that this item belongs to */
	public ArmorSet set;
	/** Creative tab/group for this item (bc Item.group is final) */
	@Nullable
	public ItemGroup group;
	/** Armor material (bc ArmorItem.material is final) */
	private IArmorMaterial material;
	private HashMultimap<Attribute, AttributeModifier> attributes;

	public BlockArmorItem(BlockArmorMaterial material, EquipmentSlotType slot, ArmorSet set) {
		super(material, slot, new Item.Properties().group(ItemGroup.COMBAT)); // combat group for recipe book
		this.set = set;
		this.setMaterial(material);
	}

	/** Change armor texture based on block */
	@Override
	@OnlyIn(Dist.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		TextureAtlasSprite sprite = ArmorSet.getSprite(this);
		String texture = sprite.getName() + ".png";
		int index = texture.indexOf(":");
		texture = texture.substring(0, index + 1) + "textures/" + texture.substring(index + 1);
		return texture;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BipedModel getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlotType slot, BipedModel oldModel) {
		TextureAtlasSprite sprite = ArmorSet.getSprite(this);
		int width = sprite.getWidth();
		int height = sprite.getHeight() * sprite.getFrameCount();
		int currentFrame = ArmorSet.getCurrentAnimationFrame(this);
		int nextFrame = ArmorSet.getNextAnimationFrame(this);
		ModelBAArmor model = (ModelBAArmor) ClientProxy.getBlockArmorModel(entity, height, width, currentFrame,
				nextFrame, slot);
		model.color = ArmorSet.getColor(this);
		model.alpha = ArmorSet.getAlpha(this);
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
		return set.isEnabled() && group != null && (group == ItemGroup.SEARCH || group == this.group);
	}

	public void setGroup(ItemGroup group) {
		this.group = group;
	}

	/** Change display name based on the block */
	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return ArmorSet.getItemStackDisplayName(stack, this.getEquipmentSlot());
	}

	/** Handles the attributes when wearing an armor set */
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		Multimap<Attribute, AttributeModifier> map = slot == this.slot ? HashMultimap.create(this.attributes) : HashMultimap.create();
		if (slot != this.slot)
			return map;

		for (SetEffect effect : set.setEffects)
			map = effect.getAttributeModifiers(map, slot, stack);

		return map;
	}

	/** Set to have tooltip color show if item has effect */
	@Override
	public Rarity getRarity(ItemStack stack) {
		if (stack.isEnchanted())
			return Rarity.RARE;
		else if (!set.setEffects.isEmpty())
			return Rarity.UNCOMMON;
		else
			return Rarity.COMMON;
	}

	/** Deals with armor tooltips */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip,
			ITooltipFlag flagIn) {
		if (stack.hasTag() && stack.getTag().contains("devSpawned"))
			tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "" + TextFormatting.BOLD + "Dev Spawned"));

		if (!set.setEffects.isEmpty() && set.setEffects.get(0).isEnabled()) {
			// add header if shifting
			if (Screen.hasShiftDown())
				tooltip.add(new StringTextComponent(TextFormatting.ITALIC + "" + TextFormatting.GOLD + "Set Effects: "
						+ TextFormatting.ITALIC + "(require " + Config.piecesForSet
						+ (Config.piecesForSet == 4 ? "" : "+") + " pieces)"));

			// set effect names and descriptions if shifting
			for (SetEffect effect : set.setEffects)
				if (effect.isEnabled())
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
						&& stack.getTag().contains("devSpawned") && !CommandDev.DEVS.contains(entity.getUniqueID()))) {
			if (((PlayerEntity) entity).inventory.getStackInSlot(itemSlot) == stack)
				((PlayerEntity) entity).inventory.setInventorySlotContents(itemSlot, ItemStack.EMPTY);
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
				&& !CommandDev.DEVS.contains(player.getUniqueID())
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

	// ======================= CHANGE MATERIAL FROM CONFIG =======================

	public void setMaterial(IArmorMaterial material) {
		this.material = material;
		// recreate attributes
		this.attributes = HashMultimap.create();
		UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
		this.attributes.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier",
				(double) this.getDamageReduceAmount(), AttributeModifier.Operation.ADDITION));
		this.attributes.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness",
				(double) this.getToughness(), AttributeModifier.Operation.ADDITION));
		if (this.material.getKnockbackResistance() > 0)
			this.attributes.put(Attributes.KNOCKBACK_RESISTANCE,
					new AttributeModifier(uuid, "Armor knockback resistance",
							(this.material.getKnockbackResistance()) / 10d * Config.globalKnockbackResistanceModifier,
							AttributeModifier.Operation.ADDITION));
		// change max damage (bc method is final)
		this.maxDamage = (int) (material.getDurability(slot) * Config.globalDurabilityModifier);
	}

	@Override
	public IArmorMaterial getArmorMaterial() {
		return this.material;
	}

	@Override
	public int getDamageReduceAmount() {
		return (int) (this.material.getDamageReductionAmount(slot) * Config.globalDamageReductionModifier);
	}

	@Override
	public float getToughness() {
		return (float) (this.material.getToughness() * Config.globalToughnessModifier);
	}

	@Override
	public int getItemEnchantability() {
		return (int) (this.material.getEnchantability() * Config.globalEnchantabilityModifier);
	}

}