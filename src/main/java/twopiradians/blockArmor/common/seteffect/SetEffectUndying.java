package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.item.ArmorSet;

@Mod.EventBusSubscriber
public class SetEffectUndying extends SetEffect {

	protected SetEffectUndying() {
		super();
		this.color = TextFormatting.DARK_PURPLE;
		this.description = "Saves you from death";
	}
	
	/**Teleport player instead of them dying*/
	@SubscribeEvent
	public static void onEvent(LivingDeathEvent event) {
		try {
			if (event.getEntityLiving() instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
				if (!player.world.isRemote && 
						ArmorSet.hasSetEffect(player, SetEffect.UNDYING) && 
						!player.getCooldownTracker().hasCooldown(ArmorSet.getFirstSetItem(player, SetEffect.UNDYING).getItem())) {
					// set health to 1 and clear effects
					player.setHealth(1);
					player.clearActivePotions();
					player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 900, 1));
					player.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 100, 1));
					player.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 800, 0));
					player.world.setEntityState(player, (byte)35);
					// cooldown, damage, sound
					SetEffect.UNDYING.setCooldown(player, 6000);
					SetEffect.UNDYING.damageArmor(player, 100, true);
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
		if (SetEffect.registryNameContains(block, new String[] {"netherite", "ancient_debris"}))
			return true;
		return false;
	}
	
}