package xy177.nethersdelightlegacy.common.item;

import com.wdcftgg.farmersdelightlegacy.api.food.AddonFoodItem;
import com.wdcftgg.farmersdelightlegacy.common.Configuration;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class NDFoodItem extends AddonFoodItem {
    private final List<EffectEntry> effects = new ArrayList<>();
    private final Item containerItem;
    private final EnumAction useAction;

    public NDFoodItem(int amount, float saturation, boolean wolfFood) {
        this(amount, saturation, wolfFood, null, EnumAction.EAT);
    }

    public NDFoodItem(int amount, float saturation, boolean wolfFood, Item containerItem, EnumAction useAction) {
        super(amount, saturation, wolfFood);
        this.containerItem = containerItem;
        this.useAction = useAction;
    }

    public NDFoodItem addEffect(PotionEffect effect, float chance) {
        Potion potion = effect.getPotion();
        return potion == null ? this : addEffect(potion.getRegistryName(), effect.getDuration(), effect.getAmplifier(), chance);
    }

    public NDFoodItem addEffect(ResourceLocation effectId, int duration, int amplifier, float chance) {
        if (effectId != null) {
            effects.add(new EffectEntry(effectId, duration, amplifier, chance));
        }
        return this;
    }

    @Override
    public void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        if (worldIn.isRemote) {
            return;
        }

        for (EffectEntry entry : effects) {
            if (worldIn.rand.nextFloat() <= entry.chance) {
                Potion potion = ForgeRegistries.POTIONS.getValue(entry.effectId);
                if (potion != null) {
                    player.addPotionEffect(new PotionEffect(potion, entry.duration, entry.amplifier));
                }
            }
        }

        afterFoodEaten(stack, worldIn, player);
    }

    protected void afterFoodEaten(ItemStack stack, World worldIn, EntityLivingBase consumer) {
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return useAction;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return containerItem != null;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return containerItem == null ? ItemStack.EMPTY : new ItemStack(containerItem);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        ItemStack result = super.onItemUseFinish(stack, worldIn, entityLiving);
        if (containerItem == null) {
            return result;
        }

        ItemStack container = new ItemStack(containerItem);
        if (result.isEmpty()) {
            return container;
        }

        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLiving;
            if (!player.capabilities.isCreativeMode && !player.inventory.addItemStackToInventory(container)) {
                player.dropItem(container, false);
            }
        }
        return result;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (!Configuration.foodEffectTooltip) {
            return;
        }

        for (EffectEntry entry : effects) {
            Potion potion = ForgeRegistries.POTIONS.getValue(entry.effectId);
            if (potion == null) {
                continue;
            }

            PotionEffect effect = new PotionEffect(potion, entry.duration, entry.amplifier);
            String durationText = Potion.getPotionDurationString(effect, 1.0F);
            String effectName = new TextComponentTranslation(effect.getEffectName()).getFormattedText();
            TextComponentTranslation line = new TextComponentTranslation("farmersdelight.tooltip.food.effect", effectName, durationText);
            line.getStyle().setColor(TextFormatting.BLUE);
            tooltip.add(line.getFormattedText());

            if (entry.chance < 0.999F) {
                TextComponentTranslation chanceLine = new TextComponentTranslation("farmersdelight.tooltip.food.effect_chance", Math.round(entry.chance * 100.0F));
                chanceLine.getStyle().setColor(TextFormatting.BLUE);
                tooltip.add(chanceLine.getFormattedText());
            }
        }
    }

    private static class EffectEntry {
        private final ResourceLocation effectId;
        private final int duration;
        private final int amplifier;
        private final float chance;

        private EffectEntry(ResourceLocation effectId, int duration, int amplifier, float chance) {
            this.effectId = effectId;
            this.duration = Math.max(0, duration);
            this.amplifier = Math.max(0, amplifier);
            this.chance = Math.max(0.0F, Math.min(1.0F, chance));
        }
    }
}
