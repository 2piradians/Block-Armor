package twopiradians.blockArmor.client;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLiquid;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TextureManager {

	/**Map of valid ItemStacks to their textures*/
	private static HashMap<ItemStack, ArrayList<ResourceLocation>> map;

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

		//get textures here
		
		ArrayList<ResourceLocation> textures = new ArrayList<ResourceLocation>();
		textures.add(new ResourceLocation(domain, "textures/" + helmetTexture.replaceAll("\"", "") + ".png"));
		textures.add(new ResourceLocation(domain, "textures/" + chestTexture.replaceAll("\"", "") + ".png"));
		textures.add(new ResourceLocation(domain, "textures/" + legTexture.replaceAll("\"", "") + ".png"));
		textures.add(new ResourceLocation(domain, "textures/" + bootTexture.replaceAll("\"", "") + ".png"));
		map.put(stack, textures);
		return textures;
	}
}
