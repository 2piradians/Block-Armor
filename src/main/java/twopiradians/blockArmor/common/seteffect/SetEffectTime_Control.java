package twopiradians.blockArmor.common.seteffect;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import twopiradians.blockArmor.client.ClientProxy;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.CommonProxy;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectTime_Control extends SetEffect {

	private Type type;

	protected SetEffectTime_Control(Type type) {
		super();
		this.type = type;
		this.color = ChatFormatting.LIGHT_PURPLE;
		this.usesButton = true;
	}
	
	@Override
	public TranslatableComponent getDescription() {
		return new TranslatableComponent("setEffect."+this.name.replaceAll(" ", "_").toLowerCase()+"."+type.name.toLowerCase()+".description", this.getDescriptionObjects());
	}

	/**Write this effect to string for config (variables need to be included)*/
	@Override
	public String writeToString() {
		return this.name+" ("+this.type.name+")";
	}

	/**Read an effect from this string in config (takes into account variables in parenthesis)*/
	@Override
	public SetEffect readFromString(String str) throws Exception {
		return new SetEffectTime_Control(Type.getType(str.substring(str.indexOf("(")+1, str.indexOf(")"))));
	}


	/**Only called when player wearing full, enabled set*/
	public void onArmorTick(Level world, Player player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player)) {
			if (type == Type.REWIND) {
				if (world.getLevelData().getDayTime()< 21)
					setWorldTime(world, 23999 + world.getDayTime() - 21);
				else
					setWorldTime(world, world.getDayTime() - 21);
				if (player.tickCount % 4 == 0)
					world.playSound(player, player.blockPosition(), SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 0.3f, 0f);
			}
			else if (type == Type.STOP && world.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
				setWorldTime(world, world.getDayTime() - 1);
				if (player.tickCount % 8 == 0)
				world.playSound(player, player.blockPosition(), SoundEvents.NOTE_BLOCK_SNARE, SoundSource.PLAYERS, 0.2f, 0f);
			}
			else if (type == Type.ACCELERATE) {
				setWorldTime(world, world.getDayTime() + 19);
				if (player.tickCount % 2 == 0)
					world.playSound(player, player.blockPosition(), SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 0.3f, 2f);
			}
		}
	}

	public void setWorldTime(Level world, long time) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {ClientProxy.setWorldTime(world, time);});
		DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {CommonProxy.setWorldTime(world, time);});
	}

	/**Can be overwritten to return a new instance depending on the given block*/
	@Override
	protected SetEffect create(Block block) {
		return new SetEffectTime_Control(Type.getType(block));
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (block == Blocks.REPEATING_COMMAND_BLOCK || block == Blocks.CHAIN_COMMAND_BLOCK ||
				block == Blocks.COMMAND_BLOCK)
			return true;
		return false;
	}

	private enum Type {
		REWIND(Blocks.REPEATING_COMMAND_BLOCK, "Rewind"), 
		STOP(Blocks.CHAIN_COMMAND_BLOCK, "Stop"), 
		ACCELERATE(Blocks.COMMAND_BLOCK, "Accelerate");

		public Block block;
		public String name;

		private Type(Block block, String name) {
			this.block = block;
			this.name = name;
		}

		public static Type getType(Block block) {
			for (Type type : Type.values())
				if (type.block == block)
					return type;
			return Type.ACCELERATE;
		}

		public static Type getType(String str) {
			for (Type type : Type.values())
				if (type.name.equalsIgnoreCase(str))
					return type;
			return Type.ACCELERATE;
		}
	}

}