package twopiradians.blockArmor.common.seteffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

public class SetEffect {

	public static final UUID ATTACK_SPEED_UUID = UUID.fromString("3094e67f-88f1-4d81-a59d-655d4e7e8065");
	public static final UUID ATTACK_STRENGTH_UUID = UUID.fromString("d7dfa4ea-1cdf-4dd9-8842-883d7448cb00");
//	protected static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("308e48ee-a300-4846-9b56-05e53e35eb8f");
//	protected static final UUID KNOCKBACK_RESISTANCE_UUID = UUID.fromString("c8bb1118-78be-4864-9de3-a718047d28bd");
//	protected static final UUID MAX_HEALTH_UUID = UUID.fromString("0fefa40c-fd5a-4019-a25e-7fffc8dcf621");
//	protected static final UUID LUCK_UUID = UUID.fromString("537fd0e2-78ef-4dd3-affb-959ff059b1bd");
//
//	protected static final String TEXT_FORMATTING_SET_EFFECT_HEADER = TextFormatting.ITALIC+""+TextFormatting.GOLD;
//	protected static final String TEXT_FORMATTING_SET_EFFECT_DESCRIPTION = TextFormatting.WHITE+"";
//	protected static final String TEXT_FORMATTING_SET_EFFECT_EXTRA = TextFormatting.GREEN+"";
	
	/**List of all set effects*/
	public static final ArrayList<SetEffect> SET_EFFECTS = new ArrayList<SetEffect> () {{
		add(new SetEffectInvisibility());
		add(new SetEffectIlluminated(0));
	}};
	/**Does set effect require button to activate*/
	protected boolean usesButton;
	/**Color of effect for tooltip*/
	protected TextFormatting color;
	/**Description of effect for tooltip*/
	protected String description;
	/**Potion effects that will be applied in onArmorTick*/
	protected ArrayList<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
	
	/**Goes through allSets and assigns set effects to appropriate sets*/
	public static void postInit() {
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
			if (!set.setEffects.isEmpty()) {
				ArmorSet.setsWithEffects.put(set, true);
				BlockArmor.logger.info(set.stack.getDisplayName()+": "+set.setEffects); //TODO change to debug when done testing
			}
		}
	}

	/**Checks if block's registry name contains any of the provided strings (with or without capitalized first letter)*/
	protected static boolean registryNameContains(Block block, String[] strings) {
		String registryName = block.getRegistryName().toString();
		for (String string : strings)
			if (registryName.contains(string) || registryName.contains(string.substring(0, 1).toUpperCase()+string.substring(1)))
				return true;

		return false;
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
	public boolean shouldApplyPotionEffect(PotionEffect potionEffect, World world, EntityPlayer player, ItemStack stack) {
		return true;
	}

	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		//apply potion effects
		for (PotionEffect potionEffect : this.potionEffects)
			if (this.shouldApplyPotionEffect(potionEffect, world, player, stack))
				player.addPotionEffect(new PotionEffect(potionEffect));
	}

	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
		ArmorSet set = ArmorSet.getSet((ItemBlockArmor) stack.getItem());
		
		if (!(entity instanceof EntityLivingBase) || !ArmorSet.isWearingFullSet((EntityLivingBase) entity, set) || 
				!ArmorSet.isSetEffectEnabled(set)) {
			stack.getTagCompound().setBoolean("isWearing", false);
			return;
		}

		stack.getTagCompound().setBoolean("isWearing", true);

		int cooldown = stack.getTagCompound().hasKey("cooldown") ? stack.getTagCompound().getInteger("cooldown") : 0;
		stack.getTagCompound().setInteger("cooldown", --cooldown);
	}

	/**Handles the attributes when wearing an armor set*/
	public Multimap<String, AttributeModifier> getAttributeModifiers(Multimap<String, AttributeModifier> map,
			EntityEquipmentSlot slot, ItemStack stack) {
		return map;
	}

	/**Set effect name and description if shifting*/
	public List<String> addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		ArmorSet set = ArmorSet.getSet((ItemBlockArmor) stack.getItem());
		
		String string = ArmorSet.isSetEffectEnabled(set) ? "" : TextFormatting.STRIKETHROUGH.toString();
		string += color+""+TextFormatting.BOLD+this.toString()+TextFormatting.RESET;
		if (GuiScreen.isShiftKeyDown())
			string += color+": "+TextFormatting.ITALIC+description;
		tooltip.add(string);
		
		return tooltip;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName().replace("SetEffect", "");
	}
}