package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
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
		this.description = type == null ? "" : type.description;
		this.color = TextFormatting.LIGHT_PURPLE;
		this.usesButton = true;
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
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack &&
				BlockArmor.key.isKeyDown(player)) {
			if (type == Type.REWIND)
				if (world.getWorldInfo().getDayTime()< 21)
					setWorldTime(world, 23999 + world.getDayTime() - 21);
				else
					setWorldTime(world, world.getDayTime() - 21);
			else if (type == Type.STOP && world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE))
				setWorldTime(world, world.getDayTime() - 1);
			else if (type == Type.ACCELERATE)
				setWorldTime(world, world.getDayTime() + 19);
		}
	}
	
	private void setWorldTime(World world, long time) {
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> {ClientProxy.setWorldTime(world, time);});
		DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {CommonProxy.setWorldTime(world, time);});
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
		REWIND(Blocks.REPEATING_COMMAND_BLOCK, "Rewind", "Rewinds time"), 
		STOP(Blocks.CHAIN_COMMAND_BLOCK, "Stop", "Stops Time"), 
		ACCELERATE(Blocks.COMMAND_BLOCK, "Accelerate", "Accelerates time");
		
		public Block block;
		public String name;
		public String description;

		private Type(Block block, String name, String description) {
			this.block = block;
			this.name = name;
			this.description = description;
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