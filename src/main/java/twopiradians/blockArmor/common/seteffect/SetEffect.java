package twopiradians.blockArmor.common.seteffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import twopiradians.blockArmor.client.gui.EntityGuiPlayer;
import twopiradians.blockArmor.client.key.KeyActivateSetEffect;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.BlockArmorItem;
import twopiradians.blockArmor.packet.SSyncCooldownsPacket;

@Mod.EventBusSubscriber
public class SetEffect {

	public static final UUID ATTACK_SPEED_UUID = UUID.fromString("3094e67f-88f1-4d81-a59d-655d4e7e8065");
	public static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("d7dfa4ea-1cdf-4dd9-8842-883d7448cb00");
	protected static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("308e48ee-a300-4846-9b56-05e53e35eb8f");
	protected static final UUID KNOCKBACK_RESISTANCE_UUID = UUID.fromString("c8bb1118-78be-4864-9de3-a718047d28bd");
	protected static final UUID MAX_HEALTH_UUID = UUID.fromString("0fefa40c-fd5a-4019-a25e-7fffc8dcf621");
	protected static final UUID LUCK_UUID = UUID.fromString("537fd0e2-78ef-4dd3-affb-959ff059b1bd");

	public static HashMap<String, SetEffect> nameToSetEffectMap = Maps.newHashMap();

	/**List of all set effects*/
	public static final ArrayList<SetEffect> SET_EFFECTS = Lists.newArrayList();

	//effects that use the button
	public static final SetEffectCrafter CRAFTER = new SetEffectCrafter();
	//public static final SetEffect CRAFTER = new SetEffectDJ();
	public static final SetEffectEnder_Hoarder ENDER_HOARDER = new SetEffectEnder_Hoarder();
	public static final SetEffectIlluminated ILLUMINATED = new SetEffectIlluminated(0);
	public static final SetEffectSnowy SNOWY = new SetEffectSnowy();
	public static final SetEffectEnder ENDER = new SetEffectEnder();
	public static final SetEffectAbsorbent ABSORBENT = new SetEffectAbsorbent();
	public static final SetEffectExplosive EXPLOSIVE = new SetEffectExplosive();
	public static final SetEffectTime_Control TIME_CONTROL = new SetEffectTime_Control(null);
	public static final SetEffectPusher PUSHER = new SetEffectPusher();
	public static final SetEffectPuller PULLER = new SetEffectPuller();
	public static final SetEffectArrow_Defence ARROW_DEFENCE = new SetEffectArrow_Defence();
	public static final SetEffectBonemealer BONEMEALER = new SetEffectBonemealer();
	public static final SetEffectSleepy SLEEPY = new SetEffectSleepy();
	//effects that don't use the button
	public static final SetEffectMusical MUSICAL = new SetEffectMusical();
	public static final SetEffectSlow_Motion SLOW_MOTION = new SetEffectSlow_Motion();
	public static final SetEffectSoft_Fall SOFT_FALL = new SetEffectSoft_Fall();
	public static final SetEffectFeeder FEEDER = new SetEffectFeeder();
	public static final SetEffectLightweight LIGHTWEIGHT = new SetEffectLightweight();
	public static final SetEffectInvisibility INVISIBILITY = new SetEffectInvisibility();
	public static final SetEffectImmovable IMMOVABLE = new SetEffectImmovable(0);
	public static final SetEffectLucky LUCKY = new SetEffectLucky();
	public static final SetEffectFiery FIERY = new SetEffectFiery();
	public static final SetEffectFrosty FROSTY = new SetEffectFrosty();
	public static final SetEffectRegrowth REGROWTH = new SetEffectRegrowth();
	public static final SetEffectPrickly PRICKLY = new SetEffectPrickly();
	public static final SetEffectSlimey SLIMEY = new SetEffectSlimey();
	public static final SetEffectSpeedy SPEEDY = new SetEffectSpeedy();
	public static final SetEffectFlame_Resistant FLAME_RESISTANT = new SetEffectFlame_Resistant();
	public static final SetEffectAutoSmelt AUTOSMELT = new SetEffectAutoSmelt();
	public static final SetEffectHealth_Boost HEALTH_BOOST = new SetEffectHealth_Boost(0);
	public static final SetEffectDiving_Suit DIVING_SUIT = new SetEffectDiving_Suit();
	public static final SetEffectExperience_Giving EXPERIENCE_GIVING = new SetEffectExperience_Giving();
	public static final SetEffectSlippery SLIPPERY = new SetEffectSlippery();
	public static final SetEffectFalling FALLING = new SetEffectFalling();
	public static final SetEffectPowerful POWERFUL = new SetEffectPowerful();
	public static final SetEffectRocky ROCKY = new SetEffectRocky();
	public static final SetEffectRespawn RESPAWN = new SetEffectRespawn();
	public static final SetEffectUndying UNDYING = new SetEffectUndying();
	public static final SetEffectHoarder HOARDER = new SetEffectHoarder();

