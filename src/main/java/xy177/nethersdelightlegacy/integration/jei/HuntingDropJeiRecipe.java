package xy177.nethersdelightlegacy.integration.jei;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class HuntingDropJeiRecipe implements IRecipeWrapper {
    private final ResourceLocation entityId;
    private final List<ItemStack> toolOptions;
    private final ItemStack output;
    private final boolean burningVariant;

    private World currentWorld;
    private EntityLivingBase entityInstance;
    private boolean entityErrored;
    private double renderScale = -1.0D;

    public HuntingDropJeiRecipe(ResourceLocation entityId, List<ItemStack> toolOptions, ItemStack output, boolean burningVariant) {
        this.entityId = entityId;
        this.toolOptions = ImmutableList.copyOf(toolOptions);
        this.output = output;
        this.burningVariant = burningVariant;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setOutput(ItemStack.class, output);
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if (minecraft.world != null && minecraft.world != currentWorld) {
            currentWorld = minecraft.world;
            entityInstance = null;
            entityErrored = false;
            renderScale = -1.0D;
        }

        if (entityInstance == null && !entityErrored && minecraft.world != null) {
            try {
                net.minecraft.entity.Entity entity = EntityList.createEntityByIDFromName(entityId, minecraft.world);
                if (entity instanceof EntityLivingBase) {
                    entityInstance = (EntityLivingBase) entity;
                } else {
                    entityErrored = true;
                }
            } catch (Exception e) {
                entityErrored = true;
            }
        }

        if (entityInstance != null) {
            forceAdultDisplay(entityInstance);

            if (burningVariant) {
                entityInstance.setFire(1);
            } else {
                entityInstance.extinguish();
            }

            if (renderScale < 0.0D) {
                double width = entityInstance.width;
                double height = entityInstance.height;
                renderScale = width > height ? 13.0D / width : 32.0D / height;
            }

            GlStateManager.enableDepth();
            GlStateManager.pushMatrix();
            GlStateManager.translate(35, 45, 0);
            GuiInventory.drawEntityOnScreen(0, 0, (int) Math.round(renderScale), -100, 0, entityInstance);
            GlStateManager.popMatrix();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }

        float theta = (float) (Math.PI / 2 * ((minecraft.world.getTotalWorldTime() + minecraft.getRenderPartialTicks()) / 10f + 5));
        double movementX = Math.sin(theta);
        double movementY = Math.cos(theta);

        double x = 50 + movementX * 16;
        double y = 35 + movementY * 8;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.rotate((float) (120 * movementX), 0, 0, 1);
        GlStateManager.disableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        minecraft.getRenderItem().renderItemAndEffectIntoGUI(getAnimatedTool(minecraft), 0, -16);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();

        if (burningVariant) {
            minecraft.fontRenderer.drawString(I18n.format("nethers_delight_legacy.jei.hunting_drops.burning"), 40, 55, 0xAA3333);
        }
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (mouseX > 9 && mouseY > 8 && mouseX < 42 && mouseY < 57) {
            if (entityInstance != null) {
                ModContainer mod = Loader.instance().getIndexedModList().get(entityId.getResourceDomain());
                return ImmutableList.of(
                    entityInstance.getName(),
                    TextFormatting.BLUE + "" + TextFormatting.ITALIC + (mod == null ? "Unknown" : mod.getName())
                );
            }
            return Collections.singletonList(entityId.toString());
        }
        return Collections.emptyList();
    }

    public ItemStack getOutput() {
        return output;
    }

    public boolean isBurningVariant() {
        return burningVariant;
    }

    private ItemStack getAnimatedTool(Minecraft minecraft) {
        if (toolOptions.isEmpty()) {
            return ItemStack.EMPTY;
        }
        long time = minecraft.world == null ? Minecraft.getSystemTime() : minecraft.world.getTotalWorldTime();
        return toolOptions.get((int) ((time / 20L) % toolOptions.size()));
    }

    private static void forceAdultDisplay(EntityLivingBase entity) {
        if (entity instanceof EntityAgeable) {
            ((EntityAgeable) entity).setGrowingAge(0);
        }
        if (entity instanceof EntityZombie) {
            ((EntityZombie) entity).setChild(false);
        }
    }
}
