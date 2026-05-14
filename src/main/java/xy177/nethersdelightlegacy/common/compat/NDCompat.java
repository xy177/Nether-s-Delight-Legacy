package xy177.nethersdelightlegacy.common.compat;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.LinkedHashSet;
import java.util.Set;

public final class NDCompat {
    public static final String FARMERS_DELIGHT = "farmersdelight";
    public static final String FUTURE_MC = "futuremc";
    public static final String NETHERIZED = "netherized";
    public static final String NETHER_BACKPORT = "nb";

    private NDCompat() {
    }

    public static boolean isLoaded(String modid) {
        return Loader.isModLoaded(modid);
    }

    public static Item findItem(String modid, String path) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(modid, path));
    }

    public static Item getPreferredNetheriteIngot() {
        return firstPresent(
            findItem(FUTURE_MC, "netherite_ingot"),
            findItem(NETHERIZED, "netherite_ingot"),
            findItem(NETHER_BACKPORT, "netherite_ingot")
        );
    }

    public static Item getPreferredNetheriteScrap() {
        return firstPresent(
            findItem(FUTURE_MC, "netherite_scrap"),
            findItem(NETHERIZED, "netherite_scrap"),
            findItem(NETHER_BACKPORT, "nether_scrap")
        );
    }

    public static Item getPreferredBlackstone() {
        return firstPresent(
            findItem(FUTURE_MC, "blackstone"),
            findItem(NETHERIZED, "blackstone"),
            findItem(NETHER_BACKPORT, "black_stone")
        );
    }

    public static Item getPreferredSoulSoil() {
        return firstPresent(
            findItem(FUTURE_MC, "soul_soil"),
            findItem(NETHERIZED, "soul_soil"),
            findItem(NETHER_BACKPORT, "soul_soil")
        );
    }

    public static Item getPreferredCrimsonFungus() {
        return firstPresent(
            findItem(FUTURE_MC, "crimson_fungus"),
            findItem(NETHERIZED, "crimson_fungus"),
            findItem(NETHER_BACKPORT, "crimson_fungus")
        );
    }

    public static Item getPreferredWarpedFungus() {
        return firstPresent(
            findItem(FUTURE_MC, "warped_fungus"),
            findItem(NETHERIZED, "warped_fungus"),
            findItem(NETHER_BACKPORT, "warped_fungus")
        );
    }

    public static Item getPreferredCrimsonRoots() {
        return firstPresent(
            findItem(FUTURE_MC, "crimson_roots"),
            findItem(NETHERIZED, "crimson_roots"),
            findItem(NETHER_BACKPORT, "crimson_roots")
        );
    }

    public static Item getPreferredWarpedRoots() {
        return firstPresent(
            findItem(FUTURE_MC, "warped_roots"),
            findItem(NETHERIZED, "warped_roots"),
            findItem(NETHER_BACKPORT, "warped_roots")
        );
    }

    public static Item getPreferredTwistingVines() {
        return firstPresent(
            findItem(FUTURE_MC, "twisting_vines"),
            findItem(NETHERIZED, "twisting_vines"),
            findItem(NETHER_BACKPORT, "warped_vine")
        );
    }

    public static Item getPreferredSmithingTable() {
        return firstPresent(
            findItem(FUTURE_MC, "smithing_table"),
            findItem(NETHERIZED, "smithing_table"),
            findItem(NETHER_BACKPORT, "smithing_table")
        );
    }

    public static void registerOreDictionaryEntries(Item... items) {
        for (Item item : items) {
            if (item != null) {
                OreDictionary.registerOre("raw_strider", item);
            }
        }
    }

    public static void registerNetherBackportOreDictionary() {
        registerOre("cropCrimsonFungus",
            findItem(FUTURE_MC, "crimson_fungus"),
            findItem(NETHERIZED, "crimson_fungus"),
            findItem(NETHER_BACKPORT, "crimson_fungus")
        );
        registerOre("cropWarpedFungus",
            findItem(FUTURE_MC, "warped_fungus"),
            findItem(NETHERIZED, "warped_fungus"),
            findItem(NETHER_BACKPORT, "warped_fungus")
        );
        registerOre("cropCrimsonRoots",
            findItem(FUTURE_MC, "crimson_roots"),
            findItem(NETHERIZED, "crimson_roots"),
            findItem(NETHER_BACKPORT, "crimson_roots")
        );
        registerOre("cropWarpedRoots",
            findItem(FUTURE_MC, "warped_roots"),
            findItem(NETHERIZED, "warped_roots"),
            findItem(NETHER_BACKPORT, "warped_roots")
        );
        registerOre("cropTwistingVines",
            findItem(FUTURE_MC, "twisting_vines"),
            findItem(NETHERIZED, "twisting_vines"),
            findItem(NETHER_BACKPORT, "warped_vine")
        );
        registerOre("blockSoulSoil",
            findItem(FUTURE_MC, "soul_soil"),
            findItem(NETHERIZED, "soul_soil"),
            findItem(NETHER_BACKPORT, "soul_soil")
        );
        registerOre("blockBlackstone",
            findItem(FUTURE_MC, "blackstone"),
            findItem(NETHERIZED, "blackstone"),
            findItem(NETHER_BACKPORT, "black_stone")
        );
        registerOre("ingotNetherite",
            findItem(FUTURE_MC, "netherite_ingot"),
            findItem(NETHERIZED, "netherite_ingot"),
            findItem(NETHER_BACKPORT, "netherite_ingot")
        );
        registerOre("scrapNetherite",
            findItem(FUTURE_MC, "netherite_scrap"),
            findItem(NETHERIZED, "netherite_scrap"),
            findItem(NETHER_BACKPORT, "nether_scrap")
        );
    }

    private static void registerOre(String oreName, Item... items) {
        Set<Item> uniqueItems = new LinkedHashSet<>();
        for (Item item : items) {
            if (item != null) {
                uniqueItems.add(item);
            }
        }
        for (Item item : uniqueItems) {
            OreDictionary.registerOre(oreName, item);
        }
    }

    private static Item firstPresent(Item... items) {
        for (Item item : items) {
            if (item != null) {
                return item;
            }
        }
        return null;
    }
}
