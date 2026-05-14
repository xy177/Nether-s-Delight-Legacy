package xy177.nethersdelightlegacy.common.event;

import com.wdcftgg.farmersdelightlegacy.common.item.ItemKnife;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import xy177.nethersdelightlegacy.NethersDelightLegacy;
import xy177.nethersdelightlegacy.common.registry.NDItems;

import java.util.Random;

@Mod.EventBusSubscriber(modid = NethersDelightLegacy.MODID)
public final class NDLootEvents {
    private static final ResourceLocation STRIDER_ID = new ResourceLocation("netherized", "strider");
    private static final ResourceLocation STRIDER_ID_NB = new ResourceLocation("nb", "strider");

    private NDLootEvents() {
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        Entity source = event.getSource().getTrueSource();
        if (!(source instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) source;
        ItemStack held = player.getHeldItemMainhand();
        if (held.isEmpty()) {
            return;
        }

        EntityLivingBase victim = event.getEntityLiving();

        if (isHoglin(victim)) {
            handleHoglinDrops(event, held);
        } else if (isStrider(victim) && isHuntingTool(held)) {
            addDrop(event, new ItemStack(NDItems.STRIDER_SLICE, 1 + event.getLootingLevel() + event.getEntityLiving().getRNG().nextInt(2)));
        }
    }

    private static void handleHoglinDrops(LivingDropsEvent event, ItemStack held) {
        Random random = event.getEntityLiving().getRNG();
        replaceHoglinMeatDrops(event);

        if (isHuntingTool(held)) {
            addDrop(event, new ItemStack(NDItems.HOGLIN_HIDE, 1 + event.getLootingLevel() + random.nextInt(2)));
        }

        if (isHuntingTool(held)) {
            Item ham = ModItems.get(event.getEntityLiving().isBurning() ? "smoked_ham" : "ham");
            if (ham != null) {
                addDrop(event, new ItemStack(ham, 1));
            }
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

    private static boolean isHuntingTool(ItemStack stack) {
        return isMachete(stack) || ItemKnife.isKnife(stack);
    }

    private static boolean isMachete(ItemStack stack) {
        Item item = stack.getItem();
        return item == NDItems.IRON_MACHETE
            || item == NDItems.DIAMOND_MACHETE
            || item == NDItems.GOLDEN_MACHETE
            || item == NDItems.NETHERITE_MACHETE;
    }

    private static boolean isHoglin(EntityLivingBase entity) {
        String name = EntityRegistry.getEntry(entity.getClass()) == null ? "" : EntityRegistry.getEntry(entity.getClass()).getRegistryName().toString();
        return "netherized:hoglin".equals(name) || "nb:hoglin".equals(name) || "nb:zoglin".equals(name);
    }

    private static boolean isStrider(EntityLivingBase entity) {
        ResourceLocation id = EntityRegistry.getEntry(entity.getClass()) == null ? null : EntityRegistry.getEntry(entity.getClass()).getRegistryName();
        return STRIDER_ID.equals(id) || STRIDER_ID_NB.equals(id);
    }

    private static void addDrop(LivingDropsEvent event, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        event.getDrops().add(new EntityItem(event.getEntityLiving().world, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, stack));
    }
}
