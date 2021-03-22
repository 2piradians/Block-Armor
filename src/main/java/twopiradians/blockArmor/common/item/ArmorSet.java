package twopiradians.blockArmor.common.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.OreBlock;
import net.minecraft.block.SilverfishBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.seteffect.SetEffect;
import twopiradians.blockArmor.creativetab.BlockArmorCreativeTab;
import twopiradians.blockArmor.utils.BlockUtils;

@SuppressWarnings({ "deprecation" })
public class ArmorSet {
	/**Used to add ItemStacks that will be approved for sets that would otherwise not be valid*/
	private static final ArrayList<ItemStack> MANUALLY_ADDED_SETS;
	static {
		MANUALLY_ADDED_SETS = new ArrayList<ItemStack>() {{
			add(new ItemStack(Items.SUGAR_CANE));
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
			add(new ItemStack(Blocks.NOTE_BLOCK));
			//add(new ItemStack(Blocks.JUKEBOX));
		}};
	}
	/**Used to add Items that have overriding textures*/
	public static final ArrayList<Item> TEXTURE_OVERRIDES;
	static {
		TEXTURE_OVERRIDES = new ArrayList<Item>() {{
			add(Items.SUGAR_CANE);
			add(Item.getItemFromBlock(Blocks.ENDER_CHEST));
			//add(Item.getItemFromBlock(Blocks.CHEST));
		}};
	}
	/**All sets, including disabled sets*/
	public static ArrayList<ArmorSet> allSets;
	/**All sets, mapped by their stack's display name*/
	public static HashMap<String, ArmorSet> nameToSetMap;
	/**Armor slots*/
	public static final EquipmentSlotType[] SLOTS = new EquipmentSlotType[] 
			{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};

	/**Used to get textures, set recipe, and as repair material*/
	public ItemStack stack;
	public Item item;
	public Block block;
	public BlockArmorMaterial material;      
	public BlockArmorItem helmet;
	public BlockArmorItem chestplate;
	public BlockArmorItem leggings;
	public BlockArmorItem boots;
	public boolean isFromModdedBlock;
	public ArrayList<SetEffect> setEffects;
	public String modid;
	/**should only be modified through enable() and disable(); enabled = in tab and has recipe*/
	private boolean enabled;
	/**Only changed on client*/
	public boolean missingTextures; 


	public boolean isTranslucent;
	/**Array of block's textures sorted by EquipmentSlotType id*/

	private TextureAtlasSprite[] sprites;
	/**Array of TextureAtlasSprite's animation (or null if not animated) sorted by EquipmentSlotType id*/

	public AnimationMetadataSection[] animations;
	/**Array of TextureAtlasSprite's current frame number (including decimals between frames) sorted by EquipmentSlotType id*/

	public float[] frames;
	/**Array of quad's color (or -1 if none) sorted by EquipmentSlotType id*/

	private int[] colors;
	/**Minecraft's default missing texture sprite, assigned in initTextures()*/

	private static TextureAtlasSprite missingSprite;

	public ArmorSet(ItemStack stack) {
		this.stack = stack;
		this.item = stack.getItem();
		try {
			ResourceLocation loc = this.item.getRegistryName();
			if (!loc.getNamespace().equals("minecraft"))
				isFromModdedBlock = true;
			this.modid = loc.getNamespace().toLowerCase();
		}
		catch (Exception e) {
			this.modid = "???";
			isFromModdedBlock = true;
		}
		if (item == Items.SUGAR_CANE)
			this.block = Blocks.SUGAR_CANE;
		else
			this.block = ((BlockItem) item).getBlock();
		//calculate values for and set material
		float blockHardness = BlockUtils.getHardness(block); 
		double durability = 5;
		float toughness = 0;
		int enchantability = 12;
		float knockbackResistance = 0; // TODO

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
		this.material = new BlockArmorMaterial(getItemStackDisplayName(stack, null)+" Material", 
				(int) durability, reductionAmounts, enchantability, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, toughness,
				knockbackResistance, () -> {
				      return Ingredient.fromItems(new IItemProvider[]{this.item});
				   });
		//BlockArmor.logger.info(getItemStackDisplayName(stack, null)+": blockHardness = "+blockHardness+", toughness = "+toughness+", durability = "+durability);

		//CommandDev.addBlockName(this);
	}

