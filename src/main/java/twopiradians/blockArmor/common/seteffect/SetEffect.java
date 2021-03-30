package twopiradians.blockArmor.common.seteffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.client.gui.armorDisplay.EntityGuiPlayer;
import twopiradians.blockArmor.client.key.KeyActivateSetEffect;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.BlockArmorItem;

public class SetEffect {

	public static final UUID ATTACK_SPEED_UUID = UUID.fromString("3094e67f-88f1-4d81-a59d-655d4e7e8065");
	public static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("d7dfa4ea-1cdf-4dd9-8842-883d7448cb00");
	protected static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("308e48ee-a300-4846-9b56-05e53e35eb8f");
	protected static final UUID KNOCKBACK_RESISTANCE_UUID = UUID.fromString("c8bb1118-78be-4864-9de3-a718047d28bd");
	protected static final UUID MAX_HEALTH_UUID = UUID.fromString("0fefa40c-fd5a-4019-a25e-7fffc8dcf621");
	protected static final UUID LUCK_UUID = UUID.fromString("537fd0e2-78ef-4dd3-affb-959ff059b1bd");

	/**List of all set effects*/
	public static final ArrayList<SetEffect> SET_EFFECTS = new ArrayList<SetEffect>() {{
		//effects that use the button
		add(new SetEffectCrafter());
		//add(new SetEffectDJ());
		add(new SetEffectEnder_Hoarder());
		add(new SetEffectIlluminated(0));
		add(new SetEffectSnowy());
		add(new SetEffectEnder());
		add(new SetEffectAbsorbent());
		add(new SetEffectExplosive());
		add(new SetEffectTime_Control(null));
		add(new SetEffectPusher());
		add(new SetEffectPuller());
		add(new SetEffectArrow_Defence());
		add(new SetEffectBonemealer());
		//effects that don't use the button
		add(new SetEffectMusical());
		add(new SetEffectSlow_Motion());
		add(new SetEffectSoft_Fall());
		add(new SetEffectFeeder());
		add(new SetEffectLightweight());
		add(new SetEffectInvisibility());
		add(new SetEffectImmovable(0));
		add(new SetEffectLucky());
		add(new SetEffectFiery());
		add(new SetEffectFrosty());
		add(new SetEffectRegrowth());
		add(new SetEffectPrickly());
		add(new SetEffectSlimey());
		add(new SetEffectSpeedy());
		add(new SetEffectFlame_Resistant());
		add(new SetEffectAutoSmelt());
		add(new SetEffectHealth_Boost(0));
		add(new SetEffectDiving_Suit());
		add(new SetEffectExperience_Giving());
		add(new SetEffectSlippery());
		add(new SetEffectFalling());
		add(new SetEffectPowerful());
	}};

	/**Does set effect require button to activate*/
	protected boolean usesButton;
	/**Color of effect for tooltip*/
	protected TextFormatting color;
	/**Potion effects that will be applied in onArmorTick*/
	protected ArrayList<EffectInstance> potionEffects = new ArrayList<EffectInstance>();
	/**AttributeModifiers that will be applied in getAttributeModifiers*/
	protected ArrayList<AttributeModifier> attributeModifiers = new ArrayList<AttributeModifier>();
	/**EnchantmentData that will be applied in onUpdate*/
	protected ArrayList<EnchantmentData> enchantments = new ArrayList<EnchantmentData>();
	/**Description of effect for tooltip*/
	public String description;

	/**Goes through allSets and assigns set effects to appropriate sets*/
	public static void setup() {
		for (ArmorSet set : ArmorSet.allSets) {
			boolean hasEffectWithButton = false;
			set.setEffects = new ArrayList<SetEffect>();
			for (SetEffect effect : SetEffect.SET_EFFECTS)
				//assign set effect if valid for block and set has max of 1 effect that uses button
				if (effect.isValid(set.block) && !(effect.usesButton && hasEffectWithButton)) {
					if (effect.usesButton)
						hasEffectWithButton = true;
					set.setEffects.add(effect.create(set.block));
				}
		}
	}

