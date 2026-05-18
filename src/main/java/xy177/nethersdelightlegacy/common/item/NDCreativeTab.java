package xy177.nethersdelightlegacy.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import xy177.nethersdelightlegacy.NethersDelightLegacy;
import xy177.nethersdelightlegacy.common.registry.NDBlocks;
import xy177.nethersdelightlegacy.common.registry.NDItems;

public class NDCreativeTab extends CreativeTabs {
    public static final NDCreativeTab INSTANCE = new NDCreativeTab();

    private NDCreativeTab() {
        super(NethersDelightLegacy.MODID);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(NDItems.HOGLIN_LOIN == null ? Items.COOKED_BEEF : NDItems.HOGLIN_LOIN);
    }

    @Override
    public void displayAllRelevantItems(NonNullList<ItemStack> items) {
        add(items, NDBlocks.BLACKSTONE_STOVE_ITEM);

        add(items, NDBlocks.CRIMSON_FUNGUS_COLONY_ITEM);
        add(items, NDBlocks.WARPED_FUNGUS_COLONY_ITEM);
        add(items, NDBlocks.SOUL_COMPOST_ITEM);
        add(items, NDBlocks.RICH_SOUL_SOIL_ITEM);
        add(items, NDBlocks.MIMICARNATION_ITEM);
        add(items, NDBlocks.HOGLIN_TROPHY_ITEM);
        add(items, NDItems.RAW_STUFFED_HOGLIN);
        add(items, NDBlocks.STUFFED_HOGLIN_ITEM);
        add(items, NDBlocks.PROPELPLANT_CANE_ITEM);
        add(items, NDItems.PROPELPEARL);
        add(items, NDBlocks.PROPELPLANT_TORCH_ITEM);

        add(items, NDItems.IRON_MACHETE);
        add(items, NDItems.GOLDEN_MACHETE);
        add(items, NDItems.DIAMOND_MACHETE);
        add(items, NDItems.NETHERITE_MACHETE);

        add(items, NDItems.HOGLIN_LOIN);
        add(items, NDItems.HOGLIN_SIRLOIN);
        add(items, NDItems.HOGLIN_EAR);
        add(items, NDItems.STRIDER_SLICE);
        add(items, NDItems.GRILLED_STRIDER);
        add(items, NDItems.GROUND_STRIDER);
        add(items, NDItems.WARPED_MOLDY_MEAT);
        add(items, NDItems.STRIDER_MOSS_STEW);
        add(items, NDItems.PLATE_OF_STUFFED_HOGLIN_SNOUT);
        add(items, NDItems.PLATE_OF_STUFFED_HOGLIN_HAM);
        add(items, NDItems.PLATE_OF_STUFFED_HOGLIN_ROAST);
        add(items, NDItems.NETHER_SKEWER);
        add(items, NDItems.MAGMA_GELATIN);

        add(items, NDItems.HOGLIN_HIDE);
    }

    private static void add(NonNullList<ItemStack> items, Item item) {
        if (item != null) {
            items.add(new ItemStack(item));
        }
    }
}
