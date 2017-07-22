package twopiradians.blockArmor.common.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockSlab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.command.CommandDev;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.seteffect.SetEffect;
import twopiradians.blockArmor.creativetab.BlockArmorCreativeTab;

@SuppressWarnings({ "deprecation" })
public class ArmorSet {
	/**Used to add ItemStacks that will be approved for sets that would otherwise not be valid*/
	private static final ArrayList<ItemStack> MANUALLY_ADDED_SETS;
	static {
		MANUALLY_ADDED_SETS = new ArrayList<ItemStack>() {{
			add(new ItemStack(Items.REEDS));
			add(new ItemStack(Blocks.CACTUS));
			add(new ItemStack(Blocks.SNOW));
			add(new ItemStack(Blocks.DISPENSER));
			add(new ItemStack(Blocks.DROPPER));
			add(new ItemStack(Blocks.BEACON));
			add(new ItemStack(Blocks.FURNACE));
			add(new ItemStack(Blocks.ENCHANTING_TABLE));
			add(new ItemStack(Blocks.COMMAND_BLOCK));
			add(new ItemStack(Blocks.CHAIN_COMMAND_BLOCK));
			add(new ItemStack(Blocks.REPEATING_COMMAND_BLOCK));
			add(new ItemStack(Blocks.BROWN_MUSHROOM_BLOCK));
			add(new ItemStack(Blocks.RED_MUSHROOM_BLOCK));
			add(new ItemStack(Blocks.SOUL_SAND));
			add(new ItemStack(Blocks.ENDER_CHEST));
			//add(new ItemStack(Blocks.CHEST));
			add(new ItemStack(Blocks.NOTEBLOCK));
			//add(new ItemStack(Blocks.JUKEBOX));
		}};
	}
	/**Used to add Items that have overriding textures*/
	public static final ArrayList<Item> TEXTURE_OVERRIDES;
	static {
		TEXTURE_OVERRIDES = new ArrayList<Item>() {{
			add(Items.REEDS);
			add(Item.getItemFromBlock(Blocks.ENDER_CHEST));
			//add(Item.getItemFromBlock(Blocks.CHEST));
		}};
	}
	/**All sets, including disabled sets*/
	public static ArrayList<ArmorSet> allSets;
	/**All sets, mapped by their stack's display name*/
	public static HashMap<String, ArmorSet> nameToSetMap;
	/**Armor slots*/
	public static final EntityEquipmentSlot[] SLOTS = new EntityEquipmentSlot[] 
			{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

	/**Used to get textures, set recipe, and as repair material*/
	public ItemStack stack;
	public Item item;
	public int meta;
	public Block block;
	public ArmorMaterial material;      
	public ItemBlockArmor helmet;
	public ItemBlockArmor chestplate;
	public ItemBlockArmor leggings;
	public ItemBlockArmor boots;
	public boolean isFromModdedBlock;
	public ArrayList<SetEffect> setEffects;
	public String modid;
	/**should only be modified through enable() and disable(); enabled = in tab and has recipe*/
	private boolean enabled;
	public ArrayList<IRecipe> recipes;
	/**Only changed on client*/
	public boolean missingTextures; 

	@SideOnly(Side.CLIENT)
	public boolean isTranslucent;
	/**Array of block's textures sorted by EntityEquipmentSlot id*/
	@SideOnly(Side.CLIENT)
	private TextureAtlasSprite[] sprites;
	/**Array of TextureAtlasSprite's animation (or null if not animated) sorted by EntityEquipmentSlot id*/
	@SideOnly(Side.CLIENT)
	public AnimationMetadataSection[] animations;
	/**Array of TextureAtlasSprite's current frame number (including decimals between frames) sorted by EntityEquipmentSlot id*/
	@SideOnly(Side.CLIENT)
	public float[] frames;
	/**Array of quad's color (or -1 if none) sorted by EntityEquipmentSlot id*/
	@SideOnly(Side.CLIENT)
	private int[] colors;
	/**Minecraft's default missing texture sprite, assigned in initTextures()*/
	@SideOnly(Side.CLIENT)
	private static TextureAtlasSprite missingSprite;

	public ArmorSet(ItemStack stack) {
		this.stack = stack;
		this.item = stack.getItem();
		try {
			ResourceLocation loc = (ResourceLocation)Item.REGISTRY.getNameForObject(this.item);
			if (!loc.getResourceDomain().equals("minecraft"))
				isFromModdedBlock = true;
			this.modid = loc.getResourceDomain().toLowerCase();
		}
		catch (Exception e) {
			this.modid = "???";
			isFromModdedBlock = true;
		}
		this.meta = stack.getMetadata();
		if (item == Items.REEDS)
			this.block = Blocks.REEDS;
		else
			this.block = ((ItemBlock) item).getBlock();
		//calculate values for and set material
		float blockHardness = 0; 
		double durability = 5;
		float toughness = 0;
		int enchantability = 12;

		try {
			blockHardness = this.block.getBlockHardness(this.block.getDefaultState(), null, new BlockPos(0,0,0));
		} catch(Exception e) {
			blockHardness = ReflectionHelper.getPrivateValue(Block.class, this.block, 11); //blockHardness
		}
		if (blockHardness == -1) {
			durability = 0;
			blockHardness = 1000;
		}
		else
			durability = 2 + 8* Math.log(blockHardness + 1);
		if (blockHardness > 10)
			toughness = Math.min(blockHardness / 10F, 10);
		durability = Math.min(30, durability);
		//blockHardness = (float) Math.log(blockHardness+1.5D)+1;
		int reductionHelmetBoots = (int) ((Math.min(Math.floor(Math.log10(Math.pow(blockHardness, 2)+1)+1.6D), 3) + 4) / 2 - 1 + Math.min(blockHardness, 1));
		int reductionChest = (int) ((Math.min(blockHardness + 1, 8) + 4) / 2 - 1 + Math.min(blockHardness, 1));
		int reductionLegs = (int) ((Math.max(reductionChest - 2, reductionHelmetBoots) + 4) / 2 - 1 + Math.min(blockHardness, 1));
		int[] reductionAmounts = new int[] {reductionHelmetBoots, reductionLegs, reductionChest, reductionHelmetBoots};
		this.material = EnumHelper.addArmorMaterial(getItemStackDisplayName(stack, null)+" Material", "", 
				(int) durability, reductionAmounts, enchantability, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, toughness);
		this.material.customCraftingMaterial = item;
		//BlockArmor.logger.info(getItemStackDisplayName(stack, null)+": blockHardness = "+blockHardness+", toughness = "+toughness+", durability = "+durability);

		CommandDev.addBlockName(this);
	}

	/**Returns armor item for slot*/
	public ItemBlockArmor getArmorForSlot(EntityEquipmentSlot slot) {
		switch(slot) {
		case HEAD:
			return helmet;
		case CHEST:
			return chestplate;
		case LEGS:
			return leggings;
		case FEET:
			return boots;
		default:
			return null;
		}
	}

	/**Creates ArmorSets for each valid registered item and puts them in allSets*/
	public static void postInit() {
		//create list of all ItemStacks with different display names and list of the display names
		ArrayList<String> displayNames = new ArrayList<String>();
		Block[] blocks = Iterators.toArray(Block.REGISTRY.iterator(), Block.class);
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		for (Block block : blocks) {
			for (int i=0; i<16; i++)
				try {
					ItemStack stack = block == Blocks.REEDS ? new ItemStack(Items.REEDS, 1, i) : new ItemStack(block, 1, i);

					boolean manuallyAdded = false;
					for (ItemStack manualStack : MANUALLY_ADDED_SETS)
						if (stack != null && stack.getItem() == manualStack.getItem() && stack.getMetadata() == manualStack.getMetadata())
							manuallyAdded = true;

					if (block.equals(Blocks.LOG) && i > 3) //logs after meta 3 are in log2
						break;
					if (manuallyAdded || (stack != null && stack.getItem() != null && !stack.getDisplayName().equals("") && 
							!displayNames.contains(stack.getDisplayName()))) {
						stacks.add(stack);
						displayNames.add(stack.getDisplayName());
					}
				} catch (Exception e) {continue;}
		}

		//creates list of names that the items will be registered with to prevent duplicates
		ArrayList<String> registryNames = new ArrayList<String>();

		//checks list of ItemStacks for valid ones and creates set and adds to allSets
		allSets = new ArrayList<ArmorSet>();
		nameToSetMap = Maps.newHashMap();
		for (ItemStack stack : stacks)
			if (isValid(stack) && ArmorSet.getSet(stack.getItem(), stack.getMetadata()) == null) {
				String registryName = getItemStackRegistryName(stack);
				if (!registryNames.contains(registryName) && !registryName.equals("")) {
					ArmorSet set = new ArmorSet(stack);
					allSets.add(set);
					nameToSetMap.put(registryName, set);
					registryNames.add(registryName);
				}
			}
	}

	/**Returns TextureAtlasSprite corresponding to given ItemModArmor*/
	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite getSprite(ItemBlockArmor item) {		
		if (item != null) {
			TextureAtlasSprite sprite = item.set.sprites[item.armorType.getIndex()];
			return sprite == null ? missingSprite : sprite;
		}
		else
			return missingSprite;
	}

	/**Returns current alpha for animation overlay corresponding to given ItemModArmor*/
	@SideOnly(Side.CLIENT)
	public static float getAlpha(ItemBlockArmor item) {
		if (item != null) {
			float frame = item.set.frames[item.armorType.getIndex()];
			return frame - (int) frame;
		}
		else
			return 0;
	}

	/**Returns current animation frame corresponding to given ItemModArmor*/
	@SideOnly(Side.CLIENT)
	public static int getCurrentAnimationFrame(ItemBlockArmor item) {
		AnimationMetadataSection animation;
		if (item != null && (animation = item.set.animations[item.armorType.getIndex()]) != null) {
			int frame = (int) item.set.frames[item.armorType.getIndex()];
			return animation.getFrameIndex(frame);
		}
		else
			return 0;
	}

	/**Returns next animation frame corresponding to given ItemModArmor*/
	@SideOnly(Side.CLIENT)
	public static int getNextAnimationFrame(ItemBlockArmor item) {
		AnimationMetadataSection animation;
		if (item != null && (animation = item.set.animations[item.armorType.getIndex()]) != null) {
			int frame = (int) item.set.frames[item.armorType.getIndex()];
			if (frame++ >= animation.getFrameCount()-1)
				frame -= animation.getFrameCount();
			return animation.getFrameIndex(frame);
		}
		else
			return 0;
	}

	/**Returns color corresponding to given ItemModArmor*/
	@SideOnly(Side.CLIENT)
	public static int getColor(ItemBlockArmor item) {
		if (item != null)
			return item.set.colors[item.armorType.getIndex()];
		else
			return -1;
	}

	/**Used to uniformly create registry name*/
	public static String getItemStackRegistryName(ItemStack stack) {
		try {
			String registryName = stack.getItem().getRegistryName().getResourcePath().toLowerCase().replace(" ", "_");
			registryName += (stack.getHasSubtypes() ? "_"+stack.getMetadata() : "");
			return registryName;
		} 
		catch (Exception e) {
			return "";
		}
	}

	/**Change display name based on the block*/
	public static String getItemStackDisplayName(ItemStack stack, EntityEquipmentSlot slot)	{
		String name;
		if (stack != null && stack.getItem() instanceof ItemBlockArmor) {
			name = ((ItemBlockArmor) stack.getItem()).set.stack.getDisplayName();
		}
		else if (stack != null && stack.getItem() != null)
			name = stack.getDisplayName();
		else
			name = "";

		//manually set display names
		name = name.replace("Block of ", "") 
				.replace("Block ", "")
				.replace(" Block", "")
				.replace("Sugar Canes", "Sugar Cane")
				.replace("Bricks", "Brick")
				.replace("Planks", "Plank");

		name = TextFormatting.getTextWithoutFormattingCodes(name);

		if (slot != null)
			switch (slot) {
			case HEAD:
				name += " Helmet";
				break;
			case CHEST:
				name += " Chestplate";
				break;
			case LEGS:
				name += " Leggings";
				break;
			case FEET:
				name += " Boots";
				break;
			default:
				break;
			}

		return name;
	}

	/**Returns first piece of armor of the entity's worn set, or null*/
	public static ItemStack getFirstSetItem(EntityLivingBase entity, SetEffect effect) {
		if (effect != null && getWornSetEffects(entity).contains(effect))
			for (EntityEquipmentSlot slot : SLOTS) {
				ItemStack stack = entity.getItemStackFromSlot(slot);
				if (stack != null && stack.getItem() instanceof ItemBlockArmor &&
						((ItemBlockArmor)stack.getItem()).set.setEffects.contains(effect))
					return stack;
			}
		return null;
	}

	/**Returns the active+enabled set effects of the armor that the entity is wearing*/
	public static ArrayList<SetEffect> getWornSetEffects(EntityLivingBase entity) {
		ArrayList<SetEffect> effects = new ArrayList<SetEffect>();
		HashMap<String, Tuple<SetEffect, Integer>> setCounts = Maps.newHashMap();
		if (entity != null) {
			for (EntityEquipmentSlot slot : SLOTS) {
				ItemStack stack = entity.getItemStackFromSlot(slot);
				if (stack != null && stack.getItem() instanceof ItemBlockArmor) {
					ItemBlockArmor armor = (ItemBlockArmor) stack.getItem();
					for (SetEffect effect : armor.set.setEffects) {
						if (effect.isEnabled()) {
							Tuple t = null;
							for (String description : setCounts.keySet())
								if (description.equals(effect.description))
									t = setCounts.get(effect.description);
							if (t != null)
								setCounts.put(effect.description, new Tuple(t.getFirst(), ((Integer)t.getSecond())+1));
							else
								setCounts.put(effect.description, new Tuple(effect, 1));
						}
					}
				}
			}
			for (String description : setCounts.keySet())
				if (setCounts.get(description).getSecond() >= Config.piecesForSet)
					effects.add(setCounts.get(description).getFirst());
		}
		return effects;
	}

	/**Returns armor set corresponding to given block and meta, or null if none exists*/
	public static ArmorSet getSet(Block block, int meta) {
		for (ArmorSet set : allSets)
			if (set.block == block && set.meta == meta)
				return set;
		return null;
	}

	/**Returns armor set corresponding to given item and meta, or null if none exists*/
	public static ArmorSet getSet(Item item, int meta) {
		for (ArmorSet set : allSets)
			if (set.item == item && set.meta == meta)
				return set;
		return null;
	}

	/**Should an armor set be made from this item*/
	private static boolean isValid(ItemStack stack) {
		try {			
			for (ItemStack manualStack : MANUALLY_ADDED_SETS)
				if (stack != null && stack.getItem() == manualStack.getItem() && stack.getMetadata() == manualStack.getMetadata())
					return true;

			if (stack == null || !(stack.getItem() instanceof ItemBlock) || 
					stack.getItem().getRegistryName().getResourceDomain().contains("one_point_twelve_concrete") ||
					stack.getItem().getRegistryName().getResourceDomain().contains("railcraft") ||
					stack.getItem().getRegistryName().getResourcePath().contains("ore") || 
					stack.getItem().getRegistryName().getResourcePath().contains("ingot") || 
					stack.getDisplayName().contains(".name") || stack.getDisplayName().contains("Ore") ||
					stack.getDisplayName().contains("%") || stack.getDisplayName().contains("Ingot"))
				return false;

			Block block = ((ItemBlock)stack.getItem()).getBlock();
			if (block instanceof BlockLiquid || block instanceof BlockContainer || block.hasTileEntity() || 
					block instanceof BlockOre || block instanceof BlockCrops || block instanceof BlockBush ||
					block == Blocks.BARRIER || block instanceof BlockSlab || block == Blocks.MONSTER_EGG ||
					block.getRenderType(block.getDefaultState()) != EnumBlockRenderType.MODEL ||
					block == Blocks.IRON_BLOCK || block == Blocks.GOLD_BLOCK || block == Blocks.DIAMOND_BLOCK)
				return false;

			String registryName = block.getRegistryName().toString();
			if (registryName.equalsIgnoreCase("evilcraft:darkBlock") || 
					registryName.equalsIgnoreCase("evilcraft:obscuredGlass") ||
					registryName.equalsIgnoreCase("evilcraft:hardenedBlood") ||
					registryName.equalsIgnoreCase("evilcraft:darkPowerGemBlock") ||
					registryName.equalsIgnoreCase("darkutils:filter") || 
					registryName.equalsIgnoreCase("darkutils:filter_inverted") ||
					registryName.equalsIgnoreCase("agriculturalrevolution:rustedanalyser") ||
					registryName.equalsIgnoreCase("agriculturalrevolution:rustedbot") ||
					registryName.equalsIgnoreCase("agriculturalrevolution:rustedmutationstation") ||
					registryName.equalsIgnoreCase("agriculturalrevolution:rustedinscriber") ||
					registryName.equalsIgnoreCase("agriculturalrevolution:rustedhydrophonic") ||
					registryName.equalsIgnoreCase("agriculturalrevolution:rustedresearch") ||
					registryName.equalsIgnoreCase("agriculturalrevolution:rustedpipe") ||
					registryName.equalsIgnoreCase("agriculturalrevolution:rustedironscaff") ||
					registryName.equalsIgnoreCase("tconstruct:clear_glass"))
				return false;

			//Check if full block
			ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
			block.addCollisionBoxToList(block.getDefaultState(), null, BlockPos.ORIGIN, Block.FULL_BLOCK_AABB, list, null);
			if (list.size() != 1 || !list.get(0).equals(Block.FULL_BLOCK_AABB)) 
				return false;

			return true;
		}
		catch (Exception e) { 
			return false; 
		}
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	/**Adds set items to creative tab and adds recipes*/
	public boolean enable() {
		if (this.enabled || this.missingTextures) //don't enable sets with missing textures
			return false;
		else
			this.enabled = true;

		ItemBlockArmor[] armors = new ItemBlockArmor[] {this.helmet, this.chestplate, this.leggings, this.boots};
		for (ItemBlockArmor armor : armors) {
			//add to tab
			if (isFromModdedBlock) {
				if (BlockArmor.moddedTab == null)
					BlockArmor.moddedTab = new BlockArmorCreativeTab("tabBlockArmorModded");
				BlockArmor.moddedTab.orderedStacks.add(new ItemStack(armor));
				armor.setCreativeTab(BlockArmor.moddedTab);
			}
			else {
				if (BlockArmor.vanillaTab == null)
					BlockArmor.vanillaTab = new BlockArmorCreativeTab("tabBlockArmorVanilla");
				BlockArmor.vanillaTab.orderedStacks.add(new ItemStack(armor));
				armor.setCreativeTab(BlockArmor.vanillaTab);
			}
		}

		//add recipes
		for (IRecipe recipe : recipes)
			if (!CraftingManager.getInstance().getRecipeList().contains(recipe))
				CraftingManager.getInstance().getRecipeList().add(recipe);

		return true;
	}

	/**Remove set items from creative tab and removes recipes*/
	public boolean disable() {
		if (this.enabled)
			this.enabled = false;
		else
			return false;

		ItemBlockArmor[] armors = new ItemBlockArmor[] {this.helmet, this.chestplate, this.leggings, this.boots};
		for (ItemBlockArmor armor : armors) {
			//remove from creative tab
			armor.setCreativeTab(null);

			//remove from vanilla tab
			if (BlockArmor.vanillaTab != null && BlockArmor.vanillaTab.orderedStacks != null)
				for (ItemStack tabStack : BlockArmor.vanillaTab.orderedStacks)
					if (tabStack.getItem() == armor) {
						BlockArmor.vanillaTab.orderedStacks.remove(tabStack);
						break;
					}

			//remove from modded tab
			if (BlockArmor.moddedTab != null && BlockArmor.moddedTab.orderedStacks != null)
				for (ItemStack tabStack : BlockArmor.moddedTab.orderedStacks)
					if (tabStack.getItem() == armor) {
						BlockArmor.moddedTab.orderedStacks.remove(tabStack);
						break;
					}
		}

		//remove recipes
		for (IRecipe recipe : recipes)
			if (CraftingManager.getInstance().getRecipeList().contains(recipe))
				CraftingManager.getInstance().getRecipeList().remove(recipe);

		return true;
	}

	/**Initialize set's texture variable*/
	@SideOnly(Side.CLIENT)
	public Tuple<Integer, Boolean> initTextures() {
		boolean missingTextures = false;

		if (missingSprite == null)
			missingSprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();

		int numTextures = 0;
		this.sprites = new TextureAtlasSprite[EntityEquipmentSlot.values().length];
		this.animations = new AnimationMetadataSection[EntityEquipmentSlot.values().length];
		this.frames = new float[EntityEquipmentSlot.values().length];
		this.colors = new int[EntityEquipmentSlot.values().length];
		for (int i=0; i<colors.length; i++)
			this.colors[i] = -1;

		//Gets textures from item model's BakedQuads (textures for each side)
		List<BakedQuad> list = new ArrayList<BakedQuad>();
		try {
			ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

			//getting quads may throw exception if a mod's modeler doesn't obey @Nullable
			list.addAll(mesher.getItemModel(this.stack).getQuads(null, null, 0));
			for (EnumFacing facing : EnumFacing.VALUES)
				list.addAll(mesher.getItemModel(this.stack).getQuads(null, facing, 0));

			for (BakedQuad quad : list) {
				ResourceLocation loc1 = new ResourceLocation(quad.getSprite().getIconName());

				TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(loc1.toString());
				AnimationMetadataSection animation = (AnimationMetadataSection) (sprite.getFrameCount() > 1 ? ReflectionHelper.getPrivateValue(TextureAtlasSprite.class, sprite, 3) : null); //animationMetadata
				int color = quad.hasTintIndex() ? Minecraft.getMinecraft().getItemColors().getColorFromItemstack(this.stack, quad.getTintIndex()) : -1;

				if (sprite.getIconName().contains("overlay")) //overlays not supported by forge so we can't account for them
					continue;

				if (quad.getFace() == EnumFacing.UP) { //top
					if (sprite != missingSprite)
						numTextures++;
					this.sprites[EntityEquipmentSlot.HEAD.getIndex()] = sprite;
					this.animations[EntityEquipmentSlot.HEAD.getIndex()] = animation;
					this.colors[EntityEquipmentSlot.HEAD.getIndex()] = color;
				}
				else if (quad.getFace() == EnumFacing.NORTH) { //front
					if (sprite != missingSprite)
						numTextures++;
					this.sprites[EntityEquipmentSlot.CHEST.getIndex()] = sprite;
					this.animations[EntityEquipmentSlot.CHEST.getIndex()] = animation;
					this.colors[EntityEquipmentSlot.CHEST.getIndex()] = color;
				}
				else if (quad.getFace() == EnumFacing.SOUTH) { //back
					if (sprite != missingSprite)
						numTextures++;
					this.sprites[EntityEquipmentSlot.LEGS.getIndex()] = sprite;
					this.animations[EntityEquipmentSlot.LEGS.getIndex()] = animation;
					this.colors[EntityEquipmentSlot.LEGS.getIndex()] = color;
				}
				else if (quad.getFace() == EnumFacing.DOWN) { //bottom
					if (sprite != missingSprite)
						numTextures++;
					this.sprites[EntityEquipmentSlot.FEET.getIndex()] = sprite;
					this.animations[EntityEquipmentSlot.FEET.getIndex()] = animation;
					this.colors[EntityEquipmentSlot.FEET.getIndex()] = color;
				}
			}
		}
		catch (Exception e) {}

		//Check for block texture overrides
		if (TEXTURE_OVERRIDES.contains(item))
			for (EntityEquipmentSlot slot : SLOTS) {
				ResourceLocation texture = new ResourceLocation(BlockArmor.MODID+":textures/items/overrides/"+
						item.getRegistryName().getResourcePath().toLowerCase().replace(" ", "_")+"_"+slot.getName()+".png");
				try {
					Minecraft.getMinecraft().getResourceManager().getResource(texture); //does texture exist?
					texture = new ResourceLocation(texture.getResourceDomain(), texture.getResourcePath().replace("textures/", "").replace(".png", ""));
					TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());
					this.sprites[slot.getIndex()] = sprite;
					this.animations[slot.getIndex()] = null;
					//BlockArmor.logger.info("Override texture for "+this.stack.getDisplayName()+" found at: "+texture.toString());
				} catch (Exception e) {
					//BlockArmor.logger.info("Override texture for "+this.stack.getDisplayName()+" NOT found at: "+texture.toString()); 
				}
			}

		//If a sprite is missing, disable the set
		if (this.sprites[EntityEquipmentSlot.HEAD.getIndex()] == null || 
				this.sprites[EntityEquipmentSlot.CHEST.getIndex()] == null || 
				this.sprites[EntityEquipmentSlot.LEGS.getIndex()] == null || 
				this.sprites[EntityEquipmentSlot.FEET.getIndex()] == null ||
				this.sprites[EntityEquipmentSlot.HEAD.getIndex()] == missingSprite ||
				this.sprites[EntityEquipmentSlot.CHEST.getIndex()] == missingSprite ||
				this.sprites[EntityEquipmentSlot.LEGS.getIndex()] == missingSprite || 
				this.sprites[EntityEquipmentSlot.FEET.getIndex()] == missingSprite) 
			missingTextures = true;

		this.isTranslucent = this.block.getBlockLayer() != BlockRenderLayer.SOLID && this.block != Blocks.REEDS;

		return new Tuple(numTextures, missingTextures);
	}
}