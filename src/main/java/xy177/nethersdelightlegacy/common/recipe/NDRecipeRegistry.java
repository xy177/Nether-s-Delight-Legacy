package xy177.nethersdelightlegacy.common.recipe;

import com.wdcftgg.farmersdelightlegacy.api.recipe.CookingPotRecipeApi;
import com.wdcftgg.farmersdelightlegacy.api.recipe.CuttingBoardRecipeApi;
import com.wdcftgg.farmersdelightlegacy.common.recipe.CampfireCookingRecipeManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import xy177.nethersdelightlegacy.NethersDelightLegacy;
import xy177.nethersdelightlegacy.common.compat.NDCompat;
import xy177.nethersdelightlegacy.common.registry.NDItems;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class NDRecipeRegistry {
    private static boolean registered;

    private NDRecipeRegistry() {
    }

    public static void registerAll() {
        if (registered) {
            return;
        }
        registered = true;

        registerFurnaceRecipes();
        registerCampfireRecipes();
        registerCookingPotRecipes();
        registerCuttingBoardRecipes();
        registerNetheriteMacheteUpgrade();
    }

    private static void registerFurnaceRecipes() {
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(NDItems.HOGLIN_LOIN), new ItemStack(NDItems.HOGLIN_SIRLOIN), 0.35F);
    }

    private static void registerCampfireRecipes() {
        CampfireCookingRecipeManager.registerScriptRecipe(
            id("hoglin_sirloin_from_campfire"),
            ingredientList(stackString(NDItems.HOGLIN_LOIN)),
            parseStackString(stackString(NDItems.HOGLIN_SIRLOIN)),
            600
        );
    }

    private static void registerCookingPotRecipes() {
        CookingPotRecipeApi.registerRecipe(
            id("grilled_strider"),
            ingredientList(
                "ore:raw_strider",
                "ore:cropWarpedFungus",
                "ore:cropCrimsonFungus",
                "ore:cropWarpedRoots",
                "ore:cropCrimsonRoots"
            ),
            new ItemStack(NDItems.GRILLED_STRIDER),
            new ItemStack(Items.BOWL),
            200,
            1.0F
        );

        CookingPotRecipeApi.registerRecipe(
            id("magma_gelatin"),
            ingredientList(
                "minecraft:magma_cream",
                "minecraft:magma_cream",
                "minecraft:magma_cream",
                stackString(NDItems.PROPELPEARL)
            ),
            new ItemStack(NDItems.MAGMA_GELATIN),
            new ItemStack(Items.BUCKET),
            200,
            1.0F
        );

        CookingPotRecipeApi.registerRecipe(
            id("strider_moss_stew"),
            ingredientList(
                "ore:cropWarpedFungus",
                "ore:cropCrimsonFungus",
                "ore:cropCrimsonRoots",
                "ore:cropWarpedFungus",
                "ore:raw_strider"
            ),
            new ItemStack(NDItems.STRIDER_MOSS_STEW),
            new ItemStack(Items.BOWL),
            200,
            1.0F
        );

        CookingPotRecipeApi.registerRecipe(
            id("stuffed_hoglin"),
            ingredientList(
                "farmersdelight:nether_salad",
                stackString(NDItems.RAW_STUFFED_HOGLIN),
                "farmersdelight:nether_salad"
            ),
            new ItemStack(NDItems.STUFFED_HOGLIN),
            400,
            2.0F
        );
    }

    private static void registerCuttingBoardRecipes() {
        CuttingBoardRecipeApi.registerRecipe(
            id("ground_strider"),
            stackString(NDItems.STRIDER_SLICE),
            null,
            stackString(NDItems.GROUND_STRIDER),
            2,
            1.0F
        );

        CuttingBoardRecipeApi.registerRecipe(
            id("hoglin_hide"),
            stackString(NDItems.HOGLIN_HIDE),
            null,
            "minecraft:leather",
            4,
            1.0F
        );

        CuttingBoardRecipeApi.registerRecipe(
            id("propelplant_cane"),
            stackString(NDItems.PROPELPLANT_CANE),
            null,
            "minecraft:gunpowder",
            1,
            1.0F
        );
    }

    private static void registerNetheriteMacheteUpgrade() {
        if (tryRegisterFutureMcSmithing() || tryRegisterNetherizedUpgrade()) {
            return;
        }
    }

    private static boolean tryRegisterFutureMcSmithing() {
        if (!NDCompat.isLoaded(NDCompat.FUTURE_MC)) {
            return false;
        }
        try {
            Class<?> recipesClass = Class.forName("thedarkcolour.futuremc.recipe.smithing.SmithingRecipes");
            Class<?> recipeClass = Class.forName("thedarkcolour.futuremc.recipe.smithing.SmithingRecipe");
            Field instanceField = recipesClass.getField("INSTANCE");
            Object instance = instanceField.get(null);
            Method getRecipes = recipesClass.getMethod("getRecipes");
            @SuppressWarnings("unchecked")
            List<Object> recipes = (List<Object>) getRecipes.invoke(instance);
            Constructor<?> ctor = recipeClass.getConstructor(Ingredient.class, Ingredient.class, ItemStack.class);
            Object recipe = ctor.newInstance(
                Ingredient.fromStacks(new ItemStack(NDItems.DIAMOND_MACHETE, 1, 32767)),
                Ingredient.fromItem(NDCompat.getPreferredNetheriteIngot()),
                new ItemStack(NDItems.NETHERITE_MACHETE)
            );
            recipes.add(recipe);
            return true;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static boolean tryRegisterNetherizedUpgrade() {
        if (!NDCompat.isLoaded(NDCompat.NETHERIZED)) {
            return false;
        }
        try {
            Class<?> upgradeClass = Class.forName("mellohi138.netherized.recipe.ArmorUpgradeRecipe");
            Constructor<?> ctor = upgradeClass.getConstructor(Item.class, Item.class);
            Object recipe = ctor.newInstance(NDItems.NETHERITE_MACHETE, NDItems.DIAMOND_MACHETE);
            net.minecraftforge.fml.common.registry.ForgeRegistries.RECIPES.register(
                ((net.minecraft.item.crafting.IRecipe) recipe).setRegistryName(new ResourceLocation(NethersDelightLegacy.MODID, "netherite_machete_upgrade"))
            );
            return true;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static String id(String path) {
        return NethersDelightLegacy.MODID + ":" + path;
    }

    private static String[] ingredientList(String... values) {
        List<String> filtered = new ArrayList<>();
        for (String value : values) {
            if (value != null) {
                filtered.add(value);
            }
        }
        return filtered.toArray(new String[0]);
    }

    private static ItemStack parseStackString(String value) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(value));
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static String stackString(Item item) {
        return item == null || item.getRegistryName() == null ? null : item.getRegistryName().toString();
    }

}
