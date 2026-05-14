package xy177.nethersdelightlegacy.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import xy177.nethersdelightlegacy.NethersDelightLegacy;
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

}
