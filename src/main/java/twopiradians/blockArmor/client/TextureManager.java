package twopiradians.blockArmor.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ItemModelMesherForge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TextureManager {

	/**Map of valid ItemStacks to their textures - used for efficiency*/
	private static HashMap<ItemStack, ArrayList<ResourceLocation>> map = Maps.newHashMap();
	/**Reflected value used to find textures*/
	private static ItemModelMesherForge itemModelMesher;

	/**Should an armor set be made from this item*/
	public static boolean isValid(ItemStack stack) {
		if (stack == null || !(stack.getItem() instanceof ItemBlock))
			return false;

		Block block = ((ItemBlock)stack.getItem()).getBlock();
		if (block instanceof BlockLiquid || block instanceof BlockContainer)
			return false;

		//Check if full block (requires player) (possibly check if block's model is for a full block? - accept only parent: block/cube*)
		/*ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		block.addCollisionBoxToList(block.getDefaultState(), player.worldObj, new BlockPos(0,0,0), Block.FULL_BLOCK_AABB, list, player);
		if (list.size() != 1 || !list.get(0).equals(Block.FULL_BLOCK_AABB)) {
			System.out.println("Not full block");
			return false;
		}*/

		return true;
	}

	/**Gets item's textures if valid, otherwise returns null
	 * 
	 * @return ArrayList of helmet, chestplate, leggings, and boot ResourceLocations*/
	@SideOnly(Side.CLIENT)
	public static ArrayList<ResourceLocation> getTextures(ItemStack stack) {
		if (map.containsKey(stack))
			return map.get(stack);

		if (!isValid(stack))
			return null;

		if (itemModelMesher == null)
			itemModelMesher = ReflectionHelper.getPrivateValue(RenderItem.class, Minecraft.getMinecraft().getRenderItem(), 3);

		ResourceLocation helmetTexture = null;
		ResourceLocation chestTexture = null;
		ResourceLocation leggingsTexture = null;
		ResourceLocation bootsTexture = null;

		//Gets textures from item model's BakedQuads (textures for each side)
		IBlockState state = ((ItemBlock) stack.getItem()).getBlock().getDefaultState();
		List<BakedQuad> list = new ArrayList<BakedQuad>();
		list.addAll(itemModelMesher.getItemModel(stack).getQuads(state, null, 0));
		for (EnumFacing facing : EnumFacing.VALUES)
			list.addAll(itemModelMesher.getItemModel(stack).getQuads(state, facing, 0));
		if (list.size() < 6)
			System.out.println("less than 6 textures! - I did not expect this!"); //TODO remove after testing
		for (BakedQuad quad : list) { //there's at least one texture per face
			switch (quad.getFace()) {
			case DOWN:
				if (bootsTexture != null)
					break;
				bootsTexture = new ResourceLocation(quad.getSprite().getIconName());
				bootsTexture = new ResourceLocation(bootsTexture.getResourceDomain(), "textures/"+bootsTexture.getResourcePath()+".png");
				break;
			case EAST:
				break;
			case NORTH:
				if (chestTexture != null)
					break;
				chestTexture = new ResourceLocation(quad.getSprite().getIconName());
				chestTexture = new ResourceLocation(chestTexture.getResourceDomain(), "textures/"+chestTexture.getResourcePath()+".png");
				break;
			case SOUTH:
				if (leggingsTexture != null)
					break;
				leggingsTexture = new ResourceLocation(quad.getSprite().getIconName());
				leggingsTexture = new ResourceLocation(leggingsTexture.getResourceDomain(), "textures/"+leggingsTexture.getResourcePath()+".png");
				break;
			case UP:
				if (helmetTexture != null)
					break;
				helmetTexture = new ResourceLocation(quad.getSprite().getIconName());
				helmetTexture = new ResourceLocation(helmetTexture.getResourceDomain(), "textures/"+helmetTexture.getResourcePath()+".png");
				break;
			case WEST:
				break;
			}
		}

		if (helmetTexture == null || chestTexture == null || leggingsTexture == null || bootsTexture == null) {
			System.out.println("null texture - this shouldn't happen!"); //TODO remove after testing
			return null;
		}

		ArrayList<ResourceLocation> textures = new ArrayList<ResourceLocation>();
		textures.add(helmetTexture);
		textures.add(chestTexture);
		textures.add(leggingsTexture);
		textures.add(bootsTexture);
		map.put(stack, textures);
		return textures;
	}
}
