package twopiradians.blockArmor.client.model;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.HashMap;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.ModelRenderer.ModelBox;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.command.CommandDev;
import twopiradians.blockArmor.common.item.BlockArmorItem;

@OnlyIn(Dist.CLIENT)
public class ModelBAArmor<T extends LivingEntity> extends BipedModel<T> {

	private static final Field MODEL_RENDERER_CUBELIST;
	private static final Field MODEL_BOX_QUADS;
	/**Cached resource locations for armor textures*/
	private static final HashMap<String, ResourceLocation> TEXTURES = Maps.newHashMap();

	static {
		MODEL_RENDERER_CUBELIST = ObfuscationReflectionHelper.findField(ModelRenderer.class, "field_78804_l");
		MODEL_BOX_QUADS = ObfuscationReflectionHelper.findField(ModelRenderer.ModelBox.class, "field_78254_i");
	}

	// normal models for currentFrame
	private ModelRenderer normalBipedHead;
	private ModelRenderer normalBipedBody;
	private ModelRenderer normalBipedRightArm;
	private ModelRenderer normalBipedLeftArm;
	private ModelRenderer normalBipedRightLeg;
	private ModelRenderer normalBipedLeftLeg;
	private ModelRenderer normalBipedWaist;
	private ModelRenderer normalBipedRightFoot;
	private ModelRenderer normalBipedLeftFoot;

	// models with offset texture according to nextFrame
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

	public int color;
	public float alpha;

	private EquipmentSlotType slot;
	/**
	 * Entity currently being rendered in this model (since entity isn't passed into
	 * render() anymore...)
	 */
	public LivingEntity entity;

