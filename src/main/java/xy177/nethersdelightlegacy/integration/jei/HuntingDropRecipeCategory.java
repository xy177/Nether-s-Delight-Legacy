package xy177.nethersdelightlegacy.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xy177.nethersdelightlegacy.NethersDelightLegacy;
import xy177.nethersdelightlegacy.common.registry.NDItems;

import java.util.Collections;
import java.util.List;

public class HuntingDropRecipeCategory implements IRecipeCategory<HuntingDropJeiRecipe> {
    private static final ResourceLocation BG = new ResourceLocation(NethersDelightLegacy.MODID, "textures/gui/jei/hunting_drops.png");

    private final IDrawable background;
    private final IDrawable icon;
    static HuntingDropRecipeCategory instance;

    public HuntingDropRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(BG, 0, 0, 134, 66, 0, 0, 0, 0);
        this.icon = helper.createDrawableIngredient(new ItemStack(NDItems.IRON_MACHETE));
        instance = this;
    }

    @Override
    public String getUid() {
        return NDJeiRecipeTypes.HUNTING_DROPS;
    }

    @Override
    public String getTitle() {
        return I18n.format("nethers_delight_legacy.jei.hunting_drops.title");
    }

    @Override
    public String getModName() {
        return NethersDelightLegacy.NAME;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, HuntingDropJeiRecipe recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, false, 107, 22);
        recipeLayout.getItemStacks().set(0, recipeWrapper.getOutput());

        recipeLayout.getItemStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (slotIndex == 0) {
                if (!recipeWrapper.isBurningVariant() && ingredient != null && ingredient.getItem() == NDItems.HOGLIN_HIDE) {
                    tooltip.add(I18n.format("nethers_delight_legacy.jei.hunting_drops.hide_note"));
                } else if (ingredient != null && ingredient.getItem() == NDItems.STRIDER_SLICE) {
                    tooltip.add(I18n.format("nethers_delight_legacy.jei.hunting_drops.strider_note"));
                }
            }
        });
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (mouseX > 43 && mouseY > 17 && mouseX < 73 && mouseY < 45) {
            return Collections.singletonList(I18n.format("nethers_delight_legacy.jei.hunting_drops.tools"));
        }
        return Collections.emptyList();
    }
}
