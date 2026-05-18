package xy177.nethersdelightlegacy.common.block;

import com.wdcftgg.farmersdelightlegacy.common.item.ItemKnife;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import xy177.nethersdelightlegacy.common.config.NDConfig;
import xy177.nethersdelightlegacy.common.registry.NDBlocks;
import xy177.nethersdelightlegacy.common.registry.NDItems;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockPropelplantCane extends BlockBush implements IGrowable, IPlantable {
    public static final PropertyBool PEARL = PropertyBool.create("pearl");
    public static final PropertyBool STEM = PropertyBool.create("stem");
    public static final PropertyBool BUD = PropertyBool.create("bud");
    public static final PropertyBool CUT = PropertyBool.create("cut");
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

    public BlockPropelplantCane() {
        super(Material.PLANTS);
        setHardness(0.1F);
        setResistance(0.1F);
        setSoundType(SoundType.PLANT);
        setLightOpacity(0);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState()
            .withProperty(PEARL, false)
            .withProperty(STEM, false)
            .withProperty(BUD, false)
            .withProperty(CUT, false));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!state.getValue(PEARL) || !state.getValue(BUD)) {
            return false;
        }

        ItemStack held = player.getHeldItem(hand);
        if (held.getItem() instanceof ItemShears) {
            if (!worldIn.isRemote) {
                int count = 1 + worldIn.rand.nextInt(2);
                spawnAsEntity(worldIn, pos, new ItemStack(NDItems.PROPELPEARL, count));
                worldIn.playSound(null, pos, net.minecraft.init.SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockState(pos, state.withProperty(PEARL, false), 2);
            }
            return true;
        }

        if (!worldIn.isRemote && isHazardous(worldIn, pos)) {
            destroyWholeColumnNoDrops(worldIn, pos);
            explode(worldIn, pos, player);
            return true;
        }

        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (worldIn.isRemote || !isHazardous(worldIn, pos)) {
            return;
        }

        if (entityIn instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entityIn;
            if (!living.isSneaking()) {
                destroyWholeColumnNoDrops(worldIn, pos);
                explode(worldIn, pos, living);
            }
            return;
        }

        if (entityIn instanceof EntityThrowable || entityIn instanceof EntityArrow) {
            destroyWholeColumnNoDrops(worldIn, pos);
            explode(worldIn, pos, null);
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!canBlockStay(worldIn, pos, state)) {
            if (state.getValue(CUT)) {
                destroyWholeColumnNoDrops(worldIn, pos);
            } else if (!worldIn.isRemote && isHazardous(worldIn, pos)) {
                destroyWholeColumnNoDrops(worldIn, pos);
                explode(worldIn, pos, null);
            }
            return;
        }

        if (state.getValue(BUD)) {
            int height = getColumnHeight(worldIn, pos);
            if (!state.getValue(PEARL) && height >= 3 && rand.nextInt(8) == 0) {
                worldIn.setBlockState(pos, state.withProperty(PEARL, true), 2);
                state = worldIn.getBlockState(pos);
            }

            if (worldIn.isAirBlock(pos.up()) && rand.nextInt(12) == 0 && height < 3) {
                IBlockState below = worldIn.getBlockState(pos.down());
                IBlockState bottom = worldIn.getBlockState(pos.down().down());
                if (!(below.getBlock() == this && bottom.getBlock() == this)) {
                    worldIn.setBlockState(pos, state.withProperty(BUD, false).withProperty(PEARL, false), 2);
                    worldIn.setBlockState(
                        pos.up(),
                        getDefaultState().withProperty(BUD, true).withProperty(PEARL, false).withProperty(STEM, false),
                        2
                    );
                }
            }
        }
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        IBlockState below = worldIn.getBlockState(pos.down());
        return (below.getBlock() == this && !below.getValue(PEARL)) || canSustainBush(below);
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        Block block = state.getBlock();
        return block == NDBlocks.RICH_SOUL_SOIL_BLOCK
            || block == Blocks.NETHERRACK
            || block == NDBlocks.SOUL_COMPOST_BLOCK
            || isNylium(block);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        IBlockState below = worldIn.getBlockState(pos.down());
        IBlockState above = worldIn.getBlockState(pos.up());
        IBlockState updated = state
            .withProperty(STEM, below.getBlock() != this)
            .withProperty(BUD, above.getBlock() != this)
            .withProperty(PEARL, above.getBlock() == this ? false : state.getValue(PEARL));
        if (updated != state) {
            worldIn.setBlockState(pos, updated, 2);
            state = updated;
        }
        if (!canBlockStay(worldIn, pos, state)) {
            worldIn.scheduleUpdate(pos, this, 1);
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (!world.isRemote) {
            ItemStack tool = player.getHeldItemMainhand();
            boolean hunting = isHuntingTool(tool) && !(tool.getItem() instanceof ItemShears);

            if (hunting) {
                int dropCount = countFromPosToTop(world, pos);
                clearFromPosUp(world, pos);
                spawnAsEntity(world, pos, new ItemStack(NDBlocks.PROPELPLANT_CANE_ITEM, dropCount));
            } else if (isPlantedOnRichSoulSoil(world, pos)) {
                clearFromPosUp(world, pos);
            } else {
                destroyWholeColumnNoDrops(world, pos);
                explode(world, pos, player);
            }
        }

        return world.setBlockToAir(pos);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState below = worldIn.getBlockState(pos.down());
        IBlockState above = worldIn.getBlockState(pos.up());
        return getDefaultState()
            .withProperty(STEM, below.getBlock() != this)
            .withProperty(BUD, above.getBlock() != this);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PEARL, STEM, BUD, CUT);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        if (state.getValue(PEARL)) meta |= 1;
        if (state.getValue(STEM)) meta |= 2;
        if (state.getValue(BUD)) meta |= 4;
        if (state.getValue(CUT)) meta |= 8;
        return meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
            .withProperty(PEARL, (meta & 1) != 0)
            .withProperty(STEM, (meta & 2) != 0)
            .withProperty(BUD, (meta & 4) != 0)
            .withProperty(CUT, (meta & 8) != 0);
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return state.getValue(BUD) && !state.getValue(PEARL);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        if (getColumnHeight(worldIn, pos) < 3) {
            if (worldIn.isAirBlock(pos.up())) {
                worldIn.setBlockState(pos, state.withProperty(BUD, false).withProperty(PEARL, false), 2);
                worldIn.setBlockState(
                    pos.up(),
                    getDefaultState().withProperty(BUD, true).withProperty(PEARL, false).withProperty(STEM, false),
                    2
                );
            }
            return;
        }

        worldIn.setBlockState(pos, state.withProperty(PEARL, true), 2);
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Nether;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return getDefaultState();
    }

    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if (state.getValue(PEARL)) {
            drops.add(new ItemStack(NDItems.PROPELPEARL, 1 + RANDOM.nextInt(2)));
        }
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable net.minecraft.tileentity.TileEntity te, ItemStack tool) {
    }

    private void explode(World worldIn, BlockPos pos, @Nullable EntityLivingBase source) {
        worldIn.createExplosion(source, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, NDConfig.propelplantExplosionPower, false);
        damageNearbyEntities(worldIn, pos, source);
    }

    private void damageNearbyEntities(World worldIn, BlockPos pos, @Nullable EntityLivingBase source) {
        float damage = NDConfig.propelplantExplosionPower;
        if (damage <= 0.0F) {
            return;
        }

        AxisAlignedBB area = new AxisAlignedBB(pos).grow(2.0D);
        for (EntityLivingBase target : worldIn.getEntitiesWithinAABB(EntityLivingBase.class, area)) {
            double distance = target.getDistanceSq(pos);
            if (distance > 16.0D) {
                continue;
            }
            float scaled = (float) (damage * (1.0D - (Math.sqrt(distance) / 4.0D)));
            if (scaled > 0.0F) {
                target.attackEntityFrom(DamageSource.GENERIC, scaled);
            }
        }
    }

    private boolean isHazardous(World worldIn, BlockPos pos) {
        if (NDConfig.propelplantHazardMode == 0) {
            return true;
        }
        if (NDConfig.propelplantHazardMode == 1) {
            return false;
        }
        return !isPlantedOnRichSoulSoil(worldIn, pos);
    }

    private boolean isPlantedOnRichSoulSoil(World worldIn, BlockPos pos) {
        BlockPos bottom = findBottomPos(worldIn, pos);
        return worldIn.getBlockState(bottom.down()).getBlock() == NDBlocks.RICH_SOUL_SOIL_BLOCK;
    }

    private int getColumnHeight(World worldIn, BlockPos pos) {
        BlockPos bottom = findBottomPos(worldIn, pos);
        int height = 1;
        BlockPos cursor = bottom.up();
        while (worldIn.getBlockState(cursor).getBlock() == this) {
            height++;
            cursor = cursor.up();
        }
        return height;
    }

    private int countFromPosToTop(World worldIn, BlockPos pos) {
        int count = 0;
        BlockPos cursor = pos;
        while (worldIn.getBlockState(cursor).getBlock() == this) {
            count++;
            cursor = cursor.up();
        }
        return count;
    }

    private BlockPos findBottomPos(World worldIn, BlockPos pos) {
        BlockPos cursor = pos;
        while (worldIn.getBlockState(cursor.down()).getBlock() == this) {
            cursor = cursor.down();
        }
        return cursor;
    }

    private BlockPos findTopPos(World worldIn, BlockPos pos) {
        BlockPos cursor = pos;
        while (worldIn.getBlockState(cursor.up()).getBlock() == this) {
            cursor = cursor.up();
        }
        return cursor;
    }

    private void destroyWholeColumnNoDrops(World worldIn, BlockPos pos) {
        if (worldIn.isRemote || worldIn.getBlockState(pos).getBlock() != this) {
            return;
        }

        BlockPos bottom = findBottomPos(worldIn, pos);
        BlockPos top = findTopPos(worldIn, pos);
        BlockPos cursor = bottom;
        while (cursor.getY() <= top.getY()) {
            if (worldIn.getBlockState(cursor).getBlock() == this) {
                worldIn.setBlockToAir(cursor);
            }
            cursor = cursor.up();
        }
    }

    private void clearFromPosUp(World worldIn, BlockPos pos) {
        BlockPos cursor = pos;
        while (worldIn.getBlockState(cursor).getBlock() == this) {
            worldIn.setBlockToAir(cursor);
            cursor = cursor.up();
        }
    }

    private boolean isNylium(Block block) {
        String key = block.getRegistryName() == null ? "" : block.getRegistryName().toString();
        return "futuremc:crimson_nylium".equals(key)
            || "futuremc:warped_nylium".equals(key)
            || "netherized:crimson_nylium".equals(key)
            || "netherized:warped_nylium".equals(key)
            || "nb:crimson_grass".equals(key)
            || "nb:warped_grass".equals(key);
    }

    private boolean isHuntingTool(ItemStack stack) {
        return ItemKnife.isKnife(stack);
    }
}