	/**Checks if block's registry name contains any of the provided strings (with or without capitalized first letter)*/
	public static boolean registryNameContains(Block block, String[] strings) {
		try {
			String registryName = block.getRegistryName().getPath();
			String displayName = new ItemStack(block, 1).getDisplayName().getUnformattedComponentText();
			for (String string : strings) {
				if (registryName.contains(string) || registryName.contains(string.substring(0, 1).toUpperCase()+string.substring(1)) ||
						displayName.contains(string.substring(0, 1).toUpperCase()+string.substring(1)))
					return true;
			}
		}
		catch (Exception e) {
			return false;
		}

		return false;
	}

	/**Is this set effect enabled in the config*/
	public boolean isEnabled() {
		return true;// TODO !Config.disabledSetEffects.contains(this.getClass());
	}

	/**Can be overwritten to return a new instance depending on the given block*/
	protected SetEffect create(Block block) {
		return this;
	}

	/**Should block be given this set effect*/
	protected boolean isValid(Block block) {
		return false;
	}

	/**Should player be given potionEffect now*/
	protected boolean shouldApplyEffect(EffectInstance potionEffect, World world, PlayerEntity player, ItemStack stack) {
		return true;
	}

	/**Damage worn armor with this effect, if enabled in config - split damage amongst items prioritizing highest durability items*/
	protected void damageArmor(LivingEntity entity, int amount, boolean ignoreConfig) {
		if ((!ignoreConfig/* && !Config.effectsUseDurability*/) || entity == null || entity.world.isRemote)
			return; // TODO ^

		//get list of all worn armor with this effect
		ArrayList<ItemStack> armor = new ArrayList<ItemStack>();
		for (EquipmentSlotType slot : ArmorSet.SLOTS) {
			ItemStack stack = entity.getItemStackFromSlot(slot);
			if (stack != null && stack.getItem() instanceof BlockArmorItem && 
					((BlockArmorItem)stack.getItem()).set.setEffects.contains(this))
				armor.add(stack);
		}

		for (int i=0; i<amount; ++i) {
			//find item with highest durability
			ItemStack highestDur = null;
			for (ItemStack stack : armor) {
				if (!stack.isEmpty() && (highestDur == null || 
						stack.getMaxDamage()-stack.getDamage() > 
				highestDur.getMaxDamage()-highestDur.getDamage()))
					highestDur = stack;
			}
			//if item will break, play sound and spawn particles and remove from armor
			if (highestDur != null && highestDur.getMaxDamage()-highestDur.getDamage() == 0) {
				armor.remove(highestDur);

				//play sound - item particles crash on server (because entity.renderBrokenItemStack() doesn't work on server
				entity.world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_ITEM_BREAK, 
						SoundCategory.PLAYERS, 0.8F, 0.8F + entity.world.rand.nextFloat() * 0.4F);
				highestDur.shrink(1);
			}
			else if (highestDur != null)
				highestDur.damageItem(1, entity, (e) -> {});
		}
	}

	/**Set cooldown for all worn BlockArmorItem on player for specified ticks*/
	protected void setCooldown(PlayerEntity player, int ticks) {
		if (player != null) 
			for (EquipmentSlotType slot : ArmorSet.SLOTS) {
				ItemStack stack = player.getItemStackFromSlot(slot);
				if (stack != null && stack.getItem() instanceof BlockArmorItem && 
						((BlockArmorItem)stack.getItem()).set.setEffects.contains(this))
					player.getCooldownTracker().setCooldown(stack.getItem(), ticks);
			}
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack) {			
			//apply potion effects
			for (EffectInstance potionEffect : this.potionEffects)
				if (this.shouldApplyEffect(potionEffect, world, player, stack))
					player.addPotionEffect(new EffectInstance(potionEffect));
		}
	}

	@SuppressWarnings("deprecation")
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
		if (!world.isRemote) {
			//keep track of wearingFullSet nbt if it has attributeModifiers
			if (!this.attributeModifiers.isEmpty()) {
				if (!(entity instanceof LivingEntity) || !ArmorSet.getWornSetEffects((LivingEntity) entity).contains(this) || 
						((LivingEntity) entity).getItemStackFromSlot(((BlockArmorItem)stack.getItem()).getEquipmentSlot()) != stack ||
						!this.isEnabled()) 
					stack.getTag().putBoolean("wearingFullSet", false);
				else
					stack.getTag().putBoolean("wearingFullSet", true);
			}

			//do enchantments
			if (!this.enchantments.isEmpty()) {
				ListNBT enchantNbt = stack.getTag().getList("ench", 10);
				for (EnchantmentData enchant : this.enchantments) {
					if (((BlockArmorItem)stack.getItem()).getEquipmentSlot() != enchant.slot)
						continue;
					//see if it has enchant already
					boolean hasEnchant = false;
					for (int i=0; i<enchantNbt.size(); i++) {
						if (enchantNbt.getCompound(i).getString("id") == Registry.ENCHANTMENT.getKey(enchant.ench).toString() &&
								enchantNbt.getCompound(i).getShort("lvl") >= enchant.level)
							hasEnchant = true;
					}

					//should remove enchantment
					if (hasEnchant && (!(entity instanceof LivingEntity) || 
							!ArmorSet.getWornSetEffects((LivingEntity) entity).contains(this) || !this.isEnabled()) ||
							((LivingEntity) entity).getItemStackFromSlot(((BlockArmorItem)stack.getItem()).getEquipmentSlot()) != stack) {
						for (int i=enchantNbt.size()-1; i>=0; i--)
							if (enchantNbt.getCompound(i).getBoolean(BlockArmor.MODID+" enchant"))
								enchantNbt.remove(i);
					}

					//should add enchantment
					else if (!hasEnchant && 
							((LivingEntity) entity).getItemStackFromSlot(((BlockArmorItem)stack.getItem()).getEquipmentSlot()) == stack &&
							ArmorSet.getWornSetEffects((LivingEntity) entity).contains(this) && this.isEnabled()) {
						CompoundNBT nbt = new CompoundNBT();
						nbt.putString("id", Registry.ENCHANTMENT.getKey(enchant.ench).toString());
						nbt.putShort("lvl", enchant.level);
						nbt.putBoolean(BlockArmor.MODID+" enchant", true);
						enchantNbt.add(nbt);
					}
				}
				if (enchantNbt.isEmpty())
					stack.getTag().remove("ench");
				else
					stack.getTag().put("ench", enchantNbt);
			}
		}
	}

	/**Handles the attributes when wearing an armor set*/
	public Multimap<String, AttributeModifier> getAttributeModifiers(Multimap<String, AttributeModifier> map,
			EquipmentSlotType slot, ItemStack stack) {

		if (!stack.hasTag())
			stack.setTag(new CompoundNBT());

		if (stack.getTag().getBoolean("wearingFullSet")) //FIXME removing piece will reset attributes
			for (AttributeModifier attribute : this.attributeModifiers)
				map.put(attribute.getName(), attribute);

		return map;
	}

	/**Set effect name and description if shifting*/

	public List<ITextComponent> addInformation(ItemStack stack, boolean isShiftDown, PlayerEntity player, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		String string = color.toString();
		if (player instanceof EntityGuiPlayer || (ArmorSet.getWornSetEffects(player).contains(this) && 
				player.getItemStackFromSlot(((BlockArmorItem)stack.getItem()).getEquipmentSlot()) == stack))
			string += TextFormatting.BOLD;
		string += this.isEnabled() ? "" : TextFormatting.STRIKETHROUGH.toString();
		string += this.toString()+TextFormatting.RESET;
		if (isShiftDown) {
			string += color;
			string += this.isEnabled() ? "" : TextFormatting.STRIKETHROUGH.toString();
			string += ": "+TextFormatting.ITALIC+description+TextFormatting.RESET;
			if (this.usesButton)
				string += TextFormatting.BLUE+" <"+TextFormatting.BOLD+KeyActivateSetEffect.ACTIVATE_SET_EFFECT.func_238171_j_().getUnformattedComponentText()
				+TextFormatting.RESET+""+TextFormatting.BLUE+">";
		}
		tooltip.add(new StringTextComponent(string));

		return tooltip;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName().replace("SetEffect", "").replace("_", " ");
	}

	@Override
	public boolean equals(Object obj) {
		return obj.getClass() == this.getClass() && this.description.equals(((SetEffect)obj).description);
	}

	/**Used to store data for enchantments easily*/
	static class EnchantmentData {
		public Enchantment ench;
		public Short level;
		public EquipmentSlotType slot;

		public EnchantmentData(Enchantment ench, Short level, EquipmentSlotType slot) {
			this.ench = ench;
			this.level = level;
			this.slot = slot;
		}
	}
}