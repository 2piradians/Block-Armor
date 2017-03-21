package twopiradians.blockArmor.common.item;

import java.util.List;

import com.google.common.collect.Multimap;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twopiradians.blockArmor.client.model.ModelBlockArmor;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.command.CommandDev;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.seteffect.SetEffect;

public class ItemBlockArmor extends ItemArmor
{
	/**The ArmorSet that this item belongs to*/
	public ArmorSet set;

	public ItemBlockArmor(ArmorMaterial material, int renderIndex, EntityEquipmentSlot equipmentSlot, ArmorSet set) {
		super(material, renderIndex, equipmentSlot);
		this.set = set;
		this.setCreativeTab(null);
	}

	/**Change armor texture based on block*/
	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		TextureAtlasSprite sprite = ArmorSet.getSprite(this);
		String texture = sprite.getIconName()+".png";
		int index = texture.indexOf(":");
		texture = texture.substring(0, index+1)+"textures/"+texture.substring(index+1);
		return texture;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, EntityEquipmentSlot slot, ModelBiped oldModel) {
		TextureAtlasSprite sprite = ArmorSet.getSprite(this);
		int width = sprite.getIconWidth();
		int height = sprite.getIconHeight() * sprite.getFrameCount();
		boolean isTranslucent = set.isTranslucent;
		int currentFrame = ArmorSet.getCurrentAnimationFrame(this);
		int nextFrame = ArmorSet.getNextAnimationFrame(this);
		int color = ArmorSet.getColor(this);
		float alpha = ArmorSet.getAlpha(this);
		//ModelBlockArmor model = new ModelBlockArmor(height, width, currentFrame, nextFrame, slot);
		ModelBlockArmor model =  (ModelBlockArmor) BlockArmor.proxy.getBlockArmorModel(height, width, currentFrame, nextFrame, slot);
		model.translucent = isTranslucent;
		model.color = color;
		model.alpha = alpha;
		return model;
	}

	/**Don't display item in creative tab/JEI if disabled*/
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		if (!set.isEnabled())
			return;

		subItems.add(new ItemStack(itemIn));
	}

	/**Change display name based on the block*/
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return ArmorSet.getItemStackDisplayName(stack, this.armorType);
	}

	/**Handles the attributes when wearing an armor set*/
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		Multimap<String, AttributeModifier> map = this.getItemAttributeModifiers(slot);
		if (slot != this.armorType)
			return map;

		for (SetEffect effect : set.setEffects)
			map = effect.getAttributeModifiers(map, slot, stack);

		return map;
	}

	/**Set to have tooltip color show if item has effect*/
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		if (stack.isItemEnchanted())
			return EnumRarity.RARE;
		else if (!set.setEffects.isEmpty())
			return EnumRarity.UNCOMMON;
		else
			return EnumRarity.COMMON;
	}

	/**Deals with armor tooltips*/
	@Override
	@SideOnly(Side.CLIENT)//TODO do something about disabled set items
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("devSpawned"))
			tooltip.add(TextFormatting.DARK_PURPLE+""+TextFormatting.BOLD+"Dev Spawned");

		if (!set.setEffects.isEmpty()) {
			//add header if shifting
			if (GuiScreen.isShiftKeyDown())
				tooltip.add(TextFormatting.ITALIC+""+TextFormatting.GOLD+"Set Effects: "+TextFormatting.ITALIC+
						"(requires "+Config.piecesForSet+(Config.piecesForSet == 4 ? "" : "+")+" pieces to be worn)");

			//set effect names and descriptions if shifting
			for (SetEffect effect : set.setEffects)
				tooltip = effect.addInformation(stack, GuiScreen.isShiftKeyDown(), player, tooltip, advanced);
		}
	}

	/**Handles enchanting armor when worn*/
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {	
		//delete dev spawned items if not in dev's inventory
		if (!entity.world.isRemote && entity instanceof EntityPlayer && stack.hasTagCompound() &&
				stack.getTagCompound().hasKey("devSpawned") && !CommandDev.DEVS.contains(entity.getPersistentID()) &&
				((EntityPlayer)entity).inventory.getStackInSlot(slot) == stack) {
			((EntityPlayer)entity).inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
			return;
		}

		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		for (SetEffect effect : set.setEffects)
			effect.onUpdate(stack, world, entity, slot, isSelected);
	}

	/**Delete dev spawned dropped items*/
	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (!entityItem.world.isRemote && entityItem != null && entityItem.getEntityItem() != null && 
				entityItem.getEntityItem().hasTagCompound() && entityItem.getEntityItem().getTagCompound().hasKey("devSpawned")) {
			entityItem.setDead();
			return true;
		}
		return false;
	}

	/**Handles most of the armor set special effects and bonuses.*/
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {		
		//delete dev spawned items if not worn by dev
		if (!world.isRemote && stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("devSpawned") && 
				!CommandDev.DEVS.contains(player.getPersistentID()) && 
				player.getItemStackFromSlot(this.getEquipmentSlot()) == stack) {
			player.setItemStackToSlot(this.getEquipmentSlot(), ItemStack.EMPTY);
			return;
		}

		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		for (SetEffect effect : set.setEffects) 
			if (ArmorSet.getWornSetEffects(player).contains(effect))
				effect.onArmorTick(world, player, stack);
	}
}