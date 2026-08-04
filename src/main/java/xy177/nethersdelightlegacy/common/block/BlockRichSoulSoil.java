package xy177.nethersdelightlegacy.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import xy177.nethersdelightlegacy.common.compat.NDCompat;
import xy177.nethersdelightlegacy.common.registry.NDBlocks;

import java.util.Random;

public class BlockRichSoulSoil extends Block {
    public BlockRichSoulSoil() {
        super(Material.GROUND);
        setHardness(0.6F);
        setResistance(0.6F);
        setSoundType(SoundType.GROUND);
        setTickRandomly(true);
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        return plantable.getPlantType(world, pos.offset(direction)) == EnumPlantType.Nether;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote) {
            return;
        }

        BlockPos abovePos = pos.up();
        IBlockState aboveState = worldIn.getBlockState(abovePos);

        if (isBlock(aboveState, NDCompat.FUTURE_MC, "crimson_fungus")
            || isBlock(aboveState, NDCompat.FARMERS_FUTURE_DELIGHT, "crimson_fungus")
            || isBlock(aboveState, NDCompat.NETHERIZED, "crimson_fungus")
            || isBlock(aboveState, NDCompat.NETHER_BACKPORT, "crimson_fungus")) {
            worldIn.setBlockState(abovePos, NDBlocks.CRIMSON_FUNGUS_COLONY_BLOCK.getDefaultState(), 3);
            return;
        }

        if (isBlock(aboveState, NDCompat.FUTURE_MC, "warped_fungus")
            || isBlock(aboveState, NDCompat.FARMERS_FUTURE_DELIGHT, "warped_fungus")
            || isBlock(aboveState, NDCompat.NETHERIZED, "warped_fungus")
            || isBlock(aboveState, NDCompat.NETHER_BACKPORT, "warped_fungus")) {
            worldIn.setBlockState(abovePos, NDBlocks.WARPED_FUNGUS_COLONY_BLOCK.getDefaultState(), 3);
            return;
        }

        if (worldIn.isAirBlock(abovePos) && rand.nextInt(50) == 0) {
            worldIn.setBlockState(abovePos, NDBlocks.MIMICARNATION_BLOCK.getDefaultState(), 3);
            return;
        }

        if (aboveState.getBlock() == Blocks.NETHER_WART
            && aboveState.getProperties().containsKey(BlockNetherWart.AGE)
            && aboveState.getValue(BlockNetherWart.AGE) < 3
            && rand.nextInt(5) == 0) {
            worldIn.setBlockState(
                abovePos,
                aboveState.withProperty(BlockNetherWart.AGE, aboveState.getValue(BlockNetherWart.AGE) + 1),
                3
            );
            return;
        }

        if (tryGrowNetherVine(worldIn, abovePos, aboveState, rand)) {
            return;
        }

        BlockPos belowPos = pos.down();
        IBlockState belowState = worldIn.getBlockState(belowPos);
        tryGrowNetherVine(worldIn, belowPos, belowState, rand);
    }

    private boolean tryGrowNetherVine(World worldIn, BlockPos vinePos, IBlockState vineState, Random rand) {
        Block vineBlock = vineState.getBlock();
        if (!(vineBlock instanceof IGrowable) || !isSupportedNetherVine(vineState)) {
            return false;
        }

        IGrowable growable = (IGrowable) vineBlock;
        if (!growable.canGrow(worldIn, vinePos, vineState, worldIn.isRemote)
            || !growable.canUseBonemeal(worldIn, rand, vinePos, vineState)
            || rand.nextInt(4) != 0) {
            return false;
        }

        growable.grow(worldIn, rand, vinePos, vineState);
        return true;
    }

    private boolean isSupportedNetherVine(IBlockState state) {
        return isBlock(state, NDCompat.FUTURE_MC, "twisting_vines")
            || isBlock(state, NDCompat.FUTURE_MC, "twisting_vines_plant")
            || isBlock(state, NDCompat.FUTURE_MC, "weeping_vines")
            || isBlock(state, NDCompat.FUTURE_MC, "weeping_vines_plant")
            || isBlock(state, NDCompat.FARMERS_FUTURE_DELIGHT, "twisting_vines")
            || isBlock(state, NDCompat.FARMERS_FUTURE_DELIGHT, "twisting_vines_plant")
            || isBlock(state, NDCompat.FARMERS_FUTURE_DELIGHT, "weeping_vines")
            || isBlock(state, NDCompat.FARMERS_FUTURE_DELIGHT, "weeping_vines_plant")
            || isBlock(state, NDCompat.NETHERIZED, "twisting_vines")
            || isBlock(state, NDCompat.NETHERIZED, "twisting_vines_end")
            || isBlock(state, NDCompat.NETHERIZED, "weeping_vines")
            || isBlock(state, NDCompat.NETHERIZED, "weeping_vines_end")
            || isBlock(state, NDCompat.NETHER_BACKPORT, "warped_vine")
            || isBlock(state, NDCompat.NETHER_BACKPORT, "crimson_vine");
    }

    private boolean isBlock(IBlockState state, String modid, String path) {
        return state.getBlock() != Blocks.AIR
            && state.getBlock().getRegistryName() != null
            && state.getBlock().getRegistryName().toString().equals(modid + ":" + path);
    }
}
