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

public class CompositionRecipeCategory implements IRecipeCategory<CompositionRecipeWrapper> {
    private static final ResourceLocation BG = new ResourceLocation(NethersDelightLegacy.MODID, "textures/gui/jei/composition.png");
    private final IDrawable background;
    private final IDrawable icon;

    public CompositionRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(BG, 0, 0, 118, 80);
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
        recipeLayout.getItemStacks().init(0, true, 9, 26);
        recipeLayout.getItemStacks().set(0, new ItemStack(NDBlocks.SOUL_COMPOST_ITEM));

        recipeLayout.getItemStacks().init(1, false, 93, 26);
        recipeLayout.getItemStacks().set(1, new ItemStack(NDBlocks.RICH_SOUL_SOIL_ITEM));

        recipeLayout.getItemStacks().init(2, true, 37, 54);
        recipeLayout.getItemStacks().set(2, recipeWrapper.getFlameStacks());

        recipeLayout.getItemStacks().init(3, true, 63, 54);
        recipeLayout.getItemStacks().set(3, recipeWrapper.getAcceleratorStacks());

        recipeLayout.getItemStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (slotIndex == 2) {
                tooltip.add(0, getTooltipTitle(true));
                tooltip.add(1, getAcceptedText());
            } else if (slotIndex == 3) {
                tooltip.add(0, getTooltipTitle(false));
                tooltip.add(1, getAcceptedText());
            }
        });
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        minecraft.fontRenderer.drawString(I18n.format("nethers_delight_legacy.jei.composition.nether"), 44, 0, 0x505050);
    }

    private static String getTooltipTitle(boolean flame) {
        if (isChinese()) {
            return flame ? "火焰" : "催化物";
        }
        return I18n.format(flame
            ? "nethers_delight_legacy.jei.composition.light"
            : "nethers_delight_legacy.jei.composition.accelerators");
    }

    private static String getAcceptedText() {
        return isChinese() ? "可接受" : I18n.format("nethers_delight_legacy.jei.composition.accepted");
    }

    private static boolean isChinese() {
        return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode().startsWith("zh");
    }
}
