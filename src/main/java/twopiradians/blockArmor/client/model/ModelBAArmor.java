package twopiradians.blockArmor.client.model;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.HashMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.command.CommandDev;
import twopiradians.blockArmor.common.item.BlockArmorItem;

@OnlyIn(Dist.CLIENT)
public class ModelBAArmor<T extends LivingEntity> extends HumanoidModel<T> {

	private static final Field MODEL_BOX_QUADS = ObfuscationReflectionHelper.findField(ModelPart.Cube.class, "polygons");
	/**Cached resource locations for armor textures*/
	private static final HashMap<String, ResourceLocation> TEXTURES = Maps.newHashMap();

	// models with offset texture according to nextFrame
	private ModelPart offsetHead;
	private ModelPart offsetBody;
	private ModelPart offsetRightArm;
	private ModelPart offsetLeftArm;
	private ModelPart offsetRightLeg;
	private ModelPart offsetLeftLeg;
	private ModelPart offsetWaist;
	private ModelPart offsetRightFoot;
	private ModelPart offsetLeftFoot;

	private ModelPart waist;
	private ModelPart rightFoot;
	private ModelPart leftFoot;

	public int color;
	public float alpha;
	private int textureHeight;
	private int textureWidth;

	private EquipmentSlot slot;
	/**
	 * Entity currently being rendered in this model (since entity isn't passed into
	 * render() anymore...)
	 */
	public LivingEntity entity;

	/**Not sure why this is needed, but have to re-model once for animated textures to have the offset be correct*/
	public int remodelOffset; 

