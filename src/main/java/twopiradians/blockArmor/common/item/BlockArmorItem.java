package twopiradians.blockArmor.common.item;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;
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
	public CreativeModeTab group;
	/** Armor material (bc ArmorItem.material is final) */
	private ArmorMaterial material;
	private HashMultimap<Attribute, AttributeModifier> attributes;

	public BlockArmorItem(BlockArmorMaterial material, EquipmentSlot slot, ArmorSet set) {
		super(material, slot, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)); // combat group for recipe book
		this.set = set;
		this.setMaterial(material);
	}

	/** Change armor texture based on block */
	@Override
	@OnlyIn(Dist.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		TextureAtlasSprite sprite = this.set.getTextureInfo(slot).sprite;
		String texture = sprite.getName() + ".png";
		int index = texture.indexOf(":");
		texture = texture.substring(0, index + 1) + "textures/" + texture.substring(index + 1);
		return texture;
	}

	@Override 
	@OnlyIn(Dist.CLIENT)
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		consumer.accept(new IItemRenderProperties() {
			@Override
			@OnlyIn(Dist.CLIENT)
			public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot armorSlot, A _default) {
				BlockArmorItem item = (BlockArmorItem) stack.getItem();
				TextureInfo info = item.set.getTextureInfo(item.slot);
				TextureAtlasSprite sprite = info.sprite;
				int width = sprite.getWidth();
				int height = info.originalHeight;
				int currentFrame = info.getCurrentAnimationFrame();
				int nextFrame = info.getNextAnimationFrame();
				ModelBAArmor model = (ModelBAArmor) ClientProxy.getBlockArmorModel(entity, height, width, currentFrame, nextFrame, slot);
				model.color = info.color;
				model.alpha = info.getAlpha();
				return (A) model;
			}
		});
	}

	/** Don't display item in creative tab/JEI if disabled */
	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (!set.isEnabled())
			return;
		else
			super.fillItemCategory(group, items);
	}

	@Override
	protected boolean allowdedIn(CreativeModeTab group) {
		return set.isEnabled() && group != null && (group == CreativeModeTab.TAB_SEARCH || group == this.group);
	}

	public void setGroup(CreativeModeTab group) {
		this.group = group;
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		// default
		stack.getOrCreateTag().putInt("Damage", Math.max(0, damage));

		// spawn hoarder items
		if (damage >= this.getMaxDamage(stack) && this.set.setEffects.contains(SetEffect.HOARDER))
			SetEffect.HOARDER.onBreak(stack);
	}

	/** Change display name based on the block */
	@Override
	public Component getName(ItemStack stack) {
		return ArmorSet.getItemStackDisplayName(stack.getItem(), this.getSlot());
	}

	/** Handles the attributes when wearing an armor set */
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
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
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip,
			TooltipFlag flagIn) {
		if (stack.hasTag() && stack.getTag().contains("devSpawned"))
			tooltip.add(new TextComponent(ChatFormatting.DARK_PURPLE + "" + ChatFormatting.BOLD + "Dev Spawned"));

		if (!set.setEffects.isEmpty() && set.setEffects.get(0).isEnabled()) {
			// add header if shifting
			if (Screen.hasShiftDown())
				tooltip.add(new TranslatableComponent("item.blockarmor.tooltip.setEffects", 
						new TranslatableComponent("item.blockarmor.tooltip.setEffectsRequire"+(Config.piecesForSet == 4 ? "" : "+"), Config.piecesForSet)
						.withStyle(ChatFormatting.ITALIC), Config.piecesForSet)
						.withStyle(ChatFormatting.GOLD));

			// set effect names and descriptions if shifting
			for (SetEffect effect : set.setEffects)
				if (effect.isEnabled())
					tooltip = effect.addInformation(stack, Screen.hasShiftDown(), Minecraft.getInstance().player,
							tooltip, flagIn);
		}
	}

	/** Mostly handles nbt and enchanting armor */
	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
		// delete dev spawned items if not in dev's inventory and delete disabled items
		// (except missingTexture items in SMP)
		if (stack.isEmpty() || (!set.isEnabled() && !world.isClientSide & entity instanceof Player)
				|| (!world.isClientSide && entity instanceof Player && stack.hasTag()
						&& stack.getTag().contains("devSpawned") && !CommandDev.DEVS.contains(entity.getUUID()))) {
			if (((Player) entity).getInventory().getItem(itemSlot) == stack)
				((Player) entity).getInventory().setItem(itemSlot, ItemStack.EMPTY);
			return;
		}

		if (!stack.hasTag())
			stack.setTag(new CompoundTag());

		for (SetEffect effect : set.setEffects)
			effect.onUpdate(stack, world, entity, slot.getFilterFlag(), isSelected);
	}

	/** Delete dev spawned dropped items */
	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entityItem) {
		// delete dev spawned items if not worn by dev and delete disabled items (except
		// missingTexture items in SMP)
		if ((!set.isEnabled() && !entityItem.level.isClientSide)
				|| (!entityItem.level.isClientSide && entityItem != null && entityItem.getItem() != null
				&& entityItem.getItem().hasTag() && entityItem.getItem().getTag().contains("devSpawned"))) {
			entityItem.discard();
			return true;
		}
		return false;
	}

	/** Handles most of the armor set special effects and bonuses. */
	@Override
	public void onArmorTick(ItemStack stack, Level world, Player player) {
		// delete dev spawned items if not worn by dev and delete disabled items (except
		// missingTexture items in SMP)
		if (stack.isEmpty() || (!set.isEnabled() && !world.isClientSide)
				|| (!world.isClientSide && stack != null && stack.hasTag() && stack.getTag().contains("devSpawned")
				&& !CommandDev.DEVS.contains(player.getUUID())
				&& player.getItemBySlot(this.slot) == stack)) {
			player.setItemSlot(this.slot, ItemStack.EMPTY);
			return;
		}

		if (!stack.hasTag())
			stack.setTag(new CompoundTag());

		for (SetEffect effect : set.setEffects)
			if (ArmorSet.getWornSetEffects(player).contains(effect))
				effect.onArmorTick(world, player, stack);
	}

	// ======================= CHANGE MATERIAL FROM CONFIG =======================

	public void setMaterial(ArmorMaterial material) {
		this.material = material;
		// recreate attributes
		this.attributes = HashMultimap.create();
		UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
		this.attributes.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier",
				(double) this.getDefense(), AttributeModifier.Operation.ADDITION));
		this.attributes.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness",
				(double) this.getToughness(), AttributeModifier.Operation.ADDITION));
		if (this.material.getKnockbackResistance() > 0)
			this.attributes.put(Attributes.KNOCKBACK_RESISTANCE,
					new AttributeModifier(uuid, "Armor knockback resistance",
							(this.material.getKnockbackResistance()) / 10d * Config.globalKnockbackResistanceModifier,
							AttributeModifier.Operation.ADDITION));
		// change max damage (bc method is final)
		this.maxDamage = (int) (material.getDurabilityForSlot(slot) * Config.globalDurabilityModifier);
	}

	@Override
	public ArmorMaterial getMaterial() {
		return this.material;
	}

	@Override
	public int getDefense() {
		return (int) (this.material.getDefenseForSlot(slot) * Config.globalDamageReductionModifier);
	}

	@Override
	public float getToughness() {
		return (float) (this.material.getToughness() * Config.globalToughnessModifier);
	}

	@Override
	public int getEnchantmentValue() {
		return (int) (this.material.getEnchantmentValue() * Config.globalEnchantabilityModifier);
	}

}