package twopiradians.blockArmor.common.item;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import twopiradians.blockArmor.common.BlockArmor;
import twopiradians.blockArmor.common.config.Config;
import twopiradians.blockArmor.common.recipe.RecipeBlockArmor;
import twopiradians.blockArmor.common.seteffect.SetEffect;

public class ModItems
{
	public static ArrayList<ItemBlockArmor> allArmors = new ArrayList<ItemBlockArmor>();

	@Mod.EventBusSubscriber
	public static class RegistrationHandler {

		@SubscribeEvent(priority=EventPriority.LOWEST)
		public static void registerItems(final RegistryEvent.Register<Item> event) {						
			ArmorSet.postInit();
			SetEffect.postInit();
			Config.postInit(BlockArmor.configFile);

			int vanillaItems = 0;
			int moddedItems = 0;

			for (ArmorSet set : ArmorSet.allSets) { 
				if (!Config.disabledSets.contains(set)) {
					String registryName = ArmorSet.getItemStackRegistryName(set.stack);
					set.helmet = register(event.getRegistry(), new ItemBlockArmor(set.material, 0, EntityEquipmentSlot.HEAD, set), registryName+"_helmet", true);
					set.chestplate = register(event.getRegistry(), new ItemBlockArmor(set.material, 0, EntityEquipmentSlot.CHEST, set), registryName+"_chestplate", true);
					set.leggings = register(event.getRegistry(), new ItemBlockArmor(set.material, 0, EntityEquipmentSlot.LEGS, set), registryName+"_leggings", true);
					set.boots = register(event.getRegistry(), new ItemBlockArmor(set.material, 0, EntityEquipmentSlot.FEET, set), registryName+"_boots", true);
					if (set.isFromModdedBlock)
						moddedItems += 4;
					else
						vanillaItems += 4;

					ArrayList<IRecipe> recipes = new ArrayList<IRecipe>(); 
					ItemStack A = set.stack;

					NonNullList<Ingredient> helmetRecipe = NonNullList.from(Ingredient.EMPTY,
							Ingredient.fromStacks(A), Ingredient.fromStacks(A), Ingredient.fromStacks(A),
							Ingredient.fromStacks(A), Ingredient.EMPTY, Ingredient.fromStacks(A));

					NonNullList<Ingredient> armorRecipe = NonNullList.from(Ingredient.EMPTY,
							Ingredient.fromStacks(A), Ingredient.EMPTY, Ingredient.fromStacks(A),
							Ingredient.fromStacks(A), Ingredient.fromStacks(A), Ingredient.fromStacks(A),
							Ingredient.fromStacks(A), Ingredient.fromStacks(A), Ingredient.fromStacks(A));

					NonNullList<Ingredient> legsRecipe = NonNullList.from(Ingredient.EMPTY,
							Ingredient.fromStacks(A), Ingredient.fromStacks(A), Ingredient.fromStacks(A),
							Ingredient.fromStacks(A), Ingredient.EMPTY, Ingredient.fromStacks(A),
							Ingredient.fromStacks(A), Ingredient.EMPTY, Ingredient.fromStacks(A));

					NonNullList<Ingredient> bootsRecipe = NonNullList.from(Ingredient.EMPTY,
							Ingredient.fromStacks(A), Ingredient.EMPTY, Ingredient.fromStacks(A),
							Ingredient.fromStacks(A), Ingredient.EMPTY, Ingredient.fromStacks(A));

					recipes.add(new RecipeBlockArmor(set, BlockArmor.MODNAME, 3, 2, helmetRecipe, new ItemStack(set.helmet)).setRegistryName(set.helmet.getRegistryName()));
					recipes.add(new RecipeBlockArmor(set, BlockArmor.MODNAME, 3, 3, armorRecipe, new ItemStack(set.chestplate)).setRegistryName(set.chestplate.getRegistryName()));
					recipes.add(new RecipeBlockArmor(set, BlockArmor.MODNAME, 3, 3, legsRecipe, new ItemStack(set.leggings)).setRegistryName(set.leggings.getRegistryName()));
					recipes.add(new RecipeBlockArmor(set, BlockArmor.MODNAME, 3, 2, bootsRecipe, new ItemStack(set.boots)).setRegistryName(set.boots.getRegistryName()));

					//add recipes
					for (IRecipe recipe : recipes)
						if (!ForgeRegistries.RECIPES.containsValue(recipe))
							ForgeRegistries.RECIPES.register(recipe);
				}
			}
			for (ArmorSet set : Config.disabledSets)
				ArmorSet.allSets.remove(set);

			BlockArmor.logger.info("Generated "+vanillaItems+" Block Armor items from Vanilla Blocks");
			if (moddedItems > 0)
				BlockArmor.logger.info("Generated "+moddedItems+" Block Armor items from Modded Blocks");

			Config.syncConfig();
		}

		private static ItemBlockArmor register(IForgeRegistry<Item> registry, ItemBlockArmor armor, String itemName, boolean isFromModdedBlock) {
			allArmors.add(armor);
			armor.setRegistryName(BlockArmor.MODID, itemName);
			armor.setUnlocalizedName(armor.getRegistryName().getResourcePath());
			registry.register(armor);
			return armor;
		}

	}

	public static void registerRenders() {
		for (ItemBlockArmor item : allArmors)
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register
			(item, 0, new ModelResourceLocation(BlockArmor.MODID+":" + item.getUnlocalizedName().substring(5), "inventory"));
	}

}