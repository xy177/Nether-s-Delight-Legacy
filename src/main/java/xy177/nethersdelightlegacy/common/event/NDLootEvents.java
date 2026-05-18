package xy177.nethersdelightlegacy.common.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import xy177.nethersdelightlegacy.NethersDelightLegacy;
import xy177.nethersdelightlegacy.common.registry.NDItems;

@Mod.EventBusSubscriber(modid = NethersDelightLegacy.MODID)
public final class NDLootEvents {
    private NDLootEvents() {
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        EntityLivingBase victim = event.getEntityLiving();
        if (isHoglin(victim)) {
            replaceHoglinMeatDrops(event);
        }
    }

    private static void replaceHoglinMeatDrops(LivingDropsEvent event) {
        int meatCount = 0;
        java.util.Iterator<EntityItem> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            EntityItem entityItem = iterator.next();
            ItemStack stack = entityItem.getItem();
            Item item = stack.getItem();
            if (item == Items.PORKCHOP || item == Items.COOKED_PORKCHOP) {
                meatCount += stack.getCount();
                iterator.remove();
            }
        }

        if (meatCount > 0) {
            Item replacement = event.getEntityLiving().isBurning() ? NDItems.HOGLIN_SIRLOIN : NDItems.HOGLIN_LOIN;
            addDrop(event, new ItemStack(replacement, meatCount));
        }
    }

    private static boolean isHoglin(EntityLivingBase entity) {
        String name = EntityRegistry.getEntry(entity.getClass()) == null ? "" : EntityRegistry.getEntry(entity.getClass()).getRegistryName().toString();
        return "netherized:hoglin".equals(name) || "nb:hoglin".equals(name) || "nb:zoglin".equals(name);
    }

    private static void addDrop(LivingDropsEvent event, ItemStack stack) {
        if (!stack.isEmpty()) {
            event.getDrops().add(new EntityItem(event.getEntityLiving().world, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, stack));
        }
    }
}
