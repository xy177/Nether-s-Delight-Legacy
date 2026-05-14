package xy177.nethersdelightlegacy.common.block;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockMushroomColony;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import xy177.nethersdelightlegacy.NethersDelightLegacy;
import xy177.nethersdelightlegacy.common.compat.NDCompat;
import xy177.nethersdelightlegacy.common.registry.NDBlocks;

import java.util.Random;

public class BlockFungusColony extends BlockMushroomColony {
    private final String fungusType;

    public BlockFungusColony(String fungusType) {
        super(new net.minecraft.util.ResourceLocation(NethersDelightLegacy.MODID, fungusType).toString());
        this.fungusType = fungusType;
        setTickRandomly(true);
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        return plantable.getPlantType(world, pos.offset(direction)) == EnumPlantType.Nether
            || super.canSustainPlant(state, world, pos, direction, plantable);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        int age = state.getValue(AGE);
        ItemStack held = player.getHeldItem(hand);

        if (age > 0 && held.getItem() == Items.SHEARS) {
            Item fungusItem = getFungusItem();
            if (fungusItem != null && !worldIn.isRemote) {
                spawnAsEntity(worldIn, pos, new ItemStack(fungusItem));
                worldIn.setBlockState(pos, state.withProperty(AGE, age - 1), 2);
                held.damageItem(1, player);
            }

            worldIn.playSound(player, pos, net.minecraft.init.SoundEvents.ENTITY_MOOSHROOM_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return true;
        }

        return false;
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        if (isGrowableGround(worldIn.getBlockState(pos.down()))) {
            return true;
        }
        return super.canBlockStay(worldIn, pos, state);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);

        IBlockState currentState = worldIn.getBlockState(pos);
        if (currentState.getBlock() != this) {
            return;
        }

        if (currentState.getValue(AGE) < getMaxAge()
            && worldIn.getBlockState(pos.down()).getBlock() == NDBlocks.RICH_SOUL_SOIL_BLOCK
            && rand.nextInt(4) == 0) {
            worldIn.setBlockState(pos, currentState.withProperty(AGE, currentState.getValue(AGE) + 1), 2);
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        Item fungusItem = getFungusItem();
        if (fungusItem == null) {
            return;
        }
        drops.add(new ItemStack(fungusItem, state.getValue(AGE) + 2));
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, net.minecraft.tileentity.TileEntity te, ItemStack stack) {
        if (worldIn.isRemote || player.capabilities.isCreativeMode) {
            return;
        }

        NonNullList<ItemStack> drops = NonNullList.create();
        boolean harvestedWithShears = stack.getItem() == Items.SHEARS;

        if (harvestedWithShears && state.getValue(AGE) == getMaxAge()) {
            drops.add(new ItemStack(this));
        } else {
            getDrops(drops, worldIn, pos, state, 0);
        }

        for (ItemStack drop : drops) {
            spawnAsEntity(worldIn, pos, drop);
        }
    }

    private Item getFungusItem() {
        if ("crimson_fungus".equals(fungusType)) {
            return NDCompat.getPreferredCrimsonFungus();
        }
        if ("warped_fungus".equals(fungusType)) {
            return NDCompat.getPreferredWarpedFungus();
        }
        return null;
    }

    private boolean isGrowableGround(IBlockState state) {
        Block block = state.getBlock();
        return block == NDBlocks.RICH_SOUL_SOIL_BLOCK
            || block == NDBlocks.SOUL_COMPOST_BLOCK
            || block == Blocks.MYCELIUM
            || block == Blocks.FARMLAND
            || block == Blocks.GRASS
            || block == Blocks.SOUL_SAND
            || isPodzol(state)
            || isBlock(state, NDCompat.FUTURE_MC, "soul_soil")
            || isBlock(state, NDCompat.NETHERIZED, "soul_soil")
            || isBlock(state, NDCompat.NETHER_BACKPORT, "soul_soil")
            || isBlock(state, NDCompat.FUTURE_MC, "crimson_nylium")
            || isBlock(state, NDCompat.FUTURE_MC, "warped_nylium")
            || isBlock(state, NDCompat.NETHERIZED, "crimson_nylium")
            || isBlock(state, NDCompat.NETHERIZED, "warped_nylium")
            || isBlock(state, NDCompat.NETHER_BACKPORT, "crimson_grass")
            || isBlock(state, NDCompat.NETHER_BACKPORT, "warped_grass");
    }

    private boolean isPodzol(IBlockState state) {
        return state.getBlock() == Blocks.DIRT
            && state.getProperties().containsKey(BlockDirt.VARIANT)
            && state.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.PODZOL;
    }

    private boolean isBlock(IBlockState state, String modid, String path) {
        return state.getBlock().getRegistryName() != null
            && state.getBlock().getRegistryName().toString().equals(modid + ":" + path);
    }
}
