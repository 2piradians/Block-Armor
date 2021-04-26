package twopiradians.blockArmor.common.item;

import java.util.HashMap;

import com.google.common.collect.Maps;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;

public class TextureOverrideInfo {

	public HashMap<EquipmentSlotType, Info> overrides = Maps.newHashMap();

	public void addSlot(EquipmentSlotType slot, int color, ResourceLocation loc) {
		this.overrides.put(slot, new Info(color, loc));
	}
	
	public class Info {
		
		public int color;
		public ResourceLocation shortLoc;
		public ResourceLocation longLoc;

		protected Info(int color, ResourceLocation shortLoc) {
			this.color = color;
			this.shortLoc = shortLoc;
			this.longLoc = new ResourceLocation(shortLoc.getNamespace(), "textures/"+shortLoc.getPath()+".png");
		}			
		
	}

}