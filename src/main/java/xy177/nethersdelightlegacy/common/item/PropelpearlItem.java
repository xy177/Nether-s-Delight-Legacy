package xy177.nethersdelightlegacy.common.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PropelpearlItem extends NDFoodItem {
    public PropelpearlItem() {
        super(2, 0.5F, false);
    }

    @Override
    protected void afterFoodEaten(ItemStack stack, World worldIn, EntityLivingBase consumer) {
        if (!consumer.isImmuneToFire()) {
            consumer.setFire(4);
        }
    }
}