	/**Returns armor item for slot*/
	public BlockArmorItem getArmorForSlot(EquipmentSlotType slot) {
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
		Block[] blocks = Iterators.toArray(Registry.BLOCK.iterator(), Block.class);
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		for (Block block : blocks) {
				try {
					ItemStack stack = block == Blocks.SUGAR_CANE ? new ItemStack(Items.SUGAR_CANE) : new ItemStack(block);

					boolean manuallyAdded = false;
					for (ItemStack manualStack : MANUALLY_ADDED_SETS)
						if (stack != null && stack.getItem() == manualStack.getItem())
							manuallyAdded = true;

					if (manuallyAdded || (stack != null && stack.getItem() != null && !stack.getDisplayName().equals("") && 
							!displayNames.contains(stack.getDisplayName()))) {
						stacks.add(stack);
						displayNames.add(stack.getDisplayName().getString());
					}
				} catch (Exception e) {continue;}
		}

		//creates list of names that the items will be registered with to prevent duplicates
		ArrayList<String> registryNames = new ArrayList<String>();

		//checks list of ItemStacks for valid ones and creates set and adds to allSets
		allSets = new ArrayList<ArmorSet>();
		nameToSetMap = Maps.newHashMap();
		for (ItemStack stack : stacks)
			if (isValid(stack) && ArmorSet.getSet(stack.getItem()) == null) {
				String registryName = getItemStackRegistryName(stack);
				if (!registryNames.contains(registryName) && !registryName.equals("")) {
					try {
						ArmorSet set = new ArmorSet(stack);
						allSets.add(set);
						nameToSetMap.put(registryName, set);
						registryNames.add(registryName);
					}
					catch (Exception e) {}
				}
			}
	}

	/**Returns TextureAtlasSprite corresponding to given ItemModArmor*/
	public static TextureAtlasSprite getSprite(BlockArmorItem item) {		
		if (item != null) {
			if (item.set.sprites == null)
				item.set.initTextures();
			TextureAtlasSprite sprite = item.set.sprites[item.getEquipmentSlot().getIndex()];
			return sprite == null ? missingSprite : sprite;
		}
		else
			return missingSprite;
	}

	/**Returns current alpha for animation overlay corresponding to given ItemModArmor*/

	public static float getAlpha(BlockArmorItem item) {
		if (item != null) {
			float frame = item.set.frames[item.getEquipmentSlot().getIndex()];
			return frame - (int) frame;
		}
		else
			return 0;
	}

	/**Returns current animation frame corresponding to given ItemModArmor*/

	public static int getCurrentAnimationFrame(BlockArmorItem item) {
		AnimationMetadataSection animation;
		if (item != null && (animation = item.set.animations[item.getEquipmentSlot().getIndex()]) != null) {
			int frame = (int) item.set.frames[item.getEquipmentSlot().getIndex()];
			return animation.getFrameIndex(frame);
		}
		else
			return 0;
	}

	/**Returns next animation frame corresponding to given ItemModArmor*/

	public static int getNextAnimationFrame(BlockArmorItem item) {
		AnimationMetadataSection animation;
		if (item != null && (animation = item.set.animations[item.getEquipmentSlot().getIndex()]) != null) {
			int frame = (int) item.set.frames[item.getEquipmentSlot().getIndex()];
			if (frame++ >= animation.getFrameCount()-1)
				frame -= animation.getFrameCount();
			return animation.getFrameIndex(frame);
		}
		else
			return 0;
	}

	/**Returns color corresponding to given ItemModArmor*/

	public static int getColor(BlockArmorItem item) {
		if (item != null)
			return item.set.colors[item.getEquipmentSlot().getIndex()];
		else
			return -1;
	}

