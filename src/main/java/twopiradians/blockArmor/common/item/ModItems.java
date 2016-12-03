package twopiradians.blockArmor.common.item;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import twopiradians.blockArmor.common.BlockArmor;

public class ModItems
{
	/*
	public static ArmorMaterial stone = EnumHelper.addArmorMaterial("stone", "blockarmor:stone", 12, new int[] {1,3,4,2}, 5, null, 0);      
	public static Item stone_helmet;
	public static Item stone_chestplate;
	public static Item stone_leggings;
	public static Item stone_boots;

	public static ArmorMaterial granite = EnumHelper.addArmorMaterial("granite", "blockarmor:granite", 12, new int[] {1,3,4,2}, 5, null, 0);      
	public static Item granite_helmet;
	public static Item granite_chestplate;
	public static Item granite_leggings;
	public static Item granite_boots;

	public static ArmorMaterial smoothgranite = EnumHelper.addArmorMaterial("smoothgranite", "blockarmor:smoothgranite", 12, new int[] {1,3,4,2}, 5, null, 0);      
	public static Item smoothgranite_helmet;
	public static Item smoothgranite_chestplate;
	public static Item smoothgranite_leggings;
	public static Item smoothgranite_boots;

	public static ArmorMaterial diorite = EnumHelper.addArmorMaterial("diorite", "blockarmor:diorite", 12, new int[] {1,3,4,2}, 5, null, 0);      
	public static Item diorite_helmet;
	public static Item diorite_chestplate;
	public static Item diorite_leggings;
	public static Item diorite_boots;

	public static ArmorMaterial smoothdiorite = EnumHelper.addArmorMaterial("smoothdiorite", "blockarmor:smoothdiorite", 12, new int[] {1,3,4,2}, 5, null, 0);      
	public static Item smoothdiorite_helmet;
	public static Item smoothdiorite_chestplate;
	public static Item smoothdiorite_leggings;
	public static Item smoothdiorite_boots;

	public static ArmorMaterial andesite = EnumHelper.addArmorMaterial("andesite", "blockarmor:andesite", 12, new int[] {1,3,4,2}, 5, null, 0);      
	public static Item andesite_helmet;
	public static Item andesite_chestplate;
	public static Item andesite_leggings;
	public static Item andesite_boots;

	public static ArmorMaterial smoothandesite = EnumHelper.addArmorMaterial("smoothandesite", "blockarmor:smoothandesite", 12, new int[] {1,3,4,2}, 5, null, 0);      
	public static Item smoothandesite_helmet;
	public static Item smoothandesite_chestplate;
	public static Item smoothandesite_leggings;
	public static Item smoothandesite_boots;

	public static ArmorMaterial dirt = EnumHelper.addArmorMaterial("dirt", "blockarmor:dirt", 3, new int[] {1, 1, 1, 1}, 15, null, 0);      
	public static Item dirt_helmet;
	public static Item dirt_chestplate;
	public static Item dirt_leggings;
	public static Item dirt_boots;

	public static ArmorMaterial cobble = EnumHelper.addArmorMaterial("cobble", "blockarmor:cobble", 12, new int[] {1,3,4,2}, 5, null, 0);      
	public static Item cobble_helmet;
	public static Item cobble_chestplate;
	public static Item cobble_leggings;
	public static Item cobble_boots;

	public static ArmorMaterial oakwoodplanks = EnumHelper.addArmorMaterial("oakwoodplanks", "blockarmor:oakwoodplanks", 10, new int[] {1, 2, 3, 1}, 10, null, 0);      
	public static Item oakwoodplanks_helmet;
	public static Item oakwoodplanks_chestplate;
	public static Item oakwoodplanks_leggings;
	public static Item oakwoodplanks_boots;

	public static ArmorMaterial sprucewoodplanks = EnumHelper.addArmorMaterial("sprucewoodplanks", "blockarmor:sprucewoodplanks", 10, new int[] {1, 2,3, 1}, 10, null, 0);      
	public static Item sprucewoodplanks_helmet;
	public static Item sprucewoodplanks_chestplate;
	public static Item sprucewoodplanks_leggings;
	public static Item sprucewoodplanks_boots;

	public static ArmorMaterial birchwoodplanks = EnumHelper.addArmorMaterial("birchwoodplanks", "blockarmor:birchwoodplanks", 10, new int[] {1, 2, 3, 1}, 10, null, 0);      
	public static Item birchwoodplanks_helmet;
	public static Item birchwoodplanks_chestplate;
	public static Item birchwoodplanks_leggings;
	public static Item birchwoodplanks_boots;

	public static ArmorMaterial junglewoodplanks = EnumHelper.addArmorMaterial("junglewoodplanks", "blockarmor:junglewoodplanks", 10, new int[] {1, 2, 3, 1}, 10, null, 0);      
	public static Item junglewoodplanks_helmet;
	public static Item junglewoodplanks_chestplate;
	public static Item junglewoodplanks_leggings;
	public static Item junglewoodplanks_boots;

	public static ArmorMaterial acaciawoodplanks = EnumHelper.addArmorMaterial("acaciawoodplanks", "blockarmor:acaciawoodplanks", 10, new int[] {1, 2, 3, 1}, 10, null, 0);      
	public static Item acaciawoodplanks_helmet;
	public static Item acaciawoodplanks_chestplate;
	public static Item acaciawoodplanks_leggings;
	public static Item acaciawoodplanks_boots;

	public static ArmorMaterial darkoakwoodplanks = EnumHelper.addArmorMaterial("darkoakwoodplanks", "blockarmor:darkoakwoodplanks", 10, new int[] {1, 2, 3, 1}, 10, null, 0);      
	public static Item darkoakwoodplanks_helmet;
	public static Item darkoakwoodplanks_chestplate;
	public static Item darkoakwoodplanks_leggings;
	public static Item darkoakwoodplanks_boots;

	public static ArmorMaterial bedrock = EnumHelper.addArmorMaterial("bedrock", "blockarmor:bedrock", 0, new int[] {10,15,20,10}, 25, null, 20);      
	public static Item bedrock_helmet;
	public static Item bedrock_chestplate;
	public static Item bedrock_leggings;
	public static Item bedrock_boots;

	public static ArmorMaterial oakwood = EnumHelper.addArmorMaterial("oakwood", "blockarmor:oakwood", 10, new int[] {2,3,3,2}, 10, null, 0);      
	public static Item oakwood_helmet;
	public static Item oakwood_chestplate;
	public static Item oakwood_leggings;
	public static Item oakwood_boots;

	public static ArmorMaterial sprucewood = EnumHelper.addArmorMaterial("sprucewood", "blockarmor:sprucewood", 10, new int[] {2,3,3,2}, 10, null, 0);      
	public static Item sprucewood_helmet;
	public static Item sprucewood_chestplate;
	public static Item sprucewood_leggings;
	public static Item sprucewood_boots;

	public static ArmorMaterial birchwood = EnumHelper.addArmorMaterial("birchwood", "blockarmor:birchwood", 10, new int[] {2,3,3,2}, 10, null, 0);      
	public static Item birchwood_helmet;
	public static Item birchwood_chestplate;
	public static Item birchwood_leggings;
	public static Item birchwood_boots;

	public static ArmorMaterial junglewood = EnumHelper.addArmorMaterial("junglewood", "blockarmor:junglewood", 10, new int[] {2,3,3,2}, 10, null, 0);      
	public static Item junglewood_helmet;
	public static Item junglewood_chestplate;
	public static Item junglewood_leggings;
	public static Item junglewood_boots;

	public static ArmorMaterial lapis = EnumHelper.addArmorMaterial("lapis", "blockarmor:lapis", 20, new int[] {1,4,5,2}, 15, null, 0);      
	public static Item lapis_helmet;
	public static Item lapis_chestplate;
	public static Item lapis_leggings;
	public static Item lapis_boots;

	public static ArmorMaterial brick = EnumHelper.addArmorMaterial("brick", "blockarmor:brick", 15, new int[] {1,3,4,2}, 10, null, 0);      
	public static Item brick_helmet;
	public static Item brick_chestplate;
	public static Item brick_leggings;
	public static Item brick_boots;

	public static ArmorMaterial obsidian = EnumHelper.addArmorMaterial("obsidian", "blockarmor:obsidian", 50, new int[] {3,5,6,3}, 15, null, 2);      
	public static Item obsidian_helmet;
	public static Item obsidian_chestplate;
	public static Item obsidian_leggings;
	public static Item obsidian_boots;

	public static ArmorMaterial snow = EnumHelper.addArmorMaterial("snow", "blockarmor:snow", 5, new int[] {1,2,2,1}, 20, null, 0);      
	public static Item snow_helmet;
	public static Item snow_chestplate;
	public static Item snow_leggings;
	public static Item snow_boots;

	public static ArmorMaterial netherrack = EnumHelper.addArmorMaterial("netherrack", "blockarmor:netherrack", 3, new int[] {1,1,1,1}, 20, null, 0);      
	public static Item netherrack_helmet;
	public static Item netherrack_chestplate;
	public static Item netherrack_leggings;
	public static Item netherrack_boots;

	public static ArmorMaterial endstone = EnumHelper.addArmorMaterial("endstone", "blockarmor:endstone", 12, new int[] {2,3,4,2}, 15, null, 0);      
	public static Item endstone_helmet;
	public static Item endstone_chestplate;
	public static Item endstone_leggings;
	public static Item endstone_boots;

	public static ArmorMaterial emerald = EnumHelper.addArmorMaterial("emerald", "blockarmor:emerald", 25, new int[] {3,5,6,4}, 25, null, 0);      
	public static Item emerald_helmet;
	public static Item emerald_chestplate;
	public static Item emerald_leggings;
	public static Item emerald_boots;

	public static ArmorMaterial quartz = EnumHelper.addArmorMaterial("quartz", "blockarmor:quartz", 10, new int[] {1,4,5,2}, 25, null, 0);      
	public static Item quartz_helmet;
	public static Item quartz_chestplate;
	public static Item quartz_leggings;
	public static Item quartz_boots;

	public static ArmorMaterial acaciawood = EnumHelper.addArmorMaterial("acaciawood", "blockarmor:acaciawood", 10, new int[] {2,3,3,2}, 10, null, 0);      
	public static Item acaciawood_helmet;
	public static Item acaciawood_chestplate;
	public static Item acaciawood_leggings;
	public static Item acaciawood_boots;

	public static ArmorMaterial darkoakwood = EnumHelper.addArmorMaterial("darkoakwood", "blockarmor:darkoakwood", 10, new int[] {2,3,3,2}, 10, null, 0);      
	public static Item darkoakwood_helmet;
	public static Item darkoakwood_chestplate;
	public static Item darkoakwood_leggings;
	public static Item darkoakwood_boots;

	public static ArmorMaterial darkprismarine = EnumHelper.addArmorMaterial("darkprismarine", "blockarmor:darkprismarine", 15, new int[] {1,4,5,2}, 12, null, 0);      
	public static Item darkprismarine_helmet;
	public static Item darkprismarine_chestplate;
	public static Item darkprismarine_leggings;
	public static Item darkprismarine_boots;

	public static ArmorMaterial slime = EnumHelper.addArmorMaterial("slime", "blockarmor:slime", 2, new int[] {1,1,1,1}, 20, null, 0);      
	public static Item slime_helmet;
	public static Item slime_chestplate;
	public static Item slime_leggings;
	public static Item slime_boots;

	public static ArmorMaterial redstone = EnumHelper.addArmorMaterial("redstone", "blockarmor:redstone", 20, new int[] {2,3,4,2}, 20, null, 0);      
	public static Item redstone_helmet;
	public static Item redstone_chestplate;
	public static Item redstone_leggings;
	public static Item redstone_boots;

	public static ArmorMaterial sugarcane = EnumHelper.addArmorMaterial("sugarcane", "blockarmor:sugarcane", 3, new int[] {1,1,1,1}, 25, null, 0);      
	public static Item sugarcane_helmet;
	public static Item sugarcane_chestplate;
	public static Item sugarcane_leggings;
	public static Item sugarcane_boots;

	public static ArmorMaterial poliwag = EnumHelper.addArmorMaterial("poliwag", "blockarmor:poliwag", 2, new int[] {1,2,3, 1}, 10, null, 0);      
	public static Item poliwag_helmet;
	public static Item poliwag_chestplate;
	public static Item poliwag_leggings;
	public static Item poliwag_boots;*/

