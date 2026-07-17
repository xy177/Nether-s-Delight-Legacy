package xy177.nethersdelightlegacy.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xy177.nethersdelightlegacy.NethersDelightLegacy;
import xy177.nethersdelightlegacy.common.registry.NDBlocks;

import java.util.Collections;
import java.util.List;

public class CompositionRecipeCategory implements IRecipeCategory<CompositionRecipeWrapper> {
    private static final ResourceLocation BG = new ResourceLocation(NethersDelightLegacy.MODID, "textures/gui/jei/composition.png");
    private final IDrawable background;
    private final IDrawable slotIcon;
    private final IDrawable icon;

    public CompositionRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(BG, 0, 0, 118, 80);
        this.slotIcon = helper.createDrawable(BG, 119, 0, 22, 22);
        this.icon = helper.createDrawableIngredient(new ItemStack(NDBlocks.RICH_SOUL_SOIL_ITEM));
    }

    @Override
    public String getUid() {
        return NDJeiRecipeTypes.COMPOSITION;
    }

    @Override
    public String getTitle() {
        return I18n.format("nethers_delight_legacy.jei.composition.title");
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
    public void setRecipe(IRecipeLayout recipeLayout, CompositionRecipeWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 8, 25);
        recipeLayout.getItemStacks().set(0, new ItemStack(NDBlocks.SOUL_COMPOST_ITEM));

        recipeLayout.getItemStacks().init(1, false, 92, 25);
        recipeLayout.getItemStacks().set(1, new ItemStack(NDBlocks.RICH_SOUL_SOIL_ITEM));

        recipeLayout.getItemStacks().init(2, true, 37, 53);
        recipeLayout.getItemStacks().set(2, recipeWrapper.getFlameStacks());

        recipeLayout.getItemStacks().init(3, true, 63, 53);
        recipeLayout.getItemStacks().set(3, recipeWrapper.getAcceleratorStacks());
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        slotIcon.draw(minecraft, 37, 53);
        slotIcon.draw(minecraft, 63, 53);
        minecraft.fontRenderer.drawString(I18n.format("nethers_delight_legacy.jei.composition.nether"), 44, 0, 0x505050);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (isInside(40, 38, mouseX, mouseY)) {
            return Collections.singletonList(I18n.format("nethers_delight_legacy.jei.composition.light_detail"));
        }
        if (isInside(53, 38, mouseX, mouseY)) {
            return Collections.singletonList(I18n.format("nethers_delight_legacy.jei.composition.fluid_detail"));
        }
        if (isInside(67, 38, mouseX, mouseY)) {
            return Collections.singletonList(I18n.format("nethers_delight_legacy.jei.composition.accelerators_detail"));
        }
        return Collections.emptyList();
    }

    private static boolean isInside(int x, int y, int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + 11 && mouseY >= y && mouseY < y + 11;
    }
}
