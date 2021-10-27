package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectSleepy extends SetEffect {

	protected SetEffectSleepy() {
		super();
		this.color = ChatFormatting.WHITE;
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isClientSide && BlockArmor.key.isKeyDown(player) && ArmorSet.getFirstSetItem(player, this) == stack &&
				!player.getCooldowns().isOnCooldown(stack.getItem())) {
			// in nether - use explosive effect
			if (!player.level.dimensionType().bedWorks())
				SetEffectExplosive.tryExplode(this, world, player);
			// sleep
			else if (player.level.isNight() && world instanceof ServerLevel) {
				long l = world.getDayTime() + 24000L;
				SetEffect.TIME_CONTROL.setWorldTime(world, net.minecraftforge.event.ForgeEventFactory.onSleepFinished((ServerLevel) world, l - l % 24000L, world.getDayTime()));
				if (player instanceof ServerPlayer) 
					((ServerPlayer)player).connection.send(new ClientboundCustomSoundPacket(SoundEvents.NOTE_BLOCK_CHIME.getRegistryName(), SoundSource.PLAYERS, player.position(), 0.5F, 1.4f));	
				this.setCooldown(player, 100);
			}
			// not night time
			else if (player instanceof ServerPlayer) {
				((ServerPlayer)player).connection.send(new ClientboundCustomSoundPacket(SoundEvents.NOTE_BLOCK_BASS.getRegistryName(), SoundSource.PLAYERS, player.position(), 1.0F, world.random.nextFloat() + 0.5F));
				this.setCooldown(player, 10);
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"bed", "sleep", "hammock"}) &&
				!SetEffect.registryNameContains(block, new String[] {"bedrock"}))
			return true;		
		return false;
	}
}