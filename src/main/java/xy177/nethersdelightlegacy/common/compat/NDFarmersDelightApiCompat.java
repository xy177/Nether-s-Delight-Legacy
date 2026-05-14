package xy177.nethersdelightlegacy.common.compat;

import com.wdcftgg.farmersdelightlegacy.api.heat.HeatSourceApi;
import xy177.nethersdelightlegacy.NethersDelightLegacy;
import xy177.nethersdelightlegacy.common.registry.NDBlocks;

public final class NDFarmersDelightApiCompat {
    private static boolean registered;

    private NDFarmersDelightApiCompat() {
    }

    public static void registerApis() {
        if (registered) {
            return;
        }
        registered = true;

        if (NDBlocks.BLACKSTONE_STOVE_BLOCK != null) {
            HeatSourceApi.registerDirectHeatSourcePredicate(
                NethersDelightLegacy.MODID + ":blackstone_stove",
                (world, pos, state) -> state.getBlock() == NDBlocks.BLACKSTONE_STOVE_BLOCK
                    && state.getProperties().containsKey(xy177.nethersdelightlegacy.common.block.BlockBlackstoneStove.LIT)
                    && state.getValue(xy177.nethersdelightlegacy.common.block.BlockBlackstoneStove.LIT)
            );
        }
    }
}
