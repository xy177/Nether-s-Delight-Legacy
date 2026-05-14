package xy177.nethersdelightlegacy.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xy177.nethersdelightlegacy.common.config.NDConfig;
import xy177.nethersdelightlegacy.common.registry.NDBlocks;

import java.util.HashSet;
import java.util.Set;

public class BlockMimicarnation extends BlockBush {
    private static final Set<String> MEAL_ITEMS = new HashSet<>();

    static {
        addMeal("minecraft:mushroom_stew");
        addMeal("minecraft:beetroot_soup");
        addMeal("minecraft:rabbit_stew");
        addMeal("minecraft:suspicious_stew");
        addMeal("farmersdelight:tomato_sauce");
        addMeal("farmersdelight:fruit_salad");
        addMeal("farmersdelight:mixed_salad");
        addMeal("farmersdelight:nether_salad");
        addMeal("farmersdelight:cooked_rice");
        addMeal("farmersdelight:beef_stew");
        addMeal("farmersdelight:chicken_soup");
        addMeal("farmersdelight:vegetable_soup");
        addMeal("farmersdelight:fish_stew");
        addMeal("farmersdelight:fried_rice");
        addMeal("farmersdelight:pumpkin_soup");
        addMeal("farmersdelight:baked_cod_stew");
        addMeal("farmersdelight:noodle_soup");
        addMeal("farmersdelight:bacon_and_eggs");
        addMeal("farmersdelight:pasta_with_meatballs");
        addMeal("farmersdelight:pasta_with_mutton_chop");
        addMeal("farmersdelight:roasted_mutton_chops");
        addMeal("farmersdelight:vegetable_noodles");
        addMeal("farmersdelight:steak_and_potatoes");
        addMeal("farmersdelight:ratatouille");
        addMeal("farmersdelight:squid_ink_pasta");
        addMeal("farmersdelight:grilled_salmon");
        addMeal("farmersdelight:roast_chicken");
        addMeal("farmersdelight:stuffed_pumpkin");
        addMeal("farmersdelight:stuffed_potato");
        addMeal("farmersdelight:honey_glazed_ham");
        addMeal("farmersdelight:shepherds_pie");
        addMeal("nethers_delight_legacy:warped_moldy_meat");
        addMeal("nethers_delight_legacy:grilled_strider");
        addMeal("nethers_delight_legacy:strider_moss_stew");
        addMeal("nethers_delight_legacy:plate_of_stuffed_hoglin_snout");
        addMeal("nethers_delight_legacy:plate_of_stuffed_hoglin_ham");
        addMeal("nethers_delight_legacy:plate_of_stuffed_hoglin_roast");
    }

    public BlockMimicarnation() {
        super(Material.PLANTS);
        setHardness(0.0F);
        setResistance(0.0F);
        setSoundType(SoundType.PLANT);
        setLightOpacity(0);
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.NETHERRACK
            || block == Blocks.SOUL_SAND
            || block == NDBlocks.SOUL_COMPOST_BLOCK
            || block == NDBlocks.RICH_SOUL_SOIL_BLOCK
            || super.canSustainBush(state);
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        return canSustainBush(worldIn.getBlockState(pos.down()));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty() || !isMealItem(held) || worldIn.getBlockState(pos.down()).getBlock() != NDBlocks.RICH_SOUL_SOIL_BLOCK) {
            return false;
        }

        if (!worldIn.isRemote) {
            ItemStack drop = new ItemStack(held.getItem(), 1, held.getMetadata());
            EntityItem entity = new EntityItem(worldIn, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, drop);
            entity.motionX = 0.0D;
            entity.motionY = 0.0D;
            entity.motionZ = 0.0D;
            worldIn.spawnEntity(entity);
            worldIn.playSound(null, pos, net.minecraft.init.SoundEvents.ENTITY_GHAST_HURT, SoundCategory.BLOCKS, 1.0F, 1.7F);
            worldIn.setBlockToAir(pos);
        }
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    private static void addMeal(String key) {
        MEAL_ITEMS.add(key);
    }

    private static boolean isMealItem(ItemStack stack) {
        Item item = stack.getItem();
        if (item.getRegistryName() == null) {
            return false;
        }

        String key = item.getRegistryName().toString();
        return MEAL_ITEMS.contains(key) || NDConfig.mimicarnationExtraMealItems.contains(key);
    }
}
