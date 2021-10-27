package twopiradians.blockArmor.common.seteffect;

import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.item.ArmorSet;

@Mod.EventBusSubscriber
public class SetEffectLucky extends SetEffect {

	protected SetEffectLucky() {
		super();
		this.color = ChatFormatting.DARK_GREEN;
		this.attributes.put(Attributes.LUCK, new AttributeModifier(LUCK_UUID, 
				"Luck", 3, AttributeModifier.Operation.ADDITION));
	}

	/**Increase looting*/
	@SubscribeEvent
	public static void addLooting(LootingLevelEvent event) { 
		if (event.getDamageSource() != null &&
				event.getDamageSource().getEntity() instanceof Player && 
				event.getEntity().level instanceof ServerLevel &&
				ArmorSet.getWornSetEffects((LivingEntity) event.getDamageSource().getEntity()).contains(SetEffect.LUCKY)) {
			event.setLootingLevel(event.getLootingLevel()+4);
			SetEffect.LUCKY.doParticlesAndSound((ServerLevel) event.getEntity().level, event.getEntity().blockPosition(), 
					(Player) event.getDamageSource().getEntity(), 4);
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"emerald", "luck"}))
			return true;		
		return false;
	}
	
	/**Spawn particles and make sound*/
	private void doParticlesAndSound(Level world, BlockPos pos, LivingEntity player, float amplifier) {
		if (world instanceof ServerLevel) {
			((ServerLevel)world).sendParticles(ParticleTypes.HAPPY_VILLAGER, 
					pos.getX()+0.5d, pos.getY()+0.5d, pos.getZ()+0.5d, 5, 0.4f, 0.4f, 0.4f, 0);
			world.playSound(null, player.getX(), player.getY(), player.getZ(), 
					SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 
					0.05f*amplifier, world.random.nextFloat()+0.9f);
		}
	}

	public static class SetEffectLuckyModifier extends LootModifier {

		protected SetEffectLuckyModifier(LootItemCondition[] conditionsIn) {
			super(conditionsIn);
		}

		/**Increase fortune*/
		@Override
		protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
			Entity entityIn = context.getParamOrNull(LootContextParams.THIS_ENTITY);
			if (entityIn instanceof LivingEntity && 
					ArmorSet.getWornSetEffects((LivingEntity) entityIn).contains(SetEffect.LUCKY)) {
				LivingEntity entity = (LivingEntity) entityIn;
				Vec3 pos = context.getParamOrNull(LootContextParams.ORIGIN);
				BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
				ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
				// make sure this doesn't drop the same block (i.e. Iron Ore)
				Item item = state != null ? state.getBlock().asItem() : null;
				if (item != null)
					for (ItemStack stack : generatedLoot)
						if (stack != null && stack.getItem() == item)
							return generatedLoot;
				if (pos != null && state != null && state.getBlock() instanceof OreBlock &&
						(tool == null || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) <= 0)) {
					int beforeCount = 0;
					int afterCount = 0;
					for (ItemStack stack : generatedLoot) {
						beforeCount += stack.getCount();
						stack.setCount(Math.min(stack.getCount()*2, stack.getMaxStackSize()));
						afterCount += stack.getCount();
					}
					if (afterCount > beforeCount) {
						SetEffect.LUCKY.doParticlesAndSound(entity.level, new BlockPos(pos), entity, afterCount-beforeCount);
						SetEffect.LUCKY.damageArmor(entity, afterCount-beforeCount, false);
					}
				}
			}
			return generatedLoot;
		}

		public static class Serializer extends GlobalLootModifierSerializer<SetEffectLuckyModifier> {

			@Override
			public SetEffectLuckyModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
				return new SetEffectLuckyModifier(conditions);
			}

			@Override
			public JsonObject write(SetEffectLuckyModifier instance) {
				return this.makeConditions(instance.conditions);
			}

		}

	}

}