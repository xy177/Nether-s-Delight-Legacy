package xy177.nethersdelightlegacy.common.registry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import com.wdcftgg.farmersdelightlegacy.common.item.ItemMushroomColony;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xy177.nethersdelightlegacy.NethersDelightLegacy;
import xy177.nethersdelightlegacy.common.block.BlockBlackstoneStove;
import xy177.nethersdelightlegacy.common.block.BlockFungusColony;
import xy177.nethersdelightlegacy.common.block.BlockHoglinTrophy;
import xy177.nethersdelightlegacy.common.block.BlockMimicarnation;
import xy177.nethersdelightlegacy.common.block.BlockPottedMimicarnation;
import xy177.nethersdelightlegacy.common.block.BlockPropelplantCane;
import xy177.nethersdelightlegacy.common.block.BlockPropelplantTorch;
import xy177.nethersdelightlegacy.common.block.BlockRichSoulSoil;
import xy177.nethersdelightlegacy.common.block.BlockSoulCompost;
import xy177.nethersdelightlegacy.common.block.BlockStuffedHoglin;
import xy177.nethersdelightlegacy.common.compat.NDCompat;
import xy177.nethersdelightlegacy.common.item.ItemBlockStuffedHoglin;
import xy177.nethersdelightlegacy.common.item.NDCreativeTab;

@Mod.EventBusSubscriber(modid = NethersDelightLegacy.MODID)
public final class NDBlocks {
    public static Block STUFFED_HOGLIN_BLOCK;
    public static Block CRIMSON_FUNGUS_COLONY_BLOCK;
    public static Block WARPED_FUNGUS_COLONY_BLOCK;
    public static Block MIMICARNATION_BLOCK;
    public static Block POTTED_MIMICARNATION_BLOCK;
    public static Block PROPELPLANT_CANE_BLOCK;
    public static Block PROPELPLANT_TORCH_BLOCK;
    public static Block BLACKSTONE_STOVE_BLOCK;
    public static Block HOGLIN_TROPHY_BLOCK;
    public static Block SOUL_COMPOST_BLOCK;
    public static Block RICH_SOUL_SOIL_BLOCK;
    public static Item STUFFED_HOGLIN_ITEM;
    public static Item CRIMSON_FUNGUS_COLONY_ITEM;
    public static Item WARPED_FUNGUS_COLONY_ITEM;
    public static Item MIMICARNATION_ITEM;
    public static Item PROPELPLANT_CANE_ITEM;
    public static Item PROPELPLANT_TORCH_ITEM;
    public static Item BLACKSTONE_STOVE_ITEM;
    public static Item HOGLIN_TROPHY_ITEM;
    public static Item SOUL_COMPOST_ITEM;
    public static Item RICH_SOUL_SOIL_ITEM;

    private NDBlocks() {
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        STUFFED_HOGLIN_BLOCK = new BlockStuffedHoglin();
        CRIMSON_FUNGUS_COLONY_BLOCK = new BlockFungusColony("crimson_fungus");
        WARPED_FUNGUS_COLONY_BLOCK = new BlockFungusColony("warped_fungus");
        MIMICARNATION_BLOCK = new BlockMimicarnation();
        POTTED_MIMICARNATION_BLOCK = new BlockPottedMimicarnation();
        PROPELPLANT_CANE_BLOCK = new BlockPropelplantCane();
        PROPELPLANT_TORCH_BLOCK = new BlockPropelplantTorch();
        BLACKSTONE_STOVE_BLOCK = new BlockBlackstoneStove();
        HOGLIN_TROPHY_BLOCK = new BlockHoglinTrophy();
        SOUL_COMPOST_BLOCK = new BlockSoulCompost();
        RICH_SOUL_SOIL_BLOCK = new BlockRichSoulSoil();
        registerBlock(event, STUFFED_HOGLIN_BLOCK, "stuffed_hoglin");
        registerBlock(event, CRIMSON_FUNGUS_COLONY_BLOCK, "crimson_fungus_colony");
        registerBlock(event, WARPED_FUNGUS_COLONY_BLOCK, "warped_fungus_colony");
        registerBlock(event, MIMICARNATION_BLOCK, "mimicarnation");
        registerBlock(event, POTTED_MIMICARNATION_BLOCK, "potted_mimicarnation", false);
        registerBlock(event, PROPELPLANT_CANE_BLOCK, "propelplant_cane");
        registerBlock(event, PROPELPLANT_TORCH_BLOCK, "propelplant_torch");
        registerBlock(event, BLACKSTONE_STOVE_BLOCK, "blackstone_stove");
        registerBlock(event, HOGLIN_TROPHY_BLOCK, "hoglin_trophy");
        registerBlock(event, SOUL_COMPOST_BLOCK, "soul_compost");
        registerBlock(event, RICH_SOUL_SOIL_BLOCK, "rich_soul_soil");
    }