	/**Used to uniformly create registry name*/
	public static String getItemStackRegistryName(ItemStack stack) {
		try {
			String registryName = stack.getItem().getRegistryName().getPath().toLowerCase().replace(" ", "_");
			return registryName;
		} 
		catch (Exception e) {
			return "";
		}
	}

	/**Change display name based on the block*/
	public static ITextComponent getItemStackDisplayName(ItemStack stack, EquipmentSlotType slot)	{
		String name;
		if (stack != null && stack.getItem() instanceof BlockArmorItem) {
			name = ((BlockArmorItem) stack.getItem()).set.stack.getDisplayName().getString();
		}
		else if (stack != null && stack.getItem() != null)
			name = stack.getDisplayName().getString();
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

		return new StringTextComponent(name);
	}

	/**Returns first piece of armor of the entity's worn set, or null*/
	public static ItemStack getFirstSetItem(LivingEntity entity, SetEffect effect) {
		if (effect != null && getWornSetEffects(entity).contains(effect))
			for (EquipmentSlotType slot : SLOTS) {
				ItemStack stack = entity.getItemStackFromSlot(slot);
				if (stack != null && stack.getItem() instanceof BlockArmorItem &&
						((BlockArmorItem)stack.getItem()).set.setEffects.contains(effect))
					return stack;
			}
		return null;
	}

	/**Returns the active+enabled set effects of the armor that the entity is wearing*/
	public static ArrayList<SetEffect> getWornSetEffects(LivingEntity entity) {
		ArrayList<SetEffect> effects = new ArrayList<SetEffect>();
		HashMap<String, Tuple<SetEffect, Integer>> setCounts = Maps.newHashMap();
		if (entity != null) {
			for (EquipmentSlotType slot : SLOTS) {
				ItemStack stack = entity.getItemStackFromSlot(slot);
				if (stack != null && stack.getItem() instanceof BlockArmorItem) {
					BlockArmorItem armor = (BlockArmorItem) stack.getItem();
					for (SetEffect effect : armor.set.setEffects) {
						if (effect.isEnabled()) {
							Tuple t = null;
							for (String description : setCounts.keySet())
								if (description.equals(effect.description))
									t = setCounts.get(effect.description);
							if (t != null)
								setCounts.put(effect.description, new Tuple(t.getA(), ((Integer)t.getB())+1));
							else
								setCounts.put(effect.description, new Tuple(effect, 1));
						}
					}
				}
			}
			for (String description : setCounts.keySet())
				if (setCounts.get(description).getB() >= /*Config.piecesForSet*/4) // TODO
					effects.add(setCounts.get(description).getA());
		}
		return effects;
	}

	/**Returns armor set corresponding to given block and meta, or null if none exists*/
	public static ArmorSet getSet(Block block) {
		for (ArmorSet set : allSets)
			if (set.block == block)
				return set;
		return null;
	}

	/**Returns armor set corresponding to given item, or null if none exists*/
	public static ArmorSet getSet(Item item) {
		for (ArmorSet set : allSets)
			if (set.item == item)
				return set;
		return null;
	}