	public ModelBAArmor(int textureHeight, int textureWidth, int currentFrame, int nextFrame, EquipmentSlotType slot) { 
		super(0);
		int size = Math.max(1, textureWidth / 16);
		this.textureHeight = textureHeight / size;
		this.textureWidth = textureWidth / size;
		this.slot = slot;

		if (currentFrame != nextFrame) { // if animated, create models with offset textures for overlay
			this.createModel(slot, this.textureWidth * nextFrame);
			offsetBipedHead = this.bipedHead;
			offsetBipedBody = this.bipedBody;
			offsetBipedRightArm = this.bipedRightArm;
			offsetBipedLeftArm = this.bipedLeftArm;
			offsetBipedRightLeg = this.bipedRightLeg;
			offsetBipedLeftLeg = this.bipedLeftLeg;
			offsetBipedWaist = this.bipedWaist;
			offsetBipedRightFoot = this.bipedRightFoot;
			offsetBipedLeftFoot = this.bipedLeftFoot;
		}

		this.createModel(slot, this.textureWidth * currentFrame);
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

	public void createModel(EquipmentSlotType slot, int yOffset) {
		// Initialization and rotation points
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

		// Add planes for specified slot
		switch (slot) {

		case HEAD:
			// HELMET
			this.addPlane(bipedHead, 9, 3 + yOffset, -5.0f, -9.0f, -5.0f, 10, 0, 10, false); // top
			this.addPlane(bipedHead, 9, 0 + yOffset, -5.0f, -9.0f, 5.0f, 10, 8, 0, true); // back
			this.addPlane(bipedHead, -1, 8 + yOffset, -3.0f, -1.0f, 5.0f, 6, 1, 0, true); // back bottom
			this.addPlane(bipedHead, 6, -10 + yOffset, -5.0f, -9.0f, -5.0f, 0, 5, 10, false); // right
			this.addPlane(bipedHead, 0, 0 + yOffset, -5.0f, -4.0f, -0.0f, 0, 1, 5, false); // right bottom
			this.addPlane(bipedHead, 6, -10 + yOffset, 5.0f, -9.0f, -5.0f, 0, 5, 10, true); // left
			this.addPlane(bipedHead, 11, 0 + yOffset, 5.0f, -4.0f, -0.0f, 0, 1, 5, true); // left bottom
			this.addPlane(bipedHead, 3, 0 + yOffset, -5.0f, -9.0f, -5.0f, 10, 4, 0, false); // front
			this.addPlane(bipedHead, 0, 4 + yOffset, -5.0F, -5.0F, -5.0F, 1, 1, 0, false); // front left
			this.addPlane(bipedHead, 7, 4 + yOffset, -1.0F, -5.0F, -5.0F, 2, 2, 0, false); // front mid
			this.addPlane(bipedHead, 12, 4 + yOffset, 4.0F, -5.0F, -5.0F, 1, 1, 0, false); // front right
			break;

		case CHEST:
			// CHEST
			this.addPlane(bipedBody, 3, 4 + yOffset, -5.0F, 1.0F, -3.0F, 10, 8, 0, false); // front
			this.addPlane(bipedBody, 4, 12 + yOffset, -4.0F, 9.0F, -3.0F, 8, 1, 0, false); // front bottom mid
			this.addPlane(bipedBody, 5, 13 + yOffset, -3.0F, 10.0F, -3.0F, 6, 1, 0, false); // front bottom
			this.addPlane(bipedBody, 3, 2 + yOffset, -5.0F, -1.0F, -3.0F, 2, 2, 0, false); // front top right 2x2
			this.addPlane(bipedBody, 5, 2 + yOffset, -3.0F, -0.0F, -3.0F, 1, 1, 0, false); // front top right 1x1
			this.addPlane(bipedBody, 11, 2 + yOffset, 3.0F, -1.0F, -3.0F, 2, 2, 0, false); // front top left 2x2
			this.addPlane(bipedBody, 10, 2 + yOffset, 2.0F, -0.0F, -3.0F, 1, 1, 0, false); // front top left 1x1
			this.addPlane(bipedBody, 5, -4 + yOffset, -5.0F, -1.0F, -3.0F, 0, 10, 6, true); // right
			this.addPlane(bipedBody, -1, -4 + yOffset, 5.0F, -1.0F, -3.0F, 0, 10, 6, false); // left
			this.addPlane(bipedBody, 9, 3 + yOffset, -5.0F, 0.0F, 3.0F, 10, 9, 0, true); // back
			this.addPlane(bipedBody, 11, 2 + yOffset, -5.0F, -1.0F, 3.0F, 2, 0, 0, true); // back top right
			this.addPlane(bipedBody, 3, 2 + yOffset, 3.0F, -1.0F, 3.0F, 2, 1, 0, true); // back top left
			this.addPlane(bipedBody, 4, 12 + yOffset, -4.0F, 9.0F, 3.0F, 8, 1, 0, true); // back bottom

			// RIGHT ARM
			this.addPlane(bipedRightArm, 5, -4 + yOffset, -4.5F, -3.0F, -3.0F, 0, 6, 6, true); // right
			this.addPlane(bipedRightArm, 5, -1 + yOffset, 1.5F, -3.0F, -3.0F, 0, 6, 6, true); // left
			this.addPlane(bipedRightArm, -2, 2 + yOffset, -4.5F, -3.0F, -3.0F, 6, 6, 0, false); // front
			this.addPlane(bipedRightArm, -8, 2 + yOffset, -4.5F, -3.0F, 3.0F, 6, 6, 0, true); // back
			this.addPlane(bipedRightArm, 15, 5 + yOffset, -4.5F, -3.0F, -3.0F, 6, 0, 6, false); // top

			// LEFT ARM
			this.addPlane(bipedLeftArm, -1, -1 + yOffset, -1.5F, -3.0F, -3.0F, 0, 6, 6, false); // right
			this.addPlane(bipedLeftArm, -1, -4 + yOffset, 4.5F, -3.0F, -3.0F, 0, 6, 6, false); // left
			this.addPlane(bipedLeftArm, 12, 2 + yOffset, -1.5F, -3.0F, -3.0F, 6, 6, 0, false); // front
			this.addPlane(bipedLeftArm, 5, 5 + yOffset, -1.5F, -3.0F, 3.0F, 6, 6, 0, true); // back
			this.addPlane(bipedLeftArm, 15, 5 + yOffset, -1.5F, -3.0F, -3.0F, 6, 0, 6, false); // top
			break;

		case LEGS:
			// WAIST
			this.addPlane(bipedWaist, 3, 0 + yOffset, -4.5F, 6.5F, -2.5F, 9, 6, 0, false); // front
			this.addPlane(bipedWaist, 6, -5 + yOffset, -4.5F, 6.5F, -2.5F, 0, 6, 5, false); // right
			this.addPlane(bipedWaist, 6, -5 + yOffset, 4.5F, 6.5F, -2.5F, 0, 6, 5, false); // left
			this.addPlane(bipedWaist, 11, 0 + yOffset, -4.5F, 6.5F, 2.5F, 9, 6, 0, true); // back

			// RIGHT LEG
			this.addPlane(bipedRightLeg, 6, 0 + yOffset, -2.5F, -0.5F, -2.5F, 0, 10, 5, false); // right
			this.addPlane(bipedRightLeg, 9, 5 + yOffset, 2.5F, -0.5F, -2.5F, 0, 10, 5, true); // left
			this.addPlane(bipedRightLeg, 3, 5 + yOffset, -2.5F, -0.5F, -2.5F, 5, 10, 0, false); // front
			this.addPlane(bipedRightLeg, -1, 5 + yOffset, -2.5F, -0.5F, 2.5F, 5, 10, 0, true); // back
			this.addPlane(bipedRightLeg, 9, 0 + yOffset, -2.5F, -0.5F, -2.5F, 5, 0, 5, false); // top

			// LEFT LEG
			this.addPlane(bipedLeftLeg, 9, 5 + yOffset, -2.5F, -0.5F, -2.5F, 0, 10, 5, false); // right
			this.addPlane(bipedLeftLeg, 6, 0 + yOffset, 2.5F, -0.5F, -2.5F, 0, 10, 5, false); // left
			this.addPlane(bipedLeftLeg, 7, 5 + yOffset, -2.5F, -0.5F, -2.5F, 5, 10, 0, false); // front
			this.addPlane(bipedLeftLeg, 3, 5 + yOffset, -2.5F, -0.5F, 2.5F, 5, 10, 0, true); // back
			this.addPlane(bipedLeftLeg, 9, 0 + yOffset, -2.5F, -0.5F, -2.5F, 5, 0, 5, false); // top
			break;

		case FEET:
			// RIGHT FOOT
			this.addPlane(bipedRightFoot, 20, 3 + yOffset, -3.0F, 6.0F, -3.0F, 0, 7, 6, false); // right
			this.addPlane(bipedRightFoot, 21, 3 + yOffset, 3.0F, 6.0F, -3.0F, 0, 7, 6, true); // left
			this.addPlane(bipedRightFoot, 2, 7 + yOffset, -3.0F, 6.0F, -3.0F, 6, 7, 0, false); // front
			this.addPlane(bipedRightFoot, -2, 9 + yOffset, -3.0F, 6.0F, 3.0F, 6, 7, 0, true); // back
			this.addPlane(bipedRightFoot, -1, 5 + yOffset, -3.0F, 13.0F, -3.0F, 6, 0, 6, false); // bottom

			// LEFT FOOT
			this.addPlane(bipedLeftFoot, 21, 3 + yOffset, -3.0F, 6.0F, -3.0F, 0, 7, 6, true); // right
			this.addPlane(bipedLeftFoot, 0, 3 + yOffset, 3.0F, 6.0F, -3.0F, 0, 7, 6, true); // left
			this.addPlane(bipedLeftFoot, -8, 7 + yOffset, -3.0F, 6.0F, -3.0F, 6, 7, 0, false); // front
			this.addPlane(bipedLeftFoot, 0, 9 + yOffset, -3.0F, 6.0F, 3.0F, 6, 7, 0, true); // back
			this.addPlane(bipedLeftFoot, -1, 5 + yOffset, -3.0F, 13.0F, -3.0F, 6, 0, 6, false); // bottom
			break;

		default:
			break;
		}
	}

	/** Add a plane (model box with just one quad) to this model */
	private void addPlane(ModelRenderer model, int textureOffsetX, int textureOffsetY, float x, float y, float z,
			float width, float height, float depth, boolean flip) {
		model.setTextureOffset(textureOffsetX, textureOffsetY);
		model.addBox(x, y, z, width, height, depth, flip);
		try {
			ObjectList<ModelBox> cubelist = (ObjectList<ModelBox>) MODEL_RENDERER_CUBELIST.get(model);
			ModelBox box = cubelist.get(cubelist.size() - 1);
			ModelRenderer.TexturedQuad[] quads = (ModelRenderer.TexturedQuad[]) MODEL_BOX_QUADS.get(box);
			ModelRenderer.TexturedQuad[] newQuads = new ModelRenderer.TexturedQuad[1];

			if (width == 0) // y-z plane
				newQuads[0] = quads[flip ? 1 : 0];
			else if (height == 0)
				newQuads[0] = quads[flip ? 3 : 2];
			else if (depth == 0) // x-z plane
				newQuads[0] = quads[flip ? 5 : 4];
			MODEL_BOX_QUADS.set(box, newQuads);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	@Override
	public void render(MatrixStack matrix, IVertexBuilder vertex, int lightMapUV, int overlayUV, float ageInTicks,
			float netHeadYaw, float headPitch, float scale) {
		//this.createModel(slot, 0);

		this.setRotationAngles((T) entity, lightMapUV, overlayUV, ageInTicks, netHeadYaw, headPitch);

		ItemStack stack = entity.getItemStackFromSlot(slot);

		//don't render enchant if only enchant is from set effect
		boolean renderEnchant = stack != null && stack.isEnchanted();
		if (stack != null && stack.getItem() instanceof BlockArmorItem && stack.hasTag()) {
			ListNBT enchantNbt = stack.getTag().getList("ench", 10);
			if (enchantNbt.size() == 1 && enchantNbt.getCompound(0).getBoolean(BlockArmor.MODID+" enchant"))
				renderEnchant = false;
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
			vertex = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource().getBuffer(RenderType.getEntityTranslucent(loc));
			this.actualRender(matrix, vertex, false, lightMapUV, overlayUV, ageInTicks, netHeadYaw, headPitch, scale);	
			// if enchanted - use entity glint vertex on top
			if (renderEnchant) {
				vertex = ItemRenderer.getArmorVertexBuilder(Minecraft.getInstance().getRenderTypeBuffers().getBufferSource(), RenderType.getEntityGlint(), false, true);
				this.actualRender(matrix, vertex, false, lightMapUV, overlayUV, ageInTicks, netHeadYaw, headPitch, scale);
			}
		}

		// If animated, switch to offset models, render animation overlay, then switch
		// back to normal models
		if (alpha > 0 && offsetBipedHead != null) {
			offsetBipedHead.copyModelAngles(bipedHead);
			offsetBipedBody.copyModelAngles(bipedBody);
			offsetBipedRightArm.copyModelAngles(bipedRightArm);
			offsetBipedLeftArm.copyModelAngles(bipedLeftArm);
			offsetBipedRightLeg.copyModelAngles(bipedRightLeg);
			offsetBipedLeftLeg.copyModelAngles(bipedLeftLeg);
			offsetBipedWaist.copyModelAngles(bipedWaist);
			offsetBipedRightFoot.copyModelAngles(bipedRightFoot);
			offsetBipedLeftFoot.copyModelAngles(bipedLeftFoot);
			bipedHead = this.offsetBipedHead;
			bipedBody = this.offsetBipedBody;
			bipedRightArm = this.offsetBipedRightArm;
			bipedLeftArm = this.offsetBipedLeftArm;
			bipedRightLeg = this.offsetBipedRightLeg;
			bipedLeftLeg = this.offsetBipedLeftLeg;
			bipedWaist = this.offsetBipedWaist;
			bipedRightFoot = this.offsetBipedRightFoot;
			bipedLeftFoot = this.offsetBipedLeftFoot;
			this.actualRender(matrix, vertex, true, lightMapUV, overlayUV, ageInTicks, netHeadYaw, headPitch, scale);
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

	/**Do the actual rendering*/
	private void actualRender(MatrixStack matrix, IVertexBuilder vertex, boolean animationOverlay, int lightMapUV,
			int overlayUV, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

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
		Float[] color = CommandDev.devColors.get(this.entity.getUniqueID());
		if (color != null) {
			if (color[0] == 0 && color[1] == 0 && color[2] == 0) { //rainbow
				Color color2 = Color.getHSBColor(this.entity.ticksExisted/30f, 1f, 1f);
				red = color2.getRed()/255f;
				green = color2.getGreen()/255f;
				blue = color2.getBlue()/255f;
			}
			else { //pulse
				double pulse = (Math.cos(this.entity.ticksExisted/5f)+1d)/3d+0.01d;
				red += pulse * color[0];
				green += pulse * color[1];
				blue += pulse * color[2];
			}
		}

		matrix.push();

		if (this.isChild) {
			matrix.push();         
			float num;
			if (true) {
				num = 1.5F / 2f;
				matrix.scale(num, num, num);
			}
			matrix.translate(0.0D, (double)(16.0f / 16.0F), (double)(0f / 16.0F));
			this.bipedHead.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			matrix.pop();

			matrix.push();
			num = 1.0F / 2f;
			matrix.scale(num, num, num);
			matrix.translate(0.0D, (double)(24f / 16.0F), 0.0D);
			this.bipedBody.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			this.bipedRightArm.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			this.bipedLeftArm.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			this.bipedWaist.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			this.bipedRightLeg.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			this.bipedLeftLeg.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			this.bipedRightFoot.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			this.bipedLeftFoot.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			matrix.pop();
		} else {
			this.bipedHead.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			this.bipedBody.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			this.bipedRightArm.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			this.bipedLeftArm.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			this.bipedWaist.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			this.bipedRightLeg.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			this.bipedLeftLeg.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			this.bipedRightFoot.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
			this.bipedLeftFoot.render(matrix, vertex, lightMapUV, overlayUV, red, green, blue, animationOverlay ? alpha : 1.0f);
		}

		matrix.pop();
	}

	@Override
	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {
		// offset slightly to prevent z-fighting
		this.bipedLeftLeg.rotateAngleX -= 0.001f;
		bipedWaist.copyModelAngles(bipedBody);
		bipedLeftFoot.copyModelAngles(bipedLeftLeg);
		bipedRightFoot.copyModelAngles(bipedRightLeg);
	}
}