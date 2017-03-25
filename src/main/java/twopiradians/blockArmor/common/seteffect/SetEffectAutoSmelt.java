package twopiradians.blockArmor.common.seteffect;

import java.util.ListIterator;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

public class SetEffectAutoSmelt extends SetEffect {

	protected SetEffectAutoSmelt() {
		this.color = TextFormatting.DARK_RED;
		this.description = "Smelts harvested blocks";
		this.usesButton = true;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);

		if (ArmorSet.getFirstSetItem(player, this) == stack &&
				!world.isRemote && BlockArmor.key.isKeyDown(player) && !player.getCooldownTracker().hasCooldown(stack.getItem())) {
			boolean deactivated = !stack.getTagCompound().getBoolean("deactivated");
			stack.getTagCompound().setBoolean("deactivated", deactivated);
			player.sendMessage(new TextComponentTranslation(TextFormatting.GRAY+"[Block Armor] "+TextFormatting.ITALIC+"AutoSmelt set effect "
					+ (deactivated ? TextFormatting.RED+""+TextFormatting.ITALIC+"disabled." : TextFormatting.GREEN+""+TextFormatting.ITALIC+"enabled.")));
			this.setCooldown(player, 10);
		}
	}

	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void onEvent(HarvestDropsEvent event) //only server side
	{
		if (ArmorSet.getWornSetEffects(event.getHarvester()).contains(this)) {
			ItemStack stack = ArmorSet.getFirstSetItem(event.getHarvester(), this);
			if (event.getWorld().isRemote || event.isSilkTouching() || 
					!stack.hasTagCompound() || stack.getTagCompound().hasKey("deactivated"))
				return;

			ListIterator<ItemStack> dropsIterator = event.getDrops().listIterator();

			boolean smelted = false;
			while (dropsIterator.hasNext()) {
				ItemStack oldDrops = dropsIterator.next();
				ItemStack newDrops = FurnaceRecipes.instance().getSmeltingResult(oldDrops);

				if (newDrops != null && newDrops.getItem() != null && !(newDrops.getItem() instanceof ItemAir)) {
					newDrops = newDrops.copy();
					event.getDrops().clear();
					event.getDrops().add(newDrops);
					smelted = true;	
				}
			}

			if (smelted) {
				((WorldServer)event.getWorld()).spawnParticle(EnumParticleTypes.SMOKE_NORMAL, 
						(float)event.getPos().getX()+0.5f, (float)event.getPos().getY()+0.5f,(float)event.getPos().getZ()+0.5f, 
						10, 0.3f, 0.3f, 0.3f, 0, new int[0]);
				event.getWorld().playSound(null, event.getHarvester().getPosition(), 
						SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 0.2f, event.getWorld().rand.nextFloat()+0.7f);			
				for (EntityEquipmentSlot slot : ArmorSet.SLOTS) {
					ItemStack armor = event.getHarvester().getItemStackFromSlot(slot);
					if (event.getWorld().rand.nextInt(10) == 0 && armor != null && 
							armor.getItem() instanceof ItemBlockArmor && 
							((ItemBlockArmor)armor.getItem()).set.setEffects.contains(this))
						armor.damageItem(1, event.getHarvester());
				}
			}
		}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (SetEffect.registryNameContains(block, new String[] {"furnace", "fire", "flame", "smelt"}))
			return true;		
		return false;
	}
}