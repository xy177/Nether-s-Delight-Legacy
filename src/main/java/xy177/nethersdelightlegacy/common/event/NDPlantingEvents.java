package xy177.nethersdelightlegacy.common.event;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xy177.nethersdelightlegacy.NethersDelightLegacy;
import xy177.nethersdelightlegacy.common.registry.NDBlocks;

@Mod.EventBusSubscriber(modid = NethersDelightLegacy.MODID)
public final class NDPlantingEvents {
    private NDPlantingEvents() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();
        EnumFacing face = event.getFace();

        if (stack.isEmpty() || face != EnumFacing.UP) {
            return;
        }

        Block clickedBlock = world.getBlockState(event.getPos()).getBlock();
        if (clickedBlock != NDBlocks.SOUL_COMPOST_BLOCK && clickedBlock != NDBlocks.RICH_SOUL_SOIL_BLOCK) {
            return;
        }

        Block plantBlock = getSupportedPlantBlock(stack.getItem(), clickedBlock);
        if (plantBlock == null) {
            return;
        }

        BlockPos placePos = event.getPos().up();
        if (!world.getBlockState(placePos).getBlock().isReplaceable(world, placePos)) {
            return;
        }

        if (!world.isRemote) {
            world.setBlockState(placePos, plantBlock.getDefaultState(), 3);
            plantBlock.onBlockPlacedBy(world, placePos, plantBlock.getDefaultState(), player, stack);

            SoundType soundType = plantBlock.getSoundType();
            world.playSound(
                null,
                placePos,
                soundType.getPlaceSound(),
                SoundCategory.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F,
                soundType.getPitch() * 0.8F
            );

            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }

        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }

    private static Block getSupportedPlantBlock(Item item, Block clickedBlock) {
        if (item.getRegistryName() == null) {
            return null;
        }

        String key = item.getRegistryName().toString();
        if ("futuremc:crimson_fungus".equals(key)
            || "futuremc:warped_fungus".equals(key)
            || "netherized:crimson_fungus".equals(key)
            || "netherized:warped_fungus".equals(key)
            || "nb:crimson_fungus".equals(key)
            || "nb:warped_fungus".equals(key)) {
            return clickedBlock == NDBlocks.SOUL_COMPOST_BLOCK || clickedBlock == NDBlocks.RICH_SOUL_SOIL_BLOCK
                ? Block.getBlockFromItem(item)
                : null;
        }

        if ("futuremc:crimson_roots".equals(key)
            || "futuremc:warped_roots".equals(key)
            || "netherized:crimson_roots".equals(key)
            || "netherized:warped_roots".equals(key)
            || "nb:crimson_roots".equals(key)
            || "nb:warped_roots".equals(key)
            || "futuremc:nether_sprouts".equals(key)
            || "netherized:warped_sprouts".equals(key)
            || "nb:warped_sprout".equals(key)) {
            return clickedBlock == NDBlocks.RICH_SOUL_SOIL_BLOCK
                ? Block.getBlockFromItem(item)
                : null;
        }

        return null;
    }
}
