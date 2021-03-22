package twopiradians.blockArmor.common.seteffect;

import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectLucky extends SetEffect {

	public static SetEffectLucky INSTANCE;
	
	protected SetEffectLucky() {
		this.color = TextFormatting.DARK_GREEN;
		this.description = "Greatly increases Fortune, Looting, and Luck";
		this.attributeModifiers.add(new AttributeModifier(LUCK_UUID, 
				"Luck", 3, AttributeModifier.Operation.ADDITION));
		MinecraftForge.EVENT_BUS.register(this);
		INSTANCE = this;
	}
	
	/**Increase looting*/
	@SubscribeEvent
	public void addLooting(LootingLevelEvent event) { // TEST - does this fire for mining too???
		if (event.getDamageSource().getTrueSource() instanceof PlayerEntity && 
				event.getEntity().world instanceof ServerWorld &&
				ArmorSet.getWornSetEffects((LivingEntity) event.getDamageSource().getTrueSource()).contains(this)) {
			event.setLootingLevel(event.getLootingLevel()+4);
			doParticlesAndSound((ServerWorld) event.getEntity().world, event.getEntity().getPosition(), 
					(PlayerEntity) event.getDamageSource().getTrueSource(), 4);
		}
	}

	/**Spawn particles and make sound*/
	private void doParticlesAndSound(ServerWorld world, BlockPos pos, PlayerEntity player, float amplifier) {
		((ServerWorld)world).spawnParticle(ParticleTypes.HAPPY_VILLAGER, 
				pos.getX()+0.5d, pos.getY()+0.5d, pos.getZ()+0.5d, 5, 0.4f, 0.4f, 0.4f, 0);
		world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), 
				SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 
				0.05f*amplifier, world.rand.nextFloat()+0.9f);
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"emerald", "luck"}))
			return true;		
		return false;
	}
	
	public static class SetEffectLuckyModifier extends LootModifier {

		protected SetEffectLuckyModifier(ILootCondition[] conditionsIn) {
			super(conditionsIn);
		}

		/**Increase fortune*/
		@Override
		protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
			/*Entity entityIn = context.get(LootParameters.THIS_ENTITY);
			if (entityIn instanceof LivingEntity && ArmorSet.getWornSetEffects((LivingEntity) entityIn).contains(this)) {
				LivingEntity entity = (LivingEntity) entityIn;
				context.addCondition(new )
				
				
				NonNullList<ItemStack> newDrops = NonNullList.create();
				event.getState().getBlock().getDrops(newDrops, event.getWorld(), event.getPos(), event.getState(), event.getFortuneLevel()+4);
				if (newDrops.size() > event.getDrops().size()) {
					doParticlesAndSound((ServerWorld) event.getWorld(), event.getPos(), event.getHarvester(), 
							(newDrops.size()-event.getDrops().size()));
					
					event.getDrops().clear();
					event.getDrops().addAll(newDrops);
					this.damageArmor(event.getHarvester(), 1, false);
				}
			}*/ // TODO
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