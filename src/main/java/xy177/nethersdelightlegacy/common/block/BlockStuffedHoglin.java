package xy177.nethersdelightlegacy.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xy177.nethersdelightlegacy.common.registry.NDItems;
import xy177.nethersdelightlegacy.common.tile.TileEntityStuffedHoglin;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockStuffedHoglin extends Block implements ITileEntityProvider {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyInteger SERVINGS = PropertyInteger.create("servings", 0, 11);
    public static final PropertyEnum<Part> PART = PropertyEnum.create("part", Part.class);
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D);

    public BlockStuffedHoglin() {
        super(Material.CAKE);
        setHardness(0.8F);
        setResistance(1.0F);
        setDefaultState(blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(SERVINGS, 11)
            .withProperty(PART, Part.HEAD));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityStuffedHoglin();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        int servings = getServings(worldIn, pos);
        ItemStack held = player.getHeldItem(hand);

        if (servings > 9) {
            if (isKnife(held)) {
                return cutEar(worldIn, pos, state);
            }
            if (!worldIn.isRemote) {
                player.sendStatusMessage(new TextComponentTranslation("nethers_delight_legacy.block.feast.use_knife"), true);
            }
            return true;
        }

        if (held.getItem() == Items.BOWL) {
            if (servings == 9) {
                return takeServing(worldIn, pos, state, player, hand, NDItems.PLATE_OF_STUFFED_HOGLIN_SNOUT);
            }
            if (servings > 4) {
                return takeServing(worldIn, pos, state, player, hand, NDItems.PLATE_OF_STUFFED_HOGLIN_HAM);
            }
            if (servings > 0) {
                return takeServing(worldIn, pos, state, player, hand, NDItems.PLATE_OF_STUFFED_HOGLIN_ROAST);
            }
        }

        if (servings == 0 && !worldIn.isRemote) {
            clearLeftovers(worldIn, pos, state);
        } else if (!worldIn.isRemote) {
            player.sendStatusMessage(new TextComponentTranslation("farmersdelight.block.feast.use_container", new ItemStack(Items.BOWL).getDisplayName()), true);
        }
        return true;
    }

    private boolean cutEar(World worldIn, BlockPos pos, IBlockState state) {
        setServings(worldIn, pos, getServings(worldIn, pos) - 1);
        if (!worldIn.isRemote) {
            worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(NDItems.HOGLIN_EAR)));
        }
        return true;
    }

    private boolean takeServing(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, Item serving) {
        setServings(worldIn, pos, getServings(worldIn, pos) - 1);
        if (!worldIn.isRemote) {
            if (!player.capabilities.isCreativeMode) {
                player.getHeldItem(hand).shrink(1);
            }
            ItemStack servingStack = new ItemStack(serving);
            if (!player.inventory.addItemStackToInventory(servingStack)) {
                player.dropItem(servingStack, false);
            }
        }
        return true;
    }

    private void clearLeftovers(World worldIn, BlockPos pos, IBlockState state) {
        EnumFacing facing = state.getValue(FACING);
        BlockPos otherPos = pos.offset(state.getValue(PART) == Part.HEAD ? facing : facing.getOpposite());
        IBlockState otherState = worldIn.getBlockState(otherPos);

        worldIn.setBlockToAir(pos);
        if (otherState.getBlock() == this) {
            worldIn.setBlockToAir(otherPos);
        }

        worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.BOWL)));
        worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.BONE, 4)));
    }

    private void setServings(World worldIn, BlockPos pos, int servings) {
        IBlockState state = worldIn.getBlockState(pos);
        EnumFacing facing = state.getValue(FACING);
        Part part = state.getValue(PART);
        BlockPos otherPos = pos.offset(part == Part.HEAD ? facing : facing.getOpposite());
        IBlockState otherState = worldIn.getBlockState(otherPos);
        int clamped = Math.max(0, Math.min(11, servings));

        worldIn.setBlockState(pos, state.withProperty(SERVINGS, clamped), 3);
        setTileServings(worldIn, pos, clamped);
        worldIn.notifyBlockUpdate(pos, state, state.withProperty(SERVINGS, clamped), 3);
        if (otherState.getBlock() == this) {
            worldIn.setBlockState(otherPos, otherState.withProperty(SERVINGS, clamped), 3);
            setTileServings(worldIn, otherPos, clamped);
            worldIn.notifyBlockUpdate(otherPos, otherState, otherState.withProperty(SERVINGS, clamped), 3);
        }
    }

    private void setTileServings(World worldIn, BlockPos pos, int servings) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityStuffedHoglin) {
            ((TileEntityStuffedHoglin) tile).setServings(servings);
        }
    }

    private int getServings(World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityStuffedHoglin) {
            return ((TileEntityStuffedHoglin) tile).getServings();
        }
        return 11;
    }

    private boolean isKnife(ItemStack stack) {
        String key = stack.getItem().getRegistryName() == null ? "" : stack.getItem().getRegistryName().toString();
        return key.startsWith("farmersdelight:") && key.endsWith("_knife");
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumFacing facing = state.getValue(FACING);
        BlockPos footPos = pos.offset(facing);
        worldIn.setBlockState(pos, state.withProperty(PART, Part.HEAD).withProperty(SERVINGS, 11), 3);
        worldIn.setBlockState(footPos, state.withProperty(PART, Part.FOOT).withProperty(SERVINGS, 11), 3);
        setTileServings(worldIn, pos, 11);
        setTileServings(worldIn, footPos, 11);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        EnumFacing facing = state.getValue(FACING);
        BlockPos otherPos = pos.offset(state.getValue(PART) == Part.HEAD ? facing : facing.getOpposite());
        if (worldIn.getBlockState(otherPos).getBlock() != this) {
            worldIn.destroyBlock(pos, false);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
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
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(PART) == Part.HEAD ? Item.getItemFromBlock(this) : Items.AIR;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, SERVINGS, PART);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getHorizontal(meta & 3);
        Part part = (meta & 4) != 0 ? Part.FOOT : Part.HEAD;
        return getDefaultState().withProperty(FACING, facing).withProperty(PART, part).withProperty(SERVINGS, 11);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(FACING).getHorizontalIndex();
        if (state.getValue(PART) == Part.FOOT) {
            meta |= 4;
        }
        return meta;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityStuffedHoglin) {
            int servings = ((TileEntityStuffedHoglin) tile).getServings();
            return state.withProperty(SERVINGS, Math.max(0, Math.min(11, servings)));
        }
        return state.withProperty(SERVINGS, 11);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    public enum Part implements net.minecraft.util.IStringSerializable {
        HEAD,
        FOOT;

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }
}
