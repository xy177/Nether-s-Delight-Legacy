package xy177.nethersdelightlegacy.common.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import xy177.nethersdelightlegacy.common.config.NDConfig;
import xy177.nethersdelightlegacy.common.registry.NDBlocks;

import java.util.Random;

public class PropelplantWorldGenerator implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (!NDConfig.propelplantWorldgenEnabled || NDConfig.propelplantGenerationAttemptsPerChunk <= 0 || world.provider.getDimension() != -1) {
            return;
        }

        int originX = chunkX * 16;
        int originZ = chunkZ * 16;
        for (int i = 0; i < NDConfig.propelplantGenerationAttemptsPerChunk; i++) {
            BlockPos start = new BlockPos(originX + random.nextInt(16), 20 + random.nextInt(90), originZ + random.nextInt(16));
            if (!canGenerateInBiome(world, start)) {
                continue;
            }
            generatePatch(world, start, random);
        }
    }

    private void generatePatch(World world, BlockPos origin, Random random) {
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (Math.abs(x) >= 2 && Math.abs(z) >= 2) {
                    continue;
                }
                for (int y = -1; y <= 1; y++) {
                    BlockPos pos = origin.add(x, y, z);
                    BlockPos below = pos.down();
                    IBlockState belowState = world.getBlockState(below);
                    if (!canGrowPropelplant(belowState) || random.nextInt(3) != 0 || !world.isAirBlock(pos)) {
                        continue;
                    }

                    placeColumn(world, pos, random);
                }
            }
        }
    }

    private void placeColumn(World world, BlockPos pos, Random random) {
        BlockPos above = pos.up();
        BlockPos top = pos.up(2);
        IBlockState stem = NDBlocks.PROPELPLANT_CANE_BLOCK.getDefaultState()
            .withProperty(xy177.nethersdelightlegacy.common.block.BlockPropelplantCane.STEM, true);
        IBlockState middle = NDBlocks.PROPELPLANT_CANE_BLOCK.getDefaultState()
            .withProperty(xy177.nethersdelightlegacy.common.block.BlockPropelplantCane.STEM, false)
            .withProperty(xy177.nethersdelightlegacy.common.block.BlockPropelplantCane.BUD, false)
            .withProperty(xy177.nethersdelightlegacy.common.block.BlockPropelplantCane.PEARL, false);
        IBlockState berry = NDBlocks.PROPELPLANT_CANE_BLOCK.getDefaultState()
            .withProperty(xy177.nethersdelightlegacy.common.block.BlockPropelplantCane.BUD, true)
            .withProperty(xy177.nethersdelightlegacy.common.block.BlockPropelplantCane.PEARL, random.nextBoolean());

        if (random.nextInt(6) == 0 && world.isAirBlock(above)) {
            world.setBlockState(pos, stem, 2);
            world.setBlockState(above, berry, 2);
            return;
        }

        if (world.isAirBlock(above) && world.isAirBlock(top)) {
            world.setBlockState(pos, stem, 2);
            world.setBlockState(above, middle, 2);
            world.setBlockState(top, berry, 2);
        }
    }

    private boolean canGrowPropelplant(IBlockState state) {
        Block block = state.getBlock();
        String key = block.getRegistryName() == null ? "" : block.getRegistryName().toString();
        return "futuremc:crimson_nylium".equals(key)
            || "futuremc:warped_nylium".equals(key)
            || "netherized:crimson_nylium".equals(key)
            || "netherized:warped_nylium".equals(key)
            || "nb:crimson_grass".equals(key)
            || "nb:warped_grass".equals(key)
            || block == Blocks.NETHERRACK;
    }

    private boolean canGenerateInBiome(World world, BlockPos pos) {
        if (NDConfig.propelplantAllowedBiomes.contains("*")) {
            return true;
        }
        Biome biome = world.getBiome(pos);
        String key = biome.getRegistryName() == null ? "" : biome.getRegistryName().toString();
        return NDConfig.propelplantAllowedBiomes.contains(key);
    }
}
