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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLiquid;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
		if(player.getHeldItemMainhand() == null || !(player.getHeldItemMainhand().getItem() instanceof ItemBlock) 
				|| ((ItemBlock)player.getHeldItemMainhand().getItem()).getBlock() instanceof BlockLiquid
				|| ((ItemBlock)player.getHeldItemMainhand().getItem()).getBlock() instanceof BlockContainer)
			return;

		try
		{
			if(!((ItemBlock)player.getHeldItemMainhand().getItem()).getBlock().getBoundingBox(((ItemBlock)player.getHeldItemMainhand().getItem()).getBlock().getDefaultState(), 
					player.worldObj, new BlockPos(0,0,0)).equals(Block.FULL_BLOCK_AABB))
				return;
		}
		catch(Exception e){return;}

		Item item = player.getHeldItemMainhand().getItem();
		ItemStack stack = player.getHeldItemMainhand();
		Block block = ((ItemBlock)item).getBlock();
		IBlockState state = block.getStateFromMeta(stack.getMetadata());
		int meta = stack.getMetadata();

		ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		block.addCollisionBoxToList(block.getDefaultState(), Minecraft.getMinecraft().theWorld, new BlockPos(0,0,0), Block.FULL_BLOCK_AABB, list, Minecraft.getMinecraft().thePlayer);
		if (!(list.size() == 1 && list.get(0).equals(Block.FULL_BLOCK_AABB)))
		{
			return;
		}

		//finding location of item json
		ResourceLocation loc1 = locations.get(item).get(meta);
		ResourceLocation loc = new ResourceLocation(loc1.getResourceDomain(), "models/item/" + loc1.getResourcePath() + ".json");

		//used to check if should use models/block or blockstate for model
		boolean checkBlockstate = false;

		//see if item json exists at loc
		IResource iresourceItem = null;
		try 
		{
			iresourceItem = Minecraft.getMinecraft().getResourceManager().getResource(loc);
		} 
		catch (Exception e) 
		{
			//if (stack != this.lastReportedStack)
			//System.out.println("Bad item location: " + loc);
			//return;
			checkBlockstate = true;
		}

		ArrayList<String> blockLocations = new ArrayList<String>();
		IResource iresource = null;

		if(!checkBlockstate) //read jsons through models/block normally
		{		
			//read item json
			Reader readerItem = new InputStreamReader(iresourceItem.getInputStream(), Charsets.UTF_8);
			JsonObject objectItem = Streams.parse(new JsonReader(readerItem)).getAsJsonObject();
			if (objectItem.has("parent"))
			{
				JsonElement jsonobject = objectItem.get("parent");
				blockLocations.add(jsonobject.toString().replaceAll("\"", ""));
			}

			//get location of block json from models
			if(!blockLocations.isEmpty())
				loc = new ResourceLocation(loc1.getResourceDomain(), "models/" + blockLocations.get(0) + ".json");

			//see if block json exists at loc
			try
			{
				iresource = Minecraft.getMinecraft().getResourceManager().getResource(loc);
			} 
			catch (IOException e) 
			{
				if (stack != this.lastReportedStack)
					System.out.println("Bad block location in models: " + loc);
				return;
			}
		}
		else //read jsons in blockstates instead
		{
			//get location of block json from blockstates
			loc = new ResourceLocation(loc.getResourceDomain(), "blockstates/" + loc1.getResourcePath() + ".json");

			//see if block json exists at loc
			try 
			{
				iresource = Minecraft.getMinecraft().getResourceManager().getResource(loc);
			} 
			catch (Exception e) 
			{
				if (stack != this.lastReportedStack)
					System.out.println("Bad block location in blockstates: " + loc);
				return;
			} 
		}

		Reader reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);
		JsonObject object = Streams.parse(new JsonReader(reader)).getAsJsonObject();
		JsonObject jsonobject = null;
		boolean variants = false;

		//get textures from json - from ModelBlock.getTextures()
		ArrayList<String> textureLocations = new ArrayList<String>();

		if (object.has("textures"))
		{
			jsonobject = object.getAsJsonObject("textures");
			for (Entry<String, JsonElement> entry : jsonobject.entrySet())
				textureLocations.add(entry.getValue().getAsString());
		}
		else if (object.has("variants"))
		{
			JsonElement jsonelement = object.get("variants");
			System.out.println(jsonelement);
		}

		if(jsonobject == null)
		{
			System.out.println("No textures");
			return;
		}

		String helmetTexture = null;
		String chestTexture = null;
		String legTexture = null;
		String feetTexture = null;

		JsonElement jsonelement = null;

		if(!variants && jsonobject.has("textures"))
		{
			helmetTexture = jsonobject.get("textures").toString();
			chestTexture = jsonobject.get("textures").toString();
			legTexture = jsonobject.get("textures").toString();
			feetTexture = jsonobject.get("textures").toString();
		}

		if(!variants)
		{
			if(jsonobject.has("all"))
				helmetTexture = jsonobject.get("all").toString();
			else if(jsonobject.has("end"))
				helmetTexture = jsonobject.get("end").toString();
			else if(jsonobject.has("up"))
				helmetTexture = jsonobject.get("up").toString();
			else if(jsonobject.has("top"))
				helmetTexture = jsonobject.get("top").toString();
			else if(jsonobject.has("side"))
				helmetTexture = jsonobject.get("side").toString();

			if(jsonobject.has("all"))
				chestTexture = jsonobject.get("all").toString();
			else if(jsonobject.has("front"))
				chestTexture = jsonobject.get("front").toString();
			else if(jsonobject.has("side"))
				chestTexture = jsonobject.get("side").toString();
			else if(jsonobject.has("north"))
				chestTexture = jsonobject.get("north").toString();
			else if(jsonobject.has("south"))
				chestTexture = jsonobject.get("south").toString();
			else if(jsonobject.has("east"))
				chestTexture = jsonobject.get("east").toString();
			else if(jsonobject.has("west"))
				chestTexture = jsonobject.get("west").toString();

			if(jsonobject.has("all"))
				legTexture = jsonobject.get("all").toString();
			else if(jsonobject.has("side"))
				legTexture = jsonobject.get("side").toString();
			else if(jsonobject.has("north"))
				legTexture = jsonobject.get("north").toString();
			else if(jsonobject.has("south"))
				legTexture = jsonobject.get("south").toString();
			else if(jsonobject.has("east"))
				legTexture = jsonobject.get("east").toString();
			else if(jsonobject.has("west"))
				legTexture = jsonobject.get("west").toString();

			if(jsonobject.has("all"))
				feetTexture = jsonobject.get("all").toString();
			else if(jsonobject.has("end"))
				feetTexture = jsonobject.get("end").toString();
			else if(jsonobject.has("down"))
				feetTexture = jsonobject.get("down").toString();
			else if(jsonobject.has("bottom"))
				feetTexture = jsonobject.get("bottom").toString();
			else if(jsonobject.has("side"))
				feetTexture = jsonobject.get("side").toString();
		}

		if(helmetTexture == null && chestTexture == null && legTexture == null && feetTexture == null)
			return;

		helmetTexture = helmetTexture.replaceAll("\"", "");
		chestTexture = chestTexture.replaceAll("\"", "");
		legTexture = legTexture.replaceAll("\"", "");
		feetTexture = feetTexture.replaceAll("\"", "");

		this.lastReportedStack = player.getHeldItemMainhand();
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
			this.livingEntityRenderer.bindTexture(new ResourceLocation(loc.getResourceDomain(), "textures/" + helmetTexture + ".png"));
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
			this.livingEntityRenderer.bindTexture(new ResourceLocation(loc.getResourceDomain(), "textures/" + chestTexture + ".png"));
			chest.renderAll();
			GL11.glPopMatrix();
			// Second pass with colour.
			GL11.glPushMatrix();
			GL11.glRotated(180, 1, 0, 0);
			GL11.glTranslatef(dx, dy - 0.25f, dz + 0.6f);
			GL11.glScalef(1.2f*s, 1.2f*s, 1.2f*s);
			this.livingEntityRenderer.bindTexture(new ResourceLocation(loc.getResourceDomain(), "textures/" + legTexture + ".png"));
			leftShoulder.renderAll();
			GL11.glColor3f(1, 1, 1);
			GL11.glPopMatrix();
			// Third pass with colour.
			GL11.glPushMatrix();
			GL11.glRotated(180, 1, 0, 0);
			GL11.glTranslatef(dx, dy - 0.25f, dz + 0.6f);
			GL11.glScalef(1.2f*s, 1.2f*s, 1.2f*s);
			this.livingEntityRenderer.bindTexture(new ResourceLocation(loc.getResourceDomain(), "textures/" + legTexture + ".png"));
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
			this.livingEntityRenderer.bindTexture(new ResourceLocation(loc.getResourceDomain(), "textures/" + legTexture + ".png"));
			waist.renderAll();
			GL11.glPopMatrix();
			// Second pass
			GL11.glPushMatrix();
			GL11.glRotated(180, 0, 0, 0);
			GL11.glTranslatef(dx, dy - 1.05f, dz);
			GL11.glScalef(s, s, s);
			this.livingEntityRenderer.bindTexture(new ResourceLocation(loc.getResourceDomain(), "textures/" + legTexture + ".png"));
			leftLeg.renderAll();
			GL11.glColor3f(1, 1, 1);
			GL11.glPopMatrix();
			// Third pass
			GL11.glPushMatrix();
			GL11.glRotated(180, 0, 0, 0);
			GL11.glTranslatef(dx, dy - 0.82f, dz);
			GL11.glScalef(s, s, 1.005f*s);
			this.livingEntityRenderer.bindTexture(new ResourceLocation(loc.getResourceDomain(), "textures/" + legTexture + ".png"));
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
			this.livingEntityRenderer.bindTexture(new ResourceLocation(loc.getResourceDomain(), "textures/" + feetTexture + ".png"));
			leftFoot.renderAll();
			GL11.glPopMatrix();
			// Second pass
			GL11.glPushMatrix();
			GL11.glRotated(180, 0, 0, 0);
			GL11.glTranslatef(dx, dy - 1.55f, dz);
			GL11.glScalef(s, s, 1.01f*s);
			this.livingEntityRenderer.bindTexture(new ResourceLocation(loc.getResourceDomain(), "textures/" + feetTexture + ".png"));
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