    @SubscribeEvent
    public static void registerBlockItems(RegistryEvent.Register<Item> event) {
        STUFFED_HOGLIN_ITEM = new ItemBlockStuffedHoglin(STUFFED_HOGLIN_BLOCK)
            .setRegistryName(STUFFED_HOGLIN_BLOCK.getRegistryName())
            .setUnlocalizedName(STUFFED_HOGLIN_BLOCK.getUnlocalizedName())
            .setCreativeTab(NDCreativeTab.INSTANCE);
        CRIMSON_FUNGUS_COLONY_ITEM = new ItemMushroomColony(CRIMSON_FUNGUS_COLONY_BLOCK)
            .setRegistryName(CRIMSON_FUNGUS_COLONY_BLOCK.getRegistryName())
            .setUnlocalizedName(CRIMSON_FUNGUS_COLONY_BLOCK.getUnlocalizedName())
            .setCreativeTab(NDCreativeTab.INSTANCE);
        WARPED_FUNGUS_COLONY_ITEM = new ItemMushroomColony(WARPED_FUNGUS_COLONY_BLOCK)
            .setRegistryName(WARPED_FUNGUS_COLONY_BLOCK.getRegistryName())
            .setUnlocalizedName(WARPED_FUNGUS_COLONY_BLOCK.getUnlocalizedName())
            .setCreativeTab(NDCreativeTab.INSTANCE);
        MIMICARNATION_ITEM = createBlockItem(MIMICARNATION_BLOCK);
        PROPELPLANT_CANE_ITEM = createBlockItem(PROPELPLANT_CANE_BLOCK);
        PROPELPLANT_TORCH_ITEM = createBlockItem(PROPELPLANT_TORCH_BLOCK);
        BLACKSTONE_STOVE_ITEM = createBlockItem(BLACKSTONE_STOVE_BLOCK);
        HOGLIN_TROPHY_ITEM = createBlockItem(HOGLIN_TROPHY_BLOCK);
        SOUL_COMPOST_ITEM = createBlockItem(SOUL_COMPOST_BLOCK);
        RICH_SOUL_SOIL_ITEM = createBlockItem(RICH_SOUL_SOIL_BLOCK);
        event.getRegistry().registerAll(
            STUFFED_HOGLIN_ITEM,
            CRIMSON_FUNGUS_COLONY_ITEM,
            WARPED_FUNGUS_COLONY_ITEM,
            MIMICARNATION_ITEM,
            PROPELPLANT_CANE_ITEM,
            PROPELPLANT_TORCH_ITEM,
            BLACKSTONE_STOVE_ITEM,
            HOGLIN_TROPHY_ITEM,
            SOUL_COMPOST_ITEM,
            RICH_SOUL_SOIL_ITEM
        );
        NDItems.STUFFED_HOGLIN = STUFFED_HOGLIN_ITEM;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        if (STUFFED_HOGLIN_ITEM != null) {
            registerModel(STUFFED_HOGLIN_ITEM);
        }
        registerModel(CRIMSON_FUNGUS_COLONY_ITEM);
        registerModel(WARPED_FUNGUS_COLONY_ITEM);
        registerModel(MIMICARNATION_ITEM);
        registerModel(PROPELPLANT_CANE_ITEM);
        registerModel(PROPELPLANT_TORCH_ITEM);
        registerModel(BLACKSTONE_STOVE_ITEM);
        registerModel(HOGLIN_TROPHY_ITEM);
        registerModel(SOUL_COMPOST_ITEM);
        registerModel(RICH_SOUL_SOIL_ITEM);
    }

    private static void registerBlock(RegistryEvent.Register<Block> event, Block block, String name) {
        registerBlock(event, block, name, true);
    }

    private static void registerBlock(RegistryEvent.Register<Block> event, Block block, String name, boolean creativeTab) {
        block.setRegistryName(NethersDelightLegacy.MODID, name);
        block.setUnlocalizedName(NethersDelightLegacy.MODID + "." + name);
        if (creativeTab) {
            block.setCreativeTab(NDCreativeTab.INSTANCE);
        }
        event.getRegistry().register(block);
    }

    private static Item createBlockItem(Block block) {
        return new ItemBlock(block)
            .setRegistryName(block.getRegistryName())
            .setUnlocalizedName(block.getUnlocalizedName())
            .setCreativeTab(NDCreativeTab.INSTANCE);
    }

    @SideOnly(Side.CLIENT)
    private static void registerModel(Item item) {
        if (item != null) {
            ModelLoader.setCustomModelResourceLocation(
                item,
                0,
                new ModelResourceLocation(item.getRegistryName(), "inventory")
            );
        }
    }
}
