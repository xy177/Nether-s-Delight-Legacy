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

        if (config.hasChanged()) {
            config.save();
        }
    }
}
