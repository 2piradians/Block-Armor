package twopiradians.blockArmor.common.seteffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twopiradians.blockArmor.client.key.KeyActivateSetEffect;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

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
		//add(new SetEffectDiorite_Vision()); 
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
	protected ArrayList<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
	/**AttributeModifiers that will be applied in getAttributeModifiers*/
	protected ArrayList<AttributeModifier> attributeModifiers = new ArrayList<AttributeModifier>();
	/**EnchantmentData that will be applied in onUpdate*/
	protected ArrayList<EnchantmentData> enchantments = new ArrayList<EnchantmentData>();
	/**Description of effect for tooltip*/
	public String description;

	/**Goes through allSets and assigns set effects to appropriate sets*/
	public static void postInit() {
		for (ArmorSet set : ArmorSet.allSets) {
			boolean hasEffectWithButton = false;
			set.setEffects = new ArrayList<SetEffect>();
			for (SetEffect effect : SetEffect.SET_EFFECTS)
				//assign set effect if valid for block and set has max of 1 effect that uses button
				if (effect.isValid(set.block, set.meta) && !(effect.usesButton && hasEffectWithButton)) {
					if (effect.usesButton)
						hasEffectWithButton = true;
					set.setEffects.add(effect.create(set.block));
				}
		}
	}

	/**Checks if block's registry name contains any of the provided strings (with or without capitalized first letter)*/
	protected static boolean registryNameContains(Block block, int meta, String[] strings) {
		try {
			String registryName = block.getRegistryName().getResourcePath();
			String displayName = new ItemStack(block, 1, meta).getDisplayName();
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
		return !Config.disabledSetEffects.contains(this.getClass());
	}

	/**Can be overwritten to return a new instance depending on the given block*/
	protected SetEffect create(Block block) {
		return this;
	}

	/**Should block be given this set effect*/
	protected boolean isValid(Block block, int meta) {
		return false;
	}

	/**Should player be given potionEffect now*/
	protected boolean shouldApplyPotionEffect(PotionEffect potionEffect, World world, EntityPlayer player, ItemStack stack) {
		return true;
	}

	/**Damage worn armor with this effect, if enabled in config - split damage amongst items prioritizing highest durability items*/
	protected void damageArmor(EntityLivingBase entity, int amount, boolean ignoreConfig) {
		if ((!ignoreConfig && !Config.effectsUseDurability) || entity == null || entity.worldObj.isRemote)
			return;

		//get list of all worn armor with this effect
		ArrayList<ItemStack> armor = new ArrayList<ItemStack>();
		for (EntityEquipmentSlot slot : ArmorSet.SLOTS) {
			ItemStack stack = entity.getItemStackFromSlot(slot);
			if (stack != null && stack.getItem() instanceof ItemBlockArmor && 
					((ItemBlockArmor)stack.getItem()).set.setEffects.contains(this))
				armor.add(stack);
		}

		for (int i=0; i<amount; ++i) {
			//find item with highest durability
			ItemStack highestDur = null;
			for (ItemStack stack : armor) {
				if (!stack.func_190926_b() && (highestDur == null || 
						stack.getMaxDamage()-stack.getItemDamage() > 
				highestDur.getMaxDamage()-highestDur.getItemDamage()))
					highestDur = stack;
			}
			//if item will break, play sound and spawn particles and remove from armor
			if (highestDur != null && highestDur.getMaxDamage()-highestDur.getItemDamage() == 0) {
				armor.remove(highestDur);

				//play sound - item particles crash on server (because entity.renderBrokenItemStack() doesn't work on server
				entity.worldObj.playSound(null, entity.getPosition(), SoundEvents.ENTITY_ITEM_BREAK, 
						SoundCategory.PLAYERS, 0.8F, 0.8F + entity.worldObj.rand.nextFloat() * 0.4F);
				highestDur.func_190918_g(1);
			}
			else if (highestDur != null)
				highestDur.damageItem(1, entity);
		}
	}

	/**Set cooldown for all worn ItemBlockArmor on player for specified ticks*/
	protected void setCooldown(EntityPlayer player, int ticks) {
		if (player != null) 
			for (EntityEquipmentSlot slot : ArmorSet.SLOTS) {
				ItemStack stack = player.getItemStackFromSlot(slot);
				if (stack != null && stack.getItem() instanceof ItemBlockArmor && 
						((ItemBlockArmor)stack.getItem()).set.setEffects.contains(this))
					player.getCooldownTracker().setCooldown(stack.getItem(), ticks);
			}
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		if (!world.isRemote && ArmorSet.getFirstSetItem(player, this) == stack) {			
			//apply potion effects
			for (PotionEffect potionEffect : this.potionEffects)
				if (this.shouldApplyPotionEffect(potionEffect, world, player, stack))
					player.addPotionEffect(new PotionEffect(potionEffect));
		}
	}

	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
		if (!world.isRemote) {
			//keep track of wearingFullSet nbt if it has attributeModifiers
			if (!this.attributeModifiers.isEmpty()) {
				if (!(entity instanceof EntityLivingBase) || !ArmorSet.getWornSetEffects((EntityLivingBase) entity).contains(this) || 
						((EntityLivingBase) entity).getItemStackFromSlot(((ItemBlockArmor)stack.getItem()).armorType) != stack ||
						!this.isEnabled()) 
					stack.getTagCompound().setBoolean("wearingFullSet", false);
				else
					stack.getTagCompound().setBoolean("wearingFullSet", true);
			}

			//do enchantments
			if (!this.enchantments.isEmpty()) {
				NBTTagList enchantNbt = stack.getTagCompound().getTagList("ench", 10);
				for (EnchantmentData enchant : this.enchantments) {
					if (((ItemBlockArmor)stack.getItem()).armorType != enchant.slot)
						continue;
					//see if it has enchant already
					boolean hasEnchant = false;
					for (int i=0; i<enchantNbt.tagCount(); i++) {
						if (enchantNbt.getCompoundTagAt(i).getShort("id") == (short)Enchantment.getEnchantmentID(enchant.ench) &&
								enchantNbt.getCompoundTagAt(i).getShort("lvl") >= enchant.level)
							hasEnchant = true;
					}

					//should remove enchantment
					if (hasEnchant && (!(entity instanceof EntityLivingBase) || 
							!ArmorSet.getWornSetEffects((EntityLivingBase) entity).contains(this) || !this.isEnabled()) ||
							((EntityLivingBase) entity).getItemStackFromSlot(((ItemBlockArmor)stack.getItem()).armorType) != stack) {
						for (int i=enchantNbt.tagCount()-1; i>=0; i--)
							if (enchantNbt.getCompoundTagAt(i).getBoolean(BlockArmor.MODID+" enchant"))
								enchantNbt.removeTag(i);
					}

					//should add enchantment
					else if (!hasEnchant && 
							((EntityLivingBase) entity).getItemStackFromSlot(((ItemBlockArmor)stack.getItem()).armorType) == stack &&
							ArmorSet.getWornSetEffects((EntityLivingBase) entity).contains(this) && this.isEnabled()) {
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setShort("id", (short)Enchantment.getEnchantmentID(enchant.ench));
						nbt.setShort("lvl", enchant.level);
						nbt.setBoolean(BlockArmor.MODID+" enchant", true);
						enchantNbt.appendTag(nbt);
					}
				}
				if (enchantNbt.hasNoTags())
					stack.getTagCompound().removeTag("ench");
				else
					stack.getTagCompound().setTag("ench", enchantNbt);
			}
		}
	}

	/**Handles the attributes when wearing an armor set*/
	public Multimap<String, AttributeModifier> getAttributeModifiers(Multimap<String, AttributeModifier> map,
			EntityEquipmentSlot slot, ItemStack stack) {

		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		if (stack.getTagCompound().getBoolean("wearingFullSet")) //FIXME removing piece will reset attributes
			for (AttributeModifier attribute : this.attributeModifiers)
				map.put(attribute.getName(), attribute);

		return map;
	}

	/**Set effect name and description if shifting*/
	@SideOnly(Side.CLIENT)
	public List<String> addInformation(ItemStack stack, boolean isShiftDown, EntityPlayer player, List<String> tooltip, boolean advanced) {
		String string = color+""+(ArmorSet.getWornSetEffects(player).contains(this) && player.getItemStackFromSlot(((ItemBlockArmor)stack.getItem()).armorType) == stack ? TextFormatting.BOLD : "");
		string += this.isEnabled() ? "" : TextFormatting.STRIKETHROUGH.toString();
		string += this.toString()+TextFormatting.RESET;
		if (isShiftDown) {
			string += color;
			string += this.isEnabled() ? "" : TextFormatting.STRIKETHROUGH.toString();
			string += ": "+TextFormatting.ITALIC+description+TextFormatting.RESET;
			if (this.usesButton)
				string += TextFormatting.BLUE+" <"+TextFormatting.BOLD+KeyActivateSetEffect.ACTIVATE_SET_EFFECT.getDisplayName()
				+TextFormatting.RESET+""+TextFormatting.BLUE+">";
		}
		tooltip.add(string);

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
		public EntityEquipmentSlot slot;

		public EnchantmentData(Enchantment ench, Short level, EntityEquipmentSlot slot) {
			this.ench = ench;
			this.level = level;
			this.slot = slot;
		}
	}
}