	public ModelBAArmor(int textureHeight, int textureWidth, int currentFrame, int nextFrame, EquipmentSlot slot) { 
		super(new EntityRendererProvider.Context(Minecraft.getInstance().getEntityRenderDispatcher(), Minecraft.getInstance().getItemRenderer(), Minecraft.getInstance().getResourceManager(), Minecraft.getInstance().getEntityModels(), Minecraft.getInstance().font).bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
		int size = Math.max(1, textureWidth / 16);
		this.textureHeight = textureHeight / size;
		this.textureWidth = textureWidth / size;
		this.slot = slot;

		this.waist = new ModelPart(Lists.newArrayList(new ModelPart.Cube(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, 0, 0)), Maps.newHashMap());
		this.rightFoot = new ModelPart(Lists.newArrayList(new ModelPart.Cube(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, 0, 0)), Maps.newHashMap());
		this.leftFoot = new ModelPart(Lists.newArrayList(new ModelPart.Cube(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, true, 0, 0)), Maps.newHashMap());

		if (currentFrame != nextFrame) { // if animated, create models with offset textures for overlay
			offsetHead = new ModelPart(Lists.newArrayList(), Maps.newHashMap());
			offsetBody = new ModelPart(Lists.newArrayList(), Maps.newHashMap());
			offsetRightArm = new ModelPart(Lists.newArrayList(), Maps.newHashMap());
			offsetLeftArm = new ModelPart(Lists.newArrayList(), Maps.newHashMap());
			offsetRightLeg = new ModelPart(Lists.newArrayList(), Maps.newHashMap());
			offsetLeftLeg = new ModelPart(Lists.newArrayList(), Maps.newHashMap());
			offsetWaist = new ModelPart(Lists.newArrayList(), Maps.newHashMap());
			offsetRightFoot = new ModelPart(Lists.newArrayList(), Maps.newHashMap());
			offsetLeftFoot = new ModelPart(Lists.newArrayList(), Maps.newHashMap());
			this.createModel(slot, textureWidth * nextFrame,
					this.offsetHead, this.offsetBody, this.offsetRightArm, this.offsetLeftArm, 
					this.offsetRightLeg, this.offsetLeftLeg, this.offsetWaist, this.offsetRightFoot, this.offsetLeftFoot);
		}

		this.remodelOffset = textureWidth * currentFrame;
		this.createModel(slot, textureHeight * currentFrame,
				this.head, this.body, this.rightArm, this.leftArm, 
				this.rightLeg, this.leftLeg, this.waist, this.rightFoot, this.leftFoot);
	}

	private void createModel(EquipmentSlot slot, int yOffset, 
			ModelPart head, ModelPart body, ModelPart rightArm, 
			ModelPart leftArm, ModelPart rightLeg, ModelPart leftLeg, 
			ModelPart waist, ModelPart rightFoot, ModelPart leftFoot) {	

		// Initialization and rotation points
		head.cubes = Lists.newArrayList();
		head.setPos(0.0F, 0.0F, 0.0F);
		body.cubes = Lists.newArrayList();
		body.setPos(0.0F, 0.0F, 0.0F);
		rightArm.cubes = Lists.newArrayList();
		rightArm.setPos(-5.0F, 2.0F, 0.0F);
		leftArm.cubes = Lists.newArrayList();
		leftArm.setPos(5.0F, 2.0F, 0.0F);
		waist.setPos(0.0F, 0.0F, 0.0F);
		rightLeg.cubes = Lists.newArrayList();
		rightLeg.setPos(-1.9F, 12.0F, 0.0F);
		leftLeg.cubes = Lists.newArrayList();
		leftLeg.setPos(1.9F, 12.0F, 0.0F);
		rightFoot.setPos(-1.9F, 12.0F, 0.0F);
		leftFoot.setPos(1.9F, 12.0F, 0.0F);

		// Add planes for specified slot
		switch (slot) {

		case HEAD:
			// HELMET
			this.addPlane(head, 9, 3 + yOffset, -5.0f, -9.0f, -5.0f, 10, 0, 10, false); // top
			this.addPlane(head, 9, 0 + yOffset, -5.0f, -9.0f, 5.0f, 10, 8, 0, true); // back
			this.addPlane(head, -1, 8 + yOffset, -3.0f, -1.0f, 5.0f, 6, 1, 0, true); // back bottom
			this.addPlane(head, 6, -10 + yOffset, -5.0f, -9.0f, -5.0f, 0, 5, 10, false); // right
			this.addPlane(head, 0, 0 + yOffset, -5.0f, -4.0f, -0.0f, 0, 1, 5, false); // right bottom
			this.addPlane(head, 6, -10 + yOffset, 5.0f, -9.0f, -5.0f, 0, 5, 10, true); // left
			this.addPlane(head, 11, 0 + yOffset, 5.0f, -4.0f, -0.0f, 0, 1, 5, true); // left bottom
			this.addPlane(head, 3, 0 + yOffset, -5.0f, -9.0f, -5.0f, 10, 4, 0, false); // front
			this.addPlane(head, 0, 4 + yOffset, -5.0F, -5.0F, -5.0F, 1, 1, 0, false); // front left
			this.addPlane(head, 7, 4 + yOffset, -1.0F, -5.0F, -5.0F, 2, 2, 0, false); // front mid
			this.addPlane(head, 12, 4 + yOffset, 4.0F, -5.0F, -5.0F, 1, 1, 0, false); // front right
			break;

		case CHEST:
			// CHEST
			this.addPlane(body, 3, 4 + yOffset, -5.0F, 1.0F, -3.0F, 10, 8, 0, false); // front
			this.addPlane(body, 4, 12 + yOffset, -4.0F, 9.0F, -3.0F, 8, 1, 0, false); // front bottom mid
			this.addPlane(body, 5, 13 + yOffset, -3.0F, 10.0F, -3.0F, 6, 1, 0, false); // front bottom
			this.addPlane(body, 3, 2 + yOffset, -5.0F, -1.0F, -3.0F, 2, 2, 0, false); // front top right 2x2
			this.addPlane(body, 5, 2 + yOffset, -3.0F, -0.0F, -3.0F, 1, 1, 0, false); // front top right 1x1
			this.addPlane(body, 11, 2 + yOffset, 3.0F, -1.0F, -3.0F, 2, 2, 0, false); // front top left 2x2
			this.addPlane(body, 10, 2 + yOffset, 2.0F, -0.0F, -3.0F, 1, 1, 0, false); // front top left 1x1
			this.addPlane(body, 5, -4 + yOffset, -5.0F, -1.0F, -3.0F, 0, 10, 6, true); // right
			this.addPlane(body, -1, -4 + yOffset, 5.0F, -1.0F, -3.0F, 0, 10, 6, false); // left
			this.addPlane(body, 9, 3 + yOffset, -5.0F, 0.0F, 3.0F, 10, 9, 0, true); // back
			this.addPlane(body, 11, 2 + yOffset, -5.0F, -1.0F, 3.0F, 2, 0, 0, true); // back top right
			this.addPlane(body, 3, 2 + yOffset, 3.0F, -1.0F, 3.0F, 2, 1, 0, true); // back top left
			this.addPlane(body, 4, 12 + yOffset, -4.0F, 9.0F, 3.0F, 8, 1, 0, true); // back bottom

			// RIGHT ARM
			this.addPlane(rightArm, 5, -4 + yOffset, -4.5F, -3.0F, -3.0F, 0, 6, 6, true); // right
			this.addPlane(rightArm, 5, -1 + yOffset, 1.5F, -3.0F, -3.0F, 0, 6, 6, true); // left
			this.addPlane(rightArm, -2, 2 + yOffset, -4.5F, -3.0F, -3.0F, 6, 6, 0, false); // front
			this.addPlane(rightArm, -8, 2 + yOffset, -4.5F, -3.0F, 3.0F, 6, 6, 0, true); // back
			this.addPlane(rightArm, 15, 5 + yOffset, -4.5F, -3.0F, -3.0F, 6, 0, 6, false); // top

			// LEFT ARM
			this.addPlane(leftArm, -1, -1 + yOffset, -1.5F, -3.0F, -3.0F, 0, 6, 6, false); // right
			this.addPlane(leftArm, -1, -4 + yOffset, 4.5F, -3.0F, -3.0F, 0, 6, 6, false); // left
			this.addPlane(leftArm, 12, 2 + yOffset, -1.5F, -3.0F, -3.0F, 6, 6, 0, false); // front
			this.addPlane(leftArm, 5, 5 + yOffset, -1.5F, -3.0F, 3.0F, 6, 6, 0, true); // back
			this.addPlane(leftArm, 15, 5 + yOffset, -1.5F, -3.0F, -3.0F, 6, 0, 6, false); // top
			break;

		case LEGS:
			// WAIST
			this.addPlane(waist, 3, 0 + yOffset, -4.5F, 6.5F, -2.5F, 9, 6, 0, false); // front
			this.addPlane(waist, 6, -5 + yOffset, -4.5F, 6.5F, -2.5F, 0, 6, 5, false); // right
			this.addPlane(waist, 6, -5 + yOffset, 4.5F, 6.5F, -2.5F, 0, 6, 5, false); // left
			this.addPlane(waist, 11, 0 + yOffset, -4.5F, 6.5F, 2.5F, 9, 6, 0, true); // back

			// RIGHT LEG
			this.addPlane(rightLeg, 6, 0 + yOffset, -2.5F, -0.5F, -2.5F, 0, 10, 5, false); // right
			this.addPlane(rightLeg, 9, 5 + yOffset, 2.5F, -0.5F, -2.5F, 0, 10, 5, true); // left
			this.addPlane(rightLeg, 3, 5 + yOffset, -2.5F, -0.5F, -2.5F, 5, 10, 0, false); // front
			this.addPlane(rightLeg, -1, 5 + yOffset, -2.5F, -0.5F, 2.5F, 5, 10, 0, true); // back
			this.addPlane(rightLeg, 9, 0 + yOffset, -2.5F, -0.5F, -2.5F, 5, 0, 5, false); // top

			// LEFT LEG
			this.addPlane(leftLeg, 9, 5 + yOffset, -2.5F, -0.5F, -2.5F, 0, 10, 5, false); // right
			this.addPlane(leftLeg, 6, 0 + yOffset, 2.5F, -0.5F, -2.5F, 0, 10, 5, false); // left
			this.addPlane(leftLeg, 7, 5 + yOffset, -2.5F, -0.5F, -2.5F, 5, 10, 0, false); // front
			this.addPlane(leftLeg, 3, 5 + yOffset, -2.5F, -0.5F, 2.5F, 5, 10, 0, true); // back
			this.addPlane(leftLeg, 9, 0 + yOffset, -2.5F, -0.5F, -2.5F, 5, 0, 5, false); // top
			break;

		case FEET:
			// RIGHT FOOT
			this.addPlane(rightFoot, 20, 3 + yOffset, -3.0F, 6.0F, -3.0F, 0, 7, 6, false); // right
			this.addPlane(rightFoot, 21, 3 + yOffset, 3.0F, 6.0F, -3.0F, 0, 7, 6, true); // left
			this.addPlane(rightFoot, 2, 7 + yOffset, -3.0F, 6.0F, -3.0F, 6, 7, 0, false); // front
			this.addPlane(rightFoot, -2, 9 + yOffset, -3.0F, 6.0F, 3.0F, 6, 7, 0, true); // back
			this.addPlane(rightFoot, -1, 5 + yOffset, -3.0F, 13.0F, -3.0F, 6, 0, 6, false); // bottom

			// LEFT FOOT
			this.addPlane(leftFoot, 21, 3 + yOffset, -3.0F, 6.0F, -3.0F, 0, 7, 6, true); // right
			this.addPlane(leftFoot, 0, 3 + yOffset, 3.0F, 6.0F, -3.0F, 0, 7, 6, true); // left
			this.addPlane(leftFoot, -8, 7 + yOffset, -3.0F, 6.0F, -3.0F, 6, 7, 0, false); // front
			this.addPlane(leftFoot, 0, 9 + yOffset, -3.0F, 6.0F, 3.0F, 6, 7, 0, true); // back
			this.addPlane(leftFoot, -1, 5 + yOffset, -3.0F, 13.0F, -3.0F, 6, 0, 6, false); // bottom
			break;

		default:
			break;
		}
	}

	/** Add a plane (model box with just one quad) to this model */
	private void addPlane(ModelPart model, int textureOffsetX, int textureOffsetY, float x, float y, float z,
			float width, float height, float depth, boolean flip) {
		try {			
			Cube cube = new Cube(textureOffsetX, textureOffsetY, x, y, z, width, height, depth, 0, 0, 0, flip, this.textureWidth, this.textureHeight);
			ModelPart.Polygon[] quads = (ModelPart.Polygon[]) MODEL_BOX_QUADS.get(cube);
			ModelPart.Polygon[] newQuads = new ModelPart.Polygon[1];

			if (width == 0) // y-z plane
				newQuads[0] = quads[flip ? 1 : 0];
			else if (height == 0)
				newQuads[0] = quads[flip ? 3 : 2];
			else if (depth == 0) // x-z plane
				newQuads[0] = quads[flip ? 5 : 4];
			MODEL_BOX_QUADS.set(cube, newQuads);
			model.cubes.add(cube);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	@Override
	public void renderToBuffer(PoseStack matrix, VertexConsumer vertex, int lightMapUV, int overlayUV, float ageInTicks,
			float netHeadYaw, float headPitch, float scale) {
		if (this.remodelOffset != -1) {
			this.createModel(slot, remodelOffset,
					this.head, this.body, this.rightArm, this.leftArm, 
					this.rightLeg, this.leftLeg, this.waist, this.rightFoot, this.leftFoot); 
			this.remodelOffset = -1;
		}

		this.setupAnim((T) entity, lightMapUV, overlayUV, ageInTicks, netHeadYaw, headPitch);

		ItemStack stack = entity.getItemBySlot(slot);

		//don't render enchant if only enchant is from set effect
		boolean renderEnchant = false;
		if (stack != null && stack.isEnchanted() && stack.getItem() instanceof BlockArmorItem) {
			ListTag enchantNbt = stack.getEnchantmentTags();
			for (int i=0; i<enchantNbt.size(); ++i)
				if (!enchantNbt.getCompound(i).getBoolean(BlockArmor.MODID+" enchant")) {
					renderEnchant = true;
					break;
				}
		}

		// use different vertex builder to allow translucency
		if (stack != null && stack.getItem() instanceof BlockArmorItem) {
			// get texture location
			String str = ((BlockArmorItem)stack.getItem()).getArmorTexture(stack, entity, slot, "");
			ResourceLocation loc = TEXTURES.get(str);
			if (loc == null) {
				loc = new ResourceLocation(str);
				TEXTURES.put(str, loc);
			}
			// normal render - use translucent vertex
			vertex = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.entityTranslucent(loc));
			this.actualRender(matrix, vertex, false, lightMapUV, overlayUV, ageInTicks, netHeadYaw, headPitch, scale,
					this.head, this.body, this.rightArm, this.leftArm, 
					this.rightLeg, this.leftLeg, this.waist, this.rightFoot, this.leftFoot);	
			// if enchanted - use entity glint vertex on top
			if (renderEnchant) {
				vertex = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.entityGlintDirect());
				this.actualRender(matrix, vertex, false, lightMapUV, overlayUV, ageInTicks, netHeadYaw, headPitch, scale,
						this.head, this.body, this.rightArm, this.leftArm, 
						this.rightLeg, this.leftLeg, this.waist, this.rightFoot, this.leftFoot);
			}
		}

		// If animated, switch to offset models, render animation overlay, then switch
		// back to normal models
		if (alpha > 0 && offsetHead != null) {
			offsetHead.copyFrom(head);
			offsetBody.copyFrom(body);
			offsetRightArm.copyFrom(rightArm);
			offsetLeftArm.copyFrom(leftArm);
			offsetRightLeg.copyFrom(rightLeg);
			offsetLeftLeg.copyFrom(leftLeg);
			offsetWaist.copyFrom(waist);
			offsetRightFoot.copyFrom(rightFoot);
			offsetLeftFoot.copyFrom(leftFoot);
			this.actualRender(matrix, vertex, true, lightMapUV, overlayUV, ageInTicks, netHeadYaw, headPitch, scale,
					this.offsetHead, this.offsetBody, this.offsetRightArm, this.offsetLeftArm, 
					this.offsetRightLeg, this.offsetLeftLeg, this.offsetWaist, this.offsetRightFoot, this.offsetLeftFoot);
		}
	}

	/**Do the actual rendering*/
	private void actualRender(PoseStack matrix, VertexConsumer vertex, boolean animationOverlay, int lightMapUV,
			int overlayUV, float ageInTicks, float netHeadYaw, float headPitch, float scale, 
			ModelPart head, ModelPart body, ModelPart rightArm, 
			ModelPart leftArm, ModelPart rightLeg, ModelPart leftLeg, 
			ModelPart waist, ModelPart rightFoot, ModelPart leftFoot) {

		float red = 1.0f;
		float green = 1.0f;
		float blue = 1.0f;

		// change color if needed (i.e. grass)
		if (color != -1) { 
			color = color | -16777216;
			float cb = color & 0xFF;
			float cg = (color >>> 8) & 0xFF;
			float cr = (color >>> 16) & 0xFF;

			red = cr / 255f;
			green = cg / 255f;
			blue = cb / 255f;
		}

		// dev effects 
		Float[] color = CommandDev.devColors.get(this.entity.getUUID());
		if (color != null) {
			if (color[0] == 0 && color[1] == 0 && color[2] == 0) { //rainbow
				Color color2 = Color.getHSBColor(this.entity.tickCount/30f, 1f, 1f);
				red = color2.getRed()/255f;
				green = color2.getGreen()/255f;
				blue = color2.getBlue()/255f;
			}
			else { //pulse
				double pulse = (Math.cos(this.entity.tickCount/5f)+1d)/3d+0.01d;
				red += pulse * color[0];
				green += pulse * color[1];
				blue += pulse * color[2];
			}
		}

		matrix.pushPose();

		if (this.young) {
			matrix.pushPose();         
			float num;
			if (true) {
				num = 1.5F / 2f;
				matrix.scale(num, num, num);
			}
			matrix.translate(0.0D, (double)(16.0f / 16.0F), (double)(0f / 16.0F));
			this.head.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			matrix.popPose();

			matrix.pushPose();
			num = 1.0F / 2f;
			matrix.scale(num, num, num);
			matrix.translate(0.0D, (double)(24f / 16.0F), 0.0D);
			body.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			rightArm.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			leftArm.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			waist.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			rightLeg.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			leftLeg.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			rightFoot.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			leftFoot.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			matrix.popPose();
		} else {
			head.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			body.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			rightArm.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			leftArm.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			waist.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			rightLeg.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			leftLeg.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			rightFoot.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			leftFoot.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
		}

		matrix.popPose();
	}

	@Override
	public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {
		// offset slightly to prevent z-fighting
		this.leftLeg.xRot -= 0.001f;
		waist.copyFrom(body);
		leftFoot.copyFrom(leftLeg);
		rightFoot.copyFrom(rightLeg);
	}
}