package twopiradians.blockArmor.client.model;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelPlane extends ModelBox {

	private TexturedQuad quad;

	public ModelPlane(ModelRenderer renderer, int textureX, int textureY, float offX, float offY, float offZ, int width, int height, int depth)
	{
		super(renderer, textureX, textureY, offX, offY, offZ, width, height, depth, 0, false);

		PositionTextureVertex vertex1 = null;
		PositionTextureVertex vertex2 = null;
		PositionTextureVertex vertex3 = null;
		PositionTextureVertex vertex4 = null;
		
		if (width == 0) {
			vertex1 = new PositionTextureVertex(offX, offY + (float)height, offZ + (float)depth, 0.0F, 8.0F);//6
			vertex2 = new PositionTextureVertex(offX, offY, offZ + (float)depth, 0.0F, 0.0F);//3
			vertex3 = new PositionTextureVertex(offX, offY, offZ, 0.0F, 0.0F);//7
			vertex4 = new PositionTextureVertex(offX, offY + (float)height, offZ, 0.0F, 8.0F);//2
			this.quad = new TexturedQuad(new PositionTextureVertex[] {vertex1, vertex2, vertex3, vertex4}, textureX, textureY + depth, textureX + height, textureY + depth + depth, renderer.textureWidth, renderer.textureHeight);//???
		}
		else if (height == 0) {
			vertex1 = new PositionTextureVertex(offX + (float)width, offY, offZ + (float)depth, 0.0F, 8.0F);//4
			vertex2 = new PositionTextureVertex(offX, offY, offZ + (float)depth, 0.0F, 0.0F);//3
			vertex3 = new PositionTextureVertex(offX, offY, offZ, 0.0F, 0.0F);//7                                                             //width7 height8 depth9
			vertex4 = new PositionTextureVertex(offX + (float)width, offY, offZ, 0.0F, 8.0F);//0
			this.quad = new TexturedQuad(new PositionTextureVertex[] {vertex1, vertex2, vertex3, vertex4}, textureX + depth, textureY, textureX + depth + width, textureY + depth, renderer.textureWidth, renderer.textureHeight);//2
		}
		else if (depth == 0) {
			vertex1 = new PositionTextureVertex(offX + (float)width, offY + (float)height, offZ, 0.0F, 8.0F);//1
			vertex2 = new PositionTextureVertex(offX, offY + (float)height, offZ, 0.0F, 0.0F);//2
			vertex3 = new PositionTextureVertex(offX, offY, offZ, 0.0F, 0.0F);//7
			vertex4 = new PositionTextureVertex(offX + (float)width, offY, offZ, 0.0F, 8.0F);//0
			this.quad = new TexturedQuad(new PositionTextureVertex[] {vertex1, vertex2, vertex3, vertex4}, textureX + depth, textureY + depth, textureX + depth + width, textureY + depth + height, renderer.textureWidth, renderer.textureHeight);//4
		}

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(VertexBuffer renderer, float scale)
	{
		quad.draw(renderer, scale);
	}
}
