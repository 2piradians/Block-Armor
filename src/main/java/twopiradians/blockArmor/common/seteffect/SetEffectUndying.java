package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.item.ArmorSet;

@Mod.EventBusSubscriber
public class SetEffectUndying extends SetEffect {

	protected SetEffectUndying() {
		super();
		this.color = ChatFormatting.DARK_PURPLE;
	}
	
	/**Teleport player instead of them dying*/
	@SubscribeEvent
	public static void onEvent(LivingDeathEvent event) {
		try {
			if (event.getEntityLiving() instanceof ServerPlayer) {
				ServerPlayer player = (ServerPlayer) event.getEntityLiving();
				if (!player.level.isClientSide && 
						ArmorSet.hasSetEffect(player, SetEffect.UNDYING) && 
						!player.getCooldowns().isOnCooldown(ArmorSet.getFirstSetItem(player, SetEffect.UNDYING).getItem())) {
					// set health to 1 and clear effects
					player.setHealth(1);
					player.removeAllEffects();
					player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
					player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
					player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
					player.level.broadcastEntityEvent(player, (byte)35);
					// cooldown, damage, sound
					SetEffect.UNDYING.setCooldown(player, 6000);
					SetEffect.UNDYING.damageArmor(player, 100, true);
					player.connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, (double)player.getX(), (double)player.getY(), (double)player.getZ(), 1.0F, 1.0F));
					// cancel event so player doesn't die
					event.setCanceled(true);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"netherite", "ancient_debris"}))
			return true;
		return false;
	}
	
}