	/**Does set effect require button to activate*/
	protected boolean usesButton;
	/**Color of effect for tooltip*/
	protected TextFormatting color;
	/**Potion effects that will be applied in onArmorTick*/
	protected ArrayList<EffectInstance> potionEffects = new ArrayList<EffectInstance>();
	/**Attributes that will be applied in getAttributeModifiers*/
	protected HashMap<Attribute, AttributeModifier> attributes = Maps.newHashMap();
	/**EnchantmentData that will be applied in onUpdate*/
	protected ArrayList<EnchantmentData> enchantments = new ArrayList<EnchantmentData>();
	/**Description of effect for tooltip*/
	public String description;
	/**Name of this effect (class name without SetEffect)*/
	public String name;

	protected SetEffect() {
		this.name = this.getClass().getSimpleName().replace("SetEffect", "").replace("_", " ");
		// if this effect is unique (not in the list already) add it to SET_EFFECTS and maps
		boolean unique = true;
		for (SetEffect effect : SET_EFFECTS)
			if (effect.getClass() == this.getClass()) {
				unique = false;
				break;
			}
		if (unique) {
			SET_EFFECTS.add(this);
			nameToSetEffectMap.put(this.name, this);
		}
	}

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
			set.defaultSetEffects = new ArrayList(set.setEffects);
		}
	}

	/**Checks if block's registry name contains any of the provided strings (with or without capitalized first letter)*/
	public static boolean registryNameContains(Block block, String... strings) {
		try { // TODO only work for " word ", "<eof>word ", " word<eof>", "<eof>word<eof>" (NOT "asdfWord")
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

	/**Is this set effect enabled in the config
	 * Not used anymore - all set effects are enabled, but can be removed from sets*/
	public boolean isEnabled() {
		return true;
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
		if ((!ignoreConfig && !Config.effectsUseDurability) || entity == null || entity.world.isRemote)
			return;

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
				highestDur.damageItem(1, entity, (e) -> {}); // TODO on broken
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

	/**Send cooldowns to client bc it forgets about them when changing dimensions*/
	@SubscribeEvent
	public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getPlayer() instanceof ServerPlayerEntity)
			BlockArmor.NETWORK.send(PacketDistributor.PLAYER.with(()->(ServerPlayerEntity) event.getPlayer()), new SSyncCooldownsPacket(event.getPlayer()));
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
			//do enchantments
			if (!this.enchantments.isEmpty()) {
				ListNBT enchantNbt = stack.getEnchantmentTagList();
				for (EnchantmentData enchant : this.enchantments) {
					if (((BlockArmorItem)stack.getItem()).getEquipmentSlot() != enchant.slot)
						continue;
					//see if it has enchant already
					boolean hasEnchant = EnchantmentHelper.getEnchantmentLevel(enchant.ench, stack) >= enchant.level;

					//should remove enchantment
					if (hasEnchant && (!(entity instanceof LivingEntity) || 
							!ArmorSet.getWornSetEffects((LivingEntity) entity).contains(this) || !this.isEnabled()) ||
							((LivingEntity) entity).getItemStackFromSlot(((BlockArmorItem)stack.getItem()).getEquipmentSlot()) != stack) {
						for (int i=enchantNbt.size()-1; i>=0; i--)
							if (enchantNbt.getCompound(i).getBoolean(BlockArmor.MODID+" enchant"))
								enchantNbt.remove(i);
						stack.setTagInfo("Enchantments", enchantNbt);
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
						stack.setTagInfo("Enchantments", enchantNbt);
					}
				}
				if (enchantNbt.isEmpty())
					stack.getTag().remove("ench");
				else
					stack.getTag().put("ench", enchantNbt);
			}
		}
	}

	/**Update stack nbt to show full set for getAttributeModifiers
	 * Sometimes doesn't update items that are removed because 
	 * the event.to, event.from, and event.slot aren't always accurate*/
	@SubscribeEvent
	public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
		HashSet<SetEffect> effects = ArmorSet.getWornSetEffects(event.getEntityLiving());
		for (EquipmentSlotType slot : EquipmentSlotType.values())
			if (slot.getSlotType() == Group.ARMOR) {
				ItemStack stack = event.getEntityLiving().getItemStackFromSlot(slot);
				if (stack == event.getFrom())
					stack = event.getTo();
				if (stack != null && stack.getItem() instanceof BlockArmorItem) {
					if (!stack.hasTag())
						stack.setTag(new CompoundNBT());

					if (effects.containsAll(((BlockArmorItem)stack.getItem()).set.setEffects))
						stack.getTag().putBoolean("wearingFullSet", true);
					else
						stack.getTag().putBoolean("wearingFullSet", false);
				}
			}
	}

	/**Handles the attributes when wearing an armor set*/
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(Multimap<Attribute, AttributeModifier> map,
			EquipmentSlotType slot, ItemStack stack) {

		if (!stack.hasTag())
			stack.setTag(new CompoundNBT());

		if (stack.getTag().getBoolean("wearingFullSet")) {//FIXME removing piece will reset attributes (not sure how to fix)
			for (Attribute attribute : this.attributes.keySet())
				map.put(attribute, this.attributes.get(attribute));
		}

		return map;
	}

	/**Set effect name and description if shifting*/
	@OnlyIn(Dist.CLIENT)
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
				string += TextFormatting.BLUE+" <"+TextFormatting.BOLD+KeyActivateSetEffect.ACTIVATE_SET_EFFECT.func_238171_j_().getString().toUpperCase()
				+TextFormatting.RESET+""+TextFormatting.BLUE+">";
		}
		tooltip.add(new StringTextComponent(string));

		return tooltip;
	}

	@Override
	public String toString() {
		return this.name;
	}

	/**Override so instances of classes are the same as SetEffect.INSTANCE*/
	@Override
	public boolean equals(Object obj) {
		//System.out.println("comparing: "+this.toString()+" & "+obj.toString()+" = "+(obj.getClass() == this.getClass())); // TODO remove
		return obj.getClass() == this.getClass()/* && this.description.equals(((SetEffect)obj).description)*/; // removed 4/23/21 for playerSetEffects in ArmorSet
	}
	
	/**Override so instances of classes are the same as SetEffect.INSTANCE*/
	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}

	/**Write this effect to string for config (variables need to be included)*/
	public String writeToString() {
		return this.name;
	}

	/**Read an effect from this string in config (takes into account variables in parenthesis)*/
	public SetEffect readFromString(String str) throws Exception {
		return this;
	}

	/**Read an effect from this string in config*/
	@Nullable
	public static SetEffect getEffectFromString(String strIn) {
		// ignore anything in parenthesis for finding which effect this is
		String str = strIn;
		if (strIn.contains("("))
			str = strIn.substring(0, strIn.indexOf("(")).trim();
		SetEffect effect = SetEffect.nameToSetEffectMap.get(str);
		// get actual effect for this string (including variables in parenthesis)
		try {
			return effect.readFromString(strIn);
		}
		catch (Exception e) {
			return null;
		}
	}

	/**Used to store data for enchantments easily*/
	protected static class EnchantmentData {
		public Enchantment ench;
		public Short level;
		public EquipmentSlotType slot;

		public EnchantmentData(Enchantment ench, Short level, EquipmentSlotType slot) {
			this.ench = ench;
			this.level = level;
			this.slot = slot;
		}
	}
	
	/**Called when full set is first equipped*/
	public void onStart(PlayerEntity player) {}

	/**Called when full set is unequipped or player logged out*/
	public void onStop(PlayerEntity player) {}
	
}