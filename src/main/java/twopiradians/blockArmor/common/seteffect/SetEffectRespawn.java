package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.item.ArmorSet;

@Mod.EventBusSubscriber
public class SetEffectRespawn extends SetEffect {

	protected SetEffectRespawn() {
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
						ArmorSet.hasSetEffect(player, SetEffect.RESPAWN) && 
						!player.getCooldowns().isOnCooldown(ArmorSet.getFirstSetItem(player, SetEffect.RESPAWN).getItem())) {
					// set health to 1 and clear effects
					player.setHealth(1);
					player.removeAllEffects();
					player.level.broadcastEntityEvent(player, (byte)3);
					player.clearFire();
					player.getCombatTracker().recheckStatus();
					// set player's position and dimension to spawn point
					ServerLevel respawnWorld = player.server.getLevel(player.getRespawnDimension());
					if (respawnWorld == null)
						respawnWorld = player.server.overworld();
					BlockPos respawnPos = player.getRespawnPosition();
					if (respawnPos == null)
						respawnPos = respawnWorld.getSharedSpawnPos();
					if (respawnWorld != player.level) {
						player.handleInsidePortal(player.blockPosition());
						player.changeDimension(respawnWorld);
					}
					player.moveTo(respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(), 0, 0);
					player.setRespawnPosition(respawnWorld.dimension(), respawnPos, player.getRespawnAngle(), player.isRespawnForced(), false);
					player.connection.send(new ClientboundRespawnPacket(player.level.dimensionType(), player.level.dimension(), BiomeManager.obfuscateSeed(player.getLevel().getSeed()), player.gameMode.getGameModeForPlayer(), player.gameMode.getPreviousGameModeForPlayer(), player.getLevel().isDebug(), player.getLevel().isFlat(), true));
					player.connection.teleport(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
					player.connection.send(new ClientboundSetDefaultSpawnPositionPacket(respawnWorld.getSharedSpawnPos(), respawnWorld.getSharedSpawnAngle()));
					player.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
					player.initMenu(player.containerMenu); 
					// cooldown, damage, sound
					SetEffect.RESPAWN.setCooldown(player, 6000);
					SetEffect.RESPAWN.damageArmor(player, 100, true);
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
		if (SetEffect.registryNameContains(block, new String[] {"respawn"}))
			return true;
		return false;
	}

}