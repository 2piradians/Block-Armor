package twopiradians.blockArmor.client.render.item;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ItemModelMesherForge;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import twopiradians.blockArmor.client.render.x3d.X3dModel;

public class RenderArmor implements LayerRenderer<EntityPlayer>
{
	X3dModel                          chest;
	X3dModel                          helmet;
	X3dModel                          leftFoot;
	X3dModel                          leftLeg;
	X3dModel                          leftShoulder;
	X3dModel                          rightFoot;
	X3dModel                          rightLeg;
	X3dModel                          rightShoulder;
	X3dModel                          waist;

	ResourceLocation                  block = new ResourceLocation("minecraft", "textures/blocks/bedrock.png");
	ResourceLocation                  block2 = new ResourceLocation("minecraft", "textures/blocks/dirt.png");
	ResourceLocation                  block3 = new ResourceLocation("minecraft", "textures/blocks/glass.png");
	ResourceLocation                  block4 = new ResourceLocation("minecraft", "textures/blocks/stone.png");
	ResourceLocation                  block5 = new ResourceLocation("minecraft", "textures/blocks/gravel.png");


	private final RenderLivingBase<?> livingEntityRenderer;
	IdentityHashMap<Item, TIntObjectHashMap<ModelResourceLocation>> locations = Maps.newIdentityHashMap();
	/**Used to prevent s.o.p spam during testing*/
	private ItemStack lastReportedStack;
	/**Set in event during initialization*/
	public static ModelLoader modelLoader;

	public RenderArmor(RenderLivingBase<?> livingEntityRendererIn)
	{
		ItemModelMesherForge itemModelMesher = ReflectionHelper.getPrivateValue(RenderItem.class, Minecraft.getMinecraft().getRenderItem(), 3);
		locations = ReflectionHelper.getPrivateValue(ItemModelMesherForge.class, itemModelMesher, 0);

		this.livingEntityRenderer = livingEntityRendererIn;
		chest = new X3dModel(new ResourceLocation("blockarmor:models/Chest.x3d"));
		helmet = new X3dModel(new ResourceLocation("blockarmor:models/Helmet.x3d"));
		leftFoot = new X3dModel(new ResourceLocation("blockarmor:models/LeftFoot.x3d"));
		leftLeg = new X3dModel(new ResourceLocation("blockarmor:models/LeftLeg.x3d"));
		leftShoulder = new X3dModel(new ResourceLocation("blockarmor:models/LeftShoulder.x3d"));
		rightFoot = new X3dModel(new ResourceLocation("blockarmor:models/RightFoot.x3d"));
		rightLeg = new X3dModel(new ResourceLocation("blockarmor:models/RightLeg.x3d"));
		rightShoulder = new X3dModel(new ResourceLocation("blockarmor:models/RightShoulder.x3d"));
		waist = new X3dModel(new ResourceLocation("blockarmor:models/Waist.x3d"));
	}

	public class ModelLoaderEvent 
	{
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(ModelBakeEvent event) 
		{
			RenderArmor.modelLoader = event.getModelLoader();
		}
	}
	
