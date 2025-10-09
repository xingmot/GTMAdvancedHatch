package com.xingmot.gtmadvancedhatch.integration.gtmt.newmonitor;

import com.xingmot.gtmadvancedhatch.GTMAdvancedHatch;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static com.xingmot.gtmadvancedhatch.util.FormattingUtil.DECIMAL_FORMAT_SIC_2F;
import static com.xingmot.gtmadvancedhatch.util.FormattingUtil.formatNumberReadable;

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
        var spacerCount = (spaceLength / 2) - 4;
        var spacer = spacerCount > 0 ? (".".repeat((spaceLength / 2) - 4) + " ") : "";
        var spacerComponent = Component.literal(spacer);
        // return tmp;
        a[0] = spacerComponent.append(body);
        return Component.translatable(labelKey, (Object[]) a);
    }

    public static Component voltageName(BigDecimal avgEnergy) {
        return Component.literal(GTValues.VNF[GTUtil.getFloorTierByVoltage(avgEnergy.abs().longValue())]);
    }

    public static BigDecimal voltageAmperage(BigDecimal avgEnergy) {
        return avgEnergy.abs().divide(BigDecimal.valueOf(GTValues.VEX[GTUtil.getFloorTierByVoltage(avgEnergy.abs().longValue())]), 1, RoundingMode.FLOOR);
    }

    private static int getComponentLength(Component component) {
        if (GTMAdvancedHatch.isClientSide()) {
            return Minecraft.getInstance().font.width(component.getString());
        } else {
            return component.getString().length() * 9;
        }
    }
}
