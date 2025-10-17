package com.xingmot.gtmadvancedhatch.api.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import static net.minecraft.ChatFormatting.*;

public class VoltageLevelLookup {

    // 标准格雷科技MAX电压基准值 (2^31 EU)
    private static final BigDecimal BASE_MAX_VOLTAGE = new BigDecimal(Integer.MAX_VALUE);

    // 4倍递增的底数
    private static final BigDecimal MULTIPLIER = new BigDecimal("4");

    // 计算上限 1E1000
    private static final BigDecimal MAX_EU_LIMIT = new BigDecimal("1E1000");

    // MAX+16对应的EU值
    private static final BigDecimal MAX_PLUS_16_EU;

    // 预计算的电压等级数组 (按EU值排序)
    private static final VoltageLevel[] VOLTAGE_LEVELS;

    // 预计算的最大扩展等级索引
    private static final int MAX_EXTENDED_LEVEL_INDEX;

    // 完整显示安数上限，超过使用科学计数法显示
    private static final BigDecimal MAX_EUA_DISPLAY_THRESHOLD = new BigDecimal(67108864);

    // 科学计数法显示超大安数格式化规则
    private static final DecimalFormat OVER_MAX_EUA_FORMAT = new DecimalFormat("0.##E0");

    static {
        // 初始化电压等级数组
        VOLTAGE_LEVELS = initializeVoltageLevels();
        MAX_EXTENDED_LEVEL_INDEX = VOLTAGE_LEVELS.length - 1;
        // 计算MAX+16对应的EU值
        MAX_PLUS_16_EU = calculateMaxPlus16EU();
    }

    /**
     * 计算MAX+16对应的EU值
     */
    private static BigDecimal calculateMaxPlus16EU() {
        BigDecimal current = BASE_MAX_VOLTAGE;
        for (int i = 0; i < 16; i++) {
            current = current.multiply(MULTIPLIER, MathContext.DECIMAL128);
        }
        return current;
    }

    /**
     * 初始化所有电压等级及其对应的EU上限值
     */
    private static VoltageLevel[] initializeVoltageLevels() {
        int extendedLevelsCount = 17;

        // 创建电压等级数组 (标准等级 + 扩展等级)
        VoltageLevel[] levels = new VoltageLevel[15 + extendedLevelsCount];

        // 添加标准电压等级
        addStandardVoltageLevels(levels);

        // 添加扩展电压等级 (MAX+1 到 MAX+n)
        addExtendedVoltageLevels(levels, extendedLevelsCount);

        return levels;
    }

    /**
     * 添加标准电压等级 (ULV 到 MAX)
     */
    private static void addStandardVoltageLevels(VoltageLevel[] levels) {
        BigDecimal baseEut = new BigDecimal("8");
        levels[0] = new VoltageLevel(baseEut.multiply(MULTIPLIER.pow(0)), DARK_GRAY + "ULV");
        levels[1] = new VoltageLevel(baseEut.multiply(MULTIPLIER.pow(1)), GRAY + "LV");
        levels[2] = new VoltageLevel(baseEut.multiply(MULTIPLIER.pow(2)), AQUA + "MV");
        levels[3] = new VoltageLevel(baseEut.multiply(MULTIPLIER.pow(3)), GOLD + "HV");
        levels[4] = new VoltageLevel(baseEut.multiply(MULTIPLIER.pow(4)), DARK_PURPLE + "EV");
        levels[5] = new VoltageLevel(baseEut.multiply(MULTIPLIER.pow(5)), BLUE + "IV");
        levels[6] = new VoltageLevel(baseEut.multiply(MULTIPLIER.pow(6)), LIGHT_PURPLE + "LuV");
        levels[7] = new VoltageLevel(baseEut.multiply(MULTIPLIER.pow(7)), RED + "ZPM");
        levels[8] = new VoltageLevel(baseEut.multiply(MULTIPLIER.pow(8)), DARK_AQUA + "UV");
        levels[9] = new VoltageLevel(baseEut.multiply(MULTIPLIER.pow(9)), DARK_AQUA + "UHV");
        levels[10] = new VoltageLevel(baseEut.multiply(MULTIPLIER.pow(10)), DARK_AQUA + "UEV");
        levels[11] = new VoltageLevel(baseEut.multiply(MULTIPLIER.pow(11)), DARK_AQUA + "UIV");
        levels[12] = new VoltageLevel(baseEut.multiply(MULTIPLIER.pow(12)), DARK_AQUA + "UXV");
        levels[13] = new VoltageLevel(baseEut.multiply(MULTIPLIER.pow(13)), DARK_AQUA + "OPV");
        levels[14] = new VoltageLevel(BASE_MAX_VOLTAGE, "MAX");
    }