	public static ArrayList<ItemModArmor> allArmors = new ArrayList<ItemModArmor>();

	public static void postInit() 
	{
		ArrayList<ArmorSet> generatedSets = new ArrayList<ArmorSet>();

		for (ArmorSet set : ArmorSet.allSets) {
			if (!ArmorSet.autoGeneratedSets.containsKey(set) || ArmorSet.autoGeneratedSets.get(set) == true) {
				generatedSets.add(set);
				String name = set.item.getRegistryName().getResourcePath().toLowerCase().replace(" ", "_");
				if (set.stack.getHasSubtypes())
					name += "_"+set.meta;
				set.helmet = (ItemModArmor) registerItemWithTab(new ItemModArmor(set.material, 0, EntityEquipmentSlot.HEAD), name+"_helmet");
				set.chestplate = (ItemModArmor) registerItemWithTab(new ItemModArmor(set.material, 0, EntityEquipmentSlot.CHEST), name+"_chestplate");
				set.leggings = (ItemModArmor) registerItemWithTab(new ItemModArmor(set.material, 0,EntityEquipmentSlot.LEGS), name+"_leggings");
				set.boots = (ItemModArmor) registerItemWithTab(new ItemModArmor(set.material, 0, EntityEquipmentSlot.FEET), name+"_boots");
			}
		}
		
		System.out.println("[Block Armor] "+generatedSets.size()+" armor sets generated."); //TODO replace with logger
		for (ArmorSet set : generatedSets) 
			System.out.println("- "+set.stack.getDisplayName());
		/*
		stone_helmet = registerItemWithTab(new ItemModArmor(stone, 0, EntityEquipmentSlot.HEAD), "stone_helmet");
		stone_chestplate = registerItemWithTab(new ItemModArmor(stone, 0, EntityEquipmentSlot.CHEST), "stone_chestplate");
		stone_leggings = registerItemWithTab(new ItemModArmor(stone, 0,EntityEquipmentSlot.LEGS), "stone_leggings");
		stone_boots = registerItemWithTab(new ItemModArmor(stone, 0, EntityEquipmentSlot.FEET), "stone_boots");

		granite_helmet = registerItemWithTab(new ItemModArmor(granite, 0, EntityEquipmentSlot.HEAD), "granite_helmet");
		granite_chestplate = registerItemWithTab(new ItemModArmor(granite, 0, EntityEquipmentSlot.CHEST), "granite_chestplate");
		granite_leggings = registerItemWithTab(new ItemModArmor(granite, 0,EntityEquipmentSlot.LEGS), "granite_leggings");
		granite_boots = registerItemWithTab(new ItemModArmor(granite, 0, EntityEquipmentSlot.FEET), "granite_boots");

		smoothgranite_helmet = registerItemWithTab(new ItemModArmor(smoothgranite, 0, EntityEquipmentSlot.HEAD), "smoothgranite_helmet");
		smoothgranite_chestplate = registerItemWithTab(new ItemModArmor(smoothgranite, 0, EntityEquipmentSlot.CHEST), "smoothgranite_chestplate");
		smoothgranite_leggings = registerItemWithTab(new ItemModArmor(smoothgranite, 0,EntityEquipmentSlot.LEGS), "smoothgranite_leggings");
		smoothgranite_boots = registerItemWithTab(new ItemModArmor(smoothgranite, 0, EntityEquipmentSlot.FEET), "smoothgranite_boots");

		diorite_helmet = registerItemWithTab(new ItemModArmor(diorite, 0, EntityEquipmentSlot.HEAD), "diorite_helmet");
		diorite_chestplate = registerItemWithTab(new ItemModArmor(diorite, 0, EntityEquipmentSlot.CHEST), "diorite_chestplate");
		diorite_leggings = registerItemWithTab(new ItemModArmor(diorite, 0,EntityEquipmentSlot.LEGS), "diorite_leggings");
		diorite_boots = registerItemWithTab(new ItemModArmor(diorite, 0, EntityEquipmentSlot.FEET), "diorite_boots");

		smoothdiorite_helmet = registerItemWithTab(new ItemModArmor(smoothdiorite, 0, EntityEquipmentSlot.HEAD), "smoothdiorite_helmet");
		smoothdiorite_chestplate = registerItemWithTab(new ItemModArmor(smoothdiorite, 0, EntityEquipmentSlot.CHEST), "smoothdiorite_chestplate");
		smoothdiorite_leggings = registerItemWithTab(new ItemModArmor(smoothdiorite, 0,EntityEquipmentSlot.LEGS), "smoothdiorite_leggings");
		smoothdiorite_boots = registerItemWithTab(new ItemModArmor(smoothdiorite, 0, EntityEquipmentSlot.FEET), "smoothdiorite_boots");

		andesite_helmet = registerItemWithTab(new ItemModArmor(andesite, 0, EntityEquipmentSlot.HEAD), "andesite_helmet");
		andesite_chestplate = registerItemWithTab(new ItemModArmor(andesite, 0, EntityEquipmentSlot.CHEST), "andesite_chestplate");
		andesite_leggings = registerItemWithTab(new ItemModArmor(andesite, 0,EntityEquipmentSlot.LEGS), "andesite_leggings");
		andesite_boots = registerItemWithTab(new ItemModArmor(andesite, 0, EntityEquipmentSlot.FEET), "andesite_boots");

		smoothandesite_helmet = registerItemWithTab(new ItemModArmor(smoothandesite, 0, EntityEquipmentSlot.HEAD), "smoothandesite_helmet");
		smoothandesite_chestplate = registerItemWithTab(new ItemModArmor(smoothandesite, 0, EntityEquipmentSlot.CHEST), "smoothandesite_chestplate");
		smoothandesite_leggings = registerItemWithTab(new ItemModArmor(smoothandesite, 0,EntityEquipmentSlot.LEGS), "smoothandesite_leggings");
		smoothandesite_boots = registerItemWithTab(new ItemModArmor(smoothandesite, 0, EntityEquipmentSlot.FEET), "smoothandesite_boots");

		dirt_helmet = registerItemWithTab(new ItemModArmor(dirt, 0, EntityEquipmentSlot.HEAD), "dirt_helmet");
		dirt_chestplate = registerItemWithTab(new ItemModArmor(dirt, 0, EntityEquipmentSlot.CHEST), "dirt_chestplate");
		dirt_leggings = registerItemWithTab(new ItemModArmor(dirt, 0,EntityEquipmentSlot.LEGS), "dirt_leggings");
		dirt_boots = registerItemWithTab(new ItemModArmor(dirt, 0, EntityEquipmentSlot.FEET), "dirt_boots");

		cobble_helmet = registerItemWithTab(new ItemModArmor(cobble, 0, EntityEquipmentSlot.HEAD), "cobble_helmet");
		cobble_chestplate = registerItemWithTab(new ItemModArmor(cobble, 0, EntityEquipmentSlot.CHEST), "cobble_chestplate");
		cobble_leggings = registerItemWithTab(new ItemModArmor(cobble, 0,EntityEquipmentSlot.LEGS), "cobble_leggings");
		cobble_boots = registerItemWithTab(new ItemModArmor(cobble, 0, EntityEquipmentSlot.FEET), "cobble_boots");

		oakwoodplanks_helmet = registerItemWithTab(new ItemModArmor(oakwoodplanks, 0, EntityEquipmentSlot.HEAD), "oakwoodplanks_helmet");
		oakwoodplanks_chestplate = registerItemWithTab(new ItemModArmor(oakwoodplanks, 0, EntityEquipmentSlot.CHEST), "oakwoodplanks_chestplate");
		oakwoodplanks_leggings = registerItemWithTab(new ItemModArmor(oakwoodplanks, 0,EntityEquipmentSlot.LEGS), "oakwoodplanks_leggings");
		oakwoodplanks_boots = registerItemWithTab(new ItemModArmor(oakwoodplanks, 0, EntityEquipmentSlot.FEET), "oakwoodplanks_boots");

		sprucewoodplanks_helmet = registerItemWithTab(new ItemModArmor(sprucewoodplanks, 0, EntityEquipmentSlot.HEAD), "sprucewoodplanks_helmet");
		sprucewoodplanks_chestplate = registerItemWithTab(new ItemModArmor(sprucewoodplanks, 0, EntityEquipmentSlot.CHEST), "sprucewoodplanks_chestplate");
		sprucewoodplanks_leggings = registerItemWithTab(new ItemModArmor(sprucewoodplanks, 0,EntityEquipmentSlot.LEGS), "sprucewoodplanks_leggings");
		sprucewoodplanks_boots = registerItemWithTab(new ItemModArmor(sprucewoodplanks, 0, EntityEquipmentSlot.FEET), "sprucewoodplanks_boots");

		birchwoodplanks_helmet = registerItemWithTab(new ItemModArmor(birchwoodplanks, 0, EntityEquipmentSlot.HEAD), "birchwoodplanks_helmet");
		birchwoodplanks_chestplate = registerItemWithTab(new ItemModArmor(birchwoodplanks, 0, EntityEquipmentSlot.CHEST), "birchwoodplanks_chestplate");
		birchwoodplanks_leggings = registerItemWithTab(new ItemModArmor(birchwoodplanks, 0,EntityEquipmentSlot.LEGS), "birchwoodplanks_leggings");
		birchwoodplanks_boots = registerItemWithTab(new ItemModArmor(birchwoodplanks, 0, EntityEquipmentSlot.FEET), "birchwoodplanks_boots");

		junglewoodplanks_helmet = registerItemWithTab(new ItemModArmor(junglewoodplanks, 0, EntityEquipmentSlot.HEAD), "junglewoodplanks_helmet");
		junglewoodplanks_chestplate = registerItemWithTab(new ItemModArmor(junglewoodplanks, 0, EntityEquipmentSlot.CHEST), "junglewoodplanks_chestplate");
		junglewoodplanks_leggings = registerItemWithTab(new ItemModArmor(junglewoodplanks, 0,EntityEquipmentSlot.LEGS), "junglewoodplanks_leggings");
		junglewoodplanks_boots = registerItemWithTab(new ItemModArmor(junglewoodplanks, 0, EntityEquipmentSlot.FEET), "junglewoodplanks_boots");

		acaciawoodplanks_helmet = registerItemWithTab(new ItemModArmor(acaciawoodplanks, 0, EntityEquipmentSlot.HEAD), "acaciawoodplanks_helmet");
		acaciawoodplanks_chestplate = registerItemWithTab(new ItemModArmor(acaciawoodplanks, 0, EntityEquipmentSlot.CHEST), "acaciawoodplanks_chestplate");
		acaciawoodplanks_leggings = registerItemWithTab(new ItemModArmor(acaciawoodplanks, 0,EntityEquipmentSlot.LEGS), "acaciawoodplanks_leggings");
		acaciawoodplanks_boots = registerItemWithTab(new ItemModArmor(acaciawoodplanks, 0, EntityEquipmentSlot.FEET), "acaciawoodplanks_boots");

		darkoakwoodplanks_helmet = registerItemWithTab(new ItemModArmor(darkoakwoodplanks, 0, EntityEquipmentSlot.HEAD), "darkoakwoodplanks_helmet");
		darkoakwoodplanks_chestplate = registerItemWithTab(new ItemModArmor(darkoakwoodplanks, 0, EntityEquipmentSlot.CHEST), "darkoakwoodplanks_chestplate");
		darkoakwoodplanks_leggings = registerItemWithTab(new ItemModArmor(darkoakwoodplanks, 0,EntityEquipmentSlot.LEGS), "darkoakwoodplanks_leggings");
		darkoakwoodplanks_boots = registerItemWithTab(new ItemModArmor(darkoakwoodplanks, 0, EntityEquipmentSlot.FEET), "darkoakwoodplanks_boots");

		bedrock_helmet = registerItemWithTab(new ItemModArmor(bedrock, 0, EntityEquipmentSlot.HEAD), "bedrock_helmet");
		bedrock_chestplate = registerItemWithTab(new ItemModArmor(bedrock, 0, EntityEquipmentSlot.CHEST), "bedrock_chestplate");
		bedrock_leggings = registerItemWithTab(new ItemModArmor(bedrock, 0,EntityEquipmentSlot.LEGS), "bedrock_leggings");
		bedrock_boots = registerItemWithTab(new ItemModArmor(bedrock, 0, EntityEquipmentSlot.FEET), "bedrock_boots");

		oakwood_helmet = registerItemWithTab(new ItemModArmor(oakwood, 0, EntityEquipmentSlot.HEAD), "oakwood_helmet");
		oakwood_chestplate = registerItemWithTab(new ItemModArmor(oakwood, 0, EntityEquipmentSlot.CHEST), "oakwood_chestplate");
		oakwood_leggings = registerItemWithTab(new ItemModArmor(oakwood, 0,EntityEquipmentSlot.LEGS), "oakwood_leggings");
		oakwood_boots = registerItemWithTab(new ItemModArmor(oakwood, 0, EntityEquipmentSlot.FEET), "oakwood_boots");

		sprucewood_helmet = registerItemWithTab(new ItemModArmor(sprucewood, 0, EntityEquipmentSlot.HEAD), "sprucewood_helmet");
		sprucewood_chestplate = registerItemWithTab(new ItemModArmor(sprucewood, 0, EntityEquipmentSlot.CHEST), "sprucewood_chestplate");
		sprucewood_leggings = registerItemWithTab(new ItemModArmor(sprucewood, 0,EntityEquipmentSlot.LEGS), "sprucewood_leggings");
		sprucewood_boots = registerItemWithTab(new ItemModArmor(sprucewood, 0, EntityEquipmentSlot.FEET), "sprucewood_boots");

		birchwood_helmet = registerItemWithTab(new ItemModArmor(birchwood, 0, EntityEquipmentSlot.HEAD), "birchwood_helmet");
		birchwood_chestplate = registerItemWithTab(new ItemModArmor(birchwood, 0, EntityEquipmentSlot.CHEST), "birchwood_chestplate");
		birchwood_leggings = registerItemWithTab(new ItemModArmor(birchwood, 0,EntityEquipmentSlot.LEGS), "birchwood_leggings");
		birchwood_boots = registerItemWithTab(new ItemModArmor(birchwood, 0, EntityEquipmentSlot.FEET), "birchwood_boots");

		junglewood_helmet = registerItemWithTab(new ItemModArmor(junglewood, 0, EntityEquipmentSlot.HEAD), "junglewood_helmet");
		junglewood_chestplate = registerItemWithTab(new ItemModArmor(junglewood, 0, EntityEquipmentSlot.CHEST), "junglewood_chestplate");
		junglewood_leggings = registerItemWithTab(new ItemModArmor(junglewood, 0,EntityEquipmentSlot.LEGS), "junglewood_leggings");
		junglewood_boots = registerItemWithTab(new ItemModArmor(junglewood, 0, EntityEquipmentSlot.FEET), "junglewood_boots");

		lapis_helmet = registerItemWithTab(new ItemModArmor(lapis, 0, EntityEquipmentSlot.HEAD), "lapis_helmet");
		lapis_chestplate = registerItemWithTab(new ItemModArmor(lapis, 0, EntityEquipmentSlot.CHEST), "lapis_chestplate");
		lapis_leggings = registerItemWithTab(new ItemModArmor(lapis, 0,EntityEquipmentSlot.LEGS), "lapis_leggings");
		lapis_boots = registerItemWithTab(new ItemModArmor(lapis, 0, EntityEquipmentSlot.FEET), "lapis_boots");

		brick_helmet = registerItemWithTab(new ItemModArmor(brick, 0, EntityEquipmentSlot.HEAD), "brick_helmet");
		brick_chestplate = registerItemWithTab(new ItemModArmor(brick, 0, EntityEquipmentSlot.CHEST), "brick_chestplate");
		brick_leggings = registerItemWithTab(new ItemModArmor(brick, 0,EntityEquipmentSlot.LEGS), "brick_leggings");
		brick_boots = registerItemWithTab(new ItemModArmor(brick, 0, EntityEquipmentSlot.FEET), "brick_boots");

		obsidian_helmet = registerItemWithTab(new ItemModArmor(obsidian, 0, EntityEquipmentSlot.HEAD), "obsidian_helmet");
		obsidian_chestplate = registerItemWithTab(new ItemModArmor(obsidian, 0, EntityEquipmentSlot.CHEST), "obsidian_chestplate");
		obsidian_leggings = registerItemWithTab(new ItemModArmor(obsidian, 0,EntityEquipmentSlot.LEGS), "obsidian_leggings");
		obsidian_boots = registerItemWithTab(new ItemModArmor(obsidian, 0, EntityEquipmentSlot.FEET), "obsidian_boots");

		snow_helmet = registerItemWithTab(new ItemModArmor(snow, 0, EntityEquipmentSlot.HEAD), "snow_helmet");
		snow_chestplate = registerItemWithTab(new ItemModArmor(snow, 0, EntityEquipmentSlot.CHEST), "snow_chestplate");
		snow_leggings = registerItemWithTab(new ItemModArmor(snow, 0,EntityEquipmentSlot.LEGS), "snow_leggings");
		snow_boots = registerItemWithTab(new ItemModArmor(snow, 0, EntityEquipmentSlot.FEET), "snow_boots");

		netherrack_helmet = registerItemWithTab(new ItemModArmor(netherrack, 0, EntityEquipmentSlot.HEAD), "netherrack_helmet");
		netherrack_chestplate = registerItemWithTab(new ItemModArmor(netherrack, 0, EntityEquipmentSlot.CHEST), "netherrack_chestplate");
		netherrack_leggings = registerItemWithTab(new ItemModArmor(netherrack, 0,EntityEquipmentSlot.LEGS), "netherrack_leggings");
		netherrack_boots = registerItemWithTab(new ItemModArmor(netherrack, 0, EntityEquipmentSlot.FEET), "netherrack_boots");

		endstone_helmet = registerItemWithTab(new ItemModArmor(endstone, 0, EntityEquipmentSlot.HEAD), "endstone_helmet");
		endstone_chestplate = registerItemWithTab(new ItemModArmor(endstone, 0, EntityEquipmentSlot.CHEST), "endstone_chestplate");
		endstone_leggings = registerItemWithTab(new ItemModArmor(endstone, 0,EntityEquipmentSlot.LEGS), "endstone_leggings");
		endstone_boots = registerItemWithTab(new ItemModArmor(endstone, 0, EntityEquipmentSlot.FEET), "endstone_boots");

		emerald_helmet = registerItemWithTab(new ItemModArmor(emerald, 0, EntityEquipmentSlot.HEAD), "emerald_helmet");
		emerald_chestplate = registerItemWithTab(new ItemModArmor(emerald, 0, EntityEquipmentSlot.CHEST), "emerald_chestplate");
		emerald_leggings = registerItemWithTab(new ItemModArmor(emerald, 0,EntityEquipmentSlot.LEGS), "emerald_leggings");
		emerald_boots = registerItemWithTab(new ItemModArmor(emerald, 0, EntityEquipmentSlot.FEET), "emerald_boots");

		quartz_helmet = registerItemWithTab(new ItemModArmor(quartz, 0, EntityEquipmentSlot.HEAD), "quartz_helmet");
		quartz_chestplate = registerItemWithTab(new ItemModArmor(quartz, 0, EntityEquipmentSlot.CHEST), "quartz_chestplate");
		quartz_leggings = registerItemWithTab(new ItemModArmor(quartz, 0,EntityEquipmentSlot.LEGS), "quartz_leggings");
		quartz_boots = registerItemWithTab(new ItemModArmor(quartz, 0, EntityEquipmentSlot.FEET), "quartz_boots");

		acaciawood_helmet = registerItemWithTab(new ItemModArmor(acaciawood, 0, EntityEquipmentSlot.HEAD), "acaciawood_helmet");
		acaciawood_chestplate = registerItemWithTab(new ItemModArmor(acaciawood, 0, EntityEquipmentSlot.CHEST), "acaciawood_chestplate");
		acaciawood_leggings = registerItemWithTab(new ItemModArmor(acaciawood, 0,EntityEquipmentSlot.LEGS), "acaciawood_leggings");
		acaciawood_boots = registerItemWithTab(new ItemModArmor(acaciawood, 0, EntityEquipmentSlot.FEET), "acaciawood_boots");

		darkoakwood_helmet = registerItemWithTab(new ItemModArmor(darkoakwood, 0, EntityEquipmentSlot.HEAD), "darkoakwood_helmet");
		darkoakwood_chestplate = registerItemWithTab(new ItemModArmor(darkoakwood, 0, EntityEquipmentSlot.CHEST), "darkoakwood_chestplate");
		darkoakwood_leggings = registerItemWithTab(new ItemModArmor(darkoakwood, 0,EntityEquipmentSlot.LEGS), "darkoakwood_leggings");
		darkoakwood_boots = registerItemWithTab(new ItemModArmor(darkoakwood, 0, EntityEquipmentSlot.FEET), "darkoakwood_boots");

		darkprismarine_helmet = registerItemWithTab(new ItemModArmor(darkprismarine, 0, EntityEquipmentSlot.HEAD), "darkprismarine_helmet");
		darkprismarine_chestplate = registerItemWithTab(new ItemModArmor(darkprismarine, 0, EntityEquipmentSlot.CHEST), "darkprismarine_chestplate");
		darkprismarine_leggings = registerItemWithTab(new ItemModArmor(darkprismarine, 0,EntityEquipmentSlot.LEGS), "darkprismarine_leggings");
		darkprismarine_boots = registerItemWithTab(new ItemModArmor(darkprismarine, 0, EntityEquipmentSlot.FEET), "darkprismarine_boots");

		slime_helmet = registerItemWithTab(new ItemModArmor(slime, 0, EntityEquipmentSlot.HEAD), "slime_helmet");
		slime_chestplate = registerItemWithTab(new ItemModArmor(slime, 0, EntityEquipmentSlot.CHEST), "slime_chestplate");
		slime_leggings = registerItemWithTab(new ItemModArmor(slime, 0,EntityEquipmentSlot.LEGS), "slime_leggings");
		slime_boots = registerItemWithTab(new ItemModArmor(slime, 0, EntityEquipmentSlot.FEET), "slime_boots");

		redstone_helmet = registerItemWithTab(new ItemModArmor(redstone, 0, EntityEquipmentSlot.HEAD), "redstone_helmet");
		redstone_chestplate = registerItemWithTab(new ItemModArmor(redstone, 0, EntityEquipmentSlot.CHEST), "redstone_chestplate");
		redstone_leggings = registerItemWithTab(new ItemModArmor(redstone, 0,EntityEquipmentSlot.LEGS), "redstone_leggings");
		redstone_boots = registerItemWithTab(new ItemModArmor(redstone, 0, EntityEquipmentSlot.FEET), "redstone_boots");

		sugarcane_helmet = registerItemWithTab(new ItemModArmor(sugarcane, 0, EntityEquipmentSlot.HEAD), "sugarcane_helmet");
		sugarcane_chestplate = registerItemWithTab(new ItemModArmor(sugarcane, 0, EntityEquipmentSlot.CHEST), "sugarcane_chestplate");
		sugarcane_leggings = registerItemWithTab(new ItemModArmor(sugarcane, 0,EntityEquipmentSlot.LEGS), "sugarcane_leggings");
		sugarcane_boots = registerItemWithTab(new ItemModArmor(sugarcane, 0, EntityEquipmentSlot.FEET), "sugarcane_boots");

		poliwag_helmet = registerItemWithTab(new ItemModArmor(poliwag, 0, EntityEquipmentSlot.HEAD), "poliwag_helmet");
		poliwag_chestplate = registerItemWithTab(new ItemModArmor(poliwag, 0, EntityEquipmentSlot.CHEST), "poliwag_chestplate");
		poliwag_leggings = registerItemWithTab(new ItemModArmor(poliwag, 0,EntityEquipmentSlot.LEGS), "poliwag_leggings");
		poliwag_boots = registerItemWithTab(new ItemModArmor(poliwag, 0, EntityEquipmentSlot.FEET), "poliwag_boots");*/
	}

	public static void registerRenders()
	{
		for (ItemModArmor item : allArmors)
			registerRender(item);
	}

	public static Item registerItemWithTab(final Item item, final String unlocalizedName) 
	{
		if (item instanceof ItemModArmor)
			allArmors.add((ItemModArmor) item);
		item.setUnlocalizedName(unlocalizedName);
		item.setRegistryName(BlockArmor.MODID, unlocalizedName);
		item.setCreativeTab(BlockArmor.tab);
		GameRegistry.register(item);
		return item;
	}

	public static Item registerItemWithoutTab(final Item item, final String unlocalizedName) 
	{
		if (item instanceof ItemModArmor)
			allArmors.add((ItemModArmor) item);
		item.setUnlocalizedName(unlocalizedName);
		item.setRegistryName(BlockArmor.MODID, unlocalizedName);
		GameRegistry.register(item);
		return item;
	}

	public static void registerRender(Item item)
	{	
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(BlockArmor.MODID+":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
}