package twopiradians.blockArmor.client.renderer.entity.layers;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import twopiradians.blockArmor.client.model.x3d.IExtendedModelPart;
import twopiradians.blockArmor.client.model.x3d.Vector4;
import twopiradians.blockArmor.client.model.x3d.X3dModel;
import twopiradians.blockArmor.common.item.ArmorSet;
import twopiradians.blockArmor.common.item.ItemBlockArmor;

public class oldLayerBlockArmor extends LayerBipedArmor
{
	X3dModel chest;
	X3dModel helmet;
	X3dModel leftFoot;
	X3dModel leftLeg;
	X3dModel leftShoulder;
	X3dModel rightFoot;
	X3dModel rightLeg;
	X3dModel rightShoulder;
	X3dModel waist;

	private final RenderLivingBase<?> livingEntityRenderer;

	public oldLayerBlockArmor(RenderLivingBase<?> renderer)
	{
		super(renderer);

		this.livingEntityRenderer = renderer;
		chest = new X3dModel(new ResourceLocation("blockarmor:models/x3d/Chest.x3d"));
		helmet = new X3dModel(new ResourceLocation("blockarmor:models/x3d/Helmet.x3d"));
		leftFoot = new X3dModel(new ResourceLocation("blockarmor:models/x3d/LeftFoot.x3d"));
		leftLeg = new X3dModel(new ResourceLocation("blockarmor:models/x3d/LeftLeg.x3d"));
		leftShoulder = new X3dModel(new ResourceLocation("blockarmor:models/x3d/LeftShoulder.x3d"));
		rightFoot = new X3dModel(new ResourceLocation("blockarmor:models/x3d/RightFoot.x3d"));
		rightLeg = new X3dModel(new ResourceLocation("blockarmor:models/x3d/RightLeg.x3d"));
		rightShoulder = new X3dModel(new ResourceLocation("blockarmor:models/x3d/RightShoulder.x3d"));
		waist = new X3dModel(new ResourceLocation("blockarmor:models/x3d/Waist.x3d"));
	}


