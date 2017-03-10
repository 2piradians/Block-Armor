package twopiradians.blockArmor.common.seteffect;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.common.item.ArmorSet;

public class SetEffectLucky extends SetEffect {

	protected SetEffectLucky() {
		this.color = TextFormatting.DARK_GREEN;
		this.description = "Increases Fortune and Luck";
		this.attributeModifiers.add(new AttributeModifier(LUCK_UUID, 
				SharedMonsterAttributes.LUCK.getName(), 1, 0));
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**Increase fortune, spawn particles, and play sound*/
	@SubscribeEvent
	public void onEvent(HarvestDropsEvent event) {//only server side
		ArrayList<ArmorSet> sets = ArmorSet.getActiveSets(event.getHarvester());
		for (ArmorSet set : sets)
			if (ArmorSet.isSetEffectEnabled(set) && set.setEffects.contains(this)) {
				List<ItemStack> newDrops = event.getState().getBlock().getDrops(event.getWorld(), 
						event.getPos(), event.getState(), event.getFortuneLevel()+2);
				if (newDrops.size() > event.getDrops().size()) {
					((WorldServer)event.getWorld()).spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, 
							(float)event.getPos().getX()+0.5f, (float)event.getPos().getY()+0.5f,(float)event.getPos().getZ()+0.5f, 
							5, 0.4f, 0.4f, 0.4f, 0, new int[0]);
					event.getWorld().playSound(null, event.getHarvester().posX, event.getHarvester().posY, event.getHarvester().posZ, 
							SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 
							0.05f*(newDrops.size()-event.getDrops().size()), event.getWorld().rand.nextFloat()+0.9f);
				}
				event.getDrops().clear();
				event.getDrops().addAll(newDrops);
				break;
			}
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (SetEffect.registryNameContains(block, new String[] {"emerald", "luck"}))
			return true;		
		return false;
	}
}