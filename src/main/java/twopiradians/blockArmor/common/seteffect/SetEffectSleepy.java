package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectSleepy extends SetEffect {

	protected SetEffectSleepy() {
		super();
		this.color = TextFormatting.WHITE;
		this.description = "Sleep anywhere instantly";
		this.usesButton = true;
	}

	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (!world.isRemote && BlockArmor.key.isKeyDown(player) && ArmorSet.getFirstSetItem(player, this) == stack &&
				!player.getCooldownTracker().hasCooldown(stack.getItem())) {
			// in nether - use explosive effect
			if (!player.world.getDimensionType().doesBedWork())
				SetEffectExplosive.tryExplode(this, world, player);
			// sleep
			else if (player.world.isNightTime() && world instanceof ServerWorld) {
				long l = world.getDayTime() + 24000L;
				SetEffect.TIME_CONTROL.setWorldTime(world, net.minecraftforge.event.ForgeEventFactory.onSleepFinished((ServerWorld) world, l - l % 24000L, world.getDayTime()));
				if (player instanceof ServerPlayerEntity) 
					((ServerPlayerEntity)player).connection.sendPacket(new SPlaySoundPacket(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.getRegistryName(), SoundCategory.PLAYERS, player.getPositionVec(), 0.5F, 1.4f));	
				this.setCooldown(player, 100);
			}
			// not night time
			else if (player instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)player).connection.sendPacket(new SPlaySoundPacket(SoundEvents.BLOCK_NOTE_BLOCK_BASS.getRegistryName(), SoundCategory.PLAYERS, player.getPositionVec(), 1.0F, world.rand.nextFloat() + 0.5F));
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