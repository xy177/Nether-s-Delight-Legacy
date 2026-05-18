package xy177.nethersdelightlegacy.common.registry;

import com.wdcftgg.farmersdelightlegacy.common.registry.ModEffects;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import xy177.nethersdelightlegacy.NethersDelightLegacy;
import xy177.nethersdelightlegacy.common.compat.NDCompat;
import xy177.nethersdelightlegacy.common.item.MacheteItem;
import xy177.nethersdelightlegacy.common.item.MagmaGelatinItem;
import xy177.nethersdelightlegacy.common.item.NDCreativeTab;
import xy177.nethersdelightlegacy.common.item.NDFoodItem;
import xy177.nethersdelightlegacy.common.item.PropelpearlItem;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = NethersDelightLegacy.MODID)
public final class NDItems {
    private static final List<Item> ITEMS = new ArrayList<>();

    public static Item HOGLIN_HIDE;
    public static Item RAW_STUFFED_HOGLIN;
    public static Item HOGLIN_LOIN;
    public static Item HOGLIN_SIRLOIN;
    public static Item STRIDER_SLICE;
    public static Item GROUND_STRIDER;
    public static Item HOGLIN_EAR;
    public static Item HOGLIN_TROPHY;
    public static Item WARPED_MOLDY_MEAT;
    public static Item GRILLED_STRIDER;
    public static Item STRIDER_MOSS_STEW;
    public static Item STUFFED_HOGLIN;
    public static Item PLATE_OF_STUFFED_HOGLIN_SNOUT;
    public static Item PLATE_OF_STUFFED_HOGLIN_HAM;
    public static Item PLATE_OF_STUFFED_HOGLIN_ROAST;
    public static Item PROPELPEARL;
    public static Item PROPELPLANT_CANE;
    public static Item PROPELPLANT_TORCH;
    public static Item NETHER_SKEWER;
    public static Item MAGMA_GELATIN;
    public static Item IRON_MACHETE;
    public static Item DIAMOND_MACHETE;
    public static Item GOLDEN_MACHETE;
    public static Item NETHERITE_MACHETE;

