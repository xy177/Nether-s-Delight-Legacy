package xy177.nethersdelightlegacy.common.registry;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import xy177.nethersdelightlegacy.NethersDelightLegacy;

@Mod.EventBusSubscriber(modid = NethersDelightLegacy.MODID, value = Side.CLIENT)
public final class NDClientTextures {
    private NDClientTextures() {
    }

    @SubscribeEvent
    public static void stitch(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        register(map, "block/stuffed_hoglin_block");
        register(map, "block/inside_hoglin_block");
        register(map, "block/stuffed_hoglin_tray");
        register(map, "block/tray_top");
        register(map, "block/tray_bottom");
    }

    private static void register(TextureMap map, String path) {
        map.registerSprite(new ResourceLocation(NethersDelightLegacy.MODID, path));
    }
}
