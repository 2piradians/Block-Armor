package twopiradians.blockArmor.common.seteffect;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.common.item.ArmorSet;

@Mod.EventBusSubscriber
public class SetEffectLucky extends SetEffect {

	protected SetEffectLucky() {
		this.color = TextFormatting.DARK_GREEN;
		this.description = "Greatly increases Fortune, Looting, and Luck";
		this.attributeModifiers.add(new AttributeModifier(LUCK_UUID, 
				SharedMonsterAttributes.LUCK.getName(), 3, 0));
	}
	
	/**Increase looting*/
	@SubscribeEvent
	public void addLooting(LootingLevelEvent event) {
		if (event.getDamageSource().getTrueSource() instanceof EntityPlayer &&
				event.getEntity().world instanceof WorldServer &&
				ArmorSet.getWornSetEffects((EntityLivingBase) event.getDamageSource().getTrueSource()).contains(this)) {
			event.setLootingLevel(event.getLootingLevel()+4);
			doParticlesAndSound((WorldServer) event.getEntity().world, event.getEntity().getPosition(), 
					(EntityPlayer) event.getDamageSource().getTrueSource(), 4);
		}
	}

	/**Increase fortune*/
	@SubscribeEvent
	public void addFortune(HarvestDropsEvent event) {//only server side
		if (ArmorSet.getWornSetEffects(event.getHarvester()).contains(this) &&
				event.getWorld() instanceof WorldServer) {
			List<ItemStack> newDrops = event.getState().getBlock().getDrops(event.getWorld(), 
					event.getPos(), event.getState(), event.getFortuneLevel()+4);
			if (newDrops.size() > event.getDrops().size()) {
				doParticlesAndSound((WorldServer) event.getWorld(), event.getPos(), event.getHarvester(), 
						(newDrops.size()-event.getDrops().size()));
				
				event.getDrops().clear();
				event.getDrops().addAll(newDrops);
				this.damageArmor(event.getHarvester(), 1, false);
			}
		}
	}
	
	/**Spawn particles and make sound*/
	private void doParticlesAndSound(WorldServer world, BlockPos pos, EntityPlayer player, float amplifier) {
		((WorldServer)world).spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, 
				pos.getX()+0.5d, pos.getY()+0.5d, pos.getZ()+0.5d, 5, 0.4f, 0.4f, 0.4f, 0, new int[0]);
		world.playSound(null, player.posX, player.posY, player.posZ, 
				SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 
				0.05f*amplifier, world.rand.nextFloat()+0.9f);
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (SetEffect.registryNameContains(block, meta, new String[] {"emerald", "luck"}))
			return true;		
		return false;
	}
}