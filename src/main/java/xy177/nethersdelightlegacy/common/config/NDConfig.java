package xy177.nethersdelightlegacy.common.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class NDConfig {
    public static boolean soulCompostNetherOnly = true;
    public static float soulCompostSpeedMultiplier = 1.0F;
    public static Set<String> mimicarnationExtraMealItems = Collections.emptySet();
    public static int propelplantHazardMode = 2;
    public static float propelplantExplosionPower = 6.0F;
    public static boolean propelplantWorldgenEnabled = true;
    public static int propelplantGenerationAttemptsPerChunk = 8;
    public static Set<String> propelplantAllowedBiomes = Collections.emptySet();
    public static boolean propelpearlBarteringEnabled = true;

    private NDConfig() {
    }

    public static void load(File file) {
        Configuration config = new Configuration(file);
        config.load();

        soulCompostNetherOnly = config.getBoolean(
            "soulCompostNetherOnly",
            "soul_compost",
            true,
            "If true, Soul Compost only converts in the Nether.\n"
                + "若为 true，灵魂肥料只会在下界转化。"
        );
        soulCompostSpeedMultiplier = config.getFloat(
            "soulCompostSpeedMultiplier",
            "soul_compost",
            1.0F,
            0.0F,
            20.0F,
            "Multiplier for Soul Compost conversion chance. At the default base chance, 20 already reaches the 100% cap.\n"
                + "灵魂肥料转化概率倍率。按当前默认基础概率，20 就已经达到 100% 封顶。"
        );

        String[] extraMeals = config.getStringList(
            "extraMealItems",
            "mimicarnation",
            new String[0],
            "Additional meal item registry names accepted by Mimicarnation. Example: modid:item_name\n"
                + "拟灵花额外接受的餐食物品注册名。示例：modid:item_name"
        );
        LinkedHashSet<String> mealSet = new LinkedHashSet<>();
        for (String value : extraMeals) {
            String trimmed = value == null ? "" : value.trim();
            if (!trimmed.isEmpty()) {
                mealSet.add(trimmed);
            }
        }
        mimicarnationExtraMealItems = Collections.unmodifiableSet(mealSet);

        propelplantHazardMode = config.getInt(
            "propelplantHazardMode",
            "propelplant",
            2,
            0,
            2,
            "0 = always dangerous, 1 = never dangerous, 2 = safe when planted on Rich Soul Soil.\n"
                + "0 = 始终危险，1 = 永不危险，2 = 种在肥沃灵魂土上时安全。"
        );
        propelplantExplosionPower = config.getFloat(
            "propelplantExplosionPower",
            "propelplant",
            6.0F,
            0.0F,
            1000.0F,
            "Explosion power for Propelplant hazards.\n"
                + "枪药藤危险交互的爆炸强度。"
        );

        propelplantWorldgenEnabled = config.getBoolean(
            "propelplantWorldgenEnabled",
            "propelplant_worldgen",
            true,
            "If false, Propelplant Cane will not generate naturally.\n"
                + "若为 false，枪药藤不会自然生成。"
        );
        propelplantGenerationAttemptsPerChunk = config.getInt(
            "propelplantGenerationAttemptsPerChunk",
            "propelplant_worldgen",
            8,
            0,
            64,
            "Generation attempts per Nether chunk. Lower this to reduce natural Propelplant density.\n"
                + "每个下界区块的枪药藤生成尝试次数，调低可降低自然生成密度。"
        );
        String[] allowedBiomes = config.getStringList(
            "propelplantAllowedBiomes",
            "propelplant_worldgen",
            new String[]{
                "netherized:crimson_forest",
                "nb:crimson_forest"
            },
            "Allowed biome registry names for natural Propelplant generation. Use * to allow every Nether biome.\n"
                + "允许枪药藤自然生成的生物群系注册名白名单，使用 * 可允许所有下界生物群系。"
        );
        LinkedHashSet<String> biomeSet = new LinkedHashSet<>();
        for (String value : allowedBiomes) {
            String trimmed = value == null ? "" : value.trim();
            if (!trimmed.isEmpty()) {
                biomeSet.add(trimmed);
            }
        }
        propelplantAllowedBiomes = Collections.unmodifiableSet(biomeSet);

        propelpearlBarteringEnabled = config.getBoolean(
            "propelpearlBarteringEnabled",
            "piglin_bartering",
            true,
            "If false, Propelpearls will not be added to supported Piglin bartering loot tables.\n"
                + "\u82E5\u4E3A false\uFF0C\u67AA\u836F\u73E0\u4E0D\u4F1A\u52A0\u5165\u53D7\u652F\u6301\u7684\u732A\u7075\u4EA4\u6613\u6218\u5229\u54C1\u8868\u3002"
        );

        if (config.hasChanged()) {
            config.save();
        }
    }
}
