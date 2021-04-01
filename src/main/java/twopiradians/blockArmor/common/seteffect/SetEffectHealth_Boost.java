package twopiradians.blockArmor.common.seteffect;

import java.util.HashSet;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.utils.BlockUtils;

@Mod.EventBusSubscriber
public class SetEffectHealth_Boost extends SetEffect {

	private static HashSet<ServerPlayerEntity> playersToCheck = Sets.newHashSet();

	private double healthBoost;

	protected SetEffectHealth_Boost(double healthBoost) {
		super();
		this.healthBoost = healthBoost;
		this.color = TextFormatting.RED;
		this.description = "Increases Max Health by "+(int)healthBoost/2+" hearts";
		this.attributes.put(Attributes.MAX_HEALTH, new AttributeModifier(MAX_HEALTH_UUID, 
				"Max Health", healthBoost, AttributeModifier.Operation.ADDITION));
	}

	/**Only called when player wearing full, enabled set*/
	@Override
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack) {
		if (!world.isRemote && playersToCheck.contains(player)) {
			System.out.println("health: "+player.getHealth()+", max health: "+player.getMaxHealth()); // TODO remove
			// if max health already, restore health from this effect
			if (player.getHealth() >= 20f) {
				float amount = 0;
				for (SetEffect effect : ArmorSet.getWornSetEffects(player))
					if (effect instanceof SetEffectHealth_Boost)
						amount += ((SetEffectHealth_Boost)effect).healthBoost;
				player.setHealth(player.getHealth()+amount);
			}
			playersToCheck.remove(player);
		}
	}

	/**Restore health when player logging in with health armor*/
	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {		
		if (!event.getPlayer().world.isRemote && event.getPlayer() instanceof ServerPlayerEntity)
			playersToCheck.add((ServerPlayerEntity) event.getPlayer());
	}
	
	/**Restore health when player respawned with health armor on*/
	@SubscribeEvent
	public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {		
		if (!event.getPlayer().world.isRemote && event.getPlayer() instanceof ServerPlayerEntity)
			playersToCheck.add((ServerPlayerEntity) event.getPlayer());
	}

	/**Can be overwritten to return a new instance depending on the given block*/
	@Override
	protected SetEffect create(Block block) {
		double healthBoost = BlockUtils.getHardness(block);
		if (healthBoost == -1 || healthBoost >= 100) //if unbreakable
			healthBoost = 100;
		healthBoost = Math.max(healthBoost*0.4d, 4);
		return new SetEffectHealth_Boost(healthBoost);
	}

	/**Write this effect to string for config (variables need to be included)*/
	@Override
	public String writeToString() {
		return this.name+" ("+this.healthBoost+")";
	}

	/**Read an effect from this string in config (takes into account variables in parenthesis)*/
	@Override
	public SetEffect readFromString(String str) throws Exception {
		return new SetEffectHealth_Boost(Double.valueOf(str.substring(str.indexOf("(")+1, str.indexOf(")"))));
	}

	/**Should block be given this set effect*/
	@Override
	protected boolean isValid(Block block) {		
		if (SetEffect.registryNameContains(block, new String[] {"bedrock", "obsidian", "brick", "heal", "heart"})) {
			float hardness = BlockUtils.getHardness(block);
			if (hardness == -1 || hardness >= 2)
				return true;	
		}
		return false;
	}

}