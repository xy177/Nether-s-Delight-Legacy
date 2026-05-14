package xy177.nethersdelightlegacy.common.event;

import net.minecraft.block.BlockFlowerPot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xy177.nethersdelightlegacy.NethersDelightLegacy;
import xy177.nethersdelightlegacy.common.registry.NDBlocks;

@Mod.EventBusSubscriber(modid = NethersDelightLegacy.MODID)
public final class NDFlowerPotEvents {
    private NDFlowerPotEvents() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        EntityPlayer player = event.getEntityPlayer();
        EnumHand hand = event.getHand();
        ItemStack held = player.getHeldItem(hand);

        if (held.isEmpty() || held.getItem() != NDBlocks.MIMICARNATION_ITEM) {
            return;
        }

        if (world.getBlockState(pos).getBlock() != Blocks.FLOWER_POT) {
            return;
        }

        if (!(world.getTileEntity(pos) instanceof TileEntityFlowerPot)) {
            return;
        }

        TileEntityFlowerPot pot = (TileEntityFlowerPot) world.getTileEntity(pos);
        if (!pot.getFlowerItemStack().isEmpty()) {
            return;
        }

        if (!world.isRemote) {
            world.setBlockState(pos, NDBlocks.POTTED_MIMICARNATION_BLOCK.getDefaultState(), 3);
            if (!player.capabilities.isCreativeMode) {
                held.shrink(1);
            }
        }

        event.setCancellationResult(EnumActionResult.SUCCESS);
        event.setCanceled(true);
    }
}