    /**
     * 添加扩展电压等级 (MAX+1 到 MAX+n)
     */
    private static void addExtendedVoltageLevels(VoltageLevel[] levels, int extendedLevelsCount) {
        BigDecimal currentEU = BASE_MAX_VOLTAGE;

        for (int i = 0; i < extendedLevelsCount; i++) {
            currentEU = currentEU.multiply(MULTIPLIER, MathContext.DECIMAL128);
            String levelName = i < 16 ?
                    RED.toString() + BOLD + "M" + GREEN + BOLD + "A" + BLUE + BOLD + "X" + YELLOW + BOLD + "+" + RED + BOLD + (i + 1) :
                    RED.toString() + BOLD + "I" + GOLD + BOLD + "n" + YELLOW + BOLD + "f" + GREEN + BOLD + "i" + BLUE + BOLD + "N" + LIGHT_PURPLE + BOLD + "i" + AQUA + BOLD + "t" + DARK_GRAY + BOLD + "y";// infinity
            levels[15 + i] = new VoltageLevel(currentEU, levelName);
        }
    }

    /**
     * 高性能查找电压等级 - 使用二分查找
     *
     * @param eut 输入的EU值
     * @return 对应的电压等级字符串
     */
    public static String findVoltageLevel(BigDecimal eut) {
        // 验证输入有效性
        if (eut == null || eut.compareTo(BigDecimal.ZERO) <= 0) {
            return "INVALID";
        }

        // 检查是否超过MAX+16
        if (eut.compareTo(MAX_PLUS_16_EU) > 0) {
            // 在MAX+16和1E1000之间，计算倍数n
            if (eut.compareTo(MAX_EU_LIMIT) <= 0) {
                BigDecimal n = eut.divide(MAX_PLUS_16_EU, MathContext.DECIMAL128)
                        .setScale(0, RoundingMode.DOWN);
                return RED.toString() + (n.compareTo(MAX_EUA_DISPLAY_THRESHOLD) > 0 ? OVER_MAX_EUA_FORMAT.format(n) : n) + "A " + RED + BOLD + "M" + GREEN + BOLD + "A" + BLUE + BOLD + "X" +
                        YELLOW + BOLD + "+" + RED + BOLD + "16";
            } else {
                // 超过1E1000，返回特殊标记
                return VOLTAGE_LEVELS[MAX_EXTENDED_LEVEL_INDEX].levelName;
            }
        }

        // 使用二分查找确定电压等级
        int index = binarySearchVoltageLevel(eut);

        // 返回对应的电压等级
        return VOLTAGE_LEVELS[index].levelName;
    }

    /**
     * 二分查找电压等级
     */
    private static int binarySearchVoltageLevel(BigDecimal eut) {
        int low = 0;
        int high = MAX_EXTENDED_LEVEL_INDEX;

        while (low <= high) {
            int mid = (low + high) / 2;
            int comparison = eut.compareTo(VOLTAGE_LEVELS[mid].maxEU);

            if (comparison == 0) {
                // 正好等于某个等级的上限
                return mid;
            } else if (comparison < 0) {
                // eut小于当前等级上限，检查是否是第一个大于eut的等级
                if (mid == 0 || eut.compareTo(VOLTAGE_LEVELS[mid - 1].maxEU) > 0) {
                    return mid;
                }
                high = mid - 1;
            } else {
                // eut大于当前等级上限，继续向右查找
                low = mid + 1;
            }
        }

        // 如果没找到（理论上不会发生），返回最大等级
        return MAX_EXTENDED_LEVEL_INDEX;
    }

    /**
     * 电压等级数据类
     */
    private record VoltageLevel(BigDecimal maxEU, String levelName) {}

    /**
     * 性能测试方法
     */
}
