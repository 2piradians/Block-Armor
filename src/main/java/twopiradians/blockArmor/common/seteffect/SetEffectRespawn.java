package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SWorldSpawnChangedPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.item.ArmorSet;

@Mod.EventBusSubscriber
public class SetEffectRespawn extends SetEffect {

	protected SetEffectRespawn() {
		super();
		this.color = TextFormatting.DARK_PURPLE;
		this.description = "Teleports you to your respawn point before death";
	}

	/**Teleport player instead of them dying*/
	@SubscribeEvent
	public static void onEvent(LivingDeathEvent event) {
		try {
			if (event.getEntityLiving() instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
				if (!player.world.isRemote && 
						ArmorSet.hasSetEffect(player, SetEffect.RESPAWN) && 
						!player.getCooldownTracker().hasCooldown(ArmorSet.getFirstSetItem(player, SetEffect.RESPAWN).getItem())) {
					// set health to 1 and clear effects
					player.setHealth(1);
					player.clearActivePotions();
					player.world.setEntityState(player, (byte)3);
					player.extinguish();
					player.getCombatTracker().reset();
					// set player's position and dimension to spawn point
					ServerWorld respawnWorld = player.server.getWorld(player.func_241141_L_());
					if (respawnWorld == null)
						respawnWorld = player.server.func_241755_D_();
					BlockPos respawnPos = player.func_241140_K_();
					if (respawnPos == null)
						respawnWorld.getSpawnPoint();
					if (respawnWorld != player.world) {
						player.setPortal(player.getPosition());
						player.changeDimension(respawnWorld);
					}
					player.setLocationAndAngles(respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(), 0, 0);
					player.func_242111_a(respawnWorld.getDimensionKey(), respawnPos, player.func_242109_L(), player.func_241142_M_(), false);
					player.connection.sendPacket(new SRespawnPacket(player.world.getDimensionType(), player.world.getDimensionKey(), BiomeManager.getHashedSeed(player.getServerWorld().getSeed()), player.interactionManager.getGameType(), player.interactionManager.func_241815_c_(), player.getServerWorld().isDebug(), player.getServerWorld().isFlatWorld(), true));
					player.connection.setPlayerLocation(player.getPosX(), player.getPosY(), player.getPosZ(), player.rotationYaw, player.rotationPitch);
					player.connection.sendPacket(new SWorldSpawnChangedPacket(respawnWorld.getSpawnPoint(), respawnWorld.getSpawnAngle()));
					player.connection.sendPacket(new SSetExperiencePacket(player.experience, player.experienceTotal, player.experienceLevel));
					player.sendContainerToPlayer(player.openContainer);
					// cooldown, damage, sound
					SetEffect.RESPAWN.setCooldown(player, 6000);
					SetEffect.RESPAWN.damageArmor(player, 100, true);
					player.connection.sendPacket(new SPlaySoundEffectPacket(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, (double)player.getPosX(), (double)player.getPosY(), (double)player.getPosZ(), 1.0F, 1.0F));
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