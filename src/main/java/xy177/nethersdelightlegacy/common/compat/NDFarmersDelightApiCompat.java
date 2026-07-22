package xy177.nethersdelightlegacy.common.compat;

import com.wdcftgg.farmersdelightlegacy.api.heat.HeatSourceApi;
import com.wdcftgg.farmersdelightlegacy.api.recipe.knife.HarvestDropRecipeApi;
import com.wdcftgg.farmersdelightlegacy.api.recipe.knife.HuntingDropOutput;
import com.wdcftgg.farmersdelightlegacy.api.recipe.knife.HuntingDropRecipeApi;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModItems;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import xy177.nethersdelightlegacy.NethersDelightLegacy;
import xy177.nethersdelightlegacy.common.registry.NDBlocks;
import xy177.nethersdelightlegacy.common.registry.NDItems;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class NDFarmersDelightApiCompat {
    private static boolean registered;

    private NDFarmersDelightApiCompat() {
    }

    public static void registerApis() {
        if (registered) {
            return;
        }
        registered = true;

        if (NDBlocks.BLACKSTONE_STOVE_BLOCK != null) {
            HeatSourceApi.registerDirectHeatSourcePredicate(
                NethersDelightLegacy.MODID + ":blackstone_stove",
                (world, pos, state) -> state.getBlock() == NDBlocks.BLACKSTONE_STOVE_BLOCK
                    && state.getProperties().containsKey(xy177.nethersdelightlegacy.common.block.BlockBlackstoneStove.LIT)
                    && state.getValue(xy177.nethersdelightlegacy.common.block.BlockBlackstoneStove.LIT)
            );
        }

        registerKnifeDropApis();
        registerHarvestDropApis();
    }

    private static void registerHarvestDropApis() {
        if (NDBlocks.PROPELPLANT_CANE_BLOCK == null || NDBlocks.PROPELPLANT_CANE_ITEM == null) {
            return;
        }

        HarvestDropRecipeApi.registerRecipeJei(
            NethersDelightLegacy.MODID + ":propelplant_cane",
            state -> state.getBlock() == NDBlocks.PROPELPLANT_CANE_BLOCK,
            new ItemStack(NDBlocks.PROPELPLANT_CANE_ITEM),
            false,
            1.0F,
            0.0F,
            NDBlocks.PROPELPLANT_CANE_BLOCK.getDefaultState(),
            Blocks.NETHERRACK.getDefaultState()
        );
    }

    private static void registerKnifeDropApis() {
        replaceDefaultPigHamDrops();
        if (Loader.isModLoaded("netherized")) {
            registerHoglinDrops(new ResourceLocation("netherized", "hoglin"));
            registerStriderDrops(new ResourceLocation("netherized", "strider"));
        }
        if (Loader.isModLoaded("nb")) {
            registerHoglinDrops(new ResourceLocation("nb", "hoglin"));
            registerHoglinDrops(new ResourceLocation("nb", "zoglin"));
            registerStriderDrops(new ResourceLocation("nb", "strider"));
        }
    }

    private static void replaceDefaultPigHamDrops() {
        HuntingDropRecipeApi.unregisterRecipe("farmersdelight:ham");
        HuntingDropRecipeApi.unregisterRecipe("farmersdelight:smoked_ham");

        Item ham = ModItems.get("ham");
        Item smokedHam = ModItems.get("smoked_ham");
        if (ham != null) {
            registerHamDrop(
                NethersDelightLegacy.MODID + ":pig_ham",
                entity -> entity instanceof EntityPig && !((EntityPig) entity).isChild(),
                new ItemStack(ham),
                false,
                0.5F,
                0.1F,
                new ResourceLocation("minecraft", "pig")
            );
        }
        if (smokedHam != null) {
            registerHamDrop(
                NethersDelightLegacy.MODID + ":pig_smoked_ham",
                entity -> entity instanceof EntityPig && !((EntityPig) entity).isChild(),
                new ItemStack(smokedHam),
                true,
                0.5F,
                0.1F,
                new ResourceLocation("minecraft", "pig")
            );
        }
    }

    private static void registerHoglinDrops(ResourceLocation entityId) {
        Item ham = ModItems.get("ham");
        Item smokedHam = ModItems.get("smoked_ham");
        if (ham != null) {
            registerHamDrop(
                NethersDelightLegacy.MODID + ":" + entityId.getResourceDomain() + "_" + entityId.getResourcePath() + "_ham",
                entity -> hasEntityId(entity, entityId),
                new ItemStack(ham),
                false,
                1.0F,
                0.0F,
                entityId
            );
        }
        if (smokedHam != null) {
            registerHamDrop(
                NethersDelightLegacy.MODID + ":" + entityId.getResourceDomain() + "_" + entityId.getResourcePath() + "_smoked_ham",
                entity -> hasEntityId(entity, entityId),
                new ItemStack(smokedHam),
                true,
                1.0F,
                0.0F,
                entityId
            );
        }
        registerVariableDrop(
            NethersDelightLegacy.MODID + ":" + entityId.getResourceDomain() + "_" + entityId.getResourcePath() + "_hide",
            entityId,
            new ItemStack(NDItems.HOGLIN_HIDE)
        );
    }

    private static void registerStriderDrops(ResourceLocation entityId) {
        registerVariableDrop(
            NethersDelightLegacy.MODID + ":" + entityId.getResourceDomain() + "_" + entityId.getResourcePath() + "_slice",
            entityId,
            new ItemStack(NDItems.STRIDER_SLICE)
        );
    }

    private static void registerVariableDrop(String key, ResourceLocation entityId, ItemStack stack) {
        List<HuntingDropOutput> outputs = Arrays.asList(
            HuntingDropOutput.of(stack, 1.0F, 0.0F),
            HuntingDropOutput.of(stack, 0.5F, 0.5F)
        );
        HuntingDropRecipeApi.registerRecipeAdvance(
            key,
            entity -> hasEntityId(entity, entityId),
            outputs,
            true,
            entityId,
            NDFarmersDelightApiCompat::forceAdultDisplay,
            Collections.emptyList()
        );
    }

    private static void registerHamDrop(String key, com.wdcftgg.farmersdelightlegacy.common.recipe.manager.HuntingDropRecipeManager.HuntingTargetMatcher matcher, ItemStack stack, boolean burning, float chance, float lootingBonus, ResourceLocation entityId) {
        if (!burning) {
            HuntingDropRecipeApi.registerRecipe(
                key,
                entity -> matcher.matches(entity) && !entity.isBurning(),
                stack,
                false,
                chance,
                lootingBonus,
                entityId,
                NDFarmersDelightApiCompat::forceAdultDisplay,
                true,
                Collections.emptyList()
            );
            return;
        }

        HuntingDropRecipeApi.registerRecipe(
            key,
            matcher,
            stack,
            burning,
            chance,
            lootingBonus,
            true,
            entityId
        );
    }

    private static boolean hasEntityId(EntityLivingBase entity, ResourceLocation id) {
        return EntityRegistry.getEntry(entity.getClass()) != null
            && id.equals(EntityRegistry.getEntry(entity.getClass()).getRegistryName());
    }

    private static void forceAdultDisplay(EntityLivingBase entity) {
        if (entity instanceof EntityAgeable) {
            ((EntityAgeable) entity).setGrowingAge(0);
        }
        if (entity instanceof EntityZombie) {
            ((EntityZombie) entity).setChild(false);
        }
        try {
            Method method = entity.getClass().getMethod("setChild", boolean.class);
            method.invoke(entity, false);
        } catch (ReflectiveOperationException ignored) {
        }
    }
}
