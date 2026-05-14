package xy177.nethersdelightlegacy.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xy177.nethersdelightlegacy.common.config.NDConfig;
import xy177.nethersdelightlegacy.common.registry.NDBlocks;

import java.util.Random;

public class BlockSoulCompost extends Block {
    public static final PropertyInteger COMPOSTING = PropertyInteger.create("composting", 0, 3);

    public BlockSoulCompost() {
        super(Material.GROUND);
        setHardness(0.6F);
        setResistance(0.6F);
        setSoundType(SoundType.GROUND);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState().withProperty(COMPOSTING, 0));
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if ((NDConfig.soulCompostNetherOnly && !worldIn.provider.isNether()) || rand.nextFloat() > getCompostChance(worldIn, pos)) {
            return;
        }

        int composting = state.getValue(COMPOSTING);
        if (composting >= 3) {
            worldIn.setBlockState(pos, NDBlocks.RICH_SOUL_SOIL_BLOCK.getDefaultState(), 3);
        } else {
            worldIn.setBlockState(pos, state.withProperty(COMPOSTING, composting + 1), 3);
        }
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return 4 - blockState.getValue(COMPOSTING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(COMPOSTING, Math.max(0, Math.min(3, meta)));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(COMPOSTING);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, COMPOSTING);
    }

    private float getCompostChance(World world, BlockPos pos) {
        float chance = 0.05F;
        for (EnumFacing facing : EnumFacing.values()) {
            Block block = world.getBlockState(pos.offset(facing)).getBlock();
            if (block == Blocks.FIRE || block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
                chance += 0.1F;
            }
        }
        return Math.min(1.0F, chance * NDConfig.soulCompostSpeedMultiplier);
    }
}
