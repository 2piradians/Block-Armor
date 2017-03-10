package twopiradians.blockArmor.common.seteffect;

import java.util.ListIterator;

import net.minecraft.block.Block;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

public class SetEffectAutoSmelt extends SetEffect {

	protected SetEffectAutoSmelt() {
		this.color = TextFormatting.DARK_RED;
		this.description = "Smelts harvested blocks";
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onEvent(HarvestDropsEvent event) //only server side
	{
		ArmorSet set = ArmorSet.getWornSet(event.getHarvester());
		if (ArmorSet.isSetEffectEnabled(set) && set.setEffects.contains(this)) {
			if (event.getWorld().isRemote || event.isSilkTouching())
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
				if (event.getWorld().rand.nextInt(4) == 0) 
					for (EntityEquipmentSlot slot : ArmorSet.SLOTS) {
						ItemStack armor = event.getHarvester().getItemStackFromSlot(slot);
						if (armor != null && armor.getItem() instanceof ItemBlockArmor && 
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