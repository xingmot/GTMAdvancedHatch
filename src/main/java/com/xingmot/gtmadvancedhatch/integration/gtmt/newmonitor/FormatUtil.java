package com.xingmot.gtmadvancedhatch.integration.gtmt.newmonitor;

import com.xingmot.gtmadvancedhatch.GTMAdvancedHatch;
import com.xingmot.gtmadvancedhatch.util.FormattingUtil;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FormatUtil {

    public static String formatNumber(long number) {
        if (number < 1000) {
            return String.valueOf(number);
        } else if (number < 1_000_000) {
            return String.format("%.1fK", number / 1000.0);
        } else if (number < 1_000_000_000) {
            return String.format("%.2fM", number / 1_000_000.0);
        } else {
            return String.format("%.2fG", number / 1_000_000_000.0);
        }
    }

    public static MutableComponent formatWithConstantWidth(String labelKey, int width, Component body, Component... appends) {
        var a = new Component[appends.length + 1];
        a[0] = body;
        int i = 0;
        for (var c : appends) {
            a[++i] = c;
        }
        var tmp = Component.translatable(labelKey, (Object[]) a);
        var baseLength = getComponentLength(tmp);
        var spaceLength = width - baseLength;
        if (spaceLength <= 0) return tmp;

        // 获取字体实例
        Font font = Minecraft.getInstance().font;
        // 测量一个分隔符的宽度
        int dotWidth = font.width(Component.literal("·").setStyle(Style.EMPTY.withFont(new ResourceLocation("gtmadvancedhatch", "separator_font"))));
        // 测量一个空格的宽度
        int spaceWidth = font.width(" ");

        // 计算可以容纳的分隔符数量
        int totalSpacerWidth = spaceLength - spaceWidth; // 预留一个空格的宽度
        if (totalSpacerWidth <= 0) {
            a[0] = body;
            return Component.translatable(labelKey, (Object[]) a);
        }

        int spacerCount = totalSpacerWidth / dotWidth;
        if (spacerCount <= 0) {
            a[0] = body;
            return Component.translatable(labelKey, (Object[]) a);
        }

        var separatorComponent = Component.literal("·".repeat(spacerCount)).setStyle(Style.EMPTY.withFont(new ResourceLocation("gtmadvancedhatch", "separator_font")));
        var spacerComponent = Component.literal("").append(separatorComponent).append(Component.literal("  "));
        a[0] = spacerComponent.append(body);
        return Component.translatable(labelKey, (Object[]) a);
    }

    public static Component voltageName(BigDecimal avgEnergy) {
        return Component.literal(GTValues.VNF[GTUtil.getFloorTierByVoltage(avgEnergy.abs().longValue())]);
    }

    public static BigDecimal voltageAmperage(BigDecimal avgEnergy) {
        return avgEnergy.abs().divide(BigDecimal.valueOf(GTValues.VEX[GTUtil.getFloorTierByVoltage(avgEnergy.abs().longValue())]), 1, RoundingMode.FLOOR);
    }

    public static String formatBigDecimalNumberOrSicWithSign(BigDecimal number) {
        if (number.compareTo(BigDecimal.ZERO) == 0) return "古井无波，山河依在";
        else if (number.compareTo(BigDecimal.ZERO) > 0) {
            return "+" + FormattingUtil.formatNumberReadable(number);
        } else {
            return FormattingUtil.formatNumberReadable(number);
        }
    }

    private static int getComponentLength(Component component) {
        if (GTMAdvancedHatch.isClientSide()) {
            return Minecraft.getInstance().font.width(component.getString());
        } else {
            return component.getString().length() * 9;
        }
    }
}
