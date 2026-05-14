package xy177.nethersdelightlegacy.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeRegistryPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModItems;
import xy177.nethersdelightlegacy.common.registry.NDItems;
import xy177.nethersdelightlegacy.common.registry.NDBlocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@JEIPlugin
public class NethersDelightLegacyJeiPlugin implements IModPlugin {
    private static final String CRAFTING = "minecraft.crafting";
    private static final String COOKING_POT = "farmersdelight.cooking_pot";
    private static final String CUTTING_BOARD = "farmersdelight.cutting_board";

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(
            new CompositionRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
            new HuntingDropRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipeRegistryPlugin(new PriorityPlugin());
        registry.addRecipes(Arrays.asList(new CompositionRecipeWrapper()), NDJeiRecipeTypes.COMPOSITION);
        registry.addRecipes(createHuntingDropRecipes(), NDJeiRecipeTypes.HUNTING_DROPS);
        registry.addRecipeCatalyst(new ItemStack(NDBlocks.SOUL_COMPOST_ITEM), NDJeiRecipeTypes.COMPOSITION);
        addHuntingDropCatalysts(registry);
    }

    private static List<HuntingDropJeiRecipe> createHuntingDropRecipes() {
        List<HuntingDropJeiRecipe> recipes = new ArrayList<>();
        List<ItemStack> tools = createHuntingToolStacks();

        if (Loader.isModLoaded("netherized")) {
            recipes.add(new HuntingDropJeiRecipe(
                new ResourceLocation("netherized", "hoglin"),
                tools,
                new ItemStack(NDItems.HOGLIN_HIDE),
                false
            ));
            recipes.add(new HuntingDropJeiRecipe(
                new ResourceLocation("netherized", "hoglin"),
                tools,
                new ItemStack(ModItems.get("ham")),
                false
            ));
            recipes.add(new HuntingDropJeiRecipe(
                new ResourceLocation("netherized", "hoglin"),
                tools,
                new ItemStack(ModItems.get("smoked_ham")),
                true
            ));
            recipes.add(new HuntingDropJeiRecipe(
                new ResourceLocation("netherized", "strider"),
                tools,
                new ItemStack(NDItems.STRIDER_SLICE),
                false
            ));
        }

        if (Loader.isModLoaded("nb")) {
            recipes.add(new HuntingDropJeiRecipe(
                new ResourceLocation("nb", "hoglin"),
                tools,
                new ItemStack(NDItems.HOGLIN_HIDE),
                false
            ));
            recipes.add(new HuntingDropJeiRecipe(
                new ResourceLocation("nb", "hoglin"),
                tools,
                new ItemStack(ModItems.get("ham")),
                false
            ));
            recipes.add(new HuntingDropJeiRecipe(
                new ResourceLocation("nb", "hoglin"),
                tools,
                new ItemStack(ModItems.get("smoked_ham")),
                true
            ));
            recipes.add(new HuntingDropJeiRecipe(
                new ResourceLocation("nb", "zoglin"),
                tools,
                new ItemStack(NDItems.HOGLIN_HIDE),
                false
            ));
            recipes.add(new HuntingDropJeiRecipe(
                new ResourceLocation("nb", "zoglin"),
                tools,
                new ItemStack(ModItems.get("ham")),
                false
            ));
            recipes.add(new HuntingDropJeiRecipe(
                new ResourceLocation("nb", "zoglin"),
                tools,
                new ItemStack(ModItems.get("smoked_ham")),
                true
            ));
            recipes.add(new HuntingDropJeiRecipe(
                new ResourceLocation("nb", "strider"),
                tools,
                new ItemStack(NDItems.STRIDER_SLICE),
                false
            ));
        }

        return recipes;
    }

    private static List<ItemStack> createHuntingToolStacks() {
        List<ItemStack> tools = new ArrayList<>();
        tools.add(new ItemStack(NDItems.IRON_MACHETE));
        tools.add(new ItemStack(NDItems.GOLDEN_MACHETE));
        tools.add(new ItemStack(NDItems.DIAMOND_MACHETE));
        tools.add(new ItemStack(NDItems.NETHERITE_MACHETE));
        addIfPresent(tools, ModItems.get("flint_knife"));
        addIfPresent(tools, ModItems.get("iron_knife"));
        addIfPresent(tools, ModItems.get("golden_knife"));
        addIfPresent(tools, ModItems.get("diamond_knife"));
        addIfPresent(tools, ModItems.get("netherite_knife"));
        return tools;
    }

    private static void addHuntingDropCatalysts(IModRegistry registry) {
        for (ItemStack stack : createHuntingToolStacks()) {
            registry.addRecipeCatalyst(stack, NDJeiRecipeTypes.HUNTING_DROPS);
        }
    }

    private static void addIfPresent(List<ItemStack> list, Item item) {
        if (item != null) {
            list.add(new ItemStack(item));
        }
    }

    private static class PriorityPlugin implements IRecipeRegistryPlugin {
        @Override
        public <V> List<String> getRecipeCategoryUids(IFocus<V> focus) {
            if (focus == null || focus.getMode() != IFocus.Mode.OUTPUT) {
                return Collections.emptyList();
            }
            Object value = focus.getValue();
            if (!(value instanceof ItemStack)) {
                return Collections.emptyList();
            }

            Item item = ((ItemStack) value).getItem();
            List<String> ordered = new ArrayList<>();

            if (isCuttingBoardFirst(item)) {
                ordered.add(CUTTING_BOARD);
                ordered.add(CRAFTING);
                return ordered;
            }

            if (isCookingPotFirst(item)) {
                ordered.add(COOKING_POT);
                ordered.add(CRAFTING);
                return ordered;
            }

            return Collections.emptyList();
        }

        @Override
        public <T extends IRecipeWrapper, V> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
            return Collections.emptyList();
        }

        @Override
        public <T extends IRecipeWrapper> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory) {
            return Collections.emptyList();
        }

        private boolean isCookingPotFirst(Item item) {
            return item == NDItems.GRILLED_STRIDER
                || item == NDItems.STRIDER_MOSS_STEW
                || item == NDItems.MAGMA_GELATIN
                || item == NDItems.WARPED_MOLDY_MEAT;
        }

        private boolean isCuttingBoardFirst(Item item) {
            return item == NDItems.GROUND_STRIDER;
        }
    }
}
