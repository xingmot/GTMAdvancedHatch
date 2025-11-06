package com.xingmot.gtmadvancedhatch.util.copy;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.jetbrains.annotations.Nullable;

/**
 * @author qiuyeqaq
 */
// code by github qiuye2024github GTL_Extend
@SuppressWarnings("UnnecessaryUnicodeEscape")
public class FormattingUtil {

    public static final DecimalFormat DECIMAL_FORMAT_1F = new DecimalFormat("#,##0.0");
    public static final DecimalFormat DECIMAL_FORMAT_2F = new DecimalFormat("#,##0.00");
    public static final DecimalFormat DECIMAL_FORMAT_SIC_2F = new DecimalFormat("0.00E00");

    public static String formatNumberReadable(BigDecimal number) {
        return formatNumberReadable(number, false);
    }

    public static String formatNumberReadable(BigDecimal number, boolean milli) {
        return formatNumberReadable(number, milli, DECIMAL_FORMAT_1F, null);
    }

    public static String formatNumberReadable(BigDecimal number, boolean milli, NumberFormat fmt, @Nullable String unit) {
        StringBuilder sb = new StringBuilder();
        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal oneThousand = new BigDecimal(1000);
        BigDecimal numberTemp = number;
        if (numberTemp.compareTo(zero) < 0) {
            numberTemp = numberTemp.abs();
            sb.append('-');
        }

        if (milli && numberTemp.compareTo(oneThousand) >= 0) {
            milli = false;
            numberTemp = numberTemp.divide(oneThousand, MathContext.DECIMAL128);
        }

        int exp = 0;
        if (numberTemp.compareTo(oneThousand) >= 0) {
            exp = (int) (Math.log10(numberTemp.doubleValue()) / 3);
            if (exp > 10) return DECIMAL_FORMAT_SIC_2F.format(number);
            if (exp > 0) numberTemp = numberTemp.divide(BigDecimal.valueOf(Math.pow(1000, exp)), MathContext.DECIMAL128);
        }

        sb.append(fmt.format(numberTemp));
        if (exp > 0) sb.append("KMGTPEZYRQ".charAt(exp - 1));
        else if (milli && numberTemp.compareTo(zero) != 0) sb.append('m');

        if (unit != null) sb.append(unit);
        return sb.toString();
    }
}