	/**Should an armor set be made from this item*/
	private static boolean isValid(ItemStack stack) {
		try {			
			for (ItemStack manualStack : MANUALLY_ADDED_SETS)
				if (stack != null && stack.getItem() == manualStack.getItem())
					return true;
			// not BlockItem, bad modded item, ore/ingot, or unnamed
			if (stack == null || !(stack.getItem() instanceof BlockItem) || 
					stack.getItem().getRegistryName().getNamespace().contains("one_point_twelve_concrete") ||
					stack.getItem().getRegistryName().getNamespace().contains("railcraft") ||
					stack.getItem().getRegistryName().getNamespace().contains("ore") || 
					stack.getItem().getRegistryName().getNamespace().contains("ingot") || 
					stack.getDisplayName().getString().contains(".name") || stack.getDisplayName().getString().contains("Ore") ||
					stack.getDisplayName().getString().contains("%") || stack.getDisplayName().getString().contains("Ingot"))
				return false;
			// bad blocks
			Block block = ((BlockItem)stack.getItem()).getBlock();
			if (block instanceof FlowingFluidBlock || block instanceof ContainerBlock || block.hasTileEntity(block.getDefaultState()) || 
					block instanceof OreBlock || block instanceof CropsBlock || block instanceof BushBlock ||
					block == Blocks.BARRIER || block instanceof SlabBlock || block instanceof SilverfishBlock ||
					block.getRenderType(block.getDefaultState()) != BlockRenderType.MODEL ||
					block == Blocks.IRON_BLOCK || block == Blocks.GOLD_BLOCK || block == Blocks.DIAMOND_BLOCK ||
					block == Blocks.AIR)
				return false;
			// bad modded items
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
			//ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
			/* TODO
			block.addCollisionBoxToList(block.getDefaultState(), null, BlockPos.ZERO, Block.FULL_BLOCK_AABB, list, null, false); 
			if (list.size() != 1 || !list.get(0).equals(Block.FULL_BLOCK_AABB)) 
				return false;*/

			return true;
		}
		catch (Exception e) { return false; }
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

		BlockArmorItem[] armors = new BlockArmorItem[] {this.helmet, this.chestplate, this.leggings, this.boots};
		for (BlockArmorItem armor : armors) {
			//add to tab
			if (isFromModdedBlock) {
				if (BlockArmor.moddedTab == null)
					BlockArmor.moddedTab = new BlockArmorCreativeTab("tabBlockArmorModded");
				BlockArmor.moddedTab.orderedStacks.add(new ItemStack(armor));
				armor.setGroup(BlockArmor.moddedTab);
			}
			else {
				if (BlockArmor.vanillaTab == null)
					BlockArmor.vanillaTab = new BlockArmorCreativeTab("tabBlockArmorVanilla");
				BlockArmor.vanillaTab.orderedStacks.add(new ItemStack(armor));
				armor.setGroup(BlockArmor.vanillaTab);
			}
		}

		return true;
	}

