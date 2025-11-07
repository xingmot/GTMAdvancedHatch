package com.xingmot.gtmadvancedhatch.util;

import com.xingmot.gtmadvancedhatch.util.copy.NumberUtils;

import com.lowdragmc.lowdraglib.side.fluid.FluidHelper;

import java.text.DecimalFormat;

import org.jetbrains.annotations.NotNull;

public class AHFormattingUtil {

    public static String formatLongBucketsCompactStringBuckets(long value) {
        if (value == 0) return value + "";
        if (value < 100) return String.format("%sm", new DecimalFormat("0.####").format(value * 1000d / FluidHelper.getBucket()));
        return formatNumberBy2(value / 1000.0);
    }

    public static String formatLongBucketsToShort(long value, long min) {
        if (value <= min) return value + " mB";
        return NumberUtils.formatDouble(value / 1000.0) + " B";
    }

    public static @NotNull String formatNumberBy2(double number) {
        final String[] UNITS = { "", "K", "M", "G", "T", "P", "E", "Z", "Y", "B", "N", "D" };
        DecimalFormat df = new DecimalFormat("#.#");
        DecimalFormat df2 = new DecimalFormat("#");
        double temp = number;
        int unitIndex = 0;
        while (temp >= 1000 && unitIndex < UNITS.length - 1) {
            temp /= 1000;
            unitIndex++;
        }
        if (temp >= 100) {
            temp /= 1000;
            unitIndex++;
        }
        if (Math.floor(temp) != temp && temp < 10)
            return df.format(temp) + UNITS[unitIndex];
        else
            return df2.format(temp) + UNITS[unitIndex];
    }
}
