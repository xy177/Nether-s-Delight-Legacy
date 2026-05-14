package xy177.nethersdelightlegacy.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xy177.nethersdelightlegacy.common.compat.NDCompat;
import xy177.nethersdelightlegacy.common.registry.NDBlocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompositionRecipeWrapper implements IRecipeWrapper {
    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(ItemStack.class, Arrays.asList(new ItemStack(NDBlocks.SOUL_COMPOST_ITEM)));
        ingredients.setOutput(ItemStack.class, new ItemStack(NDBlocks.RICH_SOUL_SOIL_ITEM));
    }

    public List<ItemStack> getFlameStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(new ItemStack(Items.FLINT_AND_STEEL));

        Item lantern = NDCompat.findItem(NDCompat.FUTURE_MC, "lantern");
        if (lantern != null) {
            stacks.add(new ItemStack(lantern));
        }

        Item soulLantern = NDCompat.findItem(NDCompat.FUTURE_MC, "soul_lantern");
        if (soulLantern != null) {
            stacks.add(new ItemStack(soulLantern));
        }

        Item campfire = NDCompat.findItem(NDCompat.FUTURE_MC, "campfire");
        if (campfire != null) {
            stacks.add(new ItemStack(campfire));
        }

        Item soulCampfire = NDCompat.findItem(NDCompat.FUTURE_MC, "soul_campfire");
        if (soulCampfire != null) {
            stacks.add(new ItemStack(soulCampfire));
        }

        return stacks;
    }

    public List<ItemStack> getAcceleratorStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        if (NDCompat.getPreferredCrimsonFungus() != null) {
            stacks.add(new ItemStack(NDCompat.getPreferredCrimsonFungus()));
        }
        if (NDCompat.getPreferredWarpedFungus() != null) {
            stacks.add(new ItemStack(NDCompat.getPreferredWarpedFungus()));
        }
        stacks.add(new ItemStack(Blocks.BONE_BLOCK));
        stacks.add(new ItemStack(Blocks.NETHER_WART_BLOCK));
        stacks.add(new ItemStack(NDBlocks.SOUL_COMPOST_ITEM));
        stacks.add(new ItemStack(NDBlocks.RICH_SOUL_SOIL_ITEM));
        return stacks;
    }
}
