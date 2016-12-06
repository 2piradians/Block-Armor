package twopiradians.blockArmor.client.renderer.entity.layers;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twopiradians.blockArmor.client.model.ModelBlockArmor;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

@SideOnly(Side.CLIENT)
public class LayerBlockArmor extends LayerArmorBase<ModelBiped>
{
	private final RenderLivingBase<?> renderer;
	private float alpha = 1.0F;
	private float colorR = 1.0F;
	private float colorG = 1.0F;
	private float colorB = 1.0F;

	public LayerBlockArmor(RenderLivingBase<?> rendererIn)
	{
		super(rendererIn);
		this.renderer = rendererIn;
	}

	@Override
	protected void initArmor()
	{
		this.modelLeggings = new ModelBlockArmor(0.5F);
		this.modelArmor = new ModelBlockArmor(1.0F);
	}

	@Override
	@SuppressWarnings("incomplete-switch")
	protected void setModelSlotVisible(ModelBiped model, EntityEquipmentSlot slotIn)
	{
		model.setInvisible(false);

		switch (slotIn)
		{
		case HEAD:
			model.bipedHead.showModel = true;
			model.bipedHeadwear.showModel = true;
			break;
		case CHEST:
			model.bipedBody.showModel = true;
			model.bipedRightArm.showModel = true;
			model.bipedLeftArm.showModel = true;
			break;
		case LEGS:
			model.bipedBody.showModel = true;
			model.bipedRightLeg.showModel = true;
			model.bipedLeftLeg.showModel = true;
			break;
		case FEET:
			model.bipedRightLeg.showModel = true;
			model.bipedLeftLeg.showModel = true;
		}
	}

	@Override
	protected ModelBiped getArmorModelHook(EntityLivingBase entity, ItemStack itemStack, EntityEquipmentSlot slot, ModelBiped model)
	{
		return ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
	}

	//copied entirely from LayerArmorBase
	@Override
	public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.CHEST);
		this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.LEGS);
		this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.FEET);
		this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.HEAD);
	}

	//copied entirely (except for commented line) from LayerArmorBase
	private void renderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn)
	{
		ItemStack itemstack = this.getItemStackFromSlot(entityLivingBaseIn, slotIn);

		if (itemstack != null && itemstack.getItem() instanceof ItemBlockArmor) //changed ItemArmor to ItemBlockArmor
		{
			ItemArmor itemarmor = (ItemArmor)itemstack.getItem();

			if (itemarmor.getEquipmentSlot() == slotIn)
			{
				ModelBiped t = this.getModelFromSlot(slotIn);
				t = getArmorModelHook(entityLivingBaseIn, itemstack, slotIn, t);
				t.setModelAttributes(this.renderer.getMainModel());
				t.setLivingAnimations(entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks);
				this.setModelSlotVisible(t, slotIn);
				this.renderer.bindTexture(this.getArmorResource(entityLivingBaseIn, itemstack, slotIn, null));

				if (itemarmor.hasOverlay(itemstack)) // Allow this for anything, not only cloth
				{
					int i = itemarmor.getColor(itemstack);
					float f = (float)(i >> 16 & 255) / 255.0F;
					float f1 = (float)(i >> 8 & 255) / 255.0F;
					float f2 = (float)(i & 255) / 255.0F;
					GlStateManager.color(this.colorR * f, this.colorG * f1, this.colorB * f2, this.alpha);
					t.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
					this.renderer.bindTexture(this.getArmorResource(entityLivingBaseIn, itemstack, slotIn, "overlay"));
				}
				// Non-colored
				GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
				t.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
				// Default
				if (itemstack.hasEffect())
				{
					renderEnchantedGlint(this.renderer, entityLivingBaseIn, t, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
				}
			}
		}
	}
}