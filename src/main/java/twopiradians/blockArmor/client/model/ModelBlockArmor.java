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
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBlockArmor extends ModelBiped
{
	private ModelRenderer normalBipedHead;
	private ModelRenderer normalBipedBody;
	private ModelRenderer normalBipedRightArm;
	private ModelRenderer normalBipedLeftArm;
	private ModelRenderer normalBipedRightLeg;
	private ModelRenderer normalBipedLeftLeg;
	private ModelRenderer normalBipedWaist;
	private ModelRenderer normalBipedRightFoot;
	private ModelRenderer normalBipedLeftFoot;

	private ModelRenderer offsetBipedHead;
	private ModelRenderer offsetBipedBody;
	private ModelRenderer offsetBipedRightArm;
	private ModelRenderer offsetBipedLeftArm;
	private ModelRenderer offsetBipedRightLeg;
	private ModelRenderer offsetBipedLeftLeg;
	private ModelRenderer offsetBipedWaist;
	private ModelRenderer offsetBipedRightFoot;
	private ModelRenderer offsetBipedLeftFoot;

	private ModelRenderer bipedWaist;
	private ModelRenderer bipedRightFoot;
	private ModelRenderer bipedLeftFoot;

	private boolean translucent;
	private int color;
	public float alpha;

	public ModelBlockArmor(int textureHeight, int textureWidth, boolean isTranslucent, float frame, int color, EntityEquipmentSlot slot)
	{		
		int size = Math.max(1, textureWidth / 16);
		this.textureHeight = textureHeight / size;
		this.textureWidth = textureWidth / size;
		this.translucent = isTranslucent;
		this.color = color;
		//this.alpha = frame - (int) frame;
		int yOffset = this.textureWidth * (int) frame;

		//if (alpha > 0) { //animated - needs offset model TODO only do if animated?
			this.createModel(slot, yOffset + this.textureWidth);	
			offsetBipedHead = this.bipedHead;
			offsetBipedBody = this.bipedBody;
			offsetBipedRightArm = this.bipedRightArm;
			offsetBipedLeftArm = this.bipedLeftArm;
			offsetBipedRightLeg = this.bipedRightLeg;
			offsetBipedLeftLeg = this.bipedLeftLeg;
			offsetBipedWaist = this.bipedWaist;
			offsetBipedRightFoot = this.bipedRightFoot;
			offsetBipedLeftFoot = this.bipedLeftFoot;
		//}

		this.createModel(slot, yOffset);
		normalBipedHead = this.bipedHead;
		normalBipedBody = this.bipedBody;
		normalBipedRightArm = this.bipedRightArm;
		normalBipedLeftArm = this.bipedLeftArm;
		normalBipedRightLeg = this.bipedRightLeg;
		normalBipedLeftLeg = this.bipedLeftLeg;
		normalBipedWaist = this.bipedWaist;
		normalBipedRightFoot = this.bipedRightFoot;
		normalBipedLeftFoot = this.bipedLeftFoot;
	}

	public void createModel(EntityEquipmentSlot slot, int yOffset) {
		//Initialization and rotation points
		this.bipedHeadwear = new ModelRenderer(this, 0, 0);
		this.bipedHead = new ModelRenderer(this, 0, 0);
		this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedBody = new ModelRenderer(this, 0, 0);
		this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedRightArm = new ModelRenderer(this, 0, 0);
		this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		this.bipedLeftArm = new ModelRenderer(this, 0, 0);
		this.bipedLeftArm.mirror = true;
		this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		this.bipedWaist = new ModelRenderer(this, 0, 0);
		this.bipedWaist.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedRightLeg = new ModelRenderer(this, 0, 0);
		this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.bipedLeftLeg = new ModelRenderer(this, 0, 0);
		this.bipedLeftLeg.mirror = true;
		this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		this.bipedRightFoot = new ModelRenderer(this, 0, 0);
		this.bipedRightFoot.setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.bipedLeftFoot = new ModelRenderer(this, 0, 0);
		this.bipedLeftFoot.mirror = true;
		this.bipedLeftFoot.setRotationPoint(1.9F, 12.0F, 0.0F);

		//Add planes for specified slot
		switch (slot) {

		case HEAD:
			//HELMET
			this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 9, 3+yOffset, -5.0f, -9.0f, -5.0f, 10, 0, 10, false, textureWidth)); //top
			this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 3, 0+yOffset, -5.0f, -9.0f, 5.0f, 10, 8, 0, true, textureWidth)); //back
			this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 5, 8+yOffset, -3.0f, -1.0f, 5.0f, 6, 1, 0, true, textureWidth)); //back bottom
			this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 0, -10+yOffset, -5.0f, -9.0f, -5.0f, 0, 5, 10, false, textureWidth)); //right
			this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 0, 0+yOffset, -5.0f, -4.0f, -0.0f, 0, 1, 5, false, textureWidth)); //right bottom
			this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 6, -10+yOffset, 5.0f, -9.0f, -5.0f, 0, 5, 10, true, textureWidth)); //left
			this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 11, 0+yOffset, 5.0f, -4.0f, -0.0f, 0, 1, 5, true, textureWidth)); //left bottom
			this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 3, 0+yOffset, -5.0f, -9.0f, -5.0f, 10, 4, 0, false, textureWidth)); //front
			this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 0, 4+yOffset, -5.0F, -5.0F, -5.0F, 1, 1, 0, false, textureWidth)); //front left
			this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 7, 4+yOffset, -1.0F, -5.0F, -5.0F, 2, 2, 0, false, textureWidth)); //front mid
			this.bipedHead.cubeList.add(new ModelPlane(bipedHead, 12, 4+yOffset, 4.0F, -5.0F, -5.0F, 1, 1, 0, false, textureWidth)); //front right
			break;

		case CHEST:
			//CHEST
			this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 3, 4+yOffset, -5.0F, 1.0F, -3.0F, 10, 8, 0, false, textureWidth)); //front
			this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 4, 12+yOffset, -4.0F, 9.0F, -3.0F, 8, 1, 0, false, textureWidth)); //front bottom mid
			this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 5, 13+yOffset, -3.0F, 10.0F, -3.0F, 6, 1, 0, false, textureWidth)); //front bottom
			this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 3, 2+yOffset, -5.0F, -1.0F, -3.0F, 2, 2, 0, false, textureWidth)); //front top right 2x2
			this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 5, 2+yOffset, -3.0F, -0.0F, -3.0F, 1, 1, 0, false, textureWidth)); //front top right 1x1
			this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 11, 2+yOffset, 3.0F, -1.0F, -3.0F, 2, 2, 0, false, textureWidth)); //front top left 2x2
			this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 10, 2+yOffset, 2.0F, -0.0F, -3.0F, 1, 1, 0, false, textureWidth)); //front top left 1x1
			this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 5, -4+yOffset, -5.0F, -1.0F, -3.0F, 0, 10, 6, true, textureWidth)); //right 
			this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 5, -4+yOffset, 5.0F, -1.0F, -3.0F, 0, 10, 6, false, textureWidth)); //left 
			this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 3, 3+yOffset, -5.0F, 0.0F, 3.0F, 10, 9, 0, true, textureWidth)); //back 
			this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 11, 2+yOffset, -5.0F, -1.0F, 3.0F, 2, 1, 0, true, textureWidth)); //back top right 
			this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 3, 2+yOffset, 3.0F, -1.0F, 3.0F, 2, 1, 0, true, textureWidth)); //back top left 
			this.bipedBody.cubeList.add(new ModelPlane(bipedBody, 4, 12+yOffset, -4.0F, 9.0F, 3.0F, 8, 1, 0, true, textureWidth)); //back bottom

			//RIGHT ARM
			this.bipedRightArm.cubeList.add(new ModelPlane(bipedRightArm, 5, -1+yOffset, -4.5F, -3.0F, -3.0F, 0, 6, 6, true, textureWidth)); //right
			this.bipedRightArm.cubeList.add(new ModelPlane(bipedRightArm, 5, -1+yOffset, 1.5F, -3.0F, -3.0F, 0, 6, 6, true, textureWidth)); //left
			this.bipedRightArm.cubeList.add(new ModelPlane(bipedRightArm, 5, 5+yOffset, -4.5F, -3.0F, -3.0F, 6, 6, 0, false, textureWidth)); //front
			this.bipedRightArm.cubeList.add(new ModelPlane(bipedRightArm, 5, 5+yOffset, -4.5F, -3.0F, 3.0F, 6, 6, 0, true, textureWidth)); //back
			this.bipedRightArm.cubeList.add(new ModelPlane(bipedRightArm, 15, 5+yOffset, -4.5F, -3.0F, -3.0F, 6, 0, 6, false, textureWidth)); //top

			//LEFT ARM
			this.bipedLeftArm.cubeList.add(new ModelPlane(bipedLeftArm, 5, -1+yOffset, -1.5F, -3.0F, -3.0F, 0, 6, 6, false, textureWidth)); //right
			this.bipedLeftArm.cubeList.add(new ModelPlane(bipedLeftArm, 5, -1+yOffset, 4.5F, -3.0F, -3.0F, 0, 6, 6, false, textureWidth)); //left
			this.bipedLeftArm.cubeList.add(new ModelPlane(bipedLeftArm, 5, 5+yOffset, -1.5F, -3.0F, -3.0F, 6, 6, 0, false, textureWidth)); //front
			this.bipedLeftArm.cubeList.add(new ModelPlane(bipedLeftArm, 5, 5+yOffset, -1.5F, -3.0F, 3.0F, 6, 6, 0, true, textureWidth)); //back
			this.bipedLeftArm.cubeList.add(new ModelPlane(bipedLeftArm, 15, 5+yOffset, -1.5F, -3.0F, -3.0F, 6, 0, 6, false, textureWidth)); //top
			break;

		case LEGS:
			//WAIST
			this.bipedWaist.cubeList.add(new ModelPlane(bipedWaist, 3, 1+yOffset, -4.5F, 6.5F, -2.5F, 9, 6, 0, false, textureWidth)); //front
			this.bipedWaist.cubeList.add(new ModelPlane(bipedWaist, 5, -4+yOffset, -4.5F, 6.5F, -2.5F, 0, 6, 5, true, textureWidth)); //right 
			this.bipedWaist.cubeList.add(new ModelPlane(bipedWaist, 5, -4+yOffset, 4.5F, 6.5F, -2.5F, 0, 6, 5, false, textureWidth)); //left 
			this.bipedWaist.cubeList.add(new ModelPlane(bipedWaist, 3, 1+yOffset, -4.5F, 6.5F, 2.5F, 9, 6, 0, true, textureWidth)); //back

			//RIGHT LEG
			this.bipedRightLeg.cubeList.add(new ModelPlane(bipedRightLeg, 5, 0+yOffset, -2.5F, -0.5F, -2.5F, 0, 10, 5, true, textureWidth)); //right
			this.bipedRightLeg.cubeList.add(new ModelPlane(bipedRightLeg, 9, 5+yOffset, 2.5F, -0.5F, -2.5F, 0, 10, 5, true, textureWidth)); //left
			this.bipedRightLeg.cubeList.add(new ModelPlane(bipedRightLeg, 3, 5+yOffset, -2.5F, -0.5F, -2.5F, 5, 10, 0, false, textureWidth)); //front
			this.bipedRightLeg.cubeList.add(new ModelPlane(bipedRightLeg, 7, 5+yOffset, -2.5F, -0.5F, 2.5F, 5, 10, 0, true, textureWidth)); //back
			this.bipedRightLeg.cubeList.add(new ModelPlane(bipedRightLeg, 9, 0+yOffset, -2.5F, -0.5F, -2.5F, 5, 0, 5, false, textureWidth)); //top

			//LEFT LEG
			this.bipedLeftLeg.cubeList.add(new ModelPlane(bipedLeftLeg, 9, 5+yOffset, -2.5F, -0.5F, -2.5F, 0, 10, 5, true, textureWidth)); //right
			this.bipedLeftLeg.cubeList.add(new ModelPlane(bipedLeftLeg, 6, 0+yOffset, 2.5F, -0.5F, -2.5F, 0, 10, 5, true, textureWidth)); //left
			this.bipedLeftLeg.cubeList.add(new ModelPlane(bipedLeftLeg, 7, 5+yOffset, -2.5F, -0.5F, -2.5F, 5, 10, 0, false, textureWidth)); //front
			this.bipedLeftLeg.cubeList.add(new ModelPlane(bipedLeftLeg, 3, 5+yOffset, -2.5F, -0.5F, 2.5F, 5, 10, 0, true, textureWidth)); //back
			this.bipedLeftLeg.cubeList.add(new ModelPlane(bipedLeftLeg, 9, 0+yOffset, -2.5F, -0.5F, -2.5F, 5, 0, 5, false, textureWidth)); //top
			break;

		case FEET:
			//RIGHT FOOT
			this.bipedRightFoot.cubeList.add(new ModelPlane(bipedRightFoot, 13, -6+yOffset, -3.0F, 6.0F, -3.0F, 0, 7, 6, false, textureWidth)); //right
			this.bipedRightFoot.cubeList.add(new ModelPlane(bipedRightFoot, 13, -6+yOffset, 3.0F, 6.0F, -3.0F, 0, 7, 6, true, textureWidth)); //left
			this.bipedRightFoot.cubeList.add(new ModelPlane(bipedRightFoot, 3, 0+yOffset, -3.0F, 6.0F, -3.0F, 6, 7, 0, false, textureWidth)); //front
			this.bipedRightFoot.cubeList.add(new ModelPlane(bipedRightFoot, 7, 0+yOffset, -3.0F, 6.0F, 3.0F, 6, 7, 0, true, textureWidth)); //back
			this.bipedRightFoot.cubeList.add(new ModelPlane(bipedRightFoot, 10, 0+yOffset, -3.0F, 13.0F, -3.0F, 6, 0, 6, false, textureWidth)); //bottom

			//LEFT FOOT
			this.bipedLeftFoot.cubeList.add(new ModelPlane(bipedLeftFoot, 13, -6+yOffset, -3.0F, 6.0F, -3.0F, 0, 7, 6, true, textureWidth)); //right
			this.bipedLeftFoot.cubeList.add(new ModelPlane(bipedLeftFoot, 13, -6+yOffset, 3.0F, 6.0F, -3.0F, 0, 7, 6, true, textureWidth)); //left
			this.bipedLeftFoot.cubeList.add(new ModelPlane(bipedLeftFoot, 7, 0+yOffset, -3.0F, 6.0F, -3.0F, 6, 7, 0, false, textureWidth)); //front
			this.bipedLeftFoot.cubeList.add(new ModelPlane(bipedLeftFoot, 3, 0+yOffset, -3.0F, 6.0F, 3.0F, 6, 7, 0, true, textureWidth)); //back
			this.bipedLeftFoot.cubeList.add(new ModelPlane(bipedLeftFoot, 10, 0+yOffset, -3.0F, 13.0F, -3.0F, 6, 0, 6, false, textureWidth)); //bottom
			break;

		default:
			break;
		}
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		this.render2(false, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		//System.out.println(alpha);
		if (alpha > 0) {
			bipedHead = this.offsetBipedHead;
			bipedBody = this.offsetBipedBody;
			bipedRightArm = this.offsetBipedRightArm;
			bipedLeftArm = this.offsetBipedLeftArm;
			bipedRightLeg = this.offsetBipedRightLeg;
			bipedLeftLeg = this.offsetBipedLeftLeg;
			bipedWaist = this.offsetBipedWaist;
			bipedRightFoot = this.offsetBipedRightFoot;
			bipedLeftFoot = this.offsetBipedLeftFoot;
			this.render2(true, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			bipedHead = this.normalBipedHead;
			bipedBody = this.normalBipedBody;
			bipedRightArm = this.normalBipedRightArm;
			bipedLeftArm = this.normalBipedLeftArm;
			bipedRightLeg = this.normalBipedRightLeg;
			bipedLeftLeg = this.normalBipedLeftLeg;
			bipedWaist = this.normalBipedWaist;
			bipedRightFoot = this.normalBipedRightFoot;
			bipedLeftFoot = this.normalBipedLeftFoot;
		}
	}

	public void render2(boolean animationOverlay, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

		GlStateManager.pushMatrix();

		/*int numPasses = 1;
		if (alpha > 0)
			numPasses++;

		for (int pass=1; pass<= numPasses; pass++) {
			if (pass == 1) {
				ModelRenderer[] models = new ModelRenderer[] {bipedHead, bipedBody, bipedRightArm, bipedLeftArm, bipedWaist, bipedRightLeg, bipedLeftLeg, bipedRightFoot, bipedLeftFoot};
				for (ModelRenderer model : models) {
					for (ModelBox box : model.cubeList)
						if (box instanceof ModelPlane) 
							((ModelPlane) box).useOffsetQuad(true);
				}
			}
			//if second pass
			if (pass == 2) {
				GlStateManager.translate(0.5d, 0, 0);
				//set alpha
				GlStateManager.color(1.0f, 1.0f, 1.0f, alpha+1);
				//System.out.println("alpha: "+alpha);
				//offset texture in ModelPlanes

				//model.compiled = false;
				System.out.print("");
			}
			else {
				GlStateManager.color(1.0f, 1.0f, 1.0f, 0.99f);

			}
		 */

		if (color != -1) { //add color if needed
			color = color | -16777216;
			float cb = color & 0xFF;
			float cg = (color >>> 8) & 0xFF;
			float cr = (color >>> 16) & 0xFF;

			float red = cr/255f;
			float green = cg/255f;
			float blue = cb/255f;

			if (animationOverlay)
				GlStateManager.color(red, green, blue, alpha);
			else
				GlStateManager.color(red, green, blue);
		}
		else if (animationOverlay) { 
			GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
			//tGlStateManager.translate(0.5d, 0, 0);
		}

		if (this.translucent || animationOverlay) 
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
			this.bipedWaist.render(scale);
			this.bipedRightLeg.render(scale);
			this.bipedLeftLeg.render(scale);
			this.bipedRightFoot.render(scale);
			this.bipedLeftFoot.render(scale);
		}
		else
		{
			if (entityIn.isSneaking())
				GlStateManager.translate(0.0F, 0.2F, 0.0F);

			this.bipedHead.render(scale);
			this.bipedBody.render(scale);
			this.bipedRightArm.render(scale);
			this.bipedLeftArm.render(scale);
			this.bipedWaist.render(scale);
			this.bipedRightLeg.render(scale);
			this.bipedLeftLeg.render(scale);
			this.bipedRightFoot.render(scale);
			this.bipedLeftFoot.render(scale);
		}

		if (this.translucent || animationOverlay)
			GlStateManager.disableBlend(); //disable transparency
		//}

		/*		ModelRenderer[] models = new ModelRenderer[] {bipedHead, bipedBody, bipedRightArm, bipedLeftArm, bipedWaist, bipedRightLeg, bipedLeftLeg, bipedRightFoot, bipedLeftFoot};
		for (ModelRenderer model : models)
			for (ModelBox box : model.cubeList)
				if (box instanceof ModelPlane)
					((ModelPlane) box).useOffsetQuad(false);*/

		GlStateManager.popMatrix();
	}

	/**Manually added setRotationAngles from ModelZombie, ModelSkeleton, and ModelArmorStand*/
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
		}

		copyModelAngles(bipedBody, bipedWaist);
		copyModelAngles(bipedLeftLeg, bipedLeftFoot);
		copyModelAngles(bipedRightLeg, bipedRightFoot);
	}
}