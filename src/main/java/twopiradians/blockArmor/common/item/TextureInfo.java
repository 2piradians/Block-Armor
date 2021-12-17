package twopiradians.blockArmor.common.item;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite.AnimatedTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite.FrameInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@OnlyIn(Dist.CLIENT)
public class TextureInfo {

	/**Quad's texture (or missing texture if invalid/missing)*/
	public TextureAtlasSprite sprite;
	/**Quad's color (or -1 if none)*/
	public int color;
	@Nullable
	public AnimatedTexture animatedTexture;
	/**Original height of sprite (before it recalculates sprite with extra frames, like Prismarine)*/
	public int originalHeight;

	private static final Field FRAME_FIELD = ObfuscationReflectionHelper.findField(TextureAtlasSprite.AnimatedTexture.class, "f_174748_");
	private static final Field SUB_FRAME_FIELD = ObfuscationReflectionHelper.findField(TextureAtlasSprite.AnimatedTexture.class, "f_174749_");
	private static final Field FRAMES_FIELD = ObfuscationReflectionHelper.findField(TextureAtlasSprite.AnimatedTexture.class, "f_174750_");
	private static final Field INDEX_FIELD = ObfuscationReflectionHelper.findField(TextureAtlasSprite.FrameInfo.class, "f_174771_");
	private static final Field TIME_FIELD = ObfuscationReflectionHelper.findField(TextureAtlasSprite.FrameInfo.class, "f_174772_");
	private static final Field MAIN_IMAGE_FIELD = ObfuscationReflectionHelper.findField(TextureAtlasSprite.class, "f_118342_");

	protected TextureInfo(@Nullable TextureAtlasSprite sprite, int color, @Nullable AnimatedTexture animatedTexture) {
		this.sprite = sprite == null ? ArmorSet.missingSprite : sprite;
		this.color = color;
		this.animatedTexture = animatedTexture;
		try {
			this.originalHeight = ((NativeImage[]) MAIN_IMAGE_FIELD.get(this.sprite))[0].getHeight();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**Returns current alpha for animation overlay*/
	public float getAlpha() {
		if (this.animatedTexture != null) {
			try {
				int frame = this.getCurrentAnimationFrame();
				int subframe = SUB_FRAME_FIELD.getInt(this.animatedTexture);
				List<TextureAtlasSprite.FrameInfo> frames = (List<FrameInfo>) FRAMES_FIELD.get(this.animatedTexture);
				int time = TIME_FIELD.getInt(frames.get(frame));
				float alpha = (float) subframe / (float) time;
        		return alpha;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	/**Returns current animation frame*/
	public int getCurrentAnimationFrame() {
		if (this.animatedTexture != null)
			try {
				List<TextureAtlasSprite.FrameInfo> frames = (List<FrameInfo>) FRAMES_FIELD.get(this.animatedTexture);
				int currentFrame = FRAME_FIELD.getInt(this.animatedTexture);
				return INDEX_FIELD.getInt(frames.get(currentFrame));
			} catch (Exception e) {
				e.printStackTrace();
			}
		return 0;
	}

	/**Returns next animation frame*/
	public int getNextAnimationFrame() { 
		if (this.animatedTexture != null) {
			try {
				List<TextureAtlasSprite.FrameInfo> frames = (List<FrameInfo>) FRAMES_FIELD.get(this.animatedTexture);
				int currentFrame = FRAME_FIELD.getInt(this.animatedTexture)+1;
				if (currentFrame >= frames.size())
					currentFrame = 0;
				return INDEX_FIELD.getInt(frames.get(currentFrame));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

}