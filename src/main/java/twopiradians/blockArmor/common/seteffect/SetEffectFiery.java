package twopiradians.blockArmor.common.seteffect;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import twopiradians.blockArmor.common.item.ArmorSet;

@Mod.EventBusSubscriber
public class SetEffectFiery extends SetEffect {

	protected SetEffectFiery() {
		this.color = TextFormatting.RED;
		this.description = "Ignites enemies after attacking or being attacked";
	}

	/**Ignites attackers/attackees*/
	@SubscribeEvent
	public void onEvent(LivingAttackEvent event) {
		if (this.isEnabled() && event.getSource().getTrueSource() instanceof EntityLivingBase
				&& !event.getSource().getTrueSource().world.isRemote) {
			EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
			EntityLivingBase attacked = event.getEntityLiving();

			//Lights the entity that attacks the wearer of the armor
			if (ArmorSet.getWornSetEffects(attacked).contains(this) && !attacker.isInWater())	{
				if (!attacker.isBurning())
					attacker.world.playSound(null, attacker.posX, 
							attacker.posY, attacker.posZ, SoundEvents.ITEM_FIRECHARGE_USE, 
							SoundCategory.PLAYERS, 1.0f, attacker.world.rand.nextFloat());
				attacker.setFire(5);
			}
			//Lights the target of the wearer when the wearer attacks
			if (ArmorSet.getWornSetEffects(attacker).contains(this) && !attacked.isInWater())	{
				if (!attacked.isBurning())
					attacker.world.playSound(null, attacked.posX, attacked.posY, attacked.posZ, 
							SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0f, attacker.world.rand.nextFloat());
				attacked.setFire(5);
			}
		}
	}
	
	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block, int meta) {		
		if (SetEffect.registryNameContains(block, meta, new String[] {"netherrack", "magma", "fire", "flame", "lava"}))
			return true;		
		return false;
	}
}