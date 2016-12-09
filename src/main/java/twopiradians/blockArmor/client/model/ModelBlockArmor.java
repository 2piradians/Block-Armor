package twopiradians.blockArmor.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBlockArmor extends ModelBiped
{
	public boolean translucent;

	public ModelBlockArmor(int textureHeight, int textureWidth, boolean isTranslucent, int frame)
	{		
		int size = Math.max(1, textureWidth / 16);
		this.textureHeight = textureHeight / size;
		this.textureWidth = textureWidth / size;
		this.translucent = isTranslucent;
		int yOffset = this.textureWidth * frame;

		this.leftArmPose = ModelBlockArmor.ArmPose.EMPTY;
		this.rightArmPose = ModelBlockArmor.ArmPose.EMPTY;

		//HELMET
		this.bipedHeadwear = new ModelRenderer(this, 0, 0);
		this.bipedHead = new ModelRenderer(this, 0, 0);
		this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);

		this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 9, 3+yOffset, -5.0f, -9.0f, -5.0f, 10, 0, 10, false)); //top
		this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 9, 3+yOffset, -5.0f, -9.0f, 5.0f, 10, 8, 0, true)); //back
		this.bipedHead.cubeList.add(new ModelPlane(bipedHead, -1, 11+yOffset, -3.0f, -1.0f, 5.0f, 6, 1, 0, true)); //back bottom
		this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 0, 6+yOffset, -5.0f, -9.0f, -5.0f, 0, 5, 10, false)); //right
		this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 0, 0+yOffset, -5.0f, -4.0f, -0.0f, 0, 1, 5, false)); //right bottom
		this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 12, -3+yOffset, 5.0f, -9.0f, -5.0f, 0, 5, 10, true)); //left
		this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 6, 0+yOffset, 5.0f, -4.0f, -0.0f, 0, 1, 5, true)); //left bottom
		this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 3, 0+yOffset, -5.0f, -9.0f, -5.0f, 10, 4, 0, false)); //front
		this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 0, 4+yOffset, -5.0F, -5.0F, -5.0F, 1, 1, 0, false)); //front left
		this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 7, 4+yOffset, -1.0F, -5.0F, -5.0F, 2, 2, 0, false)); //front mid
		this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 12, 4+yOffset, 4.0F, -5.0F, -5.0F, 1, 1, 0, false)); //front right

		//CHESTPLATE
		this.bipedBody = new ModelRenderer(this, 0, 0);
		this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);

		this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 3, 3+yOffset, -5.0F, 1.0F, -2.5F, 10, 8, 0, false)); //front
		this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 4, 11+yOffset, -4.0F, 9.0F, -2.5F, 8, 1, 0, false)); //front bottom mid
		this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 5, 12+yOffset, -3.0F, 10.0F, -2.5F, 6, 1, 0, false)); //front bottom
		this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 3, 1+yOffset, -5.0F, -1.0F, -2.5F, 2, 2, 0, false)); //front top right 2x2
		this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 5, 1+yOffset, -3.0F, -0.0F, -2.5F, 1, 1, 0, false)); //front top right 1x1
		this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 11, 1+yOffset, 3.0F, -1.0F, -2.5F, 2, 2, 0, false)); //front top left 2x2
		this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 10, 1+yOffset, 2.0F, -0.0F, -2.5F, 1, 1, 0, false)); //front top left 1x1
		this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 9, 3+yOffset, -5.0F, -1.0F, -2.5F, 0, 10, 5, true)); //right 
		this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 9, 3+yOffset, 5.0F, -1.0F, -2.5F, 0, 10, 5, false)); //left 
		this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 9, 3+yOffset, -5.0F, 0.0F, 2.5F, 10, 9, 0, true)); //back 
		this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 9, 3+yOffset, -5.0F, -1.0F, 2.5F, 10, 1, 0, true)); //back top right 
		this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 9, 3+yOffset, 3.0F, -1.0F, 2.5F, 2, 1, 0, true)); //back top left 
		this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 9, 3+yOffset, -4.0F, 9.0F, 2.5F, 8, 1, 0, true)); //back bottom

		//RIGHT ARM
		this.bipedRightArm = new ModelRenderer(this, 0, 0);
		this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);

		this.bipedRightArm.cubeList.add(new ModelPlane(bipedRightArm, 9, 3+yOffset, -3.5F, -3.0F, -2.5F, 0, 5, 5, true)); //right
		this.bipedRightArm.cubeList.add(new ModelPlane(bipedRightArm, 9, 3+yOffset, 1.5F, -3.0F, -2.5F, 0, 5, 5, true)); //left
		this.bipedRightArm.cubeList.add(new ModelPlane(bipedRightArm, 9, 3+yOffset, -3.5F, -3.0F, -2.5F, 5, 5, 0, false)); //front
		this.bipedRightArm.cubeList.add(new ModelPlane(bipedRightArm, 9, 3+yOffset, -3.5F, -3.0F, 2.5F, 5, 5, 0, true)); //back
		this.bipedRightArm.cubeList.add(new ModelPlane(bipedRightArm, 9, 3+yOffset, -3.5F, -3.0F, -2.5F, 5, 0, 5, false)); //top

		//LEFT ARM
		this.bipedLeftArm = new ModelRenderer(this, 0, 0);
		this.bipedLeftArm.mirror = true;
		this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);

		this.bipedLeftArm.cubeList.add(new ModelPlane(bipedLeftArm, 9, 3+yOffset, -1.5F, -3.0F, -2.5F, 0, 5, 5, false)); //right
		this.bipedLeftArm.cubeList.add(new ModelPlane(bipedLeftArm, 9, 3+yOffset, 3.5F, -3.0F, -2.5F, 0, 5, 5, false)); //left
		this.bipedLeftArm.cubeList.add(new ModelPlane(bipedLeftArm, 9, 3+yOffset, -1.5F, -3.0F, -2.5F, 5, 5, 0, false)); //front
		this.bipedLeftArm.cubeList.add(new ModelPlane(bipedLeftArm, 9, 3+yOffset, -1.5F, -3.0F, 2.5F, 5, 5, 0, true)); //back
		this.bipedLeftArm.cubeList.add(new ModelPlane(bipedLeftArm, 9, 3+yOffset, -1.5F, -3.0F, -2.5F, 5, 0, 5, false)); //top

		//RIGHT LEG
		this.bipedRightLeg = new ModelRenderer(this, 0, 0);
		this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);

		//LEFT LEG
		this.bipedLeftLeg = new ModelRenderer(this, 0, 0);
		this.bipedLeftLeg.mirror = true;
		this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

		GlStateManager.pushMatrix();

		if (this.translucent) 
			GlStateManager.enableBlend(); //enables transparency

		if (this.isChild)
		{
			GlStateManager.scale(0.75F, 0.75F, 0.75F);
			GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
			this.bipedHead.render(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
			this.bipedBody.render(scale);
			this.bipedRightArm.render(scale);
			this.bipedLeftArm.render(scale);
			this.bipedRightLeg.render(scale);
			this.bipedLeftLeg.render(scale);
			this.bipedHeadwear.render(scale);
		}
		else
		{
			if (entityIn.isSneaking())
				GlStateManager.translate(0.0F, 0.2F, 0.0F);

			this.bipedHead.render(scale);
			this.bipedBody.render(scale);
			this.bipedRightArm.render(scale);
			this.bipedLeftArm.render(scale);
			this.bipedRightLeg.render(scale);
			this.bipedLeftLeg.render(scale);
			this.bipedHeadwear.render(scale);
		}

		if (this.translucent)
			GlStateManager.disableBlend(); //disable transparency

		GlStateManager.popMatrix();
	}

	/**Manually added setRotationAngles for Zombies, Skeletons, and ArmorStands*/
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	{
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

		if (entityIn instanceof EntityZombie) {
			boolean flag = entityIn instanceof EntityZombie && ((EntityZombie)entityIn).isArmsRaised();
			float f = MathHelper.sin(this.swingProgress * (float)Math.PI);
			float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float)Math.PI);
			this.bipedRightArm.rotateAngleZ = 0.0F;
			this.bipedLeftArm.rotateAngleZ = 0.0F;
			this.bipedRightArm.rotateAngleY = -(0.1F - f * 0.6F);
			this.bipedLeftArm.rotateAngleY = 0.1F - f * 0.6F;
			float f2 = -(float)Math.PI / (flag ? 1.5F : 2.25F);
			this.bipedRightArm.rotateAngleX = f2;
			this.bipedLeftArm.rotateAngleX = f2;
			this.bipedRightArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
			this.bipedLeftArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
			this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
			this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
			this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
			this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		}
		else if (entityIn instanceof EntitySkeleton) {
			ItemStack itemstack = ((EntityLivingBase)entityIn).getHeldItemMainhand();
			EntitySkeleton entityskeleton = (EntitySkeleton)entityIn;

			if (entityskeleton.isSwingingArms() && (itemstack == null || itemstack.getItem() != Items.BOW))
			{
				float f = MathHelper.sin(this.swingProgress * (float)Math.PI);
				float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float)Math.PI);
				this.bipedRightArm.rotateAngleZ = 0.0F;
				this.bipedLeftArm.rotateAngleZ = 0.0F;
				this.bipedRightArm.rotateAngleY = -(0.1F - f * 0.6F);
				this.bipedLeftArm.rotateAngleY = 0.1F - f * 0.6F;
				this.bipedRightArm.rotateAngleX = -((float)Math.PI / 2F);
				this.bipedLeftArm.rotateAngleX = -((float)Math.PI / 2F);
				this.bipedRightArm.rotateAngleX -= f * 1.2F - f1 * 0.4F;
				this.bipedLeftArm.rotateAngleX -= f * 1.2F - f1 * 0.4F;
				this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
				this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
				this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
				this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
			}
		}
		else if (entityIn instanceof EntityArmorStand) {
			EntityArmorStand entityarmorstand = (EntityArmorStand)entityIn;
			this.bipedHead.rotateAngleX = 0.017453292F * entityarmorstand.getHeadRotation().getX();
			this.bipedHead.rotateAngleY = 0.017453292F * entityarmorstand.getHeadRotation().getY();
			this.bipedHead.rotateAngleZ = 0.017453292F * entityarmorstand.getHeadRotation().getZ();
			this.bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
			this.bipedBody.rotateAngleX = 0.017453292F * entityarmorstand.getBodyRotation().getX();
			this.bipedBody.rotateAngleY = 0.017453292F * entityarmorstand.getBodyRotation().getY();
			this.bipedBody.rotateAngleZ = 0.017453292F * entityarmorstand.getBodyRotation().getZ();
			this.bipedLeftArm.rotateAngleX = 0.017453292F * entityarmorstand.getLeftArmRotation().getX();
			this.bipedLeftArm.rotateAngleY = 0.017453292F * entityarmorstand.getLeftArmRotation().getY();
			this.bipedLeftArm.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftArmRotation().getZ();
			this.bipedRightArm.rotateAngleX = 0.017453292F * entityarmorstand.getRightArmRotation().getX();
			this.bipedRightArm.rotateAngleY = 0.017453292F * entityarmorstand.getRightArmRotation().getY();
			this.bipedRightArm.rotateAngleZ = 0.017453292F * entityarmorstand.getRightArmRotation().getZ();
			this.bipedLeftLeg.rotateAngleX = 0.017453292F * entityarmorstand.getLeftLegRotation().getX();
			this.bipedLeftLeg.rotateAngleY = 0.017453292F * entityarmorstand.getLeftLegRotation().getY();
			this.bipedLeftLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftLegRotation().getZ();
			this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
			this.bipedRightLeg.rotateAngleX = 0.017453292F * entityarmorstand.getRightLegRotation().getX();
			this.bipedRightLeg.rotateAngleY = 0.017453292F * entityarmorstand.getRightLegRotation().getY();
			this.bipedRightLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getRightLegRotation().getZ();
			this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
			copyModelAngles(this.bipedHead, this.bipedHeadwear);
		}
	}
}