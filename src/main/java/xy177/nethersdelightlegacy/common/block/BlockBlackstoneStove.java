package xy177.nethersdelightlegacy.common.block;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockStove;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockBlackstoneStove extends BlockStove {
    public static final PropertyBool SOUL = PropertyBool.create("soul");

    public BlockBlackstoneStove() {
        super();
        setDefaultState(getDefaultState().withProperty(SOUL, false));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);

        if (state.getValue(LIT) && !state.getValue(SOUL) && (held.getItem() == Item.getItemFromBlock(Blocks.SOUL_SAND) || isSoulSoil(held))) {
            if (!worldIn.isRemote) {
                worldIn.playSound(null, pos, net.minecraft.init.SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                soulLight(state, worldIn, pos);
                if (!player.capabilities.isCreativeMode) {
                    held.shrink(1);
                }
            }
            return true;
        }

        return super.onBlockActivated(worldIn, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void extinguish(IBlockState state, World worldIn, BlockPos pos) {
        super.extinguish(state.withProperty(SOUL, false), worldIn, pos);
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (worldIn.getBlockState(pos).getValue(LIT)
            && worldIn.getBlockState(pos).getValue(SOUL)
            && entityIn instanceof EntityLivingBase
            && !entityIn.isImmuneToFire()
            && !net.minecraft.enchantment.EnchantmentHelper.hasFrostWalkerEnchantment((EntityLivingBase) entityIn)) {
            entityIn.attackEntityFrom(net.minecraft.util.DamageSource.HOT_FLOOR, 2.0F);
        }
        super.onEntityWalk(worldIn, pos, entityIn);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(meta).withProperty(SOUL, (meta & 8) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = super.getMetaFromState(state);
        if (state.getValue(SOUL)) {
            meta |= 8;
        }
        return meta;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, LIT, SOUL);
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random rand) {
        if (stateIn.getValue(LIT) && stateIn.getValue(SOUL)) {
            double x = pos.getX() + 0.5D;
            double y = pos.getY();
            double z = pos.getZ() + 0.5D;
            if (rand.nextInt(10) == 0) {
                world.playSound(x, y, z, net.minecraft.init.SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            EnumFacing direction = stateIn.getValue(FACING);
            EnumFacing.Axis axis = direction.getAxis();
            double horizontalOffset = rand.nextDouble() * 0.6D - 0.3D;
            double xOffset = axis == EnumFacing.Axis.X ? direction.getFrontOffsetX() * 0.52D : horizontalOffset;
            double yOffset = rand.nextDouble() * 6.0D / 16.0D;
            double zOffset = axis == EnumFacing.Axis.Z ? direction.getFrontOffsetZ() * 0.52D : horizontalOffset;
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + xOffset, y + yOffset, z + zOffset, 0.0D, 0.0D, 0.0D);
            world.spawnParticle(EnumParticleTypes.REDSTONE, x + xOffset, y + yOffset, z + zOffset, 0.1D, 0.5D, 1.0D);
            return;
        }
        super.randomDisplayTick(stateIn, world, pos, rand);
    }

    private void soulLight(IBlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, state.withProperty(SOUL, true), 2);
    }

    private boolean isSoulSoil(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem().getRegistryName() == null) {
            return false;
        }
        String key = stack.getItem().getRegistryName().toString();
        return "futuremc:soul_soil".equals(key)
            || "netherized:soul_soil".equals(key)
            || "nb:soul_soil".equals(key);
    }
}
