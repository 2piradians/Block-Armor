package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.block.BlockMovingLightSource;
import twopiradians.blockArmor.common.block.ModBlocks;
import twopiradians.blockArmor.common.item.ArmorSet;

@SuppressWarnings("deprecation")
public class SetEffectIlluminated extends SetEffect {

	private int lightLevel;

	public SetEffectIlluminated(int lightLevel) {
		this.lightLevel = Math.min(lightLevel, 15);
		this.color = TextFormatting.GOLD;
		this.description = "Produces light level "+this.lightLevel;
		this.usesButton = true;
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player) == stack &&
				!world.isRemote && BlockArmor.key.isKeyDown(player) && !player.getCooldownTracker().hasCooldown(stack.getItem())) {
			boolean deactivated = !stack.getTagCompound().getBoolean("deactivated");
			stack.getTagCompound().setBoolean("deactivated", deactivated);
			player.sendMessage(new TextComponentTranslation(TextFormatting.GRAY+"[Block Armor] "+TextFormatting.ITALIC+"Illuminated set effect "
					+ (deactivated ? TextFormatting.RED+""+TextFormatting.ITALIC+"disabled." : TextFormatting.GREEN+""+TextFormatting.ITALIC+"enabled.")));
			this.setCooldown(player, 10);
		}

		//set block at head level to BlockMovingLightSource
		if (ArmorSet.getFirstSetItem(player) == stack &&
				!world.isRemote && world.isAirBlock(player.getPosition().up()) && 
				world.getLightFor(EnumSkyBlock.BLOCK, player.getPosition().up()) < lightLevel &&
				!stack.getTagCompound().getBoolean("deactivated")) 
			world.setBlockState(player.getPosition().up(), 
					ModBlocks.movingLightSource.getDefaultState().withProperty(BlockMovingLightSource.LIGHT_LEVEL, lightLevel));
	}

	/**Can be overwritten to return a new instance depending on the given block*/
	@Override
	protected SetEffect create(Block block) {
		return new SetEffectIlluminated(block.getDefaultState().getLightValue());
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		try {
			int lightLevel = block.getDefaultState().getLightValue();
			if (lightLevel > 0)
				return true;
		} catch (Exception e) {}
		return false;
	}
}