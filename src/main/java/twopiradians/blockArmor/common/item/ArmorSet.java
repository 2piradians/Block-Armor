package twopiradians.blockArmor.common.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;

import com.google.common.collect.Maps;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ItemModelMesherForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ArmorSet {

	public static ArrayList<ArmorSet> allSets;
	public static final ArrayList<ArmorSet> SETS_WITH_EFFECTS = new ArrayList<ArmorSet>() {{
		add(new ArmorSet(new ItemStack(Blocks.STONE, 1, 1), true));//TODO add all sets with effects
	}};

	/**Map of valid ItemStacks to their textures - used for efficiency*/
	private static HashMap<ItemStack, ArrayList<ResourceLocation>> itemStackTextureMap = Maps.newHashMap();
	/**Reflected value used to find textures*/
	private static ItemModelMesherForge itemModelMesher;
	/**Reflected value used to iterate through all items*/
	private static IdentityHashMap<Item, TIntObjectHashMap<ModelResourceLocation>> locations;

	public ItemStack stack;
	public ItemBlock item;
	public int meta;
	public Block block;
	public ArmorMaterial material;      
	public boolean hasSetEffect;
	public ItemModArmor helmet;
	public ItemModArmor chestplate;
	public ItemModArmor leggings;
	public ItemModArmor boots;
	
	public ArmorSet(ItemStack stack, boolean hasSetEffect) {
		this.stack = stack;
		this.item = (ItemBlock) stack.getItem();
		this.meta = stack.getMetadata();
		this.block = item.getBlock();
		this.hasSetEffect = hasSetEffect;
		//TODO modifiy material based on block
		this.material = EnumHelper.addArmorMaterial("poliwag", "blockarmor:poliwag", 2, new int[] {1,2,3, 1}, 10, null, 0);
	}
	
	/**Creates ArmorSets for each valid registered item and puts them in allSets*/
	public static void postInit() {
		//initialize reflected fields
		itemModelMesher = ReflectionHelper.getPrivateValue(RenderItem.class, Minecraft.getMinecraft().getRenderItem(), 3);
		locations = ReflectionHelper.getPrivateValue(ItemModelMesherForge.class, itemModelMesher, 0);

		allSets = new ArrayList<ArmorSet>();
		allSets.addAll(SETS_WITH_EFFECTS);
		for (Item item : locations.keySet()) //iterate through all items and meta and create sets for valid ones
			for (int meta : locations.get(item).keys())
				if (isValid(new ItemStack(item, 1, meta)) && ArmorSet.getSet(item, meta) == null)
					allSets.add(new ArmorSet(new ItemStack(item, 1, meta), false));

		System.out.println("[Block Armor] "+allSets.size()+" armor sets created."); //TODO replace with logger
		for (ArmorSet set : allSets) 
			System.out.println("- "+set.stack.getDisplayName());
	}
	
	/**Returns armor set corresponding to given item and meta, or null if none exists*/
	public static ArmorSet getSet(Item item, int meta) {
		for (ArmorSet set : allSets)
			if (set.item == item && set.meta == meta)
				return set;
		return null;
	}
	
	/**Returns armor set containing given ItemModArmor, or null if none exists*/
	public static ArmorSet getSet(ItemModArmor item) {
		for (ArmorSet set : allSets)
			if (set.helmet == item || set.chestplate == item || set.leggings == item || set.boots == item)
				return set;
		return null;
	}

	/**Should an armor set be made from this item*/
	private static boolean isValid(ItemStack stack) {
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
	 * @return ArrayList of ResourceLocations for helmet, chestplate, leggings, and boots*/
	@SideOnly(Side.CLIENT)
	public static ArrayList<ResourceLocation> getTextures(ItemStack stack) {
		if (itemStackTextureMap.containsKey(stack))
			return itemStackTextureMap.get(stack);

		if (!isValid(stack))
			return null;

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
		itemStackTextureMap.put(stack, textures);

		return textures;
	}
}