	/**Remove set items from creative tab and removes recipes*/
	public boolean disable() {
		if (this.enabled)
			this.enabled = false;
		else
			return false;

		BlockArmorItem[] armors = new BlockArmorItem[] {this.helmet, this.chestplate, this.leggings, this.boots};
		for (BlockArmorItem armor : armors) {
			//remove from creative tab
			armor.setGroup(null);

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

		return true;
	}

	/**Initialize set's texture variable*/

	public Tuple<Integer, Boolean> initTextures() {
		boolean missingTextures = false;

		if (missingSprite == null)
			missingSprite = MissingTextureSprite.create(new AtlasTexture(new ResourceLocation("")), 0, 16, 16, 0, 0);

		int numTextures = 0;
		this.sprites = new TextureAtlasSprite[EquipmentSlotType.values().length];
		this.animations = new AnimationMetadataSection[EquipmentSlotType.values().length];
		this.frames = new float[EquipmentSlotType.values().length];
		this.colors = new int[EquipmentSlotType.values().length];
		for (int i=0; i<colors.length; i++)
			this.colors[i] = -1;

		//Gets textures from item model's BakedQuads (textures for each side)
		List<BakedQuad> list = new ArrayList<BakedQuad>();
		try {
			ItemModelMesher mesher = Minecraft.getInstance().getItemRenderer().getItemModelMesher();

			Random rand = new Random();
			//getting quads may throw exception if a mod's modeler doesn't obey @Nullable
			list.addAll(mesher.getItemModel(this.stack).getQuads(null, null, rand));
			for (Direction facing : Direction.values())
				list.addAll(mesher.getItemModel(this.stack).getQuads(null, facing, rand));

			for (BakedQuad quad : list) {
				ResourceLocation loc1 = quad.getSprite().getName();

				TextureAtlasSprite sprite = quad.getSprite();
				AnimationMetadataSection animation = (AnimationMetadataSection) (sprite.getFrameCount() > 1 ? ObfuscationReflectionHelper.getPrivateValue(TextureAtlasSprite.class, sprite, "field_110982_k") : null); //animationMetadata
				int color = quad.hasTintIndex() ? Minecraft.getInstance().getItemColors().getColor(this.stack, quad.getTintIndex()) : -1;

				if (sprite.getName().toString().contains("overlay")) //overlays not supported by forge so we can't account for them
					continue;

				if (quad.getFace() == Direction.UP) { //top
					if (sprite != missingSprite)
						numTextures++;
					this.sprites[EquipmentSlotType.HEAD.getIndex()] = sprite;
					this.animations[EquipmentSlotType.HEAD.getIndex()] = animation;
					this.colors[EquipmentSlotType.HEAD.getIndex()] = color;
				}
				else if (quad.getFace() == Direction.NORTH) { //front
					if (sprite != missingSprite)
						numTextures++;
					this.sprites[EquipmentSlotType.CHEST.getIndex()] = sprite;
					this.animations[EquipmentSlotType.CHEST.getIndex()] = animation;
					this.colors[EquipmentSlotType.CHEST.getIndex()] = color;
				}
				else if (quad.getFace() == Direction.SOUTH) { //back
					if (sprite != missingSprite)
						numTextures++;
					this.sprites[EquipmentSlotType.LEGS.getIndex()] = sprite;
					this.animations[EquipmentSlotType.LEGS.getIndex()] = animation;
					this.colors[EquipmentSlotType.LEGS.getIndex()] = color;
				}
				else if (quad.getFace() == Direction.DOWN) { //bottom
					if (sprite != missingSprite)
						numTextures++;
					this.sprites[EquipmentSlotType.FEET.getIndex()] = sprite;
					this.animations[EquipmentSlotType.FEET.getIndex()] = animation;
					this.colors[EquipmentSlotType.FEET.getIndex()] = color;
				}
			}
		}
		catch (Exception e) {}

		//Check for block texture overrides
		if (TEXTURE_OVERRIDES.contains(item))
			for (EquipmentSlotType slot : SLOTS) {
				ResourceLocation texture = new ResourceLocation(BlockArmor.MODID+":textures/items/overrides/"+
						item.getRegistryName().getPath().toLowerCase().replace(" ", "_")+"_"+slot.getName()+".png");
				try {
					Minecraft.getInstance().getResourceManager().getResource(texture); //does texture exist?
					texture = new ResourceLocation(texture.getNamespace(), texture.getPath().replace("textures/", "").replace(".png", ""));
					TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlasTexture(texture).getSprite(texture);
					this.sprites[slot.getIndex()] = sprite;
					this.animations[slot.getIndex()] = null;
					//BlockArmor.logger.info("Override texture for "+this.stack.getDisplayName()+" found at: "+texture.toString());
				} catch (Exception e) {
					//BlockArmor.logger.info("Override texture for "+this.stack.getDisplayName()+" NOT found at: "+texture.toString()); 
				}
			}

		//If a sprite is missing, disable the set
		if (this.sprites[EquipmentSlotType.HEAD.getIndex()] == null || 
				this.sprites[EquipmentSlotType.CHEST.getIndex()] == null || 
				this.sprites[EquipmentSlotType.LEGS.getIndex()] == null || 
				this.sprites[EquipmentSlotType.FEET.getIndex()] == null ||
				this.sprites[EquipmentSlotType.HEAD.getIndex()] == missingSprite ||
				this.sprites[EquipmentSlotType.CHEST.getIndex()] == missingSprite ||
				this.sprites[EquipmentSlotType.LEGS.getIndex()] == missingSprite || 
				this.sprites[EquipmentSlotType.FEET.getIndex()] == missingSprite) 
			missingTextures = true;

		this.isTranslucent = false;// TODO this.block.getBlockLayer() != BlockRenderLayer.SOLID && this.block != Blocks.SUGAR_CANE;

		return new Tuple(numTextures, missingTextures);
	}
}