package xy177.nethersdelightlegacy.common.recipe;

import com.wdcftgg.farmersdelightlegacy.api.recipe.CookingPotRecipeApi;
import com.wdcftgg.farmersdelightlegacy.api.recipe.CuttingBoardRecipeApi;
import com.wdcftgg.farmersdelightlegacy.common.recipe.manager.CampfireCookingRecipeManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xy177.nethersdelightlegacy.NethersDelightLegacy;
import xy177.nethersdelightlegacy.common.compat.NDCompat;
import xy177.nethersdelightlegacy.common.registry.NDBlocks;
import xy177.nethersdelightlegacy.common.registry.NDItems;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class NDRecipeRegistry {
    private static final Logger LOGGER = LogManager.getLogger(NethersDelightLegacy.MODID);
    private static boolean registered;
    private static boolean futureMcSmithingRegistered;

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

    public static void registerEarlyCompatibilityRecipes() {
        if (Loader.isModLoaded(NDCompat.FUTURE_MC)) {
            futureMcSmithingRegistered = tryRegisterFutureMcSmithing();
        }
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

        registerFungusColonyCuttingRecipe(
            "crimson_fungus_colony",
            NDBlocks.CRIMSON_FUNGUS_COLONY_ITEM,
            NDCompat.getPreferredCrimsonFungus()
        );
        registerFungusColonyCuttingRecipe(
            "warped_fungus_colony",
            NDBlocks.WARPED_FUNGUS_COLONY_ITEM,
            NDCompat.getPreferredWarpedFungus()
        );
    }

    private static void registerFungusColonyCuttingRecipe(String name, Item colony, Item fungus) {
        if (colony != null && fungus != null) {
            CuttingBoardRecipeApi.registerRecipe(
                id(name),
                new ItemStack(colony),
                null,
                new ItemStack(fungus, 5),
                1.0F
            );
        }
    }

    private static void registerNetheriteMacheteUpgrade() {
        if (futureMcSmithingRegistered || tryRegisterFutureMcSmithing() || tryRegisterNetherizedUpgrade()) {
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
            Item netheriteIngot = NDCompat.getPreferredNetheriteIngot();
            if (netheriteIngot == null) {
                return false;
            }
            ItemStack baseProbe = new ItemStack(NDItems.DIAMOND_MACHETE, 1, OreDictionary.WILDCARD_VALUE);
            ItemStack materialProbe = new ItemStack(netheriteIngot);
            Method getRecipe = recipesClass.getMethod("getRecipe", ItemStack.class, ItemStack.class);
            Object existingRecipe = getRecipe.invoke(instance, baseProbe, materialProbe);
            if (existingRecipe != null) {
                return isOutput(existingRecipe, NDItems.NETHERITE_MACHETE);
            }
            Constructor<?> ctor = recipeClass.getConstructor(Ingredient.class, Ingredient.class, ItemStack.class);
            Object recipe = ctor.newInstance(
                Ingredient.fromStacks(baseProbe),
                Ingredient.fromItem(netheriteIngot),
                new ItemStack(NDItems.NETHERITE_MACHETE)
            );
            recipes.add(recipe);
            LOGGER.info("Registered the Future-MC smithing table netherite machete recipe.");
            return true;
        } catch (ReflectiveOperationException exception) {
            LOGGER.error("Failed to register the Future-MC smithing table netherite machete recipe.", exception);
            return false;
        }
    }

    private static boolean isOutput(Object recipe, Item expectedItem) throws ReflectiveOperationException {
        Method getOutput = recipe.getClass().getMethod("getOutput");
        ItemStack output = (ItemStack) getOutput.invoke(recipe);
        return output.getItem() == expectedItem;
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
