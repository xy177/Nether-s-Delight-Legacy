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
import net.minecraft.item.Item;
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
    private static final String HUNTING_DROPS = "farmersdelight.hunting_drops";
    private static final String HARVEST_DROPS = "farmersdelight.harvest_drops";

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(
            new CompositionRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipeRegistryPlugin(new PriorityPlugin());
        registry.addRecipes(Arrays.asList(new CompositionRecipeWrapper()), NDJeiRecipeTypes.COMPOSITION);
        registry.addRecipeCatalyst(new ItemStack(NDBlocks.SOUL_COMPOST_ITEM), NDJeiRecipeTypes.COMPOSITION);
        addMacheteCatalysts(registry);
    }

    private static void addMacheteCatalysts(IModRegistry registry) {
        addMacheteCatalyst(registry, NDItems.IRON_MACHETE);
        addMacheteCatalyst(registry, NDItems.GOLDEN_MACHETE);
        addMacheteCatalyst(registry, NDItems.DIAMOND_MACHETE);
        addMacheteCatalyst(registry, NDItems.NETHERITE_MACHETE);
    }

    private static void addMacheteCatalyst(IModRegistry registry, Item item) {
        if (item == null) {
            return;
        }
        ItemStack stack = new ItemStack(item);
        registry.addRecipeCatalyst(stack, HUNTING_DROPS);
        registry.addRecipeCatalyst(stack, HARVEST_DROPS);
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