    private NDItems() {
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        ITEMS.clear();

        HOGLIN_HIDE = register("hoglin_hide", new Item());
        RAW_STUFFED_HOGLIN = register("raw_stuffed_hoglin", new Item().setMaxStackSize(1));
        HOGLIN_LOIN = register("hoglin_loin", food(3, 0.25F));
        HOGLIN_SIRLOIN = register("hoglin_sirloin", food(7, 0.75F));
        STRIDER_SLICE = register("strider_slice", food(4, 0.7F).addEffect(effect("minecraft", "fire_resistance"), 400, 0, 1.0F));
        GROUND_STRIDER = register("ground_strider", food(2, 0.75F)
            .addEffect(effect("minecraft", "fire_resistance"), 200, 0, 1.0F)
            .addEffect(effect("minecraft", "poison"), 200, 0, 0.35F));
        HOGLIN_EAR = register("hoglin_ear", food(2, 0.5F).setAlwaysEdible());
        HOGLIN_TROPHY = NDBlocks.HOGLIN_TROPHY_ITEM;
        WARPED_MOLDY_MEAT = register("warped_moldy_meat", bowlFood(9, 0.8F)
            .addEffect(effect("minecraft", "blindness"), 200, 0, 1.0F)
            .addEffect(effect("minecraft", "nausea"), 200, 0, 1.0F));
        GRILLED_STRIDER = register("grilled_strider", bowlFood(10, 0.9F)
            .addEffect(effect("minecraft", "fire_resistance"), 600, 0, 1.0F)
            .addEffect(effect("farmersdelight", "nourishment"), 2400, 0, 1.0F));
        STRIDER_MOSS_STEW = register("strider_moss_stew", bowlFood(8, 0.6F)
            .addEffect(effect("farmersdelight", "comfort"), 2400, 0, 1.0F));
        PLATE_OF_STUFFED_HOGLIN_SNOUT = register("plate_of_stuffed_hoglin_snout", bowlFood(14, 0.9F)
            .addEffect(effect("farmersdelight", "nourishment"), 4800, 0, 1.0F));
        PLATE_OF_STUFFED_HOGLIN_HAM = register("plate_of_stuffed_hoglin_ham", bowlFood(10, 0.75F)
            .addEffect(effect("farmersdelight", "nourishment"), 2400, 0, 1.0F));
        PLATE_OF_STUFFED_HOGLIN_ROAST = register("plate_of_stuffed_hoglin_roast", bowlFood(8, 0.6F)
            .addEffect(effect("farmersdelight", "nourishment"), 2400, 0, 1.0F));
        PROPELPLANT_CANE = NDBlocks.PROPELPLANT_CANE_ITEM;
        PROPELPLANT_TORCH = NDBlocks.PROPELPLANT_TORCH_ITEM;
        PROPELPEARL = register("propelpearl", new PropelpearlItem());
        NETHER_SKEWER = register("nether_skewer", new NDFoodItem(7, 0.5F, false, Items.BLAZE_ROD, EnumAction.EAT));
        MAGMA_GELATIN = register("magma_gelatin", new MagmaGelatinItem()
            .addEffect(effect("minecraft", "fire_resistance"), 1200, 0, 1.0F));
        IRON_MACHETE = register("iron_machete", new MacheteItem(ItemTool.ToolMaterial.IRON));
        DIAMOND_MACHETE = register("diamond_machete", new MacheteItem(ItemTool.ToolMaterial.DIAMOND));
        GOLDEN_MACHETE = register("golden_machete", new MacheteItem(ItemTool.ToolMaterial.GOLD));
        NETHERITE_MACHETE = register("netherite_machete", new MacheteItem(createNetheriteMaterial()));

        event.getRegistry().registerAll(ITEMS.toArray(new Item[0]));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerOreDictionary(RegistryEvent.Register<Item> event) {
        NDCompat.registerOreDictionaryEntries(STRIDER_SLICE, GROUND_STRIDER);
        NDCompat.registerNetherBackportOreDictionary();
        OreDictionary.registerOre("toolKnife", IRON_MACHETE);
        OreDictionary.registerOre("toolKnife", GOLDEN_MACHETE);
        OreDictionary.registerOre("toolKnife", DIAMOND_MACHETE);
        OreDictionary.registerOre("toolKnife", NETHERITE_MACHETE);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (Item item : ITEMS) {
            ModelLoader.setCustomModelResourceLocation(
                item,
                0,
                new ModelResourceLocation(item.getRegistryName(), "inventory")
            );
        }
    }

    private static Item register(String name, Item item) {
        item.setRegistryName(NethersDelightLegacy.MODID, name);
        item.setUnlocalizedName(NethersDelightLegacy.MODID + "." + name);
        item.setCreativeTab(NDCreativeTab.INSTANCE);
        ITEMS.add(item);
        return item;
    }

    private static NDFoodItem food(int amount, float saturation) {
        return new NDFoodItem(amount, saturation, true);
    }

    private static NDFoodItem bowlFood(int amount, float saturation) {
        NDFoodItem item = new NDFoodItem(amount, saturation, false, Items.BOWL, EnumAction.EAT);
        item.setMaxStackSize(16);
        return item;
    }

    private static ItemTool.ToolMaterial createNetheriteMaterial() {
        ItemTool.ToolMaterial material = EnumHelper.addToolMaterial(
            "NDL_NETHERITE",
            4,
            2031,
            9.0F,
            4.0F,
            15
        );

        Item repairItem = NDCompat.getPreferredNetheriteIngot();
        if (repairItem != null) {
            material.setRepairItem(new ItemStack(repairItem));
        }
        return material;
    }

    private static ResourceLocation effect(String modid, String path) {
        return new ResourceLocation(modid, path);
    }
}
