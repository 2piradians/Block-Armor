package twopiradians.blockArmor.common;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

public class CommonProxy 
{
	public void preInit() {}
	public void init() {}
	public void postInit() {}
	public Object getBlockArmorModel(int height, int width, int currentFrame, int nextFrame, EntityEquipmentSlot slot) {
		return null;
	}
	public void loadComplete(FMLLoadCompleteEvent event) {}
}
