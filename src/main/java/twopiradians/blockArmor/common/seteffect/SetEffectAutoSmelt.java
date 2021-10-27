package twopiradians.blockArmor.common.seteffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

@Mod.EventBusSubscriber
public class SetEffectAutoSmelt extends SetEffect {

	protected SetEffectAutoSmelt() {
		super();
		this.color = ChatFormatting.DARK_RED;
		this.usesButton = true;
	}

	@Override
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack &&
				!world.isClientSide && BlockArmor.key.isKeyDown(player) && !player.getCooldowns().isOnCooldown(stack.getItem())) {
			boolean deactivated = !stack.getTag().getBoolean("deactivated");
			stack.getTag().putBoolean("deactivated", deactivated);
			player.sendMessage(new TranslatableComponent(ChatFormatting.GRAY+""+ChatFormatting.ITALIC+"AutoSmelt set effect "
					+ (deactivated ? ChatFormatting.RED+""+ChatFormatting.ITALIC+"disabled" : ChatFormatting.GREEN+""+ChatFormatting.ITALIC+"enabled")), UUID.randomUUID());
			this.setCooldown(player, 10);
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"furnace", "fire", "flame", "smelt", "smoker", "coal"}) &&
				!SetEffect.registryNameContains(block, new String[] {"coral"}))
			return true;		
		return false;
	}

	/**Smelt mob drops*/
	@SubscribeEvent
	public static void smeltMobDrops(LivingDropsEvent event) { 
		if (event.getSource() != null && event.getEntity() != null && event.getEntity().level instanceof ServerLevel &&
				event.getSource().getEntity() instanceof LivingEntity &&
				ArmorSet.hasSetEffect((LivingEntity) event.getSource().getEntity(), SetEffect.AUTOSMELT) &&
				!(event.getEntity() instanceof Player)) { // don't work on players to prevent abuse
			// variables
			ServerLevel world = (ServerLevel) event.getEntity().level;
			boolean smelted = false;

			// check if disabled
			ItemStack stack = ArmorSet.getFirstSetItem((LivingEntity) event.getSource().getEntity(), SetEffect.AUTOSMELT);
			if (!stack.hasTag() || stack.getTag().getBoolean("deactivated"))
				return;

			// try smelting items
			for (ItemEntity item : event.getDrops()) {
				ItemStack newStack = smelt(item.getItem(), world);
				if (newStack != null) {
					smelted = true;
					item.setItem(newStack);
				}
			}

			// effects if smelted
			if (smelted) {
				Vec3 pos = event.getEntity().position();
				world.sendParticles(ParticleTypes.SMOKE, 
						pos.x()+0.5f, pos.y()+0.5f,pos.z()+0.5f, 
						10, 0.3f, 0.3f, 0.3f, 0);
				world.playSound(null, event.getEntity().blockPosition(), 
						SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.3f, world.random.nextFloat()+0.7f);			
				SetEffect.AUTOSMELT.damageArmor((LivingEntity) event.getSource().getEntity(), 1, false);
			}
		}
	}

	/**Returns smelted form of item or null if it can't be smelted*/
	@Nullable
	private static ItemStack smelt(ItemStack stack, Level world) {
		return world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), world)
				.map(SmeltingRecipe::getResultItem)
				.filter(itemStack -> !itemStack.isEmpty())
				.map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, stack.getCount() * itemStack.getCount()))
				.orElse(null);
	}

	public static class SetEffectAutoSmeltModifier extends LootModifier {

		protected SetEffectAutoSmeltModifier(LootItemCondition[] conditionsIn) {
			super(conditionsIn);
		}

		@Override
		protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
			Entity entityIn = context.getParamOrNull(LootContextParams.THIS_ENTITY);
			if (entityIn instanceof LivingEntity && ArmorSet.hasSetEffect((LivingEntity) entityIn, SetEffect.AUTOSMELT)) {
				LivingEntity entity = (LivingEntity) entityIn;
				ItemStack stack = ArmorSet.getFirstSetItem(entity, SetEffect.AUTOSMELT);
				BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
				ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);

				if (entity.level.isClientSide || 
						(tool != null && EnchantmentHelper.getEnchantments(tool).containsKey(Enchantments.SILK_TOUCH)) || 
						!stack.hasTag() || stack.getTag().getBoolean("deactivated"))
					return generatedLoot;

				boolean smelted = false;

				// smelt drops
				ArrayList<ItemStack> newLoot = new ArrayList<ItemStack>();
				for (ItemStack oldStack : generatedLoot) {
					ItemStack newStack = smelt(oldStack, context.getLevel());
					if (newStack != null) {
						smelted = true;
						newLoot.add(newStack);
					}
				}

				// smelt block
				if (!smelted && state != null) {
					ItemStack newStack = smelt(new ItemStack(state.getBlock()), context.getLevel());
					if (newStack != null)
						smelted = true;
					newLoot.add(newStack);
				}

				// effects if smelted
				if (smelted) {
					Vec3 pos = context.getParamOrNull(LootContextParams.ORIGIN);
					context.getLevel().sendParticles(ParticleTypes.SMOKE, 
							pos.x()+0.5f, pos.y()+0.5f,pos.z()+0.5f, 
							10, 0.3f, 0.3f, 0.3f, 0);
					context.getLevel().playSound(null, entity.blockPosition(), 
							SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.1f, context.getLevel().random.nextFloat()+0.7f);			
					SetEffect.AUTOSMELT.damageArmor(entity, 1, false);
					return newLoot;
				}
			}
			return generatedLoot;
		}

		public static class Serializer extends GlobalLootModifierSerializer<SetEffectAutoSmeltModifier> {

			@Override
			public SetEffectAutoSmeltModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
				return new SetEffectAutoSmeltModifier(conditions);
			}

			@Override
			public JsonObject write(SetEffectAutoSmeltModifier instance) {
				return this.makeConditions(instance.conditions);
			}

		}

	}

}