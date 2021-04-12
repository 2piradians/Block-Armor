package twopiradians.blockArmor.common.seteffect;

import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
		this.color = TextFormatting.DARK_GREEN;
		this.description = "Greatly increases Fortune, Looting, and Luck";
		this.attributes.put(Attributes.LUCK, new AttributeModifier(LUCK_UUID, 
				"Luck", 3, AttributeModifier.Operation.ADDITION));
	}

	/**Increase looting*/
	@SubscribeEvent
	public static void addLooting(LootingLevelEvent event) { 
		if (event.getDamageSource() != null &&
				event.getDamageSource().getTrueSource() instanceof PlayerEntity && 
				event.getEntity().world instanceof ServerWorld &&
				ArmorSet.getWornSetEffects((LivingEntity) event.getDamageSource().getTrueSource()).contains(SetEffect.LUCKY)) {
			event.setLootingLevel(event.getLootingLevel()+4);
			SetEffect.LUCKY.doParticlesAndSound((ServerWorld) event.getEntity().world, event.getEntity().getPosition(), 
					(PlayerEntity) event.getDamageSource().getTrueSource(), 4);
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
	private void doParticlesAndSound(World world, BlockPos pos, LivingEntity player, float amplifier) {
		if (world instanceof ServerWorld) {
			((ServerWorld)world).spawnParticle(ParticleTypes.HAPPY_VILLAGER, 
					pos.getX()+0.5d, pos.getY()+0.5d, pos.getZ()+0.5d, 5, 0.4f, 0.4f, 0.4f, 0);
			world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), 
					SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 
					0.05f*amplifier, world.rand.nextFloat()+0.9f);
		}
	}

	public static class SetEffectLuckyModifier extends LootModifier {

		protected SetEffectLuckyModifier(ILootCondition[] conditionsIn) {
			super(conditionsIn);
		}

		/**Increase fortune*/
		@Override
		protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
			Entity entityIn = context.get(LootParameters.THIS_ENTITY);
			if (entityIn instanceof LivingEntity && 
					ArmorSet.getWornSetEffects((LivingEntity) entityIn).contains(SetEffect.LUCKY)) {
				LivingEntity entity = (LivingEntity) entityIn;
				Vector3d pos = context.get(LootParameters.ORIGIN);
				BlockState state = context.get(LootParameters.BLOCK_STATE);
				if (pos != null && state != null && state.getBlock() instanceof OreBlock) {
					int beforeCount = 0;
					int afterCount = 0;
					for (ItemStack stack : generatedLoot) {
						beforeCount += stack.getCount();
						stack.setCount(Math.min(stack.getCount()*2, stack.getMaxStackSize()));
						afterCount += stack.getCount();
					}
					if (afterCount > beforeCount) {
						SetEffect.LUCKY.doParticlesAndSound(entity.world, new BlockPos(pos), entity, afterCount-beforeCount);
						SetEffect.LUCKY.damageArmor(entity, afterCount-beforeCount, false);
					}
				}
			}
			return generatedLoot;
		}

		public static class Serializer extends GlobalLootModifierSerializer<SetEffectLuckyModifier> {

			@Override
			public SetEffectLuckyModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
				return new SetEffectLuckyModifier(conditions);
			}

			@Override
			public JsonObject write(SetEffectLuckyModifier instance) {
				return this.makeConditions(instance.conditions);
			}

		}

	}

}