	@Override
	public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		/*for (ItemStack armor : entity.getArmorInventoryList()) 
			if (armor != null && armor.getItem() instanceof ItemBlockArmor) {
				ItemBlockArmor item = (ItemBlockArmor) armor.getItem();
				ModelBiped model = this.getModelFromSlot(item.getEquipmentSlot());
				model = getArmorModelHook(entity, armor, item.getEquipmentSlot(), model);
				model.setModelAttributes(this.livingEntityRenderer.getMainModel());
				model.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
				this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
				this.renderArmor(item);
				renderEnchantedGlint(this.livingEntityRenderer, entity, model, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
			}*/
	}

	private void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale, EntityLivingBase entityIn) {
		boolean flag = entityIn instanceof EntityLivingBase && ((EntityLivingBase)entityIn).getTicksElytraFlying() > 4;
		// this.bipedHead.rotateAngleY = netHeadYaw * 0.017453292F;
		for (IExtendedModelPart part : this.helmet.getParts().values())
			part.setPostRotations(new Vector4(0, netHeadYaw * 0.017453292F, 0, 0));

		if (flag)
		{
			this.helmet.getParts().get(null).setPostRotations(new Vector4(-((float)Math.PI / 4F), 0, 0, 0));
			//this.bipedHead.rotateAngleX = -((float)Math.PI / 4F);
		}
		else
		{
			//this.bipedHead.rotateAngleX = headPitch * 0.017453292F;
		}

		//this.bipedBody.rotateAngleY = 0.0F;
		//this.bipedRightArm.rotationPointZ = 0.0F;
		//this.bipedRightArm.rotationPointX = -5.0F;
		//this.bipedLeftArm.rotationPointZ = 0.0F;
		//this.bipedLeftArm.rotationPointX = 5.0F;
		float f = 1.0F;

		if (flag)
		{
			f = (float)(entityIn.motionX * entityIn.motionX + entityIn.motionY * entityIn.motionY + entityIn.motionZ * entityIn.motionZ);
			f = f / 0.2F;
			f = f * f * f;
		}

		if (f < 1.0F)
		{
			f = 1.0F;
		}

		//this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
		//this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
		//this.bipedRightArm.rotateAngleZ = 0.0F;
		//this.bipedLeftArm.rotateAngleZ = 0.0F;
		//this.bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
		//this.bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount / f;
		//this.bipedRightLeg.rotateAngleY = 0.0F;
		//this.bipedLeftLeg.rotateAngleY = 0.0F;
		//this.bipedRightLeg.rotateAngleZ = 0.0F;
		//this.bipedLeftLeg.rotateAngleZ = 0.0F;

		/*if (this.isRiding)
        {
            this.bipedRightArm.rotateAngleX += -((float)Math.PI / 5F);
            this.bipedLeftArm.rotateAngleX += -((float)Math.PI / 5F);
            this.bipedRightLeg.rotateAngleX = -1.4137167F;
            this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 10F);
            this.bipedRightLeg.rotateAngleZ = 0.07853982F;
            this.bipedLeftLeg.rotateAngleX = -1.4137167F;
            this.bipedLeftLeg.rotateAngleY = -((float)Math.PI / 10F);
            this.bipedLeftLeg.rotateAngleZ = -0.07853982F;
        }*/

		//this.bipedRightArm.rotateAngleY = 0.0F;
		//this.bipedRightArm.rotateAngleZ = 0.0F;

		/*switch (this.leftArmPose)
        {
            case EMPTY:
                this.bipedLeftArm.rotateAngleY = 0.0F;
                break;
            case BLOCK:
                this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.9424779F;
                this.bipedLeftArm.rotateAngleY = 0.5235988F;
                break;
            case ITEM:
                this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
                this.bipedLeftArm.rotateAngleY = 0.0F;
        }

        switch (this.rightArmPose)
        {
            case EMPTY:
                this.bipedRightArm.rotateAngleY = 0.0F;
                break;
            case BLOCK:
                this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.9424779F;
                this.bipedRightArm.rotateAngleY = -0.5235988F;
                break;
            case ITEM:
                this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
                this.bipedRightArm.rotateAngleY = 0.0F;
        }*/

		/*if (this.swingProgress > 0.0F)
        {
            EnumHandSide enumhandside = this.getMainHand(entityIn);
            ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
            float f1 = this.swingProgress;
            this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f1) * ((float)Math.PI * 2F)) * 0.2F;

            if (enumhandside == EnumHandSide.LEFT)
            {
                this.bipedBody.rotateAngleY *= -1.0F;
            }

            this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
            this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
            this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;
            f1 = 1.0F - this.swingProgress;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = MathHelper.sin(f1 * (float)Math.PI);
            float f3 = MathHelper.sin(this.swingProgress * (float)Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
            modelrenderer.rotateAngleX = (float)((double)modelrenderer.rotateAngleX - ((double)f2 * 1.2D + (double)f3));
            modelrenderer.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
            modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress * (float)Math.PI) * -0.4F;
        }*/

		/*if (this.isSneak)
        {
            this.bipedBody.rotateAngleX = 0.5F;
            this.bipedRightArm.rotateAngleX += 0.4F;
            this.bipedLeftArm.rotateAngleX += 0.4F;
            this.bipedRightLeg.rotationPointZ = 4.0F;
            this.bipedLeftLeg.rotationPointZ = 4.0F;
            this.bipedRightLeg.rotationPointY = 9.0F;
            this.bipedLeftLeg.rotationPointY = 9.0F;
            this.bipedHead.rotationPointY = 1.0F;
        }
        else
        {
            this.bipedBody.rotateAngleX = 0.0F;
            this.bipedRightLeg.rotationPointZ = 0.1F;
            this.bipedLeftLeg.rotationPointZ = 0.1F;
            this.bipedRightLeg.rotationPointY = 12.0F;
            this.bipedLeftLeg.rotationPointY = 12.0F;
            this.bipedHead.rotationPointY = 0.0F;
        }

        this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;

        if (this.rightArmPose == ModelBiped.ArmPose.BOW_AND_ARROW)
        {
            this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
            this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
            this.bipedRightArm.rotateAngleX = -((float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = -((float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
        }
        else if (this.leftArmPose == ModelBiped.ArmPose.BOW_AND_ARROW)
        {
            this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY - 0.4F;
            this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY;
            this.bipedRightArm.rotateAngleX = -((float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = -((float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
        }

        this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;
        this.bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY;
        this.bipedHeadwear.rotateAngleZ = this.bipedHead.rotateAngleZ;
        this.bipedHeadwear.rotationPointX = this.bipedHead.rotationPointX;
        this.bipedHeadwear.rotationPointY = this.bipedHead.rotationPointY;
        this.bipedHeadwear.rotationPointZ = this.bipedHead.rotationPointZ;*/
	}


	private void renderArmor(ItemBlockArmor item) {
		EntityEquipmentSlot slot = item.getEquipmentSlot();
		ResourceLocation[] armorTextures = ArmorSet.getSet(item).armorTextures;
		ResourceLocation helmetTexture = armorTextures[EnumFacing.UP.getIndex()];
		ResourceLocation chestTexture = armorTextures[EnumFacing.NORTH.getIndex()];
		ResourceLocation legTexture = armorTextures[EnumFacing.SOUTH.getIndex()];
		ResourceLocation bootTexture = armorTextures[EnumFacing.DOWN.getIndex()];

		if (slot == EntityEquipmentSlot.HEAD) {
			// First pass of render 
			GL11.glPushMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableRescaleNormal();
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
					GlStateManager.DestFactor.ZERO);
			GlStateManager.pushMatrix();
			GL11.glPushMatrix();
			float dx = 0, dy = -.0f, dz = -0.6f;
			GL11.glRotated(180, 0, 0, 0);
			GL11.glTranslatef(dx, dy + 0.3f, dz + 0.6f);
			float s = 1.0f;
			GL11.glScalef(s, s, s);
			this.livingEntityRenderer.bindTexture(helmetTexture);
			helmet.renderAll();
			GL11.glPopMatrix();
			GlStateManager.cullFace(GlStateManager.CullFace.BACK);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			GL11.glPopMatrix();
		}
		else if (slot == EntityEquipmentSlot.CHEST) {
			// First pass of render
			GL11.glPushMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableRescaleNormal();
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
					GlStateManager.DestFactor.ZERO);
			GlStateManager.pushMatrix();
			GL11.glPushMatrix();
			float dx = 0, dy = -.0f, dz = -0.6f;
			GL11.glRotated(180, 0, 0, 0);
			GL11.glTranslatef(dx, dy - 0.35f, dz + 0.6f);
			float s = 0.8f;
			GL11.glScalef(s, s, s);
			this.livingEntityRenderer.bindTexture(chestTexture);
			chest.renderAll();
			GL11.glPopMatrix();
			// Second pass with colour.
			GL11.glPushMatrix();
			GL11.glRotated(180, 1, 0, 0);
			GL11.glTranslatef(dx, dy - 0.25f, dz + 0.6f);
			GL11.glScalef(1.2f*s, 1.2f*s, 1.2f*s);
			this.livingEntityRenderer.bindTexture(legTexture);
			leftShoulder.renderAll();
			GL11.glColor3f(1, 1, 1);
			GL11.glPopMatrix();
			// Third pass with colour.
			GL11.glPushMatrix();
			GL11.glRotated(180, 1, 0, 0);
			GL11.glTranslatef(dx, dy - 0.25f, dz + 0.6f);
			GL11.glScalef(1.2f*s, 1.2f*s, 1.2f*s);
			this.livingEntityRenderer.bindTexture(legTexture);
			rightShoulder.renderAll();
			GL11.glColor3f(1, 1, 1);
			GL11.glPopMatrix();
			GlStateManager.cullFace(GlStateManager.CullFace.BACK);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			GL11.glPopMatrix();
		}
		else if (slot == EntityEquipmentSlot.LEGS) {
			// First pass of render
			GL11.glPushMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableRescaleNormal();
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
					GlStateManager.DestFactor.ZERO);
			GlStateManager.pushMatrix();
			GL11.glPushMatrix();
			float dx = 0, dy = -.0f, dz = 0f;
			GL11.glRotated(180, 0, 0, 0);
			GL11.glTranslatef(dx, dy - 0.9f, dz);
			float s = 1.0f;
			GL11.glScalef(1.01f*s, 1.01f*s, 1.01f*s);
			this.livingEntityRenderer.bindTexture(legTexture);
			waist.renderAll();
			GL11.glPopMatrix();
			// Second pass
			GL11.glPushMatrix();
			GL11.glRotated(180, 0, 0, 0);
			GL11.glTranslatef(dx, dy - 1.05f, dz);
			GL11.glScalef(s, s, s);
			this.livingEntityRenderer.bindTexture(legTexture);
			leftLeg.renderAll();
			GL11.glColor3f(1, 1, 1);
			GL11.glPopMatrix();
			// Third pass
			GL11.glPushMatrix();
			GL11.glRotated(180, 0, 0, 0);
			GL11.glTranslatef(dx, dy - 0.82f, dz);
			GL11.glScalef(s, s, 1.005f*s);
			this.livingEntityRenderer.bindTexture(legTexture);
			rightLeg.renderAll();
			GL11.glColor3f(1, 1, 1);
			GL11.glPopMatrix();
			GlStateManager.cullFace(GlStateManager.CullFace.BACK);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			GL11.glPopMatrix();
		}
		else if (slot == EntityEquipmentSlot.FEET) {
			// First pass of render
			GL11.glPushMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableRescaleNormal();
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
					GlStateManager.DestFactor.ZERO);
			GlStateManager.pushMatrix();
			GL11.glPushMatrix();
			float dx = 0, dy = -.0f, dz = 0f;
			GL11.glRotated(180, 0, 0, 0);
			GL11.glTranslatef(dx, dy - 1.55f, dz);
			float s = 1.0f;
			GL11.glScalef(s, s, s);
			this.livingEntityRenderer.bindTexture(bootTexture);
			leftFoot.renderAll();
			GL11.glPopMatrix();
			// Second pass
			GL11.glPushMatrix();
			GL11.glRotated(180, 0, 0, 0);
			GL11.glTranslatef(dx, dy - 1.55f, dz);
			GL11.glScalef(s, s, 1.01f*s);
			this.livingEntityRenderer.bindTexture(bootTexture);
			rightFoot.renderAll();
			GL11.glColor3f(1, 1, 1);
			GL11.glPopMatrix();
			GlStateManager.cullFace(GlStateManager.CullFace.BACK);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			GL11.glPopMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures()
	{
		return false;
	}
}