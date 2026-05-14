package xy177.nethersdelightlegacy.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ItemBlockStuffedHoglin extends ItemBlock {
    public ItemBlockStuffedHoglin(Block block) {
        super(block);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        BlockPos placePos = pos.offset(facing);
        if (!stack.isEmpty() && player.canPlayerEdit(placePos, facing, stack)) {
            BlockPos footPos = placePos.offset(player.getHorizontalFacing());
            if (!worldIn.getBlockState(footPos).getBlock().isReplaceable(worldIn, footPos)) {
                if (!worldIn.isRemote) {
                    player.sendStatusMessage(new TextComponentTranslation("nethers_delight_legacy.block.stuffed_hoglin.need_space"), true);
                }
                return EnumActionResult.FAIL;
            }
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}
