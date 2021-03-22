package twopiradians.blockArmor.common.seteffect;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.items.ItemHandlerHelper;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectAutoSmelt extends SetEffect {

	public static SetEffectAutoSmelt INSTANCE;

	protected SetEffectAutoSmelt() {
		this.color = TextFormatting.DARK_RED;
		this.description = "Smelts harvested blocks";
		this.usesButton = true;
		MinecraftForge.EVENT_BUS.register(this);
		INSTANCE = this;
	}

	@Override
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack &&
				!world.isRemote && BlockArmor.key.isKeyDown(player) && !player.getCooldownTracker().hasCooldown(stack.getItem())) {
			boolean deactivated = !stack.getTag().getBoolean("deactivated");
			stack.getTag().putBoolean("deactivated", deactivated);
			player.sendMessage(new TranslationTextComponent(TextFormatting.GRAY+"[Block Armor] "+TextFormatting.ITALIC+"AutoSmelt set effect "
					+ (deactivated ? TextFormatting.RED+""+TextFormatting.ITALIC+"disabled." : TextFormatting.GREEN+""+TextFormatting.ITALIC+"enabled.")), null);
			this.setCooldown(player, 10);
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"furnace", "fire", "flame", "smelt"}))
			return true;		
		return false;
	}

	public static class SetEffectAutoSmeltModifier extends LootModifier {

		protected SetEffectAutoSmeltModifier(ILootCondition[] conditionsIn) {
			super(conditionsIn);
		}

		@Override
		protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
			Entity entityIn = context.get(LootParameters.THIS_ENTITY);
			if (entityIn instanceof LivingEntity && ArmorSet.getWornSetEffects((LivingEntity) entityIn).contains(this)) {
				LivingEntity entity = (LivingEntity) entityIn;
				ItemStack stack = ArmorSet.getFirstSetItem(entity, SetEffectAutoSmelt.INSTANCE);
				if (entity.world.isRemote || 
						EnchantmentHelper.getEnchantments(context.get(LootParameters.TOOL)).containsKey(Enchantments.SILK_TOUCH) || 
						!stack.hasTag() || stack.getTag().getBoolean("deactivated"))
					return generatedLoot;

				boolean smelted = false;

				ArrayList<ItemStack> newLoot = new ArrayList<ItemStack>();
				for (ItemStack oldStack : generatedLoot) {
					ItemStack newStack = smelt(oldStack, context);
					if (newStack != null)
						smelted = true;
					newLoot.add(newStack);
				}

				if (smelted) {
					Vector3d pos = context.get(LootParameters.ORIGIN);
					context.getWorld().spawnParticle(ParticleTypes.SMOKE, 
							pos.getX()+0.5f, pos.getY()+0.5f,pos.getZ()+0.5f, 
							10, 0.3f, 0.3f, 0.3f, 0);
					context.getWorld().playSound(null, entity.getPosition(), 
							SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 0.1f, context.getWorld().rand.nextFloat()+0.7f);			
					INSTANCE.damageArmor(entity, 1, false);
					return newLoot;
				}
			}
			return generatedLoot;
		}

		/**Returns smelted form of item or null if it can't be smelted*/
		@Nullable
		private static ItemStack smelt(ItemStack stack, LootContext context) {
			return context.getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), context.getWorld())
					.map(FurnaceRecipe::getRecipeOutput)
					.filter(itemStack -> !itemStack.isEmpty())
					.map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, stack.getCount() * itemStack.getCount()))
					.orElse(null);
		}

		public static class Serializer extends GlobalLootModifierSerializer<SetEffectAutoSmeltModifier> {

			@Override
			public SetEffectAutoSmeltModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
				return new SetEffectAutoSmeltModifier(conditions);
			}

			@Override
			public JsonObject write(SetEffectAutoSmeltModifier instance) {
				return this.makeConditions(instance.conditions);
			}

		}

	}
	
}