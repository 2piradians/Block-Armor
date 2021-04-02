package twopiradians.blockArmor.common.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import javax.annotation.Nullable;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.OreBlock;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.block.SilverfishBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.command.CommandDev;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.seteffect.SetEffect;
import twopiradians.blockArmor.creativetab.BlockArmorCreativeTab;
import twopiradians.blockArmor.utils.BlockUtils;

@SuppressWarnings({ "deprecation" })
public class ArmorSet {

	/**Used to add ItemStacks that will be approved for sets that would otherwise not be valid*/
	private static final ArrayList<Block> MANUALLY_ADDED_SETS;
	static {
		MANUALLY_ADDED_SETS = new ArrayList<Block>() {{
			add(Blocks.SUGAR_CANE);
			add(Blocks.CACTUS);
			add(Blocks.DISPENSER);
			add(Blocks.DROPPER);
			add(Blocks.BEACON);
			add(Blocks.FURNACE);
			add(Blocks.ENCHANTING_TABLE);
			add(Blocks.COMMAND_BLOCK);
			add(Blocks.CHAIN_COMMAND_BLOCK);
			add(Blocks.REPEATING_COMMAND_BLOCK);
			add(Blocks.BROWN_MUSHROOM_BLOCK);
			add(Blocks.RED_MUSHROOM_BLOCK);
			add(Blocks.SOUL_SAND);
			add(Blocks.ENDER_CHEST);
			//add(Blocks.CHEST);
			add(Blocks.NOTE_BLOCK);
			//add(Blocks.JUKEBOX);
		}};
	}
	/**Used to add Items that have overriding textures*/
	public static final ArrayList<Block> TEXTURE_OVERRIDES;
	static {
		TEXTURE_OVERRIDES = new ArrayList<Block>() {{
			add(Blocks.SUGAR_CANE);
			add(Blocks.ENDER_CHEST);
			//add(Blocks.CHEST);
		}};
	}
	/**All sets, including disabled sets*/
	public static ArrayList<ArmorSet> allSets = Lists.newArrayList();
	/**All sets, mapped by their stack's display name*/
	public static HashMap<String, ArmorSet> nameToSetMap = Maps.newHashMap();
	/**All sets, mapped by their block's modid*/
	public static HashMap<String, TreeSet<ArmorSet>> modidToSetMap = Maps.newHashMap();
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
	public String registryName;
	/**should only be modified through enable() and disable(); enabled = in creative tab and has recipe*/
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
	public static TextureAtlasSprite missingSprite;
	// armor values calculated from block / config
	public float armorDamageReduction;
	public float armorToughness;
	public int armorDurability;
	public int armorEnchantability;
	public int armorKnockbackResistance;

	public ArmorSet(ItemStack stack) {
		this.stack = stack;
		this.item = stack.getItem();
		this.block = ((BlockItem) item).getBlock();
		this.registryName = ArmorSet.getItemStackRegistryName(this.stack);
		try {
			ResourceLocation loc = this.item.getRegistryName();
			this.modid = loc.getNamespace().toLowerCase();
			if (!this.modid.equals("minecraft"))
				isFromModdedBlock = true;
		}
		catch (Exception e) {
			this.modid = "???";
			isFromModdedBlock = true;
		}
		//calculate values for and set material
		boolean indestructible = BlockUtils.getHardness(block) == -1;
		float hardness = Math.min(BlockUtils.getHardness(block), 100);
		if (indestructible) 
			hardness = 100;
		float blastResistance = BlockUtils.getBlastResistance(block);
		boolean requiresTool = BlockUtils.getRequiresTool(block);
		boolean isSolid = BlockUtils.getIsSolid(block);
		//BlockArmor.LOGGER.info(getItemStackDisplayName(stack, null).getString()+": hardness = "+hardness+", blastResistance = "+blastResistance+", requiresTool = "+requiresTool+", isSolid = "+isSolid); // TODO remove
		this.armorDamageReduction = hardness >= 8 ? (hardness * 0.003f + 4.5f) : (hardness * 0.65f); 
		if (requiresTool)
			this.armorDamageReduction *= 1.2f;
		if (!isSolid)
			this.armorDamageReduction *= 0.5f;
		if (indestructible)
			this.armorDamageReduction *= 20f;
		this.armorDamageReduction = Math.max((armorDamageReduction * 0.6f), 1);
		this.armorDurability = (int) (indestructible ? 0 : Math.min(30, armorDamageReduction * 8f));
		this.armorToughness = Math.min(20, blastResistance > 100 ? blastResistance / 400f : 0);
		this.armorEnchantability = 12;
		this.armorKnockbackResistance = 0;  // leaving 0 cuz of set effects that give it
		this.createMaterial();

		CommandDev.addBlockName(this); 
	}

