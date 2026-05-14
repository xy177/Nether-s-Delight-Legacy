package xy177.nethersdelightlegacy.common.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MagmaGelatinItem extends NDFoodItem {
    public MagmaGelatinItem() {
        super(1, 6.0F, false, Items.BUCKET, EnumAction.DRINK);
        setMaxStackSize(1);
    }

    @Override
    protected void afterFoodEaten(ItemStack stack, World worldIn, EntityLivingBase consumer) {
        if (!consumer.isImmuneToFire()) {
            consumer.setFire(60);
        }
    }
}