	public void doRenderLayer(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, 
			float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{	
		String blockPath = null;
		String blockDomain = null;
		if(player.getHeldItemMainhand() == null || !(player.getHeldItemMainhand().getItem() instanceof ItemBlock))	
			return;

		Item item = player.getHeldItemMainhand().getItem();
		ItemStack stack = player.getHeldItemMainhand();
		Block block = ((ItemBlock)item).getBlock();
		IBlockState state = block.getStateFromMeta(stack.getMetadata());
		int meta = stack.getMetadata();

		ResourceLocation textureResourceLocation = null;

		//finding location of json
		String resourcePath = locations.get(item).get(meta).toString().replaceAll("#inventory", "").replaceAll("minecraft:", ""); //possibly better way of getting?
		ResourceLocation loc = Item.REGISTRY.getNameForObject(item); 
		loc = new ResourceLocation(loc.getResourceDomain(), "models/block/" + resourcePath + ".json");

		//see if json exists at loc
		IResource iresource = null;
		try {
			iresource = Minecraft.getMinecraft().getResourceManager().getResource(loc);
		} catch (IOException e) {
			if (stack != this.lastReportedStack)
				System.out.println("Bad location: " + loc);
			return;
		}
		Reader reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);
		JsonObject object = Streams.parse(new JsonReader(reader)).getAsJsonObject();

		//get textures from json - from ModelBlock.getTextures()
		ArrayList<String> textureLocations = new ArrayList<String>();

		if (object.has("textures"))
		{
			JsonObject jsonobject = object.getAsJsonObject("textures");
			for (Entry<String, JsonElement> entry : jsonobject.entrySet())
				textureLocations.add(entry.getValue().getAsString());
		}

		if (!textureLocations.isEmpty()) 
			textureResourceLocation = new ResourceLocation(loc.getResourceDomain(), "textures/" + textureLocations.get(0) + ".png");			


		//Guessing texture location
		/*Map<Item, List<String>> variantNames = ReflectionHelper.getPrivateValue(ModelBakery.class, RenderArmor.modelLoader, 21);
		List<String> list = (List)variantNames.get(item);
		if (list == null)
			list = Collections.<String>singletonList(((ResourceLocation)Item.REGISTRY.getNameForObject(item)).toString());
		ResourceLocation resourcelocation;
		if (list.size() > stack.getMetadata())
			resourcelocation = new ResourceLocation(list.get(stack.getMetadata()).replaceAll("#.*", ""));
		else
			resourcelocation = new ResourceLocation(list.get(0).replaceAll("#.*", ""));
		blockPath = resourcelocation.getResourcePath();
		blockDomain = resourcelocation.getResourceDomain();
		ResourceLocation textureResourceLocation = null;
		ArrayList<ResourceLocation> locs = new ArrayList<ResourceLocation>();
		locs.add(new ResourceLocation(blockDomain, "textures/blocks/" + blockPath + ".png"));
		locs.add(new ResourceLocation(blockDomain, "textures/blocks/" + blockPath + meta + ".png"));
		locs.add(new ResourceLocation(blockDomain, "textures/blocks/" + blockPath + "_" + meta + ".png"));
		locs.add(new ResourceLocation(blockDomain, "textures/" + blockPath + ".png"));
		locs.add(new ResourceLocation(blockDomain, "textures/" + blockPath + meta + ".png"));
		locs.add(new ResourceLocation(blockDomain, "textures/" + blockPath + "_" + meta + ".png"));
		for (ResourceLocation loc : locs) {
			try {
				Minecraft.getMinecraft().getResourceManager().getResource(loc);
				textureResourceLocation = loc;
				if (this.lastReportedStack != player.getHeldItemMainhand())
					System.out.println("Good: "+textureResourceLocation);
				break;
			}
			catch (Exception e){
				if (this.lastReportedStack != player.getHeldItemMainhand())
					System.out.println("Bad: "+loc);
			}
		}*/
		
		this.lastReportedStack = player.getHeldItemMainhand();

		if (textureResourceLocation == null)
			return;

		this.livingEntityRenderer.bindTexture(textureResourceLocation);
		if (player.getItemStackFromSlot(EntityEquipmentSlot.HEAD) == null)
		{
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
			helmet.renderAll();
			GL11.glPopMatrix();
			GlStateManager.cullFace(GlStateManager.CullFace.BACK);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			GL11.glPopMatrix();
		}
		if (player.getItemStackFromSlot(EntityEquipmentSlot.CHEST) == null)
		{
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
			chest.renderAll();
			GL11.glPopMatrix();
			// Second pass with colour.
			GL11.glPushMatrix();
			GL11.glRotated(180, 1, 0, 0);
			GL11.glTranslatef(dx, dy - 0.25f, dz + 0.6f);
			GL11.glScalef(1.2f*s, 1.2f*s, 1.2f*s);
			leftShoulder.renderAll();
			GL11.glColor3f(1, 1, 1);
			GL11.glPopMatrix();
			// Third pass with colour.
			GL11.glPushMatrix();
			GL11.glRotated(180, 1, 0, 0);
			GL11.glTranslatef(dx, dy - 0.25f, dz + 0.6f);
			GL11.glScalef(1.2f*s, 1.2f*s, 1.2f*s);
			rightShoulder.renderAll();
			GL11.glColor3f(1, 1, 1);
			GL11.glPopMatrix();
			GlStateManager.cullFace(GlStateManager.CullFace.BACK);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			GL11.glPopMatrix();
		}
		if (player.getItemStackFromSlot(EntityEquipmentSlot.LEGS) == null)
		{
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
			waist.renderAll();
			GL11.glPopMatrix();
			// Second pass
			GL11.glPushMatrix();
			GL11.glRotated(180, 0, 0, 0);
			GL11.glTranslatef(dx, dy - 1.05f, dz);
			GL11.glScalef(s, s, s);
			leftLeg.renderAll();
			GL11.glColor3f(1, 1, 1);
			GL11.glPopMatrix();
			// Third pass
			GL11.glPushMatrix();
			GL11.glRotated(180, 0, 0, 0);
			GL11.glTranslatef(dx, dy - 0.82f, dz);
			GL11.glScalef(s, s, 1.005f*s);
			rightLeg.renderAll();
			GL11.glColor3f(1, 1, 1);
			GL11.glPopMatrix();
			GlStateManager.cullFace(GlStateManager.CullFace.BACK);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			GL11.glPopMatrix();
		}
		if (player.getItemStackFromSlot(EntityEquipmentSlot.FEET) == null)
		{
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
			leftFoot.renderAll();
			GL11.glPopMatrix();
			// Second pass
			GL11.glPushMatrix();
			GL11.glRotated(180, 0, 0, 0);
			GL11.glTranslatef(dx, dy - 1.55f, dz);
			GL11.glScalef(s, s, 1.01f*s);
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