/*package twopiradians.blockArmor.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

public class ModelBlockArmorRenderer extends ModelRenderer {

	public ModelBlockArmorRenderer(Model parent, int p_i46358_2_, int p_i46358_3_) {
		super(parent.textureWidth, parent.textureHeight, p_i46358_2_, p_i46358_3_);
	}

	public void addPlane(int textureOffsetX, int textureOffsetY, float x, float y, float z, int width, int height, int depth, boolean flip) {

	}

	@Override
	public void render(MatrixStack matrix, IVertexBuilder vertex, int p_228309_3_, int p_228309_4_, float p_228309_5_, float p_228309_6_, float p_228309_7_, float p_228309_8_) {
		if (this.showModel) {
			if (!this.cubeList.isEmpty() || !this.childModels.isEmpty()) {
				matrix.push();
				this.translateRotate(matrix);
				this.doRender(matrix.getLast(), vertex, p_228309_3_, p_228309_4_, p_228309_5_, p_228309_6_, p_228309_7_, p_228309_8_);
				ObjectListIterator var9 = this.childModels.iterator();

				while(var9.hasNext()) {
					ModelRenderer model = (ModelRenderer)var9.next();
					model.render(matrix, vertex, p_228309_3_, p_228309_4_, p_228309_5_, p_228309_6_, p_228309_7_, p_228309_8_);
				}

				matrix.pop();
			}
		}
	}
	
	private void doRender(Entry p_228306_1_, IVertexBuilder p_228306_2_, int p_228306_3_, int p_228306_4_, float p_228306_5_, float p_228306_6_, float p_228306_7_, float p_228306_8_) {
	      Matrix4f lvt_9_1_ = p_228306_1_.getMatrix();
	      Matrix3f lvt_10_1_ = p_228306_1_.getNormal();
	      ObjectListIterator var11 = this.cubeList.iterator();

	      while(var11.hasNext()) {
	         ModelBox lvt_12_1_ = (ModelBox)var11.next();
	         TexturedQuad[] var13 = ModelBox.func_228311_a_(lvt_12_1_);
	         int var14 = var13.length;

	         for(int var15 = 0; var15 < var14; ++var15) {
	            TexturedQuad lvt_16_1_ = var13[var15];
	            Vector3f lvt_17_1_ = lvt_16_1_.normal.copy();
	            lvt_17_1_.transform(lvt_10_1_);
	            float lvt_18_1_ = lvt_17_1_.getX();
	            float lvt_19_1_ = lvt_17_1_.getY();
	            float lvt_20_1_ = lvt_17_1_.getZ();

	            for(int lvt_21_1_ = 0; lvt_21_1_ < 4; ++lvt_21_1_) {
	               PositionTextureVertex lvt_22_1_ = lvt_16_1_.vertexPositions[lvt_21_1_];
	               float lvt_23_1_ = lvt_22_1_.position.getX() / 16.0F;
	               float lvt_24_1_ = lvt_22_1_.position.getY() / 16.0F;
	               float lvt_25_1_ = lvt_22_1_.position.getZ() / 16.0F;
	               Vector4f lvt_26_1_ = new Vector4f(lvt_23_1_, lvt_24_1_, lvt_25_1_, 1.0F);
	               lvt_26_1_.transform(lvt_9_1_);
	               p_228306_2_.addVertex(lvt_26_1_.getX(), lvt_26_1_.getY(), lvt_26_1_.getZ(), p_228306_5_, p_228306_6_, p_228306_7_, p_228306_8_, lvt_22_1_.textureU, lvt_22_1_.textureV, p_228306_4_, p_228306_3_, lvt_18_1_, lvt_19_1_, lvt_20_1_);
	            }
	         }
	      }

	   }

}*/