	/**Create material for this set based on block values*/
	public void createMaterial() {
		int[] reductionAmounts = new int[] {(int) (armorDamageReduction), (int) (armorDamageReduction*2f), (int) (armorDamageReduction*2.5f), (int) (armorDamageReduction*1.45f)};
		this.material = new BlockArmorMaterial(getItemStackDisplayName(stack, null)+" Material", 
				armorDurability, reductionAmounts, armorEnchantability, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, armorToughness,
				armorKnockbackResistance, () -> {
					return Ingredient.fromItems(new IItemProvider[]{this.item});
				});
		//BlockArmor.LOGGER.info(getItemStackDisplayName(stack, null).getString()+": blockHardness = "+armorDamageReduction+", toughness = "+armorToughness+", durability = "+armorDurability+", reductionAmounts = "+Arrays.toString(reductionAmounts)); // TODO remove
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
	public static void setup() {
		//create list of all ItemStacks with different display names and list of the display names
		ArrayList<String> displayNames = new ArrayList<String>();
		Block[] blocks = Iterators.toArray(Registry.BLOCK.iterator(), Block.class);
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		for (Block block : blocks) {
			try { 
				ItemStack stack = new ItemStack(block);
				boolean manuallyAdded = false;
				for (Block manualBlock : MANUALLY_ADDED_SETS)
					if (block == manualBlock)
						manuallyAdded = true;

				if (manuallyAdded || (stack.getItem() != null && !stack.getDisplayName().getString().isEmpty() && 
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
		modidToSetMap = Maps.newHashMap();
		for (ItemStack stack : stacks) {
			if (isValid(stack) && ArmorSet.getSet(stack.getItem()) == null) {
				String registryName = getItemStackRegistryName(stack);
				if (!registryNames.contains(registryName) && !registryName.isEmpty()) {
					try {
						ArmorSet set = new ArmorSet(stack);
						allSets.add(set);
						nameToSetMap.put(set.registryName, set);
						TreeSet<ArmorSet> list = modidToSetMap.containsKey(set.modid) ? modidToSetMap.get(set.modid) : Sets.newTreeSet(new Comparator<ArmorSet>() {
							@Override
							public int compare(ArmorSet s1, ArmorSet s2) {
								return s1.registryName.compareTo(s2.registryName);
							}
						});
						list.add(set);
						modidToSetMap.put(set.modid, list);
						registryNames.add(registryName);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
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
	@Nullable
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
				if (setCounts.get(description).getB() >= Config.piecesForSet) 
					effects.add(setCounts.get(description).getA());
		}
		return effects;
	}

	/**Does this entity have this worn set effect type active*/
	public static boolean hasSetEffect(LivingEntity entity, SetEffect effect) {
		for (SetEffect effect2 : getWornSetEffects(entity))
			if (effect.getClass() == effect2.getClass())
				return true;
		return false;
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
			// manually added block
			for (Block manualBlock : MANUALLY_ADDED_SETS)
				if (stack != null && stack.getItem() == manualBlock.asItem())
					return true;
			// not BlockItem, bad modded item, ore/ingot, or unnamed
			if (stack == null || 
					!(stack.getItem() instanceof BlockItem) || 
					stack.getItem().getRegistryName().getNamespace().contains("one_point_twelve_concrete") ||
					stack.getItem().getRegistryName().getNamespace().contains("railcraft") ||
					stack.getItem().getRegistryName().getNamespace().contains("ore") || 
					stack.getItem().getRegistryName().getNamespace().contains("ingot") || 
					stack.getDisplayName().getString().contains(".name") || 
					stack.getDisplayName().getString().contains("Ore") ||
					stack.getDisplayName().getString().contains("%") || 
					stack.getDisplayName().getString().contains("Ingot"))
				return false;
			// bad blocks
			Block block = ((BlockItem)stack.getItem()).getBlock();
			if (block instanceof FlowingFluidBlock || 
					block instanceof ContainerBlock || 
					block.hasTileEntity(block.getDefaultState()) || 
					block instanceof OreBlock || 
					block instanceof CropsBlock || 
					block instanceof BushBlock ||
					block == Blocks.BARRIER || 
					block instanceof SlabBlock || 
					block instanceof SilverfishBlock ||
					block.getRenderType(block.getDefaultState()) != BlockRenderType.MODEL ||
					block == Blocks.IRON_BLOCK || 
					block == Blocks.GOLD_BLOCK || 
					block == Blocks.DIAMOND_BLOCK ||
					block == Blocks.AIR ||
					block == Blocks.SNOW)
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
			if (block.getDefaultState().getCollisionShape(null, BlockPos.ZERO, ISelectionContext.dummy()) != VoxelShapes.fullCube())
				return false;

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
			if (armor != null) {
				//add to tab
				if (isFromModdedBlock) {
					if (BlockArmor.moddedTab == null)
						BlockArmor.moddedTab = new BlockArmorCreativeTab("blockArmorModded");
					BlockArmor.moddedTab.orderedStacks.add(new ItemStack(armor));
					armor.group = BlockArmor.moddedTab;
				}
				else {
					if (BlockArmor.vanillaTab == null)
						BlockArmor.vanillaTab = new BlockArmorCreativeTab("blockArmorVanilla");
					BlockArmor.vanillaTab.orderedStacks.add(new ItemStack(armor));
					armor.group = BlockArmor.vanillaTab;
				}
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
			armor.group = null;

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
		//System.out.println("init textures ("+this.item.getRegistryName()+") ====================================="); // TODO remove
		boolean missingTextures = false;

		if (missingSprite == null)
			missingSprite = MissingTextureSprite
			.create(new AtlasTexture(ModelBakery.MODEL_MISSING/*new ResourceLocation("missingno")*/), 0, 16, 16, 0, 0);

		int numTextures = 0;
		this.sprites = new TextureAtlasSprite[EquipmentSlotType.values().length];
		this.animations = new AnimationMetadataSection[EquipmentSlotType.values().length];
		this.frames = new float[EquipmentSlotType.values().length];
		this.colors = new int[EquipmentSlotType.values().length];
		for (int i=0; i<colors.length; i++)
			this.colors[i] = -1;
		BlockState state = this.block.getDefaultState();
		// state overrides
		if (this.block == Blocks.REDSTONE_LAMP)
			state = state.with(RedstoneLampBlock.LIT, true);

		//Gets textures from item model's BakedQuads (textures for each side)
		List<BakedQuad> list = new ArrayList<BakedQuad>();
		try {
			IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);

			Random rand = new Random();
			//getting quads may throw exception if a mod's modeler doesn't obey @Nullable
			list.addAll(model.getQuads(state, null, rand));
			for (Direction facing : Direction.values()) 
				list.addAll(model.getQuads(state, facing, rand));

			for (BakedQuad quad : list) {
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
		catch (Exception e) {
			e.printStackTrace();
		}

		//Check for block texture overrides
		if (TEXTURE_OVERRIDES.contains(block))
			for (EquipmentSlotType slot : SLOTS) {
				ResourceLocation texture = new ResourceLocation(BlockArmor.MODID+":textures/items/overrides/"+
						block.getRegistryName().getPath().toLowerCase().replace(" ", "_")+"_"+slot.getName()+".png");
				try {
					Minecraft.getInstance().getResourceManager().getResource(texture); //does texture exist?
					texture = new ResourceLocation(texture.getNamespace(), texture.getPath().replace("textures/", "").replace(".png", ""));
					TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).getSprite(texture);
					this.sprites[slot.getIndex()] = sprite;
					this.animations[slot.getIndex()] = null;
					BlockArmor.LOGGER.info("Override texture for "+this.stack.getDisplayName().getString()+" "+slot.getName()+" found at: "+texture.toString());
				} catch (Exception e) {
					BlockArmor.LOGGER.info("Override texture for "+this.stack.getDisplayName().getString()+" "+slot.getName()+" NOT found at: "+texture.toString()); 
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

		this.isTranslucent = state.getRenderType() != BlockRenderType.MODEL;// TODO (<- this doesn't work) this.block.getBlockLayer() != BlockRenderLayer.SOLID && this.block != Blocks.SUGAR_CANE;
		if (this.isTranslucent)
			System.out.println(this.block+" is translucent"); // TODO remove

		return new Tuple(numTextures, missingTextures);
	}
}