package xy177.nethersdelightlegacy;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xy177.nethersdelightlegacy.common.compat.NDCompat;
import xy177.nethersdelightlegacy.common.compat.NDFarmersDelightApiCompat;
import xy177.nethersdelightlegacy.common.config.NDConfig;
import xy177.nethersdelightlegacy.common.recipe.NDRecipeRegistry;
import xy177.nethersdelightlegacy.common.registry.NDBlocks;
import xy177.nethersdelightlegacy.common.registry.NDItems;
import xy177.nethersdelightlegacy.common.tile.TileEntityStuffedHoglin;
import xy177.nethersdelightlegacy.common.world.PropelplantWorldGenerator;

@Mod(
    modid = NethersDelightLegacy.MODID,
    name = NethersDelightLegacy.NAME,
    version = NethersDelightLegacy.VERSION,
    dependencies = "required-after:farmersdelight;after:futuremc"
)
public class NethersDelightLegacy {
    public static final String MODID = "nethers_delight_legacy";
    public static final String NAME = "Nether's Delight Legacy";
    public static final String VERSION = "1.0.6";
    private static final String NETHERIZED_MODID = "netherized";
    private static final String NETHER_BACKPORT_MODID = "nb";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        validateNetherDependency();
        NDConfig.load(event.getSuggestedConfigurationFile());
        GameRegistry.registerTileEntity(TileEntityStuffedHoglin.class, MODID + ":stuffed_hoglin");
        GameRegistry.registerWorldGenerator(new PropelplantWorldGenerator(), 0);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NDRecipeRegistry.registerEarlyCompatibilityRecipes();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        NDFarmersDelightApiCompat.registerApis();
        NDRecipeRegistry.registerAll();
    }

    private void validateNetherDependency() {
        if (Loader.isModLoaded(NETHERIZED_MODID) || Loader.isModLoaded(NETHER_BACKPORT_MODID)) {
            return;
        }

        throw new IllegalStateException(
            NAME + " requires one of these mods to be installed: Netherized or Unseens Nether Backport."
        );
    }
}
