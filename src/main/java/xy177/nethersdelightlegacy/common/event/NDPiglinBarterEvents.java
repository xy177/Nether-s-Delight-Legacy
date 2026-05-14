package xy177.nethersdelightlegacy.common.event;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xy177.nethersdelightlegacy.NethersDelightLegacy;
import xy177.nethersdelightlegacy.common.registry.NDItems;

@Mod.EventBusSubscriber(modid = NethersDelightLegacy.MODID)
public final class NDPiglinBarterEvents {
    private static final ResourceLocation NB_PIGLIN_TRADE = new ResourceLocation("nb", "piglin_trade");
    private static final ResourceLocation NETHERIZED_PIGLIN_TRADE = new ResourceLocation("netherized", "piglin_trade");

    private NDPiglinBarterEvents() {
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if ((!NB_PIGLIN_TRADE.equals(event.getName()) && !NETHERIZED_PIGLIN_TRADE.equals(event.getName())) || NDItems.PROPELPEARL == null) {
            return;
        }

        LootPool pool = event.getTable().getPool("main");
        if (pool == null) {
            LootTable table = event.getTable();
            LootEntry entry = createPropelpearlEntry();
            table.addPool(new LootPool(new LootEntry[]{entry}, new LootCondition[0], new RandomValueRange(1), new RandomValueRange(0), "main"));
            return;
        }

        pool.addEntry(createPropelpearlEntry());
    }

    private static LootEntry createPropelpearlEntry() {
        LootFunction setCount = new SetCount(new LootCondition[0], new RandomValueRange(1.0F, 3.0F));
        return new LootEntryItem(NDItems.PROPELPEARL, 20, 0, new LootFunction[]{setCount}, new LootCondition[0], NethersDelightLegacy.MODID + ":propelpearl_barter");
    }
}
